package edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks;

import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Boss;
import edu.nust.game.systems.assets.EnemyAsset;
import edu.nust.game.systems.collision.Damageable;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.List;

public class BossAbility1 {

    private int damage;
    private final List<Class<? extends Damageable>> notDamageObj;
    private Image projectile;
    private final Image animationSheet;
    private final Boss boss;

    private boolean isAnimating = false;
    private boolean performingAbility = false;
    private boolean attacked = false;

    private double animElapsed = 0;
    private final double animDuration = 1.0; // seconds

    public BossAbility1(int damage, Boss boss,
                        List<Class<? extends Damageable>> notDamageObj,
                        Image animationSheet) {

        this.damage = damage;
        this.boss = boss;
        this.notDamageObj = notDamageObj;
        this.animationSheet = animationSheet;

        try {
            projectile = Resources.loadImageOrThrow(
                    "assets",
                    EnemyAsset.ZOMBIE_AXE.getPath(),
                    "Axe",
                    "Axe_Side_Thrown-Sheet9.png"
            );
        }
        catch (FileNotFoundException e) {
            System.err.println("Failed to load enemy sprite: " + e.getMessage());
        }
    }

    public void triggerAbility(SpriteRenderer renderer, TimeSpan deltaTime)
    {
        // start animation only once
        if (!isAnimating)
        {
            renderer.setImage(animationSheet, 15, 1)
                    .startAnimation().setSize(1.5*animationSheet.getWidth()/15,1.5*animationSheet.getHeight());

            performingAbility = true;
            attacked = false;
            isAnimating = true;
            animElapsed = 0;
        }
        animElapsed += deltaTime.asSeconds();

        if (animElapsed >= animDuration * 0.2 && !attacked)
        {
            boss.getScene().addGameObject(
                    new BasicAttackObj(
                            damage,
                            boss,
                            30,
                            30,
                            notDamageObj,
                            TimeSpan.fromMilliseconds(100),
                            0
                    )
            );

            BasicAttackObj axeAttack1 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            BasicAttackObj axeAttack2 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            BasicAttackObj axeAttack3 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            BasicAttackObj axeAttack4 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            BasicAttackObj axeAttack5 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            BasicAttackObj axeAttack6 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            BasicAttackObj axeAttack7 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            BasicAttackObj axeAttack8 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), 80);

            axeAttack1.setTargetDirection(new Vector2D(1, 0));
            axeAttack2.setTargetDirection(new Vector2D(0, 1));
            axeAttack3.setTargetDirection(new Vector2D(-1, 0));
            axeAttack4.setTargetDirection(new Vector2D(0, -1));

            if(boss.isResurrected()) {
                double d = 1.0 / Math.sqrt(2); // ≈ 0.707
                axeAttack5.setTargetDirection(new Vector2D(d, d));
                axeAttack6.setTargetDirection(new Vector2D(-d, d));
                axeAttack7.setTargetDirection(new Vector2D(-d, -d));
                axeAttack8.setTargetDirection(new Vector2D(d, -d));
            }

            boss.getScene().addGameObject(axeAttack1);
            boss.getScene().addGameObject(axeAttack2);
            boss.getScene().addGameObject(axeAttack3);
            boss.getScene().addGameObject(axeAttack4);

            if(boss.isResurrected()) {
                boss.getScene().addGameObject(axeAttack5);
                boss.getScene().addGameObject(axeAttack6);
                boss.getScene().addGameObject(axeAttack7);
                boss.getScene().addGameObject(axeAttack8);
            }

            attacked = true;
        }

        // end ability after full duration
        if (animElapsed >= animDuration)
        {
            performingAbility = false;
            isAnimating = false;
        }
    }

    public boolean isPerformingAbility() {
        return performingAbility;
    }
}