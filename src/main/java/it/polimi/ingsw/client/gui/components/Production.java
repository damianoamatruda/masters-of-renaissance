package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * GUI component that represents a production recipe.
 */
public class Production extends StackPane {
    private static final double padding = 2;
    private final BiConsumer<Production, Production> onProduce;
    @FXML
    private VBox input, output;
    @FXML
    private ImageView curlyBrace;
    @FXML
    private HBox recipe;
    @FXML
    private HBox exclusions;
    private double maxRowHeight;
    private double elementScale;
    private int productionId;


    /**
     * Class constructor.
     */
    public Production(BiConsumer<Production, Production> onProduce) {
        this.onProduce = onProduce;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/production.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Production() {
        this(null);
    }

    /**
     * Sets and displays the production recipe.
     *
     * @param production the cached production
     */
    public void setProduction(ReducedResourceTransactionRecipe production) {
        this.productionId = production.getId();

        elementScale = (this.getMaxHeight() - (2 * padding)) / this.getMaxHeight();

        maxRowHeight = (this.getMaxHeight() - (2 * padding)) / Math.max(
                production.getInput().entrySet().size() + (production.getInputBlanks() > 0 ? 1 : 0),
                production.getOutput().entrySet().size() + (production.getOutputBlanks() > 0 ? 1 : 0));

        input.setSpacing(2);
        output.setSpacing(2);

        /* Fill input exclusions */
        if (!production.getInputBlanksExclusions().isEmpty() || !production.getOutputBlanksExclusions().isEmpty()) {
            VBox inputExclusions = new VBox();
            inputExclusions.setMinWidth(60);
            if (!production.getInputBlanksExclusions().isEmpty())
                fillExclusions(inputExclusions, production.getInputBlanksExclusions());
            exclusions.getChildren().add(inputExclusions);
        }

        /* Fill output exclusions */
        if (!production.getOutputBlanksExclusions().isEmpty()) {
            VBox outputExclusions = new VBox();
            fillExclusions(outputExclusions, production.getOutputBlanksExclusions());
            exclusions.getChildren().add(outputExclusions);
        }

        input.getChildren().addAll(buildResourceLines(production.getInput()));

        if (production.getInputBlanks() > 0) {
            Resource blank = new Resource("Zero");
            input.getChildren().add(row(production.getInputBlanks(), blank));
        }

        output.getChildren().addAll(buildResourceLines(production.getOutput()));

        if (production.getOutputBlanks() > 0) {
            Resource blank = new Resource("Zero");
            output.getChildren().add(row(production.getOutputBlanks(), blank));
        }

        this.setPadding(new Insets(padding));
    }

    /**
     * Draws the production's excluded resources
     *
     * @param exclusions
     * @param blanksExclusions
     */
    private void fillExclusions(VBox exclusions, List<String> blanksExclusions) {
        exclusions.setSpacing(2);
        for (String resource : blanksExclusions) {
            StackPane exclusion = new StackPane();
            exclusion.getChildren().add(new Resource(resource));
            exclusion.getChildren().add(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gui/images/resourcetypes/blankexclusion.png")))));

            Group group = new Group(exclusion);
            group.setScaleX(0.3);
            group.setScaleY(0.3);
            exclusions.getChildren().add(group);
        }
    }

    /**
     * @param resourceMap
     * @return
     */
    private List<HBox> buildResourceLines(Map<String, Integer> resourceMap) {
        return resourceMap.entrySet().stream().map(e -> {
            Resource r = new Resource(e.getKey());

            return row(e.getValue(), r);
        }).toList();
    }

    /**
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

        return box;
    }

    /**
     * Getter of the production ID.
     *
     * @return the production ID
     */
    public int getProductionId() {
        return productionId;
    }

    /**
     * Adds the buttons to select the productions to be activated.
     */
    public void addProduceButton() {
        AnchorPane pane = new AnchorPane();
        SButton activate = new SButton("Produce");
        activate.setOnAction(event -> onProduce.accept(this, this));
        activate.setScaleX(1.2);
        activate.setScaleY(1.2);
        pane.getChildren().add(activate);
        AnchorPane.setLeftAnchor(activate, 25d);
        AnchorPane.setBottomAnchor(activate, -50d);

        this.getChildren().add(pane);
    }
}
