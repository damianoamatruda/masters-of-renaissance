package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.netevents.ReqGoodbye;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class EndgameController extends GuiController {
    @FXML private Text outcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        ViewModel viewModel = Gui.getInstance().getViewModel();
        
        if(viewModel.getWinner() != null && viewModel.getWinner().equals(viewModel.getLocalPlayerNickname()))
            outcome.setText("You won with " + viewModel.getPlayerVictoryPoints(viewModel.getWinner()) + " points! CONGRATULATIONS!");
        else if (viewModel.getWinner() != null)
            outcome.setText(
                    viewModel.getWinner() + " is the winner with " +
                            viewModel.getPlayerVictoryPoints(viewModel.getWinner()) + " points!");
        else
            outcome.setText("Lorenzo il Magnifico has won. Better luck next time!");
    }

    @FXML
    private void handleQuit() {
        Gui.getInstance().dispatch(new ReqQuit());
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }

}
