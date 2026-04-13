package edu.nust.game.gameobjects;

public interface DamagingObj {
    int getDamage();
    boolean isDestroyable();
    boolean destroy(boolean isDestroyable);
}
