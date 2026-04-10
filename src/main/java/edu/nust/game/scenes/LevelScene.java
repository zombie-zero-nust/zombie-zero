package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.Score;
import edu.nust.game.gameobjects.*;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class LevelScene extends GameScene
{
    @FXML private StackPane pauseOverlay;
    @FXML private VBox helpTextContainer;
    @FXML private Label scoreLabel;
    @FXML private Label ammoLabel;
    @FXML private Label reloadLabel;
    @FXML private Label healthLabel;
    @FXML private Label gunTypeLabel;
    @FXML private VBox ammoBarContainer;
    @FXML private VBox healthBarContainer;
    private boolean isPaused = false;
    private Player player;
    private Weapon weapon;
    private EnemyManager enemyManager;
    private Score score;
    private AmmoBar ammoBar;
    private HealthBar healthBar;
    private Vector2D mousePosition = Vector2D.zero();
    private double screenX;
    private double screenY;
    private double collisionCooldown = 0;

    public LevelScene(GameWorld level)
    {
        super(level);
    }

    @Override
    public void onInit()
    {
        score = new Score();
        player = new Player(new Vector2D(0,0), 100, 500, true);
        this.addGameObject(player.addTag(PlayerTag.class));

        weapon = new Weapon();
        this.addGameObject(weapon);

        Enemy enemy = new Enemy(new Vector2D(300, 0), 100);
        this.addGameObject(enemy.addTag(EnemyTag.class));

        enemyManager = new EnemyManager(this, score, enemy);

        if (ammoBarContainer != null)
        {
            ammoBar = new AmmoBar();
            ammoBarContainer.getChildren().add(ammoBar);
        }

        if (healthBarContainer != null)
        {
            healthBar = new HealthBar();
            healthBarContainer.getChildren().add(healthBar);
        }
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
    }

    @Override
    public void lateUpdate(TimeSpan deltaTime)
    {
        double canvasW = this.getWorldLayer().getWidth();
        double canvasH = this.getWorldLayer().getHeight();
        Vector2D cameraPos = this.getWorldCamera().getPosition();
        double zoom = this.getWorldCamera().getZoom();

        double worldX = cameraPos.getX() + (screenX - canvasW / 2) / zoom;
        double worldY = cameraPos.getY() + (screenY - canvasH / 2) / zoom;
        this.mousePosition = new Vector2D(worldX, worldY);

        if (weapon != null)
        {
            Bullet bullet = weapon.fireWeapon(mousePosition, deltaTime);
            if(bullet != null)
                this.addGameObject(bullet);
        }

        score.update(deltaTime);
        if (scoreLabel != null)
            scoreLabel.setText(String.valueOf(score.getScore()));

        if (ammoBar != null && weapon != null)
            ammoBar.updateUI(weapon.getAmmo(), ammoLabel, reloadLabel);

        if (healthBar != null && player != null)
            healthBar.updateUI(player.getHealthSystem(), healthLabel);

        if(player == null) return;
        Vector2D playerPos = player.getTransform().getPosition();

        if (weapon != null)
            weapon.updatePosition(mousePosition, playerPos);

        enemyManager.updateEnemyLogic(playerPos);

        collisionCooldown -= deltaTime.asSeconds();
        if (enemyManager.isPlayerColliding(playerPos) && collisionCooldown <= 0)
        {
            player.getHealthSystem().takeDamage(10);
            enemyManager.respawnEnemyAfterCollision();
            collisionCooldown = 1.0;
            if (!player.getHealthSystem().isAlive())
                gameOver();
        }

        this.getWorldCamera().setPosition(playerPos);
    }

    @Override
    public void onKeyPressed(KeyEvent event)
    {
        player.keyPress(event.getCode());

        if(event.getCode() == KeyCode.ESCAPE)
            setPaused(true);
        else if(event.getCode() == KeyCode.G)
            this.toggleDebugGrid();
        else if(event.getCode() == KeyCode.R && weapon != null)
            weapon.reload();
    }

    @Override
    public void onKeyReleased(KeyEvent event){
        player.keyRelease(event.getCode());
    }

    @Override
    public void onMouseMoved(MouseEvent event)
    {
        screenX = event.getX();
        screenY = event.getY();
    }

    @Override
    public void onMouseDragged(MouseEvent event)
    {
        this.screenX = event.getX();
        this.screenY = event.getY();
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        if(event.getButton()== MouseButton.PRIMARY)
            weapon.setFiring(true);
    }

    @Override
    public void onMouseReleased(MouseEvent event)
    {
        if(event.getButton()== MouseButton.PRIMARY)
            weapon.setFiring(false);
    }


    private void setPaused(boolean newState)
    {
        this.isPaused = newState;
        pauseOverlay.setVisible(newState);
        pauseOverlay.setManaged(newState);
        this.setActive(!newState);
    }


    @FXML
    private void resumeGame()
    {
        setPaused(false);
    }

    @FXML
    private void exitToMainMenu()
    {
        this.getWorld().setScene(new StartScene(this.getWorld()));
    }

    private void gameOver()
    {
        setPaused(true);
        pauseOverlay.setVisible(true);
        pauseOverlay.setManaged(true);
    }

    @FXML
    private void retryLevel()
    {
        this.getWorld().setScene(new LevelScene(this.getWorld()));
    }

    public int getCurrentScore()
    {
        return score != null ? score.getScore() : 0;
    }

}
