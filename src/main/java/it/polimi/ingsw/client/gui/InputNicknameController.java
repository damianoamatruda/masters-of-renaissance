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

    private String nicknameValue;

    public void handleNicknameInput(ActionEvent actionEvent) {
        Gui gui = Gui.getInstance();
        nicknameValue = nickname.getText();
        gui.dispatch(new ReqJoin(nicknameValue));
    }

    public void handleBack(ActionEvent actionEvent) throws IOException {
        Gui gui = Gui.getInstance();
        gui.setRoot(gui.isSingleplayer() ? "mainmenu" : "multiplayer");
    }

    @Override
    public void on(Gui gui, ErrNickname event) {
        super.on(gui, event);
        System.out.println("Nickname is invalid. Reason: " + event.getReason().toString().toLowerCase());
    }

    @Override
    public void on(Gui gui, UpdateBookedSeats event) {
        super.on(gui, event);
        gui.getCache().getUiData().setLocalPlayerNickname(nicknameValue);
        if (gui.isSingleplayer())
            gui.dispatch(new ReqNewGame(1));
        else
            try {
                gui.setRoot("waitingbeforegame", (WaitingBeforeGameController controller) -> {
                    controller.setBookedSeats(event.getBookedSeats());
                    controller.setCanPrepareNewGame(event.canPrepareNewGame());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void on(Gui gui, UpdateGame event) {
        super.on(gui, event);
        if (gui.isSingleplayer()) {
            try {
                gui.setRoot("setupleaders");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
