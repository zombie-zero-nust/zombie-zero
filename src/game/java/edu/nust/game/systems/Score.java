package edu.nust.game.systems;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.math.TimeSpan;

/**
 * Score - Manages player score in the game
 * <p>
 * Features: - Increments score by 1 every 5 seconds the player survives - Resets score to 0 when player collides with
 * enemy - Tracks elapsed time and score
 * <p>
 * Usage: Score score = new Score(); score.update(deltaTime);  // call every frame int currentScore = score.getScore();
 * score.reset();  // reset on game over
 */
public class Score
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    // Current score value
    private int score = 0;

    // Time elapsed since last score increment (in seconds)
    private double elapsedTime = 0.0;

    // Time interval between score increments (5 seconds)
    private static final double SCORE_INCREMENT_INTERVAL = 5.0;

    /**
     * Constructor - Initialize score at 0
     */
    public Score()
    {
        this.score = 0;
        this.elapsedTime = 0.0;
        logger.debug("Score system initialized");
    }

    /**
     * Update score based on elapsed time Call this every frame to track time and increment score
     * <p>
     * Logic: 1. Add deltaTime to elapsed time counter 2. If elapsed time >= 5 seconds: - Increment score by 1 - Reset
     * elapsed time counter - Log the score increase
     *
     * @param deltaTime Time elapsed since last frame (TimeSpan object)
     */
    public void update(TimeSpan deltaTime)
    {
        // Add the frame's time delta to the elapsed time
        elapsedTime += deltaTime.asSeconds();

        // Check if 5 seconds have passed since last score increment
        if (elapsedTime >= SCORE_INCREMENT_INTERVAL)
        {
            // Increment score by 1 (player survived another 5 seconds)
            score++;
            logger.debug("Score incremented to: {}", score);

            // Reset elapsed time counter for next interval
            elapsedTime = 0.0;
        }
    }

    /**
     * Get the current score value
     *
     * @return Current score as integer
     */
    public int getScore()
    {
        return this.score;
    }

    /**
     * Set the score to a specific value Mainly used for testing or special situations
     *
     * @param newScore The score value to set
     */
    public void setScore(int newScore)
    {
        logger.debug("Score changed from {} to {}", this.score, newScore);
        this.score = newScore;
    }


    public void addPoints(int points)
    {
        if (points <= 0) return;
        this.score += points;
    }

    /**
     * Reset score to 0 (when player collides with enemy) Also resets elapsed time counter
     */
    public void reset()
    {
        logger.warn("Score reset to 0 (player hit enemy)");
        this.score = 0;
        this.elapsedTime = 0.0;
    }

    /**
     * Get remaining time until next score increment Useful for displaying progress to player
     *
     * @return Remaining time in seconds until next score increment
     */
    public double getTimeUntilNextIncrement()
    {
        return Math.max(0, SCORE_INCREMENT_INTERVAL - elapsedTime);
    }

    /**
     * String representation of score for debugging
     *
     * @return String with current score and elapsed time
     */
    @Override
    public String toString()
    {
        return String.format("Score: %d (Time to next: %.1fs)", score, getTimeUntilNextIncrement());
    }
}

