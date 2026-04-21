package edu.nust.engine.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.fusesource.jansi.AnsiConsole;

public class ColoredConsoleAppender extends ConsoleAppender<ILoggingEvent> {

    @Override
    public void start() {
        if (System.console() != null) {
            AnsiConsole.systemInstall();
        }
        super.start();
    }

    @Override
    public void stop() {
        if (System.console() != null) {
            AnsiConsole.systemUninstall();
        }
        super.stop();
    }
}

