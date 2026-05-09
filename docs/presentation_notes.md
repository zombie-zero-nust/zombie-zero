# Zombie Zero - Observations, Advice, and Extra Info

## Presentation flow suggestions
- Start with the two-layer architecture (engine vs game) before the feature index slide.
- Use the feature index slide as a menu so the audience can pick deep dives.
- End with OOP concepts to connect implementation to software design.

## Suggested deep-dive picks (high impact)
- `GameWorld` + `GameScene` (engine backbone)
- Component system + `Transform` (composition core)
- Debug rendering in `GameScene` (developer tooling)
- Audio reference system + manager (clean separation + caching)
- Level generation pipeline (placements + spawn rules)
- UML generation pipeline (tooling and documentation)

## Demo tips
- Use dev console commands in `LevelScene` to trigger win/lose states and visualize debug overlays.
- Highlight the camera shake on hit (enemy and player) to show feedback systems.
- Show the high score UI to demonstrate file persistence.

## Build and packaging notes
- Packaging tasks are OS-specific (`packageMac`, `packageWindows`, `packageLinux`). Run them only on the target OS.
- `runNoLogs` swaps to `logback-off.xml` for quiet demos.
- `buildExecutableJar` creates a fat JAR in `build/libs/*-all.jar`.

## UML pipeline notes
- UML script requires Python + `javalang`.
- Rendering requires Mermaid CLI (`mmdc` or `npx @mermaid-js/mermaid-cli`).
- `.mmd` output lives in `uml/generated/mmd`.

## Observations about the codebase
- Engine math utilities are self-contained and reused across gameplay (vector, angle, rect, time).
- Rendering and UI are split cleanly: world uses `Canvas`, UI uses JavaFX nodes.
- Audio uses a clear reference model with caching and global volume control.
- Level decoration is data-driven via `objects_placements.txt` (easy to iterate without code changes).
- Pathfinding grid size is fixed to 2 units, which is precise but can be CPU-heavy for large maps.

## Possible enhancements (optional to mention)
- Add unit tests for math utilities and collision helpers.
- Move high score file path to a user data directory for OS compatibility.
- Add a lightweight profiler overlay (FPS is already available via dev console).

## Slide framing tips
- For each feature slide, keep the "Working" section to 1-2 sentences, then show 2-3 technical bullets.
- Keep file references on speaker notes or the bottom of the slide to avoid clutter.
- Use icons to distinguish engine features vs game features.


