package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNewGame;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller used when waiting for a game after joining a lobby, or to prepare a new game. */
public class WaitingBeforeGameController extends GuiController {
    @FXML
    private BorderPane canvas;
    @FXML
    private Text bookedSeats;
    @FXML
    private ToggleGroup group;
    @FXML
    private VBox canPrepare;

    private boolean youCanPrepare;

    public WaitingBeforeGameController() {
        youCanPrepare = false;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);
        gui.setPauseHandler(canvas);
    }

    /**
     * Sets the count of players who currently joined at runtime, and displays it.
     *
     * @param bookedSeatsValue the count of players who joined the lobby
     */
    public void setBookedSeats(int bookedSeatsValue) {
        bookedSeats.setText(String.valueOf(bookedSeatsValue));
    }

    /**
     * Displays the input field for game creation only to the player that has the right to it.
     *
     * @param canPrepareNewGame true if the player can prepare a new game
     */
    public void setCanPrepareNewGame(String canPrepareNewGame) {
        if (gui.getViewModel().getLocalPlayerNickname().equals(canPrepareNewGame)) {
            youCanPrepare = true;
            canPrepare.setVisible(true);
        }
    }

    /**
     * Handles request for a new game, with the given players count
     */
    @FXML
    private void handleNewGame() {
        RadioButton inputRadio = (RadioButton) group.getSelectedToggle();
        try {
            int count = Integer.parseInt(inputRadio.getText());
            gui.getUi().dispatch(new ReqNewGame(count));
        } catch (NumberFormatException e) {
            Platform.runLater(() -> gui.getRoot().getChildren().add(new Alert("Play Online", "Not a number")));
        }
    }

    @Override
    public void on(ErrNewGame event) {
        if (event.isInvalidPlayersCount())
            gui.reloadScene("You cannot prepare a new game",
                    "You have given an invalid players' count.");
        else
            Platform.runLater(() -> gui.getRoot().getChildren().add(new Alert(
                    "You cannot prepare a new game",
                    "You are not allowed to prepare a new game.",
                    this::setNextState
            )));
    }

    @Override
    public void on(UpdateBookedSeats event) {
        super.on(event);
        setBookedSeats(event.getBookedSeats());
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateJoinGame event) {
        super.on(event);
        if (youCanPrepare)
            canPrepare.setVisible(false);
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
