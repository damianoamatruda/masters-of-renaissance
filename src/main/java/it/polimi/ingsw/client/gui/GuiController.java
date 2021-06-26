package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.common.events.mvevents.ResQuit;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public abstract class GuiController extends UiController implements Initializable {
    protected final Gui gui = Gui.getInstance();
    
    public GuiController() {
        super(Gui.getInstance().getUi());
        Gui.getInstance().setController(this);
    }

    /**
     * Sets the next state based on the following algorithm:
     * 
     * UpdateGame tells the client whether the game's setup phase is still ongoing or not.
     * If the setup is done, the client needs to wait until UpdateCurrentPlayer to know which state to change to.
     * If the setup phase is not done:
     * The local player's UpdatePlayer tells the client what part of the player setup to switch to.
     * If the leaders hand still needs to be chosen the client will need to wait for UpdateLeadersHand.
     */
    protected void setNextState(Consumer<GuiController> callback) {
        vm.isSetupDone().ifPresent(isSetupDone -> { // received UpdateGame (if not, wait for it)
            vm.getPlayerData(vm.getLocalPlayerNickname()).ifPresent(pd -> {
                if (isSetupDone && !vm.getCurrentPlayer().equals("") &&
                    !vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).isEmpty()) { // setup is done
                    if (vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
                        gui.setScene(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"), callback);
                    else
                        gui.setScene(getClass().getResource("/assets/gui/waitingforturn.fxml"), callback);
                } else // setup not done
                    pd.getSetup().ifPresent(setup -> { // received local player's setup
                        if (isLeaderSetupAvailable())
                            gui.setScene(getClass().getResource("/assets/gui/setupleaders.fxml"), callback);
                        else if (isResourceSetupAvailable())
                            gui.setScene(getClass().getResource("/assets/gui/setupresources.fxml"), callback);
                    });
            });
        });
    }

    protected void setNextState() {
        setNextState(null);
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @Override
    public void on(ErrAction event) {
        super.on(event);

        String title = "Action error";

        switch (event.getReason()) {
            case LATE_SETUP_ACTION -> setNextState(controller ->
                    gui.getRoot().getChildren().add(
                            new Alert(title,
                                    "Setup phase is concluded, advancing to game turns.")));
            case EARLY_MANDATORY_ACTION -> setNextState(controller ->
                    gui.getRoot().getChildren().add(
                            new Alert(title,
                                    "A mandatory action is trying to be executed before the setup phase is concluded, returning to setup phase.")));
            case LATE_MANDATORY_ACTION -> gui.setScene(getClass().getResource("/assets/gui/waitingforturn.fxml"), (WaitingForTurnController c) ->
                    gui.getRoot().getChildren().add(
                            new Alert(title,
                                    "A mandatory action has already been executed, advancing to optional actions.")));
            case EARLY_TURN_END -> gui.setScene(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"), (PlaygroundBeforeActionController c) ->
                    gui.getRoot().getChildren().add(
                            new Alert(title,
                                    "A mandatory action needs to be executed before ending the turn.")));
            case GAME_ENDED -> gui.setScene(getClass().getResource("/assets/gui/endgame.fxml"), (EndgameController c) ->
                    gui.getRoot().getChildren().add(
                            new Alert(title,
                                    "The match is finished, advancing to ending screen.")));
            case NOT_CURRENT_PLAYER -> gui.setScene(getClass().getResource("/assets/gui/waitingforturn.fxml"), (WaitingForTurnController c) ->
                    gui.getRoot().getChildren().add(
                            new Alert(title,
                                    "You are not the current player. Please wait for your turn.")));
        }
    }

    @Override
    public void on(ErrActiveLeaderDiscarded event) {
        super.on(event);

        gui.reloadScene("Leader discard error",
                "Active leader cannot be discarded.");
    }

    @Override
    public void on(ErrBuyDevCard event) {
        super.on(event);
    }

    @Override
    public void on(ErrCardRequirements event) {
        String msg = "\nUnsatisfied card requirements:\n";
        if (event.getMissingDevCards().isPresent()) {
            msg = msg.concat("Missing development cards:\n");
    
            for (ReducedDevCardRequirementEntry e : event.getMissingDevCards().get())
                msg = msg.concat(String.format("Color: %s, level: %d, missing: %d\n", e.getColor(), e.getLevel(), e.getAmount()));
        } else {
            msg = msg.concat("Missing resources:\n");

            for (Map.Entry<String, Integer> e : event.getMissingResources().get().entrySet())
                msg = msg.concat(String.format("Resource type: %s, missing %d\n", e.getKey(), e.getValue()));
        }

        String content = msg;

        gui.reloadScene("Error buying development card", content);
    }

    @Override
    public void on(ErrInitialChoice event) {
        super.on(event);
    }

    @Override
    public void on(ErrNewGame event) {
        super.on(event);
    }

    @Override
    public void on(ErrNickname event) {
        super.on(event);

        switch (event.getReason()) {
            case ALREADY_SET -> gui.reloadScene(
                    "Nickname error", "Nickname is already set.", (InputNicknameController controller) ->
                            controller.setTitle(gui.getUi().isOffline() ? "Play Offline" : "Play Online"));
            case TAKEN -> gui.reloadScene(
                    "Nickname error", "Nickname is taken.", (InputNicknameController controller) ->
                            controller.setTitle(gui.getUi().isOffline() ? "Play Offline" : "Play Online"));
            case NOT_SET -> gui.reloadScene(
                    "Nickname error", "Nickname is blank.", (InputNicknameController controller) ->
                            controller.setTitle(gui.getUi().isOffline() ? "Play Offline" : "Play Online"));
            case NOT_IN_GAME -> gui.setScene(getClass().getResource("/assets/gui/inputnickname.fxml"), (InputNicknameController c) ->
                    gui.getRoot().getChildren().add(new Alert("Nickname error", "Match not joined yet.")));
        }
    }

    @Override
    public void on(ErrNoSuchEntity event) {
        super.on(event);

        gui.reloadScene("Nonexistent entity",
                String.format("No such entity %s: %s%s.",
                        event.getOriginalEntity().toString().toLowerCase().replace("_", " "),
                        event.getId() < 0 ? "" : String.format("ID %d", event.getId()),
                        event.getCode() == null ? "" : String.format("code %s", event.getCode())));
    }

    @Override
    public void on(ErrObjectNotOwned event) {
        super.on(event);

        gui.reloadScene("Object not owned",
                String.format("%s with ID %d isn't yours. Are you sure you typed that right?",
                        event.getObjectType(), event.getId()));
    }

    @Override
    public void on(ErrReplacedTransRecipe event) {
        super.on(event);
        
        if (event.isIllegalDiscardedOut())
            gui.reloadScene("Resource transaction error",
                    "Output of resource transfer cannot be discarded");
        else
            gui.reloadScene("Resource transaction error",
                    String.format(
                            "Irregular amount of %s specified in the container map: %d specified, %d required.",
                            event.getResType().isEmpty() ? "resources" : event.getResType(),
                            event.getReplacedCount(),
                            event.getShelvesChoiceResCount()));
    }

    @Override
    public void on(ErrResourceReplacement event) {
        super.on(event);

        gui.reloadScene("Resource replacement error",
                String.format("Error validating transaction request %s: %s resource found.",
                        event.isInput() ? "input" : "output",
                        event.isNonStorable() ? "nonstorable" : "excluded"));
    }

    @Override
    public void on(ErrResourceTransfer event) {
        super.on(event);

        final String reason = switch (event.getReason()) {
            case BOUNDED_RESTYPE_DIFFER -> "shelf's binding resource type is different from transferring resource";
            case NON_STORABLE -> "resource type is not storable";
            case CAPACITY_REACHED -> "shelf's capacity boundaries reached";
            case DUPLICATE_BOUNDED_RESOURCE -> "resource type is already bound to another shelf";
        };

        gui.reloadScene("Resource transfer error",
                String.format("Error %s resource %s from container: %s.",
                        event.isAdded() ? "adding" : "removing",
                        event.getResType(),
                        reason));
    }
    
    @Override
    public void on(ErrServerUnavailable event) {
        super.on(event);

        if (!gui.getUi().hasClientOpen())
            return;

        gui.getUi().closeClient();

        Platform.runLater(() -> gui.getRoot().getChildren().add(
                new Alert("Connection error", "Server is down. Try again later.", () ->
                        gui.setScene(getClass().getResource("/assets/gui/mainmenu.fxml")))));
    }

    @Override
    public void on(ResQuit event) {
        super.on(event);

        gui.setScene(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }
}
