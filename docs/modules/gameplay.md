# Gameplay Module

## Tree Diagram

```mermaid
flowchart TD
    Gameplay[Gameplay]

    Gameplay --> PlayerBranch[Player Branch]
    PlayerBranch --> Character[Character]
    Character --> Player[Player]
    PlayerBranch --> Weapon[Weapon]
    Weapon --> Bullet[Bullet]

    Gameplay --> EnemyBranch[Enemy Branch]
    EnemyBranch --> BasicEnemy[BasicEnemy]
    EnemyBranch --> EnemyManager[EnemyManager]

    Gameplay --> CollisionBranch[Collision Branch]
    CollisionBranch --> CollisionManager[CollisionManager]
    CollisionBranch --> Level1CollisionMask[Level1CollisionMask]
```

## Usages

- `LevelScene` creates and updates `Player`, `Weapon`, `BasicEnemy`, and `EnemyManager`.
- `Weapon` spawns `Bullet` objects during firing logic.
- `CollisionManager` processes contact between concrete, damaging, and damageable entities.
- `Level1CollisionMask` is used by player walkability checks.
- Enemy behavior and respawn coordination are delegated to `EnemyManager`.

