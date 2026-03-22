package edu.nust.engine.logger;

public enum LogLevel
{
    DEBUG("\u001B[45m"),
    INFO("\u001B[44m"),
    START_PROGRESS(""),   // ignored, see `LogProgress`
    REPORT_PROGRESS(""),  // ignored, see `LogProgress`
    END_PROGRESS(""),     // ignored, see `LogProgress`
    SUCCESS("\u001B[42m"),
    WARN("\u001B[43m"),
    ERROR("\u001B[41m");

    private final String ansiColor;

    LogLevel(String ansiStr)
    {
        this.ansiColor = ansiStr;
    }

    private String resetAnsi() { return "\u001B[0m"; }

    /* MESSAGE FORMATTERS */

    public String withMessage(String... message)
    {
        return this.ansiColor + "[" + this.name() + "]" + resetAnsi() + " " + String.join("", message);
    }

    public String withProgressMessage(LogProgress progress, String... message)
    {
        return this.ansiColor + progress.getAnsi() + "[" + this.name() + "]" + resetAnsi() //
                + " " + String.join("", message);
    }
}
