package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.Objects;

/** Gui component used to show card requirements. */
public class CardRequirement extends HBox {
    private static final double maxRowHeight = 30; // TODO: Parameterize

    /**
     * Builds graphic components for the resource requirements.
     *
     * @param requirements the resource requirements of the card
     */
    public void setRequirements(ReducedResourceRequirement requirements) {
        for (String resource : requirements.getRequirements().keySet()) {
            HBox entry = new HBox();

            Text l = new Text(String.valueOf(requirements.getRequirements().get(resource)));
            l.maxHeight(maxRowHeight);
            HBox.setMargin(l, new Insets(0, 0, 0, 6));

            Resource r = new Resource(resource);
            r.setPreserveRatio(true);
            r.setFitHeight(maxRowHeight);
            r.setFitWidth(30);
            // HBox.setMargin(r, new Insets(0, 0, 0, 4));

            entry.getChildren().add(l);
            entry.getChildren().add(r);

            entry.setAlignment(Pos.CENTER);
            entry.maxHeight(maxRowHeight);

            this.getChildren().add(entry);
        }
    }

    /**
     * Builds graphic components for the development card requirements.
     *
     * @param requirements the requirements on owned cards for this card
     */
    public void setRequirements(ReducedDevCardRequirement requirements) {
        for (ReducedDevCardRequirementEntry color : requirements.getEntries()) {
            HBox entry = new HBox();

            Text amountText = new Text(String.valueOf(color.getAmount()));

            String fileName = color.getColor().toLowerCase();
            if (color.getLevel() > 0) fileName += String.valueOf(color.getLevel());

            ImageView levelImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    String.format("/assets/gui/cardrequirements/%s.png", fileName)))));

            HBox.setMargin(levelImage, new Insets(0, 5, 0, 3));

            entry.getChildren().add(amountText);
            entry.getChildren().add(levelImage);
            entry.setAlignment(Pos.CENTER);

            levelImage.setPreserveRatio(true);
            levelImage.setFitHeight(maxRowHeight);
            amountText.maxHeight(maxRowHeight);

            entry.maxHeight(maxRowHeight);

            this.getChildren().add(entry);
        }
    }

}
