package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.vcevents.ReqActivateProduction;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pane component used to choose input source for a selected production. */
public class ActivateProduction extends StackPane {
    private final NumberBinding maxScale;

    @FXML private StackPane backStackPane;
    @FXML private SButton back;
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
    private Text text;
    @FXML
    private Production productionRecipe;
    @FXML
    private Strongbox strongbox;
    @FXML
    private HBox leadersBox;

    private final List<ReducedProductionRequest> requests;
    private final List<ReducedResourceContainer> tempShelves;
    private final List<ReducedResourceContainer> newTempShelves;
    private final List<ReducedResourceContainer> newTempDepots;
    private final List<Integer> toActivate;
    private final int index;
    private final Map<String, Integer> inputBlanks = new HashMap<>();
    private final Map<String, Integer> outputBlanks = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> containers = new HashMap<>();

    /**
     * Class constructor.
     *
     * @param toActivate    the production involved
     * @param index         an index used to move between the selected productions in the list
     * @param requests      the already completed production requests
     * @param tempShelves   the temporary shelves, containing the remaining payable resources for the remaining productions
     * @param tempDepots
     * @param sizeBinding
     */
    public ActivateProduction(List<Integer> toActivate, int index,
                              List<ReducedProductionRequest> requests, List<ReducedResourceContainer> tempShelves,
                              List<ReducedResourceContainer> tempDepots, NumberBinding sizeBinding) {
        Gui gui = Gui.getInstance();
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
        this.newTempDepots = new ArrayList<>(tempDepots); // this as well
        this.requests = requests;
        this.maxScale = sizeBinding;

        this.scaleXProperty().bind(maxScale);
        this.scaleYProperty().bind(maxScale);

        text.setText(String.format("Production: %s", toActivate.get(index)));

        // TODO: Maybe use ifPresent()
        ReducedResourceTransactionRecipe selectedProd = Gui.getInstance().getViewModel().getProduction(toActivate.get(index)).orElseThrow();

        gui.getViewModel().getResourceTypes().stream()
                .filter(r -> (r.isStorable() || r.isTakeableFromPlayer()) &&
                        !selectedProd.getInputBlanksExclusions().contains(r.getName()))
                .forEach(r -> {
                    HBox entry = new HBox();
                    Spinner<Integer> spinner = new Spinner<>(0, 1000, 0);
                    spinner.setMaxWidth(50);
                    spinner.editorProperty().get().setAlignment(Pos.CENTER);
                    entry.getChildren().add(spinner);
                    Resource resource = new Resource(r.getName());
                    resource.setScaleX(0.8);
                    resource.setScaleY(0.8);
                    entry.getChildren().add(resource);
                    choosableInputResources.getChildren().add(entry);
                });

        gui.getViewModel().getResourceTypes().stream()
                .filter(r -> (r.isStorable() || r.isGiveableToPlayer()) &&
                        !selectedProd.getOutputBlanksExclusions().contains(r.getName()))
                .forEach(r -> {
                    HBox entry = new HBox();
                    Spinner<Integer> spinner = new Spinner<>(0, 1000, 0);
                    spinner.setMaxWidth(50);
                    spinner.editorProperty().get().setAlignment(Pos.CENTER);
                    entry.getChildren().add(spinner);
                    Resource resource = new Resource(r.getName());
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

        //strongbox
        gui.getViewModel().getPlayerStrongbox(gui.getViewModel().getCurrentPlayer()).ifPresent(sb -> strongbox.setContent(sb));

        //leaders
        List<LeaderCard> leaders = gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getLocalPlayerNickname()).stream()
                .filter(c -> c.isActive() &&
                        (c.getLeaderType().equals("DepotLeader")))
                .map(reducedLeader -> {
                    LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType(), reducedLeader.getResourceType());
                    leaderCard.setLeaderId(reducedLeader.getId());
                    leaderCard.setLeaderType(reducedLeader.getLeaderType());
                    leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
                    leaderCard.setResourceType(reducedLeader.getResourceType());
                    if (reducedLeader.getResourceRequirement().isPresent())
                        leaderCard.setRequirement(reducedLeader.getResourceRequirement().get());
                    if (reducedLeader.getDevCardRequirement().isPresent())
                        leaderCard.setRequirement(reducedLeader.getDevCardRequirement().get());

                    leaderCard.setDepotContent(tempDepots.stream()
                                    .filter(d -> d.getId() == reducedLeader.getContainerId()).findAny().orElseThrow(),
                            reducedLeader.getResourceType(), true);

                    leaderCard.getGuiDepot().addResourcesSelector(this.containers,
                            newTempDepots.stream().filter(d -> d.getId() == reducedLeader.getContainerId()).findAny().orElseThrow());

                    return leaderCard;

                }).toList();

        leadersBox.getChildren().addAll(leaders);
        leadersBox.setMaxHeight(251);
    }

    /**
     * Finished building the last production request, and dispatches all the requests to the backend
     */
    private void handleSubmit() {
        buildRequest();

        Gui.getInstance().getUi().dispatch(new ReqActivateProduction(requests));
    }

    /**
     * Move to the next production input choice
     */
    private void handleNext() {
        buildRequest();

        // go to next production
        backStackPane.getChildren().add(new ActivateProduction(toActivate, index + 1,
                     requests, newTempShelves, newTempDepots, maxScale));
    }

    /**
     * Builds a new production request and adds it to the others that already have been formed.
     */
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

    /**
     * Handles going back to previous production, or back to playground scene.
     */
    private void handleBack() {
        if(index > 0)
            requests.remove(index - 1);
        ((Pane) this.getParent()).getChildren().remove(this);
    }
}
