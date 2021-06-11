package it.polimi.ingsw.client.gui;

import java.net.URL;
import java.util.ResourceBundle;

import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class InputNicknameController extends GuiController {
    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    @FXML private Title titleComponent;
    @FXML private TextField nickname;
    private String nicknameValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NumberBinding maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.minWidth),
                                              backStackPane.heightProperty().divide(Gui.minHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
    }
    
    @FXML
    private void handleNicknameInput() {
        nicknameValue = nickname.getText();
        Gui.getInstance().dispatch(new ReqJoin(nicknameValue));
    }

    @FXML
    private void handleBack() {
        Gui gui = Gui.getInstance();
        gui.setRoot(getClass().getResource(gui.isOffline() ? "/assets/gui/mainmenu.fxml" : "/assets/gui/playonline.fxml"));
    }

    public void setTitle(String value) {
        titleComponent.setText(value);
    }

    @Override
    public void on(Gui gui, ErrNickname event) {
        super.on(gui, event);
        System.out.println("Nickname is invalid. Reason: " + event.getReason().toString().toLowerCase());
    }

    @Override
    public void on(Gui gui, UpdateBookedSeats event) {
        super.on(gui, event);
        gui.getViewModel().setLocalPlayerNickname(nicknameValue);
        if (gui.isOffline())
            gui.dispatch(new ReqNewGame(1));
        else
            gui.setRoot(getClass().getResource("/assets/gui/waitingbeforegame.fxml"), (WaitingBeforeGameController controller) -> {
                controller.setBookedSeats(event.getBookedSeats());
                controller.setCanPrepareNewGame(event.canPrepareNewGame());
            });
    }

    @Override
    public void on(Gui gui, UpdateGame event) {
        super.on(gui, event);
        if (gui.isOffline())
            System.out.println("Game started.");
    }

    @Override
    public void on(Gui gui, UpdateLeadersHand event) {
        super.on(gui, event);
        if (gui.isOffline())
            gui.setRoot(getClass().getResource("/assets/gui/setupleaders.fxml"));
    }
}
