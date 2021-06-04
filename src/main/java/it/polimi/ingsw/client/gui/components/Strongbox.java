package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

public class Strongbox extends StackPane {
    @FXML
    private ImageView background;
    @FXML
    private GridPane grid;

    private ReducedResourceContainer c;

    public Strongbox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/strongbox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.prefWidthProperty().addListener((o, old, newP) -> { if (c != null) setContent(c); });
        this.prefHeightProperty().addListener((o, old, newP) -> { if (c != null) setContent(c); });
    }

    public void setContent(ReducedResourceContainer c) {
        this.c = c;
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(5));
        // grid.setGridLinesVisible(true);
        // this.setBorder(new Border(new BorderStroke(Color.GREEN,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        // background.setBorder(new Border(new BorderStroke(Color.PINK,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        background.setFitWidth(this.getPrefWidth());
        background.setFitHeight(this.getPrefHeight());

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

        grid.getChildren().clear();
        
        while (i.hasNext()) {
            Entry<String, Integer> e = i.next();

            grid.add(new Cell(e.getKey(), e.getValue(), cellWidth, cellHeight, scaleRatio), col, row);

            if (row == sRows - 1) { // one more row than cols
                col++;
                row = 0;
            } else {
                row++;
            }
        }
    }

    private class Cell extends HBox {
        private int count;
        private String resource;

        public Cell(String resource, int count, double cellWidth, double cellHeight, double scaleRatio) {
            this.resource = resource;
            this.count = count;

            this.setSpacing(cellWidth * 0.2);
            this.setAlignment(Pos.CENTER);

        //    cell.setBorder(new Border(new BorderStroke(Color.RED,
        //        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

            Resource r = new Resource();
            r.setResourceType(resource);
            r.setFitHeight(cellHeight * 0.8);

            Text t = new Text(String.valueOf(count));
            t.setScaleX(scaleRatio);
            t.setScaleY(scaleRatio);

            this.getChildren().add(t);
            this.getChildren().add(r);
        }

        public String getResource() {
            return resource;
        }
        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
            ((Text)this.getChildren().get(0)).setText(String.valueOf(count));
        }
    }

    public void refreshRemove(String resource) {
        Cell cell = grid.getChildren().stream().map(n -> (Cell)n).filter(c -> c.getResource().equals(resource)).findAny().orElseThrow();
        cell.setCount(cell.getCount() - 1);
    }
}
