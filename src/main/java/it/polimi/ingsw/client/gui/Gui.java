package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.LocalClient;
import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * JavaFX App
 */
public class Gui extends Application {
    private static final String initialSceneFxml = "mainmenu";
    private static final double minWidth = 854;
    private static final double minHeight = 480;

    private static Gui instance = null;

    private final EventDispatcher eventDispatcher = new EventDispatcher();
    private final View view = new View();

    private Scene scene;
    private GuiController controller;

    private boolean singleplayer;

    public static void main(String[] args) {
        launch(args);
    }

    public static Gui getInstance() {
        return instance;
    }

    private static String javaVersion() {
        return System.getProperty("java.version");
    }

    private static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Gui.instance = this;

        view.registerOnVC(eventDispatcher);
        this.registerOnMV(view);

        Parent root = loadFXML(initialSceneFxml);
        scene = new Scene(root, minWidth, minHeight, false, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);
        primaryStage.setTitle("Masters of Renaissance");
        primaryStage.show();
    }

    void setController(GuiController controller) {
        this.controller = controller;
    }

    void dispatch(Event event) {
        eventDispatcher.dispatch(event);
    }

    void startNetworkClient(String host, int port) throws IOException {
        new NetworkClient(view, host, port).start();
        singleplayer = false;
    }

    void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    void startLocalClient() {
        new LocalClient(view).start();
        singleplayer = true;
    }

    boolean isSingleplayer() {
        return singleplayer;
    }

    void quit() {
        Platform.exit();
        System.exit(0);
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(String.format("/assets/gui/%s.fxml", fxml)));
        return fxmlLoader.load();
    }

    private void registerOnMV(EventDispatcher view) {
        view.addEventListener(ResQuit.class, event -> controller.on(this, event));
        view.addEventListener(UpdateBookedSeats.class, event -> controller.on(this, event));
        view.addEventListener(UpdateJoinGame.class, event -> controller.on(this, event));
        view.addEventListener(ErrNewGame.class, event -> controller.on(this, event));
        view.addEventListener(ErrNickname.class, event -> controller.on(this, event));
        view.addEventListener(ErrAction.class, event -> controller.on(this, event));
        view.addEventListener(ErrActiveLeaderDiscarded.class, event -> controller.on(this, event));
        view.addEventListener(ErrBuyDevCard.class, event -> controller.on(this, event));
        view.addEventListener(ErrCardRequirements.class, event -> controller.on(this, event));
        view.addEventListener(ErrInitialChoice.class, event -> controller.on(this, event));
        view.addEventListener(ErrNoSuchEntity.class, event -> controller.on(this, event));
        view.addEventListener(ErrObjectNotOwned.class, event -> controller.on(this, event));
        view.addEventListener(ErrReplacedTransRecipe.class, event -> controller.on(this, event));
        view.addEventListener(ErrResourceReplacement.class, event -> controller.on(this, event));
        view.addEventListener(ErrResourceTransfer.class, event -> controller.on(this, event));
        view.addEventListener(UpdateAction.class, event -> controller.on(this, event));
        view.addEventListener(UpdateActionToken.class, event -> controller.on(this, event));
        view.addEventListener(UpdateCurrentPlayer.class, event -> controller.on(this, event));
        view.addEventListener(UpdateDevCardGrid.class, event -> controller.on(this, event));
        view.addEventListener(UpdateDevCardSlot.class, event -> controller.on(this, event));
        view.addEventListener(UpdateFaithPoints.class, event -> controller.on(this, event));
        view.addEventListener(UpdateGame.class, event -> controller.on(this, event));
        view.addEventListener(UpdateGameEnd.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLastRound.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLeader.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLeadersHandCount.class, event -> controller.on(this, event));
        view.addEventListener(UpdateMarket.class, event -> controller.on(this, event));
        view.addEventListener(UpdatePlayer.class, event -> controller.on(this, event));
        view.addEventListener(UpdatePlayerStatus.class, event -> controller.on(this, event));
        view.addEventListener(UpdateResourceContainer.class, event -> controller.on(this, event));
        view.addEventListener(UpdateSetupDone.class, event -> controller.on(this, event));
        view.addEventListener(UpdateVaticanSection.class, event -> controller.on(this, event));
        view.addEventListener(UpdateVictoryPoints.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLeadersHand.class, event -> controller.on(this, event));
    }

    private void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ResQuit.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateBookedSeats.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateJoinGame.class, event -> controller.on(this, event));
        view.removeEventListener(ErrNewGame.class, event -> controller.on(this, event));
        view.removeEventListener(ErrNickname.class, event -> controller.on(this, event));
        view.removeEventListener(ErrAction.class, event -> controller.on(this, event));
        view.removeEventListener(ErrActiveLeaderDiscarded.class, event -> controller.on(this, event));
        view.removeEventListener(ErrBuyDevCard.class, event -> controller.on(this, event));
        view.removeEventListener(ErrCardRequirements.class, event -> controller.on(this, event));
        view.removeEventListener(ErrInitialChoice.class, event -> controller.on(this, event));
        view.removeEventListener(ErrNoSuchEntity.class, event -> controller.on(this, event));
        view.removeEventListener(ErrObjectNotOwned.class, event -> controller.on(this, event));
        view.removeEventListener(ErrReplacedTransRecipe.class, event -> controller.on(this, event));
        view.removeEventListener(ErrResourceReplacement.class, event -> controller.on(this, event));
        view.removeEventListener(ErrResourceTransfer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateAction.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateActionToken.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateCurrentPlayer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateDevCardGrid.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateDevCardSlot.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateFaithPoints.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateGame.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateGameEnd.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLastRound.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLeader.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLeadersHandCount.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateMarket.class, event -> controller.on(this, event));
        view.removeEventListener(UpdatePlayer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdatePlayerStatus.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateResourceContainer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateSetupDone.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateVaticanSection.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateVictoryPoints.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLeadersHand.class, event -> controller.on(this, event));
    }
}
