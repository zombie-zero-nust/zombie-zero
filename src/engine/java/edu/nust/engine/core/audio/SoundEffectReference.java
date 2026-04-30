package edu.nust.engine.core.audio;

import edu.nust.engine.logger.GameLogger;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Use for short, one-shot sounds such as button clicks, footsteps, or hit effects.
/// <br><br>
/// Plays immediately when {@link SoundEffectReference#play()} is called. Individual overlapping instances cannot be
/// stopped independently.
/// <br><br>
/// Do not instantiate directly; use {@link GameAudioManager#loadSoundEffect(String...)}.
/// <br><br>
/// <b>{@code Use .wav instead of .mp3}</b>
///
/// @see MusicTrackReference
/// @see GameAudioManager
public final class SoundEffectReference extends AudioReference
{
    private static final GameLogger LOGGER = GameLogger.getLogger(SoundEffectReference.class);

    private final AudioFormat playbackFormat;
    private final byte[] pcmData;
    private final boolean usable;
    private final List<Clip> activeClips = Collections.synchronizedList(new ArrayList<>());

    private double userVolume = 1.0;
    private double effectiveGlobalVolume = 1.0;

    /// <b>{@code INTERNAL}</b> Use {@link GameAudioManager#loadSoundEffect(String...)} instead.
    SoundEffectReference(URL location)
    {
        super(location);

        AudioFormat format = null;
        byte[] data = null;
        boolean ok = false;

        // manually try loading the audio
        try (BufferedInputStream raw = new BufferedInputStream(location.openStream()); //
             AudioInputStream sourceStream = AudioSystem.getAudioInputStream(raw))
        {
            AudioFormat sourceFormat = sourceStream.getFormat();
            AudioInputStream pcmStream = sourceStream;
            AudioFormat targetFormat = sourceFormat;

            // convert to pcm format
            if (sourceFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
            {
                targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        sourceFormat.getSampleRate(),
                        16,
                        sourceFormat.getChannels(),
                        sourceFormat.getChannels() * 2,
                        sourceFormat.getSampleRate(),
                        false
                );
                pcmStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
            }

            data = pcmStream.readAllBytes();
            format = targetFormat;
            ok = true;
        }
        catch (UnsupportedAudioFileException | IOException | IllegalArgumentException e)
        {
            LOGGER.error(false, "Failed to load SoundEffectReference \"{}\": {}", location, e.getMessage());
            LOGGER.logException(e);
        }

        this.playbackFormat = format;
        this.pcmData = data;
        this.usable = ok;
    }

    /* PLAYBACK */

    /// Plays the sound effect. If the clip is already playing, a new overlapping instance starts.
    public void play() { play(false); }

    /// Plays the sound effect, optionally stopping any currently playing instances first.
    ///
    /// @param stopExisting If {@code true}, stops all currently playing instances before playing
    public void play(boolean stopExisting)
    {
        if (!usable) return;

        if (stopExisting) stopAll();

        Clip clip;
        try
        {
            clip = AudioSystem.getClip();
            clip.open(playbackFormat, pcmData, 0, pcmData.length);
        }
        catch (Exception e)
        {
            LOGGER.warn("Failed to acquire Clip for \"{}\": {}", getFileName(), e.getMessage());
            return;
        }

        applyVolume(clip);
        activeClips.add(clip);

        clip.addLineListener(event -> {
            if (event.getType() != LineEvent.Type.STOP) return;
            activeClips.remove(clip);
            try { clip.close(); }
            catch (Exception ignored) { }
        });

        try { clip.start(); }
        catch (Exception e)
        {
            LOGGER.warn("Failed to start Clip for \"{}\": {}", getFileName(), e.getMessage());
            activeClips.remove(clip);
            try { clip.close(); }
            catch (Exception ignored) { }
        }
    }

    /// Stops <b>all</b> currently playing instances of this sound effect.
    public void stopAll()
    {
        List<Clip> snapshot;
        // synchronized ensures that the block is atomic
        synchronized (activeClips)
        {
            snapshot = new ArrayList<>(activeClips);
            activeClips.clear();
        }
        for (Clip clip : snapshot)
        {
            try
            {
                clip.stop();
                clip.close();
            }
            catch (Exception ignored) { }
        }
    }

    /* STATE */

    /// Returns whether at least one instance of this sound effect is currently playing.
    public boolean isPlaying() { return !activeClips.isEmpty(); }

    /* VOLUME */

    /// Sets the base volume of this sound effect. The effective playback volume is this value multiplied by the
    /// manager's global volume. Applied to subsequent {@link #play()} calls.
    ///
    /// @param volume Volume between {@code 0.0} (silent) and {@code 1.0} (full); clamped automatically
    ///
    /// @see GameAudioManager#setGlobalVolume(double)
    public void setVolume(double volume) { this.userVolume = Math.clamp(volume, 0.0, 1.0); }

    /// Returns the base volume set via {@link #setVolume(double)}, independent of the global volume.
    public double getVolume() { return userVolume; }

    /// Whether this reference loaded successfully and can be played.
    public boolean isUsable() { return usable; }

    /* INTERNAL */

    @Override
    void applyGlobalVolumeToSelf(double globalVolume)
    {
        this.effectiveGlobalVolume = Math.clamp(globalVolume, 0.0, 1.0);
    }

    /// <b>{@code INTERNAL}</b>  Number of {@link Clip} instances
    int getLoadedClipCount() { return activeClips.size(); }

    private void applyVolume(Clip clip)
    {
        double linear = userVolume * effectiveGlobalVolume;

        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = linear <= 0.0001
                       ? gain.getMinimum()
                       : (float) Math.max(gain.getMinimum(), 20.0 * Math.log10(linear));
            gain.setValue(Math.min(dB, gain.getMaximum()));
            return;
        }

        if (clip.isControlSupported(FloatControl.Type.VOLUME))
        {
            FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
            vol.setValue((float) (vol.getMinimum() + (vol.getMaximum() - vol.getMinimum()) * linear));
        }
    }
}
