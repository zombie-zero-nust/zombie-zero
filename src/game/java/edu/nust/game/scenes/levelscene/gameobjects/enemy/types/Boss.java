package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;


import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks.BasicAttackObj;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks.BossAbility1;
import edu.nust.game.scenes.levelscene.gameobjects.player.Health;
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
    private Image downAttack2Sheet;
    private Image upAttack2Sheet;
    private Image leftAttack2Sheet;
    private Image rightAttack2Sheet;
    private double ability1cooldown;
    private boolean performingAbility= false;
    private TimeSpan bossAbilityCooldown = TimeSpan.fromSeconds(10);
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
    private BossAbility1 bossAbility1;
    private boolean resurrected = false;
    // Add these new fields to the top of your class
    private boolean resurrectionStarted = false;
    private boolean resurrectionFinished = false;


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
                    .setSize(image.getWidth()/4,image.getHeight()*1.5);
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

        if(!performingAbility) {
            ability1cooldown += deltaTime.asSeconds();
        }
        if(!isAttacking() && !performingAbility && ability1cooldown >= bossAbilityCooldown.asSeconds()){
            Image image = switch (facing){
                case UP -> upAttack2Sheet;
                case DOWN -> downAttack2Sheet;
                case LEFT -> leftAttack2Sheet;
                case RIGHT -> rightAttack2Sheet;
            };
            bossAbility1 = new BossAbility1(damage,this,List.of(Enemy.class),image);
            performingAbility = true;
            setAttacking(true);
            ability1cooldown = 0;

        }
        if (performingAbility) {
            bossAbility1.triggerAbility(spriteRenderer,deltaTime);

            if (!bossAbility1.isPerformingAbility()) {
                performingAbility = false;
                setAttacking(false);
            }

            return;
        }

        if (isAttacking()) {
            attackTimeElapsed += deltaTime.asMilliseconds();
            if(attackTimeElapsed >= attack1Time/4&& attack1 == null) {
                attack1 = new BasicAttackObj(
                        damage, this, 3,
                        (double) height / 2,
                        List.of(Enemy.class),
                        TimeSpan.fromMilliseconds(attack1Time/4),
                        attack1Range
                );

                this.getScene().addGameObject(attack1);
            }

            if (attackTimeElapsed >= attack1Time) {
                if (attack1 != null) {
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
                    .setAnimationTime(TimeSpan.fromMilliseconds(attack1Time/4))
                    .setSize(1.5*image.getWidth()/8,1.5*image.getHeight());

            attackTimeElapsed = 0;
            setAttacking(true);
        }

    }

    public void resetBoss() {
        Health health = new Health(10000);
        this.setHealth(health);
        this.setMovementSpeed(this.getMovementSpeed()*3);
        this.attackTimeElapsed = 0;
        this.attackCooldownElapsed = 0;
        this.ability1cooldown = 0;
        this.elapsed = 0;
        this.animationFinished = false;
        this.animationStarted = false;
        bossAbilityCooldown = bossAbilityCooldown.multiply(0.2);
        this.damage = damage * 2;
        this.setAttacking(false);
        unfreezeEnemy();
        updateSprite(0,0);
        spriteRenderer.startAnimation().setAnimationTime(TimeSpan.fromMilliseconds(150));
    }

    public void resurrect(TimeSpan deltaTime) {
        Image image = (facing == Facing.UP || facing == Facing.RIGHT) ? deathRightSheet : deathLeftSheet;

        if (!resurrectionStarted) {
            elapsed += deltaTime.asMilliseconds();
            spriteRenderer.setFrame(6, 0);

            if (elapsed < 5000) return;

            spriteRenderer.setSize(43, height);
            spriteRenderer.setImage(image, 7, 1);
            spriteRenderer.pauseAnimation();
            spriteRenderer.setFrame(6, 0);
            resurrectionStarted = true;
            elapsed = 0;
            return;
        }

        // --- REVERSE ANIMATION PHASE ---
        elapsed += deltaTime.asMilliseconds();

        int totalFrames = 7;
        double frameDuration = deathAnimationTime.asMilliseconds() / totalFrames;
        int currentFrame = (int) Math.min(elapsed / frameDuration, totalFrames - 1);
        int reverseFrame = (totalFrames - 1) - currentFrame;

        spriteRenderer.setFrame(reverseFrame, 0);

        if (reverseFrame <= 0) {
            spriteRenderer.setFrame(0, 0);
            resurrectionFinished = true;
            resurrected = true;
            resurrectionStarted = false;
            resetBoss();
        }
    }

    @Override
    public void playDeathAnimation(TimeSpan deltaTime) {
        Image image = (facing == Facing.UP || facing == Facing.RIGHT) ? deathRightSheet : deathLeftSheet;

        if (!animationFinished) {
            if (!animationStarted) {
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

            if (currentFrame >= totalFrames - 1) {
                spriteRenderer.setFrame(6, 0);  // lock on last frame
                animationFinished = true;
                elapsed = 0;
            }
        }
        // --- PHASE 2: RESURRECTION ---
        else if (!resurrected) {
            resurrect(deltaTime);   // nothing else here — resurrect manages its own frame state
        }
        // --- PHASE 3: FINAL FADE ---
        else {
            isDead = true;
            double opacity = spriteRenderer.getOpacity();
            if (opacity > 0) {
                spriteRenderer.setOpacity(opacity - 0.05);
            } else {
                this.destroy();
            }
        }
    }

    public boolean isResurrected(){
        return resurrected;
    }
    public boolean getIsDead(){
        return isDead;
    }

    @Override
    protected double baseFollowRadius() { return 200; }
}
