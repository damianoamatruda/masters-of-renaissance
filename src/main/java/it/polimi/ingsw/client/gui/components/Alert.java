package it.polimi.ingsw.client.gui.components;

import java.io.IOException;

import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Alert extends BorderPane {
    @FXML private VBox window;
    @FXML private Title titleText;
    @FXML private SButton okButton;
    @FXML private Text contentText;

    public Alert() {}

    public Alert(String title, String content, NumberBinding sizeBinding) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/alert.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.scaleXProperty().bind(sizeBinding);
        this.scaleYProperty().bind(sizeBinding);

        titleText.setText(title);

        contentText.setText(content);
        contentText.setWrappingWidth(300);
        contentText.setTextAlignment(TextAlignment.CENTER);

        okButton.setDefaultButton(true);
        okButton.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> {
            ((Pane) getParent()).getChildren().remove(this);
        });

        window.setBorder(new Border(new BorderStroke(Color.rgb(214, 150, 0),
            BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3))));
    }

    public Alert(Node center, Node top, Node right, Node left) {
        super(center, top, right, null, left);

        this.getStyleClass().add("main");
    }
}
