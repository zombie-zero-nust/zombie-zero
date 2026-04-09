package edu.nust.engine.core.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GameAudioManager
{
    private final Map<String, AudioReference> loadedSounds = new HashMap<>();
    private final Map<AudioReference, MediaPlayer> activeMediaPlayers = new HashMap<>();

    /* LOADER */

    public AudioReference loadSound(String location)
    {
        if (loadedSounds.containsKey(location))
        {
            return loadedSounds.get(location);
        }

        Media media = new Media(location);
        AudioReference ref = new AudioReference(location, media);
        loadedSounds.put(location, ref);
        return ref;
    }

    public @Nullable AudioReference get(String location) { return loadedSounds.get(location); }

    /* PLAY & STOP */

    public void play(AudioReference ref) { if (canPlay(ref)) getMediaPlayer(ref); }

    public void playRepeating(AudioReference ref)
    {
        if (canPlay(ref)) getMediaPlayer(ref).setCycleCount(MediaPlayer.INDEFINITE);
    }

    public void stop(AudioReference ref)
    {
        MediaPlayer player = activeMediaPlayers.remove(ref);
        if (player != null)
        {
            player.stop();
            player.dispose();
        }
    }

    /* HELPERS */

    private static boolean canPlay(AudioReference ref)
    {
        return ref != null && ref.getMedia() != null;
    }

    private @NotNull MediaPlayer getMediaPlayer(AudioReference ref)
    {
        MediaPlayer mediaPlayer = new MediaPlayer(ref.getMedia());
        activeMediaPlayers.put(ref, mediaPlayer);

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.dispose();
            activeMediaPlayers.remove(ref);
        });

        mediaPlayer.play();
        return mediaPlayer;
    }
}
