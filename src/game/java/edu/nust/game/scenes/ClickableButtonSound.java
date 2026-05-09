package edu.nust.game.scenes;

import edu.nust.engine.core.audio.SoundEffectReference;
import edu.nust.game.systems.audio.Audios;

public interface ClickableButtonSound
{
    default void playButtonClickSound()
    {
        Audios.clickNeutralRef().ifPresent(SoundEffectReference::play);
    }
}
