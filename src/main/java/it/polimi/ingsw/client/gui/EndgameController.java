package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller used on game end, which shows who won, and the leaderboars. */
public class EndgameController extends GuiController {
    @FXML
    private BorderPane canvas;
    @FXML
    private Text outcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        gui.setSceneScaling(canvas);

        if (vm.getWinner() != null && vm.getWinner().equals(vm.getLocalPlayerNickname()))
            outcome.setText("You won with " + vm.getPlayerVictoryPoints(vm.getWinner()) + " points! CONGRATULATIONS!");
        else if (vm.getWinner() != null && !vm.getWinner().isEmpty())
            outcome.setText(
                    vm.getWinner() + " is the winner with " +
                            vm.getPlayerVictoryPoints(vm.getWinner()) + " points!");
        else
            outcome.setText("Lorenzo il Magnifico has won. Better luck next time!");
    }

    /**
     * Handles quit from game to title
     */
    @FXML
    private void handleQuit() {
        gui.getUi().dispatch(new ReqQuit());
        gui.setScene(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }

}
