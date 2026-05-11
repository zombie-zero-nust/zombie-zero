# Zombie Zero - Feature Slides and OOP Examples

## Intro Slides (high level)

### Slide 1 - Title and goal

**Slide intent**
Present the project title, genre, and objective at a glance.

**Key points**

- Title: Zombie Zero (top-down 2D shooter)
- Objective: clear all zombies in the city
- Built on custom Java engine using JavaFX

### Slide 2 - Architecture overview

**Slide intent**
Explain the two-layer structure so later slides make sense.

**Key points**

- Engine layer: reusable runtime (core, math, audio, logging, resources)
- Game layer: scenes, systems, and content built on the engine

### Slide 3 - Runtime flow

**Slide intent**
Show how the app starts and how scenes switch.

**Key points**

- Entry flow: `Launcher` -> `Main` -> `MainWorld`
- Game loop ticks `GameScene` and its `GameObject`s
- Scene switching happens in `GameWorld.setScene(...)`

### Slide 4 - Feature selection slide

**Slide intent**
Give the audience a menu of features to pick from.

**Key points**

- Grouped feature lists below (Engine, Systems & Deployment, Scenes)
- Each curated point has at least one slide

### Slide 5 - Feature index (curated, grouped)

#### Engine

1. GameWorld
2. GameScene
    - lifecycle and layering
    - GameCamera (position, zoom, shake)
    - Render order and culling
    - Debug rendering in GameScene
3. GameObject base entity
4. Component system
    - SpriteRenderer
    - Shape renderers (BoxRenderer, CircleRenderer)
    - WorldBoundsProvider
5. Rendering pipeline (world + UI layers)
6. Input routing
7. DevConsole
    - Command system
8. Resources loader (Resources + GameURLs + URLUtils)
9. Math library (Vector2D, Angle, Rectangle, TimeSpan)
10. GameLogger (levels + formatting)
    - Colored formatted logging
    - Levels
    - Logging to console
    - Logging to file
11. Audio management
    - Audio reference system (SoundEffectReference, MusicTrackReference)
    - GameAudioManager (cache + volume)

#### Systems & Deployment

1. AssetManager cache
2. CollisionManager
3. HitBox component
4. PathFinder (A* grid)
    - MapNodeSetter (solid grid build)
5. Weapon system (Weapon + Ammo + Bullet)
6. HUD bars (Bar + HealthBar + AmmoBar)
7. ScoreDisplayController (sprite digits)
8. Audio management
    - MusicManager
    - Audios registry
9. Gradle packaging
    - packageWindows
    - packageLinux
    - packageMac
    - buildExecutableJar
10. Build/run tasks
    - run
    - runNoLogs
    - runTestLogger
11. UML generation pipeline
    - Generate UML using Python
    - Generate separate Mermaid files per package

#### Scenes

1. StartScene
2. LevelSelectScene
3. LevelScene (core gameplay)
    - CollisionMask
    - SpawnPoints generation
    - Background objects + placements file
    - StaticObjectFactory + SerializablePlacement
4. HighScoresScene
    - HighScoreStorage
    - PlayerSession
    - Score system

## Feature Slides (working of, grouped by curated list)

### Engine

#### Slide 6 - GameWorld

**Slide intent**
Explain how the window, scene switching, and game loop are centralized.

**Working**
`GameWorld` owns the JavaFX `Stage` and `Scene`, drives the `AnimationTimer` loop, and delegates each frame to the
active `GameScene`.

**Technical breakdown**

- Creates the root `StackPane` and binds it to the stage size
- Runs a smoothing FPS calculation
- Switches scenes by swapping root children (world + UI + console)

**Usage**
Subclass `GameWorld`, override `initStage()` and `loadAudios()`, then call `start()`.

**Related files**

- `src/engine/java/edu/nust/engine/core/GameWorld.java`
- `src/game/java/edu/nust/game/MainWorld.java`

#### Slide 7 - GameScene: lifecycle and layering

**Slide intent**
Show how a scene manages world objects, UI, and debug/console layers.

**Working**
`GameScene` owns the game object list, the world canvas, and the FXML UI layer, and manages input hooks.

**Technical breakdown**

- Loads `layout.fxml` and optional `style.css` per scene
- Maintains add/remove queues to avoid concurrent modification
- Hosts the world camera, render ordering, and debug overlay toggles

**Usage**
Subclass `GameScene`, implement `onInit()` and input handlers as needed.

**Related files**

- `src/engine/java/edu/nust/engine/core/GameScene.java`

#### Slide 8 - GameScene: GameCamera (position, zoom, shake)

**Slide intent**
Highlight camera controls and screen shake.

**Working**
Camera stores view center, zoom, and procedural shake offsets.

**Technical breakdown**

- Zoom controlled with safety checks
- Shake uses intensity decay per second

**Usage**
`scene.getWorldCamera().setPosition(...).setZoom(...).shake(...)`

**Related files**

- `src/engine/java/edu/nust/engine/core/GameCamera.java`

#### Slide 9 - GameScene: Render order and culling

**Slide intent**
Explain how ordering and visibility optimization work.

**Working**
Objects are sorted by `renderLayer` and culled by camera bounds.

**Technical breakdown**

- `renderLayer` integer controls sort
- `WorldBoundsProvider` defines bounds for culling

**Usage**
Set `gameObject.setRenderLayer(...)` and implement bounds provider.

**Related files**

- `src/engine/java/edu/nust/engine/core/GameScene.java`
- `src/engine/java/edu/nust/engine/core/interfaces/WorldBoundsProvider.java`

#### Slide 10 - GameScene: Debug rendering

**Slide intent**
Show built-in visual debugging aids.

**Working**
Grid, mouse crosshair, and timed/frame debug shapes render on the world canvas.

**Technical breakdown**

- Timed shapes stored in a set and expire by `TimeSpan`
- Frame-only shapes cleared each frame
- Grid snaps to camera view with zoom

**Usage**
Use `addFrameDebugRectangle`, `toggleDebugGrid`, etc.

**Related files**

- `src/engine/java/edu/nust/engine/core/GameScene.java`
- `src/engine/java/edu/nust/engine/core/debug/DebugShape.java`

#### Slide 11 - GameObject base entity

**Slide intent**
Describe the base class for all in-world objects.

**Working**
`GameObject` is an entity that can be updated/rendered and composed with components and tags.

**Technical breakdown**

- Holds component list and tag set
- Supports add/get/remove component utilities
- Exposes lifecycle hooks (`onInit`, `onUpdate`, `onRender`)

**Usage**
Subclass `GameObject` (e.g., `Player`, `Weapon`) or use `GameObject.create()` factory.

**Related files**

- `src/engine/java/edu/nust/engine/core/GameObject.java`

#### Slide 12 - Component system (overview)

**Slide intent**
Explain composition-based behavior on top of `GameObject`.

**Working**
Components add rendering/logic without subclass explosion.

**Technical breakdown**

- `Component` is `Initiable`, `Updatable`, `Renderable`
- Attached to exactly one `GameObject`
- Common implementations include renderers and physics helpers

**Usage**
Create new components (e.g., renderers, hitboxes) and add to objects.

**Related files**

- `src/engine/java/edu/nust/engine/core/Component.java`

#### Slide 13 - Component system: SpriteRenderer

**Slide intent**
Explain sprite sheet rendering and animation controls.

**Working**
`SpriteRenderer` draws a sprite or sprite sheet with frame control.

**Technical breakdown**

- Frame slicing via `columns` and `rows`
- `animationTime` advances frames in `onUpdate`
- Supports tint and opacity

**Usage**
`new SpriteRenderer(width, height, image, framesX, framesY)`

**Related files**

- `src/engine/java/edu/nust/engine/core/components/renderers/SpriteRenderer.java`

#### Slide 14 - Component system: Shape renderers (overview)

**Slide intent**
Introduce vector-based primitives for UI and debug visuals.

**Working**
Shape renderers draw rectangles or circles with optional stroke/fill.

**Technical breakdown**

- Shared base: `ShapeRenderer` with fill/stroke controls
- Implements `WorldBoundsProvider` for culling

**Usage**
Add a shape renderer to a `GameObject` for quick visual debugging.

**Related files**

- `src/engine/java/edu/nust/engine/core/components/renderers/ShapeRenderer.java`

#### Slide 15 - Component system: BoxRenderer

**Slide intent**
Show how rectangular primitives are rendered.

**Working**
`BoxRenderer` draws rounded rectangles with fill/stroke controls.

**Technical breakdown**

- Uses anchor-based offsets from `Transform`
- Exposes size and corner radius

**Usage**
`gameObject.addComponent(new BoxRenderer(width, height, color))`

**Related files**

- `src/engine/java/edu/nust/engine/core/components/renderers/BoxRenderer.java`

#### Slide 16 - Component system: CircleRenderer

**Slide intent**
Show how circular primitives are rendered.

**Working**
`CircleRenderer` draws circles with fill/stroke controls.

**Technical breakdown**

- Uses radius + anchor to compute bounds
- Supports stroke width and color

**Usage**
`gameObject.addComponent(new CircleRenderer(radius, color))`

**Related files**

- `src/engine/java/edu/nust/engine/core/components/renderers/CircleRenderer.java`

#### Slide 17 - Component system: WorldBoundsProvider

**Slide intent**
Explain how render culling is decoupled from object types.

**Working**
Components can provide world bounds for culling without knowing the scene.

**Technical breakdown**

- Interface returns `Rectangle` bounds
- Scene checks bounds before rendering

**Usage**
Implement `WorldBoundsProvider` on renderers.

**Related files**

- `src/engine/java/edu/nust/engine/core/interfaces/WorldBoundsProvider.java`

#### Slide 18 - Rendering pipeline (world + UI layers)

**Slide intent**
Show how 2D world and UI are layered together.

**Working**
World objects render to a `Canvas` while UI is standard JavaFX nodes.

**Technical breakdown**

- `worldLayer` is a `Canvas` inside `StackPane`
- `uiLayer` loaded from FXML and CSS
- `consoleLayer` stacked on top

**Usage**
Place UI in FXML; draw world via components or `onRender`.

**Related files**

- `src/engine/java/edu/nust/engine/core/GameScene.java`

#### Slide 19 - Input routing

**Slide intent**
Show how keyboard/mouse events reach scenes and objects.

**Working**
`GameScene` wires JavaFX events to `InputHandler` methods.

**Technical breakdown**

- Key/mouse handlers are set on the raw JavaFX scene
- DevConsole intercepts when open

**Usage**
Override `onKeyPressed`, `onMouseMoved`, etc. in a scene.

**Related files**

- `src/engine/java/edu/nust/engine/core/interfaces/InputHandler.java`
- `src/engine/java/edu/nust/engine/core/GameScene.java`

#### Slide 20 - DevConsole (overview)

**Slide intent**
Show in-game console for dev commands and stats.

**Working**
A JavaFX overlay with input, output log, and FPS stats.

**Technical breakdown**

- Toggles with Shift + `
- Supports suggestions and history
- Displays FPS and object counts

**Usage**
The console is added automatically by `GameScene`.

**Related files**

- `src/engine/java/edu/nust/engine/core/DevConsole.java`

#### Slide 21 - DevConsole: Command system

**Slide intent**
Explain how commands are registered and executed.

**Working**
Scenes register commands with name, usage, and executor to return output text.

**Technical breakdown**

- Commands stored in a map with normalized names
- Executors return strings shown in console output

**Usage**
Call `registerDevCommand(...)` in `GameScene.registerDevCommands()`.

**Related files**

- `src/engine/java/edu/nust/engine/core/GameScene.java`
- `src/engine/java/edu/nust/engine/core/DevConsole.java`

#### Slide 22 - Resources loader (Resources + GameURLs + URLUtils)

**Slide intent**
Explain asset lookup and path conventions.

**Working**
Resources are resolved relative to `/edu/nust/game/` and loaded by URL.

**Technical breakdown**

- `Resources.tryGetResource(...)` and `getResourceOrThrow(...)`
- `GameURLs` defines scene/layout filenames
- `URLUtils` extracts file names

**Usage**
`Resources.loadImageOrThrow("assets", "player", "weapon", "bullet.png")`

**Related files**

- `src/engine/java/edu/nust/engine/resources/Resources.java`
- `src/engine/java/edu/nust/engine/core/GameURLs.java`
- `src/engine/java/edu/nust/engine/core/files/URLUtils.java`

#### Slide 23 - Math library (overview)

**Slide intent**
Show the core math utilities used across gameplay.

**Working**
Vector, angle, rectangle, and time utilities provide reusable math operations.

**Technical breakdown**

- Shared math primitives reduce duplication
- Used in transforms, collision checks, and animation timing

**Usage**
Use the math classes in gameplay and engine code.

**Related files**

- `src/engine/java/edu/nust/engine/math/*`

#### Slide 24 - Math: Vector2D

**Slide intent**
Show the engine vector utility used across gameplay.

**Working**
2D vector class for positions, movement, and geometry math.

**Technical breakdown**

- Vector ops (add, subtract, normalize, rotate)
- Convenience factories (zero, one, fromAngle)

**Usage**
Use for transform positions, directions, and distances.

**Related files**

- `src/engine/java/edu/nust/engine/math/Vector2D.java`

#### Slide 25 - Math: Angle

**Slide intent**
Explain the reusable angle wrapper.

**Working**
Stores angles in degrees and converts to/from radians.

**Technical breakdown**

- Wraps to (-180, 180)
- Supports interpolation and clamp

**Usage**
`Transform.setRotation(Angle.fromDegrees(90))`

**Related files**

- `src/engine/java/edu/nust/engine/math/Angle.java`

#### Slide 26 - Math: Rectangle

**Slide intent**
Show axis-aligned rectangle utilities.

**Working**
Rectangles support geometry queries and transformations.

**Technical breakdown**

- `fromCorners` and `fromCenter` factories
- Contains/intersects helpers used in collision and culling

**Usage**
Collision masks and camera bounds use `Rectangle`.

**Related files**

- `src/engine/java/edu/nust/engine/math/Rectangle.java`

#### Slide 27 - Math: TimeSpan

**Slide intent**
Explain the time utility used in updates and animations.

**Working**
Encapsulates time in nanoseconds with conversion helpers.

**Technical breakdown**

- Factory methods for ms/sec/min
- Used for animation timing and delta-time logic

**Usage**
`TimeSpan.fromSeconds(5)` for debug lifetimes, etc.

**Related files**

- `src/engine/java/edu/nust/engine/math/TimeSpan.java`

#### Slide 28 - GameLogger (overview)

**Slide intent**
Show engine-level logging with formatting.

**Working**
`GameLogger` wraps SLF4J with custom color prefixes.

**Technical breakdown**

- Supports TRACE/DEBUG/INFO/SUCCESS/WARN/ERROR
- Filters by global log level

**Usage**
`GameLogger.getLogger(MyClass.class).info(...)`

**Related files**

- `src/engine/java/edu/nust/engine/logger/GameLogger.java`

#### Slide 29 - GameLogger: Colored formatted logging

**Slide intent**
Explain colorized log prefixes and formatting.

**Working**
Log messages are wrapped with ANSI color prefixes for readability.

**Technical breakdown**

- Color per level (trace/debug/info/etc.)
- Uses formatted prefixes for consistent output

**Usage**
Automatic when using `GameLogger` APIs.

**Related files**

- `src/engine/java/edu/nust/engine/logger/GameLogger.java`

#### Slide 30 - GameLogger: Levels

**Slide intent**
Show how log levels control output.

**Working**
Global log level filters which messages are emitted.

**Technical breakdown**

- Levels map to trace/debug/info/success/warn/error
- Global level set via `setGlobalLevel(...)`

**Usage**
`GameLogger.setGlobalLevel(LogLevel.INFO)`

**Related files**

- `src/engine/java/edu/nust/engine/logger/GameLogger.java`

#### Slide 31 - GameLogger: Logging to console

**Slide intent**
Show how console logging is configured.

**Working**
Logback routes console output through a custom colored appender.

**Technical breakdown**

- `ColoredConsoleAppender` for ANSI output
- Console appender registered in `logback.xml`

**Usage**
Runs automatically with default app config.

**Related files**

- `src/engine/resources/edu/nust/engine/logger/logback.xml`

#### Slide 32 - GameLogger: Logging to file

**Slide intent**
Show how logs are stored per session.

**Working**
Logback writes rolling files to `logs/latest.log` and archives.

**Technical breakdown**

- RollingFileAppender + TimeBasedRollingPolicy
- Archived logs stored in `logs/archived/`

**Usage**
Runs automatically with default app config.

**Related files**

- `src/engine/resources/edu/nust/engine/logger/logback.xml`

#### Slide 33 - Audio management (overview)

**Slide intent**
Explain the abstraction and caching for audio assets.

**Working**
Audio assets are wrapped in references and cached by a manager.

**Technical breakdown**

- Sealed audio reference hierarchy
- Manager controls caching and volume

**Usage**
Load and play audio through the manager or registry helpers.

**Related files**

- `src/engine/java/edu/nust/engine/core/audio/*`

#### Slide 34 - Audio reference system

**Slide intent**
Introduce the base class for audio references.

**Working**
`AudioReference` stores the audio URL and common metadata.

**Technical breakdown**

- Sealed base class to limit subclasses
- Provides file name and path helpers

**Usage**
Created internally by `GameAudioManager`.

**Related files**

- `src/engine/java/edu/nust/engine/core/audio/AudioReference.java`

#### Slide 35 - Audio reference system: SoundEffectReference

**Slide intent**
Explain short, one-shot audio playback.

**Working**
Loads PCM data into clips and allows overlapping playback.

**Technical breakdown**

- Converts to PCM if needed
- Manages active clip list

**Usage**
`Audios.randomPlayerGunShotRef().ifPresent(SoundEffectReference::play)`

**Related files**

- `src/engine/java/edu/nust/engine/core/audio/SoundEffectReference.java`

#### Slide 36 - Audio reference system: MusicTrackReference

**Slide intent**
Explain long-running music playback.

**Working**
Uses JavaFX `MediaPlayer` with looping and fade helpers.

**Technical breakdown**

- Supports fade-in/out and loop points
- Tracks base volume separate from global volume

**Usage**
`musicRef.play()` or `musicRef.fadeIn(Duration.seconds(1))`

**Related files**

- `src/engine/java/edu/nust/engine/core/audio/MusicTrackReference.java`

#### Slide 37 - Audio management: GameAudioManager

**Slide intent**
Show audio caching and global control.

**Working**
Manages loaded audio, global volume, mute, and fade helpers.

**Technical breakdown**

- Caches by relative path
- Applies global volume to all references
- Unload rules when switching scenes

**Usage**
`world.getAudioManager().loadSoundEffect("ui", "click.wav")`

**Related files**

- `src/engine/java/edu/nust/engine/core/audio/GameAudioManager.java`

### Systems & Deployment

#### Slide 38 - AssetManager cache

**Slide intent**
Explain centralized image caching.

**Working**
Singleton that loads assets once and reuses them.

**Technical breakdown**

- Cache key based on asset enum ID
- Generic loader for different asset types

**Usage**
`AssetManager.getInstance().loadEnemy(EnemyAsset.BASIC)`

**Related files**

- `src/game/java/edu/nust/game/systems/assets/AssetManager.java`

#### Slide 39 - CollisionManager

**Slide intent**
Explain damage and collision resolution.

**Working**
Collects `Damageable`, `Damaging`, and `Concrete` objects and resolves interactions.

**Technical breakdown**

- Awards points on enemy death
- Triggers camera shake and destroy queues

**Usage**
`collisionManager.manageCollisions()` in `LevelScene`.

**Related files**

- `src/game/java/edu/nust/game/systems/collision/CollisionManager.java`

#### Slide 40 - HitBox component

**Slide intent**
Show axis-aligned hitbox logic.

**Working**
`HitBox` calculates overlap and supports debug visualization.

**Technical breakdown**

- Maintains half-width/half-height
- `asRect()` exposes bounds for debug and collision

**Usage**
Attach to `GameObject` that participates in collision.

**Related files**

- `src/game/java/edu/nust/game/systems/collision/HitBox.java`

#### Slide 41 - PathFinder (A* grid)

**Slide intent**
Explain enemy pathfinding system.

**Working**
A* search on a node grid to reach the player.

**Technical breakdown**

- Open/closed sets via priority queue and hash set
- Manhattan heuristic for grid movement

**Usage**
`new PathFinder(levelScene).getPath(enemy)`

**Related files**

- `src/game/java/edu/nust/game/systems/pathfinder/PathFinder.java`

#### Slide 42 - MapNodeSetter (solid grid build)

**Slide intent**
Show how the pathfinding grid is generated.

**Working**
Converts collision rectangles into a grid of solid nodes.

**Technical breakdown**

- Node size is fixed to 2 units
- Maps world coordinates into grid indices

**Usage**
Constructed in `LevelScene` and shared with pathfinder.

**Related files**

- `src/game/java/edu/nust/game/systems/pathfinder/MapNodeSetter.java`

#### Slide 43 - Weapon system (overview)

**Slide intent**
Explain the combat firing loop at a high level.

**Working**
Weapon tracks aim, fires bullets with cooldown, and manages ammo/reload.

**Technical breakdown**

- Muzzle flash animation driven by sprite sheets
- Bullet spawn rate controlled by `fireRate`

**Usage**
Weapon owned by `LevelScene` and updated each frame.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Weapon.java`

#### Slide 44 - Weapon system: Ammo

**Slide intent**
Explain the reload state machine.

**Working**
`Ammo` tracks current ammo, reload delay, and refill timing.

**Technical breakdown**

- Reload delay then reload duration
- Refills after timer expires

**Usage**
`weapon.reload()` or `ammo.update(deltaTime)`.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Ammo.java`

#### Slide 45 - Weapon system: Bullet

**Slide intent**
Explain projectile behavior and lifetime.

**Working**
Bullets move forward, apply damage, and self-destruct on range.

**Technical breakdown**

- Direction set from aim vector
- Destroyed on range or collision

**Usage**
Spawned by `Weapon.fireWeapon(...)`.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Bullet.java`

#### Slide 46 - HUD bars (overview)

**Slide intent**
Show HUD indicators for gameplay state.

**Working**
UI bars update based on health and ammo state.

**Technical breakdown**

- Base `Bar` uses a fixed number of cells
- Specialized bars map state to fill percent

**Usage**
Created in `LevelScene` and updated each frame.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/hud/Bar.java`

#### Slide 47 - HUD bars: HealthBar

**Slide intent**
Show how HP is visualized.

**Working**
Health bar updates from `Health` and clips sprite fill.

**Technical breakdown**

- Uses sprite assets when available
- Falls back to cell-based bar

**Usage**
`healthBar.updateUI(player.getHealthSystem(), label)`

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/hud/HealthBar.java`

#### Slide 48 - HUD bars: AmmoBar

**Slide intent**
Show how ammo is visualized.

**Working**
Ammo bar reflects current magazine state and reload state.

**Technical breakdown**

- Updates from `Ammo` model
- Uses UI container from LevelScene

**Usage**
`ammoBar.updateUI(weapon.getAmmo(), ammoLabel)`

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/AmmoBar.java`

#### Slide 49 - ScoreDisplayController (sprite digits)

**Slide intent**
Explain game-over score UI rendering.

**Working**
Displays a 3-digit score using a sprite sheet and a panel background.

**Technical breakdown**

- Spritesheet slicing via viewports
- Digit container aligned on top of panel image

**Usage**
Created in `LevelScene` game-over state.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/hud/ScoreDisplayController.java`

#### Slide 50 - Audio management (game layer overview)

**Slide intent**
Show how audio is organized and controlled at game level.

**Working**
`Audios` lists paths and exposes helpers; `MusicManager` swaps menu/level tracks.

**Technical breakdown**

- Sound effect registry lists all audio paths
- Music transitions use fade helpers

**Usage**
Call `MusicManager.playMenuMusic()` or `playLevelMusic()`.

**Related files**

- `src/game/java/edu/nust/game/systems/audio/Audios.java`
- `src/game/java/edu/nust/game/systems/audio/MusicManager.java`

#### Slide 51 - Audio management: MusicManager

**Slide intent**
Show menu vs level music control.

**Working**
`MusicManager` handles switching and looping of main tracks.

**Technical breakdown**

- Fades out current track on transition
- Keeps menu music running across scenes

**Usage**
`MusicManager.playMenuMusic()` and `MusicManager.playLevelMusic()`.

**Related files**

- `src/game/java/edu/nust/game/systems/audio/MusicManager.java`

#### Slide 52 - Audio management: Audios registry

**Slide intent**
Show how audio references are centralized.

**Working**
`Audios` exposes helper methods for sound effects and music references.

**Technical breakdown**

- Randomized sound effect selection
- One-line helpers per audio category

**Usage**
`Audios.randomPlayerFootstepRef().ifPresent(SoundEffectReference::play)`

**Related files**

- `src/game/java/edu/nust/game/systems/audio/Audios.java`

#### Slide 53 - Gradle packaging (overview)

**Slide intent**
Show distribution pipeline for desktop installers.

**Working**
Build scripts create fat JARs and OS-specific installers.

**Technical breakdown**

- Uses `jpackage` for native installers
- Builds on target OS only

**Usage**
Run the packaging task for your OS.

**Related files**

- `build.gradle`

#### Slide 54 - Gradle packaging: packageWindows

**Slide intent**
Explain the Windows installer task.

**Working**
Builds a Windows `.exe` installer using `jpackage`.

**Technical breakdown**

- Generates Windows icon
- Enables shortcut/menu options

**Usage**
`./gradlew packageWindows` (Windows only)

**Related files**

- `build.gradle`

#### Slide 55 - Gradle packaging: packageLinux

**Slide intent**
Explain the Linux installer task.

**Working**
Builds a Linux `.deb` installer using `jpackage`.

**Technical breakdown**

- Generates Linux icon sizes
- Packages from `installDist`

**Usage**
`./gradlew packageLinux` (Linux only)

**Related files**

- `build.gradle`

#### Slide 56 - Gradle packaging: packageMac

**Slide intent**
Explain the macOS installer task.

**Working**
Builds a macOS `.dmg` installer using `jpackage`.

**Technical breakdown**

- Generates `.icns` icon
- Packages from `installDist`

**Usage**
`./gradlew packageMac` (macOS only)

**Related files**

- `build.gradle`

#### Slide 57 - Gradle packaging: buildExecutableJar

**Slide intent**
Explain the fat JAR task.

**Working**
Builds an executable fat JAR for easy distribution.

**Technical breakdown**

- Bundles runtime dependencies
- Adds Main-Class manifest

**Usage**
`./gradlew buildExecutableJar`

**Related files**

- `build.gradle`

#### Slide 58 - Build/run tasks (overview)

**Slide intent**
Explain dev run options.

**Working**
Custom Gradle tasks run with or without logging and test the logger.

**Technical breakdown**

- Standard run with default logging
- No-logs run for quiet demos
- Logger test harness task

**Usage**
Use the tasks below as needed.

**Related files**

- `build.gradle`

#### Slide 59 - Build/run tasks: run

**Slide intent**
Show the default run task.

**Working**
Starts the game with default logging and JavaFX settings.

**Technical breakdown**

- Uses `application` plugin main class
- Uses default Logback config

**Usage**
`./gradlew run`

**Related files**

- `build.gradle`

#### Slide 60 - Build/run tasks: runNoLogs

**Slide intent**
Show the no-logs run task.

**Working**
Runs the game with logging disabled via Logback config override.

**Technical breakdown**

- Sets JVM arg to `logback-off.xml`
- Keeps JavaFX modules enabled

**Usage**
`./gradlew runNoLogs`

**Related files**

- `build.gradle`
- `src/engine/resources/edu/nust/engine/logger/logback-off.xml`

#### Slide 61 - Build/run tasks: runTestLogger

**Slide intent**
Show the logger test harness task.

**Working**
Runs the logger test entry point for verifying log styles and output.

**Technical breakdown**

- Uses test source set runtime classpath
- Applies standard Logback config

**Usage**
`./gradlew runTestLogger`

**Related files**

- `build.gradle`

#### Slide 62 - UML generation pipeline (overview)

**Slide intent**
Explain how UML assets are generated for documentation.

**Working**
A Python script scans Java sources and outputs Mermaid diagrams.

**Technical breakdown**

- Parses Java using `javalang`
- Generates multiple Mermaid outputs

**Usage**
Run the generator script as needed.

**Related files**

- `uml/generate_src_uml_assets.py`

#### Slide 63 - UML generation pipeline: Generate UML using Python

**Slide intent**
Show how to generate `.mmd` files.

**Working**
Runs the UML script to parse Java and emit Mermaid files.

**Technical breakdown**

- Scans `src/` for Java files
- Writes `.mmd` files to `uml/generated/mmd`

**Usage**
`python uml/generate_src_uml_assets.py --skip-render`

**Related files**

- `uml/generate_src_uml_assets.py`

#### Slide 64 - UML generation pipeline: Separate Mermaid files per package

**Slide intent**
Explain the per-package output layout.

**Working**
The generator writes one Mermaid file per package for readability.

**Technical breakdown**

- Package-aware naming `package_*.mmd`
- Optional PDF/PNG rendering

**Usage**
Run the generator with or without rendering.

**Related files**

- `uml/generated/mmd/*`

### Scenes

#### Slide 65 - StartScene

**Slide intent**
Show the main menu scene.

**Working**
Start scene loads menu UI, starts menu music, and routes to gameplay or highscores.

**Technical breakdown**

- FXML methods switch scenes via `GameWorld.setScene`
- Plays button click SFX

**Usage**
Entry scene set in `MainWorld` startup flow.

**Related files**

- `src/game/java/edu/nust/game/scenes/start/StartScene.java`

#### Slide 66 - LevelSelectScene

**Slide intent**
Explain player name capture and level entry.

**Working**
Validates player name, stores it in session, then loads Level 1.

**Technical breakdown**

- Prevents empty names and commas
- Uses `PlayerSession` for persistence between scenes

**Usage**
Triggered from StartScene to begin gameplay.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelselect/LevelSelectScene.java`

#### Slide 67 - LevelScene (core gameplay)

**Slide intent**
Describe the main gameplay loop.

**Working**
Spawns player, weapon, enemies, UI, and processes gameplay updates.

**Technical breakdown**

- Initializes collision manager and score
- Updates camera and HUD each frame
- Handles pause and game-over state

**Usage**
Created when starting a new level.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/LevelScene.java`

#### Slide 68 - LevelScene: CollisionMask

**Slide intent**
Show how level boundaries and blocked areas are defined.

**Working**
Defines rectangle lists for boundaries and obstacles.

**Technical breakdown**

- `isWalkable` checks intersections with mask rects
- Exposes `getMapBounds()` and iterators

**Usage**
Used by player movement and spawn validation.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/level_1/Level1CollisionMask.java`

#### Slide 69 - LevelScene: SpawnPoints generation

**Slide intent**
Explain enemy spawn placement.

**Working**
Generates spawn points across a grid with random offsets and validation.

**Technical breakdown**

- Avoids non-spawnable area
- Validates with collision mask and movement checks

**Usage**
`Level1SpawnPoints.forEachEnemySpawnPoint(...)`

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/level_1/Level1SpawnPoints.java`

#### Slide 70 - LevelScene: Background objects + placements file

**Slide intent**
Show data-driven level decoration.

**Working**
Static objects are loaded from `objects_placements.txt` and added to the scene.

**Technical breakdown**

- Parses placement file lines into `StaticObject` instances
- Can regenerate placements via builder helpers

**Usage**
Called in `LevelScene` initialization.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/level_1/Level1Background.java`
- `src/game/resources/edu/nust/game/scenes/LevelScene/objects_placements.txt`

#### Slide 71 - LevelScene: StaticObjectFactory + SerializablePlacement

**Slide intent**
Explain how static objects are instantiated by type/variant.

**Working**
Factory builds static objects and tags them as static; record stores serialized placements.

**Technical breakdown**

- Applies position and tag in one helper
- `SerializablePlacement` records rectangle, position, type, variant

**Usage**
Used by level placement loader/generator.

**Related files**

- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/statics/meta/StaticObjectFactory.java`
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/statics/meta/SerializablePlacement.java`

#### Slide 72 - HighScoresScene

**Slide intent**
Explain the high-score display UI.

**Working**
Loads and renders top scores with a styled list.

**Technical breakdown**

- Uses `HighScoreStorage` to load entries
- Builds HBox rows dynamically

**Usage**
Accessible from Start or Game Over UI.

**Related files**

- `src/game/java/edu/nust/game/scenes/highscores/HighScoresScene.java`

#### Slide 73 - HighScoresScene: HighScoreStorage

**Slide intent**
Show file-based score persistence.

**Working**
Scores are stored in `highscores.txt` and sorted on load.

**Technical breakdown**

- CSV format: name,score,timestamp
- Sorts by score (desc), then timestamp

**Usage**
`HighScoreStorage.append(name, score, timestamp)`

**Related files**

- `src/game/java/edu/nust/game/scenes/highscores/highscores/HighScoreStorage.java`
- `highscores.txt`

#### Slide 74 - HighScoresScene: PlayerSession

**Slide intent**
Show lightweight session state.

**Working**
Stores player name between scenes.

**Technical breakdown**

- Static getter/setter with trimming

**Usage**
`PlayerSession.setPlayerName(...)`

**Related files**

- `src/game/java/edu/nust/game/systems/PlayerSession.java`

#### Slide 75 - HighScoresScene: Score system

**Slide intent**
Explain time-based and kill-based scoring.

**Working**
Score increases by survival time and enemy kills.

**Technical breakdown**

- 1 point every 5 seconds
- Extra points for kills

**Usage**
`score.update(deltaTime)` each frame; `addPoints(...)` on events.

**Related files**

- `src/game/java/edu/nust/game/systems/Score.java`

## OOP Concepts and Examples

### Slide 76 - OOP concepts overview

**Slide intent**
Introduce the design principles used in the project.

**Key points**

- SOLID principles
- Composition and aggregation
- Common patterns and structures

### Slide 77 - SOLID: Single Responsibility (SRP)

**Examples**

- `Resources` only resolves and loads assets.
- `Score` only tracks score state and time-based increments.

### Slide 78 - SOLID: Open/Closed (OCP)

**Examples**

- New behaviors can be added by creating `Component` subclasses without changing `GameObject`.
- New scenes extend `GameScene` without altering engine code.

### Slide 79 - SOLID: Liskov Substitution (LSP)

**Examples**

- `GameScene.addGameObject(GameObject)` accepts any subclass (`Player`, `Weapon`, `Bullet`).

### Slide 80 - SOLID: Interface Segregation (ISP)

**Examples**

- Separate interfaces: `Updatable`, `Renderable`, `InputHandler` allow small, focused contracts.

### Slide 81 - SOLID: Dependency Inversion (DIP)

**Examples**

- `GameScene` culling uses `WorldBoundsProvider` interface, decoupling from concrete renderers.

### Slide 82 - Composition

**Examples**

- `GameObject` composes behavior using `Component` (e.g., `Transform`, `SpriteRenderer`, `HitBox`).

### Slide 83 - Aggregation

**Examples**

- `GameScene` aggregates `GameObject` instances; objects can be added/removed without destroying the scene itself.

### Slide 84 - Other technical terms and structures used

**Examples**

- Factory method: `GameObject.create(...)` for fluent object creation.
- Singleton: `AssetManager.getInstance()` for cached assets.
- Command pattern: dev console commands map to executable functions.
- Sealed classes: `AudioReference` and `DebugShape` restrict subclass sets.
- Records: `SerializablePlacement`, `HighScoreEntry` for immutable data.
- Data-driven content: `objects_placements.txt` drives level decoration.
- A* pathfinding: `PathFinder` grid search with heuristic.
- Render culling: `WorldBoundsProvider` + camera bounds in `GameScene`.
- Sprite sheet animation: `SpriteRenderer` frame slicing and timers.
- Caching: audio and image caches reduce reload cost.
