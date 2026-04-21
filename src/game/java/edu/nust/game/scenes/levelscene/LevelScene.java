package edu.nust.game.scenes.levelscene;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.highscores.highscores.HighScoreStorage;
import edu.nust.game.scenes.levelscene.gameobjects._tags.EnemyTag;
import edu.nust.game.scenes.levelscene.gameobjects._tags.PlayerTag;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.AmmoBar;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.Bullet;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.Weapon;
import edu.nust.game.scenes.levelscene.hud.HealthBar;
import edu.nust.game.scenes.levelscene.level_1.Level1Background;
import edu.nust.game.scenes.levelscene.level_1.Level1CollisionMask;
import edu.nust.game.scenes.start.StartScene;
import edu.nust.game.systems.PlayerSession;
import edu.nust.game.systems.Score;
import edu.nust.game.systems.collision.CollisionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.Arrays;

public class LevelScene extends GameScene
{
    @FXML private StackPane pauseOverlay;
    @FXML private Label overlayTitleLabel;
    @FXML private Label scoreLabel;
    @FXML private Label ammoLabel;
    @FXML private Label reloadLabel;
    @FXML private Label healthLabel;
    @FXML private VBox ammoBarContainer;
    @FXML private VBox healthBarContainer;
    @FXML private Button resumeButton;
    private Player player;
    private Weapon weapon;
    private CollisionManager collisionManager;
    private Score score;
    private AmmoBar ammoBar;
    private HealthBar healthBar;
    private Level1CollisionMask level1CollisionMask;
    private double worldWidth;
    private double worldHeight;
    private Vector2D mousePosition = Vector2D.zero();
    private double screenX;
    private double screenY;
    private double collisionCooldown = 0;
    private boolean cameraZoomInitialized = false;
    private boolean scoreSaved = false;
    private boolean gameOverState = false;

    public LevelScene(GameWorld world) { super(world); }

    @Override
    public void onInit()
    {

        level1CollisionMask = new Level1CollisionMask();

        collisionManager = new CollisionManager(this);

        score = new Score();
        player = new Player(new Vector2D(80, 80), 100, 50, true);
        this.addGameObject(player.addTag(PlayerTag.class));

        player.setMovePos(clampPlayerToPlayArea(player.getTransform().getPosition()));

        // Set up collision checking for the player
        player.setWalkabilityChecker((pos, radius) -> level1CollisionMask.isWalkable(pos));

        weapon = new Weapon();
        this.addGameObject(weapon);

        initLevel1WithBackground();

        BasicEnemy enemy = new BasicEnemy(new Vector2D(100, 200), 30, 100);
        this.addGameObject(enemy.addTag(EnemyTag.class));


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

    private void initLevel1WithBackground()
    {
        Arrays.stream(Level1Background.getObjects(this.getPlayer())).forEach(this::addGameObject);
    }

    @Override
    public void lateUpdate(TimeSpan deltaTime)
    {
        if (this.getWorldCamera() == null) return;

        if (!cameraZoomInitialized)
        {
            this.getWorldCamera().setZoom(4);
            cameraZoomInitialized = true;
        }

        double canvasW = this.getWorldLayer().getWidth();
        double canvasH = this.getWorldLayer().getHeight();
        Vector2D cameraPos = this.getWorldCamera().getPosition();
        double zoom = this.getWorldCamera().getZoom();

        updateMouseWorldPosition(canvasW, canvasH, cameraPos, zoom);
        handleWeaponFiring(deltaTime);
        refreshHud(deltaTime);

        if (player == null) return;
        Vector2D playerPos = clampPlayerToPlayArea(player.getTransform().getPosition());
        if (collisionManager != null)
        {
            collisionManager.manageCollisions();
            playerPos = player.getTransform().getPosition();
        }
        updateWeaponTracking(playerPos);
        updateCameraPosition(playerPos, canvasW, canvasH, zoom);
    }

    private void updateMouseWorldPosition(double canvasW, double canvasH, Vector2D cameraPos, double zoom)
    {
        double worldX = cameraPos.getX() + (screenX - canvasW / 2) / zoom;
        double worldY = cameraPos.getY() + (screenY - canvasH / 2) / zoom;
        this.mousePosition = new Vector2D(worldX, worldY);
    }

    private void handleWeaponFiring(TimeSpan deltaTime)
    {
        if (weapon == null) return;

        Bullet bullet = weapon.fireWeapon(mousePosition, deltaTime);
        if (bullet != null) this.addGameObject(bullet);
    }

    private void refreshHud(TimeSpan deltaTime)
    {
        score.update(deltaTime);
        if (scoreLabel != null) scoreLabel.setText(String.valueOf(score.getScore()));

        if (ammoBar != null && weapon != null) ammoBar.updateUI(weapon.getAmmo(), ammoLabel, reloadLabel);

        if (healthBar != null && player != null) healthBar.updateUI(player.getHealthSystem(), healthLabel);
    }

    private Vector2D clampPlayerToPlayArea(Vector2D clampedPos)
    {
        player.getTransform().setPosition(clampedPos);
        player.setMovePos(clampedPos);
        return clampedPos;
    }

    private void updateWeaponTracking(Vector2D playerPos)
    {
        if (weapon != null) weapon.updatePosition(mousePosition, playerPos);
    }


    private void updateCameraPosition(Vector2D playerPos, double canvasW, double canvasH, double zoom)
    {
        Vector2D cameraTarget = playerPos;
        if (worldWidth > 0 && worldHeight > 0)
        {
            double halfViewportW = (canvasW / 2.0) / zoom;
            double halfViewportH = (canvasH / 2.0) / zoom;

            double minCamX = halfViewportW;
            double maxCamX = worldWidth - halfViewportW;
            double minCamY = halfViewportH;
            double maxCamY = worldHeight - halfViewportH;

            double camX = cameraTarget.getX();
            double camY = cameraTarget.getY();

            if (minCamX <= maxCamX) camX = Math.clamp(camX, minCamX, maxCamX);
            else camX = worldWidth / 2.0;

            if (minCamY <= maxCamY) camY = Math.clamp(camY, minCamY, maxCamY);
            else camY = worldHeight / 2.0;

            cameraTarget = new Vector2D(camX, camY);
        }
        this.getWorldCamera().setPosition(cameraTarget);
    }

    @Override
    public void onKeyPressed(KeyEvent event)
    {
        player.keyPress(event.getCode());

        switch (event.getCode())
        {
            case ESCAPE -> setPaused(true);
            case G -> this.toggleDebugGrid();
            case R ->
            {
                if (weapon != null) weapon.reload();
            }
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) { player.keyRelease(event.getCode()); }

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
        if (event.getButton() == MouseButton.PRIMARY) weapon.setFiring(true);
    }

    @Override
    public void onMouseReleased(MouseEvent event)
    {
        if (event.getButton() == MouseButton.PRIMARY) weapon.setFiring(false);
    }


    private void setPaused(boolean newState)
    {
        if (overlayTitleLabel != null) overlayTitleLabel.setText(gameOverState ? "Game Over" : "Paused");
        if (resumeButton != null)
        {
            boolean allowResume = !gameOverState;
            resumeButton.setVisible(allowResume);
            resumeButton.setManaged(allowResume);
        }
        pauseOverlay.setVisible(newState);
        pauseOverlay.setManaged(newState);
        this.setActive(!newState);
    }


    @FXML
    private void resumeGame()
    {
        if (gameOverState) return;
        setPaused(false);
    }

    @FXML
    private void exitToMainMenu() { this.getWorld().setScene(new StartScene(this.getWorld())); }

    private void gameOver()
    {
        gameOverState = true;
        saveScoreIfNeeded();
        setPaused(true);
    }

    private void saveScoreIfNeeded()
    {
        if (scoreSaved) return;

        scoreSaved = true;
        HighScoreStorage.append(PlayerSession.getPlayerName(), getCurrentScore(), LocalDateTime.now());
    }

    @FXML
    private void retryLevel()
    {
        gameOverState = false;
        this.getWorld().setScene(new LevelScene(this.getWorld()));
    }

    public int getCurrentScore() { return score != null ? score.getScore() : 0; }

    public double getWorldWidth() { return worldWidth; }

    public double getWorldHeight() { return worldHeight; }

    public Player getPlayer() { return player; }
}