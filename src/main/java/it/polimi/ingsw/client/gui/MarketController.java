package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.client.gui.components.Market;
import it.polimi.ingsw.client.gui.components.Warehouse;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.backend.model.leadercards.DepotLeader;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MarketController extends GuiController {
    @FXML private AnchorPane canvas;
    @FXML private Pane marketPane;
    @FXML private Pane warehousePane;
    @FXML private HBox leaderCardsBox;
    private List<ReducedResourceContainer> shelves;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Gui gui = Gui.getInstance();
        ViewModel vm = gui.getViewModel();
    
        Market market = new Market();
        market.setContent(vm.getMarket());
        market.setSelectionListener(this::marketSelected);


        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseShelves(
            vm.getPlayerData(vm.getLocalPlayerNickname()).getWarehouseShelves().stream()
                .map(id -> vm.getContainer(id).orElseThrow()).toList());

        List<LeaderCard> leaderCards = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
            .filter(c -> c.isActive() && c.getLeaderType().equals(DepotLeader.class.getSimpleName()))
            .map(reducedCard -> {
                LeaderCard leaderCard = new LeaderCard(reducedCard.getLeaderType());
                leaderCard.setLeaderId(reducedCard.getId());
                leaderCard.setLeaderType(reducedCard.getLeaderType());
                leaderCard.setVictoryPoints(reducedCard.getVictoryPoints() + "");
                leaderCard.setResourceType(reducedCard.getResourceType().getName());
                leaderCard.setRequirement(reducedCard.getResourceRequirement());
                leaderCard.setRequirement(reducedCard.getDevCardRequirement());
                
                leaderCard.setDepotContent(gui.getViewModel().getContainer(reducedCard.getContainerId()).orElseThrow(),
                        reducedCard.getResourceType().getName());
            
                return leaderCard;
        }).toList();

        marketPane.getChildren().add(market);
        warehousePane.getChildren().add(warehouse);
        leaderCardsBox.getChildren().addAll(leaderCards);
    }

    private void marketSelected(int index, boolean isRow) {
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
    }
}
