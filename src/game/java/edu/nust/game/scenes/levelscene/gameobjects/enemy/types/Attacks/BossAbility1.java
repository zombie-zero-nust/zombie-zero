package edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks;

import edu.nust.engine.core.audio.SoundEffectReference;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Boss;
import edu.nust.game.systems.assets.EnemyAsset;
import edu.nust.game.systems.audio.Audios;
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
    private double axeSpeed = 100;

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

        if (animElapsed >= animDuration * 0.5 && !attacked)
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
            Audios.randomZombieBossGrowlRef().ifPresent(SoundEffectReference::play);

            BasicAttackObj axeAttack1 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack2 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack3 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack4 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack5 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack6 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack7 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack8 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);
            BasicAttackObj axeAttack9  = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack10 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack11 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack12 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack13 = new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack14 =new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack15 =new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);

            BasicAttackObj axeAttack16 =new BasicAttackObj(damage / 2, boss, 10, 10, 300,
                    notDamageObj, 5, projectile, 9, 1,
                    TimeSpan.fromMilliseconds(300), axeSpeed);


            axeAttack1.setTargetDirection(new Vector2D(1, 0));
            axeAttack2.setTargetDirection(new Vector2D(0, 1));
            axeAttack3.setTargetDirection(new Vector2D(-1, 0));
            axeAttack4.setTargetDirection(new Vector2D(0, -1));

            if(boss.isResurrected()) {
                double d = 1.0 / Math.sqrt(2); // ≈ 0.707
                double a = 0.89442719;
                double b = 0.44721359;
                axeAttack5.setTargetDirection(new Vector2D(d, d));
                axeAttack6.setTargetDirection(new Vector2D(-d, d));
                axeAttack7.setTargetDirection(new Vector2D(-d, -d));
                axeAttack8.setTargetDirection(new Vector2D(d, -d));
                axeAttack9.setTargetDirection(new Vector2D(a, b));      // between Right and NE
                axeAttack10.setTargetDirection(new Vector2D(b, a));     // between NE and Down
                axeAttack11.setTargetDirection(new Vector2D(-b, a));    // between Down and NW
                axeAttack12.setTargetDirection(new Vector2D(-a, b));    // between NW and Left
                axeAttack13.setTargetDirection(new Vector2D(-a, -b));   // between Left and SW
                axeAttack14.setTargetDirection(new Vector2D(-b, -a));   // between SW and Up
                axeAttack15.setTargetDirection(new Vector2D(b, -a));    // between Up and SE
                axeAttack16.setTargetDirection(new Vector2D(a, -b));    // between SE and Right
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
                boss.getScene().addGameObject(axeAttack9);
                boss.getScene().addGameObject(axeAttack10);
                boss.getScene().addGameObject(axeAttack11);
                boss.getScene().addGameObject(axeAttack12);
                boss.getScene().addGameObject(axeAttack13);
                boss.getScene().addGameObject(axeAttack14);
                boss.getScene().addGameObject(axeAttack15);
                boss.getScene().addGameObject(axeAttack16);
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