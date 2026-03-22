package edu.nust.engine.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLogger
{
    private final Logger rawLogger;
    private final String className;

    /// Use {@link #getLogger(Class)} instead
    private GameLogger(Class<?> loggingClass)
    {
        this.rawLogger = LoggerFactory.getLogger(loggingClass);
        this.className = loggingClass.getName();
    }

    public static GameLogger getLogger(Class<?> loggingClass) { return new GameLogger(loggingClass); }

    /* MESSAGE FORMATTERS */

    private String resetAnsi() { return "\u001B[0m"; }

    /// Returns caller info in format "FileName:LineNumber". If the caller info cannot be determined, returns
    /// "Unknown".
    /// <br>
    /// Does not include the `.java` extension in the file name for brevity.
    private String getCallerInfo()
    {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // loop until we find stack frame of logging class
        for (StackTraceElement frame : stack) {
            // get simple name from fully-qualified class
            String fullName = frame.getClassName(); // e.g., edu.nust.engine.TestLogger
            if (fullName.equalsIgnoreCase(className)) {
                String fileName = frame.getFileName();
                if (fileName != null) return fileName.replace(".java", "") + ":" + frame.getLineNumber();
            }
        }
        return "Unknown";
    }

    private String getPrefix(String ansiColor, LogLevel level)
    {
        String levelStr = "[" + level.name() + " ";
        return ansiColor + levelStr + getCallerInfo() + "]" + resetAnsi() + " ";
    }

    public String withMessage(LogLevel level, String... message)
    {
        return getPrefix(level.ansiColor, level) + String.join("", message);
    }

    public String withProgressMessage(LogLevel level, LogProgress progress, String... message)
    {
        return getPrefix(level.ansiColor + progress.getAnsi(), level) + String.join("", message);
    }

    /* INTERNAL */

    private void logMessage(LogLevel level, String message, Object... args)
    {
        rawLogger.info(withMessage(level, message), args);
    }

    private void logProgressMessage(LogLevel level, LogProgress progress, String message, Object... args)
    {
        rawLogger.info(withProgressMessage(level, progress, message), args);
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
