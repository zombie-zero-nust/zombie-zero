# Project Class and File Purposes

This single document describes the purpose and intended use of each Java source file in this repository, plus key project-level files.

## Project-Level Files

- `README.md` - Purpose: primary project introduction and usage overview. Use: first-stop guide for setup and execution.
- `PROJECT_INFO.md` - Purpose: architecture and design reference. Use: detailed understanding of engine/game structure.
- `IMPLEMENTATION_GUIDE.md` - Purpose: implementation notes and development guidance. Use: follow established coding/system patterns.
- `BUG_FIX_REPORT.md` - Purpose: record of addressed defects and fixes. Use: track resolved issues and regression context.
- `COLLISION_SYSTEM_DOCUMENTATION.md` - Purpose: collision subsystem behavior documentation. Use: understand collision contracts and flows.
- `build.gradle` - Purpose: Gradle build, dependency, source set, and packaging configuration. Use: builds, runs, tests, and distributions.
- `settings.gradle` - Purpose: Gradle project identity/settings file. Use: root project naming/config wiring.
- `highscores.txt` - Purpose: persisted high-score data file. Use: runtime read/write leaderboard storage.
- `docs/AUDIO_API_ADDITION.md` - Purpose: notes for audio API additions. Use: maintain/extend audio features consistently.
- `docs/UML_DIAGRAM.md` - Purpose: UML-based structural documentation. Use: quick class/package relationship reference.

## Scope

- Included: all `.java` files under `src/engine/java`, `src/game/java`, `src/main/java`, and `src/tests`.
- Excluded: generated/build output (`build/`, `out/`), logs, and binary assets.

## Engine (`src/engine/java`)

- `src/engine/java/edu/nust/engine/core/Component.java` - Purpose: base behavior unit attached to a game object. Use: extend it to add reusable gameplay/render logic.
- `src/engine/java/edu/nust/engine/core/GameCamera.java` - Purpose: camera/viewport controls for world rendering. Use: pan/zoom scene rendering.
- `src/engine/java/edu/nust/engine/core/GameObject.java` - Purpose: root entity type for scene objects. Use: compose gameplay entities with components and tags.
- `src/engine/java/edu/nust/engine/core/GameScene.java` - Purpose: scene lifecycle and object management layer. Use: subclass for menu/gameplay scenes.
- `src/engine/java/edu/nust/engine/core/GameURLs.java` - Purpose: central constants/utilities for URL-like resource paths. Use: consistent resource/path references.
- `src/engine/java/edu/nust/engine/core/GameWorld.java` - Purpose: application/world host and scene switching owner. Use: create the main world, run loop, and swap scenes.

### Engine Audio

- `src/engine/java/edu/nust/engine/core/audio/AudioReference.java` - Purpose: base reference wrapper for audio assets. Use: represent reusable music/sfx handles.
- `src/engine/java/edu/nust/engine/core/audio/GameAudioManager.java` - Purpose: audio playback facade for the game. Use: play/stop/control sounds and tracks.
- `src/engine/java/edu/nust/engine/core/audio/MusicTrackReference.java` - Purpose: typed reference for music assets. Use: register and play background music.
- `src/engine/java/edu/nust/engine/core/audio/SoundEffectReference.java` - Purpose: typed reference for sound effects. Use: trigger short gameplay sfx.

### Engine Components

- `src/engine/java/edu/nust/engine/core/components/Transform.java` - Purpose: position/rotation/scale data component. Use: move and orient game objects.
- `src/engine/java/edu/nust/engine/core/components/renderers/ShapeRenderer.java` - Purpose: abstract shape-rendering component base. Use: derive concrete shape renderers.
- `src/engine/java/edu/nust/engine/core/components/renderers/BoxRenderer.java` - Purpose: rectangle drawing component. Use: render boxes/debug bodies/simple sprites.
- `src/engine/java/edu/nust/engine/core/components/renderers/CircleRenderer.java` - Purpose: circle drawing component. Use: render circular visuals/hit visuals.
- `src/engine/java/edu/nust/engine/core/components/renderers/SpriteRenderer.java` - Purpose: image sprite renderer component. Use: render textured game objects.

### Engine Interfaces and Core Utilities

- `src/engine/java/edu/nust/engine/core/files/URLUtils.java` - Purpose: URL and resource path helper methods. Use: normalize/resolve URL-based resource lookups.
- `src/engine/java/edu/nust/engine/core/gameobjects/Tag.java` - Purpose: tag marker contract for object labeling. Use: identify/query groups of objects.
- `src/engine/java/edu/nust/engine/core/interfaces/Initiable.java` - Purpose: lifecycle init contract. Use: implement startup initialization hooks.
- `src/engine/java/edu/nust/engine/core/interfaces/InputHandler.java` - Purpose: input callback contract. Use: handle keyboard/mouse events in scene/object code.
- `src/engine/java/edu/nust/engine/core/interfaces/Renderable.java` - Purpose: render callback contract. Use: custom drawing in the frame pipeline.
- `src/engine/java/edu/nust/engine/core/interfaces/Updatable.java` - Purpose: per-frame update contract. Use: game loop update and late-update logic.

### Engine Logging

- `src/engine/java/edu/nust/engine/logger/GameLogger.java` - Purpose: project logging wrapper and convenience API. Use: structured runtime diagnostics.
- `src/engine/java/edu/nust/engine/logger/LogProgress.java` - Purpose: progress-oriented logging helper. Use: track multi-step operations with status output.
- `src/engine/java/edu/nust/engine/logger/enums/LogFormats.java` - Purpose: predefined format/style constants for logs. Use: consistent message formatting.
- `src/engine/java/edu/nust/engine/logger/enums/LogLevel.java` - Purpose: log severity enum. Use: classify and filter logs.
- `src/engine/java/edu/nust/engine/logger/enums/LogProgressType.java` - Purpose: progress-state enum for step logging. Use: mark start/success/failure phases.
- `src/engine/java/edu/nust/engine/logger/logback/ANSIStripper.java` - Purpose: helper to remove ANSI sequences from output. Use: clean log lines for non-color targets.
- `src/engine/java/edu/nust/engine/logger/logback/ColoredConsoleAppender.java` - Purpose: custom Logback console appender with color support. Use: richer terminal logs.

### Engine Math and Resources

- `src/engine/java/edu/nust/engine/math/Angle.java` - Purpose: angle math/value abstraction. Use: rotation math and conversions.
- `src/engine/java/edu/nust/engine/math/Rectangle.java` - Purpose: rectangle geometry helper. Use: bounds math and intersection logic.
- `src/engine/java/edu/nust/engine/math/TimeSpan.java` - Purpose: delta-time representation wrapper. Use: frame-rate-independent updates.
- `src/engine/java/edu/nust/engine/math/Vector2D.java` - Purpose: 2D vector utility type. Use: movement, direction, and distance calculations.
- `src/engine/java/edu/nust/engine/resources/Resources.java` - Purpose: centralized resource loading helpers. Use: load images/FXML/assets safely.

## Game (`src/game/java`)

- `src/game/java/edu/nust/Launcher.java` - Purpose: launcher entry class for application startup. Use: main class configured by Gradle application plugin.
- `src/game/java/edu/nust/Main.java` - Purpose: JavaFX/game bootstrap entry point. Use: initializes and starts the game world.
- `src/game/java/edu/nust/game/MainWorld.java` - Purpose: concrete world configuration for this game. Use: set window config and initial scene.

### Game Scenes

- `src/game/java/edu/nust/game/scenes/demo/DemoScene.java` - Purpose: demo/testing scene implementation. Use: validate core mechanics quickly.
- `src/game/java/edu/nust/game/scenes/demo/gameobjects/MovingObject.java` - Purpose: simple moving demo object. Use: movement behavior experimentation.
- `src/game/java/edu/nust/game/scenes/demo/tags/MovingTag.java` - Purpose: tag for movable demo entities. Use: query/filter moving demo objects.
- `src/game/java/edu/nust/game/scenes/highscores/HighScoresScene.java` - Purpose: high-score UI scene. Use: display stored leaderboard entries.
- `src/game/java/edu/nust/game/scenes/highscores/highscores/HighScoreEntry.java` - Purpose: immutable high-score data record. Use: store one leaderboard row.
- `src/game/java/edu/nust/game/scenes/highscores/highscores/HighScoreStorage.java` - Purpose: persistence helper for high-score file IO. Use: read/write leaderboard data.
- `src/game/java/edu/nust/game/scenes/levelscene/LevelID.java` - Purpose: level identifier enum. Use: select and branch behavior by level.
- `src/game/java/edu/nust/game/scenes/levelscene/LevelScene.java` - Purpose: main gameplay level scene container. Use: run level loop and spawn level actors.
- `src/game/java/edu/nust/game/scenes/levelscene/level_1/Level1Background.java` - Purpose: level 1 background setup object. Use: render/static setup for level visuals.
- `src/game/java/edu/nust/game/scenes/levelscene/level_1/Level1CollisionMask.java` - Purpose: collision map/mask setup for level 1. Use: define walkable/blocked regions.
- `src/game/java/edu/nust/game/scenes/levelselect/LevelSelectScene.java` - Purpose: level selection menu scene. Use: route player into chosen level.
- `src/game/java/edu/nust/game/scenes/start/StartScene.java` - Purpose: starting menu scene and navigation hub. Use: move to demo, level select, high scores, or exit.

### Level Scene Game Objects

- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/_tags/EnemyTag.java` - Purpose: marker tag for enemy objects. Use: enemy queries and collision filtering.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/_tags/PlayerTag.java` - Purpose: marker tag for player object(s). Use: player lookups and interaction filtering.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/enemy/EnemyConfig.java` - Purpose: enemy configuration enum presets. Use: parameterize enemy stats/behavior by type.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/enemy/spawner/EnemySpawner.java` - Purpose: enemy spawn controller. Use: timed/conditional enemy creation.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/enemy/types/BasicEnemy.java` - Purpose: standard enemy implementation. Use: baseline hostile unit behavior.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/enemy/types/Boss.java` - Purpose: boss enemy implementation. Use: stronger/special encounter behavior.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/player/Character.java` - Purpose: shared character base behavior. Use: common movement/animation/health logic.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/player/CharacterAnimAssets.java` - Purpose: character animation asset mapping enum. Use: select animation resources by state.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/player/Health.java` - Purpose: health state model/component. Use: track and mutate HP.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/player/Player.java` - Purpose: player-controlled character implementation. Use: process input and player actions.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/statics/Tree.java` - Purpose: static decorative/obstacle tree object. Use: populate level scenery.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/unused/OrbitingBox.java` - Purpose: archived/experimental orbiting object behavior. Use: reference for experiments; not active gameplay.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Ammo.java` - Purpose: ammo state holder. Use: track current and max ammunition.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/AmmoBar.java` - Purpose: UI element for ammo display. Use: present ammo count visually.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Bullet.java` - Purpose: projectile entity. Use: hit detection and damage delivery.
- `src/game/java/edu/nust/game/scenes/levelscene/gameobjects/weapon/Weapon.java` - Purpose: weapon behavior controller. Use: fire projectiles, consume ammo, handle cooldown.
- `src/game/java/edu/nust/game/scenes/levelscene/hud/Bar.java` - Purpose: generic HUD bar base type. Use: derive health/ammo bars.
- `src/game/java/edu/nust/game/scenes/levelscene/hud/HealthBar.java` - Purpose: HUD health display component. Use: show player HP.

### Game Systems

- `src/game/java/edu/nust/game/systems/PlayerSession.java` - Purpose: player run/session state container. Use: persist values across scenes during one run.
- `src/game/java/edu/nust/game/systems/Score.java` - Purpose: score calculation and storage helper. Use: update and access scoring data.

#### Asset System

- `src/game/java/edu/nust/game/systems/assets/Asset.java` - Purpose: base asset descriptor abstraction. Use: represent concrete asset metadata.
- `src/game/java/edu/nust/game/systems/assets/AssetEnum.java` - Purpose: contract for enum-based asset identifiers. Use: standardize asset-key enums.
- `src/game/java/edu/nust/game/systems/assets/AssetManager.java` - Purpose: runtime asset registry/lookup manager. Use: load and fetch assets by key.
- `src/game/java/edu/nust/game/systems/assets/AssetType.java` - Purpose: enum of asset categories. Use: separate handling for images/audio/etc.
- `src/game/java/edu/nust/game/systems/assets/CharacterAsset.java` - Purpose: character asset key enum. Use: reference character sprites/resources.
- `src/game/java/edu/nust/game/systems/assets/EnemyAsset.java` - Purpose: enemy asset key enum. Use: reference enemy sprites/resources.
- `src/game/java/edu/nust/game/systems/assets/TilesetAsset.java` - Purpose: tileset asset key enum. Use: reference tile textures/maps.
- `src/game/java/edu/nust/game/systems/assets/WeaponAsset.java` - Purpose: weapon asset key enum. Use: reference weapon visuals/resources.

#### Audio, Collision, and Navigation

- `src/game/java/edu/nust/game/systems/audio/Audios.java` - Purpose: game-level audio constants/registry. Use: centralize named sounds/music.
- `src/game/java/edu/nust/game/systems/collision/CollisionManager.java` - Purpose: collision detection/dispatch coordinator. Use: evaluate overlaps and apply collision rules.
- `src/game/java/edu/nust/game/systems/collision/Concrete.java` - Purpose: marker/contract for solid world objects. Use: flag non-passable geometry.
- `src/game/java/edu/nust/game/systems/collision/ConcreteWall.java` - Purpose: concrete wall collision entity/helper. Use: instantiate solid boundaries.
- `src/game/java/edu/nust/game/systems/collision/Damageable.java` - Purpose: contract for objects that can receive damage. Use: unify damage intake across actors.
- `src/game/java/edu/nust/game/systems/collision/Damaging.java` - Purpose: contract for objects that deal damage. Use: identify offensive colliders/projectiles.
- `src/game/java/edu/nust/game/systems/collision/HitBox.java` - Purpose: hitbox representation and overlap checks. Use: define collision bounds.
- `src/game/java/edu/nust/game/systems/pathfinder/MapNodeSetter.java` - Purpose: helper to build/set map nodes. Use: prepare pathfinding graphs from level data.
- `src/game/java/edu/nust/game/systems/pathfinder/Node.java` - Purpose: pathfinding node data type. Use: represent graph/grid points.
- `src/game/java/edu/nust/game/systems/pathfinder/PathFinder.java` - Purpose: pathfinding algorithm implementation. Use: generate routes for AI movement.

#### Archived / Unused Tilemap System

- `src/game/java/edu/nust/game/systems/unused/tilemap/LevelBuilder.java` - Purpose: archived level construction helper for tilemaps. Use: reference-only for older tilemap flow.
- `src/game/java/edu/nust/game/systems/unused/tilemap/TileData.java` - Purpose: archived tile metadata model. Use: tile property definitions in old system.
- `src/game/java/edu/nust/game/systems/unused/tilemap/Tilemap.java` - Purpose: archived tilemap container/model. Use: older tile-based level representation.
- `src/game/java/edu/nust/game/systems/unused/tilemap/TilemapExample.java` - Purpose: archived tilemap usage example. Use: demo/reference for tilemap API.
- `src/game/java/edu/nust/game/systems/unused/tilemap/TilemapRenderer.java` - Purpose: archived tilemap renderer. Use: render tilemaps in deprecated prototype path.

## Additional Source Set (`src/main/java`)

- `src/main/java/edu/nust/game/assets/AssetSystemGuide.java` - Purpose: guide/example class for asset-system usage. Use: developer reference while integrating assets.
- `src/main/java/edu/nust/game/scenes/Level1WalkablePathClamper.java` - Purpose: level 1 path clamping utility. Use: constrain movement/path points to valid walkable area.

## Tests (`src/tests`)

- `src/tests/edu/nust/tests/TestLogger.java` - Purpose: logger test harness entry point. Use: manually verify logging behavior/formatting.

## Notes

- This file intentionally replaces per-module documentation for class/file purpose lookup in one place.
- If you want, this can be extended to include non-Java files (`.fxml`, `.css`, `.md`, Gradle files) in the same format.
