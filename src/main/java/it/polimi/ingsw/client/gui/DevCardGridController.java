package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.*;

public class DevCardGridController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @FXML private AnchorPane canvas;
    @FXML private StackPane devCardGridPane;
    @FXML private HBox containersBox;
    @FXML private HBox devSlotsBox;
    @FXML private ChoiceBox<Integer> devSlotChoicePicker;
    @FXML private Button submitBtn;
    @FXML private Button back;

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
        super.initialize(url, resourceBundle);

        /*Image backBGImage = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/gui/background.png")).toExternalForm());
        
        BackgroundImage backBG = new BackgroundImage(backBGImage,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, true));
        Background bg = new Background(backBG);
        this.canvas.setBackground(bg);*/
    
        Gui gui = Gui.getInstance();
        vm = gui.getViewModel();

        devCardGrid = new DevCardGrid();
        devCardGrid.setGrid(vm.getDevCardGrid());
        devCardGrid.setControllerListener(this::devCardPressed);
        
        // devCardGrid.setBorder(new Border(new BorderStroke(Color.GREEN,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // devCardGridPane.setBorder(new Border(new BorderStroke(Color.RED,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // containersBox.setBorder(new Border(new BorderStroke(Color.RED,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // doesn't actually do anything about the layoutBounds issue
        Group g = new Group(devCardGrid);
        g.setScaleX(0.9);
        g.setScaleY(0.9);

        devCardGridPane.getChildren().add(g);

        devSlotChoicePicker.getItems().addAll(List.of(0, 1, 2));

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnMouseClicked(this::back);

        resetWarehouse();
        resetStrongbox();
        resetSlots();

        // warehouse.setBorder(new Border(new BorderStroke(Color.BLUE,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        // strongbox.setBorder(new Border(new BorderStroke(Color.GREEN,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

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

                if (vm.getPlayerData(vm.getLocalPlayerNickname()).getStrongbox() == id)
                    strongbox.refreshRemove(resource);
                else
                    warehouse.refreshShelfRemove(id, resource);

                success = true;
            } catch (NumberFormatException e) {
                //    e.printStackTrace();
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private boolean putChoice(String resource, int shelfID) {
        boolean success = false;
        try {
            int amount = shelvesMap.get(shelfID).get(resource) + 1;
            shelvesMap.get(shelfID).put(resource, amount);
            success = true;
        } catch (NullPointerException e) {
            if(shelvesMap.get(shelfID) == null || shelvesMap.get(shelfID).keySet().size() == 0) {
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

        List<ReducedResourceContainer> whShelves = vm.getPlayerData(vm.getLocalPlayerNickname()).getWarehouseShelves().stream()
            .map(id -> vm.getContainer(id).orElseThrow()).toList();
            
        warehouse.setWarehouseShelves(whShelves);
        
        if (containersBox.getChildren().size() >= 1)
            containersBox.getChildren().remove(0);
        containersBox.getChildren().add(0, warehouse);
        // warehouse.setScaleX(0.62);
        // warehouse.setScaleY(0.62);
    }

    private void resetStrongbox() {
        strongbox = new Strongbox();
        
        strongbox.setPrefHeight(245);
        strongbox.setPrefWidth(291);

        strongbox.setContent(vm.getContainer(vm.getPlayerData(vm.getLocalPlayerNickname()).getStrongbox()).orElseThrow());

        if (containersBox.getChildren().size() == 2)
            containersBox.getChildren().remove(1);
        containersBox.getChildren().add(strongbox);
        
        // strongbox.setScaleX(0.71);
        // strongbox.setScaleY(0.71);
    }

    private void resetSlots() {
        List<ReducedDevCard> devCards = vm.getPlayerDevelopmentCards(vm.getLocalPlayerNickname());

        devSlots = new ArrayList<>();

        List<DevelopmentCard> guicards = devCards.stream().map(c -> {
            DevelopmentCard guicard = new DevelopmentCard(c.getColor());
            guicard.setRequirement(c.getCost());
            guicard.setProduction(vm.getProduction(c.getProduction()).orElseThrow());
            guicard.setVictoryPoints(c.getVictoryPoints()+"");
            return guicard;
        }).toList();

        guicards.forEach(gc -> {
            DevSlot slot = new DevSlot();

            slot.setDevCards(List.of(gc));

            devSlots.add(slot);
        });

        devSlotsBox.getChildren().clear();
        devSlotsBox.getChildren().addAll(devSlots);
    }

    private void submitPressed(ActionEvent e) {
        Gui.getInstance().dispatch(new ReqBuyDevCard(selectedColor, selectedLevel, devSlotChoicePicker.getValue(), shelvesMap));
    }

    private void back(MouseEvent mouseEvent) {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
    }

    @Override
    public void on(Gui gui, UpdateAction event) {
        super.on(gui, event);
        gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));
    }
}
