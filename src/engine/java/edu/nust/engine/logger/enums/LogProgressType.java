package edu.nust.engine.logger.enums;

public enum LogProgressType
{
    BEGIN,
    LOG,
    END;

    public String getName()
    {
        return switch (this)
        {
            case BEGIN -> "BGN";
            case LOG -> "LOG";
            case END -> "END";
        };
    }
}