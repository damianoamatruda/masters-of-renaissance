package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.LeaderBoard;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller used on game end, which shows who won, and the leaderboars. */
public class EndgameController extends GuiController {
    @FXML
    public VBox leaderboard;
    @FXML
    private BorderPane canvas;
    @FXML
    private Text outcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        gui.setSceneScaling(canvas);

        Label label = new Label("Leaderboard");
        label.setTextAlignment(TextAlignment.CENTER);
        leaderboard.getChildren().add(label);

        TableView<LeaderBoard.LeaderBoardEntry> leaderboards = new TableView<>();
        leaderboard.getChildren().add(leaderboards);
        new LeaderBoard().createLeaderboardTable(leaderboards);

        if (vm.getWinnerPlayer() != null && vm.getWinnerPlayer().equals(vm.getLocalPlayer()))
            outcome.setText("You won with " + vm.getPlayerVictoryPoints(vm.getWinnerPlayer()) + " points! CONGRATULATIONS!");
        else if (vm.getWinnerPlayer() != null && !vm.getWinnerPlayer().isEmpty())
            outcome.setText(
                    vm.getWinnerPlayer() + " is the winner with " +
                            vm.getPlayerVictoryPoints(vm.getWinnerPlayer()) + " points!");
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
