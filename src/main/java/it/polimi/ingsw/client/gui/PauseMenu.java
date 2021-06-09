package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.fxml.FXML;

public class PauseMenu extends GuiController {

    @FXML
    private void handleBack() {
        Gui.getInstance().setRoot(Gui.getInstance().getLastScene());
    }

    @FXML
    private void handleOptions() {
        Gui.getInstance().pushLastScene(getClass().getResource("/assets/gui/pausemenu.fxml"));
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/options.fxml"), (OptionsController controller) -> {
            controller.setConfigContainer(false);
        });
    }

    @FXML
    private void handleQuit() {
        Gui.getInstance().dispatch(new ReqQuit());
    }
}
