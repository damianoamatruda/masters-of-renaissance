package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import java.net.URL;
import java.util.ResourceBundle;

public class TriggerActionToken extends GuiController {
    @FXML private StackPane backStackPane;
    @FXML private BorderPane canvas;
    @FXML private ImageView token;
    @FXML private Text message;
    @FXML private Button next;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NumberBinding maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);

        backStackPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        canvas.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Gui gui = Gui.getInstance();
        next.setOnAction(actionEvent -> {
            if (!gui.getViewModel().isGameEnded())
                gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
            else gui.setRoot(getClass().getResource("/assets/gui/endgame.fxml"));
        });

        AnchorPane.setTopAnchor(message, 20.0);
        AnchorPane.setBottomAnchor(next, 20.0);

        this.token.setImage(new Image(getTokenImage(Gui.getInstance().getViewModel().getLatestToken().orElseThrow())));

        AnchorPane.setTopAnchor(this.token, 200.0);

    }

    /**
     *
     * @param token
     * @return
     */
    private String getTokenImage(ReducedActionToken token) {
        return "/assets/gui/actiontokens/" + token.getKind().toLowerCase() +
                (token.getDiscardedDevCardColor().isPresent() ? token.getDiscardedDevCardColor().get().toLowerCase() : "") + ".png";
    }
}
