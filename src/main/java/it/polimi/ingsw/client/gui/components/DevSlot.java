package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

/** Gui component that represents a runtime development slot. */
public class DevSlot extends StackPane {
    // @FXML private GridPane grid;
    private List<DevelopmentCard> cards;
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    /**
     * Class constructor.
     */
    public DevSlot() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/devslot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        Image bgimg = new Image(
                Objects.requireNonNull(getClass().getResource("/assets/gui/playerboard/devslot.PNG")).toExternalForm());

        BackgroundImage bg = new BackgroundImage(bgimg,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, true, false));

        this.setBackground(new Background(bg));

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Sets and displays the development cards inserted in this slot.
     *
     * @param cards the cards belonging to this slot
     */
    public void setDevCards(List<DevelopmentCard> cards) {
        this.cards = cards;

        // double topPaddingPercent = 0.122,
        //        bottomPaddingPercent = 0.095;
        // this.setPadding(new Insets(this.getHeight() * topPaddingPercent, 0, this.getHeight() * bottomPaddingPercent, 0));

        DevelopmentCard c;
        for (int i = 0; i < cards.size(); i++) {
            // grid.add(cards.get(i), 0, 3 - i);
            c = cards.get(i);

            AnchorPane p = new AnchorPane();

            p.getChildren().add(c);

            AnchorPane.setLeftAnchor(c, 25d);
            AnchorPane.setBottomAnchor(c, 90d + 50 * i);

            this.getChildren().add(p);
        }

        // grid.getChildren().forEach(c -> {
        //     c.setScaleX(this.getWidth());
        // });
    }

    /**
     * Method used to add input buttons to activate the top card's production.
     *
     * @param toActivate         the list of productions to be activated
     * @param activateProduction the button to be disabled, if no production is selected
     */
    public void addProduceButton(List<Integer> toActivate, SButton activateProduction) {
        if(cards != null && cards.size() > 0) {
            DevelopmentCard top = cards.get(cards.size() - 1);
            AnchorPane p = new AnchorPane();
            SButton activate = new SButton();
            activate.setText("Produce");
            activate.setOnAction((event) -> {
                top.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !top.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS));
                if(top.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS)) {
                    toActivate.add(top.getProduction().getProductionId());
                    activateProduction.setDisable(false);
                } else {
                    toActivate.remove(Integer.valueOf(top.getProduction().getProductionId()));
                    if(toActivate.size() == 0) activateProduction.setDisable(true);
                }
            });
            p.getChildren().add(activate);
            AnchorPane.setLeftAnchor(activate, 70d);
            AnchorPane.setBottomAnchor(activate, 50d);

            this.getChildren().add(p);
        }
    }
}
