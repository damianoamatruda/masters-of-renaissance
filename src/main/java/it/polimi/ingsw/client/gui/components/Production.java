package it.polimi.ingsw.client.gui.components;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Production extends HBox {
    public void setProduction(ReducedResourceTransactionRecipe production) {
        // TODO: exclusions, where png?
        Resource blank = new Resource(); blank.setResourceType("blank"); // TODO: parameterize
        VBox input = new VBox(), output = new VBox();
        
        input.getChildren().addAll(buildResourceLines(production.getInput()));

        if (production.getInputBlanks() > 0)
            input.getChildren().add(new HBox(new Label(String.valueOf(production.getInputBlanks())), blank));

        output.getChildren().addAll(buildResourceLines(production.getOutput()));

        if (production.getOutputBlanks() > 0)
            output.getChildren().add(new HBox(new Label(String.valueOf(production.getOutputBlanks())), blank));
        
        this.getChildren().add(input);
        this.getChildren().add(new ImageView(
            new Image(getClass().getResource("/assets/gui/resourcetypes/curlybrace.png").toExternalForm())));
        this.getChildren().add(output);

        input.setMaxHeight(100);
        int x = 0;
    }

    private List<HBox> buildResourceLines(Map<String, Integer> resourceMap) {
        return resourceMap.entrySet().stream().map(e -> {
            Resource r = new Resource();
            r.setResourceType(e.getKey());
            
            return new HBox(new Label(String.valueOf(e.getValue())), r);
        }).toList();
    }
}
