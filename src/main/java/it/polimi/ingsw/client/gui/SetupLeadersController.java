package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/** Gui controller used for the leader cards setup scene. */
public class SetupLeadersController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    
    private final List<LeaderCard> selection = new ArrayList<>();
    
    @FXML
    private BorderPane canvas;
    @FXML
    private HBox leadersContainer;
    @FXML private Button choiceButton;
    @FXML private Title titleComponent;
    @FXML private Text waitingText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);

        titleComponent.setText(String.format("Choose %d leader cards",
                vm.getLocalPlayer().flatMap(vm::getPlayerData).orElseThrow().getSetup().orElseThrow().getChosenLeadersCount()));

        leadersContainer.setSpacing(10);
        leadersContainer.setAlignment(Pos.CENTER);

        leadersContainer.getChildren().addAll(vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow().stream().map(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard(reducedLeader);
            switch (reducedLeader.getLeaderType()) {
                case ZERO -> leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                case DEPOT -> leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                        reducedLeader.getResourceType(), false);
                case DISCOUNT -> leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
                case PRODUCTION -> leaderCard.setProduction(vm.getProduction(reducedLeader.getProduction()).orElseThrow());
            }
            leaderCard.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (selection.contains(leaderCard)) {
                    selection.remove(leaderCard);
                    leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
                    updateChoiceButton();
                } else if (selection.size() != vm.getLocalPlayer().flatMap(vm::getPlayerData).orElseThrow().getSetup().orElseThrow().getChosenLeadersCount()) {
                    selection.add(leaderCard);
                    leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
                    updateChoiceButton();
                }
            });
            return leaderCard;
        }).toList());

        updateChoiceButton();

        gui.setPauseHandler(canvas); // TODO: Add pause button
    }

    /**
     * Refresh of the Choose button, disabling it if the count of chosen leaders does not match.
     */
    private void updateChoiceButton() {
        choiceButton.setDisable(selection.size() != vm.getLocalPlayer().flatMap(vm::getPlayerData).orElseThrow().getSetup().orElseThrow().getChosenLeadersCount());
    }

    /**
     * Dispatches a request of leader choice to the backend.
     */
    @FXML
    private void handleChoice() {
        gui.getUi().dispatch(new ReqChooseLeaders(selection.stream().map(LeaderCard::getLeaderId).toList()));
        if(!gui.getUi().isOffline() && vm.getPlayerNicknames().size() > 1){
            waitingText.setVisible(true);
            ((VBox) leadersContainer.getParent()).getChildren().remove(leadersContainer);
            ((VBox) choiceButton.getParent()).getChildren().remove(choiceButton);
        }
    }

    @Override
    public void on(ErrAction event) {
        /* If the data in the VM is correct setNextSetupState() could be used here as well.
           This different handler, which keeps track of the current player only,
           forces the client in a state that's compatible with the server's response,
           accepting it as a universal source of truth. */
        Consumer<? extends GuiController> callback = controller ->
                gui.addToOverlay(
                        new Alert("Setup phase is concluded", "Advancing to game turns."));

        if (vm.isCurrentPlayer())
            gui.setScene(getClass().getResource("/assets/gui/turnbeforeaction.fxml"), callback);
        else
            gui.setScene(getClass().getResource("/assets/gui/waitingforturn.fxml"), callback);
    }

    @Override
    public void on(ErrInitialChoice event) {
        if (event.isLeadersChoice()) // if the error is from the initial leaders choice
            if (event.getMissingLeadersCount() == 0) // no leaders missing -> already chosen
                Platform.runLater(() -> gui.addToOverlay(new Alert(
                        "You cannot choose the leader cards",
                        "You have already chosen the leader cards.",
                        this::setNextState)));
            else
                gui.reloadScene("You cannot buy the development card",
                        String.format("You have not chosen enough leaders: %d missing.", event.getMissingLeadersCount()));
        else
            Platform.runLater(() -> gui.addToOverlay(new Alert(
                    "You cannot choose the leader cards",
                    "You have already chosen the initial resources.",
                    this::setNextState)));
    }

    @Override
    public void on(UpdateAction event) {
        if (event.getAction() != UpdateAction.ActionType.CHOOSE_LEADERS && vm.getLocalPlayer().isPresent() && event.getPlayer().equals(vm.getLocalPlayer().get()))
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");


        if (vm.getLocalPlayer().isPresent() && event.getPlayer().equals(vm.getLocalPlayer().get())) {
            titleComponent.setText("Leader setup done.");
            setNextState();
        }
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        setNextState();
    }
}
