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
    private static final double AIM_EPSILON = 0.0001;
    private final double fireRate = 10; // more -> more bullets
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


    private Image gunIdleRightSheet;

    private Image fireRightSheet;



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

        weaponRenderer = new SpriteRenderer(width, height, gunIdleRightSheet, GUN_IDLE_FRAMES, 1);
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
        context.translate(barrelTip.getX(), barrelTip.getY() );
        context.rotate(this.getTransform().getRotation().getDegrees());
        context.drawImage(
                muzzleFlashRenderer.getImage(),
                sx,
                sy,
                muzzleFlashRenderer.getFrameWidth(),
                muzzleFlashRenderer.getFrameHeight(),
                -w / 2 ,
                -h / 2 ,
                w,
                h
        );
        context.restore();
    }

    public void updatePosition(Vector2D mousePos, Vector2D playerPos, Vector2D nonFollowAreaSize)
    {
        Vector2D delta = mousePos.subtract(playerPos);
        double rotation = this.getTransform().getRotation().getRadians();

        boolean isInsideNonFollowArea = isInsideNonFollowArea(delta, nonFollowAreaSize);
        if (!isInsideNonFollowArea && !delta.isZeroEpsilon(AIM_EPSILON))
        {
            rotation = Math.atan2(delta.getY(), delta.getX());
        }

        Vector2D aimDirection = Vector2D.fromAngleRadians(rotation);
        Vector2D orbitingDistance = aimDirection.multiply(WEAPON_OFFSET);

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

    private static boolean isInsideNonFollowArea(Vector2D delta, Vector2D nonFollowAreaSize)
    {
        if (nonFollowAreaSize == null) return false;

        double halfWidth = Math.abs(nonFollowAreaSize.getX()) / 2.0;
        double halfHeight = Math.abs(nonFollowAreaSize.getY()) / 2.0;

        return Math.abs(delta.getX()) <= halfWidth && Math.abs(delta.getY()) <= halfHeight;
    }

    public Bullet fireWeapon(Vector2D playerCenterPos, TimeSpan deltaTime)
    {
        Vector2D weaponPos = this.getTransform().getPosition();
        Vector2D shotDirection = Vector2D.fromAngleRadians(this.getTransform().getRotation().getRadians());
        if (shotDirection.isZeroEpsilon(AIM_EPSILON)) shotDirection = Vector2D.right();

        Vector2D shotTarget = playerCenterPos.add(shotDirection);
        ammo.update(deltaTime);
        if (ammo.isReloading() || !ammo.hasAmmo()) return null;

        if (!isFiring){

            return null;
        }

        if (!autoFire)
        {
            setFiring(false);
            ammo.decreaseAmmo();
            triggerMuzzleFlash();
            return new Bullet(playerCenterPos, 1000, shotTarget, damage);
        }

        fireCooldown -= deltaTime.asSeconds();
        if (fireCooldown <= 0.01)
        {
            fireCooldown = 1.0 / fireRate;
            ammo.decreaseAmmo();
            triggerMuzzleFlash();
            return new Bullet(playerCenterPos, 1000, shotTarget, damage);
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


            fireRightSheet = Resources.loadImageOrThrow(
                    "assets",
                    "raw",
                    "PostApocalypse",
                    "Character",
                    "Guns",
                    "Fire",
                    "Fire_side-Sheet3.png"
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

    private void triggerMuzzleFlash()
    {
        if (muzzleFlashRenderer == null) return;

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