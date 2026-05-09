# Zombie Zero - Feature Slides and OOP Examples

## Intro Slides (high level)

### Slide 0 - Title and goal
**Slide intent**
Present the project title, genre, and objective at a glance.

**Key points**
- Title: Zombie Zero (top-down 2D shooter)
- Objective: clear all zombies in the city
- Built on custom Java engine using JavaFX

### Slide 1 - Architecture overview
**Slide intent**
Explain the two-layer structure so later slides make sense.

**Key points**
- Engine layer: reusable runtime (core, math, audio, logging, resources)
- Game layer: scenes, systems, and content built on the engine

### Slide 2 - Runtime flow
**Slide intent**
Show how the app starts and how scenes switch.

**Key points**
- Entry flow: `Launcher` -> `Main` -> `MainWorld`
- Game loop ticks `GameScene` and its `GameObject`s
- Scene switching happens in `GameWorld.setScene(...)`

### Slide 3 - Feature selection slide
**Slide intent**
Give the audience a menu of features to pick from.

**Key points**
- 50 feature slides listed below
- Each slide includes: working, technical breakdown, usage

## Feature Index (50 items)
1. GameWorld runtime host
2. GameScene lifecycle and layering
3. GameObject base entity
4. Component system
5. Default Transform component
6. Tag system
7. Update/LateUpdate lifecycle
8. Rendering pipeline (world + UI layers)
9. Render order and culling
10. GameCamera (position, zoom, shake)
11. Input routing
12. DevConsole UI
13. Dev command system
14. Debug rendering in GameScene
15. Resources loader (Resources + GameURLs + URLUtils)
16. SpriteRenderer (sprites + animation)
17. Shape renderers (BoxRenderer + CircleRenderer)
18. WorldBoundsProvider
19. Math: Vector2D
20. Math: Angle
21. Math: Rectangle
22. Math: TimeSpan
23. GameLogger (levels + formatting)
24. LogProgress (progress channels)
25. Logback config and runNoLogs
26. Audio reference system (AudioReference + Sound/Music)
27. GameAudioManager (cache + volume)
28. MusicManager + Audios registry
29. AssetManager cache
30. StartScene
31. LevelSelectScene
32. LevelScene (core gameplay)
33. HighScoresScene
34. HighScoreStorage
35. PlayerSession
36. Score system
37. CollisionManager
38. HitBox component
39. Level1CollisionMask
40. Level1SpawnPoints generation
41. Level1Background + placements file
42. StaticObjectFactory + SerializablePlacement
43. PathFinder (A* grid)
44. MapNodeSetter (solid grid build)
45. Weapon system (Weapon + Ammo + Bullet)
46. HUD bars (Bar + HealthBar + AmmoBar)
47. ScoreDisplayController (sprite digits)
48. UML generation pipeline
49. Gradle packaging (fatJar + jpackage)
50. Build/run tasks (runNoLogs + runTestLogger)

## Feature Slides (working of)

### 1) GameWorld runtime host
**Slide intent**
Explain how the window, scene switching, and game loop are centralized.

**Working**
`GameWorld` owns the JavaFX `Stage` and `Scene`, drives the `AnimationTimer` loop, and delegates each frame to the active `GameScene`.

**Technical breakdown**
- Creates the root `StackPane` and binds it to the stage size
- Runs a smoothing FPS calculation
- Switches scenes by swapping root children (world + UI + console)

**Usage**
Subclass `GameWorld`, override `initStage()` and `loadAudios()`, then call `start()`.

**Related files**
- `src/engine/java/edu/nust/engine/core/GameWorld.java`
- `src/game/java/edu/nust/game/MainWorld.java`

### 2) GameScene lifecycle and layering
**Slide intent**
Show how a scene manages world objects, UI, and debug/console layers.

**Working**
`GameScene` owns the game object list, the world canvas, and the FXML UI layer, and manages input hooks.

**Technical breakdown**
- Loads `layout.fxml` and optional `style.css` per scene
- Maintains add/remove queues to avoid concurrent modification
- Updates and renders objects each frame

**Usage**
Subclass `GameScene`, implement `onInit()` and input handlers as needed.

**Related files**
- `src/engine/java/edu/nust/engine/core/GameScene.java`

### 3) GameObject base entity
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

### 4) Component system
**Slide intent**
Explain composition-based behavior on top of `GameObject`.

**Working**
Components add rendering/logic without subclass explosion.

**Technical breakdown**
- `Component` is `Initiable`, `Updatable`, `Renderable`
- Attached to exactly one `GameObject`
- Controlled by active/visible flags

**Usage**
Create new components (e.g., renderers, hitboxes) and add to objects.

**Related files**
- `src/engine/java/edu/nust/engine/core/Component.java`

### 5) Default Transform component
**Slide intent**
Show the built-in spatial data on every object.

**Working**
Each `GameObject` auto-adds `Transform` (position, rotation, anchor).

**Technical breakdown**
- Default component added in `GameObject` constructor
- Provides direction helpers and look-at utilities

**Usage**
Use `gameObject.getTransform()` for movement and rotation.

**Related files**
- `src/engine/java/edu/nust/engine/core/components/Transform.java`

### 6) Tag system
**Slide intent**
Explain how tags are used for grouping and queries.

**Working**
Tags are class-based identifiers attached to `GameObject` without instances.

**Technical breakdown**
- Stores tag class types in a `Set`
- `hasTag` supports subclass checks

**Usage**
`gameObject.addTag(PlayerTag.class)` then query via scene.

**Related files**
- `src/engine/java/edu/nust/engine/core/gameobjects/Tag.java`
- `src/engine/java/edu/nust/engine/core/GameObject.java`

### 7) Update/LateUpdate lifecycle
**Slide intent**
Explain deterministic frame order for updates.

**Working**
`Updatable` defines update order across scene, objects, and components.

**Technical breakdown**
- Ordered sequence: scene -> objects -> components -> late updates
- Active flag gates updates

**Usage**
Override `onUpdate` and `lateUpdate` in scene/object/component.

**Related files**
- `src/engine/java/edu/nust/engine/core/interfaces/Updatable.java`
- `src/engine/java/edu/nust/engine/core/GameScene.java`

### 8) Rendering pipeline (world + UI layers)
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

### 9) Render order and culling
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

### 10) GameCamera (position, zoom, shake)
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

### 11) Input routing
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

### 12) DevConsole UI
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

### 13) Dev command system
**Slide intent**
Explain how commands are registered and executed.

**Working**
Scenes register commands with `commandName`, usage, description, and executor.

**Technical breakdown**
- Commands stored in a map with normalized names
- Executor returns output string shown in console

**Usage**
Call `registerDevCommand(...)` in `GameScene.registerDevCommands()`.

**Related files**
- `src/engine/java/edu/nust/engine/core/GameScene.java`
- `src/engine/java/edu/nust/engine/core/DevConsole.java`

### 14) Debug rendering in GameScene
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

### 15) Resources loader (Resources + GameURLs + URLUtils)
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

### 16) SpriteRenderer (sprites + animation)
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

### 17) Shape renderers (BoxRenderer + CircleRenderer)
**Slide intent**
Show vector-based debugging or UI primitives.

**Working**
Shape renderers draw rectangles or circles with optional stroke/fill.

**Technical breakdown**
- Shared base: `ShapeRenderer` with fill/stroke controls
- Implements `WorldBoundsProvider` for culling

**Usage**
Add `BoxRenderer` or `CircleRenderer` to a `GameObject`.

**Related files**
- `src/engine/java/edu/nust/engine/core/components/renderers/ShapeRenderer.java`
- `src/engine/java/edu/nust/engine/core/components/renderers/BoxRenderer.java`
- `src/engine/java/edu/nust/engine/core/components/renderers/CircleRenderer.java`

### 18) WorldBoundsProvider
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

### 19) Math: Vector2D
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

### 20) Math: Angle
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

### 21) Math: Rectangle
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

### 22) Math: TimeSpan
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

### 23) GameLogger (levels + formatting)
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

### 24) LogProgress (progress channels)
**Slide intent**
Show structured progress logging for long tasks.

**Working**
`LogProgress` adds begin/log/end messages with consistent styling.

**Technical breakdown**
- Random ANSI background per progress instance
- Sanitizes progress names

**Usage**
Used when loading scenes or level placements.

**Related files**
- `src/engine/java/edu/nust/engine/logger/LogProgress.java`

### 25) Logback config and runNoLogs
**Slide intent**
Explain how logging can be enabled/disabled at runtime.

**Working**
Gradle task `runNoLogs` swaps Logback config to disable logs.

**Technical breakdown**
- JVM arg switches `logback.xml` vs `logback-off.xml`
- Adds JavaFX module path automatically

**Usage**
Run with `runNoLogs` task for silent gameplay.

**Related files**
- `build.gradle`
- `src/engine/resources/edu/nust/engine/logger/logback.xml`
- `src/engine/resources/edu/nust/engine/logger/logback-off.xml`

### 26) Audio reference system (AudioReference + Sound/Music)
**Slide intent**
Explain the abstraction for audio assets.

**Working**
Audio assets are wrapped in references, separating metadata from playback.

**Technical breakdown**
- `AudioReference` is sealed base class
- `SoundEffectReference` for short clips
- `MusicTrackReference` for longer tracks

**Usage**
Use `GameAudioManager.loadSoundEffect(...)` or `loadMusicTrack(...)`.

**Related files**
- `src/engine/java/edu/nust/engine/core/audio/AudioReference.java`
- `src/engine/java/edu/nust/engine/core/audio/SoundEffectReference.java`
- `src/engine/java/edu/nust/engine/core/audio/MusicTrackReference.java`

### 27) GameAudioManager (cache + volume)
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

### 28) MusicManager + Audios registry
**Slide intent**
Show how audio is organized and controlled at game level.

**Working**
`Audios` lists paths and exposes helpers; `MusicManager` swaps menu/level tracks.

**Technical breakdown**
- Randomized sound effect selection
- Fade-in/out transitions between tracks

**Usage**
Call `MusicManager.playMenuMusic()` or `playLevelMusic()`.

**Related files**
- `src/game/java/edu/nust/game/systems/audio/Audios.java`
- `src/game/java/edu/nust/game/systems/audio/MusicManager.java`

### 29) AssetManager cache
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

### 30) StartScene
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

### 31) LevelSelectScene
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

### 32) LevelScene (core gameplay)
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

### 33) HighScoresScene
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

### 34) HighScoreStorage
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

### 35) PlayerSession
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

### 36) Score system
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

### 37) CollisionManager
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

### 38) HitBox component
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

### 39) Level1CollisionMask
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

### 40) Level1SpawnPoints generation
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

### 41) Level1Background + placements file
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

### 42) StaticObjectFactory + SerializablePlacement
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

### 43) PathFinder (A* grid)
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

### 44) MapNodeSetter (solid grid build)
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

### 45) Weapon system (Weapon + Ammo + Bullet)
**Slide intent**
Explain the combat firing loop.

**Working**
Weapon tracks aim, fires bullets with cooldown, and manages ammo/reload.

**Technical breakdown**
- Muzzle flash animation driven by sprite sheets
- Bullets move forward and self-destroy on range
- Ammo handles reload delays and timing

**Usage**
Weapon owned by `LevelScene` and updated each frame.

**Related files**
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Weapon.java`
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Ammo.java`
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Bullet.java`

### 46) HUD bars (Bar + HealthBar + AmmoBar)
**Slide intent**
Show HUD indicators for gameplay state.

**Working**
UI bars update based on health and ammo state.

**Technical breakdown**
- Base `Bar` uses a fixed number of cells
- `HealthBar` uses sprite-based fill when available

**Usage**
Created in `LevelScene` and updated each frame.

**Related files**
- `src/game/java/edu/nust/game/scenes/levelscene/hud/Bar.java`
- `src/game/java/edu/nust/game/scenes/levelscene/hud/HealthBar.java`
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/AmmoBar.java`

### 47) ScoreDisplayController (sprite digits)
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

### 48) UML generation pipeline
**Slide intent**
Explain how UML assets are generated for documentation.

**Working**
A Python script scans Java sources and outputs Mermaid diagrams.

**Technical breakdown**
- Parses Java using `javalang`
- Generates package-aware `.mmd` files
- Optional PDF/PNG rendering via Mermaid CLI

**Usage**
`python uml/generate_src_uml_assets.py --skip-render`

**Related files**
- `uml/generate_src_uml_assets.py`
- `uml/generated/mmd/*`

### 49) Gradle packaging (fatJar + jpackage)
**Slide intent**
Show distribution pipeline for desktop installers.

**Working**
Build scripts create fat JARs and OS-specific installers.

**Technical breakdown**
- `fatJar` task bundles all runtime dependencies
- `packageMac/Windows/Linux` use `jpackage`
- Icon generation per OS

**Usage**
Run `./gradlew packageMac` (macOS) or equivalent on target OS.

**Related files**
- `build.gradle`

### 50) Build/run tasks (runNoLogs + runTestLogger)
**Slide intent**
Explain dev run options.

**Working**
Custom Gradle tasks run with or without logging and test the logger.

**Technical breakdown**
- `runNoLogs` swaps Logback config
- `runTestLogger` targets a test harness class

**Usage**
`./gradlew runNoLogs` or `./gradlew runTestLogger`

**Related files**
- `build.gradle`

## OOP Concepts and Examples

### SOLID principles
**Single Responsibility (SRP)**
- `Resources` only resolves and loads assets.
- `Score` only tracks score state and time-based increments.

**Open/Closed (OCP)**
- New behaviors can be added by creating new `Component` subclasses without changing `GameObject`.
- New scenes extend `GameScene` without altering engine code.

**Liskov Substitution (LSP)**
- `GameScene.addGameObject(GameObject)` accepts any subclass (`Player`, `Weapon`, `Bullet`).

**Interface Segregation (ISP)**
- Separate interfaces: `Updatable`, `Renderable`, `InputHandler` allow small, focused contracts.

**Dependency Inversion (DIP)**
- `GameScene` culling uses `WorldBoundsProvider` interface, decoupling from concrete renderers.

### Composition
- `GameObject` composes behavior using `Component` (e.g., `Transform`, `SpriteRenderer`, `HitBox`).

### Aggregation
- `GameScene` aggregates `GameObject` instances; objects can be added/removed without destroying the scene itself.

### Other technical terms and structures used
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
