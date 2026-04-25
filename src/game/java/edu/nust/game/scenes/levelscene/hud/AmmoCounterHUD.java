package edu.nust.game.scenes.levelscene.hud;

import edu.nust.engine.resources.Resources;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;

/**
 * Pixel-Art Ammo Counter HUD Component
 *
 * A custom JavaFX component that displays ammo count with pixel-art styling.
 * Features:
 * - Gray background panel image
 * - Weapon silhouette image (top half)
 * - Bottom section with bullet icons (HBox) and numerical ammo count label
 * - Dynamic updateAmmo(int current, int max) method
 * - Sharp pixel-art rendering with -fx-image-smoothing: false
 * - 2px spacing between bullet icons
 * - Pixel-style font for ammo count label
 */
public class AmmoCounterHUD extends StackPane
{
    private static final String BULLET_UI_BASE = "raw/PostApocalypse/UI/BulletIndicators";
    private static final double BULLET_ICON_HEIGHT = 42;
    private static final double BULLET_SPACING = 2;
    private static final int MAX_BULLET_SLOTS = 5;

    private final ImageView backgroundPanel = new ImageView();
    private final ImageView weaponSilhouette = new ImageView();
    private final HBox bulletContainer = new HBox(BULLET_SPACING);
    private final Label ammoCountLabel = new Label();
    private final ImageView[] bulletViews = new ImageView[MAX_BULLET_SLOTS];

    private Image filledBulletImage;
    private Image emptyBulletImage;
    private boolean spritesReady;

    public AmmoCounterHUD()
    {
        loadBulletSprites();
        initializeLayout();
        updateAmmo(MAX_BULLET_SLOTS, MAX_BULLET_SLOTS);
    }

    /**
     * Load bullet sprites from resources
     */
    private void loadBulletSprites()
    {
        URL filledUrl = Resources.tryGetResource("assets", BULLET_UI_BASE, "Gun-Bullet.png");
        URL emptyUrl = Resources.tryGetResource("assets", BULLET_UI_BASE, "Gun-Bullet_Empty.png");

        if (filledUrl == null || emptyUrl == null)
        {
            spritesReady = false;
            return;
        }

        filledBulletImage = new Image(filledUrl.toExternalForm());
        emptyBulletImage = new Image(emptyUrl.toExternalForm());
        spritesReady = true;
    }

    /**
     * Initialize the layout structure
     */
    private void initializeLayout()
    {
        // Configure background panel
        backgroundPanel.setStyle("-fx-image-smoothing: false;");
        backgroundPanel.setPreserveRatio(false);

        // Configure weapon silhouette
        weaponSilhouette.setStyle("-fx-image-smoothing: false;");
        weaponSilhouette.setPreserveRatio(true);

        // Configure bullet container (HBox for individual bullet icons)
        bulletContainer.setStyle("-fx-padding: 0; -fx-alignment: CENTER_LEFT;");

        // Initialize bullet slots
        initializeBulletSlots();

        // Configure ammo count label with pixel-style font
        ammoCountLabel.setStyle(
                "-fx-text-fill: #FFFFFF; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-padding: 4;"
        );
        ammoCountLabel.setText("0 / 0");

        // Build layout structure
        VBox topSection = new VBox();
        topSection.setStyle("-fx-padding: 0;");
        if (weaponSilhouette.getImage() != null)
        {
            topSection.getChildren().add(weaponSilhouette);
        }

        VBox bottomSection = new VBox(4);
        bottomSection.setStyle("-fx-padding: 4; -fx-alignment: TOP_LEFT;");
        bottomSection.getChildren().addAll(bulletContainer, ammoCountLabel);

        VBox mainContent = new VBox();
        mainContent.setStyle("-fx-padding: 0;");
        mainContent.getChildren().addAll(topSection, bottomSection);
        VBox.setVgrow(topSection, Priority.ALWAYS);

        this.getChildren().addAll(backgroundPanel, mainContent);
    }

    /**
     * Initialize bullet ImageView slots
     */
    private void initializeBulletSlots()
    {
        for (int i = 0; i < MAX_BULLET_SLOTS; i++)
        {
            ImageView bulletView = new ImageView();
            bulletView.setStyle("-fx-image-smoothing: false;");
            bulletView.setPreserveRatio(true);
            bulletView.setFitHeight(BULLET_ICON_HEIGHT);

            if (spritesReady)
            {
                bulletView.setImage(emptyBulletImage);
            }

            bulletViews[i] = bulletView;
            bulletContainer.getChildren().add(bulletView);
        }
    }

    /**
     * Update the ammo display with current and max ammo counts
     *
     * @param current current ammo count
     * @param max     maximum ammo count
     */
    public void updateAmmo(int current, int max)
    {
        // Clamp current to valid range [0, MAX_BULLET_SLOTS]
        int filledSlots = Math.max(0, Math.min(current, MAX_BULLET_SLOTS));

        // Update bullet icons
        for (int i = 0; i < MAX_BULLET_SLOTS; i++)
        {
            if (spritesReady)
            {
                bulletViews[i].setImage(i < filledSlots ? filledBulletImage : emptyBulletImage);
            }
        }

        // Update ammo count label
        ammoCountLabel.setText(current + " / " + max);
    }

    /**
     * Get the ammo count label for direct styling if needed
     *
     * @return the ammo count label
     */
    public Label getAmmoCountLabel()
    {
        return ammoCountLabel;
    }

    /**
     * Get the bullet container HBox for direct manipulation if needed
     *
     * @return the bullet container HBox
     */
    public HBox getBulletContainer()
    {
        return bulletContainer;
    }

    /**
     * Check if sprites are ready
     *
     * @return true if sprites loaded successfully
     */
    public boolean isSpritesReady()
    {
        return spritesReady;
    }
}
