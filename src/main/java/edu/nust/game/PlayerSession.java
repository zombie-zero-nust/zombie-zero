package edu.nust.game;

public final class PlayerSession
{
    private static String playerName = "Player";

    private PlayerSession() { }

    public static void setPlayerName(String name)
    {
        if (name == null)
            return;

        String trimmed = name.trim();
        if (!trimmed.isEmpty())
            playerName = trimmed;
    }

    public static String getPlayerName()
    {
        return playerName;
    }
}

