package edu.nust.engine.core.audio;

import javafx.scene.media.AudioClip;

import java.net.URL;

/// Plays immediately, use for small clips such as Button Clicks etc.
/// <br>
/// **`Use .wav instead of .mp3`**
public final class SoundEffectReference extends AudioReference
{
    private final AudioClip clip;

    SoundEffectReference(URL location, AudioClip clip)
    {
        super(location);
        this.clip = clip;
    }

    /* PLAYERS */

    public void play() { clip.play(); }

    public void play(boolean stopExisting)
    {
        if (stopExisting) stop();
        play();
    }

    public void stop() { clip.stop(); }
}
