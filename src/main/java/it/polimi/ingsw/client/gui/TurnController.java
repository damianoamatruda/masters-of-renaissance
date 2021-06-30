package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Gui abstract controller for the main turn scenes, which will show the playerboard of the current player and the
 * available actions, depending on the turn phase.
 */
public abstract class TurnController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    @FXML
    protected AnchorPane canvas;
    @FXML
    protected Playerboard playerBoard;
    private final VBox leadersBox = new VBox();
    @FXML
    protected Title title = new Title();
    protected Warehouse warehouse;
    protected Strongbox strongbox;
    protected List<DevSlot> devSlots;
    private final List<Integer> toActivate = new ArrayList<>();
    private SButton activateProdButton;
    protected boolean allowProductions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);
        gui.setPauseHandler(canvas);
        
        warehouse = getWarehouse();
        strongbox = getStrongBox();
        Production baseProduction = getBaseProduction();
        devSlots = getDevSlots();
        FaithTrack faithTrack = new FaithTrack(vm.getFaithTrack().orElseThrow());

        boolean hasInkwell = vm.getInkwellPlayer().equals(vm.getCurrentPlayer());

        playerBoard = new Playerboard(warehouse, strongbox, baseProduction, devSlots, faithTrack, hasInkwell);
        AnchorPane.setBottomAnchor(playerBoard, 0d);
        AnchorPane.setTopAnchor(playerBoard, 0d);
        AnchorPane.setLeftAnchor(playerBoard, 0d);
        AnchorPane.setRightAnchor(playerBoard, 90d);
        canvas.getChildren().add(playerBoard);
        
        // has to be here so it's in front of every element
        gui.addPauseButton(canvas);

        addLeaderboardsButton();
    }

    /**
     * Sets the leaders hand view component, that also includes the activation/discard buttons under which card
     *
     * @param rightAnchor the right anchor
     * @param bottomAnchor the bottom anchor
     */
    protected void setLeadersBox(double rightAnchor, double bottomAnchor) {
        canvas.getChildren().remove(leadersBox);
        leadersBox.setAlignment(Pos.CENTER);
        leadersBox.setPrefWidth(166);
        leadersBox.setSpacing(20);
        setLeaderBoxes();
        AnchorPane.setRightAnchor(leadersBox, rightAnchor);
        AnchorPane.setBottomAnchor(leadersBox, bottomAnchor);
        canvas.getChildren().add(leadersBox);
    }

    private void setLeaderBoxes() {
        leadersBox.getChildren().clear();
        leadersBox.getChildren().addAll(
                vm.getCurrentPlayer().map(vm::getPlayerLeaderCards).orElseThrow().stream().map(reducedLeaderCard ->
                        new LeaderBox(reducedLeaderCard, this::handleActivate, this::handleDiscard, this::handleProduce, allowProductions)).toList());
        leadersBox.getChildren().addAll(
                Stream.iterate(vm.getCurrentPlayer().map(vm::getPlayerLeaderCards).orElseThrow().size(),
                        n -> n < vm.getCurrentPlayer().map(vm::getPlayerLeadersCount).orElseThrow(), n -> n + 1)
                        .map(i -> new LeaderCard(null, null)).toList());
    }

    private void refreshLeaderBoxes() {
        Platform.runLater(() -> {
            leadersBox.getChildren().clear();
            setLeaderBoxes();
        });
    }

    /**
     * Sends a request of activation of a leader card to the backend
     *
     * @param leaderCard the leader card to be activated
     */
    private void handleActivate(LeaderCard leaderCard) {
        gui.getUi().dispatch(new ReqLeaderAction(leaderCard.getLeaderId(), true));
    }

    /**
     * Sends a request of discard of a leader card to the backend
     *
     * @param leaderCard the leader card to be discarded
     */
    private void handleDiscard(LeaderCard leaderCard) {
        gui.getUi().dispatch(new ReqLeaderAction(leaderCard.getLeaderId(), false));
    }

    private <T extends Node> void handleProduce(T node, Production production) {
        node.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !node.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS));
        if (node.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS)) {
            toActivate.add(production.getProductionId());
            activateProdButton.setDisable(false);
        } else {
            toActivate.remove(Integer.valueOf(production.getProductionId())); // TODO: Use Production instead of ID
            if (toActivate.size() == 0)
                activateProdButton.setDisable(true);
        }
    }

    protected Warehouse getWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseShelves(vm.getCurrentPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow(), (s1, s2) -> {
            warehouse.setWaitingForSwap(s1, s2);
            gui.getUi().dispatch(new ReqSwapShelves(s1, s2));
        });
        warehouse.setWhiteText();
        return warehouse;
    }

    protected Strongbox getStrongBox() {
        Strongbox strongbox = new Strongbox();
        vm.getCurrentPlayer().flatMap(vm::getPlayerStrongbox).ifPresent(strongbox::setContent);
        return strongbox;
    }

    protected Production getBaseProduction() {
        Production baseProduction = new Production(this::handleProduce);
        vm.getCurrentPlayer().flatMap(vm::getPlayerBaseProduction).ifPresent(baseProduction::setProduction);
        return baseProduction;
    }

    protected List<DevSlot> getDevSlots() {
        return vm.getCurrentPlayer().map(vm::getPlayerDevelopmentCards).orElseThrow().stream().map(modelSlot -> {
            DevSlot slot = new DevSlot(this::handleProduce);
            slot.setDevCards(modelSlot.stream()
                    .flatMap(Optional::stream)
                    .map(DevelopmentCard::new).toList());
            return slot;
        }).toList();
    }

    /**
     * Adds buttons for production activation on all the components that have a production
     */
    protected void addProduceButtons() {
        activateProdButton = new SButton("Activate production");
        activateProdButton.setOnAction(event ->
                gui.getRoot().getChildren().add(new ActivateProductions(new ArrayList<>(), new ArrayList<>(vm.getCurrentPlayer().map(vm::getPlayerShelves).orElseThrow()), new ArrayList<>(vm.getLocalPlayer().map(vm::getPlayerDepots).orElseThrow()), toActivate, 0
                )));
        activateProdButton.setDisable(true);
        AnchorPane.setLeftAnchor(activateProdButton, 318d);
        AnchorPane.setBottomAnchor(activateProdButton, 15d);
        canvas.getChildren().add(activateProdButton);

        /* Add production button to each topmost development card in dev slots */
        playerBoard.addProduceButtons();

        // refreshLeaderBoxes();
    }

    private void addLeaderboardsButton() {
        Button leaderboardsBtn = new SButton("Leaderboards");
        leaderboardsBtn.addEventHandler(ActionEvent.ACTION, actionEvent ->
                gui.getRoot().getChildren().add(new LeaderBoard()));
        canvas.getChildren().add(leaderboardsBtn);
        AnchorPane.setTopAnchor(leaderboardsBtn, 10.0);
        AnchorPane.setLeftAnchor(leaderboardsBtn, 850.0);
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        if (event.getAction() == UpdateAction.ActionType.SWAP_SHELVES && vm.getLocalPlayer().isPresent() && event.getPlayer().equals(vm.getLocalPlayer().get())) {
            Shelf s1 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap1()).findAny().orElseThrow();
            Shelf s2 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap2()).findAny().orElseThrow();
            warehouse.swapShelves(s1, s2, false);
        } else if (event.getAction() == UpdateAction.ActionType.END_TURN)
            setNextState();
    }

    @Override
    public void on(UpdateActivateLeader event) {
        super.on(event);
        if (vm.isCurrentPlayer())
            leadersBox.getChildren().stream()
                    .map(LeaderBox.class::cast)
                    .filter(leaderBox -> leaderBox.getLeaderCard().getLeaderId() == event.getLeader())
                    .findAny()
                    .ifPresent(leaderBox -> {
                        leaderBox.getLeaderCard().setActive(true);
                        leaderBox.refreshButtons(allowProductions);
                    });
        else Platform.runLater(() -> setLeadersBox(54d, 30d));
    }

    @Override
    public void on(UpdateDevSlot event) {
        super.on(event);

        devSlots = getDevSlots();

        Platform.runLater(() -> playerBoard.setDevSlotsBox(devSlots));
    }

    @Override
    public void on(UpdateFaithPoints event) {
        int oldPoints = !event.isBlackCross() ? vm.getPlayerFaithPoints(event.getPlayer()) : vm.getBlackCrossFP();
        super.on(event);
        if (oldPoints < vm.getFaithTrack().orElseThrow().getMaxFaith())
            Platform.runLater(() -> playerBoard.updateFaithPoints(event, oldPoints));
    }

    @Override
    public void on(UpdateGameEnd event) {
        super.on(event);
        gui.setScene(getClass().getResource("/assets/gui/endgame.fxml"));
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);
        refreshLeaderBoxes();
    }

    @Override
    public void on(UpdateLeadersHandCount event) {
        super.on(event);
        refreshLeaderBoxes();
    }

    @Override
    public void on(UpdateResourceContainer event) {
        super.on(event);
        if (!vm.isCurrentPlayer())
            Platform.runLater(() -> {
                warehouse = getWarehouse();
                strongbox = getStrongBox();

                playerBoard.setContainers(warehouse, strongbox);
        });
    }
}
