package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LeaderBox extends VBox {
    private final LeaderCard leaderCard;
    private final HBox buttonsContainer;
    private final Consumer<LeaderCard> onActivate;
    private final Consumer<LeaderCard> onDiscard;
    private final BiConsumer<LeaderCard, Production> onProduce;

    public LeaderBox(ReducedLeaderCard reducedLeaderCard, Consumer<LeaderCard> onActivate,
                     Consumer<LeaderCard> onDiscard, BiConsumer<LeaderCard, Production> onProduce, boolean allowProductions) {
        this.onActivate = onActivate;
        this.onDiscard = onDiscard;
        this.onProduce = onProduce;

        ViewModel vm = Gui.getInstance().getUi().getViewModel();

        leaderCard = new LeaderCard(reducedLeaderCard);
        switch (reducedLeaderCard.getLeaderType()) {
            case DEPOT -> leaderCard.setDepotContent(vm.getContainer(reducedLeaderCard.getContainerId()).orElseThrow(),
                    reducedLeaderCard.getResourceType(), false);
            case DISCOUNT -> leaderCard.setDiscount(reducedLeaderCard.getResourceType(), reducedLeaderCard.getDiscount());
            case PRODUCTION -> leaderCard.setProduction(vm.getProduction(reducedLeaderCard.getProduction()).orElseThrow());
            case ZERO -> leaderCard.setZeroReplacement(reducedLeaderCard.getResourceType());
        }
        getChildren().add(leaderCard);

        buttonsContainer = new HBox();
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setSpacing(20);
        setButtons(allowProductions);
        getChildren().add(buttonsContainer);

        this.setSpacing(7);
    }

    public LeaderCard getLeaderCard() {
        return leaderCard;
    }

    private void setButtons(boolean allowProductions) {
        if (!leaderCard.isActive()) {
            Button activate = new SButton("Activate");
            activate.setOnAction(event -> onActivate.accept(leaderCard));
            buttonsContainer.getChildren().add(activate);

            Button discard = new SButton("Discard");
            discard.setOnAction(event -> onDiscard.accept(leaderCard));
            buttonsContainer.getChildren().add(discard);
        } else {
            switch (leaderCard.getLeaderType()) {
                case PRODUCTION -> {
                    if (allowProductions) {
                        SButton activate = new SButton("Produce");
                        activate.setOnAction(event -> onProduce.accept(leaderCard, leaderCard.getProduction()));
                        buttonsContainer.getChildren().add(activate);
                    }
                }
            }
        }
    }

    public void refreshButtons(boolean allowProductions) {
        Platform.runLater(() -> {
            buttonsContainer.getChildren().clear();
            setButtons(allowProductions);
        });
    }
}
