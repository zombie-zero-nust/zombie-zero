A top-down 2D shooter built on a custom Java game engine with JavaFX.

## Game Metadata

- Name: Zombie Zero
- Genre: Top down 2D Shooter

## Objective

- Clear All Zombies in the city

## Technical Overview

## What This Project Contains

- A reusable engine layer (`GameWorld`, `GameScene`, `GameObject`, components, math, logging, resources)
- A game layer (start/menu scenes, level scene, enemy/player systems, collision, pathfinding, assets, audio)

## Quick Start

### Prerequisites

- Java (with JavaFX support through Gradle plugin setup)
- Gradle Wrapper (`gradlew.bat` on Windows)

### Gradle Configurations

#### Run Configs

```powershell
.\gradlew.bat run               # Runs the game with default logging
.\gradlew.bat runNoLogs         # Runs the game with logging disabled
.\gradlew.bat runTestLogger     # Tests the logger by different types
```

### Package into `exe`, `dmg`, `deb` or `jar` installers

```powershell
.\gradlew.bat packageWindows
.\gradlew.bat packageMac
.\gradlew.bat packageLinux
.\gradlew.bat buildExecutableJar
```

## Architecture

### Engine Core (`src/engine/java`)

- `GameWorld` manages the app window, game loop, and scene switching.
- `GameScene` manages game objects, rendering layers, and scene lifecycle.
- `GameObject` + `Component` provide composition-based gameplay behavior.
- Core services include audio references/manager, math utilities, logging, and resource loading.

### Game Layer (`src/game/java`)

- Entry flow: `Launcher` -> `Main` -> `MainWorld`.
- Scene flow includes start menu, level selection, gameplay level scene, demo scene, and high scores.
- Gameplay systems include player/enemy objects, HUD bars, weapons/ammo, collision, pathfinding, and score/session
  state.

## Source Layout

- `src/engine/java` - engine framework code
- `src/engine/resources` - engine runtime resources


- `src/game/java` - game implementation code
- `src/game/resources` - game runtime resources


- `src/tests` - test and harness code


- `uml/generate_src_uml_assets.py` - UML generation script
- `uml/generated` - generated Mermaid (`.mmd`) and rendered (`.pdf`, `.png`) UML assets

## UML Generation

### Prerequisites

- Python CLI
- Python dependency: `javalang`
- Mermaid CLI for rendering PDF/PNG (`mmdc` or `npx`)

### Install Dependencies

```zsh
python -m pip install javalang
```

### Generate Mermaid Files Only (`.mmd`)

```zsh
python uml/generate_src_uml_assets.py --skip-render
```

### Generate Mermaid + Rendered Assets (`.pdf`, `.png`)

```zsh
python uml/generate_src_uml_assets.py
```

### Custom source/output paths

```zsh
python uml/generate_src_uml_assets.py --source-dir src --output-dir uml/generated
```

## Screenshots

Main Menu
<img width="1263" height="706" alt="image" src="https://github.com/user-attachments/assets/0140ad4c-94a9-4d41-a059-f4ef651792d7" />
Level
<img width="1264" height="710" alt="image" src="https://github.com/user-attachments/assets/87f34202-dcc7-4a5e-ac16-259c9b4e134f" />
High Score
<img width="1261" height="708" alt="image" src="https://github.com/user-attachments/assets/0698ca07-8d7a-4116-80a2-055e832405f0" />


