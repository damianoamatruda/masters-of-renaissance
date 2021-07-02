package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Logger;

/** Gui controller used for the resources setup scene. */
public class SetupResourcesController extends GuiController {
    private static final Logger LOGGER = Logger.getLogger(SetupResourcesController.class.getName());

    private final Map<Integer, Map<String, Integer>> selection = new HashMap<>();
    private final int choosableCount = vm.getLocalPlayer().flatMap(vm::getPlayer).orElseThrow().getSetup().getInitialResources();
    private List<ReducedResourceType> choosableResources;
    @FXML
    private BorderPane canvas;
    @FXML
    private HBox resourceTypesContainer;
    @FXML
    private Warehouse warehouse;
    @FXML
    private Button choiceButton;
    @FXML
    private Title titleComponent;
    @FXML
    private Text waitingText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);

        if (isLocalResourceSetupDone()) {
            showWaitingText();
            return;
        }

        titleComponent.setText(String.format("Choose %d resources",
                vm.getLocalPlayer().flatMap(vm::getPlayer).orElseThrow().getSetup().getInitialResources()));

        resourceTypesContainer.setSpacing(40);
        resourceTypesContainer.setAlignment(Pos.CENTER);

        choosableResources = vm.getResourceTypes().stream().filter(r -> r.isStorable() && !r.getName().equalsIgnoreCase("Faith")).toList();

        // enable resources from choice box as drag source
        choosableResources.forEach(res -> {
            Resource r = new Resource(res.getName());

            r.setOnDragDetected((event) -> {
                if (selection.values().stream().flatMap(m -> m.values().stream()).reduce(0, Integer::sum) >= choosableCount) {
                    event.consume();
                    return;
                }
                Dragboard db = r.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(r.getImage());
                    db.setContent(content);
                    event.consume();
                }
            );

            resourceTypesContainer.getChildren().add(r);
        });

        warehouse.setWarehouseShelves(vm.getLocalPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow(), (s1, s2) -> {
            warehouse.setWaitingForSwap(s1, s2);
            gui.getUi().dispatch(new ReqSwapShelves(s1, s2));
        });

        // On drag over + dropped for the warehouse shelves
        warehouse.getChildren().forEach(shelf -> shelf.setOnDragOver((event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasImage()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        }));
        warehouse.getChildren().forEach(shelf -> shelf.setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                int id = ((Shelf) shelf).getShelfId();
                String resource = ((Resource) event.getGestureSource()).getName();

                boolean alreadyHasBoundShelf = selection.keySet().stream().anyMatch(sh -> selection.get(sh).containsKey(resource) && sh != id)
                        && !(db.hasString() && (selection.get(Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT))).get(resource) < 2));

                if((selection.get(id) == null || selection.get(id).get(resource) == null ||
                        ((Shelf) shelf).getSize() > selection.get(id).get(resource)) && !alreadyHasBoundShelf)
                    try {
                        int quantity = selection.get(id).get(resource) + 1;
                        selection.get(id).put(resource, quantity);
                        success = true;
                    } catch (NullPointerException e) {
                        if (selection.get(id) == null || selection.get(id).keySet().isEmpty()) {
                            Map<String, Integer> entry = new HashMap<>();
                            entry.put(resource, 1);
                            selection.put(id, entry);
                            success = true;
                        }

                    }
                if (success) warehouse.getShelf(id).addResourceDraggable(resource);
                updateChoiceButton();
            }
            if(db.hasString() && success) {
                int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                String resource = ((Resource) event.getGestureSource()).getName();

                int quantity = selection.get(id).get(resource) - 1;
                if (quantity > 0)
                    selection.get(id).put(resource, quantity);
                else
                    selection.get(id).remove(resource);

                warehouse.refreshShelfRemove(id);
                updateChoiceButton();

            }
            event.setDropCompleted(success);
            event.consume();
        }));

        // On drag over + dropped outside of warehouse
        this.canvas.setOnDragOver((event) -> {
                    Dragboard db = event.getDragboard();
                    if (db.hasImage()) {
                        event.acceptTransferModes(TransferMode.ANY);
                    }
                    event.consume();
                }
        );
        this.canvas.setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            try {
                int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                String resource = ((Resource) event.getGestureSource()).getName();

                int quantity = selection.get(id).get(resource) - 1;
                if (quantity > 0)
                    selection.get(id).put(resource, quantity);
                else
                    selection.get(id).remove(resource);

                warehouse.refreshShelfRemove(id);
                updateChoiceButton();

                success = true;
            } catch (NumberFormatException e) {
                    // It is normal if it gets here:
                // it means that the resource hasn't been dropped inside of any shelf, but outside instead
                // Drop is simply ignored
            }

            event.setDropCompleted(success);
            event.consume();
        });

        updateChoiceButton();

        gui.setPauseHandler(canvas);
    }

    /**
     * Refresh of the Choose button, disabling it if the count of chosen resources does not game.
     */
    private void updateChoiceButton() {
        int count = selection.values().stream().flatMap(m -> m.values().stream()).reduce(0, Integer::sum);
        choiceButton.setDisable(count != choosableCount);
    }

    /**
     * Dispatches a request of resources choice to the backend.
     */
    public void handleChoice() {
        gui.getUi().dispatch(new ReqChooseResources(selection));
        if (!gui.getUi().isOffline() && vm.getPlayers().size() > 1)
            showWaitingText();
    }

    private void showWaitingText() {
        waitingText.setVisible(true);
        ((VBox) resourceTypesContainer.getParent()).getChildren().remove(resourceTypesContainer);
        ((VBox) warehouse.getParent()).getChildren().remove(warehouse);
        ((VBox) choiceButton.getParent()).getChildren().remove(choiceButton);
    }

    @Override
    public void on(ErrAction event) {
        /* If the data in the VM is correct setNextState() could be used here as well.
           This different handler, which keeps track of the current player only,
           forces the client in a state that's compatible with the server's response,
           accepting it as a universal source of truth. */
        Consumer<? extends GuiController> callback = controller ->
                gui.addToOverlay(
                        new Alert("Setup already concluded", "You have already concluded setup, advancing to game turns."));

        if (vm.localPlayerIsCurrent())
            gui.setScene(getClass().getResource("/assets/gui/turnbeforeaction.fxml"), callback);
        else
            gui.setScene(getClass().getResource("/assets/gui/waitingforturn.fxml"), callback);
    }

    @Override
    public void on(ErrInitialChoice event) {
        if (event.isLeadersChoice()) // if the error is from the initial leaders choice
            if (event.getMissingLeadersCount() == 0) // no leaders missing -> already chosen
                Platform.runLater(() -> gui.addToOverlay(new Alert(
                        "You cannot choose the leader cards",
                        "You have already chosen the initial leader cards.",
                        this::setNextState
                )));
            else
                gui.reloadScene("You cannot buy the development card",
                        String.format("You have not chosen enough leader cards: %d missing.", event.getMissingLeadersCount()));
        else
            Platform.runLater(() -> gui.addToOverlay(new Alert(
                    "You cannot choose the leader cards",
                    "You have already chosen the initial resources.",
                    this::setNextState
            )));
    }

    @Override
    public void on(UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_RESOURCES && vm.getLocalPlayer().isPresent() && event.getPlayer().equals(vm.getLocalPlayer().get()))
            throw new RuntimeException("Resources setup: UpdateAction received with action type not CHOOSE_RESOURCES.");

        if (vm.getLocalPlayer().isPresent() && event.getPlayer().equals(vm.getLocalPlayer().get()))
            titleComponent.setText("Resource setup done.");
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        setNextState();
    }
}
