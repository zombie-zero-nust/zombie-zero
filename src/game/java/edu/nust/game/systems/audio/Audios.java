package edu.nust.game.systems.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.audio.GameAudioManager;
import edu.nust.engine.core.audio.MusicTrackReference;
import edu.nust.engine.core.audio.SoundEffectReference;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Audios
{
    private static GameAudioManager manager;

    private static final String[] TEST_SOUNDS = {
            "testSound.wav"
    };

    private static final String[] PLAYER_FOOTSTEPS = {
            "player/footsteps/footstep_1.wav", "player/footsteps/footstep_2.wav", "player/footsteps/footstep_3.wav"
    };

    private static final String[] PLAYER_SHOOT_GUN = {
            "player/shoot/gun/gun_1.wav", "player/shoot/gun/gun_2.wav"
    };

    private static final String[] PLAYER_SHOOT_IMPACT_ENVIRONMENT = {
            "player/shoot/impact_env/impact_1.wav", "player/shoot/impact_env/impact_2.wav"
    };

    private static final String[] PLAYER_SHOOT_IMPACT_ZOMBIE = {
            "player/shoot/impact_zombie/impact_1.wav", "player/shoot/impact_zombie/impact_2.wav"
    };

    private static final String[] UI_NEUTRAL = {
            "ui/click_neutral.wav"
    };

    private static final String[] ZOMBIE_BASIC_ATTACK = {
            "zombie/basic/attack/attack_1.wav", "zombie/basic/attack/attack_2.wav"
    };

    private static final String[] ZOMBIE_BASIC_HURT = {
            "zombie/basic/hurt/hurt_1.wav", "zombie/basic/hurt/hurt_2.wav"
    };

    private static final String[] ZOMBIE_BOSS_ATTACK = {
            "zombie/boss/attack/attack_1.wav", "zombie/boss/attack/attack_2.wav", "zombie/boss/attack/attack_3.wav"
    };

    private static final String[] ZOMBIE_BOSS_GROWL = {
            "zombie/boss/growl/growl_1.wav"
    };

    private static final String[] ZOMBIE_BOSS_HURT = {
            "zombie/boss/hurt/hurt_1.wav", "zombie/boss/hurt/hurt_2.wav"
    };

    private static final String[] SOUND_EFFECTS = concat(
            TEST_SOUNDS,
            PLAYER_FOOTSTEPS,
            PLAYER_SHOOT_GUN,
            PLAYER_SHOOT_IMPACT_ENVIRONMENT,
            PLAYER_SHOOT_IMPACT_ZOMBIE,
            UI_NEUTRAL,
            ZOMBIE_BASIC_ATTACK,
            ZOMBIE_BASIC_HURT,
            ZOMBIE_BOSS_ATTACK,
            ZOMBIE_BOSS_GROWL,
            ZOMBIE_BOSS_HURT
    );

    private static final String[] MUSIC_TRACKS = {
            "music/background.wav", "music/MainMenu.wav", "music/levelScene.wav"
    };

    /* REFERENCES */

    public static Optional<SoundEffectReference> testSoundRef() { return soundEffectRef("testSound.wav"); }

    public static Optional<SoundEffectReference> clickNeutralRef() { return soundEffectRef("ui/click_neutral.wav"); }

    public static Optional<SoundEffectReference> randomPlayerFootstepRef()
    {
        return randomSoundEffectRef(PLAYER_FOOTSTEPS);
    }

    public static Optional<SoundEffectReference> randomPlayerGunShotRef()
    {
        return randomSoundEffectRef(PLAYER_SHOOT_GUN);
    }

    public static Optional<SoundEffectReference> randomPlayerImpactEnvironmentRef()
    {
        return randomSoundEffectRef(PLAYER_SHOOT_IMPACT_ENVIRONMENT);
    }

    public static Optional<SoundEffectReference> randomPlayerImpactZombieRef()
    {
        return randomSoundEffectRef(PLAYER_SHOOT_IMPACT_ZOMBIE);
    }

    public static Optional<SoundEffectReference> randomZombieBasicAttackRef()
    {
        return randomSoundEffectRef(ZOMBIE_BASIC_ATTACK);
    }

    public static Optional<SoundEffectReference> randomZombieBasicHurtRef()
    {
        return randomSoundEffectRef(ZOMBIE_BASIC_HURT);
    }

    public static Optional<SoundEffectReference> randomZombieBossAttackRef()
    {
        return randomSoundEffectRef(ZOMBIE_BOSS_ATTACK);
    }

    public static Optional<SoundEffectReference> randomZombieBossGrowlRef()
    {
        return randomSoundEffectRef(ZOMBIE_BOSS_GROWL);
    }

    public static Optional<SoundEffectReference> randomZombieBossHurtRef()
    {
        return randomSoundEffectRef(ZOMBIE_BOSS_HURT);
    }

    public static Optional<MusicTrackReference> backgroundMusicRef() { return musicTrackRef("music/background.wav"); }

    public static Optional<MusicTrackReference> mainMenuMusicRef() { return musicTrackRef("music/MainMenu.wav"); }

    public static Optional<MusicTrackReference> levelSceneMusicRef() { return musicTrackRef("music/levelScene.wav"); }

    /* SETUP */

    public static void setManager(GameWorld world) { Audios.manager = world.getAudioManager(); }

    public static void forEachSoundEffect(Consumer<String> action)
    {
        for (String audio : SOUND_EFFECTS)
            action.accept(audio);
    }

    public static void forEachMusicTrack(Consumer<String> action)
    {
        for (String audio : MUSIC_TRACKS)
            action.accept(audio);
    }

    private static Optional<SoundEffectReference> soundEffectRef(String relativePath)
    {
        if (manager == null) return Optional.empty();
        return manager.loadSoundEffect(relativePath).filter(ref -> matchesPath(ref.getPath(), relativePath));
    }

    private static Optional<MusicTrackReference> musicTrackRef(String relativePath)
    {
        if (manager == null) return Optional.empty();
        return manager.loadMusicTrack(relativePath).filter(ref -> matchesPath(ref.getPath(), relativePath));
    }

    private static Optional<SoundEffectReference> randomSoundEffectRef(String[] relativePaths)
    {
        return soundEffectRef(relativePaths[ThreadLocalRandom.current().nextInt(relativePaths.length)]);
    }

    private static boolean matchesPath(String loadedPath, String relativePath)
    {
        return loadedPath.replace('\\', '/').endsWith('/' + relativePath);
    }

    private static String[] concat(String[]... groups)
    {
        int size = 0;
        for (String[] group : groups)
            size += group.length;

        String[] combined = new String[size];
        int index = 0;
        for (String[] group : groups)
        {
            System.arraycopy(group, 0, combined, index, group.length);
            index += group.length;
        }
        return combined;
    }
}