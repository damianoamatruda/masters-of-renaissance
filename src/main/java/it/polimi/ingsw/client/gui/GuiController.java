package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.common.events.mvevents.ResQuit;
import it.polimi.ingsw.common.events.mvevents.UpdateActionToken;
import it.polimi.ingsw.common.events.mvevents.UpdatePlayerStatus;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class represents a generic GUI controller.
 */
public abstract class GuiController extends UiController implements Initializable {
    protected final Gui gui = Gui.getInstance();

    public GuiController() {
        super(Gui.getInstance().getUi());
        Gui.getInstance().setController(this);
    }

    /**
     * Sets the next state based on the following algorithm:
     * <p>
     * Differentiation between setup phase and turn phase
     * <ul>
     *     <li>check <code>isSetupDone</code> to know which phase to limit the choice</li>
     * </ul>
     * If the setup phase is not concluded:
     * <ul>
     *     <li>check whether the leader setup still needs to be done,</li>
     *     <li>else go to the resource setup, which will internally choose whether it still needs to be done or whether
     *         it has to simply show the waiting screen</li>
     * </ul>
     * If the setup phase is concluded:
     * <ul>
     *     <li>check whether the current player and the local player are the same player and switch accordingly</li>
     * </ul>
     */
    protected void setNextState() {
        if (vm.isGameEnded())
            gui.setScene(getClass().getResource("/assets/gui/scenes/endgame.fxml"));
        else
            vm.getLocalPlayer().flatMap(vm::getPlayer).ifPresent(player -> {
                if (vm.isSetupDone() && vm.getCurrentPlayer().isPresent() &&
                        !vm.getPlayerLeaderCards(vm.getLocalPlayer().get()).isEmpty()) { // Setup is done
                    if (vm.localPlayerIsCurrent())
                        gui.setScene(getClass().getResource("/assets/gui/scenes/turnbeforeaction.fxml"));
                    else
                        gui.setScene(getClass().getResource("/assets/gui/scenes/waitingforturn.fxml"));
                } else if (!vm.isSetupDone()) // Setup is not done
                    if (isLeaderSetupAvailable())
                        gui.setScene(getClass().getResource("/assets/gui/scenes/setupleaders.fxml"));
                    else if (isLocalLeaderSetupDone())
                        gui.setScene(getClass().getResource("/assets/gui/scenes/setupresources.fxml"));
            });
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @Override
    public void on(ErrAction event) {
        super.on(event);

        switch (event.getReason()) {
            case LATE_SETUP_ACTION -> Platform.runLater(() -> gui.addToOverlay(new Alert(
                    "Setup phase is concluded",
                    "Advancing to game turns.",
                    this::setNextState)));
            case EARLY_MANDATORY_ACTION -> Platform.runLater(() -> gui.addToOverlay(new Alert(
                    "Setup phase is not concluded yet",
                    "Returning to setup phase.",
                    this::setNextState)));
            case LATE_MANDATORY_ACTION -> Platform.runLater(() -> gui.addToOverlay(new Alert(
                    "You have already done a mandatory action",
                    "Advancing to optional actions.",
                    () -> gui.setScene(getClass().getResource("/assets/gui/scenes/turnafteraction.fxml")))));
            case EARLY_TURN_END -> Platform.runLater(() -> gui.addToOverlay(new Alert(
                    "You cannot end the turn yet",
                    "A mandatory action needs to be done before ending the turn.",
                    () -> gui.setScene(getClass().getResource("/assets/gui/scenes/turnbeforeaction.fxml")))));
            case GAME_ENDED -> Platform.runLater(() -> gui.getRoot().getChildren().add(new Alert(
                    "The game has ended",
                    "Advancing to ending screen.",
                    () -> gui.setScene(getClass().getResource("/assets/gui/scenes/endgame.fxml")))));
            case NOT_CURRENT_PLAYER -> Platform.runLater(() -> gui.addToOverlay(new Alert(
                    "You are not the current player",
                    "Please wait for your turn.",
                    () -> gui.setScene(getClass().getResource("/assets/gui/scenes/waitingforturn.fxml")))));
        }
    }

    @Override
    public void on(ErrActiveLeaderDiscarded event) {
        super.on(event);

        gui.reloadScene("You cannot discard the leader card",
                "Active leader card cannot be discarded.");
    }

    @Override
    public void on(ErrCardRequirements event) {
        gui.reloadScene("You cannot activate the leader card",
                event.getMissingDevCards().map(missingDevCards -> {
                    StringBuilder msg = new StringBuilder("Missing development cards:\n\n");

                    for (ReducedDevCardRequirementEntry e : missingDevCards)
                        msg.append(String.format("%s %s × %d\n",
                                e.getColor(),
                                e.getLevel() == 0 ? "any level" : String.format("level %d", e.getLevel()),
                                e.getQuantity()));

                    return msg.toString();
                }).orElse(event.getMissingResources().map(missingResources -> {
                    StringBuilder msg = new StringBuilder("Missing resources:\n\n");

                    for (Map.Entry<String, Integer> e : missingResources.entrySet())
                        msg.append(String.format("%s × %d\n", e.getKey(), e.getValue()));

                    return msg.toString();
                }).orElse("")));
    }

    @Override
    public void on(ErrNickname event) {
        super.on(event);

        switch (event.getReason()) {
            case ALREADY_SET -> gui.reloadScene(
                    "Invalid nickname", "Given nickname is already set.", (InputNicknameController controller) ->
                            controller.setTitle(gui.getUi().isOffline() ? "Play Offline" : "Play Online"));
            case TAKEN -> gui.reloadScene(
                    "Invalid nickname", "Given nickname is taken.", (InputNicknameController controller) ->
                            controller.setTitle(gui.getUi().isOffline() ? "Play Offline" : "Play Online"));
            case NOT_SET -> gui.reloadScene(
                    "Invalid nickname", "Given nickname is blank.", (InputNicknameController controller) ->
                            controller.setTitle(gui.getUi().isOffline() ? "Play Offline" : "Play Online"));
            case NOT_IN_GAME -> gui.setScene(getClass().getResource("/assets/gui/scenes/inputnickname.fxml"), (InputNicknameController c) ->
                    gui.addToOverlay(new Alert("Invalid action", "Game not joined yet.")));
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
                String.format("%s with ID %d is not yours. Are you sure you typed that right?",
                        event.getObjectType(), event.getId()));
    }

    @Override
    public void on(ErrReplacedTransRecipe event) {
        super.on(event);

        if (event.isIllegalDiscardedOut())
            gui.reloadScene("Invalid transfer of resources",
                    "The payment's cost is erroneously specified, please choose all and only the needed resources.");
        else
            gui.reloadScene("Invalid transfer of resources",
                    String.format(
                            "Irregular quantity of %s %s specified in the container map: %d required, %d chosen.",
                            event.isInput() ? "input" : "output",
                            event.getResType().isEmpty() ? "resources" : event.getResType(),
                            event.getReplacedCount(),
                            event.getShelvesChoiceResCount()));
    }

    @Override
    public void on(ErrInvalidResourceTransaction event) {
        super.on(event);

        String direction = event.isInput() ? "input" : "output";
        String isReplacement = event.isReplacement() ? " replacements" : "";

        switch (event.getReason()) {
            case EXCLUDED -> gui.reloadScene("Invalid transfer of resources",
                    String.format("Invalid %s%s: excluded resource specified.",
                            direction, isReplacement));
            case ILLEGAL_NON_STORABLE -> gui.reloadScene("Invalid transfer of resources",
                    String.format("Invalid %s%s: non-storable resource specified in container map.",
                            direction, isReplacement));
            case ILLEGAL_STORABLE -> gui.reloadScene("Invalid transfer of resources",
                    String.format("Invalid %s%s: storable resource specified as non-storable.",
                            direction, isReplacement));
            case NEGATIVE_VALUES -> gui.reloadScene("Invalid transfer of resources",
                    String.format("Invalid %s%s: negative quantity specified.",
                            direction, isReplacement));
        }
    }

    @Override
    public void on(ErrResourceTransfer event) {
        super.on(event);

        String reason = switch (event.getReason()) {
            case BOUNDED_RESTYPE_DIFFER -> event.getResType() != null ?
                    "shelf's binding resource type is different from transferring resource" :
                    "multiple resource types cannot be bound to the same shelf";
            case NON_STORABLE -> "resource is not storable";
            case CAPACITY_REACHED -> "container's capacity boundaries reached";
            case DUPLICATE_BOUNDED_RESOURCE -> "resource type is already bound to another shelf";
        };

        gui.reloadScene("You cannot move the resources",
                String.format(event.isAdded() ? "You cannot add%s into container: %s." : "You cannot remove%s from container: %s.",
                        event.getResType() == null ? "" : String.format(" %s", event.getResType()),
                        reason));
    }

    @Override
    public void on(ErrServerUnavailable event) {
        super.on(event);

        if (!gui.getUi().hasClientOpen())
            return;

        gui.getUi().closeClient();

        Platform.runLater(() -> gui.addToOverlay(
                new Alert("Server is down", "Try again later.", () ->
                        gui.setScene(getClass().getResource("/assets/gui/scenes/mainmenu.fxml")))));
    }

    @Override
    public void on(ResQuit event) {
        super.on(event);

        gui.setScene(getClass().getResource("/assets/gui/scenes/mainmenu.fxml"));
    }

    @Override
    public void on(UpdateActionToken event) {
        super.on(event);
        gui.setScene(getClass().getResource("/assets/gui/scenes/triggeractiontoken.fxml"));
    }

    @Override
    public void on(UpdatePlayerStatus event) {
        super.on(event);

        Platform.runLater(() -> gui.addToOverlay(
                new Alert("Player status change",
                        String.format("Player %s %s", event.getPlayer(), event.isActive() ? "reconnected" : "disconnected"))));
    }
}
