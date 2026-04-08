package edu.nust.game.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.control.Label;

public class HealthBar extends Bar
{
    private static final int MAX_HEALTH = 100;

    public HealthBar()
    {
        super(18, 12, 4);
    }

    @Override
    protected Color getFilledColor()
    {
        return Color.web("#FF0000");
    }

    @Override
    protected Color getFilledStrokeColor()
    {
        return Color.web("#AA0000");
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

    public void updateUI(Health healthSystem, Label healthLabel)
    {
        if (healthLabel != null)
            healthLabel.setText(healthSystem.getCurrentHealth() + " / 100");

        updateBar(healthSystem.getCurrentHealth(), MAX_HEALTH);
    }
}


