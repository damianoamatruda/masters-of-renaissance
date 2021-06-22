package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.common.events.mvevents.ResQuit;
import it.polimi.ingsw.common.events.mvevents.errors.ErrServerUnavailable;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

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
    protected void setNextSetupState() {
        vm.isSetupDone().ifPresent(isSetupDone -> { // received UpdateGame (if not, wait for it)
            if (isSetupDone) { // setup is done
                if (vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
                    gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
                else
                    gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));
            } else // setup not done
                vm.getPlayerData(vm.getLocalPlayerNickname()).ifPresent(pd -> {
                    pd.getSetup().ifPresent(setup -> { // received local player's setup
                        if (isLeaderSetupAvailable())
                            gui.setRoot(getClass().getResource("/assets/gui/setupleaders.fxml"));
                        else if (isResourceSetupAvailable())
                            gui.setRoot(getClass().getResource("/assets/gui/setupresources.fxml"));
                    });
                });
        });
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    abstract StackPane getRootElement();

    @Override
    public void on(ResQuit event) {
        super.on(event);
        
        gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }

    @Override
    public void on(ErrServerUnavailable event) {
        super.on(event);

        if (!gui.getUi().hasClientOpen())
            return;

        gui.getUi().closeClient();

        gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"),
                (MainMenuController controller) ->
                        controller.getBackStackPane().getChildren()
                                .add(new Alert("Connection error", "Server is down. Try again later.",
                                        controller.getMaxScale())));

    }
}
