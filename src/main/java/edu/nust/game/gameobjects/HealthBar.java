package edu.nust.game.gameobjects;

import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class HealthBar extends Bar
{
    private static final int MAX_HEALTH = 100;
    private static final String HP_UI_BASE = "raw/PostApocalypse/UI/HP";

    private final ImageView heartImageView = new ImageView();
    private final ImageView hpFillImageView = new ImageView();
    private final ImageView hpFrameImageView = new ImageView();
    private final Rectangle hpFillClip = new Rectangle();

    private final Map<HealthState, Image> stateImages = new EnumMap<>(HealthState.class);

    private Image hpBarFrameImage;
    private Image hpFillImage;

    private double displayBarWidth;
    private double displayBarHeight;
    private double innerInsetX;
    private double innerInsetY;

    private boolean spritesReady;

    public HealthBar()
    {
        super(18, 12, 4);
        loadStateSprites();

        if (spritesReady)
        {
            this.getChildren().clear();
            buildSpriteUi();
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
            heartImageView.setImage(stateImages.get(state));
            updateHpFillClip(healthSystem.getCurrentHealth());
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
            URL url = Resources.tryGetResource("assets", HP_UI_BASE, state.getSpriteFile());
            if (url == null)
            {
                spritesReady = false;
                stateImages.clear();
                return;
            }
            stateImages.put(state, new Image(url.toExternalForm()));
        }

        URL barFrameUrl = Resources.tryGetResource("assets", HP_UI_BASE, "HP-Bar.png");
        URL barFillUrl = Resources.tryGetResource("assets", HP_UI_BASE, "HP.png");
        if (barFrameUrl == null || barFillUrl == null)
        {
            spritesReady = false;
            stateImages.clear();
            return;
        }

        hpBarFrameImage = new Image(barFrameUrl.toExternalForm());
        hpFillImage = new Image(barFillUrl.toExternalForm());

        spritesReady = true;
    }

    private void buildSpriteUi()
    {
        heartImageView.setPreserveRatio(true);
        heartImageView.setFitHeight(34);

        double targetBarHeight = 24;
        double scale = targetBarHeight / hpBarFrameImage.getHeight();
        displayBarWidth = hpBarFrameImage.getWidth() * scale;
        displayBarHeight = targetBarHeight;

        // Insets tuned so HP fill stays inside the bar's inner empty region.
        innerInsetX = Math.max(2.0, 5.0 * scale);
        innerInsetY = Math.max(1.0, 4.0 * scale);

        hpFrameImageView.setImage(hpBarFrameImage);
        hpFrameImageView.setPreserveRatio(false);
        hpFrameImageView.setFitWidth(displayBarWidth);
        hpFrameImageView.setFitHeight(displayBarHeight);

        hpFillImageView.setImage(hpFillImage);
        hpFillImageView.setPreserveRatio(false);
        hpFillImageView.setFitWidth(displayBarWidth);
        hpFillImageView.setFitHeight(displayBarHeight);

        hpFillImageView.setClip(hpFillClip);
        updateHpFillClip(MAX_HEALTH);

        StackPane hpBarStack = new StackPane(hpFillImageView, hpFrameImageView);
        HBox composite = new HBox(8, heartImageView, hpBarStack);
        this.getChildren().add(composite);
    }

    private void updateHpFillClip(int currentHealth)
    {
        double ratio = Math.max(0.0, Math.min(1.0, (double) currentHealth / MAX_HEALTH));
        double fillableWidth = Math.max(0.0, displayBarWidth - (innerInsetX * 2.0));
        double fillableHeight = Math.max(0.0, displayBarHeight - (innerInsetY * 2.0));

        hpFillClip.setX(innerInsetX);
        hpFillClip.setY(innerInsetY);
        hpFillClip.setWidth(fillableWidth * ratio);
        hpFillClip.setHeight(fillableHeight);
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
        FULL("Heart_Full.png"),
        HALF("Heart_Half.png"),
        EMPTY("Heart_Empty.png");

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


