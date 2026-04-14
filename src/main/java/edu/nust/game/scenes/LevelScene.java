package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.PlayerSession;
import edu.nust.game.Score;
import edu.nust.game.assets.TilesetAsset;
import edu.nust.game.gameobjects.*;
import edu.nust.game.highscores.HighscoreStore;
import edu.nust.game.tilemap.LevelBuilder;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

public class LevelScene extends GameScene
{
    private static final ThreadLocal<LevelId> PENDING_LEVEL = new ThreadLocal<>();

    private LevelId selectedLevel;

    @FXML private StackPane pauseOverlay;
    @FXML private Label scoreLabel;
    @FXML private Label ammoLabel;
    @FXML private Label reloadLabel;
    @FXML private Label healthLabel;
    @FXML private VBox ammoBarContainer;
    @FXML private VBox healthBarContainer;
    private boolean isPaused = false;
    private Player player;
    private Weapon weapon;
    private EnemyManager enemyManager;
    private CollisionManager collisionManager;
    private Score score;
    private AmmoBar ammoBar;
    private HealthBar healthBar;
    private LevelBuilder levelBuilder;
    private LevelBuilder.PlayAreaBounds playAreaBounds;
    private Level1CollisionMask level1CollisionMask;
    private double worldWidth;
    private double worldHeight;
    private Vector2D mousePosition = Vector2D.zero();
    private double screenX;
    private double screenY;
    private double collisionCooldown = 0;
    private boolean scoreSaved = false;

    public LevelScene(GameWorld level) { this(level, LevelId.LEVEL_1); }

    public LevelScene(GameWorld level, LevelId selectedLevel)
    {
        super(prepareWorld(level, selectedLevel));
    }

    private static GameWorld prepareWorld(GameWorld world, LevelId levelId)
    {
        PENDING_LEVEL.set(levelId != null ? levelId : LevelId.LEVEL_1);
        return world;
    }

    private static LevelId consumePendingLevel()
    {
        LevelId level = PENDING_LEVEL.get();
        PENDING_LEVEL.remove();
        return level != null ? level : LevelId.LEVEL_1;
    }

    @Override
    public void onInit()
    {
        selectedLevel = consumePendingLevel();

        if (selectedLevel == LevelId.LEVEL_1)
            initLevel1WithBackground();
        else
            initTileLevel();

        collisionManager = new CollisionManager(this);
        if (selectedLevel == LevelId.LEVEL_1)
        {
            if (level1CollisionMask != null)
                levelBuilder.buildConcreteBoundaryWallsForLevel1(this, playAreaBounds, worldWidth, worldHeight);
        }
        else
        {
            levelBuilder.buildConcreteBoundaryWalls(this, worldWidth, worldHeight);
        }
        if (selectedLevel == LevelId.LEVEL_1 && level1CollisionMask != null)
            levelBuilder.buildLevel1InternalConcreteWalls(this, level1CollisionMask, worldWidth, worldHeight);

        score = new Score();
        player = new Player(new Vector2D(80, 80), 100, 500, true);
        this.addGameObject(player.addTag(PlayerTag.class));
        player.setMovePos(clampPlayerToPlayArea(player.getTransform().getPosition()));

        // Set up collision checking for the player
        if (selectedLevel == LevelId.LEVEL_1 && level1CollisionMask != null)
        {
            player.setWalkabilityChecker((pos, radius) -> level1CollisionMask.isWalkable(pos));
        }

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

    private void initTileLevel()
    {
        // Keep tilemap logic for non-level1 scenes.
        levelBuilder = new LevelBuilder(30, 20, 64);
        levelBuilder.fillBackground(TilesetAsset.BUILDINGS_BEIGE)
                .addBoundaries(TilesetAsset.BRICK_WALL)
                .preloadAssets();

        this.addGameObject(levelBuilder.build());
        this.playAreaBounds = levelBuilder.getPlayAreaBounds();
        this.worldWidth = levelBuilder.getTilemap().getPixelWidth();
        this.worldHeight = levelBuilder.getTilemap().getPixelHeight();
    }

    private void initLevel1WithBackground()
    {
        try
        {
            Image level1Image = Level1BackgroundLoader.loadOrThrow();

            this.worldWidth = level1Image.getWidth();
            this.worldHeight = level1Image.getHeight();
            this.playAreaBounds = new LevelBuilder.PlayAreaBounds(
                    25.0,
                    worldWidth - 25.0,
                    25.0,
                    worldHeight - 25.0
            );
            this.level1CollisionMask = Level1CollisionMask.fromImage(level1Image);

            // Create a minimal LevelBuilder for Level1 to use its utility methods
            levelBuilder = new LevelBuilder(1, 1, (int) worldWidth);

            GameObject background = GameObject.create();
            background.addComponent(new SpriteRenderer(worldWidth, worldHeight, level1Image));
            background.getTransform().setPosition(worldWidth / 2.0, worldHeight / 2.0);
            this.addGameObject(background);
        }
        catch (FileNotFoundException ex)
        {
            initTileLevel();
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
        double zoom = this.getWorldCamera().setZoom(2).getZoom();

        updateMouseWorldPosition(canvasW, canvasH, cameraPos, zoom);
        handleWeaponFiring(deltaTime);
        refreshHud(deltaTime);

        if(player == null) return;
        Vector2D playerPos = clampPlayerToPlayArea(player.getTransform().getPosition());
        if (collisionManager != null)
        {
            collisionManager.manageCollisions();
            playerPos = player.getTransform().getPosition();
        }
        updateWeaponTracking(playerPos);
        updateEnemyAndCollision(playerPos, deltaTime);
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
        if (weapon == null)
            return;

        Bullet bullet = weapon.fireWeapon(mousePosition, deltaTime);
        if (bullet != null)
            this.addGameObject(bullet);
    }

    private void refreshHud(TimeSpan deltaTime)
    {
        score.update(deltaTime);
        if (scoreLabel != null)
            scoreLabel.setText(String.valueOf(score.getScore()));

        if (ammoBar != null && weapon != null)
            ammoBar.updateUI(weapon.getAmmo(), ammoLabel, reloadLabel);

        if (healthBar != null && player != null)
            healthBar.updateUI(player.getHealthSystem(), healthLabel);
    }

    private Vector2D clampPlayerToPlayArea(Vector2D playerPos)
    {
        Vector2D clampedPos = playerPos;
        if (playAreaBounds != null)
            clampedPos = playAreaBounds.clampPosition(playerPos);


        player.getTransform().setPosition(clampedPos);
        player.setMovePos(clampedPos);
        return clampedPos;
    }

    private void updateWeaponTracking(Vector2D playerPos)
    {
        if (weapon != null)
            weapon.updatePosition(mousePosition, playerPos);
    }

    private void updateEnemyAndCollision(Vector2D playerPos, TimeSpan deltaTime)
    {
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

            if (minCamX <= maxCamX)
                camX = Math.max(minCamX, Math.min(camX, maxCamX));
            else
                camX = worldWidth / 2.0;

            if (minCamY <= maxCamY)
                camY = Math.max(minCamY, Math.min(camY, maxCamY));
            else
                camY = worldHeight / 2.0;

            cameraTarget = new Vector2D(camX, camY);
        }
        this.getWorldCamera().setPosition(cameraTarget);
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
        saveScoreIfNeeded();
        setPaused(true);
        pauseOverlay.setVisible(true);
        pauseOverlay.setManaged(true);
    }

    private void saveScoreIfNeeded()
    {
        if (scoreSaved)
            return;

        scoreSaved = true;
        HighscoreStore.append(PlayerSession.getPlayerName(), getCurrentScore(), LocalDateTime.now());
    }

    @FXML
    private void retryLevel()
    {
        this.getWorld().setScene(new LevelScene(this.getWorld(), selectedLevel));
    }

    public int getCurrentScore()
    {
        return score != null ? score.getScore() : 0;
    }


}
