package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardRequirement extends HBox {
    List<HBox> requirements;
    private double maxRowHeight;

    public CardRequirement() {
        maxRowHeight = 30; //TODO parameterize
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/cardrequirement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setRequirements(ReducedResourceRequirement requirements) {
        if(requirements != null) {
            this.requirements = new ArrayList<>();
            for (String resource : requirements.getRequirements().keySet()) {
                HBox entry = new HBox();

                Resource r = new Resource();
                r.setResourceType(resource);

                Text l = new Text(requirements.getRequirements().get(resource) + "");

                entry.getChildren().add(l);
                entry.getChildren().add(r);

                entry.setAlignment(Pos.CENTER);

                r.setPreserveRatio(true);
                r.setFitHeight(maxRowHeight);
                l.maxHeight(maxRowHeight);

                entry.maxHeight(maxRowHeight);

                entry.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                this.getChildren().add(entry);
            }
        }
    }

    public void setRequirements(ReducedDevCardRequirement requirements) {
        if(requirements != null) {
            this.requirements = new ArrayList<>();
            for (ReducedDevCardRequirementEntry color : requirements.getEntries()) {
                HBox entry = new HBox();

                ImageView i = new ImageView();
                String fileName = color.getColor().toLowerCase();
                if (color.getLevel() > 0) fileName += color.getLevel() + "";
                i.setImage(new Image(Objects.requireNonNull(getClass().getResource(
                        String.format("/assets/gui/cardrequirements/%s.png", fileName))).toExternalForm()));

                Text l = new Text(color.getAmount() + "");
                entry.getChildren().add(l);
                entry.getChildren().add(i);

                entry.setAlignment(Pos.CENTER);

                i.setPreserveRatio(true);
                i.setFitHeight(maxRowHeight);
                l.maxHeight(maxRowHeight);

                entry.maxHeight(maxRowHeight);

                entry.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

//            this.requirements.add(entry);
                this.getChildren().add(entry);
            }
        }
    }

}
