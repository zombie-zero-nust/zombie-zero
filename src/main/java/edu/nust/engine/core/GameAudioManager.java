package edu.nust.engine.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

public class GameAudioManager
{
    private final Map<String, Media> soundCache = new HashMap<>();

    public void play(String location)
    {
        Media media = getMedia(location);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(mediaPlayer::dispose);
        mediaPlayer.play();
    }

    public void playRepeating(String location)
    {
        Media media = getMedia(location);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setOnEndOfMedia(mediaPlayer::dispose);
        mediaPlayer.play();
    }

    /* HELPERS */

    private Media getMedia(String location)
    {
        if (soundCache.containsKey(location))
        {
            return soundCache.get(location);
        }
        else
        {
            Media media = new Media(location);
            soundCache.put(location, media);
            return media;
        }
    }
}
