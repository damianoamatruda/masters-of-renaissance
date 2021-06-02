package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import it.polimi.ingsw.common.reducedmodel.ReducedMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    @FXML private ImageView background;
    @FXML private GridPane grid;

    private BiConsumer<Integer, Boolean> controllerListener;

    ToggleGroup indexSelectors = new ToggleGroup();

    private int selectedIndex = 0;
    private boolean isColumnSelected = false;

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

    public void setSelectionListener(BiConsumer<Integer, Boolean> f) {
        this.controllerListener = f;
    }

    public void setContent(ReducedMarket m) {
        // this.setBorder(new Border(new BorderStroke(Color.RED, 
        //         BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        grid.setGridLinesVisible(true);
        double height = this.getPrefHeight(), // height of market region
               width = this.getPrefWidth();

        int marketRows = m.getGrid().size() + 2,
            marketColumns = m.getGrid().get(0).size() + 3;
        int row = 0, col = 0;
        
        // these are used for scalin, caps are arbitrary and looked good on my machine
        double cellSide = Math.min(height / marketRows, width / marketColumns);
        
        RowConstraints rc = grid.getRowConstraints().remove(0);
        rc.setVgrow(Priority.ALWAYS);
        rc.setPercentHeight(100d/marketRows);
        ColumnConstraints cc = grid.getColumnConstraints().remove(0);
        cc.setHgrow(Priority.ALWAYS);
        cc.setPercentWidth(100d/marketColumns);
        
        
        ImageView slide = new ImageView();
        slide.setImage(new Image(Resource.getMarblePath(m.getSlide())));
        slide.setFitHeight(cellSide);
        slide.setFitWidth(cellSide);
        grid.add(slide, m.getGrid().get(0).size() + 1, 0);

        
        while (row < m.getGrid().size()) {
            List<String> mRow = m.getGrid().get(row).stream().map(ReducedResourceType::getName).toList();

            while (col < mRow.size()) {
                String sRes = mRow.get(col);

                ImageView r = new ImageView();
                r.setImage(new Image(Resource.getMarblePath(sRes)));
                r.setFitHeight(cellSide);
                r.setFitWidth(cellSide);

                grid.add(r, col + 1, row + 1);

                col++;
            }
            
            row++;
            col = 0;
        }

        for (int i = 1; i < marketRows - 1; i++) {
            IndexRadioButton b = new IndexRadioButton(String.valueOf(i), i - 1, true);
            b.setToggleGroup(indexSelectors);
            // TODO scale text (see Strongbox)
            grid.add(b, marketColumns - 1, i);
        }
        for (int i = 1; i < marketColumns - 2; i++) {
            IndexRadioButton b = new IndexRadioButton(String.valueOf(i), i - 1, false);
            b.setToggleGroup(indexSelectors);

            grid.add(b, i, marketRows - 1);
        }

        while (grid.getColumnConstraints().size() <= col)
            grid.getColumnConstraints().add(cc);

        while (grid.getRowConstraints().size() < row)
            grid.getRowConstraints().add(rc);


        indexSelectors.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
                if (indexSelectors.getSelectedToggle() != null)
                    controllerListener.accept(((IndexRadioButton)newToggle).getIndex(), ((IndexRadioButton)newToggle).isRow());
            }
        });
    }

    private class IndexRadioButton extends RadioButton {
        private boolean isRow;
        private int index;
        
        public IndexRadioButton(String label, int index, boolean isRow) {
            super(label);
            this.index = index;
            this.isRow = isRow;
        }

        public int getIndex() {
            return index;
        }

        public boolean isRow() {
            return isRow;
        }
    }
}
