package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.statics.Tree;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public final class Level1Background
{
    private static final GameLogger LOGGER = GameLogger.getLogger(Level1Background.class);

    private Level1Background() { }

    public static GameObject[] getObjects(Player player)
    {
        try
        {
            final ArrayList<GameObject> objects = new ArrayList<>();
            objects.add(backgroundGO());
            objects.add(Tree.at(0, 0, player));
            objects.add(Tree.at(10, 0, player));
            objects.add(Tree.at(0, 10, player));
            objects.add(Tree.at(10, 10, player));
            return objects.toArray(new GameObject[0]);
        }
        catch (Exception e)
        {
            LOGGER.error(true, "Failed to GameObjects.");
        }

        return new GameObject[]{};
    }

    /* GAME OBJECTS */

    private static GameObject backgroundGO() throws FileNotFoundException
    {
        GameObject object = GameObject.create().setRenderLayer(-1);
        Image bgImage = Resources.loadImageOrThrow("assets", "scenes", "level_1", "background.png");
        object.addComponent(new SpriteRenderer(bgImage));
        return object;
    }
}
