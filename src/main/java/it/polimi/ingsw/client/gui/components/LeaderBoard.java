package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/** Gui component used to show the leaderboards of the game. */
public class LeaderBoard extends VBox {
    @FXML private TableView<LeaderBoardEntry> content;

    /**
     * Class constructor.
     */
    public LeaderBoard() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/leaderboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        ViewModel viewModel = Gui.getInstance().getViewModel();

        TableColumn playerColumn = new TableColumn("Player");
        playerColumn.setCellValueFactory(new PropertyValueFactory<LeaderBoardEntry,String>("player"));
        playerColumn.setSortable(false);

        TableColumn pointsColumn = new TableColumn("Victory points");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<LeaderBoardEntry,Integer>("points"));
        pointsColumn.setSortable(false);

        content.getColumns().addAll(playerColumn, pointsColumn);

        List<LeaderBoardEntry> entries = viewModel.getPlayerNicknames().stream()
                .map(player -> new LeaderBoardEntry(player, viewModel.getPlayerVictoryPoints(player)))
                .sorted(Comparator.comparingInt(a -> a.points))
                .toList();

        content.getItems().addAll(entries);
        content.setMaxWidth(350);
        content.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        content.setMaxHeight(42 * (content.getItems().size() + 1.01));
    }

    /**
     * Class representing a single entry in the leaderboards.
     */
    public static class LeaderBoardEntry {
        private final String player;
        private final int points;

        public LeaderBoardEntry(String player, int points) {
            this.player = player;
            this.points = points;
        }

        public String getPlayer() {
            return player;
        }

        public int getPoints() {
            return points;
        }
    }
}
