package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class Weapon extends GameObject
{
    private static final double WEAPON_OFFSET = 12;
    private final double fireRate = 25; // more -> more bullets
    private static final double MUZZLE_FRAME_DURATION = 0.04;
    private static final int MUZZLE_FRAMES = 3;
    private double width = 12;
    private double height = 8;
    private int damage;

    private static final int GUN_IDLE_FRAMES = 6;
    private SpriteRenderer muzzleFlashRenderer;
    private SpriteRenderer weaponRenderer;
    private boolean isFiring;
    private boolean autoFire;
    private double fireCooldown = 2;
    private double muzzleElapsed;
    private boolean muzzlePlaying;
    private Ammo ammo;


    // removing idleup and idledown sheet because it does match the rotation mechanism
    private Image gunIdleRightSheet;
    private Image gunIdleLeftSheet;
    private Image fireUpSheet;
    private Image fireDownSheet;
    private Image fireRightSheet;
    private Image fireLeftSheet;

    public Weapon()
    {
        loadWeaponAimAssets();

        loadMuzzleFireAssets();

        isFiring = false;
        autoFire = true;
        fireCooldown = 0;
        muzzleElapsed = 0;
        muzzlePlaying = false;
        ammo = new Ammo();
        damage = 10;
    }

    private void loadWeaponAimAssets()
    {
        try
        {
            gunIdleRightSheet = Resources.loadImageOrThrow(
                    "assets",
                    "raw",
                    "PostApocalypse",
                    "Character",
                    "Guns",
                    "Gun",
                    "Gun_side_idle-and-run-Sheet6.png"
            );
        }
        catch (FileNotFoundException e)
        {
            weaponRenderer = null;
            this.addComponent(new BoxRenderer(36, 36, javafx.scene.paint.Color.CYAN));
        }
        try
        {
            gunIdleLeftSheet = Resources.loadImageOrThrow(
                    "assets",
                    "raw",
                    "PostApocalypse",
                    "Character",
                    "Guns",
                    "Gun",
                    "Gun_side-left_idle-and-run-Sheet6.png"
            );
        }
        catch (FileNotFoundException e)
        {
            weaponRenderer = null;
            this.addComponent(new BoxRenderer(36, 36, javafx.scene.paint.Color.CYAN));

        }
        weaponRenderer = new SpriteRenderer(width, height, gunIdleLeftSheet, GUN_IDLE_FRAMES, 1);
        weaponRenderer.setAnimationTime(TimeSpan.fromMilliseconds(60)).startAnimation();
        this.addComponent(weaponRenderer);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (!muzzlePlaying || muzzleFlashRenderer == null) return;

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

    // manually rendering the muzzle
    @Override
    public void onRender(GraphicsContext context)
    {
        if (!muzzlePlaying || muzzleFlashRenderer == null) return;

        double rotation = this.getTransform().getRotation().getRadians();
        Vector2D weaponPos = this.getTransform().getPosition();
        Vector2D barrelDirection = new Vector2D(Math.cos(rotation), Math.sin(rotation));
        Vector2D barrelTip = weaponPos.add(barrelDirection.multiply(width));

        double w = muzzleFlashRenderer.getWidth();
        double h = muzzleFlashRenderer.getHeight();

        double sx = muzzleFlashRenderer.getFrameX() * muzzleFlashRenderer.getFrameWidth();
        double sy = muzzleFlashRenderer.getFrameY() * muzzleFlashRenderer.getFrameHeight();

        context.save();
        context.translate(barrelTip.getX() + 2, barrelTip.getY() - 1);
        context.rotate(this.getTransform().getRotation().getDegrees());
        context.drawImage(
                muzzleFlashRenderer.getImage(),
                sx,
                sy,
                muzzleFlashRenderer.getFrameWidth(),
                muzzleFlashRenderer.getFrameHeight(),
                -w / 2 - 2,
                -h / 2 - 2,
                w,
                h
        );
        context.restore();
    }

    public void updatePosition(Vector2D mousePos, Vector2D playerPos)
    {
        Vector2D delta = mousePos.subtract(playerPos);
        if (delta.isZeroEpsilon(0.0001)) delta = Vector2D.right();

        double dx = delta.getX();
        double dy = delta.getY();
        double rotation = Math.atan2(dy, dx);
        Vector2D orbitingDistance = delta.normalize().multiply(WEAPON_OFFSET);

        if (weaponRenderer != null)
        {
            if (Math.abs(rotation) > Math.PI / 2)
            {
                this.weaponRenderer.flipVertical();
            }
            else
            {
                this.weaponRenderer.unFlipVertical();
            }
        }

        if (weaponRenderer != null)
        {
            Image directionalGunSheet = getDirectionalGunIdleSheet();
            if (directionalGunSheet != null && weaponRenderer.getImage() != directionalGunSheet)
            {
                weaponRenderer.setImage(directionalGunSheet, GUN_IDLE_FRAMES, 1).setFrame(0, 0).startAnimation();
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


        if (ammo.isReloading() || !ammo.hasAmmo()) return null;

        if (!isFiring) return null;

        if (!autoFire)
        {
            setFiring(false);
            ammo.decreaseAmmo();
            triggerMuzzleFlash(bulletPos, targetPos);
            return new Bullet(200, bulletPos, 1000, targetPos, damage);
        }

        fireCooldown -= deltaTime.asSeconds();
        if (fireCooldown <= 0.01)
        {
            fireCooldown = 1.0 / fireRate;
            ammo.decreaseAmmo();
            triggerMuzzleFlash(weaponPos, targetPos);
            return new Bullet(200, bulletPos, 1000, targetPos, damage);
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

    public void setCurrentAmmo(int amount) { ammo.setCurrentAmmo(amount); }

    public void reload()
    {
        ammo.startReload();
    }

    private void loadMuzzleFireAssets()
    {
        try
        {
            fireUpSheet = Resources.loadImageOrThrow(
                    "assets",
                    "raw",
                    "PostApocalypse",
                    "Character",
                    "Guns",
                    "Fire",
                    "Fire_Up-Sheet3.png"
            );
            fireDownSheet = Resources.loadImageOrThrow(
                    "assets",
                    "raw",
                    "PostApocalypse",
                    "Character",
                    "Guns",
                    "Fire",
                    "Fire_Down-Sheet3.png"
            );
            fireRightSheet = Resources.loadImageOrThrow(
                    "assets",
                    "raw",
                    "PostApocalypse",
                    "Character",
                    "Guns",
                    "Fire",
                    "Fire_side-Sheet3.png"
            );
            fireLeftSheet = Resources.loadImageOrThrow(
                    "assets",
                    "raw",
                    "PostApocalypse",
                    "Character",
                    "Guns",
                    "Fire",
                    "Fire_side-left-Sheet3.png"
            );

            muzzleFlashRenderer = new SpriteRenderer(10, 10, fireRightSheet, MUZZLE_FRAMES, 1);
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
        if (muzzleFlashRenderer == null) return;

        Vector2D direction = targetPos.subtract(weaponPos);
        Image directionalSheet = getDirectionalFireSheet();
        if (directionalSheet == null) return;

        muzzleFlashRenderer.setImage(directionalSheet, MUZZLE_FRAMES, 1);
        muzzleFlashRenderer.setFrame(0, 0);
        muzzleFlashRenderer.setVisible(true);

        muzzleElapsed = 0;
        muzzlePlaying = true;
    }

    private Image getDirectionalFireSheet() { return fireRightSheet; }

    private Image getDirectionalGunIdleSheet() { return gunIdleRightSheet; }

    public int getDamage() { return damage; }

    public void setDamage(int damage) { this.damage = damage; }
}