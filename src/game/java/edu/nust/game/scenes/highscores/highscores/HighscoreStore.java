package edu.nust.game.scenes.highscores.highscores;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HighscoreStore
{
    private static final Path FILE_PATH = Path.of("highscores.txt");
    public static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private HighscoreStore() { }

    public static void append(String name, int score, LocalDateTime timestamp)
    {
        String safeName = (name == null || name.isBlank()) ? "Player" : name.replace(",", " ").trim();
        String safeTimestamp = timestamp.format(TIMESTAMP_FORMAT);
        String line = safeName + "," + score + "," + safeTimestamp + System.lineSeparator();

        try
        {
            Files.writeString(
                    FILE_PATH,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        }
        catch (IOException ignored)
        {
        }
    }

    public static List<HighscoreEntry> loadTop(int limit)
    {
        List<HighscoreEntry> all = loadAllSorted();
        int end = Math.min(Math.max(limit, 0), all.size());
        return new ArrayList<>(all.subList(0, end));
    }

    public static List<HighscoreEntry> loadAllSorted()
    {
        if (!Files.exists(FILE_PATH))
            return new ArrayList<>();

        List<HighscoreEntry> entries = new ArrayList<>();

        try
        {
            for (String line : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8))
            {
                HighscoreEntry parsed = parseLine(line);
                if (parsed != null)
                    entries.add(parsed);
            }
        }
        catch (IOException ignored)
        {
            return new ArrayList<>();
        }

        entries.sort(
                Comparator
                        .comparingInt(HighscoreEntry::getScore)
                        .reversed()
                        .thenComparing(HighscoreEntry::getTimestamp)
        );

        return entries;
    }

    private static HighscoreEntry parseLine(String line)
    {
        if (line == null || line.isBlank())
            return null;

        String[] parts = line.split(",", 3);
        if (parts.length != 3)
            return null;

        String name = parts[0].trim();
        if (name.isEmpty())
            name = "Player";

        try
        {
            int score = Integer.parseInt(parts[1].trim());
            LocalDateTime timestamp = LocalDateTime.parse(parts[2].trim(), TIMESTAMP_FORMAT);
            return new HighscoreEntry(name, score, timestamp);
        }
        catch (NumberFormatException | DateTimeParseException ignored)
        {
            return null;
        }
    }
}

