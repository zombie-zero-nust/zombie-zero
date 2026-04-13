package edu.nust.game.gameobjects;

public interface Damaging {
    int getDamage();
    boolean isDestroyable();
    boolean destroy(boolean isDestroyable);
}
