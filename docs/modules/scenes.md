# Scenes Module

## Tree Diagram

```mermaid
flowchart TD
    Scenes[Scenes]

    Scenes --> StartScene[StartScene]
    Scenes --> LevelSelectScene[LevelSelectScene]
    Scenes --> LevelScene[LevelScene]
    Scenes --> DemoScene[DemoScene]
    Scenes --> HighScoresScene[HighScoresScene]

    StartScene --> StartNav[Start Menu Navigation]
    LevelSelectScene --> NameInput[Player Name Input + Validation]
    LevelScene --> RuntimeHUD[HUD + Pause Overlay]
    DemoScene --> DemoControls[Pause + Camera Controls]
    HighScoresScene --> ScoreView[Leaderboard Rendering]
```

## Usages

- `StartScene` is the main entry point and routes to gameplay, level select, and highscores.
- `LevelSelectScene` sets player name via `PlayerSession` and opens `LevelScene`.
- `LevelScene` handles core gameplay, camera tracking, score saving, and pause/game-over overlays.
- `DemoScene` acts as a sandbox scene with movement and camera controls.
- `HighScoresScene` reads score data from `HighScoreStorage` and renders leaderboard rows.

## Scene Navigation Tree

```mermaid
flowchart TD
    Start[StartScene]

    Start --> Demo[DemoScene]
    Start --> LevelSelect[LevelSelectScene]
    Start --> HighScores[HighScoresScene]

    LevelSelect --> Level[LevelScene]

    Demo --> BackStart1[Back to StartScene]
    Level --> BackStart2[Back to StartScene]
    HighScores --> BackStart3[Back to StartScene]
    LevelSelect --> BackStart4[Back to StartScene]
```

