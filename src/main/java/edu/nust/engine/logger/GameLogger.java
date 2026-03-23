package edu.nust.engine.logger;

import edu.nust.engine.logger.enums.LogFormats;
import edu.nust.engine.logger.enums.LogLevel;
import edu.nust.engine.logger.enums.LogProgressType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class GameLogger
{
    private static LogLevel GLOBAL_LEVEL = LogLevel.TRACE;
    private static boolean ENABLED = true;

    private final Logger rawLogger;

    /// Use {@link #getLogger(Class)} instead
    private GameLogger(Class<?> loggingClass) { this.rawLogger = LoggerFactory.getLogger(loggingClass); }

    public static GameLogger getLogger(Class<?> loggingClass) { return new GameLogger(loggingClass); }

    /* MESSAGE FORMATTERS */

    private String getPrefix(String level, String ansiColor)
    {
        String levelStr = "[" + level + "]";
        return ansiColor + levelStr + LogFormats.RESET + " ";
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
        // filter logs
        if (!ENABLED || !level.isEnabled(GLOBAL_LEVEL)) return;
        rawLogger.info(withMessage(level, message), args);
    }

    private void logProgressMessage(LogProgressType type, LogProgress progress, String message, Object... args)
    {
        if (!ENABLED || !LogLevel.isEnabled(GLOBAL_LEVEL, LogLevel.INFO)) return;
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
    public void error(boolean shouldThrow, String message, Object... args)
    {
        logMessage(LogLevel.ERROR, message, args);
        if (shouldThrow) throw new RuntimeException(MessageFormatter.arrayFormat(message, args).getMessage());
    }

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

    /* CONFIGURATION */

    public static void setGlobalLevel(LogLevel level) { GLOBAL_LEVEL = level; }

    public static void setEnabled(boolean enabled) { ENABLED = enabled; }
}
