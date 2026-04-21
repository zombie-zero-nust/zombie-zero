# CS-212-SP Project Information

## Project Overview
This is a **Java-based 2D Game Engine** built with **JavaFX** for rendering. It's a component-based game architecture with scene management, camera system, and object pooling patterns.

**Project Name:** cs-212-sp  
**Language:** Java  
**Build Tool:** Gradle  
**Game Framework:** Custom Engine + JavaFX 23.0.2  
**Package Structure:** `edu.nust`

---

## Build Configuration

### Gradle Plugins
- `java` - Java compilation
- `application` - Executable JAR creation
- `org.openjfx.javafxplugin v0.1.0` - JavaFX integration

### Key Dependencies
- **JavaFX 23.0.2** - UI & Graphics rendering
  - Modules: `javafx.controls`, `javafx.fxml`, `javafx.media`
- **Logging Stack**
  - `org.slf4j:slf4j-api:2.0.17`
  - `ch.qos.logback:logback-classic:1.5.32`
  - `org.fusesource.jansi:jansi:2.4.1` (colored console output)
- **Testing:** JUnit 5 (Jupiter) 5.10.0
- **Annotations:** Jetbrains annotations 24.1.0 (compileOnly)

### Main Classes
- **Entry Point:** `edu.nust.Main` (JavaFX Application)
- **Game World:** `edu.nust.game.MainWorld` (extends GameWorld)
- **Test Entry:** `edu.nust.tests.TestLogger` (alternative entry point)

### Execution Configurations
```gradle
mainClass = 'edu.nust.Main' (default)
mainClass = 'edu.nust.tests.TestLogger' (with -PtestLogger flag)
```

**Custom Gradle Tasks:**
- `runNoLogs` - Run with logging disabled
- `runTestLogger` - Run TestLogger entry point

---

## Architecture Overview

### Core Engine Structure (`edu.nust.engine`)

#### 1. **Core System** (`engine.core`)
- **GameObject** - Base class for all game objects
  - Has components, tags, and lifecycle methods
  - Supports add/remove/query components
  - Active/Visible state management
  - Inherits: `Initiable`, `Updatable<GameObject>`, `Renderable<GameObject>`

- **Component** - Base class for all game components
  - Attached to GameObjects (composition pattern)
  - Examples: Transform, Renderers, Colliders
  - Active/Visible state management
  - Lifecycle: `onInit()`, `onUpdate()`, `lateUpdate()`, `onRender()`

- **GameWorld** - Main window & game loop manager
  - Manages Stage (JavaFX window)
  - Runs the main AnimationTimer loop
  - Handles scene switching
  - Properties: size, title, fullscreen, cursor, resizable
  - Loads fonts and stylesheets

- **GameScene** - Container for game objects & UI
  - Manages two layers:
    1. **World Layer** - Canvas for game objects
    2. **UI Layer** - JavaFX nodes (FXML-based)
  - Loads FXML from `resources/scenes/{SceneName}/layout.fxml`
  - Loads CSS from `resources/scenes/{SceneName}/style.css`
  - Contains GameCamera for viewport management
  - Manages GameObject lifecycle in scene

- **GameCamera** - Viewport management
  - Zoom and pan functionality
  - Applies transformations to GraphicsContext

#### 2. **Components** (`engine.core.components`)
- **Transform** - Position, rotation, scale
- **Renderers** (in `components/renderers/`)
  - `ShapeRenderer` - Base class
  - `BoxRenderer` - Draws rectangles
  - `CircleRenderer` - Draws circles
  - `SpriteRenderer` - Draws images
- Custom components inherit from `Component`

#### 3. **Game Objects** (`engine.core.gameobjects`)
- **Tag** - Marker interface for object identification
  - Tags are class-based (no instances)
  - Support inheritance checking

#### 4. **Interfaces** (`engine.core.interfaces`)
- **Initiable** - `onInit()` method
- **Updatable<T>** - `onUpdate(TimeSpan)`, `lateUpdate(TimeSpan)` methods
- **Renderable<T>** - `onRender(GraphicsContext)` method
- **InputHandler** - Keyboard/mouse input event handlers
  - `onKeyPressed(KeyEvent)`
  - `onKeyReleased(KeyEvent)`
  - `onMousePressed(MouseEvent)`
  - `onMouseReleased(MouseEvent)`
  - `onMouseMoved(MouseEvent)`
  - `onMouseDragged(MouseEvent)`

#### 5. **Math Utilities** (`engine.math`)
- **Vector2D** - 2D vector with operations
- **Angle** - Angle representation
- **TimeSpan** - Time delta representation

#### 6. **Logger System** (`engine.logger`)
- **GameLogger** - Centralized logging (SLF4J-based)
  - Log Levels: TRACE, DEBUG, INFO, WARN, ERROR, SUCCESS
  - Color-coded output via Jansi
- **LogProgress** - Progress logging for multi-step operations
- **LogFormats** - Pre-formatted text output enums
- Configuration: `logback.xml`, `logback-off.xml` in `resources/edu/nust/engine/logger/`

#### 7. **Resources** (`engine.resources`)
- **Resources** - Static utility for loading assets
  - Images, fonts, FXML files
  - Methods: `getResourceOrThrow()`, `tryGetResource()`, `loadImageOrThrow()`

#### 8. **Audio** (`engine.core`)
- **GameAudioManager** - Audio playback management

---

## Game-Specific Code (`edu.nust.game`)

### Game Objects (`game.gameobjects`)
- **Character** - Base class for player & enemies
- **Player** - Controlled by player input
  - Supports WASD movement
  - Health system
  - Weapon/ammo system
- **Enemy** - AI-controlled enemies
- **Weapon** - Weapon system
- **Bullet** - Projectiles
- **Ammo** / **AmmoBar** - Ammunition tracking
- **Health** / **HealthBar** - Health tracking
- **HitBox** / **OrbitingBox** - Collision/detection
- **Bar** - Base class for UI bars
- **MovingObject** - Base for moving entities
- **EnemyManager** - Enemy spawning/management
- **EnemyConfig** - Enemy configuration

### Tags (`game.gameobjects`)
- **PlayerTag** - Identifies player objects
- **EnemyTag** - Identifies enemy objects
- **MovingTag** - Identifies moving objects

### Scenes (`game.scenes`)
- **StartScene** - Initial menu/start screen
- **MainGameScene** - Main gameplay
- **LevelScene** - Level-specific scene
- Each has:
  - `layout.fxml` - UI definition
  - `style.css` - Styling (optional)

### World Implementation
- **MainWorld** - Concrete GameWorld implementation
  - Window size: 1280x768
  - Title: "Test World"
  - Centered on screen

### Game State
- **Score** - Score tracking system

---

## File Organization

```
src/main/
├── java/edu/nust/
│   ├── Main.java                          # Entry point
│   ├── engine/
│   │   ├── core/
│   │   │   ├── GameObject.java           # 475 lines
│   │   │   ├── GameWorld.java            # 295 lines
│   │   │   ├── GameScene.java            # 611 lines
│   │   │   ├── Component.java
│   │   │   ├── GameCamera.java
│   │   │   ├── GameAudioManager.java
│   │   │   ├── components/
│   │   │   │   ├── Transform.java
│   │   │   │   └── renderers/
│   │   │   │       ├── ShapeRenderer.java
│   │   │   │       ├── BoxRenderer.java
│   │   │   │       ├── CircleRenderer.java
│   │   │   │       └── SpriteRenderer.java
│   │   │   ├── gameobjects/
│   │   │   │   └── Tag.java
│   │   │   └── interfaces/
│   │   │       ├── Initiable.java
│   │   │       ├── Updatable.java
│   │   │       ├── Renderable.java
│   │   │       └── InputHandler.java
│   │   ├── math/
│   │   │   ├── Vector2D.java
│   │   │   ├── Angle.java
│   │   │   └── TimeSpan.java
│   │   ├── logger/
│   │   │   ├── GameLogger.java
│   │   │   ├── LogProgress.java
│   │   │   ├── enums/
│   │   │   └── logback/
│   │   │       ├── logback.xml
│   │   │       └── logback-off.xml
│   │   └── resources/
│   │       └── Resources.java
│   ├── game/
│   │   ├── MainWorld.java
│   │   ├── Score.java
│   │   ├── gameobjects/
│   │   │   ├── Character.java
│   │   │   ├── Player.java
│   │   │   ├── Enemy.java
│   │   │   ├── Weapon.java
│   │   │   ├── Bullet.java
│   │   │   ├── Ammo.java
│   │   │   ├── AmmoBar.java
│   │   │   ├── Health.java
│   │   │   ├── HealthBar.java
│   │   │   ├── HitBox.java
│   │   │   ├── MovingObject.java
│   │   │   ├── OrbitingBox.java
│   │   │   ├── Bar.java
│   │   │   ├── EnemyManager.java
│   │   │   ├── EnemyConfig.java
│   │   │   ├── PlayerTag.java
│   │   │   ├── EnemyTag.java
│   │   │   └── MovingTag.java
│   │   └── scenes/
│   │       ├── StartScene.java
│   │       ├── MainGameScene.java
│   │       └── LevelScene.java
│   └── tests/
│       └── TestLogger.java
└── resources/edu/nust/
    ├── engine/logger/
    │   ├── logback.xml
    │   └── logback-off.xml
    └── game/
        ├── assets/
        │   ├── fonts/
        │   │   └── PixelifySans.ttf
        │   └── images/
        │       └── test.png
        └── scenes/
            ├── common.css
            ├── StartScene/
            │   ├── layout.fxml
            │   └── style.css
            ├── MainGameScene/
            │   ├── layout.fxml
            │   └── style.css
            └── LevelScene/
                ├── layout.fxml
                └── style.css
```

---

## Key Design Patterns

### 1. **Component-Based Architecture**
- GameObjects contain Components
- Flexible composition over inheritance
- Example: `Player` has `Transform`, `SpriteRenderer`, `Health`, etc.

### 2. **Factory Methods**
- `GameObject.create()` - Create simple GO
- `GameObject.create(Consumer)` - Create with initializer lambda
- Scene spawning methods: `spawnGameObject()`, `addGameObject()`

### 3. **Fluent/Chainable API**
- Most methods return `this` or the object for chaining
- Example: `window.setSize(1280, 768).centerWindow().setResizable(false)`

### 4. **Lifecycle Management**
- **Initialization Phase:** `onInit()`
- **Update Phase:** `onUpdate(TimeSpan)`
- **Late Update Phase:** `lateUpdate(TimeSpan)` (for post-physics)
- **Render Phase:** `onRender(GraphicsContext)`
- **Activation/Visibility:** `setActive()`, `setVisible()`

### 5. **Tag-Based Identification**
- Query objects by type or tag
- Tag inheritance support
- Methods: `hasTag()`, `addTag()`, `removeTag()`
- Scene queries: `getFirstWithTag()`, `getAllWithTag()`

### 6. **Event System**
- Input events: keyboard, mouse
- Scene-level handlers (can propagate to GameObjects)

### 7. **Resource Management**
- Centralized Resources utility
- Lazy loading of assets
- Path-based resource resolution

### 8. **Scene Management**
- Dual-layer rendering (World + UI)
- Scene switching via `window.setScene()`
- FXML-based UI system
- CSS styling per scene

---

## Game Loop

```
GameLoop (AnimationTimer):
├── Calculate ΔTime (TimeSpan)
├── Scene.onUpdate(ΔTime)
├── Each GameObject.onUpdate(ΔTime)
├── Each Component.onUpdate(ΔTime)
├── Each GameObject.lateUpdate(ΔTime)
├── Each Component.lateUpdate(ΔTime)
├── Scene.lateUpdate(ΔTime)
├── Remove queued GameObjects
├── Add queued GameObjects
├── Clear Canvas
└── Scene.onRender()
    ├── Apply Camera Transformations
    ├── Each GameObject.onRender(ctx)
    └── Each Component.onRender(ctx)
```

---

## Rendering System

### Canvas-Based Rendering
- Uses JavaFX `Canvas` + `GraphicsContext`
- Camera applies transformations (translate, scale)
- Rendering happens in `fetchWorldContextAndRun()` method

### Renderer Types
- **ShapeRenderer** - Base for shape rendering
- **BoxRenderer** - Rectangle rendering
- **CircleRenderer** - Circle rendering
- **SpriteRenderer** - Image-based rendering

---

## Important Conventions

### Naming
- GameObjects: PascalCase (e.g., `Player`, `Enemy`, `Bullet`)
- Components: Descriptive names with "Renderer" suffix (e.g., `BoxRenderer`)
- Tags: PascalCase ending with "Tag" (e.g., `PlayerTag`, `EnemyTag`)

### Resource Paths
- **FXML:** `resources/scenes/{SceneName}/layout.fxml`
- **CSS:** `resources/scenes/{SceneName}/style.css`
- **Images:** `resources/assets/images/{name}.png`
- **Fonts:** `resources/assets/fonts/{name}.ttf`

### Logging
- Use `protected final GameLogger logger = GameLogger.getLogger(this.getClass());`
- Log levels: TRACE, DEBUG, INFO, WARN, ERROR, SUCCESS
- Use `LogProgress` for multi-step operations

---

## Common API Usage Patterns

### Creating a GameObject
```java
// Simple creation
GameObject obj = GameObject.create();
obj.addComponent(new SpriteRenderer(...));

// Factory with initializer
GameObject obj = GameObject.create(go -> {
    go.addComponent(new SpriteRenderer(...));
    go.getTransform().setPosition(100, 200);
    go.addTag(PlayerTag.class);
});
```

### Adding to Scene
```java
scene.addGameObject(newObject);
scene.spawnGameObject(new Enemy(), 50, 100);
```

### Querying Objects
```java
GameObject player = scene.getFirstOfType(Player.class);
List<GameObject> enemies = scene.getAllWithTag(EnemyTag.class);
```

### Transform Access
```java
Transform t = gameObject.getTransform();
t.setPosition(100, 200);
Vector2D pos = t.getPosition();
```

### Component Management
```java
gameObject.addComponent(new BoxRenderer(50, 50));
BoxRenderer renderer = gameObject.getFirstComponent(BoxRenderer.class);
boolean hasRenderer = gameObject.hasAnyComponentOfType(BoxRenderer.class);
List<Component> allComps = gameObject.getAllComponents();
```

### Scene Management
```java
window.setScene(new MainGameScene(window));
GameScene currentScene = window.getScene();
GameCamera camera = currentScene.getWorldCamera();
camera.setZoom(2.0);
```

---

## Testing Infrastructure

- **Test Entry Point:** `edu.nust.tests.TestLogger`
- **Framework:** JUnit 5
- **Logging:** Full logging enabled for tests
- Run via: `gradle runTestLogger`

---

## Performance Considerations

1. **Copy-on-Iterate Pattern**
   - GameObjects are copied when iterated to allow add/remove during updates
   - Uses `gameObjectsToAdd` and `gameObjectsToRemove` queues

2. **Lazy Resource Loading**
   - Resources are loaded on-demand
   - Fonts loaded once at startup

3. **Active/Visible Flags**
   - Skip updates for inactive objects
   - Skip rendering for invisible objects

4. **Canvas Optimization**
   - Clear entire canvas each frame
   - Batch render operations when possible

---

## Configuration & Customization

### Window Configuration (MainWorld)
- Size: 1280x768 (editable in `initStage()`)
- Resizable: Set in `initStage()`
- Fullscreen: `window.setFullscreen(true)`

### Logging Configuration
- Default: `logback.xml` (INFO level)
- No-logs mode: `logback-off.xml`
- Global level: `GameLogger.setGlobalLevel(LogLevel.X)`

### Font Loading
- Loads `PixelifySans.ttf` at startup
- Loaded in `GameWorld.loadFont()`

---

## Future Development Areas

1. **Physics System** - Collision detection, rigidbody simulation
2. **Particle System** - Effects and visual feedback
3. **Audio Integration** - Sound effects, background music (framework exists)
4. **State Machine** - Scene state management
5. **Prefab System** - Reusable object templates
6. **Networking** - Multiplayer support
7. **Animation System** - Sprite animation keyframes
8. **UI Framework Expansion** - More FXML/JavaFX integration

---

## Notes for Future Development

- Always extend `GameObject` or `Component` for new game elements
- Use `addTag()` for semantic identification instead of instanceof
- Prefer `getTransform()` for position/rotation access
- Use `GameObject.create(Consumer)` for inline configuration
- Scene queries (`getFirstOfType()`, `getAllWithTag()`) for object discovery
- Implement lifecycle methods (`onInit()`, `onUpdate()`, `onRender()`) for behavior
- Avoid direct list manipulation; use provided add/remove methods
- Handle delta time properly: `deltaTime.asSeconds()` for frame-rate independence
- Use logging consistently with `logger` instance


