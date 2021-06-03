package it.polimi.ingsw.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import it.polimi.ingsw.client.gui.components.DevSlot;
import it.polimi.ingsw.client.gui.components.DevelopmentCard;
import it.polimi.ingsw.client.gui.components.Playerboard;
import it.polimi.ingsw.client.gui.components.Production;
import it.polimi.ingsw.client.gui.components.Strongbox;
import it.polimi.ingsw.client.gui.components.Warehouse;
import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public abstract class PlaygroundController extends GuiController {
    @FXML
    protected AnchorPane canvas;

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
        prod.setStyle("-fx-background-image: url('/assets/gui/playerboard/baseproduction.png');" +
                "-fx-background-position: center center;" +
                "-fx-alignment: center;" +
                "-fx-background-repeat: stretch;" +
                "-fx-opacity: 1;" +
                "-fx-background-size: 100 100;");
        prod.setProduction(p1);


        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseShelves(gui.getViewModel().getPlayerShelves(gui.getViewModel().getLocalPlayerNickname()));

        
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

        List<DevSlot> slots = new ArrayList<>();

        List<List<Integer>> modelSlots = gui.getViewModel().getPlayerData(gui.getViewModel().getLocalPlayerNickname()).getDevSlots();
        for (List<Integer> modelSlot : modelSlots) {
            DevSlot slot = new DevSlot();

            List<DevelopmentCard> cards = modelSlot.stream()
                    .map(i -> new DevelopmentCard(gui.getViewModel().getDevelopmentCard(i).orElseThrow()))
                    .toList();
            slot.setDevCards(cards);

            slots.add(slot);
        }
        for(int i = 0; i < gui.getViewModel().getSlotsCount() - modelSlots.size(); i++){
            slots.add(new DevSlot());
        }

        Playerboard pboard = new Playerboard(warehouse, s, prod, slots);

        canvas.getChildren().add(pboard);

        AnchorPane.setBottomAnchor(pboard, 0d);
        AnchorPane.setTopAnchor(pboard, 0d);
        AnchorPane.setLeftAnchor(pboard, 0d);
        AnchorPane.setRightAnchor(pboard, 0d);

        FaithTrack f = new FaithTrack(gui.getViewModel().getFaithTrack());
        Group g = new Group(f);
        g.setAutoSizeChildren(true);
        g.setScaleX(g.getScaleX() * 0.85);
        g.setScaleY(g.getScaleY() * 0.85);
        canvas.getChildren().add(g);
        AnchorPane.setLeftAnchor(canvas.getChildren().get(1), -250.0);
        AnchorPane.setTopAnchor(canvas.getChildren().get(1), -20.0);

        canvas.setBorder(new Border(new BorderStroke(Color.PINK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pboard.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

    }

}
