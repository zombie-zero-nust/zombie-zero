package edu.nust.tests;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.logger.enums.LogFormats;
import org.junit.jupiter.api.Test;

public final class TestLogger
{
    public static final GameLogger LOGGER = GameLogger.getLogger(TestLogger.class);

    public static void main(String[] args)
    {
        new TestLogger().runLoggerSmokeTest();
    }

    @Test
    void runLoggerSmokeTest()
    {
        testAllTextFormats();
        testAllForegroundColors();
        testAllBackgroundColors();
        testBrightBackgrounds();
        testProgress();
        testError();
    }

    private static void testAllTextFormats()
    {
        LOGGER.trace("TRACE -> {}", LogFormats.BOLD.apply("bold"));
        LOGGER.debug("DEBUG -> {}", LogFormats.DIM.apply("dim"));
        LOGGER.info("INFO -> {}", LogFormats.ITALIC.apply("italic"));
        LOGGER.success("SUCCESS -> {}", LogFormats.UNDERLINE.apply("underline"));
        LOGGER.warn("WARN -> {}", LogFormats.BLINK.apply("blink"));
        LOGGER.error(false, "ERROR -> {}", LogFormats.REVERSE.apply("reverse"));
    }

    private static void testAllForegroundColors()
    {
        LOGGER.info("{} {}", LogFormats.BLACK.apply("black"), LogFormats.BLACK.apply("black"));
        LOGGER.info("{} {}", LogFormats.RED.apply("red"), LogFormats.RED.apply("red"));
        LOGGER.info("{} {}", LogFormats.GREEN.apply("green"), LogFormats.GREEN.apply("green"));
        LOGGER.info("{} {}", LogFormats.YELLOW.apply("yellow"), LogFormats.YELLOW.apply("yellow"));
        LOGGER.info("{} {}", LogFormats.BLUE.apply("blue"), LogFormats.BLUE.apply("blue"));
        LOGGER.info("{} {}", LogFormats.MAGENTA.apply("magenta"), LogFormats.MAGENTA.apply("magenta"));
        LOGGER.info("{} {}", LogFormats.CYAN.apply("cyan"), LogFormats.CYAN.apply("cyan"));
        LOGGER.info("{} {}", LogFormats.WHITE.apply("white"), LogFormats.WHITE.apply("white"));
    }

    private static void testAllBackgroundColors()
    {
        LOGGER.info("BG {} ", LogFormats.BG_BLACK.apply("black"));
        LOGGER.info("BG {} ", LogFormats.BG_RED.apply("red"));
        LOGGER.info("BG {} ", LogFormats.BG_GREEN.apply("green"));
        LOGGER.info("BG {} ", LogFormats.BG_YELLOW.apply("yellow"));
        LOGGER.info("BG {} ", LogFormats.BG_BLUE.apply("blue"));
        LOGGER.info("BG {} ", LogFormats.BG_MAGENTA.apply("magenta"));
        LOGGER.info("BG {} ", LogFormats.BG_CYAN.apply("cyan"));
        LOGGER.info("BG {} ", LogFormats.BG_WHITE.apply("white"));
    }

    private static void testBrightBackgrounds()
    {
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_BLACK.apply("bright black"));
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_RED.apply("bright red"));
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_GREEN.apply("bright green"));
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_YELLOW.apply("bright yellow"));
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_BLUE.apply("bright blue"));
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_MAGENTA.apply("bright magenta"));
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_CYAN.apply("bright cyan"));
        LOGGER.info("BG {} ", LogFormats.BG_BRIGHT_WHITE.apply("bright white"));
    }

    private static void testProgress()
    {
        for (int i = 0; i < 10; i++)
        {
            progress(i);
        }
    }

    private static void progress(int counter)
    {
        LogProgress progress = LogProgress.create(
                "Counter " + LogFormats.BG_BRIGHT_MAGENTA.apply(String.valueOf(counter)) + " / " + LogFormats.BOLD.apply(
                        "A"), LOGGER
        );

        progress.begin("Starting progress {}...", counter);
        progress.log("Halfway through progress {}...", counter);
        progress.end("Finished progress {}.", counter);
    }

    private static void testError()
    {
        LOGGER.error(false, "This is a {} message.", LogFormats.REVERSE.apply("non-throwing error"));

        try
        {
            LOGGER.error(true, "This is a {} message.", LogFormats.REVERSE.apply("throwing error"));
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("Caught expected exception: {}", e.getMessage());
        }
    }
}