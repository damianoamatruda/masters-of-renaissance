package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Ui;
import javafx.application.Application;
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

    private static String javaVersion() {
        return System.getProperty("java.version");
    }

    private static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getResource(String.format("/assets/gui/%s.fxml", fxml)));
        return fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = loadFXML(initialSceneFxml);
        scene = new Scene(root, minWidth, minHeight, false, SceneAntialiasing.BALANCED);
        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public void execute() {
        launch();
    }
}
