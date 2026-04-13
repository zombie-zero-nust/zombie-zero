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
import javafx.scene.image.PixelReader;
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
    private Score score;
    private AmmoBar ammoBar;
    private HealthBar healthBar;
    private LevelBuilder levelBuilder;
    private LevelBuilder.PlayAreaBounds playAreaBounds;
    private LevelPathMask level1PathMask;
    private Vector2D lastValidPlayerPosition;
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

        score = new Score();
        player = new Player(new Vector2D(0,0), 100, 500, true);
        this.addGameObject(player.addTag(PlayerTag.class));
        player.setMovePos(clampPlayerToPlayArea(player.getTransform().getPosition()));

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
            this.playAreaBounds = new LevelBuilder.PlayAreaBounds(0, worldWidth, 0, worldHeight);
            this.level1PathMask = LevelPathMask.fromImage(level1Image);

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
        double zoom = this.getWorldCamera().getZoom();

        updateMouseWorldPosition(canvasW, canvasH, cameraPos, zoom);
        handleWeaponFiring(deltaTime);
        refreshHud(deltaTime);

        if(player == null) return;
        Vector2D playerPos = clampPlayerToPlayArea(player.getTransform().getPosition());
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

        if (selectedLevel == LevelId.LEVEL_1 && level1PathMask != null)
            clampedPos = clampToLevel1WalkablePath(clampedPos);

        player.getTransform().setPosition(clampedPos);
        player.setMovePos(clampedPos);
        return clampedPos;
    }

    private Vector2D clampToLevel1WalkablePath(Vector2D candidate)
    {
        if (level1PathMask.isWalkable(candidate))
        {
            lastValidPlayerPosition = candidate;
            return candidate;
        }

        if (lastValidPlayerPosition == null)
        {
            Vector2D nearest = level1PathMask.findNearestWalkable(candidate, 160);
            lastValidPlayerPosition = nearest;
            return nearest;
        }

        Vector2D slideX = new Vector2D(candidate.getX(), lastValidPlayerPosition.getY());
        if (level1PathMask.isWalkable(slideX))
        {
            lastValidPlayerPosition = slideX;
            return slideX;
        }

        Vector2D slideY = new Vector2D(lastValidPlayerPosition.getX(), candidate.getY());
        if (level1PathMask.isWalkable(slideY))
        {
            lastValidPlayerPosition = slideY;
            return slideY;
        }

        return lastValidPlayerPosition;
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

    private static final class LevelPathMask
    {
        private final boolean[][] walkable;
        private final int width;
        private final int height;

        private LevelPathMask(boolean[][] walkable, int width, int height)
        {
            this.walkable = walkable;
            this.width = width;
            this.height = height;
        }

        static LevelPathMask fromImage(Image image)
        {
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            boolean[][] walkable = new boolean[height][width];

            PixelReader reader = image.getPixelReader();
            if (reader == null)
                return new LevelPathMask(walkable, width, height);

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    int argb = reader.getArgb(x, y);
                    walkable[y][x] = isPathPixel(argb);
                }
            }

            return new LevelPathMask(walkable, width, height);
        }

        boolean isWalkable(Vector2D worldPos)
        {
            int px = clamp((int) Math.round(worldPos.getX()), 0, width - 1);
            int py = clamp((int) Math.round(worldPos.getY()), 0, height - 1);
            return walkable[py][px];
        }

        Vector2D findNearestWalkable(Vector2D worldPos, int maxRadius)
        {
            int startX = clamp((int) Math.round(worldPos.getX()), 0, width - 1);
            int startY = clamp((int) Math.round(worldPos.getY()), 0, height - 1);

            if (walkable[startY][startX])
                return new Vector2D(startX, startY);

            for (int radius = 1; radius <= maxRadius; radius++)
            {
                for (int dx = -radius; dx <= radius; dx++)
                {
                    int x1 = clamp(startX + dx, 0, width - 1);
                    int yTop = clamp(startY - radius, 0, height - 1);
                    int yBottom = clamp(startY + radius, 0, height - 1);
                    if (walkable[yTop][x1]) return new Vector2D(x1, yTop);
                    if (walkable[yBottom][x1]) return new Vector2D(x1, yBottom);
                }

                for (int dy = -radius + 1; dy <= radius - 1; dy++)
                {
                    int y = clamp(startY + dy, 0, height - 1);
                    int xLeft = clamp(startX - radius, 0, width - 1);
                    int xRight = clamp(startX + radius, 0, width - 1);
                    if (walkable[y][xLeft]) return new Vector2D(xLeft, y);
                    if (walkable[y][xRight]) return new Vector2D(xRight, y);
                }
            }

            return new Vector2D(startX, startY);
        }

        private static boolean isPathPixel(int argb)
        {
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            int max = Math.max(r, Math.max(g, b));
            int min = Math.min(r, Math.min(g, b));

            double brightness = max / 255.0;
            double saturation = max == 0 ? 0.0 : (max - min) / (double) max;

            // Path pixels in level1Background are brighter and less saturated than wall bricks.
            return brightness >= 0.58 && saturation <= 0.50;
        }

        private static int clamp(int value, int min, int max)
        {
            return Math.max(min, Math.min(value, max));
        }
    }

}
