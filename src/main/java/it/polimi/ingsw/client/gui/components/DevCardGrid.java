package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DevCardGrid extends HBox {
    private BiConsumer<ReducedDevCard, DevelopmentCard> controllerListener;

    public DevCardGrid() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/devcardgrid.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setControllerListener(BiConsumer<ReducedDevCard, DevelopmentCard> f) {
        controllerListener = f;
    }

    public void setGrid(ReducedDevCardGrid grid) {
        for(String color : grid.getGrid().keySet()) {
            VBox column = new VBox(5);
            for(int i = grid.getLevelsCount(); i >= 1 ; i--) {
                ReducedDevCard card;

                try {
                    int cardid = grid.getGrid().get(color).get(i).peek();
                    card = Gui.getInstance().getViewModel().getDevelopmentCard(cardid).orElseThrow(NullPointerException::new);
                } catch (NullPointerException e) {
                    column.getChildren().add(new Label());
                    continue;
                }

                DevelopmentCard guicard = new DevelopmentCard(card.getColor());

                ReducedResourceTransactionRecipe r = Gui.getInstance().getViewModel().getProduction(card.getProduction()).orElseThrow();
                guicard.setRequirement(card.getCost());
                guicard.setProduction(r);
                guicard.setLevel(card.getLevel());
                guicard.setVictoryPoints(card.getVictoryPoints()+"");

                // guicard.setBorder(new Border(new BorderStroke(Color.BLUE,
                //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                guicard.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    controllerListener.accept(card, guicard);
                    mouseEvent.consume();
                });
                column.getChildren().add(guicard);
            }
            this.getChildren().add(column);
        }
    }
}
