package edu.nust.engine.logger.enums;

public enum LogLevel
{
    TRACE(LogFormats.BRIGHT_CYAN.ansiCode()),
    DEBUG(LogFormats.BRIGHT_MAGENTA.ansiCode()),
    INFO(LogFormats.BRIGHT_BLUE.ansiCode()),
    START_PROGRESS(LogFormats.RESET),
    REPORT_PROGRESS(LogFormats.RESET),
    END_PROGRESS(LogFormats.RESET),
    SUCCESS(LogFormats.BRIGHT_GREEN.ansiCode()),
    WARN(LogFormats.BRIGHT_YELLOW.ansiCode()),
    ERROR(LogFormats.BRIGHT_RED.ansiCode());

    private final String ansiColor;

    LogLevel(String ansiColor) { this.ansiColor = ansiColor; }

    public String getAnsiColor() { return ansiColor; }

    public boolean isEnabled(LogLevel current) { return this.ordinal() >= current.ordinal(); }

    public static boolean isEnabled(LogLevel current, LogLevel target) { return target.ordinal() >= current.ordinal(); }
}
