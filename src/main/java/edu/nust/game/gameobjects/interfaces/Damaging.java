package edu.nust.game.gameobjects.interfaces;

public interface Damaging {
    String[] notInteractWith();
    int getDamage();
    boolean isDestroyable();
    void destroy(boolean isDestroyable);
}
