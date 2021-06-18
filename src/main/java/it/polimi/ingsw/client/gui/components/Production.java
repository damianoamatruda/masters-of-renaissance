package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Production extends StackPane {
    private final double padding = 2;
    @FXML
    private VBox input, output;
    @FXML
    private ImageView curlyBrace;
    @FXML
    private HBox recipe;
    private double maxRowHeight;
    private double elementScale;
    private int productionId;

    /**
     *
     */
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

    /**
     *
     * @param production
     */
    public void setProduction(ReducedResourceTransactionRecipe production) {
        this.productionId = production.getId();

        elementScale = (this.getMaxHeight() - (2 * padding)) / this.getMaxHeight();
        
        maxRowHeight = (this.getMaxHeight() - (2 * padding)) / Math.max(
            production.getInput().entrySet().size() + (production.getInputBlanks() > 0 ? 1 : 0),
            production.getOutput().entrySet().size() + (production.getOutputBlanks() > 0 ? 1 : 0));
        
        input.setSpacing(2);
        output.setSpacing(2);

        // input.setScaleX(elementScale);
        // input.setScaleY(elementScale);
        // output.setScaleX(elementScale);
        // output.setScaleY(elementScale);
        
        // TODO: exclusions, where png?
        
        input.getChildren().addAll(buildResourceLines(production.getInput()));

        if (production.getInputBlanks() > 0) {
            Resource blank = new Resource(); blank.setResourceType("Zero"); // TODO: parameterize
            input.getChildren().add(row(production.getInputBlanks(), blank));
        }

        output.getChildren().addAll(buildResourceLines(production.getOutput()));

        if (production.getOutputBlanks() > 0) {
            Resource blank = new Resource(); blank.setResourceType("Zero"); // TODO: parameterize
            output.getChildren().add(row(production.getOutputBlanks(), blank));
        }
        
        this.setPadding(new Insets(padding));
    }

    /**
     *
     * @param resourceMap
     * @return
     */
    private List<HBox> buildResourceLines(Map<String, Integer> resourceMap) {
        return resourceMap.entrySet().stream().map(e -> {
            Resource r = new Resource();
            r.setResourceType(e.getKey());

            return row(e.getValue(), r);
        }).toList();
    }

    /**
     *
     * @param key
     * @param r
     * @return
     */
    private HBox row(int key, Resource r) {
        HBox box = new HBox(5);
        Text l = new Text(String.valueOf(key));
        box.getChildren().add(l);
        box.getChildren().add(r);

        box.setAlignment(Pos.CENTER);
        
        r.setFitHeight(maxRowHeight);
        r.setFitWidth(30);

        l.setScaleY(elementScale);
        l.setScaleX(elementScale);

        box.setMinHeight(0);
        box.setMaxHeight(maxRowHeight);

//        box.setBorder(new Border(new BorderStroke(Color.GREEN,
//            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        return box;
    }

    /**
     *
     * @return
     */
    public int getProductionId() {
        return productionId;
    }

    /**
     *
     * @param toActivate
     * @param activateProduction
     */
    public void addProduceButton(List<Integer> toActivate, SButton activateProduction) {
        // TODO duplicated code
        AnchorPane pane = new AnchorPane();
        SButton activate = new SButton();
        activate.setText("Produce");
        activate.setOnAction((event) -> {
//            top.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !top.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS));
//            if(top.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS))
            if(recipe.getOpacity() != 0.5) {
                recipe.setOpacity(0.5);
                toActivate.add(this.getProductionId());
                activateProduction.setDisable(false);
            }
            else {
                recipe.setOpacity(1);
                toActivate.remove(Integer.valueOf(this.getProductionId()));
                if(toActivate.size() == 0) activateProduction.setDisable(true);
            }
        });
        pane.getChildren().add(activate);
        AnchorPane.setLeftAnchor(activate, 25d);
        AnchorPane.setBottomAnchor(activate, -50d);

        this.getChildren().add(pane);
    }
}
