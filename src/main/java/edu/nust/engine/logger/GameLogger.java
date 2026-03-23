package edu.nust.engine.logger;

import edu.nust.engine.logger.enums.LogLevel;
import edu.nust.engine.logger.enums.LogProgressType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLogger
{
    private final Logger rawLogger;

    /// Use {@link #getLogger(Class)} instead
    private GameLogger(Class<?> loggingClass)
    {
        this.rawLogger = LoggerFactory.getLogger(loggingClass);
    }

    public static GameLogger getLogger(Class<?> loggingClass) { return new GameLogger(loggingClass); }

    /* MESSAGE FORMATTERS */

    private String resetAnsi() { return "\u001B[0m"; }

    private String getPrefix(String level, String ansiColor)
    {
        String levelStr = "[" + level + "]";
        return ansiColor + levelStr + resetAnsi() + " ";
    }

    private String withMessage(LogLevel level, String... message)
    {
        return getPrefix(level.name(), level.getAnsiColor()) + String.join("", message);
    }

    private String withProgressMessage(LogProgressType type, LogProgress progress, String... message)
    {
        return getPrefix(type.getName() + " " + progress.getName(), progress.getAnsi()) + String.join("", message);
    }


    /* INTERNAL */

    private void logMessage(LogLevel level, String message, Object... args)
    {
        rawLogger.info(withMessage(level, message), args);
    }

    private void logProgressMessage(LogProgressType type, LogProgress progress, String message, Object... args)
    {
        rawLogger.info(withProgressMessage(type, progress, message), args);
    }

    /* LOG TYPES */

    /// Trace log with cyan text
    public void trace(String message, Object... args) { logMessage(LogLevel.TRACE, message, args); }

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

    /// **`INTERNAL`** Use {@link LogProgress#begin(String, Object...)} instead
    void beginProgress(LogProgress progress, String message, Object... args)
    {
        logProgressMessage(LogProgressType.BEGIN, progress, message, args);
    }

    /// **`INTERNAL`** Use {@link LogProgress#log(String, Object...)} instead
    void logProgress(LogProgress progress, String message, Object... args)
    {
        logProgressMessage(LogProgressType.LOG, progress, message, args);
    }

    /// **`INTERNAL`** Use {@link LogProgress#end(String, Object...)} instead
    void endProgress(LogProgress progress, String message, Object... args)
    {
        logProgressMessage(LogProgressType.END, progress, message, args);
    }
}
