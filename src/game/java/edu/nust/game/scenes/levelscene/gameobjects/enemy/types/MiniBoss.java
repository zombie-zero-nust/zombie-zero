package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;


import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.core.audio.SoundEffectReference;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks.BasicAttackObj;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.assets.EnemyAsset;
import edu.nust.game.systems.audio.Audios;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.List;

public class MiniBoss extends Enemy
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
    private Image downAttack2Sheet;
    private Image upAttack2Sheet;
    private Image leftAttack2Sheet;
    private Image rightAttack2Sheet;
    private Image axeProjectileSheet;

    private double ability1cooldown;
    private boolean performingAbility = false;
    private TimeSpan bossAbilityCooldown = TimeSpan.fromSeconds(10);
    private double abilityAnimationElapsed = 0;
    private boolean axeThrownThisAbility = false;

    private boolean isDead = false;

    private int damage = 20;
    private SpriteRenderer spriteRenderer;
    private boolean animationStarted = false;
    private boolean animationFinished = false;
    private int width = 24;
    private int height = 36;
    private Facing facing = Facing.DOWN;
    private TimeSpan deathAnimationTime = TimeSpan.fromMilliseconds(1000);
    private double elapsed = 0;
    private double attack1Range = 30;
    private double attack1Time = 600;
    private double attackTimeElapsed;
    private double attackCooldownElapsed = 0;
    private double attackCooldownTime = 800; // ms
    private boolean canAttack = true;
    private BasicAttackObj attack1;
    private static final double ABILITY_ANIMATION_DURATION_SECONDS = 1.0;
    private static final double AXE_THROW_RANGE = 300;
    private static final double AXE_THROW_SPEED = 100;


    public MiniBoss(Vector2D pos, double speed, int health, double damage)
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
            downAttack2Sheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Down_Second-Attack-Sheet15.png"
            );
            upAttack2Sheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Up_Second-Attack-Sheet15.png"
            );
            leftAttack2Sheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side-left_Second-Attack-Sheet15.png"
            );
            rightAttack2Sheet = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath(),
                    "Zombie_Big_Side_Second-Attack-Sheet15.png"
            );
            axeProjectileSheet = Resources.loadImageOrThrow(
                    "assets",
                    EnemyAsset.ZOMBIE_AXE.getPath(),
                    "Axe",
                    "Axe_Side_Thrown-Sheet9.png"
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
            spriteRenderer.setImage(image, 6, 1)
                    .setSize(image.getWidth() / 4, image.getHeight() * 1.5);
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
            spriteRenderer.setImage(image, 8, 1).setSize(this.getWidth(), this.getHeight());
        }
    }

    @Override
    public void attack(TimeSpan deltaTime)
    {
        Player player = (Player) this.getScene().getFirstOfType(Player.class);
        if (player == null) return;

        if (!performingAbility)
        {
            ability1cooldown += deltaTime.asSeconds();
        }

        if (!isAttacking() && !performingAbility && ability1cooldown >= bossAbilityCooldown.asSeconds())
        {
            Image image = switch (facing)
            {
                case UP -> upAttack2Sheet;
                case DOWN -> downAttack2Sheet;
                case LEFT -> leftAttack2Sheet;
                case RIGHT -> rightAttack2Sheet;
            };

            spriteRenderer.setImage(image, 15, 1)
                    .startAnimation()
                    .setSize(1.5 * image.getWidth() / 15, 1.5 * image.getHeight());

                Audios.randomZombieBossGrowlRef().ifPresent(SoundEffectReference::play);
            performingAbility = true;
            setAttacking(true);
            ability1cooldown = 0;
            abilityAnimationElapsed = 0;
            axeThrownThisAbility = false;
        }

        if (performingAbility)
        {
            abilityAnimationElapsed += deltaTime.asSeconds();

            if (abilityAnimationElapsed >= ABILITY_ANIMATION_DURATION_SECONDS * 0.5 && !axeThrownThisAbility)
            {
                throwAxeAtPlayer(player);
                axeThrownThisAbility = true;
            }

            if (abilityAnimationElapsed >= ABILITY_ANIMATION_DURATION_SECONDS)
            {
                performingAbility = false;
                setAttacking(false);
            }

            return;
        }

        if (isAttacking())
        {
            attackTimeElapsed += deltaTime.asMilliseconds();

            if (attackTimeElapsed >= attack1Time / 4 && attack1 == null)
            {
                attack1 = new BasicAttackObj(
                        damage, this, 3,
                        (double) height / 2,
                        List.of(Enemy.class),
                        TimeSpan.fromMilliseconds(attack1Time / 4),
                        attack1Range
                );

                    Audios.randomZombieBossAttackRef().ifPresent(SoundEffectReference::play);
                this.getScene().addGameObject(attack1);
            }

            if (attackTimeElapsed >= attack1Time)
            {
                if (attack1 != null)
                {
                    attack1.destroy();
                    attack1 = null;
                }
                setAttacking(false);
                canAttack = false;
                attackCooldownElapsed = 0;
            }
            return;
        }

        // If cooldown active -> update cooldown timer only
        if (!canAttack)
        {
            attackCooldownElapsed += deltaTime.asMilliseconds();

            if (attackCooldownElapsed >= attackCooldownTime)
            {
                canAttack = true;
                attackCooldownElapsed = 0;
            }
            return;
        }

        double dist = player.getTransform().getPosition()
                .subtract(this.getTransform().getPosition())
                .magnitude();

        double minDist = attack1Range + (this.getWidth() / 2);

        if (dist <= minDist)
        {
            Image image = switch (facing)
            {
                case UP -> upAttackSheet;
                case DOWN -> downAttackSheet;
                case RIGHT -> rightAttackSheet;
                case LEFT -> leftAttackSheet;
            };

            Audios.randomZombieBossGrowlRef().ifPresent(SoundEffectReference::play);
            spriteRenderer.setImage(image, 8, 1)
                    .startAnimation()
                    .setAnimationTime(TimeSpan.fromMilliseconds(attack1Time / 4))
                    .setSize(1.5 * image.getWidth() / 8, 1.5 * image.getHeight());

            attackTimeElapsed = 0;
            setAttacking(true);
        }

    }

    private void throwAxeAtPlayer(Player player)
    {
        if (axeProjectileSheet == null || player == null) return;

        Vector2D direction = player.getTransform().getPosition()
                .subtract(this.getTransform().getPosition())
                .normalize();

        BasicAttackObj axeAttack = new BasicAttackObj(
                damage / 2,
                this,
                10,
                10,
                AXE_THROW_RANGE,
                List.of(Enemy.class),
                5,
                axeProjectileSheet,
                9,
                1,
                TimeSpan.fromMilliseconds(300),
                AXE_THROW_SPEED
        );
        axeAttack.setTargetDirection(direction);
        Audios.randomZombieBossAttackRef().ifPresent(SoundEffectReference::play);
        this.getScene().addGameObject(axeAttack);
    }

    @Override
    public void playDeathAnimation(TimeSpan deltaTime)
    {
        Image image = (facing == Facing.UP || facing == Facing.RIGHT) ? deathRightSheet : deathLeftSheet;

        if (!animationFinished)
        {
            if (!animationStarted)
            {
                spriteRenderer.setSize(43, height);
                spriteRenderer.setImage(image, 7, 1);
                spriteRenderer.pauseAnimation();   // no internal timer — we drive frames manually
                spriteRenderer.setFrame(0, 0);     // start on first frame
                animationStarted = true;
                elapsed = 0;
                return;
            }

            elapsed += deltaTime.asMilliseconds();

            int totalFrames = 7;
            double frameDuration = deathAnimationTime.asMilliseconds() / totalFrames;
            int currentFrame = (int) Math.min(elapsed / frameDuration, totalFrames - 1);

            spriteRenderer.setFrame(currentFrame, 0);

            if (currentFrame >= totalFrames - 1)
            {
                spriteRenderer.setFrame(6, 0);  // lock on last frame
                animationFinished = true;
                elapsed = 0;
            }
            return;
        }

        isDead = true;
        double opacity = spriteRenderer.getOpacity();
        if (opacity > 0)
        {
            spriteRenderer.setOpacity(opacity - 0.05);
        }
        else
        {
            this.destroy();
        }
    }

    public boolean getIsDead()
    {
        return isDead;
    }

    @Override
    public double getBaseFollowRadius() { return 300; }
}
