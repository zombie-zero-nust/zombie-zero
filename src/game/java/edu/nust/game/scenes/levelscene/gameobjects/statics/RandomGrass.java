package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.util.Random;

public class RandomGrass extends GameObject
{
    private static final int NUM_IMAGES = 25;
    private static final Random SEEDED_RANDOM = new Random(1);

    public static GameObject at(double x, double y)
    {
        return new RandomGrass().getTransform().setPosition(x, y).getGameObject();
    }

    public static GameObject at(Vector2D pos) { return at(pos.getX(), pos.getY()); }

    /* LIFETIME */

    @Override
    public void onInit()
    {
        this.setRenderLayer(2);

        Image grassImage = null;
        final int i = SEEDED_RANDOM.nextInt(NUM_IMAGES) + 1;
        final String filename = "grass_" + i + ".png";
        try
        {
            grassImage = Resources.loadImageOrThrow("assets", "scenes", "level_1", "grass", filename);
            this.addComponent(new SpriteRenderer(grassImage));
        }
        catch (Exception e)
        {
            logger.error(false, "Failed to load grass image ({})", filename);
            logger.logException(e);
        }

        this.getTransform().setAnchorBottomCenter();
    }
}
