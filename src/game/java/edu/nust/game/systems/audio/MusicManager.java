package edu.nust.game.systems.audio;

import edu.nust.engine.core.audio.MusicTrackReference;
import edu.nust.engine.logger.GameLogger;
import javafx.util.Duration;

import java.util.Optional;

public class MusicManager
{
    private static final GameLogger logger = GameLogger.getLogger(MusicManager.class);
    private static final double MENU_MUSIC_VOLUME = 1.0;
    private static final double LEVEL_MUSIC_VOLUME = 0.4;
    private static final Duration MUSIC_FADE_DURATION = Duration.seconds(1);
    private static MusicTrackReference currentMusic;
    private static boolean hasPlaybackStarted = false;

    /**
     * Plays the main menu music with looping enabled.
     */
    public static void playMenuMusic()
    {
        logger.info("playMenuMusic() called");
        Optional<MusicTrackReference> musicRef = Audios.mainMenuMusicRef();
        if (musicRef.isEmpty())
        {
            logger.warn("MainMenu music reference is empty!");
            return;
        }

        transitionToMusic(musicRef.get(), MENU_MUSIC_VOLUME, "MainMenu");
    }

    /**
     * Ensures menu music is playing. Safe to call even if audio manager isn't ready yet.
     */
    public static void ensureMenuMusicPlaying()
    {
        logger.info("ensureMenuMusicPlaying() called - hasPlaybackStarted = {}", hasPlaybackStarted);
        if (hasPlaybackStarted && currentMusic != null)
        {
            logger.info("Menu music already started");
            return;
        }

        playMenuMusic();
    }

    /**
     * Stops the currently playing music.
     */
    public static void stopMusic()
    {
        logger.info("stopMusic() called");
        if (currentMusic != null)
        {
            currentMusic.setFadeOnStop(true, MUSIC_FADE_DURATION);
            currentMusic.stop();
            currentMusic = null;
            hasPlaybackStarted = false;
            logger.info("Music stopped");
        }
        else
        {
            logger.warn("Attempted to stop music but currentMusic is null");
        }
    }

    /**
     * Plays the level scene music with looping enabled.
     */
    public static void playLevelMusic()
    {
        logger.info("playLevelMusic() called");
        Optional<MusicTrackReference> musicRef = Audios.levelSceneMusicRef();
        if (musicRef.isEmpty())
        {
            logger.warn("Level scene music reference is empty!");
            return;
        }

        transitionToMusic(musicRef.get(), LEVEL_MUSIC_VOLUME, "Level scene");
    }

    /**
     * Stops the menu music specifically without stopping other music.
     */
    private static void stopMenuMusic()
    {
        logger.info("stopMenuMusic() called");
        hasPlaybackStarted = false;
    }

    /**
     * Resumes menu music playback after level ends.
     */
    public static void resumeMusic()
    {
        logger.info("resumeMusic() called - resuming menu music");
        playMenuMusic();
    }

    /**
     * Returns whether menu music is currently playing or has been started.
     */
    public static boolean isMenuMusicPlaying()
    {
        return hasPlaybackStarted && currentMusic != null;
    }

    private static void transitionToMusic(MusicTrackReference nextMusic, double volume, String label)
    {
        if (currentMusic != null)
        {
            logger.info("Fading out current music track");
            currentMusic.setFadeOnStop(true, MUSIC_FADE_DURATION);
            currentMusic.stop();
        }

        currentMusic = nextMusic;
        logger.info("Starting {} music playback with looping and fade-in", label);
        currentMusic.setLooping(true);
        currentMusic.setVolume(volume);
        currentMusic.fadeIn(MUSIC_FADE_DURATION);
        hasPlaybackStarted = true;
        logger.info("{} music fade-in started successfully", label);
    }
}

