package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedMarket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * GUI component representing the market.
 */
public class Market extends StackPane {
    ToggleGroup indexSelectors = new ToggleGroup();
    @FXML
    private ImageView background;
    @FXML
    private GridPane grid;
    private BiConsumer<Integer, Boolean> controllerListener;

    /**
     * Class constructor.
     */
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

    /**
     * @param f
     */
    public void setSelectionListener(BiConsumer<Integer, Boolean> f) {
        this.controllerListener = f;
    }

    /**
     * Sets and displays the market marbles.
     *
     * @param m the cached market state
     */
    public void setContent(ReducedMarket m) {
        double height = this.getPrefHeight(), // Height of market region
                width = this.getPrefWidth();

        int marketRows = m.getGrid().size() + 2,
                marketColumns = m.getGrid().get(0).size() + 3;
        int row = 0, col = 0;

        /* These are used for scaling, caps are arbitrary and looked good */
        double cellSide = Math.min(height / marketRows, width / marketColumns);

        RowConstraints rc = grid.getRowConstraints().remove(0);
        rc.setVgrow(Priority.ALWAYS);
        rc.setPercentHeight(100d / marketRows);
        ColumnConstraints cc = grid.getColumnConstraints().remove(0);
        cc.setHgrow(Priority.ALWAYS);
        cc.setPercentWidth(100d / marketColumns);


        ImageView slide = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(Resource.getMarblePath(m.getSlide())))));
        slide.setFitHeight(cellSide);
        slide.setFitWidth(cellSide);
        grid.add(slide, m.getGrid().get(0).size() + 1, 0);


        while (row < m.getGrid().size()) {
            List<String> mRow = m.getGrid().get(row);

            while (col < mRow.size()) {
                String sRes = mRow.get(col);

                ImageView r = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(Resource.getMarblePath(sRes)))));
                r.setFitHeight(cellSide);
                r.setFitWidth(cellSide);

                grid.add(r, col + 1, row + 1);

                col++;
            }

            row++;
            col = 0;
        }

        for (int i = 1; i < marketRows - 1; i++) {
            IndexRadioButton b = new IndexRadioButton(i - 1, true);
            b.setToggleGroup(indexSelectors);
            grid.add(b, marketColumns - 1, i);
        }
        for (int i = 1; i < marketColumns - 2; i++) {
            IndexRadioButton b = new IndexRadioButton(i - 1, false);
            b.setToggleGroup(indexSelectors);

            grid.add(b, i, marketRows - 1);
        }

        while (grid.getColumnConstraints().size() <= col)
            grid.getColumnConstraints().add(cc);

        while (grid.getRowConstraints().size() < row)
            grid.getRowConstraints().add(rc);


        indexSelectors.selectedToggleProperty().addListener((ov, oldToggle, newToggle) -> {
            if (indexSelectors.getSelectedToggle() != null)
                controllerListener.accept(((IndexRadioButton) newToggle).getIndex(), ((IndexRadioButton) newToggle).isRow());
        });
    }

    /**
     * Class that represents a radio button indexing one market row or column.
     */
    private static class IndexRadioButton extends RadioButton {
        private final boolean isRow;
        private final int index;

        public IndexRadioButton(int index, boolean isRow) {
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
