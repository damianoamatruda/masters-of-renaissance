package it.polimi.ingsw.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import it.polimi.ingsw.client.gui.components.DevSlot;
import it.polimi.ingsw.client.gui.components.DevelopmentCard;
import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.client.gui.components.Market;
import it.polimi.ingsw.client.gui.components.Playerboard;
import it.polimi.ingsw.client.gui.components.Production;
import it.polimi.ingsw.client.gui.components.Strongbox;
import it.polimi.ingsw.client.gui.components.Warehouse;
import it.polimi.ingsw.common.reducedmodel.*;
import javafx.beans.value.ObservableValue;
import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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

        List<DevSlot> slots = new ArrayList<>();
        DevSlot slot = new DevSlot();
        slot.setDevCards(List.of(guicard, guicard2, guicard3));
        slots.add(slot);
        slots.add(new DevSlot());

        Playerboard pboard = new Playerboard(warehouse, s, prod, slots);

        canvas.getChildren().add(pboard);

        AnchorPane.setBottomAnchor(pboard, 0d);
        AnchorPane.setTopAnchor(pboard, 0d);
        AnchorPane.setLeftAnchor(pboard, 0d);
        AnchorPane.setRightAnchor(pboard, 0d);

        Button left = new Button();
//        left.setAlignment(Pos.BOTTOM_LEFT);
        left.setText("Market");
        left.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            try {
                gui.setRoot(getClass().getResource("/assets/gui/market.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        canvas.getChildren().add(left);

        Button right = new Button();
//        right.setAlignment(Pos.CENTER_RIGHT);
        right.setText("Grid");
//        right.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
//            try {
//                gui.setRoot("grid");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        canvas.getChildren().add(right);
        AnchorPane.setRightAnchor(canvas.getChildren().get(2), 0.0);
//        AnchorPane.setBottomAnchor(canvas.getChildren().get(1), this.canvas.getHeight()/2);
//        AnchorPane.setBottomAnchor(canvas.getChildren().get(2), 100.0);



        canvas.setBorder(new Border(new BorderStroke(Color.PINK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pboard.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

    }

}
