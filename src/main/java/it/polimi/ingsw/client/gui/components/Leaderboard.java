package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedPlayer;
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

/** Gui component used to show the leaderboard of the game. */
public class Leaderboard extends StackPane {
    @FXML
    private BorderPane main;
    @FXML
    private TableView<LeaderBoardEntry> content;
    @FXML
    private SButton back;

    /**
     * Class constructor.
     */
    public Leaderboard() {
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
        back.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> ((Pane) getParent()).getChildren().remove(this));
    }

    public void createLeaderboardTable(TableView<LeaderBoardEntry> content) {
        ViewModel vm = Gui.getInstance().getViewModel();

        TableColumn<LeaderBoardEntry, String> playerColumn = new TableColumn<>("Player");
        playerColumn.setCellValueFactory(new PropertyValueFactory<LeaderBoardEntry, String>("player"));
        playerColumn.setSortable(false);

        TableColumn<LeaderBoardEntry, Integer> faithColumn = new TableColumn<>("F. points");
        faithColumn.setCellValueFactory(new PropertyValueFactory<LeaderBoardEntry, Integer>("faith"));
        faithColumn.setSortable(false);

        TableColumn<LeaderBoardEntry, Integer> pointsColumn = new TableColumn<>("V. points");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<LeaderBoardEntry, Integer>("points"));
        pointsColumn.setSortable(false);

        content.getColumns().addAll(List.of(playerColumn, faithColumn, pointsColumn));

        List<LeaderBoardEntry> entries = vm.getPlayers().stream()
                .map(ReducedPlayer::getNickname)
                .map(player -> new LeaderBoardEntry(player, vm.getPlayerFaithPoints(player), vm.getPlayerVictoryPoints(player)))
                .sorted(Comparator.comparingInt(LeaderBoardEntry::getPoints).reversed())
                .sorted(Comparator.comparingInt(a -> a.points))
                .toList();

        content.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(LeaderBoardEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    /* Add indicator of local player */
                    if (vm.getLocalPlayer().isPresent() && item.getPlayer().equals(vm.getLocalPlayer().get()))
                        item.setLocalPlayer();

                    String player = item.getPlayer();
                    if (item.isMe())
                        player = player.substring(0, player.length() - 5);
                    Optional<String> color = vm.getHexPlayerColor(player);
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
     * This class represents a single entry in the leaderboard.
     */
    public static class LeaderBoardEntry {
        private final int points;
        private final int faith;
        private String player;
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
