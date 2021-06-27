package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Gui abstract controller for the main turn scenes,
 * which will show the playerboard of the current player and the available actions, depending on the turn phase.
 */
public abstract class PlaygroundController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    @FXML
    protected AnchorPane canvas;
    @FXML
    protected Playerboard playerBoard;
    private final VBox leadersBox = new VBox();
    @FXML
    protected Title title = new Title();
    protected Warehouse warehouse;
    private final List<Integer> toActivate = new ArrayList<>();
    private SButton activateProdButton;
    protected boolean allowProductions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);
        gui.setPauseHandler(canvas);
        gui.addPauseButton(canvas);

        warehouse = getWarehouse();
        Strongbox strongbox = getStrongBox();
        Production baseProduction = getBaseProduction();
        List<DevSlot> devSlots = getDevSlots();
        FaithTrack faithTrack = new FaithTrack(vm.getFaithTrack().orElseThrow());

        playerBoard = new Playerboard(warehouse, strongbox, baseProduction, devSlots, faithTrack);
        AnchorPane.setBottomAnchor(playerBoard, 0d);
        AnchorPane.setTopAnchor(playerBoard, 0d);
        AnchorPane.setLeftAnchor(playerBoard, 0d);
        AnchorPane.setRightAnchor(playerBoard, 90d);
        canvas.getChildren().add(playerBoard);
    }

    /**
     * Sets the leaders hand view component, that also includes the activation/discard buttons under which card
     */
    protected void setLeadersBox(double rightAnchor, double bottomAnchor) {
        leadersBox.setAlignment(Pos.CENTER);
        leadersBox.setPrefWidth(166);
        leadersBox.setSpacing(20);
        setLeaderBoxes();
        AnchorPane.setRightAnchor(leadersBox, rightAnchor);
        AnchorPane.setBottomAnchor(leadersBox, bottomAnchor);
        canvas.getChildren().add(leadersBox);
    }

    private void setLeaderBoxes() {
        leadersBox.getChildren().addAll(
                vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream().map(reducedLeaderCard ->
                        new LeaderBox(reducedLeaderCard, this::handleActivate, this::handleDiscard, this::handleProduce, allowProductions)).toList());
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

    private void handleProduce(LeaderCard leaderCard) {
        leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !leaderCard.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS));
        if (leaderCard.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS)) {
            toActivate.add(leaderCard.getProduction().getProductionId());
            activateProdButton.setDisable(false);
        } else {
            toActivate.remove(Integer.valueOf(leaderCard.getProduction().getProductionId())); // TODO: Use Production instead of ID
            if (toActivate.size() == 0)
                activateProdButton.setDisable(true);
        }
    }

    @Override
    public void on(UpdateFaithPoints event) {
        int oldPoints = !event.isBlackCross() ? vm.getPlayerFaithPoints(event.getPlayer()) : vm.getBlackCrossFP();
        super.on(event);
        if (event.getPlayer().equals(vm.getCurrentPlayer()) && oldPoints < vm.getFaithTrack().orElseThrow().getMaxFaith())
            Platform.runLater(() -> playerBoard.updateFaithPoints(event, oldPoints));
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        String prevPlayer = vm.getCurrentPlayer();
        super.on(event);
        if (vm.getPlayerNicknames().size() > 1)
            gui.setScene(event.getPlayer().equals(vm.getLocalPlayerNickname())
                    ? getClass().getResource("/assets/gui/playgroundbeforeaction.fxml")
                    : getClass().getResource("/assets/gui/waitingforturn.fxml"));
        else if (prevPlayer.equals(event.getPlayer()))
            gui.setScene(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
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
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);
        refreshLeaderBoxes();
    }

    @Override
    public void on(UpdateLeadersHandCount event) {
        super.on(event);
        // TODO: Update unknown cards of other players
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        if (event.getAction() == UpdateAction.ActionType.SWAP_SHELVES && event.getPlayer().equals(vm.getLocalPlayerNickname())) {
            Shelf s1 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap1()).findAny().orElseThrow();
            Shelf s2 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap2()).findAny().orElseThrow();
            warehouse.swapShelves(s1, s2);
        }
    }

    @Override
    public void on(UpdateGameEnd event) {
        super.on(event);
        gui.setScene(getClass().getResource("/assets/gui/endgame.fxml"));
    }

    @Override
    public void on(UpdateResourceContainer event) {
        super.on(event);
        if (!vm.isCurrentPlayer())
            Platform.runLater(() -> warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getCurrentPlayer()), (s1, s2) -> {
            }));
    }

    protected Warehouse getWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getCurrentPlayer()), (s1, s2) -> {
            warehouse.setWaitingForSwap(s1, s2);
            gui.getUi().dispatch(new ReqSwapShelves(s1, s2));
        });
        return warehouse;
    }

    protected Strongbox getStrongBox() {
        Strongbox strongbox = new Strongbox();
        vm.getPlayerStrongbox(vm.getCurrentPlayer()).ifPresent(strongbox::setContent);
        return strongbox;
    }

    protected Production getBaseProduction() {
        Production baseProduction = new Production();
        vm.getPlayerBaseProduction(vm.getCurrentPlayer()).ifPresent(baseProduction::setProduction);
        return baseProduction;
    }

    protected List<DevSlot> getDevSlots() {
        return vm.getPlayerDevelopmentCards(vm.getCurrentPlayer()).stream().map(modelSlot -> {
            DevSlot slot = new DevSlot();
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
                gui.getRoot().getChildren().add(new ActivateProduction(toActivate, 0,
                        new ArrayList<>(), new ArrayList<>(vm.getPlayerShelves(vm.getCurrentPlayer())),
                        new ArrayList<>(vm.getPlayerDepots(vm.getLocalPlayerNickname())))));
        activateProdButton.setDisable(true);
        AnchorPane.setLeftAnchor(activateProdButton, 318d);
        AnchorPane.setBottomAnchor(activateProdButton, 15d);
        canvas.getChildren().add(activateProdButton);

        /* Add production button to each topmost development card in dev slots */
        playerBoard.addProduceButtons(toActivate, activateProdButton);

        // refreshLeaderBoxes();
    }
}
