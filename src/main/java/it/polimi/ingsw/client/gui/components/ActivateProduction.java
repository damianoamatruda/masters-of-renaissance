package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.vcevents.ReqActivateProduction;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivateProduction extends StackPane {
    private NumberBinding maxScale;

    @FXML private StackPane backStackPane;
    @FXML private SButton back;
    @FXML private SButton next;
    @FXML private SButton submit;
    @FXML private Warehouse guiShelves;
    @FXML private HBox choosableInputResources;
    @FXML private HBox choosableOutputResources;
    @FXML private Text text;
    @FXML private Production productionRecipe;

    private List<ReducedProductionRequest> requests;
    private List<ReducedResourceContainer> tempShelves;
    private List<ReducedResourceContainer> newTempShelves;
    private List<Integer> toActivate;
    private int index;
    private Map<String, Integer> inputBlanks = new HashMap<>();
    private Map<String, Integer> outputBlanks = new HashMap<>();
    private Map<Integer, Map<String, Integer>> containers = new HashMap<>();

    public ActivateProduction(List<Integer> toActivate, int index,
                              List<ReducedProductionRequest> requests, List<ReducedResourceContainer> tempShelves,
                              NumberBinding sizeBinding) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/activateproduction.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.toActivate = toActivate;
        this.index = index;
        this.tempShelves = tempShelves;
        this.newTempShelves = new ArrayList<>(tempShelves); // this does need to be a deep copy
        this.requests = requests;
        this.maxScale = sizeBinding;

//        this.scaleXProperty().bind(maxScale);
//        this.scaleYProperty().bind(maxScale);

        text.setText(String.format("Production: %s", toActivate.get(index)));

        Gui.getInstance().getViewModel().getResourceTypes().stream().filter(ReducedResourceType::isStorable).forEach(r -> {
            HBox entry = new HBox();
            Spinner<Integer> spinner = new Spinner<>(0, 1000, 0);
            spinner.setMaxWidth(50);
            spinner.editorProperty().get().setAlignment(Pos.CENTER);
            entry.getChildren().add(spinner);
            Resource resource = new Resource();
            resource.setResourceType(r.getName());
            resource.setScaleX(0.8);
            resource.setScaleY(0.8);
            entry.getChildren().add(resource);
            choosableInputResources.getChildren().add(entry);
        });

        Gui.getInstance().getViewModel().getResourceTypes().stream().filter(ReducedResourceType::isStorable).forEach(r -> {
            HBox entry = new HBox();
            Spinner<Integer> spinner = new Spinner<>(0, 1000, 0);
            spinner.setMaxWidth(50);
            spinner.editorProperty().get().setAlignment(Pos.CENTER);
            entry.getChildren().add(spinner);
            Resource resource = new Resource();
            resource.setResourceType(r.getName());
            resource.setScaleX(0.8);
            resource.setScaleY(0.8);
            entry.getChildren().add(resource);
            choosableOutputResources.getChildren().add(entry);
        });

        guiShelves.setWarehouseShelves(tempShelves, (a, b) -> {});
        guiShelves.addResourcesSelector(this.containers, newTempShelves);

        back.setOnAction(e -> handleBack());
        if(index == toActivate.size() - 1) next.setDisable(true);
        else next.setOnAction(e -> handleNext());
        if(index < toActivate.size() - 1) submit.setDisable(true);
        else submit.setOnAction(e -> handleSubmit());

        Gui.getInstance().getViewModel().getProduction(toActivate.get(index)).ifPresent(p -> productionRecipe.setProduction(p));
    }

    private void handleSubmit() {
        buildRequest();

        Gui.getInstance().getUi().dispatch(new ReqActivateProduction(requests));
    }

    private void handleNext() {
        buildRequest();

        // go to next production
        backStackPane.getChildren().add(new ActivateProduction(toActivate, index + 1,
                     requests, newTempShelves, maxScale));
    }

    private void buildRequest() {
        // get chosen blanks
        choosableInputResources.getChildren().forEach(hbox ->
                inputBlanks.put(((Resource) ((HBox) hbox).getChildren().get(1)).getName(),
                        ((Spinner<Integer>) ((HBox) hbox).getChildren().get(0)).getValue()));

        choosableOutputResources.getChildren().forEach(hbox ->
                outputBlanks.put(((Resource) ((HBox) hbox).getChildren().get(1)).getName(),
                        ((Spinner<Integer>) ((HBox) hbox).getChildren().get(0)).getValue()));

        // add production request
        requests.add(new ReducedProductionRequest(toActivate.get(index), inputBlanks, outputBlanks, containers));
    }

    private void handleBack() {
        if(index > 0)
            requests.remove(index - 1);
        ((Pane) this.getParent()).getChildren().remove(this);
    }

}
