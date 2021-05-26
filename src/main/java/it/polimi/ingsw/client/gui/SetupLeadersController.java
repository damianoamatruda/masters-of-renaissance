package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.LeaderCard;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SetupLeadersController extends GuiController {
    public HBox leadersContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Gui gui = Gui.getInstance();

        if (gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getUiData().getLocalPlayerNickname()) == null)
            throw new RuntimeException();

        List<LeaderCard> leaderCards = gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getUiData().getLocalPlayerNickname()).stream().map(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard();
            leaderCard.setLeaderType(reducedLeader.getLeaderType());
            leaderCard.setResourceType(reducedLeader.getResourceType());
            return leaderCard;
        }).toList();

        System.out.println(leaderCards);

        leadersContainer.getChildren().addAll(leaderCards);
    }
}
