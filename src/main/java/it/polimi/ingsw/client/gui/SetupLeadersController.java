package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.reducedmodel.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

public class SetupLeadersController extends GuiController {
    public HBox leadersContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Gui gui = Gui.getInstance();

        // if (gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getUiData().getLocalPlayerNickname()) == null)
        //     throw new RuntimeException();

        leadersContainer.setSpacing(10);
        leadersContainer.setAlignment(Pos.CENTER);


        Map<String, Integer> m1 = new HashMap<>();
        Map<String, Integer> m2 = Map.of("shield", 2);
        m1.put("coin", 1);
        m1.put("Shield", 2);
        m1.put("Servant", 2);
        ReducedResourceTransactionRecipe p1 = new ReducedResourceTransactionRecipe(0, m1, 1, null, m2, 2, null, false);

        StackPane pane = new StackPane();

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
//
//        System.out.println(leaderCards);

        leadersContainer.getChildren().addAll(leaderCards);

        // Warehouse w = new Warehouse();
        // List<ReducedResourceContainer> containers = new ArrayList<>();
        // containers.add(new ReducedResourceContainer(0, 1, Map.of("Coin", 1), "Coin"));
        // containers.add(new ReducedResourceContainer(0, 2, Map.of("Shield", 2), "Shield"));
        // containers.add(new ReducedResourceContainer(0, 3, Map.of(), null));

//         w.setWarehouseShelves(containers);
//         leadersContainer.getChildren().add(w);



        // // prod.maxWidthProperty().bind(pane.maxWidthProperty());
        // // prod.maxHeightProperty().bind(pane.maxHeightProperty());

        // pane.getChildren().add(prod);
        // pane.setBorder(new Border(new BorderStroke(Color.BLACK, 
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        // // System.out.println(leaderCards);
        // pane.setAlignment(Pos.CENTER);
        // pane.setPadding(new Insets(2));
        // pane.setMaxHeight(60);

        // leadersContainer.getChildren().add(pane);
        
//         Map<String, Integer> content = new HashMap<>();
//         content.put("Coin", 1);
//         content.put("Shield", 2);
//         content.put("Servant", 2);
//         content.put("Stone", 2);
//         content.put("Blank", 2);
//         content.put("Faith", 2);
//
//         Strongbox s = new Strongbox();
//         ReducedResourceContainer c = new ReducedResourceContainer(0, -1, content, null);
//
//         s.setContent(c);
//
//         leadersContainer.getChildren().add(s);

//        ReducedDevCard card = gui.getViewModel().getGameData().getDevelopmentCard(0).orElseThrow();
//
//        DevelopmentCard guicard = new DevelopmentCard(card.getColor());
//        guicard.setProduction(p1);
//        guicard.setRequirement(card.getCost());
//        guicard.setVictoryPoints(12+"");
//        leadersContainer.getChildren().add(guicard);

//        ReducedDevCardGrid grid = gui.getViewModel().getGameData().getDevCardGrid();
//
//        DevCardGrid guigrid = new DevCardGrid();
//        guigrid.setGrid(grid);
//
//        leadersContainer.getChildren().add(guigrid);
    }
}
