package edu.nust.game.scenes.levelscene.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.util.Random;

public class Car extends GameObject
{
    private static final int VARIANT_COUNT = 26;
    private final Random random = new Random(12);

    public Car(double x, double y)
    {
        this.getTransform().setPosition(x, y);
    }

    @Override
    public void onInit()
    {
        try
        {
            Image image = Resources.loadImageOrThrow(
                    "scenes",
                    "LevelScene",
                    "objects",
                    "cars",
                    "car_" + getVariantFromPosition() + ".png"
            );
            this.addComponent(new SpriteRenderer(image));
        }
        catch (Exception e)
        {
            logger.warn("Car error: {}", e.getMessage());
        }
    }

    private int getVariantFromPosition()
    {
        Vector2D pos = getTransform().getPosition().copy();
        // Simple hash: combine x and y into a deterministic index 1..VARIANT_COUNT
        long hash = 31L * Double.doubleToLongBits(pos.getX()) + 17L * Double.doubleToLongBits(pos.getY());
        int index = (int) (Math.abs(hash) % VARIANT_COUNT);
        return index + 1;   // 1‑based variant
    }
}
