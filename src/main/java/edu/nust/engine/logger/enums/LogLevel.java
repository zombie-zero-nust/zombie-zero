package edu.nust.engine.logger.enums;

public enum LogLevel
{
    TRACE("\u001B[36m"),   // cyan text
    DEBUG("\u001B[35m"),   // magenta text
    INFO("\u001B[34m"),    // blue text
    START_PROGRESS(""),    // ignored for progress
    REPORT_PROGRESS(""),   // ignored
    END_PROGRESS(""),      // ignored
    SUCCESS("\u001B[32m"), // green text
    WARN("\u001B[33m"),    // yellow text
    ERROR("\u001B[31m");   // red text

    private final String ansiColor;

    LogLevel(String ansiColor) { this.ansiColor = ansiColor; }

    public String getAnsiColor() { return ansiColor; }

    public boolean isEnabled(LogLevel current) { return this.ordinal() >= current.ordinal(); }

    public static boolean isEnabled(LogLevel current, LogLevel target) { return target.ordinal() >= current.ordinal(); }
}
