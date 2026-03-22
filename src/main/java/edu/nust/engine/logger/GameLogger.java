package edu.nust.engine.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLogger
{
    private final Logger rawLogger;

    /// Use {@link #getLogger(Class)} instead
    private GameLogger(Class<?> loggingClass) { this.rawLogger = LoggerFactory.getLogger(loggingClass); }

    public static GameLogger getLogger(Class<?> loggingClass) { return new GameLogger(loggingClass); }

    /* INTERNAL */

    private void logMessage(LogLevel level, String message, Object... args)
    {
        rawLogger.info(level.withMessage(message), args);
    }

    private void logProgressMessage(LogLevel level, LogProgress progress, String message, Object... args)
    {
        rawLogger.info(level.withProgressMessage(progress, message), args);
    }

    /* LOG TYPES */

    /// Debug log with magenta text
    public void debug(String message, Object... args) { logMessage(LogLevel.DEBUG, message, args); }

    /// Info log with blue text
    public void info(String message, Object... args) { logMessage(LogLevel.INFO, message, args); }

    /// Success log with green text
    public void success(String message, Object... args) { logMessage(LogLevel.SUCCESS, message, args); }

    /// Warning log with yellow text
    public void warn(String message, Object... args) { logMessage(LogLevel.WARN, message, args); }

    /// Error log with red text
    public void error(String message, Object... args) { logMessage(LogLevel.ERROR, message, args); }

    /* PROGRESS */

    /// Starts a {@link LogProgress} with a random color, and returns the progress object to be used for reporting and
    /// ending the progress.
    public LogProgress startProgress(String message, Object... args)
    {
        LogProgress progress = LogProgress.create();
        logProgressMessage(LogLevel.START_PROGRESS, progress, message, args);
        return progress;
    }

    public void reportProgress(LogProgress progress, String message, Object... args)
    {
        logProgressMessage(LogLevel.REPORT_PROGRESS, progress, message, args);
    }

    public void endProgress(LogProgress progress, String message, Object... args)
    {
        logProgressMessage(LogLevel.END_PROGRESS, progress, message, args);
    }
}
