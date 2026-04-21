# Engine Core Module

## Tree Diagram

```mermaid
flowchart TD
    Engine[Engine Core]

    Engine --> GameWorld[GameWorld]
    Engine --> GameScene[GameScene]
    Engine --> GameObject[GameObject]
    Engine --> GameCamera[GameCamera]
    Engine --> Component[Component]

    GameWorld --> Loop[Game Loop / AnimationTimer]
    GameWorld --> Stage[Stage + Root Scene]

    GameScene --> UILayer[UI Layer FXML]
    GameScene --> WorldLayer[World Canvas Layer]
    GameScene --> CameraAccess[getWorldCamera()]

    GameObject --> Transform[Transform Component]
    GameObject --> Tags[Tag System]
    GameObject --> RenderUpdate[Render + Update Lifecycle]
```

## Usages

- `GameWorld` owns and switches `GameScene` instances.
- `GameScene` owns and updates `GameObject` collections.
- Game scenes in `src/game/java/edu/nust/game/scenes` inherit from `GameScene`.
- Gameplay entities in `src/game/java/edu/nust/game/scenes/**/gameobjects` inherit from `GameObject`.
- Camera operations (`zoom`, `position`) are accessed through `GameScene.getWorldCamera()`.

