package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Gui component used to show the leaderboards of the game. */
public class LeaderBoard extends StackPane {
    @FXML
    private BorderPane main;
    @FXML
    private TableView<LeaderBoardEntry> content;
    @FXML
    private SButton back;

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

        Gui.getInstance().setSceneScaling(main);

        createLeaderboardTable(content);

        addBackHandler();
    }

    /**
     * Sets handler to back button.
     */
    private void addBackHandler() {
        back.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> {
            ((Pane) getParent()).getChildren().remove(this);
        });
    }

    public void createLeaderboardTable(TableView<LeaderBoardEntry> content) {
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

        content.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(LeaderBoardEntry item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    Optional<String> color = Gui.getInstance().getViewModel().getClientGuiColor(item.getPlayer());
                    color.ifPresent(c -> setStyle("-fx-background-color: " + c + ";"));
                }
            }
        });

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
