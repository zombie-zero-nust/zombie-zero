package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;

import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks.BasicAttackObj;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.assets.EnemyAsset;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.List;

public class BasicEnemy extends Enemy
{
    private Image downIdleSheet;
    private Image upIdleSheet;
    private Image rightIdleSheet;
    private Image leftIdleSheet;
    private Image rightMoveSheet;
    private Image leftMoveSheet;
    private Image upMoveSheet;
    private Image downMoveSheet;
    private Image upAttackSheet;
    private Image downAttackSheet;
    private Image rightAttackSheet;
    private Image leftAttackSheet;
    private Image deathRightSheet;
    private Image deathLeftSheet;
    private SpriteRenderer spriteRenderer;
    private boolean animationStarted = false;
    private boolean animationFinished = false;
    private int width = 12;
    private int height = 16;
    private Facing facing = Facing.getRandom();
    private TimeSpan deathAnimationTime = TimeSpan.fromMilliseconds(300);
    private double elapsed = 0;
    private double attack1Range = 3;
    private double attack1Time = 400;
    private double attackTimeElapsed;
    private double attackCooldownElapsed = 0;
    private double attackCooldownTime = 800; // ms (adjust)
    private boolean canAttack = true;

    public BasicEnemy(Vector2D pos)
    {
        super(pos, 30, 100, 16, 12, 10, EnemyAsset.ZOMBIE_SMALL);
        loadSprites(EnemyAsset.ZOMBIE_SMALL);
    }

    @Override
    public void onInit()
    {
    }

    @Override
    public void loadSprites(EnemyAsset enemyType)
    {
        try
        {
            downIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Down_Idle-Sheet6.png"
            );
            upIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Up_Idle-Sheet6.png"
            );
            rightIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side_Idle-Sheet6.png"
            );
            leftIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side-left_Idle-Sheet6.png"
            );
            downMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Down_walk-Sheet6.png"
            );
            leftMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side-left_Walk-Sheet6.png"
            );
            rightMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side_Walk-Sheet6.png"
            );
            upMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Up_Walk-Sheet6.png"
            );
            downAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Down_First-Attack-Sheet4.png"
            );
            leftAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side-left_First-Attack-Sheet4.png"
            );
            rightAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side_First-Attack-Sheet4.png"
            );
            upAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Up_First-Attack-Sheet4.png"
            );
            deathLeftSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side-left_First-Death-Sheet6.png"
            );
            deathRightSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side_First-Death-Sheet6.png"
            );
            spriteRenderer = new SpriteRenderer(width, height, downIdleSheet, 6, 1);
            spriteRenderer.setAnimationTime(TimeSpan.fromMilliseconds(150)).startAnimation();

            this.addComponent(spriteRenderer);
            onHealthChanged();
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Failed to load enemy sprite: " + e.getMessage());
        }
    }

    @Override
    protected void onHealthChanged()
    {
        if (spriteRenderer == null) return;

        javafx.scene.paint.Color tintColor = getHealthTintColor();
        if (tintColor == null) spriteRenderer.clearTint();
        else spriteRenderer.tintSelf(tintColor);
    }

    @Override
    public void updateSprite(double dx, double dy)
    {
        Image image;
        if (dx == 0 && dy == 0)
        {
            image = switch (facing)
            {
                case UP -> upIdleSheet;
                case DOWN -> downIdleSheet;
                case LEFT -> leftIdleSheet;
                case RIGHT -> rightIdleSheet;
            };
        }
        else
        {
            if (Math.abs(dx) > Math.abs(dy)) facing = dx > 0 ? Facing.RIGHT : Facing.LEFT;
            else facing = dy > 0 ? Facing.DOWN : Facing.UP;
            image = switch (facing)
            {
                case UP -> upMoveSheet;
                case DOWN -> downMoveSheet;
                case LEFT -> leftMoveSheet;
                case RIGHT -> rightMoveSheet;
            };
        }
        spriteRenderer.setImage(image, 6, 1).setSize(this.getWidth(),this.getHeight());
    }

    @Override
    public void attack(TimeSpan deltaTime) {

        Player player = (Player) this.getScene().getFirstOfType(Player.class);
        if (player == null) return;
        // If attacking -> update attack timer only
        if (isAttacking()) {
            attackTimeElapsed += deltaTime.asMilliseconds();

            if (attackTimeElapsed >= attack1Time) {
                setAttacking(false);
                canAttack = false;
                attackCooldownElapsed = 0;
            }
            return;
        }

        // If cooldown active -> update cooldown timer only
        if (!canAttack) {
            attackCooldownElapsed += deltaTime.asMilliseconds();

            if (attackCooldownElapsed >= attackCooldownTime) {
                canAttack = true;
                attackCooldownElapsed = 0;
            }
            return;
        }

        double dist = player.getTransform().getPosition()
                .subtract(this.getTransform().getPosition())
                .magnitude();

        double minDist = attack1Range + (this.getWidth() / 2);

        if (dist <= minDist) {
            BasicAttackObj attack1 = new BasicAttackObj(
                    10, this, 3,
                    (double) height / 2,
                    List.of(Enemy.class),
                    TimeSpan.fromMilliseconds(attack1Time),
                    attack1Range
            );

            this.getScene().addGameObject(attack1);

            Image image = switch (facing) {
                case UP -> upAttackSheet;
                case DOWN -> downAttackSheet;
                case RIGHT -> rightAttackSheet;
                case LEFT -> leftAttackSheet;
            };

            spriteRenderer.setImage(image, 4, 1)
                    .startAnimation()
                    .setAnimationTime(TimeSpan.fromMilliseconds(attack1Time / 4));
            spriteRenderer.setSize(image.getWidth()/4,image.getHeight());

            attackTimeElapsed = 0;
            setAttacking(true);
        }
    }


    @Override
    public void playDeathAnimation(TimeSpan deltaTime) {

        elapsed += deltaTime.asMilliseconds();

        Image image;

        if (facing == Facing.UP || facing == Facing.RIGHT) {
            image = deathRightSheet;
        } else {
            image = deathLeftSheet;
        }

        // Play animation only once
        if (!animationStarted) {
            spriteRenderer.setSize(16,14);
            spriteRenderer.setImage(image, 6, 1)
                    .startAnimation()
                    .setAnimationTime(deathAnimationTime);
            animationStarted = true;
        }
        if(! animationFinished) {
            if (elapsed >= deathAnimationTime.asMilliseconds()) {
                spriteRenderer.stopAnimation();
                spriteRenderer.setFrame(6, 1);
                elapsed = 0;
                animationFinished = true;
            }
        }
        else {
            double opacity = spriteRenderer.getOpacity();
            if (opacity > 0) {
                spriteRenderer.setOpacity(opacity - 0.05);
            } else {
                this.destroy();
            }
        }
    }

}
