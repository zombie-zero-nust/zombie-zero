package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.util.Random;

public abstract class StaticObject extends GameObject
{
    private Random random = new Random();

    public StaticObject setRandom(Random random)
    {
        this.random = random;
        return this;
    }

    /* LIFETIME */

    @Override
    public void onInit()
    {
        this.setRenderLayer(renderLayer());

        final int i = random.nextInt(1, numImages() + 1);
        try
        {
            Image image = Resources.loadImageOrThrow("assets", "scenes", "level_1", folderName(), filename(i));
            this.addComponent(new SpriteRenderer(image));
        }
        catch (Exception e)
        {
            logger.error(false, "Failed to load grass image ({})", filename(i));
            logger.logException(e);
        }
    }

    /* ABSTRACT */

    protected int renderLayer() { return -1; }

    protected abstract int numImages();

    protected abstract String folderName();

    protected abstract String filename(int index);
}
