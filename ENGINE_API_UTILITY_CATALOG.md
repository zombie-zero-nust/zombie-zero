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

#### Rotation and orientation

#### Direction vectors and utility queries

#### Anchor and bounds helpers


### 2) `Vector2D` (`src/main/java/edu/nust/engine/math/Vector2D.java`)

#### In-place mutators (fluent)

#### Query and algebra methods

#### Static helpers


### 3) `Angle` (`src/main/java/edu/nust/engine/math/Angle.java`)


### 6) `TimeSpan` (`src/main/java/edu/nust/engine/math/TimeSpan.java`)


### 7) `Component` (`src/main/java/edu/nust/engine/core/Component.java`)


### 8) `GameObject` (`src/main/java/edu/nust/engine/core/GameObject.java`)


### 9) `GameScene` (`src/main/java/edu/nust/engine/core/GameScene.java`)


### 10) `GameWindow` (`src/main/java/edu/nust/engine/core/GameWindow.java`)


### 11) `Resources` (`src/main/java/edu/nust/engine/resources/Resources.java`)


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

