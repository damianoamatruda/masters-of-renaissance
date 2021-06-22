package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.client.gui.Gui.setPauseHandlers;

/** Gui controller class of the take Market resources action. */
public class MarketController extends GuiController {
    private static final Logger LOGGER = Logger.getLogger(MarketController.class.getName());

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @FXML
    private StackPane backStackPane;
    @FXML
    private AnchorPane canvas;
    @FXML
    private Pane marketPane;
    @FXML
    private Pane warehousePane;
    @FXML
    private HBox leaderCardsBox;
    @FXML
    private HBox resourcesBox;
    @FXML
    private Button submitBtn;
    @FXML
    private Button back;

    private NumberBinding maxScale;

    private boolean isRow;
    private int index;
    private Market market;
    private Warehouse warehouse;
    private List<LeaderCard> leaderCards;
    private Map<Integer, Map<String, Integer>> selection = new HashMap<>();
    private boolean isZeroReplaceable;
    private final Map<String, Integer> replacements = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);

        market = new Market();
        market.setContent(vm.getMarket().orElseThrow());
        market.setSelectionListener(this::marketSelected);

        resetChoice();

        marketPane.getChildren().add(market);

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnAction(this::back);

        setPauseHandlers(backStackPane, canvas, maxScale);
    }

    @Override
    StackPane getRootElement() {
        return backStackPane;
    }

    /**
     * Fills the choice box with the resources included in the row or column selected.
     *
     * @param index the index of the row or column selected
     * @param isRow true if row has been selected
     */
    private void marketSelected(int index, boolean isRow) {
        this.isRow = isRow;
        this.index = index;

        // get a list with the selected resources
        List<String> chosenResources = new ArrayList<>();
        if (isRow)
            chosenResources = vm.getMarket().orElseThrow().getGrid().get(index);
        else {
            for (List<String> row : vm.getMarket().orElseThrow().getGrid())
                chosenResources.add(row.get(index));
        }
        chosenResources = chosenResources.stream()
                .map(n -> vm.getResourceTypes().stream().filter(r -> r.getName().equals(n)).findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(r -> r.isStorable() || r.getName().equals("Zero") && isZeroReplaceable)
                .map(ReducedResourceType::getName)
                .toList();

        resourcesBox.getChildren().clear();
        resourcesBox.getChildren().addAll(chosenResources.stream().map(n -> {
            Resource r = new Resource(n, isZeroReplaceable && n.equalsIgnoreCase("Zero"));
            setDragAndDropSource(r);
            return r;
        }).toList());

        resetChoice();
        resetLeaders();
    }

    /**
     * Inserts a chosen resource inside of a shelf.
     *
     * @param resource  the resource to be inserted
     * @param shelfID   the ID of the shelf involved
     * @return true if transaction successful
     */
    private boolean putChoice(String resource, int shelfID) {
        boolean success = false;
        try {
            int amount = selection.get(shelfID).get(resource) + 1;
            selection.get(shelfID).put(resource, amount);
            success = true;
        } catch (NullPointerException e) {
            if(selection.get(shelfID) == null || selection.get(shelfID).isEmpty()) {
                Map<String, Integer> entry = new HashMap<>();
                entry.put(resource, 1);
                selection.put(shelfID, entry);
                success = true;
            }
        }
        return success;
    }

    /**
     * Removes a resource from the choice container after it is chosen
     *
     * @param resource the resource type to be removed from the choice box (because chosen)
     */
    private void removeResourceFromBox(String resource) {
        resourcesBox.getChildren().remove(
                resourcesBox.getChildren().stream().filter(r -> ((Resource)r).getName().equals(resource)).findAny().orElse(null));
    }

    /**
     * Resets warehouse content to state from before market action
     */
    private void resetWarehouse() {
        warehouse = new Warehouse();

        List<ReducedResourceContainer> whShelves = vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname());

        warehouse.setWarehouseShelves(whShelves, (s1, s2) -> { warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().getUi().dispatch(new ReqSwapShelves(s1, s2)); });

        warehouse.getChildren().forEach(shelf -> ((Shelf) shelf).getChildren().get(1).setOnDragOver((event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        }));

        warehouse.getChildren().forEach(shelf -> ((Shelf) shelf).getChildren().get(1).setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                try {
                    int shelfID = ((Shelf) shelf).getShelfId();
                    String resource = ((Resource) event.getGestureSource()).getName();

                    boolean alreadyHasBoundShelf = (selection.keySet().stream().anyMatch(sh -> selection.get(sh).containsKey(resource) && sh != shelfID) ||
                            warehouse.getShelfByResource(resource).isPresent() && warehouse.getShelfByResource(resource).get().getShelfId() != shelfID)
                            && !(db.hasString() && (selection.get(Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT))).get(resource) < 2));

                    Shelf s = warehouse.getShelf(shelfID);

                    if(s.getContentSize() < s.getSize() && (s.getBoundResource() == null || s.getBoundResource().equalsIgnoreCase(resource)) && !alreadyHasBoundShelf) {
                        success = putChoice(resource, shelfID);
                        if (success) {
                            warehouse.addResourceDraggable(shelfID, resource);
                            if(!db.hasString())
                                removeResourceFromBox(resource);
                            replacements.put(resource, replacements.containsKey(resource) ? replacements.get(resource) + 1 : 1);
                        }
                    }

                } catch (Exception e) { // TODO remove this catch once debugged
                    LOGGER.log(Level.SEVERE, "Unknown exception (TODO: Remove this)", e);
                }
            }
            // if the resource of choice was previously inserted in another shelf the moved
            if(db.hasString() && success) {
                int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                String resource = ((Resource) event.getGestureSource()).getName();

                int amount = selection.get(id).get(resource) - 1;
                if(amount > 0)
                    selection.get(id).put(resource, amount);
                else
                    selection.get(id).remove(resource);

                warehouse.refreshShelfRemove(id);
            }
            event.setDropCompleted(success);
            event.consume();
        }));

        setDnDCanvas();

        warehousePane.getChildren().clear();
        warehousePane.getChildren().add(warehouse);

        warehouse.enableSwapper();
    }

    /**
     * Adds drag and drop handling for throwing resources out of the warehouse.
     */
    private void setDnDCanvas() {
        //On drag over
        this.canvas.setOnDragOver((event) -> {
                    Dragboard db = event.getDragboard();
                    if (db.hasImage()) {
                        event.acceptTransferModes(TransferMode.ANY);
                    }
                    event.consume();
                }
        );

        // On drag dropped
        this.canvas.setOnDragDropped((event) -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    try {
                        int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                        Resource res = (Resource) event.getGestureSource();
                        String resource = res.getName();

                        int amount = selection.get(id).get(res.getName()) - 1;
                        if(amount > 0)
                            selection.get(id).put(resource, amount);
                        else
                            selection.get(id).remove(resource);

                        if(warehouse.getShelf(id) != null)
                            warehouse.refreshShelfRemove(id);
                        else leaderCards.stream()
                                .filter(l -> l.getGuiDepot() != null && l.getGuiDepot().getBoundResource().equals(resource))
                                .map(LeaderCard::getGuiDepot).findAny().ifPresent(Shelf::removeResource);
                        resourcesBox.getChildren().add(res);
                        setDragAndDropSource(res);
//                        replacements.put(resource, replacements.containsKey(resource) ? replacements.get(resource) - 1 : 0);
//                        if(replacements.containsKey(resource) && replacements.get(resource) == 0) replacements.remove(resource);
                        success = true;
                    } catch (NumberFormatException | NullPointerException e) {
                        // it is fine if it passes here. Drop will be ignored
                    } catch (Exception e) { // TODO remove this catch once debugged
                        LOGGER.log(Level.SEVERE, "Unknown exception (TODO: Remove this)", e);
                    }

                    event.setDropCompleted(success);
                    event.consume();
                }
        );
    }

    private void setDragAndDropSource(Resource res) {
        if(!res.isBlank())
            res.setOnDragDetected((evt) -> {
                Dragboard resdb = res.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putImage(res.getImage());
                resdb.setContent(content);
                evt.consume();
            });
    }


    /**
     * Resets the leaders box view component.
     */
    private void resetLeaders() {
        Gui gui = Gui.getInstance();

        leaderCards = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
                .filter(c -> c.isActive() &&
                        (c.getLeaderType().equals("DepotLeader") || c.getLeaderType().equals("ZeroLeader")))
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

                    if (reducedLeader.getLeaderType().equals("ZeroLeader")) {
                        isZeroReplaceable = true;
                        leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                        // TODO handle substitution
                    } else {
                        leaderCard.setDepotContent(gui.getViewModel().getContainer(reducedLeader.getContainerId()).orElseThrow(),
                                reducedLeader.getResourceType(), true);

                        leaderCard.setOnDragOver((event) -> {
                            Dragboard db = event.getDragboard();
                            if (db.hasImage()) {
                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                            event.consume();
                        });

                        leaderCard.getGuiDepot().setOnDragDropped((event) -> {
                            Shelf s = leaderCard.getGuiDepot();
                            Dragboard db = event.getDragboard();
                            boolean success = false;
                            try {
                                int shelfID = (leaderCard.getGuiDepot()).getShelfId();
                                String resource = ((Resource) event.getGestureSource()).getName();

                                if(s.getContentSize() < s.getSize() && (s.getBoundResource() == null || s.getBoundResource().equalsIgnoreCase(resource))) {
                                    success = putChoice(resource, shelfID);
                                    if (success) {
                                        s.addResourceDraggable(resource);
                                        if(!db.hasString())
                                            removeResourceFromBox(resource);
                                        replacements.put(resource, replacements.containsKey(resource) ? replacements.get(resource) + 1 : 1);
                                    }
                                }

                            } catch (Exception e) { // TODO remove this catch once debugged
                                LOGGER.log(Level.SEVERE, "Unknown exception (TODO: Remove this)", e);
                            }
                            if(db.hasString() && success) {
                                int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                                String resource = ((Resource) event.getGestureSource()).getName();

                                int amount = selection.get(id).get(resource) - 1;
                                if(amount > 0)
                                    selection.get(id).put(resource, amount);
                                else
                                    selection.get(id).remove(resource);

                                s.removeResource();
                            }
                            event.setDropCompleted(success);
                            event.consume();

                        });
                    }

                    return leaderCard;
                }).toList();

        List<VBox> content = leaderCards.stream().map(l -> {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(0.5);
            vbox.getChildren().add(l);
            if(l.getLeaderType().equals("ZeroLeader")) {
                SButton replace = new SButton("Replace a Zero");
                replace.setOnAction((actionEvent -> {
                    if(replaceBlank(l.getResourceType()))
                        replace.setDisable(true);
                }));
                vbox.getChildren().add(replace);
            }
            return vbox;
        }).toList();

        leaderCardsBox.getChildren().clear();
        leaderCardsBox.getChildren().addAll(content);
    }

    private boolean replaceBlank(String resourceType) {
        Optional<Node> blank = resourcesBox.getChildren().stream().filter(r -> ((Resource) r).isBlank()).findFirst();

        blank.ifPresent(node -> {
            Platform.runLater(() -> {
                Resource replacement = new Resource(resourceType);
                setDragAndDropSource(replacement);
                resourcesBox.getChildren().set(resourcesBox.getChildren().indexOf(node), replacement);
            });
        });

        return resourcesBox.getChildren().stream().noneMatch(r -> ((Resource) r).isBlank());
    }

    /**
     * Resets the player's choices.
     */
    private void resetChoice() {
        resetWarehouse();

        resetLeaders();

        selection = new HashMap<>();
    }

    /**
     * Submits a request to the backend for taking the resources.
     *
     * @param actionEvent the event object
     */
    private void submitPressed(ActionEvent actionEvent) {
        Gui.getInstance().getUi().dispatch(new ReqTakeFromMarket(isRow, index, replacements, selection));
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
        if(event.getAction() == UpdateAction.ActionType.TAKE_MARKET_RESOURCES)
            gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));

        else if(event.getAction() == UpdateAction.ActionType.SWAP_SHELVES) {
            Shelf s1 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap1()).findAny().orElseThrow();
            Shelf s2 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap2()).findAny().orElseThrow();

            Platform.runLater(() -> {
                Map<String, Integer> temp = selection.get(s1.getShelfId());
                if(selection.get(s2.getShelfId()) != null)
                    selection.put(s1.getShelfId(), selection.get(s2.getShelfId()));
                else selection.remove(s1.getShelfId());
                if(temp != null)
                    selection.put(s2.getShelfId(), temp);
                else selection.remove(s2.getShelfId());

                warehouse.swapShelves(s1, s2);
            });
        }
    }
}
