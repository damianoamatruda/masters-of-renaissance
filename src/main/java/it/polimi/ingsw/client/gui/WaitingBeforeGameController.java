package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.UpdatePlayer;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller used when waiting for a game after joining a lobby, or to create a new game. */
public class WaitingBeforeGameController extends GuiController {
    @FXML
    private StackPane backStackPane;
    @FXML
    private BorderPane bpane;
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
     * Sets the count of players who currently joined at runtime, and displays it.
     *
     * @param bookedSeatsValue the count of players who joined the lobby
     */
    public void setBookedSeats(int bookedSeatsValue) {
        bookedSeats.setText(Integer.toString(bookedSeatsValue));
    }

    /**
     * Displays the input field for game creation only to the player that has the right to it.
     *
     * @param canPrepareNewGame true if the player can create a new game
     */
    public void setCanPrepareNewGame(String canPrepareNewGame) {
        if (Gui.getInstance().getViewModel().getLocalPlayerNickname().equals(canPrepareNewGame)) {
            youCanPrepare = true;
            canPrepare.setVisible(true);
        }
    }

    /**
     * Handles request for a new game, with the given players count
     */
    @FXML
    private void handleNewGame() {
        Gui gui = Gui.getInstance();
        RadioButton inputRadio = (RadioButton) group.getSelectedToggle();
        try {
            int count = Integer.parseInt(inputRadio.getText());
            gui.getUi().dispatch(new ReqNewGame(count));
        } catch (NumberFormatException e) {
            Platform.runLater(() -> backStackPane.getChildren().add(new Alert("Play Online", "Not a number", maxScale)));
        }
    }

    @Override
    public void on(UpdateBookedSeats event) {
        super.on(event);
        setBookedSeats(event.getBookedSeats());
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        setNextSetupState();
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
