package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;

public class TriggerActionToken extends GuiController{
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

        message = new Text("This token has been triggered.");
        next.setOnMouseClicked((event) -> {
            try {
                Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playground.fxml"));
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
