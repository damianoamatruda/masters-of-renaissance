package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TriggerActionToken extends GuiController {
    @FXML
    ImageView token;
    @FXML
    Text message;
    @FXML
    Button next;
    @FXML
    BorderPane canvas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

//        message.setTextAlignment(TextAlignment.CENTER);

        next.setOnMouseClicked((event) -> {
            try {
                Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        AnchorPane.setTopAnchor(message, 20.0);
        AnchorPane.setBottomAnchor(next, 20.0);

        this.token.setImage(new Image(getTokenImage(Gui.getInstance().getViewModel().getLatestToken())));

        AnchorPane.setTopAnchor(this.token, 200.0);



    }


    private String getTokenImage(ReducedActionToken token) {
        return "/assets/gui/actiontokens/" + token.getKind() +
                (token.getDiscardedDevCardColor() != null ? token.getDiscardedDevCardColor() : "") + ".png";
    }
}
