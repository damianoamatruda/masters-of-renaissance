package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/** Gui controller used for the leader cards setup scene. */
public class SetupLeadersController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    
    private final List<LeaderCard> selection = new ArrayList<>();
    
    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    @FXML private HBox leadersContainer;
    @FXML private Button choiceButton;
    @FXML private Title titleComponent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);

        if (vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()) == null)
            throw new RuntimeException();

        titleComponent.setText(String.format("Choose %d leader cards",
                vm.getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getChosenLeadersCount()));

        leadersContainer.setSpacing(10);
        leadersContainer.setAlignment(Pos.CENTER);

        List<LeaderCard> leaderCards = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream().map(reducedLeader -> {
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

            leaderCard.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (selection.contains(leaderCard)) {
                    selection.remove(leaderCard);
                    leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
                    updateChoiceButton();
                } else if (selection.size() != vm.getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getChosenLeadersCount()) {
                    selection.add(leaderCard);
                    leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
                    updateChoiceButton();
                }
            });

            return leaderCard;
        }).toList();

        leadersContainer.getChildren().addAll(leaderCards);

        updateChoiceButton();
    }

    @Override
    StackPane getRootElement() {
        return backStackPane;
    }

    /**
     * Refresh of the Choose button, disabling it if the count of chosen leaders does not match.
     */
    private void updateChoiceButton() {
        choiceButton.setDisable(
                selection.size() != Gui.getInstance().getViewModel()
                        .getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getChosenLeadersCount());
    }

    /**
     * Dispatches a request of leader choice to the backend.
     */
    @FXML
    private void handleChoice() {
        Gui.getInstance().getUi().dispatch(new ReqChooseLeaders(selection.stream().map(LeaderCard::getLeaderId).toList()));
    }

    @Override
    public void on(ErrAction event) {
        /* If the data in the VM is correct setNextSetupState() could be used here as well.
           This different handler, which keeps track of the current player only,
           forces the client in a state that's compatible with the server's response,
           accepting it as a universal source of truth. */
        Consumer<?> callback = c ->
            getRootElement().getChildren().add(
                new Alert("Action error", "Setup phase is concluded, advancing to game turns.", maxScale));

        if (vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"), callback);
        else
            gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"), callback);
    }

    @Override
    public void on(ErrInitialChoice event) {
        if (event.isLeadersChoice()) // if the error is from the initial leaders choice
            if (event.getMissingLeadersCount() == 0) // no leaders missing -> already chosen
                setNextState(c ->
                    getRootElement().getChildren().add(
                        new Alert("Error choosing leader cards", "Leader cards already chosen, advancing to next state.", maxScale)));
            else
                gui.reloadRoot("Error buying development card",
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()));
        else
            setNextState(c ->
                getRootElement().getChildren().add(
                    new Alert("Error choosing leader cards", "Initial resources already chosen, advancing to next state.", maxScale)));
    }

    @Override
    public void on(UpdateAction event) {
        if (event.getAction() != UpdateAction.ActionType.CHOOSE_LEADERS && event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");

        setNextState();
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        setNextState();
    }
}
