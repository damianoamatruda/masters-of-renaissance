package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.polimi.ingsw.common.reducedmodel.ReducedMarket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class Market extends StackPane {
    @FXML ImageView background;
    @FXML GridPane grid;

    public Market() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/market.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setContent(ReducedMarket m) {
        // this.setBorder(new Border(new BorderStroke(Color.RED, 
        //         BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        // grid.setGridLinesVisible(true);
        double height = this.getPrefHeight(), // height of market region
               width = this.getPrefWidth();

        int marketRows = m.getGrid().size() + 1,
            marketColumns = m.getGrid().get(0).size() + 2;
        int row = 0, col = 0;
        
        // these are used for scalin, caps are arbitrary and looked good on my machine
        double cellSide = Math.min(height / marketRows, width / marketColumns);
        
        RowConstraints rc = grid.getRowConstraints().get(0);
        rc.setVgrow(Priority.ALWAYS);
        rc.setPercentHeight(100d/marketRows);
        ColumnConstraints cc = grid.getColumnConstraints().get(0);
        cc.setHgrow(Priority.ALWAYS);
        cc.setPercentWidth(100d/marketColumns);
        
        
        Resource slide = new Resource();
        slide.setResourceType(m.getSlide());
        slide.setFitHeight(cellSide);
        slide.setFitWidth(cellSide);
        grid.add(slide, m.getGrid().get(0).size() + 1, 0);

        
        while (row < m.getGrid().size()) {
            List<String> mRow = m.getGrid().get(row);

            while (col < mRow.size()) {
                // StackPane sp = new StackPane();
                // ImageView cellBG = new ImageView(url)

                // sp.getChildren().add();

                String sRes = mRow.get(col);

                Resource r = new Resource();
                r.setResourceType(sRes);
                r.setFitHeight(cellSide);
                r.setFitWidth(cellSide);

                grid.add(r, col + 1, row + 1);

                col++;
            }
            
            row++;
            col = 0;
        }

        while (grid.getColumnConstraints().size() <= col)
            grid.getColumnConstraints().add(cc);

        while (grid.getRowConstraints().size() < row)
            grid.getRowConstraints().add(rc);
    }
}
