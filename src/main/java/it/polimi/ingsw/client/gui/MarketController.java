package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

public class MarketController extends GuiController {
    private static final Logger LOGGER = Logger.getLogger(MarketController.class.getName());

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
        ViewModel vm = gui.getViewModel();
    
        market = new Market();
        market.setContent(vm.getMarket().orElseThrow());
        market.setSelectionListener(this::marketSelected);

        resetChoice();
        
        marketPane.getChildren().add(market);

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnAction(this::back);

        setPauseOnEsc();
    }

    private void marketSelected(int index, boolean isRow) {
        this.isRow = isRow;
        this.index = index;

        ViewModel vm = Gui.getInstance().getViewModel();

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
                .filter(ReducedResourceType::isStorable)
                .map(ReducedResourceType::getName)
                .toList();

        resourcesBox.getChildren().clear();
        resourcesBox.getChildren().addAll(chosenResources.stream().map(n -> {
            Resource r = new Resource();
            r.setResourceType(n);
            r.setOnDragDetected((event) -> {
                Dragboard db = r.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putImage(r.getImage());
                db.setContent(content);
                event.consume();
            });
            return r;
        }).toList());

        resetChoice();
    }

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

    private void removeResourceFromBox(String resource) {
        resourcesBox.getChildren().remove(
            resourcesBox.getChildren().stream().filter(r -> ((Resource)r).getName().equals(resource)).findAny().orElse(null));
    }

    private void resetWarehouse() {
        Gui gui = Gui.getInstance();
        ViewModel vm = gui.getViewModel();

        warehouse = new Warehouse();

        List<ReducedResourceContainer> whShelves = vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname());
            
        warehouse.setWarehouseShelves(whShelves, (s1, s2) -> { warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().dispatch(new ReqSwapShelves(s1, s2)); });

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
                            removeResourceFromBox(resource);
                        }
                    }

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
            }
            event.setDropCompleted(success);
            event.consume();
        }));

        setDnDCanvas();

        warehousePane.getChildren().clear();
        warehousePane.getChildren().add(warehouse);

        warehouse.enableSwapper();
    }

    private void setDnDCanvas() {
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
                    Resource res = (Resource) event.getGestureSource();
                    String resource = res.getName();

                    int amount = selection.get(id).get(res.getName()) - 1;
                    if(amount > 0)
                        selection.get(id).put(resource, amount);
                    else
                        selection.get(id).remove(resource);

                    warehouse.refreshShelfRemove(id);
                    resourcesBox.getChildren().add(res);
                    res.setOnDragDetected((evt) -> {
                        Dragboard resdb = res.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        content.putImage(res.getImage());
                        resdb.setContent(content);
                        evt.consume();
                    });

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


    private void resetLeaders() {
        Gui gui = Gui.getInstance();
        ViewModel vm = gui.getViewModel();
        
        leaderCards = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
            .filter(c -> c.isActive() &&
                (c.getLeaderType().equals("DepotLeader") || c.getLeaderType().equals("ZeroLeader")))
            .map(reducedLeader -> {
                LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
                leaderCard.setLeaderId(reducedLeader.getId());
                leaderCard.setLeaderType(reducedLeader.getLeaderType());
                leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
                leaderCard.setResourceType(reducedLeader.getResourceType());
                if (reducedLeader.getResourceRequirement().isPresent())
                    leaderCard.setRequirement(reducedLeader.getResourceRequirement().get());
                if (reducedLeader.getDevCardRequirement().isPresent())
                    leaderCard.setRequirement(reducedLeader.getDevCardRequirement().get());

                if (reducedLeader.getLeaderType().equals("ZeroLeader")) {
                    leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                    // TODO handle substitution
                } else {
                    leaderCard.setDepotContent(gui.getViewModel().getContainer(reducedLeader.getContainerId()).orElseThrow(),
                            reducedLeader.getResourceType());

                    leaderCard.setOnDragOver((event) -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasImage()) {
                            event.acceptTransferModes(TransferMode.COPY);
                        }
                        event.consume();
                    });
                
                    leaderCard.setOnDragDropped((event) -> {
                        Dragboard db = event.getDragboard();
                        boolean success = false;
                        if (db.hasImage()) {
                            
                            int id = reducedLeader.getContainerId();
                            String resource = ((Resource) event.getGestureSource()).getName();

                            success = putChoice(resource, id);
                            if(success) { // TODO check edge cases, ex adding more than 2 resources
                                ReducedResourceContainer lCont = leaderCard.getContainer();
                                Map<String, Integer> content = lCont.getContent();
                                content.compute(resource, (k, v) -> v == null ? 1 : v++);
                                ReducedResourceContainer newContainer = new ReducedResourceContainer(lCont.getId(), lCont.getSize(), content, lCont.getBoundedResType().orElse(null));

                                leaderCard.setDepotContent(newContainer, reducedLeader.getResourceType());

                                removeResourceFromBox(resource);
                            }
                                
                        }
                        if(db.hasString() && success) {
                            success = false;
                            int id = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                            String resource = ((Resource) event.getGestureSource()).getName();
            
                            int amount = selection.get(id).get(resource) - 1;
                            if(amount > 0)
                                selection.get(id).put(resource, amount);
                            else
                                selection.get(id).remove(resource);
            
                            // warehouse.refreshShelfRemove(id, resource);
                            success = false;
                        }
                        event.setDropCompleted(success);
                        event.consume();
                    });
                }
            
                return leaderCard;
        }).toList();

        leaderCardsBox.getChildren().clear();
        leaderCardsBox.getChildren().addAll(leaderCards);
    }
    
    private void resetChoice() {
        resetWarehouse();

        resetLeaders();

        selection = new HashMap<>();
    }

    private void submitPressed(ActionEvent actionEvent) {
        Gui.getInstance().dispatch(new ReqTakeFromMarket(isRow, index, new HashMap<>(), selection));
    }

    private void back(ActionEvent actionEvent) {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
    }

    @Override
    public void on(Gui gui, UpdateAction event) {
        super.on(gui, event);
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

    private void setPauseOnEsc() {
        this.canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                backStackPane.getChildren().add(new PauseMenu(maxScale));
            }
        });
    }
}
