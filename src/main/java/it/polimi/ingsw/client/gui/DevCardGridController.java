package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.client.gui.Gui.setPauseHandlers;

public class DevCardGridController extends GuiController {
    private static final Logger LOGGER = Logger.getLogger(DevCardGridController.class.getName());
    
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @FXML
    private StackPane backStackPane;
    @FXML
    private AnchorPane canvas;
    @FXML
    private StackPane devCardGridPane;
    @FXML
    private HBox containersBox;
    @FXML
    private HBox devSlotsBox;
    @FXML
    private ChoiceBox<Integer> devSlotChoicePicker;
    @FXML
    private Button submitBtn;
    @FXML private Button back;

    private NumberBinding maxScale;

    private ViewModel vm;
    private DevCardGrid devCardGrid;
    private List<DevSlot> devSlots;
    private Warehouse warehouse;
    private Strongbox strongbox;

    private int selectedLevel;
    private String selectedColor;
    private DevelopmentCard selectedCard;

    private Map<Integer, Map<String, Integer>> shelvesMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);
        
        backStackPane.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        canvas.setBorder(new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        
        Gui gui = Gui.getInstance();
        vm = gui.getViewModel();

        devCardGrid = new DevCardGrid();
        devCardGrid.setGrid(vm.getDevCardGrid().orElseThrow());
        devCardGrid.setControllerListener(this::devCardPressed);

        devCardGrid.setScaleX(0.75);
        devCardGrid.setScaleY(0.75);

        devCardGridPane.getChildren().add(devCardGrid);

        devSlotChoicePicker.getItems().addAll(List.of(0, 1, 2));

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnAction(this::back);

        resetWarehouse();
        resetStrongbox();
        resetSlots();

        this.canvas.setOnDragOver((event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasImage()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        this.canvas.setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            try {
                int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                String resource = ((Resource) event.getGestureSource()).getName();

                putChoice(resource, id);

                strongbox.getContent().ifPresentOrElse(
                        c -> strongbox.refreshRemove(resource),
                        () -> warehouse.refreshShelfRemove(id));

                success = true;
            } catch (NumberFormatException e) { // TODO: Handle this, don't simply log it
                LOGGER.log(Level.SEVERE, "NumberFormatException (TODO: Handle this)", e);
            }

            event.setDropCompleted(success);
            event.consume();
        });

        setPauseHandlers(backStackPane, canvas, maxScale);
    }

    private boolean putChoice(String resource, int shelfID) {
        boolean success = false;
        try {
            int amount = shelvesMap.get(shelfID).get(resource) + 1;
            shelvesMap.get(shelfID).put(resource, amount);
            success = true;
        } catch (NullPointerException e) {
            if(shelvesMap.get(shelfID) == null || shelvesMap.get(shelfID).keySet().isEmpty()) {
                Map<String, Integer> entry = new HashMap<>();
                entry.put(resource, 1);
                shelvesMap.put(shelfID, entry);
                success = true;
            }
        }
        return success;
    }

    private void devCardPressed(ReducedDevCard card, DevelopmentCard guicard) {
        if (guicard == selectedCard) {
            selectedCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
            selectedCard = null;
            selectedColor = "";
            selectedLevel = 0;
        }
        else {
            if (selectedCard != null)
                selectedCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
            guicard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
            selectedCard = guicard;
            selectedColor = card.getColor();
            selectedLevel = card.getLevel();
        }

        shelvesMap = new HashMap<>();
        resetWarehouse();
        resetStrongbox();
        resetSlots();
    }

    private void resetWarehouse() {
        warehouse = new Warehouse();

        warehouse.setPrefHeight(245);
        warehouse.setPrefWidth(301);

        List<ReducedResourceContainer> whShelves = vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname());

        warehouse.setWarehouseShelves(whShelves, (s1, s2) -> {
                warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().getUi().dispatch(new ReqSwapShelves(s1, s2));
            }, true);

        if (containersBox.getChildren().size() >= 1)
            containersBox.getChildren().remove(0);
        containersBox.getChildren().add(0, warehouse);
        // warehouse.setScaleX(0.62);
        // warehouse.setScaleY(0.62);

//        warehouse.enableSwapper();
    }

    private void resetStrongbox() {
        strongbox = new Strongbox();

        strongbox.setPrefHeight(245);
        strongbox.setPrefWidth(291);

        strongbox.setContent(vm.getPlayerStrongbox(vm.getLocalPlayerNickname()).orElseThrow());

        if (containersBox.getChildren().size() == 2)
            containersBox.getChildren().remove(1);
        containersBox.getChildren().add(strongbox);

        // strongbox.setScaleX(0.71);
        // strongbox.setScaleY(0.71);
    }

    private void resetSlots() {
        devSlots = new ArrayList<>();
        List<List<ReducedDevCard>> modelSlots = vm.getPlayerDevelopmentCards(vm.getCurrentPlayer());
        
        modelSlots.forEach(modelSlot -> {
            DevSlot slot = new DevSlot();

            List<DevelopmentCard> cards = modelSlot.stream()
                    .map(c -> new DevelopmentCard(c)).toList();
            slot.setDevCards(cards);

            devSlots.add(slot);
        });
        
        for(int i = 0; i < vm.getSlotsCount() - modelSlots.size(); i++)
            devSlots.add(new DevSlot());

        devSlotsBox.getChildren().clear();
        devSlotsBox.getChildren().addAll(devSlots);
    }

    private void submitPressed(ActionEvent actionEvent) {
        Gui.getInstance().getUi().dispatch(new ReqBuyDevCard(selectedColor, selectedLevel, devSlotChoicePicker.getValue(), shelvesMap));
    }

    private void back(ActionEvent actionEvent) {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        if(event.getAction() == UpdateAction.ActionType.BUY_DEVELOPMENT_CARD)
            gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));

        else if(event.getAction() == UpdateAction.ActionType.SWAP_SHELVES) {
            Shelf s1 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap1()).findAny().orElseThrow();
            Shelf s2 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap2()).findAny().orElseThrow();

            warehouse.swapShelves(s1, s2);
        }
    }
}
