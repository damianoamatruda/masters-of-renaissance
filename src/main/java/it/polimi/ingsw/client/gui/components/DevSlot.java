package it.polimi.ingsw.client.gui.components;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * GUI component that represents a runtime development slot.
 */
public class DevSlot extends StackPane {
    private final BiConsumer<DevelopmentCard, Production> onProduce;
    private List<DevelopmentCard> devCards;

    /**
     * Class constructor.
     */
    public DevSlot(BiConsumer<DevelopmentCard, Production> onProduce) {
        this.onProduce = onProduce;

        Image bgImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gui/playerboard/devslot.png")));
        this.setBackground(new Background(new BackgroundImage(bgImg,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, true, false))));

        this.minWidthProperty().addListener(n -> this.setMaxHeight(this.getMinWidth() * 2.33));
    }

    public DevSlot() {
        this(null);
    }

    /**
     * Sets and displays the development cards inserted in this slot.
     *
     * @param devCards the cards belonging to this slot
     */
    public void setDevCards(List<DevelopmentCard> devCards) {
        this.devCards = devCards;

        int i = 0;
        for (DevelopmentCard devCard : devCards) {
            AnchorPane devCardContainer = new AnchorPane();

            AnchorPane.setLeftAnchor(devCard, 25d);
            AnchorPane.setBottomAnchor(devCard, 65d + 50 * i);
            devCardContainer.getChildren().add(devCard);

            getChildren().add(devCardContainer);

            i++;
        }
    }

    /**
     * Method used to add input buttons to activate the top card's production.
     */
    public void addProduceButton() {
        if (devCards == null || devCards.isEmpty())
            return;

        DevelopmentCard topDevCard = devCards.get(devCards.size() - 1);

        AnchorPane activateContainer = new AnchorPane();

        SButton activate = new SButton("Produce");
        activate.setOnAction(event -> onProduce.accept(topDevCard, topDevCard.getProduction()));
        AnchorPane.setLeftAnchor(activate, 75d);
        AnchorPane.setBottomAnchor(activate, 22d);
        activateContainer.getChildren().add(activate);

        this.getChildren().add(activateContainer);
    }
}
