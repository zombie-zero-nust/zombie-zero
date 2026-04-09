package edu.nust.engine.core.audio;

import javafx.scene.media.AudioClip;

import java.net.URL;

/// Use for short, one-shot sounds such as button clicks, footsteps, or hit effects.
/// <br><br>
/// Plays immediately when {@link SoundEffectReference#play()} is called. Multiple instances of the same clip can play
/// simultaneously. But cannot be stopped individually.
/// <br><br>
/// Do not instantiate directly; use {@link GameAudioManager#loadSoundEffect(String...)} instead.
/// <br><br>
/// **`Use .wav instead of .mp3`**
///
/// @see MusicTrackReference
/// @see GameAudioManager
public final class SoundEffectReference extends AudioReference
{
    private final AudioClip clip;

    /// **`INTERNAL`**
    SoundEffectReference(URL location, AudioClip clip)
    {
        super(location);
        this.clip = clip;
    }

    /* GETTERS */

    /// **`INTERNAL`** Returns the {@link AudioClip}.
    AudioClip getClip() { return clip; }

    /* PLAYBACK */

    /// Plays the sound effect. If the clip is already playing, a new overlapping instance starts.
    public void play() { play(false); }

    /// Plays the sound effect, stopping any currently playing instances first.
    ///
    /// @param stopExisting If {@code true}, stops all currently playing instances before playing
    public void play(boolean stopExisting)
    {
        if (stopExisting) stopAll();
        clip.play();
    }

    /// Stops **`all`** currently playing instances of this sound effect.
    public void stopAll() { clip.stop(); }

    /* STATE */

    public boolean isPlaying() { return clip.isPlaying(); }
}
