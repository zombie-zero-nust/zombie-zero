package edu.nust.game.gameobjects;

import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.assets.CharacterAsset;
import edu.nust.game.gameobjects.enums.CharacterAnimationAssets;
import edu.nust.game.gameobjects.interfaces.Concrete;
import edu.nust.game.gameobjects.interfaces.Damageable;
import edu.nust.game.gameobjects.interfaces.Health;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class Player extends Character implements Damageable, Concrete
{
    private enum Facing { UP, DOWN, LEFT, RIGHT }

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private HitBox hitbox;
    private final double height=30;
    private final double width = 20;
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
    private BiFunction<Vector2D, Double, Boolean> walkabilityChecker;
    private static final double COLLISION_RADIUS = 25.0;

    public Player(Vector2D pos, int initialHealth, int mSpeed, boolean moveable)
    {
        super(pos, initialHealth, mSpeed, moveable);
        health = new HealthImpl(initialHealth);
        this.addTag(PlayerTag.class);

        try
        {
            CharacterAsset characterAsset = CharacterAsset.MAIN;
            String idlePath = characterAsset.getPath() + "/Idle";
            String runPath = characterAsset.getPath() + "/Run";

            idleDown = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.IDLE_DOWN.getFilename());
            idleUp = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.IDLE_UP.getFilename());
            idleLeft = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.IDLE_LEFT.getFilename());
            idleRight = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.IDLE_RIGHT.getFilename());

            runDown = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.RUN_DOWN.getFilename());
            runUp = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.RUN_UP.getFilename());
            runLeft = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.RUN_LEFT.getFilename());
            runRight = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.RUN_RIGHT.getFilename());

            handsIdleDown = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.HANDS_IDLE_DOWN.getFilename());
            handsIdleUp = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.HANDS_IDLE_UP.getFilename());
            handsIdleLeft = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.HANDS_IDLE_LEFT.getFilename());
            handsIdleRight = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimationAssets.HANDS_IDLE_RIGHT.getFilename());

            handsRunDown = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.HANDS_RUN_DOWN.getFilename());
            handsRunUp = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.HANDS_RUN_UP.getFilename());
            handsRunLeft = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.HANDS_RUN_LEFT.getFilename());
            handsRunRight = Resources.loadImageOrThrow("assets", runPath, CharacterAnimationAssets.HANDS_RUN_RIGHT.getFilename());

            spriteRenderer = new SpriteRenderer(width, height, idleDown, 6, 1);
            spriteRenderer.setFrame(6, 1).setAnimationTime(TimeSpan.fromMilliseconds(200)).startAnimation();
            this.addComponent(spriteRenderer);

            handsRenderer = new SpriteRenderer(width, height, handsIdleDown, 6, 1);
            handsRenderer.setAnimationTime(TimeSpan.fromMilliseconds(120)).setFrame(0, 0);
            this.addComponent(handsRenderer);
        }
        catch (FileNotFoundException e)
        {
            // Fallback to test.png if character sprite not found
            try
            {
                Image fallbackImage = Resources.loadImageOrThrow("assets", "images", "test.png");
                spriteRenderer = new SpriteRenderer(width, height, fallbackImage);
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
        hitbox = new HitBox(getSpawnPos(), height / 2.0, width / 2.0);
        this.addComponent(hitbox);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        setPrePos(new Vector2D(getX(), getY()));
        movement(deltaTime);
        this.getTransform().setPosition(getMovePos());

        if (hitbox != null)
            hitbox.setPos(getMovePos());
    }

    public void movement(TimeSpan deltaTime)
    {
        double dx = 0;
        double dy = 0;

        if (activeKeys.contains(KeyCode.W)) dy -= getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.S)) dy += getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.A)) dx -= getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.D)) dx += getMovementSpeed() * deltaTime.asSeconds();

        if(dx!=0 && dy!=0) {
            dx = 0.707*dx;
            dy = 0.707*dy;
        }
        updateFacingSprite(dx, dy);

        // Check walkability before moving
        Vector2D newPos = new Vector2D(getX() + dx, getY() + dy);
        if (isWalkable(newPos))
        {
            setY(getY() + dy);
            setX(getX() + dx);
        }
        else
        {
            // Try sliding along axes if both diagonal movement is blocked
            Vector2D slideX = new Vector2D(getX() + dx, getY());
            Vector2D slideY = new Vector2D(getX(), getY() + dy);

            if (isWalkable(slideX))
                setX(getX() + dx);
            else if (isWalkable(slideY))
                setY(getY() + dy);
        }
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
            spriteRenderer.setImage(target, 6, 1).startAnimation();

        if (handsRenderer != null && handsTarget != null && handsRenderer.getImage() != handsTarget)
            handsRenderer.setImage(handsTarget, 6, 1).startAnimation();
    }

    @Override
    public void onRender(GraphicsContext context)
    {
    }

    public void takeDamage(int damage)
    {
        if (health != null)
            health.takeDamage(damage);
    }
    @Override
    public Health getHealth(){
        return health;
    }
    @Override
    public void setHealth(Health health){
        this.health = health;
    }

    @Override
    public boolean isDead(){
        return false;
    }

    public Health getHealthSystem()
    {
        return health;
    }

    @Override
    public void setHitbox()
    {
        if (hitbox == null) {
            hitbox = new HitBox(getSpawnPos(), height, width);
            this.addComponent(hitbox);
        }
    }

    @Override
    public HitBox getHitbox()
    {
        if(hitbox == null){
            setHitbox();
        }
        return hitbox;
    }

    @Override
    public void triggerCollisionEffect()
    {
        Vector2D previousPos = getPrePos();
        if (previousPos == null)
            return;

        Vector2D rollback = new Vector2D(previousPos.getX(), previousPos.getY());
        setMovePos(rollback);
        this.getTransform().setPosition(rollback);
        if (hitbox != null)
            hitbox.setPos(rollback);
    }

    public void setWalkabilityChecker(BiFunction<Vector2D, Double, Boolean> checker)
    {
        this.walkabilityChecker = checker;
    }

    private boolean isWalkable(Vector2D position)
    {
        if (walkabilityChecker == null)
            return true;
        return walkabilityChecker.apply(position, COLLISION_RADIUS);
    }


    @Override
    public String[] notInteractWith(){
        return null;
    }
}
