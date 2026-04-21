package edu.nust.game.scenes.highscores.highscores;

import java.time.LocalDateTime;

public class HighscoreEntry
{
    private final String name;
    private final int score;
    private final LocalDateTime timestamp;

    public HighscoreEntry(String name, int score, LocalDateTime timestamp)
    {
        this.name = name;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getName() { return name; }

    public int getScore() { return score; }

    public LocalDateTime getTimestamp() { return timestamp; }
}

