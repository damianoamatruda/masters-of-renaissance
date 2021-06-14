package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SetupLeadersController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    
    private final List<LeaderCard> selection = new ArrayList<>();
    
    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    @FXML private HBox leadersContainer;
    @FXML private Button choiceButton;
    @FXML private Title titleComponent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NumberBinding maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
        
        backStackPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        bpane.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Gui gui = Gui.getInstance();
        ViewModel vm = gui.getViewModel();

        if (vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()) == null)
            throw new RuntimeException();

        titleComponent.setText(String.format("Choose %d leader cards",
                vm.getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getChosenLeadersCount()));

        leadersContainer.setSpacing(10);
        leadersContainer.setAlignment(Pos.CENTER);

        List<LeaderCard> leaderCards = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream().map(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
            leaderCard.setLeaderId(reducedLeader.getId());
            leaderCard.setLeaderType(reducedLeader.getLeaderType());
            leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
            leaderCard.setResourceType(reducedLeader.getResourceType());
            if (reducedLeader.getResourceRequirement().isPresent())
                leaderCard.setRequirement(reducedLeader.getResourceRequirement().get());
            if (reducedLeader.getDevCardRequirement().isPresent())
                leaderCard.setRequirement(reducedLeader.getDevCardRequirement().get());

            if (reducedLeader.getLeaderType().equalsIgnoreCase("ZeroLeader"))
                leaderCard.setZeroReplacement(reducedLeader.getResourceType());
            else if (reducedLeader.getLeaderType().equalsIgnoreCase("DiscountLeader"))
                leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
            else if (reducedLeader.getLeaderType().equalsIgnoreCase("ProductionLeader"))
                leaderCard.setProduction(vm.getProduction(reducedLeader.getProduction()).orElseThrow());
            else
                leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                        reducedLeader.getResourceType());

            leaderCard.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (selection.contains(leaderCard)) {
                    selection.remove(leaderCard);
                    leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
                    updateChoiceButton();
                } else if (selection.size() != vm.getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getChosenLeadersCount()) {
                    selection.add(leaderCard);
                    leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
                    updateChoiceButton();
                }
            });

            return leaderCard;
        }).toList();

        leadersContainer.getChildren().addAll(leaderCards);

        updateChoiceButton();
    }

    @FXML
    private void handleChoice() {
        Gui.getInstance().getUi().dispatch(new ReqChooseLeaders(selection.stream().map(LeaderCard::getLeaderId).toList()));
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        if (event.getAction() != UpdateAction.ActionType.CHOOSE_LEADERS && event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");

        if(event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname())) {
            int choosable = gui.getViewModel().getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getInitialResources();

            if (choosable > 0)
                gui.setRoot(getClass().getResource("/assets/gui/setupresources.fxml"));
        }
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        if (gui.getViewModel().getCurrentPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
        else {
            gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));
        }
    }

    private void updateChoiceButton() {
        choiceButton.setDisable(
                selection.size() != Gui.getInstance().getViewModel()
                        .getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getChosenLeadersCount());
    }
}
