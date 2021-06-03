package it.polimi.ingsw.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

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
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DevCardGridController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @FXML private AnchorPane canvas;
    @FXML private Pane devCardGridPane;
    @FXML private Pane warehousePane;
    @FXML private Pane strongboxPane;
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

        Image backBGImage = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/gui/background.png")).toExternalForm());
        
        BackgroundImage backBG = new BackgroundImage(backBGImage,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, true));
        Background bg = new Background(backBG);
        this.canvas.setBackground(bg);
    
        Gui gui = Gui.getInstance();
        vm = gui.getViewModel();

        devCardGrid = new DevCardGrid();
        devCardGrid.setGrid(vm.getDevCardGrid());
        devCardGrid.setControllerListener(this::devCardPressed);
        
        devCardGrid.setBorder(new Border(new BorderStroke(Color.GREEN,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        devCardGridPane.setBorder(new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    
        devCardGrid.setScaleX(0.5);
        devCardGrid.setScaleY(0.5);

        devCardGridPane.getChildren().add(new Group(devCardGrid));

        devSlotChoicePicker.getItems().addAll(List.of(0, 1, 2));

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnMouseClicked(this::back);

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

        List<ReducedResourceContainer> whShelves = vm.getPlayerData(vm.getLocalPlayerNickname()).getWarehouseShelves().stream()
            .map(id -> vm.getContainer(id).orElseThrow()).toList();
            
        warehouse.setWarehouseShelves(whShelves);

        warehousePane.getChildren().clear();
        warehousePane.getChildren().add(warehouse);
    }

    private void resetStrongbox() {
        strongbox = new Strongbox();

        strongbox.setContent(vm.getContainer(vm.getPlayerData(vm.getLocalPlayerNickname()).getStrongbox()).orElseThrow());

        strongboxPane.getChildren().clear();
        strongboxPane.getChildren().add(strongbox);
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
        try {
            Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on(Gui gui, UpdateAction event) {
        super.on(gui, event);
        try {
            gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
