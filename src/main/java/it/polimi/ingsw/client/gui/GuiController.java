package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.common.events.mvevents.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class GuiController extends UiController implements Initializable {
    private final Gui gui = Gui.getInstance();
    
    public GuiController() {
        super(Gui.getInstance().getUi());
        
        Gui.getInstance().setController(this);
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @Override
    public void on(ResQuit event) {
        gui.getUi().closeClient();
        gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }
}
