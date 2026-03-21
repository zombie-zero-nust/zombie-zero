package edu.nust.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TestLogger
{
    public static final Logger LOGGER = LoggerFactory.getLogger(TestLogger.class);

    public static void main(String[] args)
    {
        LOGGER.info("Info log message");
        LOGGER.warn("Warning log message");
        LOGGER.error("Error log message");
        LOGGER.debug("Debug log message");
    }
}
