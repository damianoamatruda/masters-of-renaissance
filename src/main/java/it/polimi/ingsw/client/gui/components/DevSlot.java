package it.polimi.ingsw.client.gui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class DevSlot extends StackPane {
    // @FXML private GridPane grid;
    private List<DevelopmentCard> cards;

    public DevSlot() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/devslot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setDevCards(List<DevelopmentCard> cards) {
        this.cards = cards;

        // double topPaddingPercent = 0.122,
        //        bottomPaddingPercent = 0.095;
        // this.setPadding(new Insets(this.getHeight() * topPaddingPercent, 0, this.getHeight() * bottomPaddingPercent, 0));

        for (int i = 0; i < cards.size(); i++) {
            // grid.add(cards.get(i), 0, 3 - i);
            DevelopmentCard c = cards.get(i);

            AnchorPane p = new AnchorPane();

            p.getChildren().add(c);
            
            AnchorPane.setBottomAnchor(c, 20d + 50 * i);

            this.getChildren().add(p);
        }
        

        // grid.getChildren().forEach(c -> {
        //     c.setScaleX(this.getWidth());
        // });
    }
}
