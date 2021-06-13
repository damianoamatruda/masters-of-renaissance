package it.polimi.ingsw.client.gui.components;

import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Alert extends BorderPane {

    public Alert() {}

    public Alert(String title, String content, NumberBinding sizeBinding) {
        this.scaleXProperty().bind(sizeBinding);
        this.scaleYProperty().bind(sizeBinding);

        Text titleText = new Text(title);
        titleText.setWrappingWidth(600);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setStyle("-fx-font-size: 40px;");

        Text contentText = new Text(content);
        contentText.setWrappingWidth(600);
        contentText.setTextAlignment(TextAlignment.CENTER);

        VBox canvas = new VBox(10, titleText, contentText);
        canvas.setAlignment(Pos.CENTER);

        this.setCenter(canvas);

        Button okButton = new SButton("OK");
        okButton.setDefaultButton(true);
        okButton.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> {
            ((Pane) getParent()).getChildren().remove(this);
        });
        this.setBottom(okButton);
        BorderPane.setAlignment(okButton, Pos.CENTER);
        BorderPane.setMargin(okButton, new Insets(20));

        this.getStyleClass().add("main");
    }

    public Alert(Node center, Node top, Node right, Node left) {
        super(center, top, right, null, left);

        this.getStyleClass().add("main");
    }
}
