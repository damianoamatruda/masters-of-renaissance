package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import java.net.URL;
import java.util.ResourceBundle;

public class InputNicknameController extends GuiController {
    @FXML
    private StackPane backStackPane;
    @FXML
    private BorderPane bpane;
    @FXML
    private Title titleComponent;
    @FXML
    private TextField nickname;
    private String nicknameValue;

    private NumberBinding maxScale;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));

        backStackPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        bpane.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
    }
    
    @FXML
    private void handleNicknameInput() {
        nicknameValue = nickname.getText();
        Gui.getInstance().dispatch(new ReqJoin(nicknameValue));
    }

    @FXML
    private void handleBack() {
        Gui gui = Gui.getInstance();
        gui.setRoot(getClass().getResource(gui.isOffline() ? "/assets/gui/mainmenu.fxml" : "/assets/gui/playonline.fxml"));
    }

    public void setTitle(String value) {
        titleComponent.setText(value);
    }

    @Override
    public void on(Gui gui, ErrNickname event) {
        super.on(gui, event);
        Platform.runLater(() ->
            backStackPane.getChildren().add(
                new Alert("Play Online", String.format("Nickname is invalid. Reason: %s.", event.getReason().toString().toLowerCase()), maxScale)));
    }

    @Override
    public void on(Gui gui, UpdateBookedSeats event) {
        super.on(gui, event);
        gui.getViewModel().setLocalPlayerNickname(nicknameValue);
        if (gui.isOffline())
            gui.dispatch(new ReqNewGame(1));
        else
            gui.setRoot(getClass().getResource("/assets/gui/waitingbeforegame.fxml"), (WaitingBeforeGameController controller) -> {
                controller.setBookedSeats(event.getBookedSeats());
                controller.setCanPrepareNewGame(event.canPrepareNewGame());
            });
    }

    @Override
    public void on(Gui gui, UpdateGame event) {
        super.on(gui, event);

        if(event.isResumed()) {
            if (gui.getViewModel().getCurrentPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
                gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
            else gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));
        }
    }

    @Override
    public void on(Gui gui, UpdateLeadersHand event) {
        super.on(gui, event);
        if (gui.isOffline())
            gui.setRoot(getClass().getResource("/assets/gui/setupleaders.fxml"));
    }
}
