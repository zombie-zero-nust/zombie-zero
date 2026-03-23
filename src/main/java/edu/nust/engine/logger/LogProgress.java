package edu.nust.engine.logger;

import java.util.concurrent.atomic.AtomicInteger;

/// A LogProgress represents a `progress` that can be started, reported on, and ended. Each progress has a unique ID and
/// a random background color assigned to it, that is used for all logs related to that progress.
public class LogProgress
{
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    private static final ColorPair[] COLORS = { //
            ColorPair.withWhiteText("\u001B[40m"), // BLACK
            ColorPair.withWhiteText("\u001B[41m"), // RED
            ColorPair.withWhiteText("\u001B[42m"), // GREEN
            ColorPair.withWhiteText("\u001B[43m"), // YELLOW
            ColorPair.withWhiteText("\u001B[44m"), // BLUE
            ColorPair.withWhiteText("\u001B[45m"), // MAGENTA
            ColorPair.withWhiteText("\u001B[46m"), // CYAN
            ColorPair.withBlackText("\u001B[47m")  // WHITE
    };

    private static final String RESET_COLOR = "\u001B[0m";

    private final String ansiBg;
    private final String ansiFg;

    private final String name;
    private final GameLogger logger;

    /// Use {@link LogProgress#create(String, GameLogger)} instead
    public LogProgress(String name, GameLogger logger)
    {
        int id = NEXT_ID.getAndIncrement();
        this.ansiBg = COLORS[id % COLORS.length].bg;
        this.ansiFg = COLORS[id % COLORS.length].fg;

        this.name = name.replace(" ", "").toUpperCase();
        this.logger = logger;
    }

    /// Starts a {@link LogProgress} with a random color
    /// <br>
    /// Can use logging methods to log, such as:
    ///
    /// @see LogProgress#begin(String, Object...)
    /// @see LogProgress#log(String, Object...)
    /// @see LogProgress#end(String, Object...)
    public static LogProgress create(String name, GameLogger logger) { return new LogProgress(name, logger); }

    public void begin(String message, Object... args)
    {
        logger.beginProgress(this, message, args);
    }

    public void log(String message, Object... args)
    {
        logger.logProgress(this, message, args);
    }

    public void end(String message, Object... args)
    {
        logger.endProgress(this, message, args);
    }

    /* GAME LOGGER & INTERNAL */

    String getAnsi() { return ansiBg + ansiFg; }

    String getName() { return name; }

    private record ColorPair(String bg, String fg)
    {
        public static ColorPair withBlackText(String bg) { return new ColorPair(bg, "\u001B[38;2;0;0;0m"); }

        public static ColorPair withWhiteText(String bg) { return new ColorPair(bg, "\u001B[38;2;255;255;255m"); }
    }
}
