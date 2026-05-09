### Engine

1. GameWorld
2. GameScene
    - lifecycle and layering
    - GameCamera (position, zoom, shake)
    - Render order and culling
    - Debug rendering in GameScene
3. GameObject base entity
4. Component system
    - SpriteRenderer
    - Shape renderers
        - BoxRenderer
        - CircleRenderer
    - WorldBoundsProvider
5. Rendering pipeline (world + UI layers)
6. Input routing
7. DevConsole
    - Command System
8. Resources loader (Resources + GameURLs + URLUtils)
9. Math Library
    - Vector2D
    - Angle
    - Rectangle
    - TimeSpan
10. GameLogger (levels + formatting)
    - Colored Formatted Logging
    - Levels
    - Logging to console
    - Logging to File
11. Audio Management
    - Audio reference system
        - SoundEffectReference
        - MusicTrackReference
    - GameAudioManager (cache + volume)

### Systems & Deployment

1. AssetManager cache
2. CollisionManager
3. HitBox component
4. PathFinder (A* grid)
    - MapNodeSetter (solid grid build)
5. Weapon system (Weapon + Ammo + Bullet)
6. HUD bars (Bar + HealthBar + AmmoBar)
7. ScoreDisplayController (sprite digits)
8. Audio Management
    - MusicManager
    - Audios registry
9. Gradle packaging
    - packageWindows
    - packageLinux
    - packageMac
    - buildExecutableJar
10. Build/run tasks
    - run
    - runNoLogs
    - runTestLogger
11. UML generation pipeline
    - Generate UML using Python
    - Generate Separate for each file
    - 

### Scenes

1. StartScene
2. LevelSelectScene
3. LevelScene (core gameplay)
    - CollisionMask
    - SpawnPoints generation
    - Background Objects + placements file
    - StaticObjectFactory + SerializablePlacement
4. HighScoresScene
    - HighScoreStorage
    - PlayerSession
    - Score system