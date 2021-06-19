package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrCardRequirements;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static it.polimi.ingsw.client.gui.Gui.setPauseHandlers;

/**
 * Gui abstract controller for the main turn scenes,
 * which will show the playerboard of the current player and the available actions, depending on the turn phase.
 */
public abstract class PlaygroundController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @FXML private StackPane backStackPane;
    @FXML protected AnchorPane canvas;
    @FXML protected Playerboard pboard;

    private final VBox leadersBox = new VBox();

    @FXML protected Text topText = new Text();

    private LeaderCard toDiscard = null;

    protected final Warehouse warehouse = new Warehouse();

    private NumberBinding maxScale;

    private List<Integer> toActivate = new ArrayList<>();

    private SButton activateProduction;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);

        Gui gui = Gui.getInstance();

        Production prod = new Production();
        prod.setStyle("-fx-background-image: url('/assets/gui/playerboard/baseproduction.png');" +
                "-fx-background-position: center center;" +
                "-fx-alignment: center;" +
                "-fx-background-repeat: stretch;" +
                "-fx-opacity: 1;" +
                "-fx-background-size: 100 100;");
        gui.getViewModel().getPlayerBaseProduction(vm.getCurrentPlayer()).ifPresent(p -> prod.setProduction(p));

        warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getCurrentPlayer()), (s1, s2) -> { warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().getUi().dispatch(new ReqSwapShelves(s1, s2)); });

        Strongbox s = new Strongbox();
        gui.getViewModel().getPlayerStrongbox(vm.getCurrentPlayer()).ifPresent(sb -> s.setContent(sb));

        List<DevSlot> slots = new ArrayList<>();

        List<List<ReducedDevCard>> modelSlots = vm.getPlayerDevelopmentCards(vm.getCurrentPlayer());
        
        modelSlots.forEach(modelSlot -> {
            DevSlot slot = new DevSlot();

            List<DevelopmentCard> cards = modelSlot.stream()
                    .map(c -> new DevelopmentCard(c)).toList();
            slot.setDevCards(cards);

            slots.add(slot);
        });
        
        for(int i = 0; i < vm.getSlotsCount() - modelSlots.size(); i++)
            slots.add(new DevSlot());

        FaithTrack f = new FaithTrack(vm.getFaithTrack().orElseThrow());

        pboard = new Playerboard(warehouse, s, prod, slots, f);

        canvas.getChildren().add(pboard);

        AnchorPane.setBottomAnchor(pboard, 0d);
        AnchorPane.setTopAnchor(pboard, 0d);
        AnchorPane.setLeftAnchor(pboard, 0d);
        AnchorPane.setRightAnchor(pboard, 0d);

        setPauseHandlers(this.backStackPane, this.canvas, maxScale);
    }

    /**
     * Sets the leaders hand view component, that also includes the activation/discard buttons under which card
     */
    protected void setLeadersBox() {
        // Sets the leader gui cards
        List<LeaderCard> leaders = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
            .map(reducedLeader -> {
                LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType(), reducedLeader.getLeaderType());
                leaderCard.setLeaderId(reducedLeader.getId());
                leaderCard.setLeaderType(reducedLeader.getLeaderType());
                leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
                leaderCard.setResourceType(reducedLeader.getResourceType());
                if (reducedLeader.getResourceRequirement().isPresent())
                    leaderCard.setRequirement(reducedLeader.getResourceRequirement().get());
                if (reducedLeader.getDevCardRequirement().isPresent())
                    leaderCard.setRequirement(reducedLeader.getDevCardRequirement().get());

                if (reducedLeader.getLeaderType().equalsIgnoreCase("ZeroLeader"))
                    leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                else if (reducedLeader.getLeaderType().equalsIgnoreCase("DiscountLeader"))
                    leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
                else if (reducedLeader.getLeaderType().equalsIgnoreCase("ProductionLeader"))
                    leaderCard.setProduction(vm.getProduction(reducedLeader.getProduction()).orElseThrow());
                else
                    leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                            reducedLeader.getResourceType(), false);

                leaderCard.setActivated(reducedLeader.isActive());

                return leaderCard;
            }).toList();

        leadersBox.setAlignment(Pos.CENTER);
        leadersBox.setPrefWidth(166);
        leadersBox.setSpacing(20);

        // Adds activate/discard buttons
        int i = 0;
        for(LeaderCard leader : leaders) {
            leadersBox.getChildren().add(leader);
            HBox h = new HBox();
            h.setAlignment(Pos.CENTER);
            h.setSpacing(20);

            if (!leader.isActivated()) {
                Button activate = new SButton("Activate");
                int finalI = i;
                activate.setOnMouseClicked((event -> handleActivate(finalI)));

                Button discard = new SButton("Discard");
                discard.setOnMouseClicked((event -> handleDiscard(finalI)));

                h.getChildren().addAll(List.of(activate, discard));
            }
            leadersBox.getChildren().add(h);

//            HBox produceBtnContainer = new HBox();
//            produceBtnContainer.setAlignment(Pos.CENTER);
//            leadersBox.getChildren().add(produceBtnContainer);

            i++;
        }

        canvas.getChildren().add(leadersBox);
        AnchorPane.setRightAnchor(leadersBox, 55.0);
        AnchorPane.setBottomAnchor(leadersBox, 50.0);

    }

    @Override
    public void on(UpdateFaithPoints event) {
        int oldPts;

        if (!event.isBlackCross())
            oldPts = gui.getViewModel().getPlayerFaithPoints(event.getPlayer());
        else oldPts = gui.getViewModel().getBlackCrossFP();

        super.on(event);

        if (event.getPlayer().equals(gui.getViewModel().getCurrentPlayer()) && oldPts < gui.getViewModel().getFaithTrack().orElseThrow().getMaxFaith())
            Platform.runLater(() -> pboard.updateFaithPoints(event, oldPts));
    }


    @Override
    public void on(UpdateCurrentPlayer event) {
        String previous = gui.getViewModel().getCurrentPlayer();
        super.on(event);
        if(gui.getViewModel().getPlayerNicknames().size() > 1) {
            if (event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
                gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
            else
                gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));
        } else if(previous.equals(event.getPlayer()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
    }

    /**
     * Sends a request of activation of a leader card to the backend
     *
     * @param leaderIndex the leader card to be activated
     */
    private void handleActivate(int leaderIndex) {
        int leaderId = ((LeaderCard) leadersBox.getChildren().get(2 * leaderIndex)).getLeaderId();
        Gui.getInstance().getUi().dispatch(new ReqLeaderAction(leaderId, true));
    }

    /**
     * Sends a request of discard of a leader card to the backend
     *
     * @param leaderIndex the leader card to be discarded
     */
    private void handleDiscard(int leaderIndex) {
        LeaderCard leader = (LeaderCard) leadersBox.getChildren().get(2 * leaderIndex);
        if (toDiscard == null) {
            toDiscard = leader;
            Gui.getInstance().getUi().dispatch(new ReqLeaderAction(leader.getLeaderId(), false));
        }
    }

    @Override
    public void on(UpdateActivateLeader event) {
        super.on(event);

        if (gui.getViewModel().isCurrentPlayer()) {
            LeaderCard leader = (LeaderCard) leadersBox.getChildren().stream().filter(l -> leadersBox.getChildren().indexOf(l) % 2 == 0 && ((LeaderCard) l).getLeaderId() == event.getLeader()).findAny().orElseThrow();
            int leaderIndex = leadersBox.getChildren().stream().filter(node -> leadersBox.getChildren().indexOf(node) % 2 == 0).toList().indexOf(leader);
            leader.setActivated(true);

            HBox buttons = (HBox) leadersBox.getChildren().get(2 * leaderIndex + 1);
            Platform.runLater(() -> {
                buttons.getChildren().clear();
                refreshLeadersProduceButton();
            });
        }
    }

    @Override
    public void on(ErrCardRequirements event) {
        super.on(event);

//        Alert a = new Alert(Alert.AlertType.ERROR);
//        a.setContentText("Requirements not met. Leader cannot be activated.");
//
//        a.show();

    }

    @Override
    public void on(UpdateLeadersHandCount event) {
        super.on(event);

        if(gui.getViewModel().isCurrentPlayer()) {
            int leaderIndex = leadersBox.getChildren().stream().filter(node -> leadersBox.getChildren().indexOf(node) % 2 == 0).toList().indexOf(toDiscard);

//        leadersBox.getChildren().remove(leaderIndex, leaderIndex + 2);
            leadersBox.getChildren().get(2 * leaderIndex).setVisible(false);

            leadersBox.getChildren().get(2 * leaderIndex + 1).setDisable(true);
            leadersBox.getChildren().get(2 * leaderIndex + 1).setVisible(false);

            toDiscard = null;
        }
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);

        if(event.getAction() == UpdateAction.ActionType.SWAP_SHELVES && event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname())) {
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

        if(!gui.getViewModel().isCurrentPlayer())
            Platform.runLater(() -> warehouse.setWarehouseShelves(gui.getViewModel().getPlayerShelves(gui.getViewModel().getCurrentPlayer()), (s1, s2) -> { }));
    }

    /**
     * Adds buttons for production activation on all the components that have a production
     */
    protected void addProduceButtons() {
        // add button to proceed to payment
        activateProduction = new SButton();
        activateProduction.setText("Activate production");
        activateProduction.setOnAction((event) ->
                backStackPane.getChildren().add(new ActivateProduction(toActivate, 0,
                        new ArrayList<>(), new ArrayList<>(vm.getPlayerShelves(vm.getCurrentPlayer())),
                        new ArrayList<>(vm.getPlayerDepots(vm.getLocalPlayerNickname())), maxScale)));
        activateProduction.setDisable(true);

        canvas.getChildren().add(activateProduction);
        AnchorPane.setLeftAnchor(activateProduction, 365d);
        AnchorPane.setBottomAnchor(activateProduction, 15d);

        // add button to each production
        pboard.addProduceButtons(toActivate, activateProduction);

        refreshLeadersProduceButton();

    }

    /**
     * Refreshes buttons for production activation on the leader cards
     */
    private void refreshLeadersProduceButton() {
        // add button to production leader cards
        for(int i = 0; i < leadersBox.getChildren().size() / 2; i++) {
            LeaderCard leader = (LeaderCard) leadersBox.getChildren().get(2 * i);
            if(leader.getLeaderType().equalsIgnoreCase("ProductionLeader") && leader.isActivated()) {
                SButton activate = new SButton();
                activate.setText("Produce");
                activate.setOnAction((event) -> {
                    leader.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !leader.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS));
                    if(leader.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS)) {
                        toActivate.add(leader.getProduction().getProductionId());
                        activateProduction.setDisable(false);
                    } else {
                        toActivate.remove(Integer.valueOf(leader.getProduction().getProductionId()));
                        if(toActivate.size() == 0) activateProduction.setDisable(true);
                    }
                });
                ((HBox) leadersBox.getChildren().get(2 * i + 1)).getChildren().add(activate);
            }
        }
    }
}
