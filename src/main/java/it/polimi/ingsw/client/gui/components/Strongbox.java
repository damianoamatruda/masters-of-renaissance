package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Strongbox extends StackPane {
    @FXML ImageView background;
    @FXML GridPane grid;

    public Strongbox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/strongbox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setContent(ReducedResourceContainer c) {
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(5));
        // grid.setGridLinesVisible(true);
        // this.setBorder(new Border(new BorderStroke(Color.GREEN,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        // background.setBorder(new Border(new BorderStroke(Color.PINK,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // background.setFitWidth(this.getPrefWidth());
        // background.setFitHeight(this.getPrefHeight());

        double height = this.getPrefHeight(), // height of strongbox region
               width = this.getPrefWidth();

        double sqroot = Math.sqrt(c.getContent().size() + 1);

        int row = 0, col = 0,
            sRows = (int) Math.ceil(sqroot),
            sCols = (int) Math.floor(sqroot);
        
        // these are used for scalin, caps are arbitrary and looked good on my machine
        double defTextHeight = 20, // got this by trying (it's how big Text appears with no scaling)
               cellHeight = height / sRows,
               cellWidth = width / sCols,
               scaleRatio = 0.8 * (cellHeight / defTextHeight);
        
        Iterator<Entry<String, Integer>> i = c.getContent().entrySet().iterator();

        RowConstraints rc = grid.getRowConstraints().remove(0);
        // rc.setVgrow(Priority.ALWAYS);
        rc.setPercentHeight(100d/sRows);
        ColumnConstraints cc = grid.getColumnConstraints().remove(0);
        // cc.setHgrow(Priority.ALWAYS);
        cc.setPercentWidth(100d/sCols);

        while (grid.getColumnConstraints().size() < sCols)
            grid.getColumnConstraints().add(cc);
        while (grid.getRowConstraints().size() < sRows)
            grid.getRowConstraints().add(rc);

        while (i.hasNext()) {
            Entry<String, Integer> e = i.next();

            HBox cell = new HBox(cellWidth * 0.2);
            cell.setAlignment(Pos.CENTER);

        //    cell.setBorder(new Border(new BorderStroke(Color.RED,
        //        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

            Resource r = new Resource();
            r.setResourceType(e.getKey());
            r.setFitHeight(cellHeight);

            Text t = new Text(String.valueOf(e.getValue()));
            t.setScaleX(scaleRatio);
            t.setScaleY(scaleRatio);

            cell.getChildren().add(t);
            cell.getChildren().add(r);
            
            grid.add(cell, col, row);

            if (row == sRows - 1) { // one more row than cols
                col++;
                row = 0;
            } else {
                row++;
            }
        }
    }
}
