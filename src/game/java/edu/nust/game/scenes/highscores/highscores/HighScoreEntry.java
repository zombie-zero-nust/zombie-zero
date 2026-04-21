package edu.nust.game.scenes.highscores.highscores;

import java.time.LocalDateTime;

public record HighScoreEntry(String name, int score, LocalDateTime timestamp) { }
