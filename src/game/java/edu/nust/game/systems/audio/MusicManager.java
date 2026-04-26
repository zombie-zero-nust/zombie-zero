package edu.nust.game.systems.audio;

import edu.nust.engine.core.audio.MusicTrackReference;
import edu.nust.engine.logger.GameLogger;

import java.util.Optional;

public class MusicManager
{
    private static final GameLogger logger = GameLogger.getLogger(MusicManager.class);
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

        // Stop level music if it's playing
        if (currentMusic != null)
        {
            logger.info("Stopping current music track");
            currentMusic.stop();
        }

        currentMusic = musicRef.get();
        logger.info("Starting MainMenu music playback with looping");
        currentMusic.setLooping(true);
        currentMusic.play();
        hasPlaybackStarted = true;
        logger.info("Music play() called successfully");
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
            currentMusic.stop();
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

        // Stop menu music if playing
        if (currentMusic != null)
        {
            logger.info("Stopping current music track");
            currentMusic.stop();
        }

        currentMusic = musicRef.get();
        logger.info("Starting Level scene music playback with looping");
        currentMusic.setLooping(true);
        currentMusic.play();
        hasPlaybackStarted = true;
        logger.info("Level music play() called successfully");
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
}

