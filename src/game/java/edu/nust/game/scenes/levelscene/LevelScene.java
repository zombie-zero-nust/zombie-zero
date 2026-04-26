package edu.nust.game.scenes.levelscene;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.highscores.HighScoresScene;
import edu.nust.game.scenes.highscores.highscores.HighScoreStorage;
import edu.nust.game.scenes.levelscene.gameobjects._tags.PlayerTag;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.spawner.EnemySpawnPointGameObject;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.Ammo;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.AmmoBar;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.Bullet;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.Weapon;
import edu.nust.game.scenes.levelscene.hud.HealthBar;
import edu.nust.game.scenes.levelscene.hud.ScoreDisplayController;
import edu.nust.game.scenes.levelscene.level_1.Level1Background;
import edu.nust.game.scenes.levelscene.level_1.Level1CollisionMask;
import edu.nust.game.scenes.levelscene.level_1.Level1SpawnPoints;
import edu.nust.game.scenes.start.StartScene;
import edu.nust.game.systems.PlayerSession;
import edu.nust.game.systems.Score;
import edu.nust.game.systems.audio.MusicManager;
import edu.nust.game.systems.collision.CollisionManager;
import edu.nust.game.systems.collision.HitBox;
import edu.nust.game.systems.pathfinder.MapNodeSetter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;

public class LevelScene extends GameScene
{
    private static final Vector2D WEAPON_NON_FOLLOW_AREA_SIZE = new Vector2D(8, 12);
    private static final Vector2D GROWN_CAMERA_VIEW = new Vector2D(160, 120);
    private static final double RELOAD_OPACITY_MIN = 0.5;
    private static final double RELOAD_OPACITY_MAX = 1.0;

    @FXML private StackPane pauseOverlay;
    @FXML private Label overlayTitleLabel;
    @FXML private StackPane scoreDisplayControllerContainer;
    @FXML private Label ammoLabel;
    @FXML private Label healthLabel;
    @FXML private VBox ammoBarContainer;
    @FXML private VBox healthBarContainer;
    @FXML private ImageView gunIconView;
    @FXML private Button resumeButton;
    @FXML private Button pauseRetryButton;
    @FXML private Button pauseExitButton;
    @FXML private Button gameOverNewGameButton;
    @FXML private Button gameOverMainMenuButton;
    @FXML private Button gameOverHighScoresButton;
    @FXML private Region pauseHeadingTile;
    @FXML private Region gameOverHeadingTile;
    private Player player;
    private Weapon weapon;
    private CollisionManager collisionManager;
    private Score score;
    private ScoreDisplayController scoreDisplayController;
    private AmmoBar ammoBar;
    private HealthBar healthBar;
    private Level1CollisionMask level1CollisionMask;
    private double worldWidth;
    private double worldHeight;
    private Vector2D mousePosition = Vector2D.zero();
    private double screenX;
    private double screenY;
    private boolean cameraZoomInitialized = false;
    private boolean scoreSaved = false;
    private boolean gameOverState = false;
    private boolean playerWon = false;
    private boolean allHitboxesVisible = false;
    private MapNodeSetter nodeSetter;

    public LevelScene(GameWorld world) { super(world); }

    @Override
    public void onInit()
    {
        MusicManager.playLevelMusic();

        level1CollisionMask = new Level1CollisionMask();

        collisionManager = new CollisionManager(this);

        score = new Score();
        player = new Player(Level1SpawnPoints.PLAYER_SPAWN_POINT.copy(), 100, 50, true);
        this.addGameObject(player.addTag(PlayerTag.class));

        player.setMovePos(clampPlayerToPlayArea(player.getTransform().getPosition()));

        // Set up collision checking for the player
        player.setWalkabilityChecker((hitbox) -> level1CollisionMask.isWalkable(hitbox));

        weapon = new Weapon();
        this.addGameObject(weapon);

        initLevel1WithBackground();
        nodeSetter = new MapNodeSetter(new Vector2D(1600, 400), 3200, 800, this);
        initStaticEnemySpawnPoints();

        if (ammoBarContainer != null)
        {
            ammoBar = new AmmoBar();
            ammoBarContainer.getChildren().add(ammoBar);
        }

        if (gunIconView != null)
        {
            loadGunIcon();
        }

        if (healthBarContainer != null)
        {
            healthBar = new HealthBar();
            healthBarContainer.getChildren().add(healthBar);
        }

        // Initialize ScoreDisplayController for game over screen
        scoreDisplayController = new ScoreDisplayController();

        Rectangle bounds = Level1CollisionMask.getMapBounds();
        worldWidth = bounds.getWidth();
        worldHeight = bounds.getHeight();
    }

    private void initLevel1WithBackground()
    {
        Arrays.stream(Level1Background.getObjects(this)).forEach(this::addGameObject);
    }

    private void initStaticEnemySpawnPoints()
    {
        Level1SpawnPoints.forEachEnemySpawnPoint(definition -> {
            EnemySpawnPointGameObject spawnPoint = new EnemySpawnPointGameObject(
                    definition.position(), definition.enemyType(), //
                    GROWN_CAMERA_VIEW
            );
            spawnPoint.setSpawnEnabled(definition.enabled());
            this.addGameObject(spawnPoint);
        });
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
        if (player == null) return;

        Vector2D playerPos = clampPlayerToPlayArea(player.getTransform().getPosition());

        updateWeaponTracking(playerPos);
        handleWeaponFiring(playerPos, deltaTime);
        refreshHud(deltaTime);

        if (collisionManager != null)
        {
            collisionManager.manageCollisions();
            playerPos = player.getTransform().getPosition();
        }

        if (!gameOverState && player.isDead())
        {
            gameOver();
            return;
        }

        updateCameraPosition(playerPos, canvasW, canvasH, zoom);
        renderCollisionDebugOverlays();
    }

    private void renderCollisionDebugOverlays()
    {
        if (allHitboxesVisible)
        {
            for (GameObject object : getAllGameObjects())
            {
                HitBox hitBox = object.getFirstComponent(HitBox.class);
                if (hitBox == null) continue;

                addFrameDebugRectangle(hitBox.asRect());
            }
        }
    }

    private void updateMouseWorldPosition(double canvasW, double canvasH, Vector2D cameraPos, double zoom)
    {
        double worldX = cameraPos.getX() + (screenX - canvasW / 2) / zoom;
        double worldY = cameraPos.getY() + (screenY - canvasH / 2) / zoom;
        this.mousePosition = new Vector2D(worldX, worldY);
    }

    private void handleWeaponFiring(Vector2D playerPos, TimeSpan deltaTime)
    {
        if (weapon == null) return;

        Bullet bullet = weapon.fireWeapon(playerPos, deltaTime);
        if (bullet != null) this.addGameObject(bullet);
    }

    private void refreshHud(TimeSpan deltaTime)
    {
        score.update(deltaTime);
        // Score is only displayed on game over, not during gameplay
        // if (scoreLabel != null) scoreLabel.setText(String.valueOf(score.getScore()));

        if (ammoBar != null && weapon != null) ammoBar.updateUI(weapon.getAmmo(), ammoLabel);
        updateReloadGunOpacity();

        if (healthBar != null && player != null) healthBar.updateUI(player.getHealthSystem(), healthLabel);
    }

    private void updateReloadGunOpacity()
    {
        if (gunIconView == null || weapon == null || weapon.getAmmo() == null) return;

        Ammo ammo = weapon.getAmmo();
        if (!ammo.isReloading() && !ammo.isReloadDelayActive())
        {
            gunIconView.setOpacity(RELOAD_OPACITY_MAX);
        }
        else
        {
            gunIconView.setOpacity(RELOAD_OPACITY_MIN);
        }
    }

    private Vector2D clampPlayerToPlayArea(Vector2D clampedPos)
    {
        player.getTransform().setPosition(clampedPos);
        player.setMovePos(clampedPos);
        return clampedPos;
    }

    private void updateWeaponTracking(Vector2D playerPos)
    {
        if (weapon != null) weapon.updatePosition(mousePosition, playerPos, WEAPON_NON_FOLLOW_AREA_SIZE);
    }


    private void updateCameraPosition(Vector2D playerPos, double canvasW, double canvasH, double zoom)
    {
        Vector2D cameraTarget = playerPos;
        if (worldWidth > 0 && worldHeight > 0)
        {
            double halfViewportW = (canvasW / 2.0) / zoom;
            double halfViewportH = (canvasH / 2.0) / zoom;

            double camX = cameraTarget.getX();
            double camY = cameraTarget.getY();

            if (halfViewportW <= worldWidth - halfViewportW)
                camX = Math.clamp(camX, halfViewportW, worldWidth - halfViewportW);
            else camX = worldWidth / 2.0;

            if (halfViewportH <= worldHeight - halfViewportH)
                camY = Math.clamp(camY, halfViewportH, worldHeight - halfViewportH);
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
        boolean pauseUi = newState && !gameOverState;
        boolean gameOverUi = newState && gameOverState;

        setVisibleManaged(pauseHeadingTile, pauseUi);
        setVisibleManaged(resumeButton, pauseUi);
        setVisibleManaged(pauseRetryButton, pauseUi);
        setVisibleManaged(pauseExitButton, pauseUi);

        setVisibleManaged(gameOverHeadingTile, gameOverUi);
        setVisibleManaged(gameOverNewGameButton, gameOverUi);
        setVisibleManaged(gameOverMainMenuButton, gameOverUi);
        setVisibleManaged(gameOverHighScoresButton, gameOverUi);

        if (overlayTitleLabel != null)
        {
            // Title text is replaced by heading image tiles.
            overlayTitleLabel.setVisible(false);
            overlayTitleLabel.setManaged(false);
        }

        pauseOverlay.setVisible(newState);
        pauseOverlay.setManaged(newState);
        this.setActive(!newState);
    }

    private static void setVisibleManaged(javafx.scene.Node node, boolean visible)
    {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }


    @FXML
    private void resumeGame()
    {
        if (gameOverState) return;
        setPaused(false);
    }

    @FXML
    private void exitToMainMenu()
    {
        MusicManager.resumeMusic();
        this.getWorld().setScene(new StartScene(this.getWorld()));
    }

    private void gameOver()
    {
        gameOverState = true;

        // Update heading tile based on win/defeat condition
        if (gameOverHeadingTile != null)
        {
            gameOverHeadingTile.getStyleClass().removeAll("win-heading", "defeat-heading");
            if (playerWon)
            {
                gameOverHeadingTile.getStyleClass().add("win-heading");
            }
            else
            {
                gameOverHeadingTile.getStyleClass().add("defeat-heading");
            }
        }

        // Show score display controller on game over
        if (scoreDisplayControllerContainer != null && scoreDisplayController != null)
        {
            // Clear previous content and add the controller
            scoreDisplayControllerContainer.getChildren().clear();
            scoreDisplayControllerContainer.getChildren().add(scoreDisplayController);

            // Update score display with final score
            scoreDisplayController.updateScoreDisplay(getCurrentScore());

            // Show the container
            scoreDisplayControllerContainer.setVisible(true);
            scoreDisplayControllerContainer.setManaged(true);
        }

        saveScoreIfNeeded();
        setPaused(true);
    }

    /**
     * Called when boss is defeated - triggers win condition
     */
    public void onBossDefeated()
    {
        if (score != null)
        {
            score.addBossKill();
        }
        playerWon = true;
        // Trigger game over after a brief delay to show the kill
        gameOver();
    }

    private void saveScoreIfNeeded()
    {
        if (scoreSaved) return;

        scoreSaved = true;
        HighScoreStorage.append(PlayerSession.getPlayerName(), getCurrentScore(), LocalDateTime.now());
    }

    private void loadGunIcon()
    {
        try
        {
            URL gunIconUrl = Resources.tryGetResource("scenes", "LevelScene", "ui", "gun.png");
            if (gunIconUrl != null)
            {
                Image gunIcon = new Image(gunIconUrl.toExternalForm());
                gunIconView.setImage(gunIcon);
            }
        }
        catch (Exception e)
        {
            System.out.println("[WARN] Failed to load gun icon: " + e.getMessage());
        }
    }

    @FXML
    private void retryLevel()
    {
        gameOverState = false;
        this.getWorld().setScene(new LevelScene(this.getWorld()));
    }

    @FXML
    private void startNewGame() { retryLevel(); }

    @FXML
    private void viewHighScores()
    {
        MusicManager.resumeMusic();
        saveScoreIfNeeded();
        this.getWorld().setScene(new HighScoresScene(this.getWorld()));
    }

    public int getCurrentScore() { return score != null ? score.getScore() : 0; }

    public void addScorePoints(int points)
    {
        if (score == null) return;
        score.addPoints(points);
    }

    public double getWorldWidth() { return worldWidth; }

    public double getWorldHeight() { return worldHeight; }

    public Player getPlayer() { return player; }

    public MapNodeSetter getNodeSetter() { return nodeSetter; }

    /* DEV COMMANDS */

    @Override
    protected void registerDevCommands()
    {
        // invincible mode (sets speed to 500 and health to 9999)
        registerDevCommand(
                "/invincible", "/invincible", "Toggle invincibility mode for the player.", args -> {
                    if (player == null) return "Player not initialized";
                    player.toggleInvincibility();
                    if (player.isInvincible())
                    {
                        return "Invincibility ON: Speed set to 500, Health set to 9999.";
                    }
                    else
                    {
                        return "Invincibility OFF: Speed reset to 50, Health reset to 100.";
                    }
                }
        );

        // dev mode (set camera zoom to 0.9; show mouse coordinates)
        registerDevCommand(
                "/devmode", "/devmode", "Toggle developer mode (camera zoom 0.9, show mouse coordinates).", args -> {
                    if (this.getWorldCamera() == null) return "Camera not initialized";
                    boolean devMode = this.getWorldCamera().getZoom() != 0.9;
                    this.getWorldCamera().setZoom(devMode ? 0.9 : 4);
                    this.toggleDebugMouseLocation();
                    return "Developer mode " + (devMode ? "ON" : "OFF");
                }
        );

        // speed
        registerDevCommand(
                "/setPlayerSpeed", "/setPlayerSpeed <speed>", "Set player's movement speed directly.", args -> {
                    if (args.isEmpty()) return "Usage: /setPlayerSpeed <speed>";
                    if (player == null) return "Player not initialized";

                    int speed;
                    try
                    {
                        speed = Integer.parseInt(args.getFirst());
                    }
                    catch (NumberFormatException e)
                    {
                        return "Invalid speed: " + args.getFirst();
                    }

                    if (speed < 0) return "Speed must be >= 0";
                    player.setMovementSpeed(speed);
                    return "playerSpeed = " + player.getMovementSpeed();
                }
        );

        // ammo
        registerDevCommand(
                "/setCurrentAmmo", "/setCurrentAmmo <amount>", "Set weapon current ammo directly.", args -> {
                    if (args.isEmpty()) return "Usage: /setCurrentAmmo <amount>";
                    if (weapon == null || weapon.getAmmo() == null) return "Weapon not initialized";

                    int amount;
                    try
                    {
                        amount = Integer.parseInt(args.getFirst());
                    }
                    catch (NumberFormatException e)
                    {
                        return "Invalid ammo amount: " + args.getFirst();
                    }

                    weapon.setCurrentAmmo(amount);
                    return "ammo = " + weapon.getAmmo().getCurrentAmmo() + "/" + weapon.getAmmo().getMaxAmmo();
                }
        );

        // show level collision mask rects
        registerDevCommand(
                "/showLevelCollisionMask",
                "/showLevelCollisionMask",
                "Show level collision mask rectangles for 5 seconds.",
                args -> {
                    Level1CollisionMask.forEachRect(rect -> addTimedDebugRectangle(rect, TimeSpan.fromSeconds(5)));
                    return "Showing level collision mask rectangles for 5 seconds.";
                }
        );

        // toggle all hitboxes
        registerDevCommand(
                "/toggleAllHitboxes",
                "/toggleAllHitboxes true|false|empty",
                "Toggle hitbox visualization for all game objects that have a HitBox component.",
                args -> {
                    if (args.isEmpty())
                    {
                        allHitboxesVisible = !allHitboxesVisible;
                    }
                    else
                    {
                        String value = args.getFirst().toLowerCase();
                        if (value.equals("true") || value.equals("1") || value.equals("on") || value.equals("yes"))
                            allHitboxesVisible = true;
                        else if (value.equals("false") || value.equals("0") || value.equals("off") || value.equals("no"))
                            allHitboxesVisible = false;
                        else return "Usage: /debugAllHitboxes true|false";
                    }

                    return "toggleAllHitboxes = " + allHitboxesVisible;
                }
        );

        // show no aim follow area
        registerDevCommand(
                "/showNoAimFollowArea",
                "/showNoAimFollowArea",
                "Show the area around the player where the weapon does not follow the mouse.",
                args -> {
                    if (player == null) return "Player not initialized";
                    Vector2D playerPos = player.getTransform().getPosition();
                    Rectangle nonFollowArea = new Rectangle(
                            playerPos.subtract(WEAPON_NON_FOLLOW_AREA_SIZE.multiply(0.5)),
                            WEAPON_NON_FOLLOW_AREA_SIZE
                    );
                    addTimedDebugRectangle(nonFollowArea);
                    return "Showing weapon non-follow area for 5 seconds.";
                }
        );

        // move player to position
        registerDevCommand(
                "/movePlayerTo", "/movePlayerTo <x> <y>", "Move player to specified coordinates directly.", args -> {
                    if (args.size() < 2) return "Usage: /movePlayerTo <x> <y>";
                    if (player == null) return "Player not initialized";

                    player.setX(Double.parseDouble(args.getFirst()));
                    player.setY(Double.parseDouble(args.get(1)));
                    return "Moving player to (" + args.getFirst() + ", " + args.get(1) + ")";
                }
        );

        // mouse player to mouse
        registerDevCommand(
                "/movePlayerToMouse", "/movePlayerToMouse", "Move player to current mouse position directly.", args -> {
                    if (player == null) return "Player not initialized";

                    player.setX(mousePosition.getX());
                    player.setY(mousePosition.getY());
                    return "Moving player to mouse position (" + mousePosition.getX() + ", " + mousePosition.getY() + ")";
                }
        );
    }
}
