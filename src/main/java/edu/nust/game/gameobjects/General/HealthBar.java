package edu.nust.game.gameobjects.General;

import edu.nust.engine.resources.Resources;
import edu.nust.game.gameobjects.interfaces.Health;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

import java.net.URL;

public class HealthBar extends Bar
{
    private static final int MAX_HEALTH = 100;
    private static final String HP_UI_BASE = "raw/PostApocalypse/UI/HP";

    private final ImageView hpFillImageView = new ImageView();
    private final ImageView hpFrameImageView = new ImageView();
    private final Rectangle hpFillClip = new Rectangle();
    private final Rectangle hpEmptyBackgroundRect = new Rectangle();
    private final Pane hpInnerPane = new Pane();

    private Image hpBarFrameImage;
    private Image hpFillImage;

    private double displayBarWidth;
    private double displayBarHeight;
    private double innerInsetLeft;
    private double innerInsetRight;
    private double innerInsetTop;
    private double innerInsetBottom;
    private double innerWidth;
    private double innerHeight;

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
        // Keep health numeric value internal; UI shows only the visual HP bar.

        if (spritesReady)
        {
            updateHpFillClip(healthSystem.getCurrentHealth());
        }
        else
        {
            updateBar(healthSystem.getCurrentHealth(), MAX_HEALTH);
        }
    }

    private void loadStateSprites()
    {
        URL barFrameUrl = Resources.tryGetResource("assets", HP_UI_BASE, "HP-Bar.png");
        URL barFillUrl = Resources.tryGetResource("assets", HP_UI_BASE, "HP.png");
        if (barFrameUrl == null || barFillUrl == null)
        {
            spritesReady = false;
            return;
        }

        hpBarFrameImage = new Image(barFrameUrl.toExternalForm());
        hpFillImage = new Image(barFillUrl.toExternalForm());

        spritesReady = true;
    }

    private void buildSpriteUi()
    {
        double targetBarHeight = 40;
        double scale = targetBarHeight / hpBarFrameImage.getHeight();
        displayBarWidth = hpBarFrameImage.getWidth() * scale;
        displayBarHeight = targetBarHeight;

        // Insets tuned to match HP-Bar interior bounds.
        innerInsetLeft = Math.max(2.0, 5.0 * scale);
        innerInsetRight = Math.max(1.0, 2.5 * scale);
        innerInsetTop = Math.max(1.0, 4.0 * scale);
        innerInsetBottom = Math.max(1.0, 4.0 * scale);

        innerWidth = Math.max(0.0, displayBarWidth - innerInsetLeft - innerInsetRight);
        innerHeight = Math.max(0.0, displayBarHeight - innerInsetTop - innerInsetBottom);

        hpFrameImageView.setImage(hpBarFrameImage);
        hpFrameImageView.setPreserveRatio(false);
        hpFrameImageView.setFitWidth(displayBarWidth);
        hpFrameImageView.setFitHeight(displayBarHeight);

        hpFillImageView.setImage(hpFillImage);
        hpFillImageView.setPreserveRatio(false);
        hpFillImageView.setFitWidth(innerWidth);
        hpFillImageView.setFitHeight(innerHeight);
        hpFillImageView.setLayoutX(0);
        hpFillImageView.setLayoutY(0);

        hpEmptyBackgroundRect.setWidth(innerWidth);
        hpEmptyBackgroundRect.setHeight(innerHeight);
        hpEmptyBackgroundRect.setFill(Color.web("#0A0A0A"));
        hpEmptyBackgroundRect.setLayoutX(0);
        hpEmptyBackgroundRect.setLayoutY(0);

        hpInnerPane.setPrefSize(innerWidth, innerHeight);
        hpInnerPane.setMinSize(innerWidth, innerHeight);
        hpInnerPane.setMaxSize(innerWidth, innerHeight);
        hpInnerPane.getChildren().setAll(hpEmptyBackgroundRect, hpFillImageView);

        hpFillImageView.setClip(hpFillClip);
        updateHpFillClip(MAX_HEALTH);

        StackPane hpBarStack = new StackPane(hpInnerPane, hpFrameImageView);
        this.getChildren().add(hpBarStack);
    }

    private void updateHpFillClip(int currentHealth)
    {
        double ratio = Math.max(0.0, Math.min(1.0, (double) currentHealth / MAX_HEALTH));
        double clippedWidth = Math.floor(innerWidth * ratio);
        hpFillClip.setX(0);
        hpFillClip.setY(0);
        hpFillClip.setWidth(clippedWidth);
        hpFillClip.setHeight(innerHeight);
    }

}


