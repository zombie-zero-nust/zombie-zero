# Zombie Zero

Zombie Zero is a top-down 2D shooter built on a custom Java game engine with JavaFX.

## Game Metadata

- Name: Zombie Zero
- Levels: 4
- Genre: Top down 2D Shooter

## Storyline

- The player is a part of a special task force to deal with the ongoing zombie pandemic.
- The scientists are trying to get the cure, but cure obtained from normal zombies is very weak.
- They believe that if they can get the blood samples from the first person that was affected (Zombie Zero), a sustainable cure could be extracted.
- Intel indicates that "Zombie Zero" is hiding inside a facility in a small city.
- After many unsuccessful attempts, it is noted the missions with the least number of people are most effective, mainly because of the zombies ability to sense human presence.
- Therefore, only you are sent to get the blood samples from "Zombie Zero".

## Game Levels

- Level 1 takes place in a city.
- The player needs to go through the city fighting zombies, locate and enter the facility.
- A mini boss will appear at the facility entrance in level 1.
- Defeat boss and go inside facility.
- Level 2 is clearing the first facility level to go to next level.
- Level 3 is clearing the second facility level, killing the main boss (Zombie Zero) and returning back to facility entrance.
- Level 4 is reaching a safe place for extraction from the city.

## Features

- Gun Pickup
- Ammo Pickup
- Boss for Each Level
- Arrow on screen guiding the player

## What This Project Contains

- A reusable engine layer (`GameWorld`, `GameScene`, `GameObject`, components, math, logging, resources)
- A game layer (start/menu scenes, level scene, enemy/player systems, collision, pathfinding, assets, audio)
- A consolidated class/file reference in `docs/PROJECT_CLASS_FILE_PURPOSES.md`

## Quick Start

### Prerequisites

- Java (with JavaFX support through Gradle plugin setup)
- Gradle Wrapper (`gradlew.bat` on Windows)

### Run

```powershell
.\gradlew.bat run
```

### Run Without Logs

```powershell
.\gradlew.bat runNoLogs
```

### Run Logger Test Harness

```powershell
.\gradlew.bat runTestLogger
```

### Execute Tests

```powershell
.\gradlew.bat test
```

## Architecture Snapshot

### Engine Core (`src/engine/java`)

- `GameWorld` manages the app window, game loop, and scene switching.
- `GameScene` manages game objects, rendering layers, and scene lifecycle.
- `GameObject` + `Component` provide composition-based gameplay behavior.
- Core services include audio references/manager, math utilities, logging, and resource loading.

### Game Layer (`src/game/java`)

- Entry flow: `Launcher` -> `Main` -> `MainWorld`.
- Scene flow includes start menu, level selection, gameplay level scene, demo scene, and high scores.
- Gameplay systems include player/enemy objects, HUD bars, weapons/ammo, collision, pathfinding, and score/session state.

## Source Layout

- `src/engine/java` - engine framework code
- `src/game/java` - game implementation code
- `src/main/java` - additional/support source set content
- `src/tests` - test and harness code
- `src/engine/resources` and `src/game/resources` - runtime resources
- `docs` - design and technical documentation

## Documentation Index

- `docs/PROJECT_CLASS_FILE_PURPOSES.md` - single-file purpose/use guide for project classes/files
- `PROJECT_INFO.md` - detailed architecture and conventions
- `IMPLEMENTATION_GUIDE.md` - implementation notes and guidance
- `docs/AUDIO_API_ADDITION.md` - audio API extension notes

## Notes

- High scores are persisted in `highscores.txt`.
- Build and packaging behavior is defined in `build.gradle`.
