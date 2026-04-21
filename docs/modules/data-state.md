# Data and State Module

## Tree Diagram

```mermaid
flowchart TD
    Data[Data and State]

    Data --> RuntimeState[Runtime State]
    RuntimeState --> PlayerSession[PlayerSession]
    RuntimeState --> Score[Score]

    Data --> Persistence[Persistence]
    Persistence --> HighScoreStorage[HighScoreStorage]
    HighScoreStorage --> HighScoreEntry[HighScoreEntry]
```

## Usages

- `PlayerSession` stores current player name set in `LevelSelectScene`.
- `Score` is updated each frame in `LevelScene`.
- `HighScoreStorage` persists leaderboard entries when game over occurs.
- `HighScoresScene` reads top entries from `HighScoreStorage` for UI rendering.
- `LevelScene` writes score snapshots to `HighScoreStorage` with timestamp metadata.

