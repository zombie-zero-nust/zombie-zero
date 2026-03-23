package edu.nust.tests;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;

public final class TestLogger
{
    public static final GameLogger LOGGER = GameLogger.getLogger(TestLogger.class);

    public static void main(String[] args)
    {
        LOGGER.trace("This is a trace message.");
        LOGGER.debug("This is a debug message.");
        LOGGER.info("This is an info message.");
        for (int i = 0; i < 10; i++)
        {
            progress(i);
        }
        LOGGER.success("This is a success message.");
        LOGGER.warn("This is a warning message.");
        LOGGER.error(true, "This is an error message.");
    }

    private static void progress(int counter)
    {
        LogProgress progress = LogProgress.create("Counter " + counter, LOGGER);
        progress.begin("Starting a progress {}...", counter);
        progress.log("Halfway through the progress...");
        progress.end("Finished the progress!");
    }
}
