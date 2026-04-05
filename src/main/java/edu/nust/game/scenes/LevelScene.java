package edu.nust.game.scenes;


import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.Score;
import edu.nust.game.gameobjects.*;
import edu.nust.game.gameobjects.Bullet;
import edu.nust.game.gameobjects.Player;
import edu.nust.game.gameobjects.PlayerTag;
import edu.nust.game.gameobjects.OrbitingBox;
import edu.nust.game.gameobjects.Enemy;
import edu.nust.game.gameobjects.EnemyTag;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

/**
 * LevelScene - Main game level with player, weapon, and enemy
 *
 * Game Flow:
 * 1. Scene initializes: Creates player at origin, weapon, and enemy
 * 2. Every frame:
 *    - Input handlers process keyboard and mouse events
 *    - lateUpdate() synchronizes all game objects
 *    - Weapon tracks mouse, enemy tracks player
 *    - Camera follows player
 * 3. Player moves with WASD, weapon aims with mouse, enemy chases player
 */
import java.util.ArrayList;

public class LevelScene extends GameScene
{
    @FXML private StackPane pauseOverlay;
    @FXML private VBox helpTextContainer;
    @FXML private Label scoreLabel; // Label to display current score
    private boolean isPaused = false;
    private Player player;
    private GameObject weaponBox;
    private OrbitingBox weaponComponent;
    private Enemy enemy;
    // Score tracking
    private Score score; // Score system instance
    // Input tracking
    private Vector2D mousePosition = Vector2D.zero(); // Current mouse position in world coordinates

    /**
     * Constructor - Scene is created when level starts
     *
     * @param level The GameWorld window
     */
    public LevelScene(GameWorld level) { super(level); }

    /**
     * Initialize all game objects at the start of the level Called once when scene is created
     */
    @Override
    public void onInit()
    {
        // ===== INITIALIZE SCORE SYSTEM =====
        // Create and initialize score tracker at start of game
        score = new Score();

        // ===== CREATE CHARACTER =====
        // Player spawns at origin (0,0) with 100 health, 500 movement speed
        player = new Player(new Vector2D(0, 0), 100, 500, true);
        // Add player to scene and tag so we can find it later with getFirstWithTag()
        this.addGameObject(player.addTag(PlayerTag.class));


        // Create weapon box
        // ===== CREATE WEAPON =====
        // Create a new GameObject to hold the weapon
        weaponBox = GameObject.create();
        // Create the weapon component with 80 unit orbit distance
        weaponComponent = new OrbitingBox(80);
        // Create a cyan box renderer (40x40 pixels)
        BoxRenderer boxRenderer = new BoxRenderer(40, 40, Color.CYAN);
        // Add components to the weapon GameObject
        weaponBox.addComponent(weaponComponent);
        weaponBox.addComponent(boxRenderer);
        // Add weapon to scene
        this.addGameObject(weaponBox);

        // ===== CREATE ENEMY =====
        // Enemy spawns at position (300, 0) with 100 units/sec movement speed
        // Spawned at fixed position instead of random (camera not initialized yet)
        enemy = new Enemy(new Vector2D(300, 0), 100);
        // Add EnemyTag so we can identify enemies later
        this.addGameObject(enemy.addTag(EnemyTag.class));
    }

    /**
     * Called every frame - Handle game updates This is called before lateUpdate()
     *
     * @param deltaTime Time elapsed since last frame (seconds)
     */
    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        // Main update logic is handled in lateUpdate()
        // This method is called first, but we do updates after all objects update
    }

    /**
     * Called after all GameObjects update - Synchronize game state This is the perfect place to sync weapon/enemy with
     * player position
     *
     * @param deltaTime Time elapsed since last frame (seconds)
     */
    @Override
    public void lateUpdate(TimeSpan deltaTime)
    {
        // ===== UPDATE SCORE =====
        // Update score tracker every frame (increments score every 5 seconds)
        score.update(deltaTime);
        // Update score display label on UI
        if (scoreLabel != null)
        {
            scoreLabel.setText(String.valueOf(score.getScore()));
        }

        // Get the player GameObject using the PlayerTag
        GameObject character = this.getFirstWithTag(PlayerTag.class);

        // If no player found, exit early (shouldn't happen)
        if (character == null) return;

        // ===== UPDATE WEAPON =====
        // Tell weapon where the mouse is in world coordinates
        if (weaponComponent != null)
        {
            weaponComponent.updateMousePosition(mousePosition);
            // Tell weapon to position itself based on character and mouse
            weaponComponent.updatePositionBasedOnMouse(character.getTransform().getPosition());
        }

        // ===== UPDATE ENEMY =====
        // Tell enemy where the player is (every frame)
        if (enemy != null)
        {
            // Enemy will move towards this position in its onUpdate()
            enemy.setTargetPosition(character.getTransform().getPosition());

            // ===== COLLISION DETECTION - DEDUCT SCORE =====
            // Check if player and enemy have collided (approximately at same position)
            Vector2D playerPos = character.getTransform().getPosition();
            Vector2D enemyPos = enemy.getTransform().getPosition();
            double distance = Vector2D.subtract(playerPos, enemyPos).magnitude();

            // If distance is very small (collision threshold ~50 units), deduct points
            if (distance < 50)
            {
                // Deduct 2 points when player hits enemy
                score.setScore(score.getScore() - 2);

                // Check if score went below or equal to 0 - game is over
                if (score.getScore() < 0)
                {
                    // Set score to 0 and trigger game over
                    score.setScore(0);
                    gameOver();
                }
                else
                {
                    // Score is still positive, remove current enemy and spawn a new one
                    // Remove enemy from scene
                    this.removeGameObject(enemy);

                    // Spawn new enemy at a random edge position
                    enemy = new Enemy(getRandomEdgePosition(), 100);
                    this.addGameObject(enemy.addTag(EnemyTag.class));
                }
            }
        }

        // ===== UPDATE CAMERA =====
        // Make camera follow the player so player stays centered
        this.getWorldCamera().setPosition(character.getTransform().getPosition());
    }

    /**
     * Handle keyboard input when a key is pressed
     *
     * @param event The keyboard event (contains which key was pressed)
     */
    @Override
    public void onKeyPressed(KeyEvent event)
    {
        // Pass the key press to the player (handles WASD movement)
        player.keyPress(event.getCode());

        // Handle scene-specific keys
        if (event.getCode() == KeyCode.ESCAPE)
        {
            // ESC = Pause the game
            setPaused(true);
        }
        else if (event.getCode() == KeyCode.G)
        {
            // G = Toggle debug grid to see movement better
            this.toggleDebugGrid();
        }
    }

    /**
     * Handle keyboard input when a key is released
     *
     * @param event The keyboard event (contains which key was released)
     */
    @Override
    public void onKeyReleased(KeyEvent event)
    {
        // Pass the key release to the player (stops movement when WASD released)
        player.keyRelease(event.getCode());
    }

    /**
     * Handle mouse movement input Converts screen coordinates to world coordinates and stores position
     * <p>
     * Coordinate Conversion: 1. Get screen pixel coordinates from event (0,0 is top-left of screen) 2. Get canvas
     * dimensions and center point 3. Apply camera position and zoom to convert to world coordinates 4. Store for weapon
     * to use
     *
     * @param event The mouse event (contains screen position)
     */
    @Override
    public void onMouseMoved(MouseEvent event)
    {
        // Get mouse position in screen coordinates (pixels)
        double screenX = event.getX();
        double screenY = event.getY();

        // Get canvas (viewport) dimensions
        double canvasW = this.getWorldLayer().getWidth();
        double canvasH = this.getWorldLayer().getHeight();

        // Get camera information
        Vector2D cameraPos = this.getWorldCamera().getPosition();
        double zoom = this.getWorldCamera().getZoom();

        // ===== CONVERT SCREEN TO WORLD COORDINATES =====
        // Formula: worldPosition = cameraPosition + (screenOffset / zoom)
        // screenOffset = (screenPixel - canvasCenter)
        // This accounts for camera position and zoom level
        double worldX = cameraPos.getX() + (screenX - canvasW / 2) / zoom;
        double worldY = cameraPos.getY() + (screenY - canvasH / 2) / zoom;

        // Store the converted world position for weapon to use
        this.mousePosition = new Vector2D(worldX, worldY);
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {

        //test for checking bullet
        if (event.getButton() == MouseButton.PRIMARY)
        {
            this.addGameObject(new Bullet(1000, weaponBox.getTransform().getPosition(), 1000, 30, 30, mousePosition));
        }
    }

    public void addBullets(Bullet bullet)
    {

    }

    /**
     * Pause or unpause the game
     *
     * @param newState True to pause, false to resume
     */
    private void setPaused(boolean newState)
    {
        // Store pause state
        this.isPaused = newState;
        // Show/hide pause overlay UI
        pauseOverlay.setVisible(newState);
        pauseOverlay.setManaged(newState);
        // Stop updating game when paused, resume when unpaused
        this.setActive(!newState);
    }

    /**
     * Calculate a random position on the screen edge in world coordinates Used for spawning enemies at random edges
     * <p>
     * Algorithm: 1. Calculate camera's visible world area bounds 2. Randomly pick one of 4 edges (top, bottom, left,
     * right) 3. Randomly pick a position along that edge 4. Return the world coordinate
     *
     * @return A Vector2D position on a random screen edge
     */
    private Vector2D getRandomEdgePosition()
    {
        // Get camera position and canvas dimensions
        Vector2D cameraPos = this.getWorldCamera().getPosition();
        double canvasW = this.getWorldLayer().getWidth();
        double canvasH = this.getWorldLayer().getHeight();
        double zoom = this.getWorldCamera().getZoom();

        // Calculate the visible world area bounds (what's visible on screen)
        // Half dimensions of visible area
        double halfW = canvasW / 2.0 / zoom;
        double halfH = canvasH / 2.0 / zoom;

        // World coordinates of visible edges
        double left = cameraPos.getX() - halfW;
        double right = cameraPos.getX() + halfW;
        double top = cameraPos.getY() - halfH;
        double bottom = cameraPos.getY() + halfH;

        // Randomly pick an edge: 0=top, 1=bottom, 2=left, 3=right
        int edge = (int) (Math.random() * 4);
        double x, y;

        // Calculate random position along chosen edge
        switch (edge)
        {
            case 0: // Top edge - random x, y = top
                x = left + Math.random() * (right - left);
                y = top;
                break;
            case 1: // Bottom edge - random x, y = bottom
                x = left + Math.random() * (right - left);
                y = bottom;
                break;
            case 2: // Left edge - x = left, random y
                x = left;
                y = top + Math.random() * (bottom - top);
                break;
            case 3: // Right edge - x = right, random y
                x = right;
                y = top + Math.random() * (bottom - top);
                break;
            default: // Fallback (shouldn't happen)
                x = 0;
                y = 0;
        }

        return new Vector2D(x, y);
    }

    /**
     * Resume the game (called from pause menu button)
     */
    @FXML
    private void resumeGame()
    {
        setPaused(false);
    }

    /**
     * Exit to main menu (called from pause menu button)
     */
    @FXML
    private void exitToMainMenu()
    {
        this.getWindow().setScene(new StartScene(this.getWindow()));
    }

    /**
     * Handle game over when player collides with enemy Pause the scene and show game over options
     */
    private void gameOver()
    {
        // Pause the game to stop all updates
        setPaused(true);

        // Show game over message in pause overlay
        pauseOverlay.setVisible(true);
        pauseOverlay.setManaged(true);
    }

    /**
     * Retry the level (called from pause menu button) Reloads the level by creating a new LevelScene
     */
    @FXML
    private void retryLevel()
    {
        // Final score when player loses is stored in 'score' object before creating new scene
        this.getWindow().setScene(new LevelScene(this.getWindow()));
    }

    /**
     * Get the current score value Used for displaying score to player
     *
     * @return Current score as integer
     */
    public int getCurrentScore()
    {
        return score != null ? score.getScore() : 0;
    }

}
