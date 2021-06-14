package it.polimi.ingsw.client.gui.components;

import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class ActivateProduction extends BorderPane {
    private NumberBinding maxScale;

    @FXML private SButton back;
    @FXML private Text text;

    public ActivateProduction(List<Integer> toActivate, NumberBinding sizeBinding) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/activateproduction.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.maxScale = sizeBinding;

//        this.scaleXProperty().bind(maxScale);
//        this.scaleYProperty().bind(maxScale);

        back.setOnAction(e -> handleBack());

        text.setText("Work in progress...\nThis pane will handle the already selected productions, and will prompt for the payment.\nActivated production IDs: " + toActivate);

    }

    private void handleBack() {
        ((Pane) this.getParent()).getChildren().remove(this);
    }

}
