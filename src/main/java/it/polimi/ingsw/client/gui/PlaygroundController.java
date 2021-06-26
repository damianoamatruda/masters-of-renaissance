package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static it.polimi.ingsw.client.gui.Gui.setPauseHandlers;

/**
 * Gui abstract controller for the main turn scenes,
 * which will show the playerboard of the current player and the available actions, depending on the turn phase.
 */
public abstract class PlaygroundController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @FXML private StackPane backStackPane;
    @FXML
    protected AnchorPane canvas;
    @FXML
    protected Playerboard playerBoard;

    private final VBox leadersBox = new VBox();

    @FXML protected Text topText = new Text();

    private LeaderCard toDiscard = null;

    protected Warehouse warehouse;

    private final List<Integer> toActivate = new ArrayList<>();

    private SButton activateProdButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Scaling */
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);

        /* Pause Menu */
        setPauseHandlers(backStackPane, canvas, maxScale);

        /* Warehouse Shelves */
        warehouse = new Warehouse();
        warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getCurrentPlayer()), (s1, s2) -> {
            warehouse.setWaitingForSwap(s1, s2);
            gui.getUi().dispatch(new ReqSwapShelves(s1, s2));
        });

        /* Strongbox */
        Strongbox strongbox = new Strongbox();
        vm.getPlayerStrongbox(vm.getCurrentPlayer()).ifPresent(strongbox::setContent);

        /* Base Production */
        Production baseProduction = new Production();
        vm.getPlayerBaseProduction(vm.getCurrentPlayer()).ifPresent(baseProduction::setProduction);

        /* Development Card Slots */
        List<DevSlot> devSlots = vm.getPlayerDevelopmentCards(vm.getCurrentPlayer()).stream().map(modelSlot -> {
            DevSlot slot = new DevSlot();
            slot.setDevCards(modelSlot.stream()
                    .flatMap(Optional::stream)
                    .map(DevelopmentCard::new).toList());
            return slot;
        }).toList();

        /* Faith Track */
        FaithTrack faithTrack = new FaithTrack(vm.getFaithTrack().orElseThrow());

        /* Player Board */
        playerBoard = new Playerboard(warehouse, strongbox, baseProduction, devSlots, faithTrack);
        canvas.getChildren().add(playerBoard);
        AnchorPane.setBottomAnchor(playerBoard, 0d);
        AnchorPane.setTopAnchor(playerBoard, 0d);
        AnchorPane.setLeftAnchor(playerBoard, 0d);
        AnchorPane.setRightAnchor(playerBoard, 90d);
    }

    @Override
    StackPane getRootElement() {
        return backStackPane;
    }
    
    /**
     * Sets the leaders hand view component, that also includes the activation/discard buttons under which card
     */
    protected void setLeadersBox(double rightAnchor, double bottomAnchor) {
        leadersBox.setAlignment(Pos.CENTER);
        leadersBox.setPrefWidth(166);
        leadersBox.setSpacing(20);

        vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).forEach(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType(), reducedLeader.getResourceType());
            leaderCard.setLeaderId(reducedLeader.getId());
            leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints());
            leaderCard.setActive(reducedLeader.isActive());
            reducedLeader.getResourceRequirement().ifPresent(leaderCard::setRequirement);
            reducedLeader.getDevCardRequirement().ifPresent(leaderCard::setRequirement);
            switch (reducedLeader.getLeaderType()) {
                case ZERO -> leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                case DEPOT -> leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                        reducedLeader.getResourceType(), false);
                case DISCOUNT -> leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
                case PRODUCTION -> leaderCard.setProduction(vm.getProduction(reducedLeader.getProduction()).orElseThrow());
            }
            leadersBox.getChildren().add(leaderCard);

            /* Add activate/discard buttons */
            HBox buttonsContainer = new HBox();
            buttonsContainer.setAlignment(Pos.CENTER);
            buttonsContainer.setSpacing(20);
            if (!reducedLeader.isActive()) {
                Button activate = new SButton("Activate");
                activate.setOnMouseClicked(event -> handleActivate(leaderCard));

                Button discard = new SButton("Discard");
                discard.setOnMouseClicked(event -> handleDiscard(leaderCard));

                buttonsContainer.getChildren().addAll(List.of(activate, discard));
            }
            leadersBox.getChildren().add(buttonsContainer);
        });

        canvas.getChildren().add(leadersBox);
        AnchorPane.setRightAnchor(leadersBox, rightAnchor);
        AnchorPane.setBottomAnchor(leadersBox, bottomAnchor);
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
            gui.setRoot(event.getPlayer().equals(vm.getLocalPlayerNickname())
                    ? getClass().getResource("/assets/gui/playgroundbeforeaction.fxml")
                    : getClass().getResource("/assets/gui/waitingforturn.fxml"));
        else if (prevPlayer.equals(event.getPlayer()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
    }

    /**
     * Sends a request of activation of a leader card to the backend
     *
     * @param leader the leader card to be activated
     */
    private void handleActivate(LeaderCard leader) {
        int leaderId = leader.getLeaderId();
        gui.getUi().dispatch(new ReqLeaderAction(leaderId, true));
    }

    /**
     * Sends a request of discard of a leader card to the backend
     *
     * @param leader the leader card to be discarded
     */
    private void handleDiscard(LeaderCard leader) {
        if (toDiscard == null) {
            toDiscard = leader;
            gui.getUi().dispatch(new ReqLeaderAction(leader.getLeaderId(), false));
        }
    }

    @Override
    public void on(UpdateActivateLeader event) {
        super.on(event);
        if (vm.isCurrentPlayer()) {
            LeaderCard leader = (LeaderCard) leadersBox.getChildren().stream().filter(l -> leadersBox.getChildren().indexOf(l) % 2 == 0 && ((LeaderCard) l).getLeaderId() == event.getLeader()).findAny().orElseThrow();
            int leaderIndex = leadersBox.getChildren().stream().filter(node -> leadersBox.getChildren().indexOf(node) % 2 == 0).toList().indexOf(leader);
            leader.setActive(true);

            HBox buttons = (HBox) leadersBox.getChildren().get(2 * leaderIndex + 1);
            Platform.runLater(() -> {
                buttons.getChildren().clear();
                refreshLeadersProduceButton();
            });
        }
    }

    @Override
    public void on(UpdateLeadersHandCount event) {
        super.on(event);
        if (vm.isCurrentPlayer()) {
            int leaderIndex = leadersBox.getChildren().stream().filter(node -> leadersBox.getChildren().indexOf(node) % 2 == 0).toList().indexOf(toDiscard);
            leadersBox.getChildren().get(2 * leaderIndex).setVisible(false);
            leadersBox.getChildren().get(2 * leaderIndex + 1).setDisable(true);
            leadersBox.getChildren().get(2 * leaderIndex + 1).setVisible(false);
            toDiscard = null;
        }
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
        gui.setRoot(getClass().getResource("/assets/gui/endgame.fxml"));
    }

    @Override
    public void on(UpdateResourceContainer event) {
        super.on(event);
        if (!vm.isCurrentPlayer())
            Platform.runLater(() -> warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getCurrentPlayer()), (s1, s2) -> {
            }));
    }

    /**
     * Adds buttons for production activation on all the components that have a production
     */
    protected void addProduceButtons() {
        activateProdButton = new SButton();
        activateProdButton.setText("Activate production");
        activateProdButton.setOnAction((event) ->
                backStackPane.getChildren().add(new ActivateProduction(toActivate, 0,
                        new ArrayList<>(), new ArrayList<>(vm.getPlayerShelves(vm.getCurrentPlayer())),
                        new ArrayList<>(vm.getPlayerDepots(vm.getLocalPlayerNickname())), maxScale)));
        activateProdButton.setDisable(true);

        canvas.getChildren().add(activateProdButton);
        AnchorPane.setLeftAnchor(activateProdButton, 318d);
        AnchorPane.setBottomAnchor(activateProdButton, 15d);

        /* Add button to each production */
        playerBoard.addProduceButtons(toActivate, activateProdButton);

        refreshLeadersProduceButton();
    }

    /**
     * Refreshes buttons for production activation on the leader cards
     */
    private void refreshLeadersProduceButton() {
        // add button to production leader cards
        for (int i = 0; i < leadersBox.getChildren().size() / 2; i++) {
            LeaderCard leader = (LeaderCard) leadersBox.getChildren().get(2 * i);
            if (leader.getLeaderType() == LeaderType.PRODUCTION && leader.isActive()) {
                SButton activate = new SButton();
                activate.setText("Produce");
                activate.setOnAction(event -> {
                    leader.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !leader.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS));
                    if (leader.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS)) {
                        toActivate.add(leader.getProduction().getProductionId());
                        activateProdButton.setDisable(false);
                    } else {
                        toActivate.remove(Integer.valueOf(leader.getProduction().getProductionId()));
                        if (toActivate.size() == 0) activateProdButton.setDisable(true);
                    }
                });
                ((HBox) leadersBox.getChildren().get(2 * i + 1)).getChildren().add(activate);
            }
        }
    }
}
