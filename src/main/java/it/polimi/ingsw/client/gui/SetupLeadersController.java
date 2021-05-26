package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SetupLeadersController extends GuiController {
    public HBox leadersContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Gui gui = Gui.getInstance();

        if (gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getUiData().getLocalPlayerNickname()) == null)
            throw new RuntimeException();
        
        Map<String, Integer> m1 = new HashMap<>();
        Map<String, Integer> m2 = Map.of("shield", 2);
        m1.put("coin", 1);
        m1.put("Shield", 2);
        ReducedResourceTransactionRecipe p1 = new ReducedResourceTransactionRecipe(0, m1, 1, null, m2, 2, null, false);

        List<LeaderCard> leaderCards = gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getUiData().getLocalPlayerNickname()).stream().map(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
            leaderCard.setLeaderType(reducedLeader.getLeaderType());
            leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints()+"");
            leaderCard.setResourceType(reducedLeader.getResourceType());
            leaderCard.setProduction(p1);
            return leaderCard;
        }).toList();

        System.out.println(leaderCards);

        leadersContainer.setSpacing(10);
        leadersContainer.setAlignment(Pos.CENTER);

        leadersContainer.getChildren().addAll(leaderCards);
    }
}
