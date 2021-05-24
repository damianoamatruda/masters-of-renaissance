package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;

public class InputNicknameController extends GuiController {
    public TextField nickname;

    public void handleNicknameInput(ActionEvent actionEvent) {
        Gui.dispatch(new ReqJoin(nickname.getText()));
    }

    public void handleBack(ActionEvent actionEvent) throws IOException {
        Gui.setRoot(Gui.isSingleplayer() ? "mainmenu" : "multiplayer");
    }

    @Override
    public void on(Gui gui, ErrNickname event) {
        super.on(gui, event);
        System.out.println("Nickname is invalid. Reason: " + event.getReason().toString().toLowerCase());
    }

    @Override
    public void on(Gui gui, UpdateBookedSeats event) {
        super.on(gui, event);
        if (Gui.isSingleplayer())
            Gui.dispatch(new ReqNewGame(1));
        else
            try {
                Gui.setRoot("waitingbeforegame");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void on(Gui gui, UpdateGame event) {
        super.on(gui, event);
        if (Gui.isSingleplayer()) {
            try {
                Gui.setRoot("setupleaders");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
