package edu.nust.game.gameobjects;

import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.assets.CharacterAsset;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

public class Player extends Character
{
    private enum Facing { UP, DOWN, LEFT, RIGHT }

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private HitBox hitbox;
    private double size = 50;
    private ArrayList<Image> images = new ArrayList<>();
    private Health health;
    private SpriteRenderer spriteRenderer;
    private SpriteRenderer handsRenderer;
    private Image idleUp;
    private Image idleDown;
    private Image idleLeft;
    private Image idleRight;
    private Image runUp;
    private Image runDown;
    private Image runLeft;
    private Image runRight;

    private Image handsIdleUp;
    private Image handsIdleDown;
    private Image handsIdleLeft;
    private Image handsIdleRight;
    private Image handsRunUp;
    private Image handsRunDown;
    private Image handsRunLeft;
    private Image handsRunRight;
    private Facing facing = Facing.DOWN;

    public Player(Vector2D pos, int initialHealth, int mSpeed, boolean moveable)
    {
        super(pos, initialHealth, mSpeed, moveable);
        health = new HealthImpl();
        this.addTag(PlayerTag.class);

        try
        {
            CharacterAsset characterAsset = CharacterAsset.MAIN;
            String idlePath = characterAsset.getPath() + "/Idle";
            String runPath = characterAsset.getPath() + "/Run";

            idleDown = Resources.loadImageOrThrow("assets", idlePath, "Character_down_idle_no-hands-Sheet6.png");
            idleUp = Resources.loadImageOrThrow("assets", idlePath, "Character_up_idle_no-hands-Sheet6.png");
            idleLeft = Resources.loadImageOrThrow("assets", idlePath, "Character_side-left_idle_no-hands-Sheet6.png");
            idleRight = Resources.loadImageOrThrow("assets", idlePath, "Character_side_idle_no-hands-Sheet6.png");

            runDown = Resources.loadImageOrThrow("assets", runPath, "Character_down_run_no-hands-Sheet6.png");
            runUp = Resources.loadImageOrThrow("assets", runPath, "Character_up_run_no-hands-Sheet6.png");
            runLeft = Resources.loadImageOrThrow("assets", runPath, "Character_side-left_run_no-hands-Sheet6.png");
            runRight = Resources.loadImageOrThrow("assets", runPath, "Character_side_run_no-hands-Sheet6.png");

            handsIdleDown = Resources.loadImageOrThrow("assets", idlePath, "Hands_down_idle-Sheet6.png");
            handsIdleUp = Resources.loadImageOrThrow("assets", idlePath, "Hands_Up_idle-Sheet6.png");
            handsIdleLeft = Resources.loadImageOrThrow("assets", idlePath, "Hands_Side-left_idle-Sheet6.png");
            handsIdleRight = Resources.loadImageOrThrow("assets", idlePath, "Hands_Side_idle-Sheet6.png");

            handsRunDown = Resources.loadImageOrThrow("assets", runPath, "Hands_down_run-Sheet6.png");
            handsRunUp = Resources.loadImageOrThrow("assets", runPath, "Hands_Up_run-Sheet6.png");
            handsRunLeft = Resources.loadImageOrThrow("assets", runPath, "Hands_side-left_run-Sheet6.png");
            handsRunRight = Resources.loadImageOrThrow("assets", runPath, "Hands_Side_run-Sheet6.png");

            spriteRenderer = new SpriteRenderer(size, size, idleDown, 6, 1);
            spriteRenderer.setAnimationTime(TimeSpan.fromMilliseconds(120)).setFrame(0, 0);
            this.addComponent(spriteRenderer);

            handsRenderer = new SpriteRenderer(size, size, handsIdleDown, 6, 1);
            handsRenderer.setAnimationTime(TimeSpan.fromMilliseconds(120)).setFrame(0, 0);
            this.addComponent(handsRenderer);

            images.add(idleDown);
        }
        catch (FileNotFoundException e)
        {
            // Fallback to test.png if character sprite not found
            try
            {
                images.add(Resources.loadImageOrThrow("assets", "images", "test.png"));
                spriteRenderer = new SpriteRenderer(size, size, images.getFirst());
                this.addComponent(spriteRenderer);
            }
            catch (FileNotFoundException ignored)
            {
            }
        }

        this.getTransform().setPosition(getSpawnPos());
    }

    public void keyPress(KeyCode key)
    {
        activeKeys.add(key);
    }

    public void keyRelease(KeyCode key)
    {
        activeKeys.remove(key);
    }

    @Override
    public void onInit()
    {
        // Initialize hitbox here when GameObject is properly set up
        hitbox = new HitBox(getSpawnPos(), size + 2, size + 2);
        this.addComponent(hitbox);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        movement(deltaTime);
        this.getTransform().setPosition(getMovePos());
    }

    public void movement(TimeSpan deltaTime)
    {
        double dx = 0;
        double dy = 0;

        if (activeKeys.contains(KeyCode.W)) dy -= getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.S)) dy += getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.A)) dx -= getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.D)) dx += getMovementSpeed() * deltaTime.asSeconds();

        updateFacingSprite(dx, dy);

        if (spriteRenderer != null)
        {
            if (dx == 0 && dy == 0)
                spriteRenderer.stopAnimation().setFrame(0, 0);
            else
                spriteRenderer.startAnimation();
        }

        if (handsRenderer != null)
        {
            if (dx == 0 && dy == 0)
                handsRenderer.stopAnimation().setFrame(0, 0);
            else
                handsRenderer.startAnimation();
        }

        setY(getY() + dy);
        setX(getX() + dx);
    }

    private void updateFacingSprite(double dx, double dy)
    {
        if (spriteRenderer == null)
            return;

        boolean moving = dx != 0 || dy != 0;

        if (moving)
        {
            if (Math.abs(dx) >= Math.abs(dy))
                facing = dx >= 0 ? Facing.RIGHT : Facing.LEFT;
            else
                facing = dy >= 0 ? Facing.DOWN : Facing.UP;
        }

        Image target;
        switch (facing)
        {
            case UP:
                target = moving ? runUp : idleUp;
                break;
            case LEFT:
                target = moving ? runLeft : idleLeft;
                break;
            case RIGHT:
                target = moving ? runRight : idleRight;
                break;
            case DOWN:
            default:
                target = moving ? runDown : idleDown;
                break;
        }

        Image handsTarget;
        switch (facing)
        {
            case UP:
                handsTarget = moving ? handsRunUp : handsIdleUp;
                break;
            case LEFT:
                handsTarget = moving ? handsRunLeft : handsIdleLeft;
                break;
            case RIGHT:
                handsTarget = moving ? handsRunRight : handsIdleRight;
                break;
            case DOWN:
            default:
                handsTarget = moving ? handsRunDown : handsIdleDown;
                break;
        }

        if (target != null && spriteRenderer.getImage() != target)
            spriteRenderer.setImage(target, 6, 1).setFrame(0, 0);

        if (handsRenderer != null && handsTarget != null && handsRenderer.getImage() != handsTarget)
            handsRenderer.setImage(handsTarget, 6, 1).setFrame(0, 0);
    }

    @Override
    public void onRender(GraphicsContext context)
    {
    }

    public void setSize(double size)
    {
        this.size = size;
    }

    public Health getHealthSystem()
    {
        return health;
    }
}
