package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Resource;
import it.polimi.ingsw.client.gui.components.Shelf;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.client.gui.components.Warehouse;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Gui controller used for the resources setup scene. */
public class SetupResourcesController extends GuiController {
    private static final Logger LOGGER = Logger.getLogger(SetupResourcesController.class.getName());

    private final Map<Integer, Map<String, Integer>> selection = new HashMap<>();
    private List<ReducedResourceType> choosableResources;
    @FXML
    private StackPane backStackPane;
    @FXML
    private BorderPane bpane;
    @FXML
    private HBox resourceTypesContainer;
    @FXML
    private Warehouse warehouse;
    @FXML
    private Button choiceButton;
    @FXML
    private Title titleComponent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NumberBinding maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);

        backStackPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        bpane.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Gui gui = Gui.getInstance();

        titleComponent.setText(String.format("Choose %d resources.",
                gui.getViewModel().getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getInitialResources()));

        resourceTypesContainer.setSpacing(40);
        resourceTypesContainer.setAlignment(Pos.CENTER);

        choosableResources = vm.getResourceTypes().stream().filter(r -> r.isStorable() && !r.getName().equalsIgnoreCase("Faith")).toList();

        // enable resources from choice box as drag source
        choosableResources.forEach(res -> {
            Resource r = new Resource();
            r.setResourceType(res.getName());

            r.setOnDragDetected((event) -> {
                    Dragboard db = r.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(r.getImage());
                    db.setContent(content);
                    event.consume();
                }
            );

            resourceTypesContainer.getChildren().add(r);
        });

        warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getLocalPlayerNickname()), (s1, s2) -> { warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().getUi().dispatch(new ReqSwapShelves(s1, s2)); });

        // On drag over + dropped for the warehouse shelves
        warehouse.getChildren().forEach(shelf -> shelf.setOnDragOver((event) -> {
                    Dragboard db = event.getDragboard();
                    if (db.hasImage()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                    event.consume();
                }
        ));
        warehouse.getChildren().forEach(shelf -> shelf.setOnDragDropped((event) -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasImage()) {
                        try {
                            int id = ((Shelf) shelf).getShelfId();
                            String resource = ((Resource) event.getGestureSource()).getName();

                            boolean alreadyHasBoundShelf = selection.keySet().stream().anyMatch(sh -> selection.get(sh).containsKey(resource) && sh != id)
                                    && !(db.hasString() && (selection.get(Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT))).get(resource) < 2));

                            if((selection.get(id) == null || selection.get(id).get(resource) == null ||
                                    ((Shelf) shelf).getSize() > selection.get(id).get(resource)) && !alreadyHasBoundShelf)
                                try {
                                    int amount = selection.get(id).get(resource) + 1;
                                    selection.get(id).put(resource, amount);
                                    success = true;
                                } catch (NullPointerException e) {
                                    if(selection.get(id) == null || selection.get(id).keySet().isEmpty()) {
                                        Map<String, Integer> entry = new HashMap<>();
                                        entry.put(resource, 1);
                                        selection.put(id, entry);
                                        success = true;
                                    }

                                }
                            if(success) warehouse.addResourceDraggable(id, resource);
                            updateChoiceButton();

                        } catch (Exception e) { // TODO remove this catch once debugged
                            LOGGER.log(Level.SEVERE, "Unknown exception (TODO: Remove this)", e);
                        }
                    }
                    if(db.hasString() && success) {
                        int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                        String resource = ((Resource) event.getGestureSource()).getName();

                        int amount = selection.get(id).get(resource) - 1;
                        if(amount > 0)
                            selection.get(id).put(resource, amount);
                        else
                            selection.get(id).remove(resource);

                        warehouse.refreshShelfRemove(id);
                        updateChoiceButton();

                    }
                    event.setDropCompleted(success);
                    event.consume();
                }
        ));

        // On drag over + dropped outside of warehouse
        this.bpane.setOnDragOver((event) -> {
                Dragboard db = event.getDragboard();
                if (db.hasImage()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        );
        this.bpane.setOnDragDropped((event) -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                try {
                    int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                    String resource = ((Resource) event.getGestureSource()).getName();

                    int amount = selection.get(id).get(resource) - 1;
                    if (amount > 0)
                        selection.get(id).put(resource, amount);
                    else
                        selection.get(id).remove(resource);

                    warehouse.refreshShelfRemove(id);
                    updateChoiceButton();

                    success = true;
                } catch (NumberFormatException e) { // TODO: Handle this, don't simply log it
                    // Reply to To-Do: not much to handle, it is normal if it gets here:
                    // it means that the resource hasn't been dropped inside of any shelf, but outside instead
                    LOGGER.log(Level.SEVERE, "NumberFormatException (TODO: Handle this)", e);
                }

                event.setDropCompleted(success);
                event.consume();
            }
        );

        updateChoiceButton();

    }

    /**
     * Refresh of the Choose button, disabling it if the count of chosen resources does not match.
     */
    private void updateChoiceButton() {
        int count = selection.keySet().stream()
                .mapToInt(k -> selection.get(k).keySet().stream().mapToInt(h -> selection.get(k).get(h)).sum()).sum();
        choiceButton.setDisable(count != Gui.getInstance().getViewModel()
                .getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getInitialResources());
    }

    /**
     * Dispatches a request of resources choice to the backend.
     */
    public void handleChoice() {
        Gui.getInstance().getUi().dispatch(new ReqChooseResources(selection));
    }

    @Override
    public void on(UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_RESOURCES && event.getPlayer().equals(vm.getLocalPlayerNickname()))
            throw new RuntimeException("Resources setup: UpdateAction received with action type not CHOOSE_RESOURCES.");
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        setNextSetupState();
    }
}
