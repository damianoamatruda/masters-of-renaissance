package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.UpdatePlayer;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller that manages the input of a nickname. */
public class InputNicknameController extends GuiController {
    @FXML
    private StackPane backStackPane;
    @FXML
    private BorderPane bpane;
    @FXML
    private Title titleComponent;
    @FXML
    private TextField nickname;
    private String nicknameValue;

    private NumberBinding maxScale;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));

        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
    }

    @Override
    StackPane getRootElement() {
        return backStackPane;
    }

    /**
     * Sends a join request to the server with the given nickname.
     */
    @FXML
    private void handleNicknameInput() {
        nicknameValue = nickname.getText();
        gui.getViewModel().setLocalPlayerNickname(nicknameValue);
        Gui.getInstance().getUi().dispatch(new ReqJoin(nicknameValue));
    }

    /**
     * Handles going back to previous scene.
     */
    @FXML
    private void handleBack() {
        Gui gui = Gui.getInstance();
        gui.setRoot(getClass().getResource(gui.getUi().isOffline() ? "/assets/gui/mainmenu.fxml" : "/assets/gui/playonline.fxml"));
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
    public void on(ErrNickname event) {
        super.on(event);
        Platform.runLater(() ->
            backStackPane.getChildren().add(
                new Alert("Play Online", String.format("Nickname is invalid. Reason: %s.", event.getReason().toString().toLowerCase()), maxScale)));
    }

    @Override
    public void on(UpdateBookedSeats event) {
        if (gui.getUi().isOffline())
            gui.getUi().dispatch(new ReqNewGame(1));
        else
            gui.setRoot(getClass().getResource("/assets/gui/waitingbeforegame.fxml"), (WaitingBeforeGameController controller) -> {
                controller.setBookedSeats(event.getBookedSeats());
                controller.setCanPrepareNewGame(event.canPrepareNewGame());
            });
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdatePlayer event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        setNextSetupState();
    }
}
