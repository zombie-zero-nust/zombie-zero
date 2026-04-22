package edu.nust.game.scenes.levelscene.gameobjects.player;

import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects._tags.PlayerTag;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.systems.assets.CharacterAsset;
import edu.nust.game.systems.collision.Concrete;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.HitBox;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class Player extends Character implements Damageable, Concrete
{
    private enum Facing
    {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private HitBox hitbox;
    private final double height = 12;
    private final double width = 8;
    private final SpriteRenderer spriteRenderer;
    private final SpriteRenderer handsRenderer;
    private Health health;
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
        health = new Health(initialHealth);
        this.addTag(PlayerTag.class);

        try
        {
            CharacterAsset characterAsset = CharacterAsset.MAIN;
            String idlePath = characterAsset.getPath() + "/Idle";
            String runPath = characterAsset.getPath() + "/Run";

            idleDown = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.IDLE_DOWN.get());
            idleUp = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.IDLE_UP.get());
            idleLeft = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.IDLE_LEFT.get());
            idleRight = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.IDLE_RIGHT.get());

            runDown = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.RUN_DOWN.get());
            runUp = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.RUN_UP.get());
            runLeft = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.RUN_LEFT.get());
            runRight = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.RUN_RIGHT.get());

            handsIdleDown = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.HANDS_IDLE_DOWN.get());
            handsIdleUp = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.HANDS_IDLE_UP.get());
            handsIdleLeft = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.HANDS_IDLE_LEFT.get());
            handsIdleRight = Resources.loadImageOrThrow("assets", idlePath, CharacterAnimAssets.HANDS_IDLE_RIGHT.get());

            handsRunDown = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.HANDS_RUN_DOWN.get());
            handsRunUp = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.HANDS_RUN_UP.get());
            handsRunLeft = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.HANDS_RUN_LEFT.get());
            handsRunRight = Resources.loadImageOrThrow("assets", runPath, CharacterAnimAssets.HANDS_RUN_RIGHT.get());
        }
        catch (FileNotFoundException e)
        {
            logger.error(true, "Failed to load player sprites: " + e.getMessage());
        }

        spriteRenderer = new SpriteRenderer(width, height, idleDown, 6, 1);
        spriteRenderer.setFrame(6, 1).setAnimationTime(TimeSpan.fromMilliseconds(75)).startAnimation();
        this.addComponent(spriteRenderer);

        handsRenderer = new SpriteRenderer(width, height, handsIdleDown, 6, 1);
        handsRenderer.setAnimationTime(TimeSpan.fromMilliseconds(75)).setFrame(0, 0);
        handsRenderer.setVisible(false); // TODO: combine weapon as well
        this.addComponent(handsRenderer);

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

        if (hitbox != null) hitbox.setPos(getMovePos());
    }

    public void movement(TimeSpan deltaTime)
    {
        double dx = 0;
        double dy = 0;

        if (activeKeys.contains(KeyCode.W)) dy -= getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.S)) dy += getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.A)) dx -= getMovementSpeed() * deltaTime.asSeconds();
        if (activeKeys.contains(KeyCode.D)) dx += getMovementSpeed() * deltaTime.asSeconds();

        if (dx != 0 && dy != 0)
        {
            dx = 0.707 * dx;
            dy = 0.707 * dy;
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

            if (isWalkable(slideX)) setX(getX() + dx);
            else if (isWalkable(slideY)) setY(getY() + dy);
        }
    }

    private void updateFacingSprite(double dx, double dy)
    {
        if (spriteRenderer == null) return;

        boolean moving = dx != 0 || dy != 0;

        if (moving)
        {
            if (Math.abs(dx) >= Math.abs(dy)) facing = dx >= 0 ? Facing.RIGHT : Facing.LEFT;
            else facing = dy >= 0 ? Facing.DOWN : Facing.UP;
        }

        Image target = switch (facing)
        {
            case UP -> moving ? runUp : idleUp;
            case LEFT -> moving ? runLeft : idleLeft;
            case RIGHT -> moving ? runRight : idleRight;
            default -> moving ? runDown : idleDown;
        };

        Image handsTarget = switch (facing)
        {
            case UP -> moving ? handsRunUp : handsIdleUp;
            case LEFT -> moving ? handsRunLeft : handsIdleLeft;
            case RIGHT -> moving ? handsRunRight : handsIdleRight;
            default -> moving ? handsRunDown : handsIdleDown;
        };

        if (target != null && spriteRenderer.getImage() != target)
            spriteRenderer.setImage(target, 6, 1).startAnimation();

        if (handsRenderer != null && handsTarget != null && handsRenderer.getImage() != handsTarget)
        {
            handsRenderer.setImage(handsTarget, 6, 1).startAnimation();
        }
    }

    public void takeDamage(int damage)
    {
        if (health != null) health.takeDamage(damage);
    }

    @Override
    public Health getHealth() { return health; }

    @Override
    public void setHealth(Health health) { this.health = health; }

    @Override
    public boolean isDead() { return false; }

    public Health getHealthSystem() { return health; }

    @Override
    public void setHitbox()
    {
        if (hitbox == null)
        {
            hitbox = new HitBox(getSpawnPos(), height, width);
            this.addComponent(hitbox);
        }
    }

    @Override
    public HitBox getHitbox()
    {
        if (hitbox == null)
        {
            setHitbox();
        }
        return hitbox;
    }

    @Override
    public void triggerCollisionEffect(Concrete collidedObj)
    {
        Vector2D previousPos = getPrePos();
        if (previousPos == null) return;

        Vector2D rollback = new Vector2D(previousPos.getX(), previousPos.getY());
        setMovePos(rollback);
        this.getTransform().setPosition(rollback);
        if (hitbox != null) hitbox.setPos(rollback);
    }

    @Override
    public List<Class<? extends Concrete>> notInteractWith()
    {
        return List.of(BasicEnemy.class);
    }

    public void setWalkabilityChecker(BiFunction<Vector2D, Double, Boolean> checker) { this.walkabilityChecker = checker; }

    private boolean isWalkable(Vector2D position)
    {
        if (walkabilityChecker == null) return true;
        return walkabilityChecker.apply(position, COLLISION_RADIUS);
    }
}
