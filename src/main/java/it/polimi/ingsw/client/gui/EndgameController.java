package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Leaderboard;
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

/**
 * GUI controller used on game end, which shows who won, and the leaderboard.
 */
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

        TableView<Leaderboard.LeaderBoardEntry> leaderboard = new TableView<>();
        this.leaderboard.getChildren().add(leaderboard);
        new Leaderboard().createLeaderboardTable(leaderboard);

        vm.getWinnerPlayer().map(vm::getPlayerVictoryPoints).ifPresentOrElse(points -> {
            if (vm.getWinnerPlayer().equals(vm.getLocalPlayer()))
                outcome.setText(String.format("You won with %d points! CONGRATULATIONS!", points));
            else
                outcome.setText(String.format("%s is the winner with %d points!", vm.getWinnerPlayer().get(), points));
        }, () -> outcome.setText("Lorenzo il Magnifico has won. Better luck next time!"));

        gui.setPauseHandler(canvas);
        gui.addPauseButton(canvas);
    }

    /**
     * Handles quit from game to title
     */
    @FXML
    private void handleQuit() {
        gui.getUi().dispatch(new ReqQuit());
        gui.setScene(getClass().getResource("/assets/gui/scenes/mainmenu.fxml"));
    }

}
