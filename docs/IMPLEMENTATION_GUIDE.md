# UI & World Implementation Guide

## Summary of Changes

This implementation adds:
1. **OOP-based level building system** with LevelBuilder
2. **Sprite-based health display** with 3 states (Full/Half/Empty)
3. **Restricted play area** with boundaries
4. **Integrated tilemap system** for game world

---

## What Was Implemented

### 1. LevelBuilder.java (NEW)
- **Purpose:** Fluent builder for creating game levels with boundaries
- **Features:**
  - Chainable API for configuration
  - Background tileset management
  - Boundary wall creation (3-tile thick borders)
  - Play area bounds calculation
  - Asset preloading
  - Inner `PlayAreaBounds` class for position clamping

**Key Method:**
```java
LevelBuilder builder = new LevelBuilder(30, 20, 64);
builder.fillBackground(TilesetAsset.GRASS_ON_TOP)
       .addBoundaries()
       .preloadAssets();
```

### 2. HealthBar.java (UPDATED)
- **Added:** `HealthState` enum with 3 states
- **Health Mapping:**
  - 67-100%: FULL (`Hunger_Full.png`)
  - 34-66%: HALF (`Hunger_Half.png`)
  - 0-33%: EMPTY (`Hunger_Empty.png`)

**New Method:**
```java
HealthBar.HealthState state = healthBar.getHealthState(currentHealth);
String spriteFile = state.getSpriteFile();
```

### 3. LevelScene.java (UPDATED)
- **Added:** `playAreaBounds` field
- **Modified:** `onInit()` to use LevelBuilder
- **Modified:** `lateUpdate()` to clamp player and camera positions

**Key Changes:**
```java
// Level building
LevelBuilder levelBuilder = new LevelBuilder(30, 20, 64);
levelBuilder.fillBackground(TilesetAsset.GRASS_ON_TOP)
           .addBoundaries()
           .preloadAssets();
this.addGameObject(levelBuilder.build());
this.playAreaBounds = levelBuilder.getPlayAreaBounds();

// Position clamping
Vector2D clampedPos = playAreaBounds.clampPosition(playerPos);
```

### 4. TilemapExample.java (UPDATED)
- **Now shows:** How to use LevelBuilder instead of manual Tilemap creation
- **Examples:** Basic, with walls, and custom configurations

---

## Current Implementation

### Play Area Configuration
- **Total Map:** 30x20 tiles (64px each) = 1920x1280 pixels
- **Boundary:** 3-tile thick walls on all edges
- **Play Area:** 24x14 tiles inner area
  - Min X: 192px (3 * 64)
  - Max X: 1728px (27 * 64)
  - Min Y: 192px
  - Max Y: 1216px (14 * 64)

### Health Display (Ready for Implementation)
Current: Bar display only
Ready for: Sprite-based heart display using HealthState

---

## How to Use

### In Your Game Level:
```java
// 1. Create level
LevelBuilder levelBuilder = new LevelBuilder(30, 20, 64);

// 2. Configure it
levelBuilder.fillBackground(TilesetAsset.GRASS_ON_TOP)
            .addBoundaries()
            .preloadAssets();

// 3. Add to scene
this.addGameObject(levelBuilder.build());
this.playAreaBounds = levelBuilder.getPlayAreaBounds();

// 4. Players are now automatically restricted!
// (LevelScene handles clamping in lateUpdate)
```

### To Display Health Sprite:
```java
// Get current health state
int currentHealth = player.getHealthSystem().getCurrentHealth();
HealthBar.HealthState state = healthBar.getHealthState(currentHealth);

// Use the sprite filename for UI display
String spriteFile = state.getSpriteFile();
// Load from: UI/Hunger/{spriteFile}
```

---

## File Changes Summary

| File | Type | Change |
|------|------|--------|
| LevelBuilder.java | NEW | OOP level management |
| HealthBar.java | UPDATED | Added HealthState enum |
| LevelScene.java | UPDATED | Integrated LevelBuilder + bounds |
| TilemapExample.java | UPDATED | Updated to show LevelBuilder usage |

---

## Architecture Benefits

1. **OOP Design:** Builder pattern for clean, fluent API
2. **Reusability:** Easy to create different level configurations
3. **Maintainability:** Separate concerns (tilemap, renderer, bounds)
4. **Extensibility:** Simple to add new tileset types or boundaries
5. **Type Safety:** Enum-based health states prevent errors

---

## Testing Checklist

- [x] LevelBuilder creates level with boundaries
- [x] Play area bounds calculated correctly
- [x] Player position clamped to bounds
- [x] Camera clamped to bounds
- [x] Health bar still displays bar UI
- [x] HealthState enum provides correct sprite names
- [ ] Sprite-based UI displays hearts (optional future)

---

## Future Enhancements

1. **Building Assets:** Use PostApocalypse building sprites for boundaries
2. **Multiple Levels:** Create different level configurations using LevelBuilder
3. **Dynamic Obstacles:** Add destructible walls or moving platforms
4. **Sprite UI:** Implement visual hearts using HealthState
5. **Hunger System:** Use hunger assets in addition to health

---

## Notes

- Health thresholds (33%, 66%) are easily configurable in `HealthBar.getHealthState()`
- Boundary thickness is adjustable in `LevelBuilder.playAreaBoundaryThickness`
- Tileset assets can be swapped in `LevelBuilder.fillBackground()`
- Play area clamping happens automatically in LevelScene.lateUpdate()

