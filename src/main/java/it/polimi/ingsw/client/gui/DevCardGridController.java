package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import java.net.URL;
import java.util.*;

import static it.polimi.ingsw.client.gui.Gui.setPauseHandlers;

/** Gui controller class of the development card purchase action. */
public class DevCardGridController extends GuiController {
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
    private VBox rightVBox;
    @FXML
    private ChoiceBox<Integer> devSlotChoicePicker;
    @FXML
    private Button submitBtn;
    @FXML private Button back;
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
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);

        devCardGrid = new DevCardGrid();
        devCardGrid.setGrid(vm.getDevCardGrid().orElseThrow());
        devCardGrid.setControllerListener(this::devCardPressed);

//        devCardGrid.setScaleX(0.7);
//        devCardGrid.setScaleY(0.7);

        devCardGridPane.getChildren().add(devCardGrid);

        for (int i = 0; i < vm.getSlotsCount(); i++)
            devSlotChoicePicker.getItems().add(i);

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnAction(this::back);

        resetWarehouse();
        resetStrongbox();
        resetSlots();
        resetLeaders();

        setPauseHandlers(backStackPane, canvas, maxScale);

        canvas.setBorder(new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        devCardGridPane.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        containersBox.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        devSlotsBox.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        leadersBox.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        rightVBox.setBorder(new Border(new BorderStroke(Color.PINK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    @Override
    StackPane getRootElement() {
        return backStackPane;
    }

    /**
     *
     * @param resource
     * @param shelfID
     * @return
     */
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

    private void resetLeaders() {
        leadersBox.getChildren().clear();
        List<LeaderCard> leaders = gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getLocalPlayerNickname()).stream()
                .filter(c -> c.isActive() &&
                        (c.getLeaderType().equals("DepotLeader") || c.getLeaderType().equals("DiscountLeader")))
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

                    if(reducedLeader.getLeaderType().equals("DepotLeader")) {
                        leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                                reducedLeader.getResourceType(), true);

                        leaderCard.getGuiDepot().addResourcesSelector(shelvesMap,
                                vm.getContainer(reducedLeader.getContainerId())
                                        .stream().filter(d -> d.getId() == reducedLeader.getContainerId())
                                        .findAny().orElseThrow());
                    } else
                        leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());

                    leaderCard.setScaleX(0.8);
                    leaderCard.setScaleY(0.8);

                    return leaderCard;

                }).toList();

        leadersBox.getChildren().addAll(leaders);

        // adjust components sizes and positioning if leadersBox has cards
        if(!leaders.isEmpty()) {
            leadersBox.setMinHeight(180);
            leadersBox.setMaxHeight(180);
            // devSlotsBox.setMinHeight(400);
            // devSlotsBox.setMaxHeight(400);
            devSlotsBox.setScaleX(0.6);
            devSlotsBox.setScaleY(0.6);
            containersBox.setScaleX(0.7);
            containersBox.setScaleY(0.7);

            // devSlotsBox.setMinHeight(100);
            // devSlotsBox.setMaxHeight(100);
        }

    }

    /**
     *
     * @param resource
     * @param shelfID
     * @return
     */
    private void removeChoice(String resource, int shelfID) {
        int amount = shelvesMap.get(shelfID).get(resource) - 1;
        shelvesMap.get(shelfID).put(resource, amount);
        if(shelvesMap.get(shelfID).get(resource) == 0)
            shelvesMap.get(shelfID).remove(resource);
    }

    /**
     * Handles click on a development card.
     *
     * @param card      the cached model card
     * @param guicard   the clicked card GUI component
     */
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
        resetLeaders();
    }

    /**
     *  Resets warehouse content to its state from before moving any resources.
     */
    private void resetWarehouse() {
        warehouse = new Warehouse();

        warehouse.setPrefHeight(245);
        warehouse.setPrefWidth(301);

        List<ReducedResourceContainer> whShelves = vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname());

        warehouse.setWarehouseShelves(whShelves, (s1, s2) -> {
                warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().getUi().dispatch(new ReqSwapShelves(s1, s2));
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

        strongbox.setContent(vm.getPlayerStrongbox(vm.getLocalPlayerNickname()).orElseThrow());

        if (containersBox.getChildren().size() == 2)
            containersBox.getChildren().remove(1);
        containersBox.getChildren().add(strongbox);

        // strongbox.setScaleX(0.71);
        // strongbox.setScaleY(0.71);
    }

    /**
     * Resets card slots content to its state from before doing the action.
     */
    private void resetSlots() {
        List<DevSlot> devSlots = new ArrayList<>();
        List<List<ReducedDevCard>> modelSlots = vm.getPlayerDevelopmentCards(vm.getCurrentPlayer());
        
        modelSlots.forEach(modelSlot -> {
            DevSlot slot = new DevSlot();

            List<DevelopmentCard> cards = modelSlot.stream()
                    .filter(Objects::nonNull)
                    .map(c -> new DevelopmentCard(c)).toList();
            slot.setDevCards(cards);

            devSlots.add(slot);

            for(int i = 0; i < cards.size(); i++)
                AnchorPane.setBottomAnchor(cards.get(i), 40 + 50d * i);

        });
        
        for(int i = 0; i < vm.getSlotsCount() - modelSlots.size(); i++)
            devSlots.add(new DevSlot());

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
        Gui.getInstance().getUi().dispatch(new ReqBuyDevCard(selectedColor, selectedLevel, devSlotChoicePicker.getValue(), shelvesMap));
    }

    /**
     * Handles going back to previous scene.
     *
     * @param actionEvent the event object
     */
    private void back(ActionEvent actionEvent) {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        if (event.getAction() == UpdateAction.ActionType.BUY_DEVELOPMENT_CARD)
            gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));

        else if (event.getAction() == UpdateAction.ActionType.SWAP_SHELVES) {
            Shelf s1 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap1()).findAny().orElseThrow();
            Shelf s2 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap2()).findAny().orElseThrow();

            warehouse.swapShelves(s1, s2);
        }
    }
}
