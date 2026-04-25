package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.resources.Resources;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;

public class AmmoBar extends HBox
{
    private static final int BULLET_SLOTS = 10;
    private static final String BULLET_UI_BASE = "raw/PostApocalypse/UI/BulletIndicators";
    private static final double BULLET_ICON_HEIGHT = 42;

    private final ImageView[] bulletViews = new ImageView[BULLET_SLOTS];

    private Image filledBulletImage;
    private Image emptyBulletImage;
    private boolean spritesReady;

    public AmmoBar()
    {
        super(6);
        this.setStyle("-fx-padding: 0; -fx-alignment: CENTER_LEFT;");

        loadBulletSprites();
        initializeBulletSlots();
        updateAmmo(BULLET_SLOTS);
    }

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

    private void initializeBulletSlots()
    {
        for (int i = 0; i < BULLET_SLOTS; i++)
        {
            ImageView bulletView = new ImageView();
            bulletView.setPreserveRatio(true);
            bulletView.setSmooth(false);
            bulletView.setFitHeight(BULLET_ICON_HEIGHT);

            if (spritesReady)
            {
                bulletView.setImage(filledBulletImage);
            }

            bulletViews[i] = bulletView;
            this.getChildren().add(bulletView);
        }
    }

    public void updateAmmo(int currentAmmo)
    {
        // Math.clamp is not available on all Java versions; use explicit clamp
        int filledSlots = (int) Math.ceil(currentAmmo / 3.0);
        filledSlots = Math.max(0, Math.min(filledSlots, BULLET_SLOTS));;
        for (int i = 0; i < BULLET_SLOTS; i++)
        {
            if (spritesReady)
            {
                bulletViews[i].setImage(i < filledSlots ? filledBulletImage : emptyBulletImage);
            }
        }
    }

    public void updateUI(Ammo ammoSystem, Label ammoLabel, Label reloadLabel)
    {
        if (ammoLabel != null)
            ammoLabel.setText(ammoSystem.getCurrentAmmo() + " / " + ammoSystem.getMaxAmmo());

        // If reloading, show all bullets as filled during the reload animation
        if (ammoSystem.isReloading())
        {
            updateAmmo(BULLET_SLOTS);
        }

        else
        {
            updateAmmo(ammoSystem.getCurrentAmmo());
        }

        if (reloadLabel != null)
        {
            if (ammoSystem.isReloading())
            {
                reloadLabel.setText(String.format("RELOADING: %.1fs", ammoSystem.getReloadTimeRemaining()));
                reloadLabel.setStyle(
                        "-fx-text-fill: #FF6600; -fx-font-size: 11; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
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


