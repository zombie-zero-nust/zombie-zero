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
    private static final double BULLET_ICON_HEIGHT = 22;

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
        URL filledUrl = Resources.tryGetResource("scenes", "LevelScene", "ui", "bullet.png");
        URL emptyUrl = Resources.tryGetResource("scenes", "LevelScene", "ui", "bullet_empty.png");

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
        filledSlots = Math.clamp(filledSlots, 0, BULLET_SLOTS);
        for (int i = 0; i < BULLET_SLOTS; i++)
        {
            if (spritesReady)
            {
                bulletViews[i].setImage(i < filledSlots ? filledBulletImage : emptyBulletImage);
            }
        }
    }

    public void updateUI(Ammo ammoSystem, Label ammoLabel)
    {
        int displayedAmmo = ammoSystem.getCurrentAmmo();
        if (ammoSystem.isReloading())
        {
            int missingAmmo = ammoSystem.getMaxAmmo() - ammoSystem.getCurrentAmmo();
            displayedAmmo += (int) Math.round(missingAmmo * ammoSystem.getReloadProgress());
        }

        if (ammoLabel != null)
            ammoLabel.setText(displayedAmmo + " / " + ammoSystem.getMaxAmmo());

        updateAmmo(displayedAmmo);
    }
}


