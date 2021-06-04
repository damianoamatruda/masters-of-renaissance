package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.backend.model.leadercards.DepotLeader;
import it.polimi.ingsw.common.backend.model.leadercards.ZeroLeader;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.*;

public class MarketController extends GuiController {
    @FXML private AnchorPane canvas;
    @FXML private Pane marketPane;
    @FXML private Pane warehousePane;
    @FXML private HBox leaderCardsBox;
    @FXML private HBox resourcesBox;
    @FXML private Button submitBtn;
    @FXML private Button back;

    private boolean isRow;
    private int index;
    private Market market;
    private Warehouse warehouse;
    private List<LeaderCard> leaderCards;
    private Map<Integer, Map<String, Integer>> selection = new HashMap<>();
    
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
        ViewModel vm = gui.getViewModel();
    
        market = new Market();
        market.setContent(vm.getMarket());
        market.setSelectionListener(this::marketSelected);

        resetChoice();
        
        marketPane.getChildren().add(market);

        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(this::submitPressed);

        back.setOnAction(this::back);
    }

    private void marketSelected(int index, boolean isRow) {
        this.isRow = isRow;
        this.index = index;

        ViewModel vm = Gui.getInstance().getViewModel();
        // get a list with the selected resources
        List<ReducedResourceType> chosenResources = new ArrayList<>();
        if (isRow)
            chosenResources = vm.getMarket().getGrid().get(index);
        else {
            for (List<ReducedResourceType> row : vm.getMarket().getGrid())
                chosenResources.add(row.get(index));
        }
        List<String> chosenResourcesNames = chosenResources.stream().filter(ReducedResourceType::isStorable).map(ReducedResourceType::getName).toList();

        resourcesBox.getChildren().clear();
        resourcesBox.getChildren().addAll(chosenResourcesNames.stream().map(n -> {
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
            if(selection.get(shelfID) == null || selection.get(shelfID).size() == 0) {
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
            resourcesBox.getChildren().stream().filter(r -> ((Resource)r).getName().equals(resource)).findAny().get());
    }

    private void resetWarehouse() {
        Gui gui = Gui.getInstance();
        ViewModel vm = gui.getViewModel();

        warehouse = new Warehouse();

        List<ReducedResourceContainer> whShelves = vm.getPlayerData(vm.getLocalPlayerNickname()).getWarehouseShelves().stream()
            .map(id -> vm.getContainer(id).orElseThrow()).toList();
            
        warehouse.setWarehouseShelves(whShelves);

        warehouse.getChildren().forEach(shelf -> shelf.setOnDragOver((event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        }));

        warehouse.getChildren().forEach(shelf -> shelf.setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                try {
                    int shelfID = ((Shelf) shelf).getShelfId();
                    String resource = ((Resource) event.getGestureSource()).getName();

                    Shelf s = warehouse.getShelf(shelfID);

                    if(s.getContentSize() < s.getSize() && (s.getBoundResource() == null || s.getBoundResource().equalsIgnoreCase(resource))) {
                        success = putChoice(resource, shelfID);
                        if (success) {
                            warehouse.refreshShelfAdd(shelfID, resource);
                            removeResourceFromBox(resource);
                        }
                    }

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
            }
            event.setDropCompleted(success);
            event.consume();
        }));

        setDnDCanvas();

        warehousePane.getChildren().clear();
        warehousePane.getChildren().add(warehouse);
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

                    warehouse.refreshShelfRemove(id, resource);
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
//                    e.printStackTrace();
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
                (c.getLeaderType().equals(DepotLeader.class.getSimpleName()) || c.getLeaderType().equals(ZeroLeader.class.getSimpleName())))
            .map(reducedLeader -> {
                LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
                leaderCard.setLeaderId(reducedLeader.getId());
                leaderCard.setLeaderType(reducedLeader.getLeaderType());
                leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
                leaderCard.setResourceType(reducedLeader.getResourceType().getName());
                leaderCard.setRequirement(reducedLeader.getResourceRequirement());
                leaderCard.setRequirement(reducedLeader.getDevCardRequirement());
                
                if(reducedLeader.getLeaderType().equals(ZeroLeader.class.getSimpleName())) {
                    leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                    // TODO handle substitution
                }
                else {
                    leaderCard.setDepotContent(gui.getViewModel().getContainer(reducedLeader.getContainerId()).orElseThrow(),
                        reducedLeader.getResourceType().getName());

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
                                ReducedResourceContainer newContainer = new ReducedResourceContainer(lCont.getId(), lCont.getSize(), content, lCont.getBoundedResType());

                                leaderCard.setDepotContent(newContainer, reducedLeader.getResourceType().getName());

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
        gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));
    }
}
