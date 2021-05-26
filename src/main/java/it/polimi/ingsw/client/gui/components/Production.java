package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Production extends HBox {
    VBox input, output;
    ImageView curlyBrace;
    private double maxRowHeight;

    public Production() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/production.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setProduction(ReducedResourceTransactionRecipe production) {
        maxRowHeight = this.getMaxHeight() / Math.max(
            production.getInput().entrySet().size() + (production.getInputBlanks() > 0 ? 1 : 0),
            production.getOutput().entrySet().size() + (production.getOutputBlanks() > 0 ? 1 : 0));

        this.setAlignment(Pos.CENTER);
        this.setBorder(new Border(new BorderStroke(Color.VIOLET, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        // TODO: exclusions, where png?
        Resource blank = new Resource(); blank.setResourceType("blank"); // TODO: parameterize
        input = (VBox) this.getChildren().get(0);
        output = (VBox) this.getChildren().get(2);

        input.setBorder(new Border(new BorderStroke(Color.RED, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        output.setBorder(new Border(new BorderStroke(Color.BLUE, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        
        input.getChildren().addAll(buildResourceLines(production.getInput()));

        if (production.getInputBlanks() > 0)
            input.getChildren().add(row(production.getInputBlanks(), blank));

        output.getChildren().addAll(buildResourceLines(production.getOutput()));

        if (production.getOutputBlanks() > 0)
            output.getChildren().add(row(production.getOutputBlanks(), blank));
        
        curlyBrace = (ImageView) this.getChildren().get(1);
        curlyBrace.setImage(new Image(Objects.requireNonNull(getClass().getResource("/assets/gui/resourcetypes/curlybracelight.png")).toExternalForm()));
    }

    private List<HBox> buildResourceLines(Map<String, Integer> resourceMap) {
        return resourceMap.entrySet().stream().map(e -> {
            Resource r = new Resource();
            r.setResourceType(e.getKey());

            return row(e.getValue(), r);
        }).toList();
    }

    private HBox row(int key, Resource r) {
        HBox box = new HBox();
        Text l = new Text(String.valueOf(key));
        box.getChildren().add(l);
        box.getChildren().add(r);

        box.setAlignment(Pos.CENTER);
        
        r.setPreserveRatio(true);
        r.setFitHeight(maxRowHeight);
        l.maxHeight(maxRowHeight);

        box.maxHeight(maxRowHeight);

        box.setBorder(new Border(new BorderStroke(Color.GREEN,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        return box;
    }
}
