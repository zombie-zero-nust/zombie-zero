# Engine API Utility Catalog (Game-Developer Focus)

Scope: `edu.nust.engine` only
Style: mutable chaining for mutators (`return this`)
Delivery: both a master catalog (full scope) and iterative class-by-class rollout

## Checklist

- [x] Restrict proposals to classes under `edu.nust.engine`
- [x] Use mutable chaining as default API style
- [x] Produce one master file with 100s of utility method ideas
- [x] Define class-by-class rollout order and improvement batches
- [x] Start from `Transform` and move outward by dependency

## API Design Rules (for all classes)

1. Mutating methods return `this` for fluent gameplay code.
2. Read/query methods return primitive/value types and do not mutate.
3. Add overloads for both primitives and value objects.
4. Prefer explicit naming (`setX`, `moveBy`, `clampMagnitude`) over ambiguous verbs.
5. Include safe variants where runtime issues are likely (`tryLoad`, `orElse`, `ifPresent`).
6. Keep game loop ergonomics first: fewer temporary objects, straightforward chaining.

---

## Master Catalog (200+ Proposed Methods)

Legend: `P0` essential, `P1` high value, `P2` nice-to-have

### 1) `Transform` (`src/main/java/edu/nust/engine/core/components/Transform.java`)

#### Position and movement
- `P0` `setPosition(double x, double y)`
- `P0` `setX(double x)`
- `P0` `setY(double y)`
- `P0` `moveBy(double dx, double dy)`
- `P0` `moveBy(Vector2D delta)`
- `P0` `moveForward(double distance)`
- `P0` `moveBackward(double distance)`
- `P1` `moveRight(double distance)`
- `P1` `moveLeft(double distance)`
- `P1` `moveToward(Vector2D target, double maxDistance)`
- `P1` `teleport(double x, double y)`
- `P1` `setPositionX(double x)`
- `P1` `setPositionY(double y)`
- `P1` `addPositionX(double dx)`
- `P1` `addPositionY(double dy)`
- `P2` `snapToGrid(double cellSize)`
- `P2` `snapToGrid(double cellX, double cellY)`
- `P2` `wrapPosition(double minX, double minY, double maxX, double maxY)`

#### Rotation and orientation
- `P0` `setRotationDegrees(double degrees)`
- `P0` `setRotationRadians(double radians)`
- `P0` `rotateDegrees(double deltaDegrees)`
- `P0` `rotateRadians(double deltaRadians)`
- `P0` `lookAt(Vector2D worldPoint)`
- `P1` `lookAt(double x, double y)`
- `P1` `lookAlong(Vector2D direction)`
- `P1` `setForward(Vector2D direction)`
- `P1` `rotateAround(Vector2D pivot, Angle delta)`
- `P1` `rotateAround(double px, double py, Angle delta)`
- `P2` `normalizeRotation()`
- `P2` `clampRotation(double minDegrees, double maxDegrees)`
- `P2` `setFacingRight()`
- `P2` `setFacingUp()`

#### Direction vectors and utility queries
- `P0` `right()`
- `P0` `up()`
- `P0` `distanceTo(Vector2D point)`
- `P0` `distanceTo(Transform other)`
- `P1` `distanceSquaredTo(Vector2D point)`
- `P1` `angleTo(Vector2D point)`
- `P1` `directionTo(Vector2D point)`
- `P2` `isFacing(Vector2D direction, double toleranceDegrees)`
- `P2` `isNear(Vector2D point, double radius)`

#### Anchor and bounds helpers
- `P0` `setAnchor(double ax, double ay)`
- `P1` `setAnchorCentered()`
- `P1` `setAnchorTopLeft()`
- `P1` `setAnchorTopRight()`
- `P1` `setAnchorBottomLeft()`
- `P1` `setAnchorBottomRight()`
- `P2` `withAnchorPreset(AnchorPreset preset)`
- `P2` `computeOffset(double width, double height)`

Total proposed for `Transform`: 45+

### 2) `Vector2D` (`src/main/java/edu/nust/engine/math/Vector2D.java`)

#### In-place mutators (fluent)
- `P0` `set(double x, double y)`
- `P0` `set(Vector2D other)`
- `P0` `addInPlace(Vector2D other)`
- `P0` `addInPlace(double x, double y)`
- `P0` `subtractInPlace(Vector2D other)`
- `P0` `multiplyInPlace(double scalar)`
- `P0` `divideInPlace(double scalar)`
- `P1` `negateInPlace()`
- `P1` `absInPlace()`
- `P1` `floorInPlace()`
- `P1` `ceilInPlace()`
- `P1` `roundInPlace()`
- `P1` `clampInPlace(double min, double max)`
- `P1` `clampInPlace(Vector2D min, Vector2D max)`
- `P1` `clampMagnitudeInPlace(double maxMagnitude)`
- `P2` `lerpInPlace(Vector2D target, double t)`
- `P2` `moveTowardInPlace(Vector2D target, double maxDistance)`
- `P2` `rotateInPlace(Angle angle)`

#### Query and algebra methods
- `P0` `lengthSquared()`
- `P0` `distance(Vector2D other)`
- `P0` `distanceSquared(Vector2D other)`
- `P0` `copy()`
- `P0` `equalsEpsilon(Vector2D other, double eps)`
- `P1` `cross(Vector2D other)`
- `P1` `angleTo(Vector2D other)`
- `P1` `projectOnto(Vector2D other)`
- `P1` `reflect(Vector2D normal)`
- `P1` `perpendicular()`
- `P1` `isZero(double eps)`
- `P2` `isFinite()`
- `P2` `maxComponent()`
- `P2` `minComponent()`

#### Static helpers
- `P0` `zero()`
- `P0` `one()`
- `P0` `upVector()`
- `P0` `rightVector()`
- `P1` `fromAngle(Angle angle)`
- `P1` `lerp(Vector2D a, Vector2D b, double t)`
- `P1` `distance(Vector2D a, Vector2D b)`
- `P2` `smoothDamp(...)`

Total proposed for `Vector2D`: 40+

### 3) `Angle` (`src/main/java/edu/nust/engine/math/Angle.java`)

- `P0` `set(Angle other)`
- `P0` `setDegrees(double degrees)` (chainable)
- `P0` `setRadiansChain(double radians)`
- `P0` `addInPlace(Angle other)`
- `P0` `subtractInPlace(Angle other)`
- `P0` `multiplyInPlace(double scalar)`
- `P0` `divideInPlace(double scalar)`
- `P0` `normalize360()`
- `P0` `normalize180()`
- `P1` `deltaTo(Angle target)`
- `P1` `lerp(Angle target, double t)`
- `P1` `moveToward(Angle target, double maxDeltaDegrees)`
- `P1` `sin()`
- `P1` `cos()`
- `P1` `tan()`
- `P1` `isNear(Angle other, double toleranceDegrees)`
- `P2` `snap(double stepDegrees)`
- `P2` `clamp(double minDegrees, double maxDegrees)`
- `P2` `fromRadians(double radians)`
- `P2` `fromDegrees(double degrees)`

Total proposed for `Angle`: 20+

### 4) `Vector2I` (`src/main/java/edu/nust/engine/math/Vector2I.java`)

- `P0` `set(int x, int y)`
- `P0` `set(Vector2I other)`
- `P0` `addInPlace(Vector2I other)`
- `P0` `subtractInPlace(Vector2I other)`
- `P0` `multiplyInPlace(int scalar)`
- `P0` `divideInPlace(int scalar)`
- `P1` `clampInPlace(int min, int max)`
- `P1` `clampInPlace(Vector2I min, Vector2I max)`
- `P1` `manhattanDistance(Vector2I other)`
- `P1` `chebyshevDistance(Vector2I other)`
- `P1` `toVector2D()`
- `P1` `toVector2UIClamped()`
- `P2` `rotate90CWInPlace()`
- `P2` `rotate90CCWInPlace()`
- `P2` `isWithinRect(int minX, int minY, int maxX, int maxY)`
- `P2` `neighbors4()`
- `P2` `neighbors8()`

Total proposed for `Vector2I`: 17+

### 5) `Vector2UI` (`src/main/java/edu/nust/engine/math/Vector2UI.java`)

- `P0` `set(double x, double y)`
- `P0` `set(Vector2D other)`
- `P0` `addInPlace(double dx, double dy)`
- `P0` `addInPlace(Vector2D other)`
- `P0` `multiplyInPlace(double scalar)`
- `P0` `invertInPlace()`
- `P1` `saturate()`
- `P1` `lerpInPlace(Vector2UI target, double t)`
- `P1` `moveTowardInPlace(Vector2UI target, double maxDelta)`
- `P1` `toColorAlphaOne()`
- `P2` `toUVWrapped()`
- `P2` `withGamma(double gamma)`
- `P2` `equalsEpsilon(Vector2UI other, double eps)`

Total proposed for `Vector2UI`: 13+

### 6) `TimeSpan` (`src/main/java/edu/nust/engine/math/TimeSpan.java`)

- `P0` `zero()`
- `P0` `fromMinutes(double minutes)`
- `P0` `fromHours(double hours)`
- `P0` `asMinutes()`
- `P0` `asHours()`
- `P0` `add(TimeSpan other)`
- `P0` `subtract(TimeSpan other)`
- `P0` `multiply(double scalar)`
- `P0` `divide(double scalar)`
- `P1` `clamp(TimeSpan min, TimeSpan max)`
- `P1` `max(TimeSpan a, TimeSpan b)`
- `P1` `min(TimeSpan a, TimeSpan b)`
- `P1` `isZero()`
- `P1` `isNegative()`
- `P1` `abs()`
- `P2` `formatHms()`
- `P2` `formatMs()`

Total proposed for `TimeSpan`: 17+

### 7) `Component` (`src/main/java/edu/nust/engine/core/Component.java`)

- `P0` `isEnabled()`
- `P0` `setEnabled(boolean enabled)`
- `P0` `enable()`
- `P0` `disable()`
- `P0` `toggleEnabled()`
- `P0` `getTransform()`
- `P0` `getScene()`
- `P0` `getWindow()`
- `P1` `tryGetComponent(Class<T> type)`
- `P1` `requireComponent(Class<T> type)`
- `P1` `addComponent(T component)`
- `P1` `removeSelf()`
- `P1` `destroyGameObject()`
- `P1` lifecycle additions: `onEnable()`, `onDisable()`, `onDestroy()`
- `P2` `setTag(String tag)`
- `P2` `hasTag(String tag)`

Total proposed for `Component`: 16+

### 8) `GameObject` (`src/main/java/edu/nust/engine/core/GameObject.java`)

- `P0` `setName(String name)`
- `P0` `getName()`
- `P0` `setActive(boolean active)`
- `P0` `isActive()`
- `P0` `activate()`
- `P0` `deactivate()`
- `P0` `toggleActive()`
- `P0` `removeComponent(Class<? extends Component> type)`
- `P0` `hasComponent(Class<? extends Component> type)`
- `P0` `getOrAddComponent(Supplier<T> factory)`
- `P1` `getComponents(Class<T> type)`
- `P1` `forEachComponent(Consumer<Component> action)`
- `P1` `destroy()`
- `P1` `isDestroyed()`
- `P1` `setLayer(int layer)`
- `P1` `getLayer()`
- `P1` `setTag(String tag)`
- `P1` `hasTag(String tag)`
- `P2` `setParent(GameObject parent)`
- `P2` `getParent()`
- `P2` `addChild(GameObject child)`
- `P2` `removeChild(GameObject child)`
- `P2` `children()`
- `P2` `findChildByName(String name)`

Total proposed for `GameObject`: 24+

### 9) `GameScene` (`src/main/java/edu/nust/engine/core/GameScene.java`)

- `P0` `removeGameObject(GameObject object)`
- `P0` `clearGameObjects()`
- `P0` `findByTag(String tag)`
- `P0` `findFirstByTag(String tag)`
- `P0` `findByName(String name)`
- `P0` `countGameObjects()`
- `P0` `pause()`
- `P0` `resume()`
- `P0` `isPaused()`
- `P1` `spawn(Supplier<GameObject> prefab, Vector2D position)`
- `P1` `destroy(GameObject object)`
- `P1` `destroyAllByTag(String tag)`
- `P1` `forEachObject(Consumer<GameObject> action)`
- `P1` `forEachComponent(Class<T> type, Consumer<T> action)`
- `P1` input hooks: `onKeyReleased`, `onMouseMoved`, `onMousePressed`, `onMouseReleased`
- `P1` `setBackgroundColor(Color color)`
- `P1` `setClearEnabled(boolean clearEnabled)`
- `P2` `setTimeScale(double scale)`
- `P2` `getDeltaTime()`
- `P2` `setFixedUpdateRate(double hz)`
- `P2` `onFixedUpdate(TimeSpan fixedDelta)`

Total proposed for `GameScene`: 23+

### 10) `GameWindow` (`src/main/java/edu/nust/engine/core/GameWindow.java`)

- `P0` `setSize(double width, double height)`
- `P0` `setResizable(boolean resizable)`
- `P0` `setFullscreen(boolean fullscreen)`
- `P0` `toggleFullscreen()`
- `P0` `setVsync(boolean enabled)`
- `P0` `setTargetFps(int fps)`
- `P0` `getTargetFps()`
- `P0` `switchScene(Supplier<GameScene> sceneFactory)`
- `P1` `setIcon(String... path)`
- `P1` `setWindowPosition(double x, double y)`
- `P1` `centerWindow()`
- `P1` `setMinSize(double w, double h)`
- `P1` `setMaxSize(double w, double h)`
- `P1` `setCursorVisible(boolean visible)`
- `P1` `captureMouse(boolean captured)`
- `P1` `isFocused()`
- `P2` `setBackgroundScene(GameScene scene)`
- `P2` `setOnClose(Runnable handler)`
- `P2` stats helpers: `getFps()`, `getFrameTime()`

Total proposed for `GameWindow`: 19+

### 11) `Resources` (`src/main/java/edu/nust/engine/resources/Resources.java`)

- `P0` `tryLoadImage(String... path)`
- `P0` `loadImageOrThrow(String... path)`
- `P0` `exists(String... path)`
- `P0` `resolvePathFromBase(String base, String... path)`
- `P1` cache APIs: `cacheImage`, `getCachedImage`, `clearImageCache`, `removeCachedImage`
- `P1` typed loaders: `loadText`, `loadJson`, `loadBytes`, `loadFXML`, `loadCss`
- `P1` safety APIs: `tryLoadText`, `tryLoadJson`, `tryLoadBytes`
- `P1` diagnostics: `listResources(String... path)`, `debugDumpBasePath()`
- `P2` async loaders: `loadImageAsync`, `prefetch(String... path)`
- `P2` convenience: `asset(String relativePath)`, `sceneAsset(String sceneName, String fileName)`

Total proposed for `Resources`: 20+

### 12) `BoxRenderer` (`src/main/java/edu/nust/engine/core/components/renderers/BoxRenderer.java`)

- `P0` `setSize(double width, double height)`
- `P0` `setColor(Color color)`
- `P0` `setOpacity(double alpha)`
- `P0` `setFilled(boolean filled)`
- `P1` `setStroke(Color color, double width)`
- `P1` `setRoundedCorners(double radius)`
- `P1` `setVisible(boolean visible)`
- `P1` `setRenderOrder(int order)`
- `P2` `pulseColor(Color a, Color b, double speed)`
- `P2` `setBlendMode(BlendMode mode)`

Total proposed for `BoxRenderer`: 10+

### 13) `CircleRenderer` (`src/main/java/edu/nust/engine/core/components/renderers/CircleRenderer.java`)

- `P0` `setRadius(double radius)`
- `P0` `setColor(Color color)`
- `P0` `setOpacity(double alpha)`
- `P0` `setFilled(boolean filled)`
- `P1` `setStroke(Color color, double width)`
- `P1` `setVisible(boolean visible)`
- `P1` `setRenderOrder(int order)`
- `P2` `setArc(double startDegrees, double sweepDegrees)`
- `P2` `setBlendMode(BlendMode mode)`

Total proposed for `CircleRenderer`: 9+

### 14) `SpriteRenderer` (`src/main/java/edu/nust/engine/core/components/renderers/SpriteRenderer.java`)

- `P0` `setSize(double width, double height)`
- `P0` `setImage(Image image)`
- `P0` `setFrame(int x, int y)`
- `P0` `setFrameLinear(int index)`
- `P0` `nextFrame()`
- `P0` `previousFrame()`
- `P0` `setVisible(boolean visible)`
- `P1` tinting APIs: `setTint(Color tint)`, `clearTint()`, `setOpacity(double alpha)`
- `P1` animation APIs: `play(String clip)`, `stop()`, `pause()`, `resume()`, `isPlaying()`
- `P1` clip APIs: `addClip(String name, int startX, int startY, int length, double fps, boolean loop)`
- `P1` flipping APIs: `setFlipX(boolean)`, `setFlipY(boolean)`
- `P2` sorting/blend: `setRenderOrder(int order)`, `setBlendMode(BlendMode mode)`
- `P2` billboarding helper: `faceDirection(Vector2D dir)`

Total proposed for `SpriteRenderer`: 18+

Estimated total proposals across engine: 290+ method ideas

---

## Iterative Rollout (One Class at a Time)

### Batch A (Core movement feel) - start here
1. `Transform` (`P0`, then `P1` movement/orientation)
2. `Vector2D` (`P0` in-place mutators + distance helpers)
3. `Angle` (`normalize`, delta, moveToward)

### Batch B (Gameplay authoring ergonomics)
4. `Component` (enabled state + lifecycle + shortcuts)
5. `GameObject` (active state, naming/tagging, component ergonomics)
6. `GameScene` (query/spawn/destroy utilities)

### Batch C (Rendering workflow)
7. `SpriteRenderer` (frame + clip + visibility)
8. `BoxRenderer` (styling and visibility)
9. `CircleRenderer` (styling and visibility)

### Batch D (Window/resources/time)
10. `GameWindow` (scene switching, fps/window helpers)
11. `Resources` (typed loading, cache, safe APIs)
12. `TimeSpan` (`P0` arithmetic and formatting)
13. `Vector2I`
14. `Vector2UI`

---

## Class-by-Class Improvement Notes (Game Developer Lens)

### `Transform` first (critical for feel)
- Add direct primitive overloads so movement code is one-liner fluent chains.
- Add intent-driven verbs (`lookAt`, `moveToward`, `rotateAround`) used every frame in gameplay scripts.
- Keep read helpers (`distanceTo`, `directionTo`) allocation-light.
- Add anchor presets to reduce boilerplate in 2D game objects.

### `Vector2D` second
- Keep existing immutable-style ops, but add mutable in-place alternatives for high-frequency updates.
- Ensure all in-place methods return `this` for chaining in movement/AI code.
- Add interpolation and clamp helpers to reduce duplicate logic in game scripts.

### `Angle` third
- Normalize and shortest-path delta are needed for smooth steering and aiming.
- Move-toward APIs avoid overshoot logic copy/paste in update loops.

### Remaining classes
- `GameObject`/`Component` should feel like Unity ergonomics (`active`, `enabled`, tags, lifecycle hooks).
- `GameScene` should expose practical spawn/query/destroy APIs.
- Renderers should support visibility/tint/style changes from gameplay code without subclassing.
- `Resources` should provide safe typed loads and cache helpers to reduce runtime surprises.

---

## Suggested implementation cadence

- Iteration 1: `Transform` + `Vector2D` + `Angle` (`P0` only)
- Iteration 2: same classes (`P1`) and replace gameplay call sites
- Iteration 3: `Component` + `GameObject` (`P0`)
- Iteration 4: `GameScene` + `SpriteRenderer` (`P0`)
- Iteration 5+: the rest by priority

If you want, next I can generate `Iteration 1` as actual code changes in `Transform`, `Vector2D`, and `Angle` with chaining signatures and compile-check it.

