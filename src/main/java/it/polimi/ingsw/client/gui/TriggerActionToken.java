package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.BufferedReader;
import java.io.IOException;

public class TriggerActionToken extends GuiController {
    @FXML
    ImageView token;
    @FXML
    Text message;
    @FXML
    Button next;

    public TriggerActionToken(ReducedActionToken token) {
        this.token = new ImageView(getTokenImage(token));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/triggeractiontoken.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

//        message = new Text("This token has been triggered.");
        message.setTextAlignment(TextAlignment.CENTER);
        AnchorPane.setTopAnchor(message, 20.0);
        next.setOnMouseClicked((event) -> {
            try {
                Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    private String getTokenImage(ReducedActionToken token) {
        return "/assets/gui/actiontokens/" + token.getKind() +
                (token.getDiscardedDevCardColor() != null ? token.getDiscardedDevCardColor() : "") + ".png";
    }
}
