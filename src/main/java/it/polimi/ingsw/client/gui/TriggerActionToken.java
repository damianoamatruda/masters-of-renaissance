package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/** Gui controller used on Solo game turn switch, which displays the activated action token. */
public class TriggerActionToken extends GuiController {
    @FXML private BorderPane canvas;
    @FXML private ImageView token;
    @FXML private Text message;
    @FXML private Button next;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(gui.getRoot().widthProperty().divide(Gui.realWidth),
                gui.getRoot().heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);

        next.setOnAction(actionEvent -> {
            if (!vm.isGameEnded())
                gui.setScene(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
            else gui.setScene(getClass().getResource("/assets/gui/endgame.fxml"));
        });

        AnchorPane.setTopAnchor(message, 20.0);
        AnchorPane.setBottomAnchor(next, 20.0);

        this.token.setImage(new Image(getTokenImage(Gui.getInstance().getViewModel().getLatestToken().orElseThrow())));

        AnchorPane.setTopAnchor(this.token, 200.0);
    }

    /**
     * Get the path to the ActionToken Image component matching the activated token type
     *
     * @param token the activated token
     * @return the path to the correct ActionToken Image component
     */
    private String getTokenImage(ReducedActionToken token) {
        return "/assets/gui/actiontokens/" + token.getKind().toLowerCase() +
                (token.getDiscardedDevCardColor().isPresent() ? token.getDiscardedDevCardColor().get().toLowerCase() : "") + ".png";
    }
}
