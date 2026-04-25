package edu.nust.game.systems;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.math.TimeSpan;

/**
 * Score - Manages player score in the game
 * <p>
 * Features:
 * - 1 point for every 5 seconds survived
 * - 3 points for each zombie kill
 * - 15 points for boss kill
 * - Tracks total time elapsed
 * - Tracks if boss is defeated (win condition)
 * <p>
 * Usage: Score score = new Score(); score.update(deltaTime);  // call every frame
 * int currentScore = score.getScore(); score.addZombieKill();  // add 3 points
 * score.addBossKill();  // add 15 points and mark win
 */
public class Score
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    // Current score value
    private int score = 0;

    // Total time elapsed in the game (in seconds)
    private double totalElapsedTime = 0.0;

    // Time elapsed since last score increment (in seconds)
    private double elapsedTime = 0.0;

    // Time interval between score increments (5 seconds)
    private static final double SCORE_INCREMENT_INTERVAL = 5.0;

    // Points for kills
    private static final int ZOMBIE_KILL_POINTS = 3;
    private static final int BOSS_KILL_POINTS = 15;

    // Win condition flag
    private boolean bossDefeated = false;

    /**
     * Constructor - Initialize score at 0
     */
    public Score()
    {
        this.score = 0;
        this.elapsedTime = 0.0;
        this.totalElapsedTime = 0.0;
        this.bossDefeated = false;
        logger.debug("Score system initialized");
    }

    /**
     * Update score based on elapsed time. Call this every frame to track time and increment score.
     * <p>
     * Logic:
     * 1. Add deltaTime to total elapsed time and interval counter
     * 2. If elapsed time >= 5 seconds:
     *    - Increment score by 1
     *    - Reset interval counter
     *
     * @param deltaTime Time elapsed since last frame (TimeSpan object)
     */
    public void update(TimeSpan deltaTime)
    {
        double deltaSeconds = deltaTime.asSeconds();

        // Track total time in game
        totalElapsedTime += deltaSeconds;

        // Add the frame's time delta to the elapsed time
        elapsedTime += deltaSeconds;

        // Check if 5 seconds have passed since last score increment
        if (elapsedTime >= SCORE_INCREMENT_INTERVAL)
        {
            // Increment score by 1 (player survived another 5 seconds)
            score++;
            logger.debug("Score incremented by 1 for time survival. Total score: {}", score);

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


    /**
     * Add points for zombie kill
     */
    public void addZombieKill()
    {
        score += ZOMBIE_KILL_POINTS;
        logger.debug("Zombie killed! +{} points. Total score: {}", ZOMBIE_KILL_POINTS, score);
    }

    /**
     * Add points for boss kill and mark boss as defeated (win condition)
     */
    public void addBossKill()
    {
        score += BOSS_KILL_POINTS;
        bossDefeated = true;
        logger.debug("BOSS DEFEATED! +{} points. Total score: {}", BOSS_KILL_POINTS, score);
    }

    /**
     * Check if boss has been defeated (win condition)
     */
    public boolean isBossDefeated()
    {
        return bossDefeated;
    }

    /**
     * Get total elapsed time in the game
     *
     * @return Total elapsed time in seconds
     */
    public double getTotalElapsedTime()
    {
        return totalElapsedTime;
    }

    /**
     * Add arbitrary points (generic method for flexibility)
     * Used by CollisionManager for zombie/other kills
     */
    public void addPoints(int points)
    {
        if (points <= 0) return;
        this.score += points;
        logger.debug("Added {} points. Total score: {}", points, score);
    }

    /**
     * Reset score to 0 (when game ends or player is reset)
     * Also resets elapsed time counters
     */
    public void reset()
    {
        logger.warn("Score reset to 0");
        this.score = 0;
        this.elapsedTime = 0.0;
        this.totalElapsedTime = 0.0;
        this.bossDefeated = false;
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

