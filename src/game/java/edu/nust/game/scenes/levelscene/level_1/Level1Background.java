package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public final class Level1Background
{
    private Level1Background() { }

    public static GameObject[] getObjects(GameLogger logger)
    {
        try
        {
            final ArrayList<GameObject> objects = new ArrayList<>();
            objects.add(backgroundGO());
            objects.add(treeGO(0, 0));
            return objects.toArray(new GameObject[0]);
        }
        catch (Exception e)
        {
            logger.error(true, "Failed to GameObjects.");
        }

        return new GameObject[]{};
    }

    /* GAMEOBJECTS */

    private static GameObject backgroundGO() throws FileNotFoundException
    {
        GameObject object = GameObject.create();
        Image bgImage = Resources.loadImageOrThrow("assets", "scenes", "level_1", "background.png");
        object.addComponent(new SpriteRenderer(bgImage));
        return object;
    }

    private static GameObject treeGO(double x, double y) throws FileNotFoundException
    {
        GameObject object = GameObject.create();
        Image treeImage = Resources.loadImageOrThrow("assets", "scenes", "level_1", "tree.png");
        object.addComponent(new SpriteRenderer(treeImage));
        object.getTransform().setPosition(x, y);
        return object;
    }
}
