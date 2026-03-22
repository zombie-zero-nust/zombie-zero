package edu.nust.engine.logger;

public enum LogLevel
{
    DEBUG("\u001B[35m"),   // magenta text
    INFO("\u001B[34m"),    // blue text
    START_PROGRESS(""),    // ignored for progress
    REPORT_PROGRESS(""),   // ignored
    END_PROGRESS(""),      // ignored
    SUCCESS("\u001B[32m"), // green text
    WARN("\u001B[33m"),    // yellow text
    ERROR("\u001B[31m");   // red text

    final String ansiColor;

    LogLevel(String ansiStr) { this.ansiColor = ansiStr; }
}
