package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class TriggerActionToken extends GuiController {
    @FXML private StackPane backStackPane;
    @FXML ImageView token;
    @FXML Text message;
    @FXML Button next;
    @FXML BorderPane canvas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gui gui = Gui.getInstance();
        next.setOnAction(actionEvent -> {
            if(!gui.getViewModel().isGameEnded())
                gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
            else gui.setRoot(getClass().getResource("/assets/gui/endgame.fxml"));
        });

        AnchorPane.setTopAnchor(message, 20.0);
        AnchorPane.setBottomAnchor(next, 20.0);

        this.token.setImage(new Image(getTokenImage(Gui.getInstance().getViewModel().getLatestToken().orElseThrow())));

        AnchorPane.setTopAnchor(this.token, 200.0);

    }


    private String getTokenImage(ReducedActionToken token) {
        return "/assets/gui/actiontokens/" + token.getKind().toLowerCase() +
                (token.getDiscardedDevCardColor().isPresent() ? token.getDiscardedDevCardColor().get().toLowerCase() : "") + ".png";
    }
}
