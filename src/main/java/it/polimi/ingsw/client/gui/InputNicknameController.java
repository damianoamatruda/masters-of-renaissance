package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller that manages the input of a nickname. */
public class InputNicknameController extends GuiController {
    @FXML
    private BorderPane canvas;
    @FXML
    private Title titleComponent;
    @FXML
    private TextField nickname;
    private String nicknameValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);
    }

    /**
     * Sends a join request to the server with the given nickname.
     */
    @FXML
    private void handleNicknameInput() {
        nicknameValue = nickname.getText();
        gui.getViewModel().setLocalPlayerNickname(nicknameValue);
        gui.getUi().dispatch(new ReqJoin(nicknameValue));
    }

    /**
     * Handles going back to previous scene.
     */
    @FXML
    private void handleBack() {
        gui.setScene(getClass().getResource(gui.getUi().isOffline() ? "/assets/gui/mainmenu.fxml" : "/assets/gui/playonline.fxml"));
    }

    /**
     * Adds header text to the scene.
     *
     * @param value the text to be added to the component
     */
    public void setTitle(String value) {
        titleComponent.setText(value);
    }

    @Override
    public void on(UpdateBookedSeats event) {
        if (gui.getUi().isOffline())
            gui.getUi().dispatch(new ReqNewGame(1));
        else
            gui.setScene(getClass().getResource("/assets/gui/waitingbeforegame.fxml"), (WaitingBeforeGameController controller) -> {
                controller.setBookedSeats(event.getBookedSeats());
                controller.setCanPrepareNewGame(event.canPrepareNewGame());
            });
    }
    
    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdatePlayer event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        setNextState();
    }
}
