package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DevCardGrid extends HBox {

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

    public void setGrid(ReducedDevCardGrid grid) {
        for(String color : grid.getGrid().keySet()) {
            VBox column = new VBox();
            for(int i = 1; i <= grid.getLevelsCount(); i++) {
                ReducedDevCard card;

                try {
                    int cardid = grid.getGrid().get(color).get(i).peek();
                    card = Gui.getInstance().getViewModel().getGameData().getDevelopmentCard(cardid).orElseThrow(NullPointerException::new);
                } catch (NullPointerException e) {
                    column.getChildren().add(new Label());
                    continue;
                }

                DevelopmentCard guicard = new DevelopmentCard(card.getColor());

                ReducedResourceTransactionRecipe r = Gui.getInstance().getViewModel().getGameData().getProduction(card.getProduction()).orElseThrow();
                guicard.setRequirement(card.getCost());
                guicard.setProduction(r);
                guicard.setVictoryPoints(card.getVictoryPoints()+"");

                column.getChildren().add(guicard);
            }
            this.getChildren().add(column);
        }
    }
}
