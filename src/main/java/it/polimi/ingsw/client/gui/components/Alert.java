package it.polimi.ingsw.client.gui.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Alert extends BorderPane {
    public Alert(String title, String content) {
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
        this.setBottom(new SButton("OK"));
    }

    public Alert(Node center, Node top, Node right, Node left) {
        super(center, top, right, null, left);
    }
}
