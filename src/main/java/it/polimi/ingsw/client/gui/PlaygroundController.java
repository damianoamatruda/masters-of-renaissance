package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

public class PlaygroundController extends GuiController {
    @FXML
    private AnchorPane canvas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        Gui gui = Gui.getInstance();

        Map<String, Integer> m1 = new HashMap<>();
        Map<String, Integer> m2 = Map.of("shield", 2);
        m1.put("coin", 1);
        m1.put("Shield", 2);
        m1.put("Servant", 2);
        ReducedResourceTransactionRecipe p1 = new ReducedResourceTransactionRecipe(0, m1, 1, null, m2, 2, null, false);

        Production prod = new Production();
        prod.setProduction(p1);

        Warehouse w = new Warehouse();
        List<ReducedResourceContainer> containers = new ArrayList<>();
        containers.add(new ReducedResourceContainer(0, 1, Map.of("Coin", 1), "Coin"));
        containers.add(new ReducedResourceContainer(0, 2, Map.of("Shield", 2), "Shield"));
        containers.add(new ReducedResourceContainer(0, 3, Map.of(), null));

        w.setWarehouseShelves(containers);
        
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

        ReducedDevCard card = gui.getViewModel().getDevelopmentCard(0).orElseThrow();

        DevelopmentCard guicard = new DevelopmentCard(card.getColor());
        guicard.setProduction(p1);
        guicard.setRequirement(card.getCost());
        guicard.setVictoryPoints(12+"");
        DevelopmentCard guicard2 = new DevelopmentCard(card.getColor());
        guicard2.setProduction(p1);
        guicard2.setRequirement(card.getCost());
        guicard2.setVictoryPoints(12+"");
        DevelopmentCard guicard3 = new DevelopmentCard(card.getColor());
        guicard3.setProduction(p1);
        guicard3.setRequirement(card.getCost());
        guicard3.setVictoryPoints(12+"");

        DevSlot slot = new DevSlot();
        slot.setDevCards(List.of(guicard, guicard2, guicard3));

        Playerboard pboard = new Playerboard(w, s, prod, slot);

        canvas.getChildren().add(pboard);

        AnchorPane.setBottomAnchor(pboard, 0d);
        AnchorPane.setTopAnchor(pboard, 0d);
        AnchorPane.setLeftAnchor(pboard, 0d);
        AnchorPane.setRightAnchor(pboard, 0d);

        canvas.setBorder(new Border(new BorderStroke(Color.PINK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pboard.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}
