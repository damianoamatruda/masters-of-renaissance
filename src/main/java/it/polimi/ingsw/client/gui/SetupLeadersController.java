package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.LeaderCard;
import it.polimi.ingsw.client.gui.components.Title;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SetupLeadersController extends GuiController {
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private final List<LeaderCard> selection = new ArrayList<>();
    @FXML
    private HBox leadersContainer;
    @FXML
    private Button choiceButton;
    @FXML
    private Title titleComponent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gui gui = Gui.getInstance();

        if (gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getLocalPlayerNickname()) == null)
            throw new RuntimeException();

        titleComponent.setText(String.format("Choose %d leader cards", gui.getViewModel().getLocalPlayerData().getSetup().getChosenLeadersCount()));

        leadersContainer.setSpacing(10);
        leadersContainer.setAlignment(Pos.CENTER);

        List<LeaderCard> leaderCards = gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getLocalPlayerNickname()).stream().map(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
            leaderCard.setLeaderId(reducedLeader.getId());
            leaderCard.setLeaderType(reducedLeader.getLeaderType());
            leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
            leaderCard.setResourceType(reducedLeader.getResourceType().getName());
            if (reducedLeader.getResourceRequirement().isPresent())
                leaderCard.setRequirement(reducedLeader.getResourceRequirement().get());
            if (reducedLeader.getDevCardRequirement().isPresent())
                leaderCard.setRequirement(reducedLeader.getDevCardRequirement().get());

            if (reducedLeader.getLeaderType().equalsIgnoreCase("ZeroLeader"))
                leaderCard.setZeroReplacement(reducedLeader.getResourceType());
            else if (reducedLeader.getLeaderType().equalsIgnoreCase("DiscountLeader"))
                leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
            else if (reducedLeader.getLeaderType().equalsIgnoreCase("ProductionLeader"))
                leaderCard.setProduction(gui.getViewModel().getProduction(reducedLeader.getProduction()).orElseThrow());
            else
                leaderCard.setDepotContent(gui.getViewModel().getContainer(reducedLeader.getContainerId()).orElseThrow(),
                        reducedLeader.getResourceType().getName());

            leaderCard.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (selection.contains(leaderCard)) {
                    selection.remove(leaderCard);
                    leaderCard.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
                    updateChoiceButton();
                } else if (selection.size() != gui.getViewModel().getLocalPlayerData().getSetup().getChosenLeadersCount()) {
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
        Gui.getInstance().dispatch(new ReqChooseLeaders(selection.stream().map(LeaderCard::getLeaderId).toList()));
    }

    @Override
    public void on(Gui gui, UpdateLeadersHand event) {
        super.on(gui, event);
        // if (gui.isOffline()) {
        //     try {
        //         gui.setRoot(getClass().getResource("/assets/gui/setupresources.fxml"));
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // }
    }

    @Override
    public void on(Gui gui, UpdateAction event) {
        super.on(gui, event);
        if (event.getAction() != UpdateAction.ActionType.CHOOSE_LEADERS)
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");

        if(event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname())) {
            int choosable = gui.getViewModel().getLocalPlayerData().getSetup().getInitialResources();

            if (choosable > 0)
                gui.setRoot(getClass().getResource("/assets/gui/setupresources.fxml"));
        }
    }

    @Override
    public void on(Gui gui, UpdateSetupDone event) {
        super.on(gui, event);

        if (gui.getViewModel().getCurrentPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
        else {
            System.out.println(gui.getViewModel().getCurrentPlayer() + gui.getViewModel().getLocalPlayerNickname());
            gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));
        }
    }

    private void updateChoiceButton() {
        choiceButton.setDisable(selection.size() != Gui.getInstance().getViewModel().getLocalPlayerData().getSetup().getChosenLeadersCount());
    }
}
