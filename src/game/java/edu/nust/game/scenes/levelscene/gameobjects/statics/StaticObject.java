package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.util.Random;

public abstract class StaticObject extends GameObject
{
    /* LIFETIME */

    @Override
    public void onInit()
    {
        this.setRenderLayer(2);

        final int i = random().nextInt(numImages()) + 1;
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

        this.getTransform().setAnchorBottomCenter();
    }

    /* ABSTRACT */

    public Random random() { return new Random(1); }

    protected abstract int numImages();

    protected abstract String folderName();

    protected abstract String filename(int index);
}
