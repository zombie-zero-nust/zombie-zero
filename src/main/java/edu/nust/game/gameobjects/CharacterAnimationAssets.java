package edu.nust.game.gameobjects;

/**
 * Enum containing all character animation asset filenames.
 * Organized by animation type and direction.
 */
public enum CharacterAnimationAssets
{
    // Idle animations (no hands)
    IDLE_DOWN("Character_down_idle_no-hands-Sheet6.png"),
    IDLE_UP("Character_up_idle_no-hands-Sheet6.png"),
    IDLE_LEFT("Character_side-left_idle_no-hands-Sheet6.png"),
    IDLE_RIGHT("Character_side_idle_no-hands-Sheet6.png"),

    // Run animations (no hands)
    RUN_DOWN("Character_down_run_no-hands-Sheet6.png"),
    RUN_UP("Character_up_run_no-hands-Sheet6.png"),
    RUN_LEFT("Character_side-left_run_no-hands-Sheet6.png"),
    RUN_RIGHT("Character_side_run_no-hands-Sheet6.png"),

    // Hands idle animations
    HANDS_IDLE_DOWN("Hands_down_idle-Sheet6.png"),
    HANDS_IDLE_UP("Hands_Up_idle-Sheet6.png"),
    HANDS_IDLE_LEFT("Hands_Side-left_idle-Sheet6.png"),
    HANDS_IDLE_RIGHT("Hands_Side_idle-Sheet6.png"),

    // Hands run animations
    HANDS_RUN_DOWN("Hands_down_run-Sheet6.png"),
    HANDS_RUN_UP("Hands_Up_run-Sheet6.png"),
    HANDS_RUN_LEFT("Hands_side-left_run-Sheet6.png"),
    HANDS_RUN_RIGHT("Hands_Side_run-Sheet6.png");

    private final String filename;

    CharacterAnimationAssets(String filename)
    {
        this.filename = filename;
    }

    public String getFilename()
    {
        return filename;
    }
}

