# Codebase Tree Diagram (High-Level)

This tree shows the core architecture and scene/gameplay relationships in a top-down format.

```mermaid
flowchart TD
    Root[Codebase]

    Root --> Engine[Engine Core]
    Engine --> GameWorld[GameWorld]
    Engine --> GameScene[GameScene]
    Engine --> GameObject[GameObject]

    Root --> Scenes[Scenes]
    Scenes --> StartScene[StartScene]
    Scenes --> LevelSelectScene[LevelSelectScene]
    Scenes --> LevelScene[LevelScene]
    Scenes --> DemoScene[DemoScene]
    Scenes --> HighScoresScene[HighScoresScene]

    Root --> Gameplay[Gameplay]
    Gameplay --> Character[Character]
    Character --> Player[Player]
    Gameplay --> BasicEnemy[BasicEnemy]
    Gameplay --> Weapon[Weapon]
    Gameplay --> Bullet[Bullet]
    Gameplay --> EnemyManager[EnemyManager]
    Gameplay --> CollisionManager[CollisionManager]
    Gameplay --> Level1CollisionMask[Level1CollisionMask]

    Root --> Data[Data and State]
    Data --> PlayerSession[PlayerSession]
    Data --> Score[Score]
    Data --> HighScoreStorage[HighScoreStorage]

    LevelSelectScene -->|starts| LevelScene
    LevelSelectScene -->|sets name| PlayerSession
    HighScoresScene -->|loads| HighScoreStorage
    LevelScene -->|updates| Score
    LevelScene -->|saves| HighScoreStorage
```

## Scene Tree (UI Navigation)

```mermaid
flowchart TD
    Start[StartScene]

    Start --> Demo[DemoScene]
    Start --> LevelSelect[LevelSelectScene]
    Start --> HighScores[HighScoresScene]

    LevelSelect --> Level[LevelScene]

    Demo --> Back1[Back to StartScene]
    Level --> Back2[Back to StartScene]
    HighScores --> Back3[Back to StartScene]
    LevelSelect --> Back4[Back to StartScene]
```

## Notes

- This version is tree-first for quick understanding.
- If you want, I can also add a separate detailed tree only for `LevelScene` combat systems.

## Module Files

- Engine Core: `docs/modules/engine-core.md`
- Scenes: `docs/modules/scenes.md`
- Gameplay: `docs/modules/gameplay.md`
- Data and State: `docs/modules/data-state.md`
- Index: `docs/modules/README.md`



