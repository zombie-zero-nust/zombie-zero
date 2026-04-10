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

    /**
     * Gets the health state for sprite-based UI display
     * 0-33% = EMPTY, 34-66% = HALF, 67-100% = FULL
     */
    public HealthState getHealthState(int currentHealth)
    {
        int percentage = (currentHealth * 100) / MAX_HEALTH;
        if (percentage <= 33)
            return HealthState.EMPTY;
        else if (percentage <= 66)
            return HealthState.HALF;
        else
            return HealthState.FULL;
    }

    /**
     * Enum representing health states for sprite display
     */
    public enum HealthState
    {
        FULL("Hunger_Full.png"),
        HALF("Hunger_Half.png"),
        EMPTY("Hunger_Empty.png");

        private final String spriteFile;

        HealthState(String spriteFile)
        {
            this.spriteFile = spriteFile;
        }

        public String getSpriteFile()
        {
            return spriteFile;
        }
    }
}


