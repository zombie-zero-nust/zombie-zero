package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;

import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.systems.assets.EnemyAsset;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.sql.Time;

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
    private Image attackUpSheet;
    private Image attackDownSheet;
    private Image attackRightSheet;
    private Image attackLeftSheet;
    private Image deathRightSheet;
    private Image deathLeftSheet;
    private SpriteRenderer spriteRenderer;
    private boolean animationStarted = false;
    private boolean animationFinsihed = false;
    private int width = 12;
    private int height = 16;
    private Facing facing = Facing.DOWN;
    private TimeSpan deathAnimationTime = TimeSpan.fromMilliseconds(300);
    private double elapsed = 0;

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
            attackDownSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Down_First-Attack-Sheet4.png"
            );
            attackLeftSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side-left_First-Attack-Sheet4.png"
            );
            attackRightSheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Small_Side_First-Attack-Sheet4.png"
            );
            attackUpSheet = Resources.loadImageOrThrow(
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
        spriteRenderer.setImage(image, 6, 1);
    }

    @Override
    public void attack() { }

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
        if(! animationFinsihed) {
            if (elapsed >= deathAnimationTime.asMilliseconds()) {
                spriteRenderer.stopAnimation();
                spriteRenderer.setFrame(6, 1);
                elapsed = 0;
                animationFinsihed = true;
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
