package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

/**
 * GUI controller class of the development card purchase action.
 */
public class BuyDevelopmentCardController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @FXML
    private AnchorPane canvas;
    @FXML
    private StackPane devCardGridPane;
    @FXML
    private HBox containersBox;
    @FXML
    private HBox devSlotsBox;
    @FXML
    private VBox rightVBox;
    @FXML
    private ChoiceBox<Integer> devSlotChoicePicker;
    @FXML
    private Button submitBtn;
    @FXML
    private Button back;
    @FXML
    private HBox leadersBox;

    private DevCardGrid devCardGrid;
    private Warehouse warehouse;
    private Strongbox strongbox;

    private int selectedLevel;
    private String selectedColor;
    private DevelopmentCard selectedCard;

    private Map<Integer, Map<String, Integer>> shelvesMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);

        submitBtn.setDisable(true);

        devCardGrid = new DevCardGrid();
        devCardGrid.setGrid(vm.getDevCardGrid().orElseThrow());
        devCardGrid.setControllerListener(this::devCardPressed);

        devCardGridPane.getChildren().add(devCardGrid);

        for (int i = 1; i <= vm.getDevSlotsCount(); i++)
            devSlotChoicePicker.getItems().add(i);

        devSlotChoicePicker.setValue(devSlotChoicePicker.getItems().get(0));

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnAction(this::back);

        Platform.runLater(() -> {
            resetWarehouse();
            resetStrongbox();
            resetSlots();
            resetLeaders();
        });

        gui.setPauseHandler(canvas);
        gui.addPauseButton(canvas);
    }

    /**
     * @param resource
     * @param shelfID
     * @return
     */
    private boolean putChoice(String resource, int shelfID) {
        boolean success = false;
        try {
            int quantity = shelvesMap.get(shelfID).get(resource) + 1;
            shelvesMap.get(shelfID).put(resource, quantity);
            success = true;
        } catch (NullPointerException e) {
            if (shelvesMap.get(shelfID) == null || shelvesMap.get(shelfID).keySet().isEmpty()) {
                Map<String, Integer> entry = new HashMap<>();
                entry.put(resource, 1);
                shelvesMap.put(shelfID, entry);
                success = true;
            }
        }

        return success;
    }

    private void resetLeaders() {
        leadersBox.getChildren().clear();
        List<LeaderCard> leaders = vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow().stream()
                .filter(ReducedLeaderCard::isActive)
                .filter(c -> c.getLeaderType() == LeaderType.DEPOT || c.getLeaderType() == LeaderType.DISCOUNT)
                .map(reducedLeader -> {
                    LeaderCard leaderCard = new LeaderCard(reducedLeader);

                    switch (reducedLeader.getLeaderType()) {
                        case DEPOT -> {
                            leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                                    reducedLeader.getResourceType(), false);

                            leaderCard.getGuiDepot().addResourcesSelector(shelvesMap);
                        }
                        case DISCOUNT -> leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
                    }

                    leaderCard.setScaleX(0.8);
                    leaderCard.setScaleY(0.8);

                    return leaderCard;

                }).toList();

        leadersBox.getChildren().addAll(leaders);

        /* Adjust components sizes and positioning if leadersBox has cards */
        if (!leaders.isEmpty()) {
            leadersBox.setMinHeight(180);
            leadersBox.setMaxHeight(180);
            devSlotsBox.setScaleX(0.6);
            devSlotsBox.setScaleY(0.6);
            containersBox.setScaleX(0.7);
            containersBox.setScaleY(0.7);
        }
    }

    /**
     * @param resource
     * @param shelfID
     * @return
     */
    private void removeChoice(String resource, int shelfID) {
        int quantity = shelvesMap.get(shelfID).get(resource) - 1;
        shelvesMap.get(shelfID).put(resource, quantity);
        if (shelvesMap.get(shelfID).get(resource) == 0)
            shelvesMap.get(shelfID).remove(resource);
    }

    /**
     * Handles click on a development card.
     *
     * @param card    the cached model card
     * @param guicard the clicked card GUI component
     */
    private void devCardPressed(ReducedDevCard card, DevelopmentCard guicard) {
        if (guicard == selectedCard) {
            selectedCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
            selectedCard = null;
            selectedColor = "";
            selectedLevel = 0;

            submitBtn.setDisable(true);
        } else {
            if (selectedCard != null)
                selectedCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
            guicard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
            selectedCard = guicard;
            selectedColor = card.getColor();
            selectedLevel = card.getLevel();

            submitBtn.setDisable(false);
        }

        shelvesMap = new HashMap<>();
        Platform.runLater(() -> {
            resetWarehouse();
            resetStrongbox();
            resetSlots();
            resetLeaders();
        });
    }

    /**
     * Resets warehouse content to its state from before moving any resources.
     */
    private void resetWarehouse() {
        warehouse = new Warehouse();

        warehouse.setPrefHeight(245);
        warehouse.setPrefWidth(301);

        List<ReducedResourceContainer> whShelves = vm.getLocalPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow();

        warehouse.setWarehouseShelves(whShelves, (s1, s2) -> {
            warehouse.setWaitingForSwap(s1, s2);
            gui.getUi().dispatch(new ReqSwapShelves(s1, s2));
        }, false);

        warehouse.addResourcesSelector(this::putChoice, this::removeChoice);

        if (containersBox.getChildren().size() >= 1)
            containersBox.getChildren().remove(0);
        containersBox.getChildren().add(0, warehouse);
    }

    /**
     * Resets strongbox content to its state from before moving any resources.
     */
    private void resetStrongbox() {
        strongbox = new Strongbox();

        strongbox.setPrefHeight(245);
        strongbox.setPrefWidth(291);

        strongbox.setContent(vm.getLocalPlayer().flatMap(vm::getPlayerStrongbox).orElseThrow());
        strongbox.addSpinners();

        if (containersBox.getChildren().size() == 2)
            containersBox.getChildren().remove(1);
        containersBox.getChildren().add(strongbox);

    }

    /**
     * Resets card slots content to its state from before doing the action.
     */
    private void resetSlots() {
        List<DevSlot> devSlots = new ArrayList<>();
        List<List<Optional<ReducedDevCard>>> modelSlots = vm.getCurrentPlayer().map(vm::getPlayerDevelopmentCards).orElseThrow();

        modelSlots.forEach(modelSlot -> {
            DevSlot slot = new DevSlot(null);

            List<DevelopmentCard> cards = modelSlot.stream()
                    .flatMap(Optional::stream)
                    .map(DevelopmentCard::new).toList();
            slot.setDevCards(cards);

            devSlots.add(slot);

            for (int i = 0; i < cards.size(); i++)
                AnchorPane.setBottomAnchor(cards.get(i), 40 + 50d * i);
        });

        for (int i = 0; i < vm.getDevSlotsCount() - modelSlots.size(); i++)
            devSlots.add(new DevSlot(null));

        devSlotsBox.getChildren().clear();
        devSlotsBox.getChildren().addAll(devSlots);
        devSlots.forEach(s -> s.setMinWidth(200));
        devSlots.forEach(s -> s.setMinHeight(450));
    }

    /**
     * Handles submit of a card purchase.
     *
     * @param actionEvent the event object
     */
    private void submitPressed(ActionEvent actionEvent) {
        strongbox.fillContainersMap(shelvesMap);
        gui.getUi().dispatch(new ReqBuyDevCard(selectedColor, selectedLevel, devSlotChoicePicker.getValue() - 1, shelvesMap));
    }

    /**
     * Handles going back to previous scene.
     *
     * @param actionEvent the event object
     */
    private void back(ActionEvent actionEvent) {
        gui.setScene(getClass().getResource("/assets/gui/turnbeforeaction.fxml"));
    }

    @Override
    public void on(ErrBuyDevCard event) {
        if (event.isStackEmpty())
            gui.reloadScene("You cannot buy the development card",
                    String.format("You cannot buy the development card with color %s and level %d: deck is empty.",
                            selectedColor, selectedLevel));
        else {
            int slotLevel = vm.getLocalPlayer().map(vm::getPlayerDevelopmentSlots).orElseThrow().get(devSlotChoicePicker.getValue()).map(ReducedDevCard::getLevel).orElse(0);
            int maxLevel = Math.max(slotLevel, selectedLevel);

            String insMsg = String.format(" is insufficient (it has to be %d)", maxLevel - 1);
            String errMsg = String.format("You cannot place the development card into slot %d: card level %d%s, slot level %d%s.",
                    devSlotChoicePicker.getValue(),
                    selectedLevel, slotLevel >= selectedLevel ? insMsg : "",
                    slotLevel, slotLevel < selectedLevel ? insMsg : "");

            gui.reloadScene("You cannot buy the development card", errMsg);
        }
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        if (event.getAction() == UpdateAction.ActionType.BUY_DEVELOPMENT_CARD)
            gui.setScene(getClass().getResource("/assets/gui/turnafteraction.fxml"));

        else if (event.getAction() == UpdateAction.ActionType.SWAP_SHELVES) {
            Shelf s1 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap1()).findAny().orElseThrow();
            Shelf s2 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap2()).findAny().orElseThrow();

            warehouse.swapShelves(s1, s2, true);
        }
    }
}
