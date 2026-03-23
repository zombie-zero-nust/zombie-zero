package edu.nust.engine.logger.enums;

public class LogFormats
{
    public static final String RESET = "\u001B[0m";

    //@formatter:off
    public static final Format BOLD =               Format.from("\u001B[1m");
    public static final Format DIM =                Format.from("\u001B[2m");
    public static final Format ITALIC =             Format.from("\u001B[3m");
    public static final Format UNDERLINE =          Format.from("\u001B[4m");
    public static final Format BLINK =              Format.from("\u001B[5m");
    public static final Format REVERSE =            Format.from("\u001B[7m");
    // background colors
    public static final Format BG_BLACK =           Format.from("\u001B[40m");
    public static final Format BG_RED =             Format.from("\u001B[41m");
    public static final Format BG_GREEN =           Format.from("\u001B[42m");
    public static final Format BG_YELLOW =          Format.from("\u001B[43m");
    public static final Format BG_BLUE =            Format.from("\u001B[44m");
    public static final Format BG_MAGENTA =         Format.from("\u001B[45m");
    public static final Format BG_CYAN =            Format.from("\u001B[46m");
    public static final Format BG_WHITE =           Format.from("\u001B[47m");
    // background bright colors
    public static final Format BG_BRIGHT_BLACK =    Format.from("\u001B[100m");
    public static final Format BG_BRIGHT_RED =      Format.from("\u001B[101m");
    public static final Format BG_BRIGHT_GREEN =    Format.from("\u001B[102m");
    public static final Format BG_BRIGHT_YELLOW =   Format.from("\u001B[103m");
    public static final Format BG_BRIGHT_BLUE =     Format.from("\u001B[104m");
    public static final Format BG_BRIGHT_MAGENTA =  Format.from("\u001B[105m");
    public static final Format BG_BRIGHT_CYAN =     Format.from("\u001B[106m");
    public static final Format BG_BRIGHT_WHITE =    Format.from("\u001B[107m");
    // foreground colors
    public static final Format BLACK =              Format.from("\u001B[30m");
    public static final Format RED =                Format.from("\u001B[31m");
    public static final Format GREEN =              Format.from("\u001B[32m");
    public static final Format YELLOW =             Format.from("\u001B[33m");
    public static final Format BLUE =               Format.from("\u001B[34m");
    public static final Format MAGENTA =            Format.from("\u001B[35m");
    public static final Format CYAN =               Format.from("\u001B[36m");
    public static final Format WHITE =              Format.from("\u001B[37m");
    // foreground bright colors
    public static final Format BRIGHT_BLACK =       Format.from("\u001B[90m");
    public static final Format BRIGHT_RED =         Format.from("\u001B[91m");
    public static final Format BRIGHT_GREEN =       Format.from("\u001B[92m");
    public static final Format BRIGHT_YELLOW =      Format.from("\u001B[93m");
    public static final Format BRIGHT_BLUE =        Format.from("\u001B[94m");
    public static final Format BRIGHT_MAGENTA =     Format.from("\u001B[95m");
    public static final Format BRIGHT_CYAN =        Format.from("\u001B[96m");
    public static final Format BRIGHT_WHITE =       Format.from("\u001B[97m");
    //@formatter:on


    public record Format(String ansiCode)
    {
        public String apply(String text) { return this.ansiCode + text + RESET; }

        public static Format from(String colorCode) { return new Format(colorCode); }
    }
}
