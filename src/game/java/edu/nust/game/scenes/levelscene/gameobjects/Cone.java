package edu.nust.game.scenes.levelscene.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

public class Cone extends GameObject
{
    public Cone(double x, double y) { this.getTransform().setPosition(x, y); }

    @Override
    public void onInit()
    {
        this.getTransform().setAnchorBottomCenter();
        try
        {
            Image image = Resources.loadImageOrThrow("scenes", "LevelScene", "objects", "roadblock", "cone.png");
            this.addComponent(new SpriteRenderer(image));
        }
        catch (Exception e)
        {
            logger.warn("Car error: {}", e.getMessage());
        }
    }
}
