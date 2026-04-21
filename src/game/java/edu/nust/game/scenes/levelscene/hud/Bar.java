package edu.nust.game.scenes.levelscene.hud;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Bar extends HBox
{
    protected static final int TOTAL_CELLS = 10;
    protected Rectangle[] cells;

    public Bar(int cellWidth, int cellHeight, int spacing)
    {
        super(spacing);
        this.setStyle("-fx-padding: 0; -fx-alignment: CENTER_LEFT;");
        cells = new Rectangle[TOTAL_CELLS];
        initializeCells(cellWidth, cellHeight);
    }

    private void initializeCells(int cellWidth, int cellHeight)
    {
        for (int i = 0; i < TOTAL_CELLS; i++)
        {
            Rectangle cell = new Rectangle(cellWidth, cellHeight);
            cell.setFill(getFilledColor());
            cell.setStroke(getFilledStrokeColor());
            cell.setStrokeWidth(1);
            cells[i] = cell;
            this.getChildren().add(cell);
        }
    }

    public void updateBar(int current, int max)
    {
        int filledCells = (current * TOTAL_CELLS) / max;
        for (int i = 0; i < TOTAL_CELLS; i++)
        {
            if (i < filledCells)
            {
                cells[i].setFill(getFilledColor());
                cells[i].setStroke(getFilledStrokeColor());
            }
            else
            {
                cells[i].setFill(getEmptyColor());
                cells[i].setStroke(getEmptyStrokeColor());
            }
        }
    }

    protected abstract Color getFilledColor();

    protected abstract Color getFilledStrokeColor();

    protected abstract Color getEmptyColor();

    protected abstract Color getEmptyStrokeColor();
}


