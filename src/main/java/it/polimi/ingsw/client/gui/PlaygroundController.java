package it.polimi.ingsw.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import it.polimi.ingsw.client.gui.components.DevCardGrid;
import it.polimi.ingsw.client.gui.components.DevelopmentCard;
import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.client.gui.components.Market;
import it.polimi.ingsw.client.gui.components.Production;
import it.polimi.ingsw.client.gui.components.Strongbox;
import it.polimi.ingsw.client.gui.components.Warehouse;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class PlaygroundController extends GuiController {
    @FXML
    private GridPane canvas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/playground.fxml"));
        // fxmlLoader.setController(this);
        Gui gui = Gui.getInstance();

        canvas.setGridLinesVisible(true);

        canvas.setAlignment(Pos.CENTER);

        double scaleX, scaleY, scaleF;


        Map<String, Integer> m1 = new HashMap<>();
        Map<String, Integer> m2 = Map.of("shield", 2);
        m1.put("coin", 1);
        m1.put("Shield", 2);
        m1.put("Servant", 2);
        ReducedResourceTransactionRecipe p1 = new ReducedResourceTransactionRecipe(0, m1, 1, null, m2, 2, null, false);


        Production prod = new Production();
        prod.setProduction(p1);

        List<LeaderCard> leaderCards = gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getUiData().getLocalPlayerNickname()).stream().map(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
            leaderCard.setLeaderType(reducedLeader.getLeaderType());
            leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints()+"");
            leaderCard.setResourceType(reducedLeader.getResourceType());
            leaderCard.setRequirement(reducedLeader.getResourceRequirement());
            leaderCard.setRequirement(reducedLeader.getDevCardRequirement());
            leaderCard.setProduction(p1);
            return leaderCard;
        }).toList();

        System.out.println(leaderCards);

        scaleX = (1280 / canvas.getColumnCount()) / leaderCards.get(0).getPrefWidth();
        scaleY = (780 / canvas.getRowCount()) / leaderCards.get(0).getPrefHeight();
        scaleF = 0.9 * Math.min(scaleX, scaleY);
        
        leaderCards.get(0).setScaleX(scaleF);
        leaderCards.get(0).setScaleY(scaleF);


        canvas.add(leaderCards.get(0), 0, 0);


        Warehouse w = new Warehouse();
        List<ReducedResourceContainer> containers = new ArrayList<>();
        containers.add(new ReducedResourceContainer(0, 1, Map.of("Coin", 1), "Coin"));
        containers.add(new ReducedResourceContainer(0, 2, Map.of("Shield", 2), "Shield"));
        containers.add(new ReducedResourceContainer(0, 3, Map.of(), null));

        w.setWarehouseShelves(containers);
        
        scaleX = (1280 / canvas.getColumnCount()) / w.getPrefWidth();
        scaleY = (780 / canvas.getRowCount()) / w.getPrefHeight();
        scaleF = 0.9 * Math.min(scaleX, scaleY);
        
        w.setScaleX(scaleF);
        w.setScaleY(scaleF);

        canvas.add(w, 0, 1);


        
        Map<String, Integer> content = new HashMap<>();
        content.put("Coin", 1);
        content.put("Shield", 2);
        content.put("Servant", 2);
        content.put("Stone", 2);
        content.put("zero", 2);
        content.put("Faith", 2);

        Strongbox s = new Strongbox();
        ReducedResourceContainer c = new ReducedResourceContainer(0, -1, content, null);

        s.setContent(c);

        scaleX = (1280 / canvas.getColumnCount()) / s.getPrefWidth();
        scaleY = (780 / canvas.getRowCount()) / s.getPrefHeight();
        scaleF = 0.9 * Math.min(scaleX, scaleY);
        
        s.setScaleX(scaleF);
        s.setScaleY(scaleF);

        canvas.add(s, 2, 0);


        ReducedDevCard card = gui.getViewModel().getGameData().getDevelopmentCard(0).orElseThrow();

        DevelopmentCard guicard = new DevelopmentCard(card.getColor());
        guicard.setProduction(p1);
        guicard.setRequirement(card.getCost());
        guicard.setVictoryPoints(12+"");
        
        canvas.add(guicard, 1, 0);



        Market m = new Market();

        List<String> r0 = List.of("Coin", "Shield", "Servant", "Stone");
        List<List<String>> mgrid = List.of(r0, r0, r0);
        ReducedMarket rm = new ReducedMarket(mgrid, null, "Coin");

        m.setContent(rm);

        canvas.add(m, 2, 1);

    }
}
