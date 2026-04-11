package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class Weapon extends GameObject
{
    private static final double WEAPON_OFFSET = 30;
    private final double fireRate = 10;
    private static final double MUZZLE_FRAME_DURATION = 0.04;
    private static final int MUZZLE_FRAMES = 3;
    private double width = 54;
    private double height= 18;


    private String preDirection;

    private SpriteRenderer muzzleFlashRenderer;
    private boolean isFiring;
    private boolean autoFire;
    private double fireCooldown = 0;
    private double muzzleElapsed;
    private boolean muzzlePlaying;
    private Ammo ammo;

    private Image weaponSprite;
    private Image fireUpSheet;
    private Image fireDownSheet;
    private Image fireRightSheet;
    private Image fireLeftSheet;

    public Weapon()
    {
        try
        {
            weaponSprite = Resources.loadImageOrThrow(
                    "assets", "raw", "PostApocalypse", "Objects", "Pickable", "Gun.png"
            );
            this.addComponent(new SpriteRenderer(width, height, weaponSprite));
        }
        catch (FileNotFoundException e)
        {
            this.addComponent(new edu.nust.engine.core.components.renderers.BoxRenderer(36, 18, javafx.scene.paint.Color.CYAN));
        }

        loadMuzzleFireAssets();

        isFiring = false;
        autoFire = true;
        fireCooldown = 0;
        muzzleElapsed = 0;
        muzzlePlaying = false;
        ammo = new AmmoImpl();
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (!muzzlePlaying || muzzleFlashRenderer == null)
            return;

        muzzleElapsed += deltaTime.asSeconds();
        int frame = (int) (muzzleElapsed / MUZZLE_FRAME_DURATION);

        if (frame >= MUZZLE_FRAMES)
        {
            muzzlePlaying = false;
            muzzleFlashRenderer.setVisible(false);
            return;
        }

        muzzleFlashRenderer.setFrame(frame, 0);
    }

    //manually rendering the muzzle
    @Override
    public void onRender(GraphicsContext context)
    {
        if (!muzzlePlaying || muzzleFlashRenderer == null)
            return;

        double rotation = this.getTransform().getRotation().getRadians();
        Vector2D weaponPos = this.getTransform().getPosition();
        Vector2D barrelDirection = new Vector2D(Math.cos(rotation), Math.sin(rotation));
        Vector2D barrelTip = weaponPos.add(barrelDirection.multiply(width));

        double w = muzzleFlashRenderer.getWidth();
        double h = muzzleFlashRenderer.getHeight();

        double sx = muzzleFlashRenderer.getFrameX() * muzzleFlashRenderer.getFrameWidth();
        double sy = muzzleFlashRenderer.getFrameY() * muzzleFlashRenderer.getFrameHeight();

        context.save();
        context.translate(barrelTip.getX(), barrelTip.getY());
        context.rotate(this.getTransform().getRotation().getDegrees());
        context.drawImage(
                muzzleFlashRenderer.getImage(),
                sx, sy,
                muzzleFlashRenderer.getFrameWidth(),
                muzzleFlashRenderer.getFrameHeight(),
                -w / 2 -2, -h / 2 -2,
                w, h
        );
        context.restore();
    }

    public void updatePosition(Vector2D mousePos, Vector2D playerPos)
    {
        Vector2D delta = mousePos.subtract(playerPos);
        double dx = delta.getX();
        double dy = delta.getY();
        double rotation;
        Vector2D orbitingDistance = delta.normalize().multiply(30);
        String currDirection;
        if (dx == 0)
        {
            if(dy > 0) rotation = Math.PI/2;
            else rotation = -Math.PI/2;
        }
        else
        {
            rotation = Math.atan2(dy,dx);
        }

        SpriteRenderer spriteRenderer = this.getFirstComponent(SpriteRenderer.class);
        if(spriteRenderer!= null) {
            if (rotation > Math.PI / 2 || rotation < -Math.PI / 2) {
                spriteRenderer.flipVertical();
            } else {
                spriteRenderer.unFlipVertical();
            }
        }
        this.getTransform().setPosition(playerPos.add(orbitingDistance));
        this.getTransform().setRotationRadians(rotation);

    }

    public Bullet fireWeapon(Vector2D targetPos, TimeSpan deltaTime)
    {
        Vector2D weaponPos = this.getTransform().getPosition();
        Vector2D bulletPos = weaponPos.add(targetPos.subtract(weaponPos).normalize().multiply(width));
        ammo.update(deltaTime);


        if (ammo.isReloading() || !ammo.hasAmmo())
            return null;

        if(!isFiring)
            return null;

        if(!autoFire)
        {
            setFiring(false);
            ammo.decreaseAmmo();
            triggerMuzzleFlash(bulletPos, targetPos);
            return new Bullet(2000, bulletPos, 1000, 10, 30, targetPos);
        }

        fireCooldown -= deltaTime.asSeconds();
        if(fireCooldown <= 0.01)
        {
            fireCooldown = 1.0 / fireRate;
            ammo.decreaseAmmo();
            triggerMuzzleFlash(weaponPos, targetPos);
            return new Bullet(2000, bulletPos, 1000, 30, 30, targetPos);
        }

        return null;
    }

    public void setFiring(boolean isFiring)
    {
        this.isFiring = isFiring;
    }

    public Vector2D getWeaponPosition()
    {
        return this.getTransform().getPosition();
    }

    public Ammo getAmmo()
    {
        return ammo;
    }

    public void reload()
    {
        ammo.startReload();
    }

    private void loadMuzzleFireAssets()
    {
        try
        {
            fireUpSheet = Resources.loadImageOrThrow(
                    "assets", "raw", "PostApocalypse", "Character", "Guns", "Fire", "Fire_Up-Sheet3.png"
            );
            fireDownSheet = Resources.loadImageOrThrow(
                    "assets", "raw", "PostApocalypse", "Character", "Guns", "Fire", "Fire_Down-Sheet3.png"
            );
            fireRightSheet = Resources.loadImageOrThrow(
                    "assets", "raw", "PostApocalypse", "Character", "Guns", "Fire", "Fire_side-Sheet3.png"
            );
            fireLeftSheet = Resources.loadImageOrThrow(
                    "assets", "raw", "PostApocalypse", "Character", "Guns", "Fire", "Fire_side-left-Sheet3.png"
            );

            muzzleFlashRenderer = new SpriteRenderer(44, 44, fireRightSheet, MUZZLE_FRAMES, 1);
            muzzleFlashRenderer.setVisible(false);
        }
        catch (FileNotFoundException e)
        {
            muzzleFlashRenderer = null;
            System.out.println("[WARN] Failed to load muzzle fire assets: " + e.getMessage());
        }
    }

    private void triggerMuzzleFlash(Vector2D weaponPos, Vector2D targetPos)
    {
        if (muzzleFlashRenderer == null)
            return;

        Vector2D direction = targetPos.subtract(weaponPos);
        Image directionalSheet = getDirectionalFireSheet(direction);
        if (directionalSheet == null)
            return;

        muzzleFlashRenderer.setImage(directionalSheet, MUZZLE_FRAMES, 1);
        muzzleFlashRenderer.setFrame(0, 0);
        muzzleFlashRenderer.setVisible(true);

        muzzleElapsed = 0;
        muzzlePlaying = true;
    }

    private Image getDirectionalFireSheet(Vector2D direction)
    {
        double absX = Math.abs(direction.getX());
        double absY = Math.abs(direction.getY());

        if (absX >= absY)
            return direction.getX() >= 0 ? fireRightSheet : fireLeftSheet;

        return direction.getY() >= 0 ? fireDownSheet : fireUpSheet;
    }
}