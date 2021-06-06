package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrCardRequirements;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public abstract class PlaygroundController extends GuiController {
    @FXML
    protected AnchorPane canvas;
    @FXML
    protected Playerboard pboard;

    private final VBox leadersBox = new VBox();

    @FXML protected Text topText = new Text();

    private LeaderCard toDiscard = null;

    protected final Warehouse warehouse = new Warehouse();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        Gui gui = Gui.getInstance();

        ViewModel vm = gui.getViewModel();

        int baseProdId = gui.getViewModel().getCurrentPlayerData().getBaseProduction();

        Production prod = new Production();
        prod.setStyle("-fx-background-image: url('/assets/gui/playerboard/baseproduction.png');" +
                "-fx-background-position: center center;" +
                "-fx-alignment: center;" +
                "-fx-background-repeat: stretch;" +
                "-fx-opacity: 1;" +
                "-fx-background-size: 100 100;");
        prod.setProduction(gui.getViewModel().getProduction(baseProdId).orElseThrow());


        warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getCurrentPlayer()), (s1, s2) -> { warehouse.setWaitingForSwap(s1, s2); Gui.getInstance().dispatch(new ReqSwapShelves(s1, s2)); });


        Strongbox s = new Strongbox();
        int sbId = gui.getViewModel().getCurrentPlayerData().getStrongbox();

        s.setContent(gui.getViewModel().getContainer(sbId).orElseThrow());

        List<DevSlot> slots = new ArrayList<>();

        List<List<Integer>> modelSlots = vm.getCurrentPlayerData().getDevSlots();
        for (List<Integer> modelSlot : modelSlots) {
            DevSlot slot = new DevSlot();

            List<DevelopmentCard> cards = modelSlot.stream()
                    .map(i -> new DevelopmentCard(vm.getDevelopmentCard(i).orElseThrow()))
                    .toList();
            slot.setDevCards(cards);

            slots.add(slot);
        }
        for(int i = 0; i < vm.getSlotsCount() - modelSlots.size(); i++){
            slots.add(new DevSlot());
        }

        FaithTrack f = new FaithTrack(vm.getFaithTrack());

        pboard = new Playerboard(warehouse, s, prod, slots, f);

        canvas.getChildren().add(pboard);

        AnchorPane.setBottomAnchor(pboard, 0d);
        AnchorPane.setTopAnchor(pboard, 0d);
        AnchorPane.setLeftAnchor(pboard, 0d);
        AnchorPane.setRightAnchor(pboard, 0d);


        canvas.setBorder(new Border(new BorderStroke(Color.PINK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pboard.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

    }

    protected void setLeadersBox() {
        ViewModel vm = Gui.getInstance().getViewModel();
        List<LeaderCard> leaders = vm.getCurrentPlayerData().getLeadersHand().stream()
            .map(id -> vm.getLeaderCard(id).orElseThrow())
            .map(reducedLeader -> {
                LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
                leaderCard.setLeaderId(reducedLeader.getId());
                leaderCard.setLeaderType(reducedLeader.getLeaderType());
                leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
                leaderCard.setResourceType(reducedLeader.getResourceType().getName());
                if (reducedLeader.getResourceRequirement() != null)
                    leaderCard.setRequirement(reducedLeader.getResourceRequirement());
                if (reducedLeader.getDevCardRequirement() != null)
                    leaderCard.setRequirement(reducedLeader.getDevCardRequirement());

                if (reducedLeader.getLeaderType().equalsIgnoreCase("ZeroLeader"))
                    leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                else if (reducedLeader.getLeaderType().equalsIgnoreCase("DiscountLeader"))
                    leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
                else if (reducedLeader.getLeaderType().equalsIgnoreCase("ProductionLeader"))
                    leaderCard.setProduction(vm.getProduction(reducedLeader.getProduction()).orElseThrow());
                else
                    leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                            reducedLeader.getResourceType().getName());

                leaderCard.setActivated(reducedLeader.isActive());

                return leaderCard;
            }).toList();

        leadersBox.setAlignment(Pos.CENTER);
        leadersBox.setPrefWidth(166);
        leadersBox.setSpacing(20);

        int i = 0;
        for(LeaderCard leader : leaders) {
            leadersBox.getChildren().add(leader);
            HBox h = new HBox();
            h.setAlignment(Pos.CENTER);
            h.setSpacing(20);

            if (!leader.isActivated()) {
                Button activate = new SButton("Activate");
                int finalI = i;
                activate.setOnMouseClicked((event -> handleActivate(finalI)));

                Button discard = new SButton("Discard");
                discard.setOnMouseClicked((event -> handleDiscard(finalI)));

                h.getChildren().addAll(List.of(activate, discard));
            }

            leadersBox.getChildren().add(h);
            i++;
        }

        canvas.getChildren().add(leadersBox);
        AnchorPane.setRightAnchor(leadersBox, 10.0);
        AnchorPane.setBottomAnchor(leadersBox, 50.0);

    }

    @Override
    public void on(Gui gui, UpdateFaithPoints event) {
        int oldPts;

        if(!event.isBlackCross())
            oldPts = gui.getViewModel().getPlayerData(event.getPlayer()).getFaithPoints();
        else oldPts = gui.getViewModel().getBlackCrossFP();

        super.on(gui, event);

        Platform.runLater(() -> pboard.updateFaithPoints(event, oldPts));

    }


    @Override
    public void on(Gui gui, UpdateCurrentPlayer event) {
        super.on(gui, event);
        if(event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
        else
            gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));

    }

    private void handleActivate(int leaderIndex) {
        int leaderId = ((LeaderCard) leadersBox.getChildren().get(2 * leaderIndex)).getLeaderId();
        Gui.getInstance().dispatch(new ReqLeaderAction(leaderId, true));
    }

    private void handleDiscard(int leaderIndex) {
        LeaderCard leader = (LeaderCard) leadersBox.getChildren().get(2 * leaderIndex);
        if (toDiscard == null) {
            toDiscard = leader;
            Gui.getInstance().dispatch(new ReqLeaderAction(leader.getLeaderId(), false));
        }
    }

    @Override
    public void on(Gui gui, UpdateActivateLeader event) {
        super.on(gui, event);

        LeaderCard leader = (LeaderCard) leadersBox.getChildren().stream().filter(l -> ((LeaderCard) l).getLeaderId() == event.getLeader()).findAny().orElseThrow();
        int leaderIndex = leadersBox.getChildren().indexOf(leader);

        leader.setActivated(true);

        HBox buttons = (HBox) leadersBox.getChildren().get(leaderIndex + 1);
        buttons.getChildren().forEach(b -> { b.setDisable(true); b.setVisible(false);});
    }

    @Override
    public void on(Gui gui, ErrCardRequirements event) {
        super.on(gui, event);

//        Alert a = new Alert(Alert.AlertType.ERROR);
//        a.setContentText("Requirements not met. Leader cannot be activated.");
//
//        a.show();

    }

    @Override
    public void on(Gui gui, UpdateLeadersHandCount event) {
        super.on(gui, event);

        int leaderIndex = leadersBox.getChildren().indexOf(toDiscard);
        gui.getViewModel().getCurrentPlayerData().getLeadersHand().remove(Integer.valueOf(toDiscard.getLeaderId()));

//        leadersBox.getChildren().remove(leaderIndex, leaderIndex + 2);
        leadersBox.getChildren().get(leaderIndex).setVisible(false);

        leadersBox.getChildren().get(leaderIndex + 1).setDisable(true);
        leadersBox.getChildren().get(leaderIndex + 1).setVisible(false);

        toDiscard = null;
    }

    @Override
    public void on(Gui gui, UpdateAction event) {
        super.on(gui, event);

        if(event.getAction() == UpdateAction.ActionType.SWAP_SHELVES) {
            Shelf s1 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap1()).findAny().orElseThrow();
            Shelf s2 = (Shelf) warehouse.getChildren().stream().filter(s -> ((Shelf) s).getShelfId() == warehouse.getWaitingForSwap2()).findAny().orElseThrow();

            warehouse.swapShelves(s1, s2);
        }
    }
}
