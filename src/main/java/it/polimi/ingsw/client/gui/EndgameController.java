package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller used on game end, which shows who won, and the leaderboars. */
public class EndgameController extends GuiController {
    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    @FXML private Text outcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        NumberBinding maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);

        if (vm.getWinner() != null && vm.getWinner().equals(vm.getLocalPlayerNickname()))
            outcome.setText("You won with " + vm.getPlayerVictoryPoints(vm.getWinner()) + " points! CONGRATULATIONS!");
        else if (vm.getWinner() != null)
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
        Gui.getInstance().getUi().dispatch(new ReqQuit());
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }

}
