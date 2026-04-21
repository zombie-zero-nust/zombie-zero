package edu.nust.engine.core;

import edu.nust.Main;
import edu.nust.engine.core.audio.GameAudioManager;
import edu.nust.engine.core.audio.MusicTrackReference;
import edu.nust.engine.core.audio.SoundEffectReference;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.animation.AnimationTimer;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents the entire game world, managing the main window, scenes, and the game loop.
 * <br><br>
 * The GameWorld is responsible for initializing the main window ({@link Stage}), switching between {@link GameScene}s,
 * and running the main game loop.
 * <br><br>
 * To use, create a subclass of GameWorld and implement the {@link GameWorld#initStage} method to set up window
 * properties. Then, in the program entry point (e.g., {@link Main#start(Stage stage)}), create an instance of your
 * GameWorld subclass and call {@link GameWorld#start }to begin the game loop.
 */
public abstract class GameWorld
{
    protected final GameLogger logger = GameLogger.getLogger(this.getClass());

    protected final Stage stage;
    // When changing "scenes", we just change root
    private final Scene scene;
    private final StackPane sceneRoot;

    private GameScene currentGameScene;

    private final AnimationTimer gameLoop;

    private final GameAudioManager audioManager;

    /**
     * Use this to set up window ({@link GameWorld#stage}) properties
     * <br>
     * Examples of properties include title, window size, etc.
     */
    protected abstract void initStage();

    /**
     * Use this to preload audios using {@link GameAudioManager}.
     * <br><br>
     * Call {@link #loadSoundEffect(String...)} and {@link #loadMusicTrack(String...)} here to preload assets before the
     * first scene initializes.
     *
     * @see #loadSoundEffect(String...)
     * @see #loadMusicTrack(String...)
     */
    protected abstract void loadAudios();

    public GameWorld(Stage stage)
    {
        logger.trace("Constructing GameWorld: {}", this.getClass().getSimpleName());
        this.stage = stage;

        // setup scene
        logger.trace("Setting up Scene and StackPane");
        this.sceneRoot = new StackPane();
        this.scene = new Scene(this.sceneRoot);
        this.stage.setScene(this.scene);

        // bind `root.size` to `stage.size` so that when stage is resized
        logger.trace("Binding scene root dimensions to stage");
        this.sceneRoot.prefWidthProperty().bind(this.stage.widthProperty());
        this.sceneRoot.prefHeightProperty().bind(this.stage.heightProperty());

        logger.trace("Loading common CSS stylesheet");
        if (GameURLs.COMMON_CSS_URL != null)
        {
            this.scene.getStylesheets().add(GameURLs.COMMON_CSS_URL.toExternalForm());
            logger.debug("Loaded {}", GameURLs.COMMON_CSS_FILENAME);
        }
        else
        {
            logger.warn(
                    "\"{}\" not found, ensure 'resources/edu/nust/game/{}/{}' if not intentional.",
                    GameURLs.COMMON_CSS_FILENAME,
                    GameURLs.SCENES_ROOT_DIR,
                    GameURLs.COMMON_CSS_FILENAME
            );
        }

        logger.trace("Calling initStage() for subclass setup");
        initStage();

        logger.trace("Creating game loop AnimationTimer");
        this.gameLoop = new AnimationTimer()
        {
            private long lastTime = 0;

            @Override
            public void handle(long now)
            {
                // if first frame, initialize lastTime and skip update
                if (lastTime == 0)
                {
                    lastTime = now;
                    return;
                }

                // calculate time between this frame and last frame
                long deltaTimeNs = now - lastTime;
                lastTime = now; // update lastTime for next frame

                if (currentGameScene != null)
                    currentGameScene.invokeGameLoopFrame(TimeSpan.fromNanoseconds(deltaTimeNs));
            }
        };

        this.audioManager = new GameAudioManager();

        this.loadFont();

        logger.info("Loading audio assets");
        this.loadAudios();

        logger.success("Game World initialized successfully");
    }

    /// Call in program entry point i.e. [Main#start(Stage stage)], Starts the Game Loop
    public void start()
    {
        logger.trace("Starting GameWorld and game loop");
        logger.success("Game started");
        stage.show();
        gameLoop.start();
    }

    /// Call to exit and close
    public void stop()
    {
        logger.trace("Stopping GameWorld and game loop");
        gameLoop.stop();
        stage.close();
        logger.success("Game stopped");
    }

    /* SCENE */

    /// Switches to the given [GameScene]
    public GameWorld setScene(GameScene newScene)
    {
        logger.trace("setScene({}) called", newScene.getClass().getSimpleName());
        LogProgress sceneSwitchLogger = LogProgress.create("SCENE_SWITCH", logger);
        sceneSwitchLogger.begin("Switching Scene to {}", newScene.getClass().getSimpleName());

        this.currentGameScene = newScene;

        logger.trace("Creating world layer");
        Region worldLayer = newScene.getWorldLayer();
        // bind world scene size to root size
        logger.trace("Binding world layer dimensions to root");
        worldLayer.prefWidthProperty().bind(sceneRoot.widthProperty());
        worldLayer.prefHeightProperty().bind(sceneRoot.heightProperty());

        // add to root so `this.scene` is updated
        logger.trace("Setting both layers to root");
        this.sceneRoot.getChildren().setAll(worldLayer, newScene.getUILayer(), newScene.getConsoleLayer());

        sceneSwitchLogger.end("Switched to Scene {} successfully", newScene.getClass().getSimpleName());

        return this;
    }

    /// Switches to the given [GameScene]
    public GameWorld setScene(Supplier<GameScene> newScene) { return setScene(newScene.get()); }

    /// Get the current [GameScene]
    public GameScene getScene() { return currentGameScene; }

    /**
     * Only used in {@link edu.nust.engine.core} for internal purposes.
     * <br>
     * <br>
     * Used for adding stylesheets, events, etc.
     */
    Scene getRawScene() { return scene; }

    /* AUDIO */

    /**
     * @return {@link GameAudioManager}
     */
    public GameAudioManager getAudioManager() { return audioManager; }

    /**
     * Retrieves a loaded {@link SoundEffectReference} by filename (with extension).
     * <br><br>
     * Shorthand for {@link GameAudioManager#getSoundEffectByName(String)}.
     *
     * @param name The filename with extension, e.g. {@code "click.wav"}
     *
     * @return The optional loaded {@link SoundEffectReference}, or {@link Optional#empty()} if the file could not be
     * found or loaded
     */
    public Optional<SoundEffectReference> getSoundEffectByName(String name)
    {
        return audioManager.getSoundEffectByName(name);
    }

    /**
     * Loads a {@link SoundEffectReference} from the given path relative to {@code edu/nust/game/assets/audio/}. If the
     * file has already been loaded, the cached reference is returned.
     * <br><br>
     * Shorthand for {@code  world.getAudioManager().loadSoundEffect(relPath)}.
     * <br><br>
     * <b>{@code Use .wav instead of .mp3}</b>
     *
     * @param relPath Path relative to {@code edu/nust/game/assets/audio/} split, e.g. {@code ("sfx", "click.wav")} for
     *                {@code edu/nust/game/assets/audio/sfx/click.wav}
     *
     * @return The optional loaded {@link SoundEffectReference}, or {@link Optional#empty()} if the file could not be
     * found or loaded
     */
    public Optional<SoundEffectReference> loadSoundEffect(String... relPath)
    {
        return audioManager.loadSoundEffect(relPath);
    }

    /**
     * Retrieves a loaded {@link MusicTrackReference} by filename (with extension).
     * <br><br>
     * Shorthand for {@link GameAudioManager#getMusicTrackByName(String)}.
     *
     * @param name The filename with extension, e.g. {@code "bg_music.wav"}
     *
     * @return The optional loaded {@link MusicTrackReference}, or {@link Optional#empty()} if the file could not be
     * found or loaded
     */
    public Optional<MusicTrackReference> getMusicTrackByName(String name) { return audioManager.getMusicTrackByName(name); }

    /**
     * Loads a {@link MusicTrackReference} from the given path relative to {@code edu/nust/game/assets/audio/}. If the
     * file has already been loaded, the cached reference is returned.
     * <br><br>
     * Shorthand for {@code  world.getAudioManager().loadMusicTrack(relPath)}.
     * <br><br>
     * <b>{@code Use .wav instead of .mp3}</b>
     *
     * @param relPath Path relative to {@code edu/nust/game/assets/audio/} split, e.g. {@code ("sfx", "bg_music.wav")}
     *                for {@code edu/nust/game/assets/audio/sfx/click.wav}
     *
     * @return The loaded {@link MusicTrackReference}, or {@link Optional#empty()} if the file could not be found or
     * loaded
     */
    public Optional<MusicTrackReference> loadMusicTrack(String... relPath) { return audioManager.loadMusicTrack(relPath); }

    /* FONT */

    private void loadFont()
    {
        loadFontFile("PixelifySans.ttf");
        loadFontFile("ShareTechMono-Regular.ttf");
        loadFontFile("Oxanium-VariableFont_wght.ttf");
    }

    private void loadFontFile(String fileName)
    {
        try
        {
            Font font = Font.loadFont(Resources.getResourceOrThrow("assets", "fonts", fileName).openStream(), 12);

            if (font == null) throw new RuntimeException("Font returned null");

            logger.success("Loaded font '{}': {}", fileName, font.getName());
        }
        catch (Exception e)
        {
            logger.error(false, "Failed to load font '{}': {}", fileName, e.getMessage());
        }
    }

    /* UTILITIES */

    /// Gets the size of the window as a Vector2D (width, height)
    public Vector2D getSize() { return new Vector2D(stage.getWidth(), stage.getHeight()); }

    /// Gets the width of the window
    public double getWidth() { return stage.getWidth(); }

    /// Gets the height of the window
    public double getHeight() { return stage.getHeight(); }

    /// Gets the title of the window
    public String getWindowTitle() { return stage.getTitle(); }

    /// Gets the current cursor of the window
    public Cursor getCursor() { return stage.getScene().getCursor(); }

    /// Gets whether the cursor is visible
    public boolean isCursorVisible() { return getCursor() != Cursor.NONE; }

    /// Gets whether the window is resizable
    public boolean isFullscreen() { return stage.isFullScreen(); }

    /// **`CHAINABLE`** Sets the size of the window
    public GameWorld setSize(double width, double height)
    {
        stage.setWidth(width);
        stage.setHeight(height);
        return this;
    }

    /// **`CHAINABLE`** Sets the width of the window
    public GameWorld setWidth(double width)
    {
        stage.setWidth(width);
        return this;
    }

    /// **`CHAINABLE`** Sets the height of the window
    public GameWorld setHeight(double height)
    {
        stage.setHeight(height);
        return this;
    }

    /// **`CHAINABLE`** Sets whether the window is in fullscreen mode
    public GameWorld setFullscreen(boolean fullscreen)
    {
        stage.setFullScreen(fullscreen);
        return this;
    }

    /// **`CHAINABLE`** Toggles whether the window is in fullscreen mode
    public GameWorld toggleFullscreen()
    {
        setFullscreen(!stage.isFullScreen());
        return this;
    }

    /// **`CHAINABLE`** Sets the title of the window
    public GameWorld setWindowTitle(String title)
    {
        stage.setTitle(title);
        return this;
    }

    /// **`CHAINABLE`** Sets the cursor of the window
    public GameWorld setCursor(Cursor cursor)
    {
        stage.getScene().setCursor(cursor);
        return this;
    }

    /// **`CHAINABLE`** Sets whether the cursor is visible
    public GameWorld setCursorVisible(boolean visible)
    {
        if (!visible) setCursor(Cursor.NONE);
        return this;
    }

    /// **`CHAINABLE`** Toggles whether the cursor is visible
    public GameWorld toggleCursorVisible()
    {
        setCursorVisible(!isCursorVisible());
        return this;
    }

    /// **`CHAINABLE`** Toggles whether the window is in fullscreen mode
    public GameWorld centerWindow()
    {
        stage.centerOnScreen();
        return this;
    }

    /// **`CHAINABLE`** Sets whether the window is resizable
    public GameWorld setResizable(boolean resizable)
    {
        stage.setResizable(resizable);
        return this;
    }
}
