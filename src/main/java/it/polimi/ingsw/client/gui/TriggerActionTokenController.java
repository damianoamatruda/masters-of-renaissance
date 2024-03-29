package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * GUI controller used on Solo game turn switch, which displays the activated action token.
 */
public class TriggerActionTokenController extends GuiController {
    @FXML
    private BorderPane canvas;
    @FXML
    private ImageView token;
    @FXML
    private Text message;
    @FXML
    private Button next;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);
        gui.setPauseHandler(canvas);

        next.setOnAction(actionEvent -> {
            if (!vm.isGameEnded())
                gui.setScene(getClass().getResource("/assets/gui/scenes/turnbeforeaction.fxml"));
            else gui.setScene(getClass().getResource("/assets/gui/scenes/endgame.fxml"));
        });

        AnchorPane.setTopAnchor(message, 20.0);
        AnchorPane.setBottomAnchor(next, 20.0);

        this.token.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(getTokenImage(vm.getLatestToken().orElseThrow())))));

        AnchorPane.setTopAnchor(this.token, 200.0);
    }

    /**
     * Get the path to the ActionToken Image component matching the activated token type
     *
     * @param token the activated token
     * @return the path to the correct ActionToken Image component
     */
    private String getTokenImage(ReducedActionToken token) {
        return "/assets/gui/images/actiontokens/" + token.getKind().toLowerCase() +
                (token.getDiscardedDevCardColor().isPresent() ? token.getDiscardedDevCardColor().get().toLowerCase() : "") + ".png";
    }
}
