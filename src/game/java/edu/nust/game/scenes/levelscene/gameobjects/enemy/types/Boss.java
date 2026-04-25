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

public class Boss extends Enemy
{
    private Image downIdleSheet;
    private Image upIdleSheet;
    private Image rightIdleSheet;
    private Image leftIdleSheet;
    private Image rightMoveSheet;
    private Image leftMoveSheet;
    private Image upMoveSheet;
    private Image downMoveSheet;
    private Image deathRightSheet;
    private Image deathLeftSheet;
    private Image leftAttackSheet;
    private Image rightAttackSheet;
    private Image downAttackSheet;
    private Image upAttackSheet;
    private SpriteRenderer spriteRenderer;
    private boolean animationStarted = false;
    private boolean animationFinished = false;
    private int width = 24;
    private int height = 36;
    private Facing facing = Facing.DOWN;
    private TimeSpan deathAnimationTime = TimeSpan.fromMilliseconds(500);
    private double elapsed = 0;
    private double attack1Range = 10;
    private double attack1Time = 600;
    private double attackTimeElapsed;
    private double attackCooldownElapsed = 0;
    private double attackCooldownTime = 800; // ms (adjust)
    private boolean canAttack = true;
    private int attackingFrame;
    private BasicAttackObj attack1;


    public Boss(Vector2D pos, double speed, int health, double damage)
    {
        super(pos, speed, health, 36, 24, damage, EnemyAsset.ZOMBIE_BIG);
        loadSprites(EnemyAsset.ZOMBIE_BIG);
    }

    @Override
    public void loadSprites(EnemyAsset enemyType)
    {
        try
        {
            downIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Down_Idle-Sheet6.png"
            );
            upIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Up_Idle-Sheet6.png"
            );
            rightIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side_Idle-Sheet6.png"
            );
            leftIdleSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side-left_Idle-Sheet6.png"
            );

            downMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Down_Walk-Sheet8.png"
            );
            leftMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side-left_Walk-Sheet8.png"
            );
            rightMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side_Walk-Sheet8.png"
            );
            upMoveSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Up_Walk-Sheet8.png"
            );
            deathLeftSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side-left_First-Death-Sheet7.png"
            );
            deathRightSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side_First-Death-Sheet7.png"
            );
            downAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Down_First-Attack-Sheet8.png"
            );
            upAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Up_First-Attack-Sheet8.png"
            );
            rightAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side_First-Attack-Sheet8.png"
            );
            leftAttackSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side-left_First-Attack-Sheet8.png"
            );

            spriteRenderer = new SpriteRenderer(width, height, downIdleSheet, 6, 1);
            spriteRenderer.setAnimationTime(TimeSpan.fromMilliseconds(150)).startAnimation();

            this.addComponent(spriteRenderer);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Failed to load enemy sprite: " + e.getMessage());
        }
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
            spriteRenderer.setImage(image, 6, 1).setSize(this.getWidth(),this.getHeight());
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
            spriteRenderer.setImage(image, 8, 1).setSize(this.getWidth(),this.getHeight());
        }
    }

    @Override
    public void attack(TimeSpan deltaTime)
    {
        Player player = (Player) this.getScene().getFirstOfType(Player.class);
        if (player == null) return;
        // If attacking -> update attack timer only
        if (isAttacking()) {
            attackTimeElapsed += deltaTime.asMilliseconds();
            if(attackTimeElapsed >= 250&& attack1 == null) {
                attack1 = new BasicAttackObj(
                        10, this, 3,
                        (double) height / 2,
                        attack1Range,
                        List.of(Enemy.class),
                        TimeSpan.fromMilliseconds(attack1Time-250),
                        false
                );

                this.getScene().addGameObject(attack1);
            }

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
            Image image = switch (facing) {
                case UP -> upAttackSheet;
                case DOWN -> downAttackSheet;
                case RIGHT -> rightAttackSheet;
                case LEFT -> leftAttackSheet;
            };

            spriteRenderer.setImage(image, 8, 1)
                    .startAnimation()
                    .setAnimationTime(TimeSpan.fromMilliseconds(attack1Time/4));

            spriteRenderer.setSize(30,36);
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
            spriteRenderer.setSize(43,height);
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
