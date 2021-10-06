package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.vcevents.ReqActivateProductions;
import it.polimi.ingsw.common.reducedmodel.*;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pane component used to choose input source for a selected production.
 */
public class ActivateProductions extends StackPane {
    private final List<ActivateProductions> windows;
    private final List<ReducedProductionRequest> requests;
    private final List<ReducedResourceContainer> tempShelves;
    private final List<ReducedResourceContainer> tempDepots;
    private final List<Integer> toActivate;
    private final int index;
    private final Map<String, Integer> inputBlanks = new HashMap<>();
    private final Map<String, Integer> outputBlanks = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> containers = new HashMap<>();
    @FXML
    private BorderPane main;
    @FXML
    private SButton back;
    @FXML
    private SButton next;
    @FXML
    private SButton submit;
    @FXML
    private Warehouse guiShelves;
    @FXML
    private HBox choosableInputResources;
    @FXML
    private HBox choosableOutputResources;
    @FXML
    private Production productionRecipe;
    @FXML
    private Strongbox strongbox;
    @FXML
    private HBox leadersBox;

    /**
     * Class constructor.
     *
     * @param requests    the already completed production requests
     * @param tempShelves the temporary shelves, containing the remaining payable resources for the remaining
     *                    productions
     * @param tempDepots
     * @param toActivate  the production involved
     * @param index       an index used to move between the selected productions in the list
     */
    public ActivateProductions(List<ActivateProductions> previousWindows, List<ReducedProductionRequest> requests, List<ReducedResourceContainer> tempShelves,
                               List<ReducedResourceContainer> tempDepots, List<Integer> toActivate, int index) {
        windows = new ArrayList<>(previousWindows);
        windows.add(this);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/activateproductions.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        Gui gui = Gui.getInstance();
        ViewModel vm = gui.getViewModel();

        this.tempShelves = new ArrayList<>(); // This does need to be a deep copy
        for (ReducedResourceContainer container : tempShelves)
            this.tempShelves.add(new ReducedResourceContainer(container.getId(), container.getSize(), container.getContent(), container.getBoundedResType().orElse(null)));
        this.tempDepots = new ArrayList<>(); // This as well
        for (ReducedResourceContainer container : tempDepots)
            this.tempDepots.add(new ReducedResourceContainer(container.getId(), container.getSize(), container.getContent(), container.getBoundedResType().orElse(null)));

        this.requests = requests;
        this.toActivate = toActivate;
        this.index = index;

        gui.setSceneScaling(main);

        ReducedResourceTransactionRecipe selectedProd = vm.getProduction(toActivate.get(index)).orElseThrow();

        /* Add spinners to choose quantity */
        List<ReducedResourceType> inputResTypes = vm.getProductionInputNonStorableResTypes(selectedProd);
        List<ReducedResourceType> outputResTypes = vm.getProductionOutputResTypes(selectedProd);

        inputResTypes.forEach(r ->
                addSpinner(choosableInputResources, r, selectedProd.getInputBlanks()));

        outputResTypes.forEach(r ->
                addSpinner(choosableOutputResources, r, selectedProd.getOutputBlanks()));

        /* Remove HBox containers if no extra choosable resource available */
        if (selectedProd.getInputBlanks() == 0 || inputResTypes.isEmpty())
            ((Pane) choosableInputResources.getParent().getParent()).getChildren().remove(0);
        if (selectedProd.getOutputBlanks() == 0 || outputResTypes.isEmpty())
            ((Pane) choosableOutputResources.getParent().getParent()).getChildren().remove(0);

        /* Set shelves with click handlers */
        guiShelves.setWarehouseShelves(tempShelves, (a, b) -> {
        });
        guiShelves.addResourcesSelector(this.containers, this.tempShelves);

        /* Back Button */
        back.setOnAction(e -> handleBack());

        /* Next Button */
        next.setOnAction(e -> handleNext());
        if (index == toActivate.size() - 1)
            next.setDisable(true);

        /* Submit Button */
        submit.setOnAction(e -> handleSubmit());
        if (index < toActivate.size() - 1)
            submit.setDisable(true);

        /* Production */
        vm.getProduction(toActivate.get(index)).ifPresent(p -> productionRecipe.setProduction(p));

        /* Strongbox */
        vm.getCurrentPlayer().flatMap(vm::getPlayerStrongbox).ifPresent(c -> {
            strongbox.setContent(c);
            strongbox.addSpinners();
        });

        /* Leaders */
        leadersBox.getChildren().addAll(vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow().stream()
                .filter(ReducedLeaderCard::isActive)
                .filter(c -> c.getLeaderType() == LeaderType.DEPOT)
                .map(reducedLeader -> {
                    LeaderCard leaderCard = new LeaderCard(reducedLeader);

                    leaderCard.setDepotContent(
                            tempDepots.stream()
                                    .filter(d -> d.getId() == reducedLeader.getContainerId()).findAny().orElseThrow(),
                            reducedLeader.getResourceType(), true);

                    leaderCard.getGuiDepot().addResourcesSelector(this.containers,
                            this.tempDepots.stream().filter(d -> d.getId() == reducedLeader.getContainerId()).findAny().orElseThrow());

                    return leaderCard;
                }).toList());
    }

    /**
     * Finished building the last production request, and dispatches all the requests to the backend
     */
    private void handleSubmit() {
        buildRequest();
        Gui.getInstance().getUi().dispatch(new ReqActivateProductions(requests));
        ((Pane) this.getParent()).getChildren().removeAll(windows);
    }

    /**
     * Move to the next production input choice
     */
    private void handleNext() {
        buildRequest();
        Gui.getInstance().getRoot().getChildren().add(
                new ActivateProductions(windows, requests, tempShelves, tempDepots, toActivate, index + 1));
    }

    /**
     * Builds a new production request and adds it to the others that already have been formed.
     */
    private void buildRequest() {
        /* Get chosen blanks */
        choosableInputResources.getChildren().forEach(hbox -> {
            if (((Spinner<Integer>) ((HBox) hbox).getChildren().get(0)).getValue() > 0)
                inputBlanks.put(((Resource) ((HBox) hbox).getChildren().get(1)).getName(),
                        ((Spinner<Integer>) ((HBox) hbox).getChildren().get(0)).getValue());
        });

        choosableOutputResources.getChildren().forEach(hbox -> {
            if (((Spinner<Integer>) ((HBox) hbox).getChildren().get(0)).getValue() > 0)
                outputBlanks.put(((Resource) ((HBox) hbox).getChildren().get(1)).getName(),
                        ((Spinner<Integer>) ((HBox) hbox).getChildren().get(0)).getValue());
        });

        /* Get input from strongbox */
        strongbox.fillContainersMap(containers);

        /* Add production request */
        requests.add(new ReducedProductionRequest(toActivate.get(index), containers, inputBlanks, outputBlanks));
    }

    /**
     * Handles going back to previous production, or back to turn scene.
     */
    private void handleBack() {
        if (index > 0)
            requests.remove(index - 1);
        ((Pane) this.getParent()).getChildren().remove(this);
    }

    public void addSpinner(HBox container, ReducedResourceType r, int maxValue) {
        HBox entry = new HBox();
        entry.setAlignment(Pos.CENTER);
        Spinner<Integer> spinner = new Spinner<>(0, maxValue, 0);
        spinner.setMaxWidth(50);
        spinner.editorProperty().get().setAlignment(Pos.CENTER);
        entry.getChildren().add(spinner);
        Resource resource = new Resource(r.getName());
        resource.setScaleX(0.6);
        resource.setScaleY(0.6);
        entry.getChildren().add(resource);
        container.getChildren().add(entry);
    }
}
