package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.common.events.mvevents.ResQuit;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname.ErrNicknameReason;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public abstract class GuiController extends UiController implements Initializable {
    protected final Gui gui = Gui.getInstance();
    protected NumberBinding maxScale;
    
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
    protected void setNextState(Consumer<?> callback) {
        vm.isSetupDone().ifPresent(isSetupDone -> { // received UpdateGame (if not, wait for it)
            if (isSetupDone) { // setup is done
                if (vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
                    gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"), callback);
                else
                    gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"), callback);
            } else // setup not done
                vm.getPlayerData(vm.getLocalPlayerNickname()).ifPresent(pd -> {
                    pd.getSetup().ifPresent(setup -> { // received local player's setup
                        if (isLeaderSetupAvailable())
                            gui.setRoot(getClass().getResource("/assets/gui/setupleaders.fxml"), callback);
                        else if (isResourceSetupAvailable())
                            gui.setRoot(getClass().getResource("/assets/gui/setupresources.fxml"), callback);
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

    abstract StackPane getRootElement();

    @Override
    public void on(ErrAction event) {
        super.on(event);

        String title = "Action error";

        switch (event.getReason()) {
        case LATE_SETUP_ACTION:
            setNextState(c ->
                getRootElement().getChildren().add(
                    new Alert(title, "Setup phase is concluded, advancing to game turns.", maxScale)));
        break;
        case EARLY_MANDATORY_ACTION:
            setNextState(c ->
                getRootElement().getChildren().add(
                    new Alert(title,
                        "A mandatory action is trying to be executed before the setup phase is concluded, returning to setup phase.",
                        maxScale)));
        break;
        case LATE_MANDATORY_ACTION:
            gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"), c ->
                getRootElement().getChildren().add(
                    new Alert(title,
                        "A mandatory action has already been executed, advancing to optional actions.",
                        maxScale)));
        break;
        case EARLY_TURN_END:
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"), c ->
                getRootElement().getChildren().add(
                    new Alert(title,
                        "A mandatory action needs to be executed before ending the turn.",
                        maxScale)));
            break;
        case GAME_ENDED:
            gui.setRoot(getClass().getResource("/assets/gui/endgame.fxml"), c ->
                getRootElement().getChildren().add(
                    new Alert(title,
                        "The match is finished, advancing to ending screen.",
                        maxScale)));
        break;
        case NOT_CURRENT_PLAYER:
            gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"), c ->
                getRootElement().getChildren().add(
                    new Alert(title,
                        "You are not the current player. Please wait for your turn.",
                        maxScale)));
        break;
        default:
            gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"), c ->
                getRootElement().getChildren().add(
                    new Alert(title,
                        String.format("Unsupported ErrAction reason %s.", event.getReason().toString()),
                        maxScale)));
        break;
        }
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

        gui.reloadRoot(c ->
            getRootElement().getChildren().add(
                new Alert("Error buying development card", content, maxScale)));
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

        if (event.getReason() == ErrNicknameReason.NOT_IN_GAME) {
            gui.setRoot(getClass().getResource("/assets/gui/inputnickname.fxml"), c ->
                getRootElement().getChildren().add(
                    new Alert("Nickname error",
                        "Match not joined yet.",
                        maxScale)));
            return;
        }

        final String reason;
        switch (event.getReason()) {
            case ALREADY_SET:
            case TAKEN:
                reason = String.format("nickname is %s.", event.getReason().toString().toLowerCase().replace('_', ' '));
            break;
            case NOT_SET:
                reason = "nickname is blank.";
            break;
            default:
                reason = "unsupported ErrNickname option";
            break;
        }

        gui.reloadRoot(c ->
            getRootElement().getChildren().add(
                new Alert("Nickname error",
                    String.format("Error setting nickname: %s", reason),
                    maxScale)));
    }

    @Override
    public void on(ErrNoSuchEntity event) {
        super.on(event);
        
        gui.reloadRoot(c ->
            getRootElement().getChildren().add(
                new Alert("Nonexistent entity",
                    String.format("No such entity %s: %s%s.",
                        event.getOriginalEntity().toString().toLowerCase().replace("_", " "),
                        event.getId() < 0 ? "" : String.format("ID %d", event.getId()),
                        event.getCode() == null ? "" : String.format("code %s", event.getCode())),
                    maxScale)));
    }

    @Override
    public void on(ErrObjectNotOwned event) {
        super.on(event);
        
        gui.reloadRoot(c ->
            getRootElement().getChildren().add(
                new Alert("Object not owned",
                    String.format("%s with ID %d isn't yours. Are you sure you typed that right?",
                        event.getObjectType(), event.getId()),
                    maxScale)));
    }

    @Override
    public void on(ErrReplacedTransRecipe event) {
        super.on(event);
        
        if (event.isIllegalDiscardedOut())
            gui.reloadRoot(c ->
                getRootElement().getChildren().add(
                    new Alert("Resource transaction error",
                        "Output of resource transfer cannot be discarded",
                        maxScale)));
        else
            gui.reloadRoot(c ->
                getRootElement().getChildren().add(
                    new Alert("Resource transaction error",
                        String.format(
                            "Irregular amount of %s specified in the container map: %d specified, %d required.",
                            event.getResType().isEmpty() ? "resources" : event.getResType(),
                            event.getReplacedCount(),
                            event.getShelvesChoiceResCount()),
                        maxScale)));
    }

    @Override
    public void on(ErrResourceReplacement event) {
        super.on(event);
        
        gui.reloadRoot(c ->
            getRootElement().getChildren().add(
                new Alert("Resource replacement error",
                    String.format("Error validating transaction request %s: %s resource found.",
                        event.isInput() ? "input" : "output",
                        event.isNonStorable() ? "nonstorable" : "excluded"),
                    maxScale)));
    }

    @Override
    public void on(ErrResourceTransfer event) {
        super.on(event);

        final String reason;

        switch (event.getReason()) {
            case BOUNDED_RESTYPE_DIFFER:
                reason = "shelf's binding resource type is different from transferring resource";
            break;
            case NON_STORABLE:
                reason = "resource type is not storable";
            break;
            case CAPACITY_REACHED:
                reason = "shelf's capacity boundaries reached";
            break;
            case DUPLICATE_BOUNDED_RESOURCE:
                reason = "resource type is already bound to another shelf";
            break;
            default:
                reason = "unsupported ErrResourceTransfer option";
                break;
        }
        
        gui.reloadRoot(c ->
            getRootElement().getChildren().add(
                new Alert("Resource transfer error",
                    String.format("Error %s resource %s from container: %s.",
                        event.isAdded() ? "adding" : "removing",
                        event.getResType(),
                        reason),
                    maxScale)));
    }
    
    @Override
    public void on(ErrServerUnavailable event) {
        super.on(event);

        if (!gui.getUi().hasClientOpen())
            return;

        gui.getUi().closeClient();

        gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"),
                (MainMenuController controller) ->
                        getRootElement().getChildren()
                                .add(new Alert("Connection error", "Server is down. Try again later.",
                                        maxScale)));

    }

    @Override
    public void on(ResQuit event) {
        super.on(event);

        gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }
}
