GameAudioManager

1. `public boolean isSoundEffectLoaded(String filename)`
    - Checks if a sound effect is present in the cache.

2. `public boolean isMusicTrackLoaded(String filename)`
    - Checks if a music track is present in the cache.

3. `public void unloadSoundEffect(String filename)`
    - Removes and releases a sound effect from cache.

4. `public void unloadMusicTrack(String filename)`
    - Removes and disposes of a music track.

5. `public void unloadAllSoundEffects()`
    - Clears and releases all sound effects.

6. `public void unloadAllMusicTracks()`
   - Clears and disposes all music tracks.

7. `public void unloadAll()`
   - Unloads all audio resources and clears caches.

8. `public List<String> listLoadedSoundEffectNames()`
   - Returns filenames of loaded sound effects.

9. `public List<String> listLoadedMusicTrackNames()`
   - Returns filenames of loaded music tracks.

10. `public Optional<SoundEffectReference> tryGetSoundEffect(String filename)`
    - Safe optional getter wrapper over `getSoundEffectByName`.

11. `public Optional<MusicTrackReference> tryGetMusicTrack(String filename)`
    - Safe optional getter for music tracks.

12. `public SoundEffectReference getOrLoadSoundEffect(String... relativePath)`
    - Returns cached or loads and returns the sound effect.

13. `public MusicTrackReference getOrLoadMusicTrack(String... relativePath)`
    - Returns cached or loads music track.

14. `public int getLoadedSoundEffectCount()`
    - Returns number of cached SFX.

15. `public int getLoadedMusicTrackCount()`
    - Returns number of cached music tracks.

16. `public void setGlobalVolume(double volume)`
    - Sets a global master volume between 0.0 and 1.0.

17. `public double getGlobalVolume()`
    - Returns current master volume.

18. `public void muteAll()`
    - Mutes all audio (applies to music & SFX).

19. `public void unmuteAll()`
    - Reverses mute.

20. `public boolean isMuted()`
    - Returns mute state.

21. `public void fadeOutMusic(MusicTrackReference ref, Duration duration)`
    - Smoothly fades out a specific track over the duration.

22. `public void fadeInMusic(MusicTrackReference ref, Duration duration)`
    - Smoothly fades in a track.

23. `public void stopAllMusic()`
    - Stops playback of all music tracks.

24. `public void stopAllSoundEffects()`
    - Stops all sound effects currently playing.

AudioReference — 15 proposals

1. `public String getPath()`
    - Returns the asset relative path string for the resource (if resolvable); falls back to URL path.

2. `public boolean equalsLocation(URL other)`
    - Compares underlying URL/location equality.

3. `public String getExtension()`
    - Returns file extension (eg. "wav", "mp3").

4. `public boolean isSame(AudioReference other)`
   - Logical equivalence based on resolved resource path.

5. `public void addListener(ReferenceListener listener)`
   - Adds a listener for reference lifecycle events (e.g., unloaded).

6. `public void setPriority(int priority)`
   - Set priority used by the manager for resource decisions.

7. `public int getPriority()`
   - Get priority.

8. `public void fadeIn(Duration duration)`
   - Fades in the clip over the specified duration.

9. `public void fadeOut(Duration duration)`
   - Fades out the clip over the specified duration.

10. `public void crossfadeTo(T other, Duration duration)` (where T is the subclass type)
    - Crossfades this clip into `other` over `duration`.

SoundEffectReference — 20 proposals

1. `public void setPitch(double pitch)`
    - Sets playback pitch multiplier (if supported by `AudioClip`).

2. `public double getPitch()`
    - Returns current pitch value.

3. `public void setBalance(double balance)`
    - Sets stereo balance [-1.0..1.0].

4. `public double getBalance()`
    - Returns balance.

5. `public void setVolume(double volume)`
    - Sets clip base volume.

6. `public double getVolume()`
    - Returns clip base volume.

7. `public void playAt(double x, double y, double z)`
    - Plays with simple 3D positional attenuation (engine-provided attenuation model).

8. `public void setSpatialEnabled(boolean enabled)`
    - Toggle spatialization for this effect.

9. `public boolean isSpatialEnabled()`
    - Returns spatialization state.

10. `public void schedulePlay(Duration delay)`
    - Schedules play to occur after `delay`.

11. `public void scheduleStop(Duration delay)`
    - Schedules stop after delay.

12. `public void setCooldown(Duration cooldown)`
    - Sets a minimum time between successive plays to prevent spam.

13. `public boolean canPlay()`
    - Returns whether `play()` respects cooldown and resource limits.

14. `public void setMaxConcurrentPlays(int max)`
    - Limits how many overlapping instances can exist concurrently.

15. `public int getMaxConcurrentPlays()`
    - Returns max concurrent plays.

16. `public void attachToEntity(EntityId id)`
    - Attach SFX to a game entity for positional updates.

17. `public void detach()`
    - Detach from any entity.

MusicTrackReference — 25 proposals

1. `public void setPlaybackRate(double rate)`
    - Sets playback speed/rate for the music track.

2. `public double getPlaybackRate()`
    - Returns playback rate.

3. `public void setLooping(boolean loop)`
    - Public loop control.

4. `public boolean isLooping()`
    - Returns loop state.

5. `public Duration getPlayheadPosition()`
    - Returns current play head position.

6. `public void seek(Duration position)`
    - Seeks to a given position.

7. `public Duration getDuration()`
    - Returns total duration of the media.

8. `public void setOnEndOfMedia(Runnable callback)`
    - Registers a callback to run when track ends.

9. `public void setOnReady(Runnable callback)`
    - Called when `MediaPlayer` becomes ready (metadata loaded).

10. `public void setOnError(Consumer<Throwable> callback)`
    - Called on playback or loading errors.

11. `public void setVolume(double volume)`
    - Set per-track volume.

12. `public double getVolume()`
    - Get per-track volume.

13. `public void fadeToVolume(double newVolume, Duration duration)`
    - Smoothly transition the track volume.

14. `public void setPan(double pan)`
    - Set stereo pan for the track.

15. `public double getPan()`
    - Returns current pan.

16. `public void setLoopBetween(Duration start, Duration end)`
    - Set loop points inside the track for repeated playback between `start` and `end`.

17. `public void clearLoopPoints()`
    - Remove internal loop points.

18. `public void setFadeOnStop(boolean enabled, Duration duration)`
    - Configure automatic fade when stopping.

19. `public boolean isPlaying()`
    - Returns playback state

20. `public void setBufferingStrategy(BufferingStrategy strategy)`
    - Adjust media buffering strategy for optimization (enum `BufferingStrategy`).
