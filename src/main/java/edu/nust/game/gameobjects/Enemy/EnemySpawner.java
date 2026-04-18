package edu.nust.game.gameobjects.Enemy;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.Player.Player;
import javafx.scene.Scene;
import java.sql.Time;
import java.util.ArrayList;

public class EnemySpawner extends GameObject {
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Vector2D pos;
    private int enemyNo;
    private int totalEnemies;
    private double spawnTime;
    private boolean spawnActive = false;
    private TimeSpan elapsed = TimeSpan.zero();

    public EnemySpawner(int totalEnemies,double spawnTime,Vector2D pos){
        this.totalEnemies = totalEnemies;
        this.spawnTime = spawnTime;
        this.pos = pos;
    }

    @Override
    public void onInit(){
        enemyNo = 0;
        setEnemies(totalEnemies);
    }
    @Override
    public void onUpdate(TimeSpan deltaTime){
        updateStatus(this.getScene());
        if(isSpawnActive()){
            if(enemyNo<totalEnemies) {
                spawn(this.getScene(), deltaTime);
            }
            else{
                this.destroy();
            }
        }
    }

    private void setEnemies(int enemies){
        for(int i = 0;i<enemies;i++){
            this.enemies.add(new Enemy(pos,30,100));
        }
    }


    public boolean isSpawnActive() {
        return spawnActive;
    }

    public void setSpawnActive(boolean spawnActive) {
        this.spawnActive = spawnActive;
    }

    public void spawn(GameScene scene, TimeSpan deltaTime){
        elapsed.add(deltaTime);
        if(elapsed.asSeconds() % spawnTime == 0){
            scene.addGameObject(enemies.get(enemyNo));
            enemyNo++;
        }
    }

    public void updateStatus(GameScene scene){
        Player player = (Player) scene.getGameObjectsOfType(Player.class);
        Vector2D playerPos = player.getTransform().getPosition();
        if(playerPos.subtract(this.pos).magnitude() < 150){
            setSpawnActive(true);
        }
    }
}
