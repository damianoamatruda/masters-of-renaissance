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

        TableColumn faithColumn = new TableColumn("F. points");
        faithColumn.setCellValueFactory(new PropertyValueFactory<LeaderBoardEntry,Integer>("faith"));
        faithColumn.setSortable(false);

        TableColumn pointsColumn = new TableColumn("V. points");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<LeaderBoardEntry,Integer>("points"));
        pointsColumn.setSortable(false);

        content.getColumns().addAll(playerColumn, faithColumn, pointsColumn);

        List<LeaderBoardEntry> entries = viewModel.getPlayerNicknames().stream()
                .map(player -> new LeaderBoardEntry(player, viewModel.getPlayerFaithPoints(player), viewModel.getPlayerVictoryPoints(player)))
                .sorted(Comparator.comparingInt(LeaderBoardEntry::getPoints).reversed())
                .sorted(Comparator.comparingInt(a -> a.points))
                .toList();

        content.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(LeaderBoardEntry item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    // add indicator of local player
                    if (item.getPlayer().equals(viewModel.getLocalPlayerNickname()))
                        item.setLocalPlayer();

                    String player = item.getPlayer();
                    if(item.isMe()) player = player.substring(0, player.length() - 5);
                    Optional<String> color = Gui.getInstance().getViewModel().getClientGuiColor(player);
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
        private String player;
        private final int points;
        private final int faith;
        private boolean isMe;

        public LeaderBoardEntry(String player, int faith, int points) {
            this.player = player;
            this.points = points;
            this.faith = faith;
        }

        public String getPlayer() {
            return player;
        }

        public int getPoints() {
            return points;
        }

        public int getFaith() {
            return faith;
        }

        public boolean isMe() {
            return isMe;
        }

        public void setLocalPlayer() {
            isMe = true;
            player += " (me)";
        }
    }
}
