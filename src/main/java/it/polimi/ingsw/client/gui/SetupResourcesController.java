package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Resource;
import it.polimi.ingsw.client.gui.components.Shelf;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.client.gui.components.Warehouse;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SetupResourcesController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private final Map<Integer, Map<String, Integer>> selection = new HashMap<>();
    private List<ReducedResourceType> choosableResources;
    @FXML
    private HBox resourceTypesContainer;
    @FXML
    private Warehouse warehouse;
    @FXML
    private Button choiceButton;
    @FXML
    private Title titleComponent;
    @FXML
    private BorderPane window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Gui gui = Gui.getInstance();
        ViewModel vm = gui.getViewModel();

        titleComponent.setText(String.format("Choose %d resources.", gui.getViewModel().getLocalPlayerData().getSetup().getInitialResources()));

        resourceTypesContainer.setSpacing(40);
        resourceTypesContainer.setAlignment(Pos.CENTER);

        choosableResources = vm.getResourceTypes().stream().filter(r -> r.isStorable() && !r.getName().equalsIgnoreCase("Faith")).toList();

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

        warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getLocalPlayerNickname()), (s1, s2) -> { warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().dispatch(new ReqSwapShelves(s1, s2)); });

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

                            if(selection.get(id) == null || selection.get(id).get(resource) == null || ((Shelf) shelf).getSize() > selection.get(id).get(resource))
                                try {
                                    int amount = selection.get(id).get(resource) + 1;
                                    selection.get(id).put(resource, amount);
                                    success = true;
                                } catch (NullPointerException e) {
                                    if(selection.get(id) == null || selection.get(id).keySet().size() == 0) {
                                        Map<String, Integer> entry = new HashMap<>();
                                        entry.put(resource, 1);
                                        selection.put(id, entry);
                                        success = true;
                                    }

                                }
                            if(success) warehouse.refreshShelfAdd(id, resource);
                            updateChoiceButton();

                        } catch (Exception e) { // TODO remove this catch once debugged
                            e.printStackTrace();
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

                        warehouse.refreshShelfRemove(id, resource);
                        updateChoiceButton();

                    }
                    event.setDropCompleted(success);
                    event.consume();
                }
        ));

        this.window.setOnDragOver((event) -> {
                Dragboard db = event.getDragboard();
                if (db.hasImage()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        );
        this.window.setOnDragDropped((event) -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                try {
                    int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                    String resource = ((Resource) event.getGestureSource()).getName();

                    int amount = selection.get(id).get(resource) - 1;
                    if(amount > 0)
                        selection.get(id).put(resource, amount);
                    else
                        selection.get(id).remove(resource);

                    warehouse.refreshShelfRemove(id, resource);
                    updateChoiceButton();

                    success = true;
                } catch (NumberFormatException e) {
//                    e.printStackTrace();
                }

                event.setDropCompleted(success);
                event.consume();
            }
        );

        updateChoiceButton();

    }

    private void updateChoiceButton() {
        int count = selection.keySet().stream().mapToInt(k -> selection.get(k).keySet().stream().mapToInt(h -> selection.get(k).get(h)).sum()).sum();
        choiceButton.setDisable(count != Gui.getInstance().getViewModel().getLocalPlayerData().getSetup().getInitialResources());
    }

    public void handleChoice() {
        Gui.getInstance().dispatch(new ReqChooseResources(selection));
    }

//    @Override
//    public void on(Gui gui, UpdateAction event) {
//        super.on(gui, event);
//        if (event.getAction() == UpdateAction.ActionType.CHOOSE_RESOURCES
//                && event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
//            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
//    }

    @Override
    public void on(Gui gui, UpdateSetupDone event) {
        super.on(gui, event);

        super.on(gui, event);
        if (gui.getViewModel().getCurrentPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
        else gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));

    }
}
