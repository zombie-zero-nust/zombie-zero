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


### 13) `CircleRenderer` (`src/main/java/edu/nust/engine/core/components/renderers/CircleRenderer.java`)


### 14) `SpriteRenderer` (`src/main/java/edu/nust/engine/core/components/renderers/SpriteRenderer.java`)

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
