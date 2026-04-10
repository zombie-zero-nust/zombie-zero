package edu.nust.game.gameobjects;

import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class HealthBar extends Bar
{
    private static final int MAX_HEALTH = 100;
    private static final String HUNGER_UI_BASE = "raw/PostApocalypse/UI/Hunger";

    private final ImageView stateImageView = new ImageView();
    private final Map<HealthState, Image> stateImages = new EnumMap<>(HealthState.class);
    private boolean spritesReady;

    public HealthBar()
    {
        super(18, 12, 4);
        loadStateSprites();

        if (spritesReady)
        {
            this.getChildren().clear();
            stateImageView.setPreserveRatio(true);
            stateImageView.setFitHeight(24);
            this.getChildren().add(stateImageView);
        }
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

        if (spritesReady)
        {
            HealthState state = getHealthState(healthSystem.getCurrentHealth());
            stateImageView.setImage(stateImages.get(state));
        }
        else
        {
            updateBar(healthSystem.getCurrentHealth(), MAX_HEALTH);
        }
    }

    private void loadStateSprites()
    {
        for (HealthState state : HealthState.values())
        {
            URL url = Resources.tryGetResource("assets", HUNGER_UI_BASE, state.getSpriteFile());
            if (url == null)
            {
                spritesReady = false;
                stateImages.clear();
                return;
            }
            stateImages.put(state, new Image(url.toExternalForm()));
        }
        spritesReady = true;
    }

    /**
     * Gets the health state for sprite-based UI display 0-33% = EMPTY, 34-66% = HALF, 67-100% = FULL
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


