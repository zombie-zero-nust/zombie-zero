package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.util.Random;

public abstract class StaticObject extends GameObject
{
    protected final int variant;

    public StaticObject(int variant)
    {
        this.variant = variant;
    }

    public StaticObject(Random random)
    {
        this.variant = random.nextInt(1, numImages() + 1);
    }

    /* LIFETIME */

    @Override
    public void onInit()
    {
        this.setRenderLayer(renderLayer());

        try
        {
            Image image = Resources.loadImageOrThrow("assets", "scenes", "level_1", folderName(), filename(variant));
            SpriteRenderer renderer = new SpriteRenderer(image);
            this.addComponent(renderer);

            // deterministic random flip
            boolean shouldFlip = ((getTransform().getPosition().getX() * variant) % 2) == 0;
            if (shouldFlip) renderer.flipHorizontal();
        }
        catch (Exception e)
        {
            logger.error(false, "Failed to load grass image ({})", filename(variant));
            logger.logException(e);
        }

        // rotate based on current position
        if (rotateRandom())
            this.getTransform().setRotationDegrees(((getTransform().getPosition().getX() * variant) % 4) * 90);
    }

    /* GETTERS & SETTERS */

    public int getVariant() { return variant; }

    /* ABSTRACT */

    protected int renderLayer() { return -1; }

    protected boolean rotateRandom() { return false; }

    protected abstract int numImages();

    protected abstract String folderName();

    protected abstract String filename(int index);
}
