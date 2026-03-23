package edu.nust.engine.logger;

import edu.nust.engine.logger.enums.LogLevel;
import edu.nust.engine.logger.enums.LogProgressType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class GameLogger
{
    private final LocationAwareLogger rawLogger;
    private static final String FQCN = GameLogger.class.getName();
    private static final String PROGRESS_FQCN = LogProgress.class.getName();

    /// Use {@link #getLogger(Class)} instead
    private GameLogger(Class<?> loggingClass)
    {
        Logger logger = LoggerFactory.getLogger(loggingClass);
        if (logger instanceof LocationAwareLogger)
        {
            this.rawLogger = (LocationAwareLogger) logger;
        }
        else
        {
            throw new UnsupportedOperationException("Logger does not support location awareness.");
        }
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
        rawLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, withMessage(level, message), args, null);
    }

    /* LOG TYPES */

    /// Debug log with magenta text
    public void debug(String message, Object... args)
    {
        rawLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, withMessage(LogLevel.DEBUG, message), args, null);
    }

    /// Info log with blue text
    public void info(String message, Object... args)
    {
        rawLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, withMessage(LogLevel.INFO, message), args, null);
    }

    /// Success log with green text
    public void success(String message, Object... args)
    {
        rawLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, withMessage(LogLevel.SUCCESS, message), args, null);
    }

    /// Warning log with yellow text
    public void warn(String message, Object... args)
    {
        rawLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, withMessage(LogLevel.WARN, message), args, null);
    }

    /// Error log with red text
    public void error(String message, Object... args)
    {
        rawLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, withMessage(LogLevel.ERROR, message), args, null);
    }

    /* PROGRESS */

    /// **`INTERNAL`** Use {@link LogProgress#begin(String, Object...)} instead
    void beginProgress(LogProgress progress, String message, Object... args)
    {
        rawLogger.log(null, PROGRESS_FQCN, LocationAwareLogger.INFO_INT, withProgressMessage(LogProgressType.BEGIN, progress, message), args, null);
    }

    /// **`INTERNAL`** Use {@link LogProgress#log(String, Object...)} instead
    void logProgress(LogProgress progress, String message, Object... args)
    {
        rawLogger.log(null, PROGRESS_FQCN, LocationAwareLogger.INFO_INT, withProgressMessage(LogProgressType.LOG, progress, message), args, null);
    }

    /// **`INTERNAL`** Use {@link LogProgress#end(String, Object...)} instead
    void endProgress(LogProgress progress, String message, Object... args)
    {
        rawLogger.log(null, PROGRESS_FQCN, LocationAwareLogger.INFO_INT, withProgressMessage(LogProgressType.END, progress, message), args, null);
    }
}
