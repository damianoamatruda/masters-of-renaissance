package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.BiConsumer;

/** Gui component that represent the development card grid. */
public class DevCardGrid extends HBox {
    private BiConsumer<ReducedDevCard, DevelopmentCard> controllerListener;

    public DevCardGrid() {
        this.setSpacing(5.0);
    }

    /**
     *
     * @param f
     */
    public void setControllerListener(BiConsumer<ReducedDevCard, DevelopmentCard> f) {
        controllerListener = f;
    }

    /**
     * Sets and displays the actual grid, based on the cached model state.
     *
     * @param grid the grid to be displayed
     */
    public void setGrid(ReducedDevCardGrid grid) {
        for (String color : grid.getTopCards().keySet()) {
            VBox column = new VBox(5);
            for (int i = grid.getLevelsCount(); i >= 1; i--) {
                ReducedDevCard card;

                Optional<Integer> cardId = grid.getTopCards().get(color).get(i);
                if (cardId.isPresent())
                    card = Gui.getInstance().getViewModel().getDevelopmentCard(cardId.get()).orElseThrow(NullPointerException::new);
                else {
                    column.getChildren().add(new Label());
                    continue;
                }

                DevelopmentCard guicard = new DevelopmentCard(card.getColor());

                ReducedResourceTransactionRecipe r = Gui.getInstance().getViewModel().getProduction(card.getProduction()).orElseThrow();
                card.getCost().ifPresent(cost -> guicard.setRequirement(cost));
                guicard.setProduction(r);
                guicard.setLevel(card.getLevel());
                guicard.setVictoryPoints(card.getVictoryPoints()+"");

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
