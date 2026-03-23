package edu.nust.engine.logger;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ANSIStripper extends MessageConverter
{
    private static final String ANSI_REGEX = "\u001B\\[[0-9;]*m";

    @Override
    public String convert(ILoggingEvent event)
    {
        String msg = event.getFormattedMessage();
        return msg.replaceAll(ANSI_REGEX, "");
    }
}