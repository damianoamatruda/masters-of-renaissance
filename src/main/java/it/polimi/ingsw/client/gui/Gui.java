package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.LocalClient;
import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.client.Ui;
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
public class Gui extends Application implements Ui {
    private static final String initialSceneFxml = "mainmenu";
    private static final double minWidth = 854;
    private static final double minHeight = 480;

    private static Scene scene;
    private static final EventDispatcher eventDispatcher = new EventDispatcher();
    private static final View view = new View();
    private static GuiController controller;
    private static boolean singleplayer;

    static void setController(GuiController controller) {
        Gui.controller = controller;
    }

    static void dispatch(Event event) {
        eventDispatcher.dispatch(event);
    }

    static void startNetworkClient(String host, int port) throws IOException {
        new NetworkClient(view, host, port).start();
        singleplayer = false;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    static void startLocalClient() {
        new LocalClient(view).start();
        singleplayer = true;
    }

    static boolean isSingleplayer() {
        return singleplayer;
    }

    static void quit() {
        Platform.exit();
        System.exit(0);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getResource(String.format("/assets/gui/%s.fxml", fxml)));
        return fxmlLoader.load();
    }

    private static String javaVersion() {
        return System.getProperty("java.version");
    }

    private static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

    public void execute() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Gui.view.registerOnVC(eventDispatcher);
        this.registerOnMV(Gui.view);

        Parent root = loadFXML(initialSceneFxml);
        scene = new Scene(root, minWidth, minHeight, false, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);
        primaryStage.setTitle("Masters of Renaissance");
        primaryStage.show();
    }

    private void registerOnMV(EventDispatcher view) {
        view.addEventListener(ResQuit.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateBookedSeats.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateJoinGame.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrNewGame.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrNickname.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrAction.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrActiveLeaderDiscarded.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrBuyDevCard.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrCardRequirements.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrInitialChoice.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrNoSuchEntity.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrObjectNotOwned.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrReplacedTransRecipe.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrResourceReplacement.class, event -> Gui.controller.on(this, event));
        view.addEventListener(ErrResourceTransfer.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateAction.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateActionToken.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateCurrentPlayer.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateDevCardGrid.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateDevCardSlot.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateFaithPoints.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateGame.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateGameEnd.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateLastRound.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateLeader.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateLeadersHandCount.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateMarket.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdatePlayer.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdatePlayerStatus.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateResourceContainer.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateSetupDone.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateVaticanSection.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateVictoryPoints.class, event -> Gui.controller.on(this, event));
        view.addEventListener(UpdateLeadersHand.class, event -> Gui.controller.on(this, event));
    }

    private void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ResQuit.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateBookedSeats.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateJoinGame.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrNewGame.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrNickname.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrAction.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrActiveLeaderDiscarded.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrBuyDevCard.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrCardRequirements.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrInitialChoice.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrNoSuchEntity.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrObjectNotOwned.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrReplacedTransRecipe.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrResourceReplacement.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(ErrResourceTransfer.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateAction.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateActionToken.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateCurrentPlayer.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateDevCardGrid.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateDevCardSlot.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateFaithPoints.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateGame.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateGameEnd.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateLastRound.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateLeader.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateLeadersHandCount.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateMarket.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdatePlayer.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdatePlayerStatus.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateResourceContainer.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateSetupDone.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateVaticanSection.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateVictoryPoints.class, event -> Gui.controller.on(this, event));
        view.removeEventListener(UpdateLeadersHand.class, event -> Gui.controller.on(this, event));
    }
}
