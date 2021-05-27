package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Warehouse extends VBox {
    int maxRowHeight;

    public Warehouse() {
        maxRowHeight = 100; //TODO parameterize
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/warehouse.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setWarehouseShelves(List<ReducedResourceContainer> shelves) {
        if(shelves != null) {
            for (ReducedResourceContainer shelf : shelves) {
                HBox content = new HBox();
                content.setPrefHeight(100);
                content.setPrefWidth(300);
                content.setStyle("-fx-background-image: url('/assets/gui/playerboard/warehouseshelf.png');" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: stretch;" +
                        "-fx-opacity: 1;" +
                        "-fx-background-size: 300 100;");
                for(String resource : shelf.getContent().keySet()) {
                    HBox entry = new HBox();



//                    Text l = new Text(shelf.getContent().get(resource) + "");
//
//                    entry.getChildren().add(l);
                    for(int i = 0; i < shelf.getContent().get(resource); i++) {
                        Resource r = new Resource();
                        r.setResourceType(resource);
                        entry.getChildren().add(r);
                        r.setPreserveRatio(true);
                    }

                    entry.setAlignment(Pos.CENTER);


//                    l.maxHeight(maxRowHeight);

                    entry.maxHeight(maxRowHeight);

                    entry.setBorder(new Border(new BorderStroke(Color.BLACK,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                    content.getChildren().add(entry);
                }
                this.getChildren().addAll(content);
            }
        }
    }
}
