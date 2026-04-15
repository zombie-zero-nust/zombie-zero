package edu.nust.game.gameobjects;

import edu.nust.game.gameobjects.interfaces.Ammo;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

public class AmmoBar extends Bar
{
    private static final int BULLETS_PER_CELL = 2;
    private static final int MAX_AMMO = TOTAL_CELLS * BULLETS_PER_CELL;

    public AmmoBar()
    {
        super(18, 12, 4);
    }

    @Override
    protected Color getFilledColor()
    {
        return Color.web("#00FF00");
    }

    @Override
    protected Color getFilledStrokeColor()
    {
        return Color.web("#00AA00");
    }

    @Override
    protected Color getEmptyColor()
    {
        return Color.web("#1A1A1A");
    }

    @Override
    protected Color getEmptyStrokeColor()
    {
        return Color.web("#333333");
    }

    public void updateAmmo(int currentAmmo)
    {
        int filledCells = (currentAmmo + BULLETS_PER_CELL - 1) / BULLETS_PER_CELL;
        for (int i = 0; i < TOTAL_CELLS; i++)
        {
            cells[i].setFill(i < filledCells ? getFilledColor() : getEmptyColor());
            cells[i].setStroke(i < filledCells ? getFilledStrokeColor() : getEmptyStrokeColor());
        }
    }

    public void updateUI(Ammo ammoSystem, Label ammoLabel, Label reloadLabel)
    {
        if (ammoLabel != null)
            ammoLabel.setText(ammoSystem.getCurrentAmmo() + " / 20");

        updateAmmo(ammoSystem.getCurrentAmmo());

        if (reloadLabel != null)
        {
            if (ammoSystem.isReloading())
            {
                reloadLabel.setText(String.format("RELOADING: %.1fs", ammoSystem.getReloadTimeRemaining()));
                reloadLabel.setStyle("-fx-text-fill: #FF6600; -fx-font-size: 11; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
                reloadLabel.setVisible(true);
                reloadLabel.setManaged(true);
            }
            else
            {
                reloadLabel.setVisible(false);
                reloadLabel.setManaged(false);
            }
        }
    }
}


