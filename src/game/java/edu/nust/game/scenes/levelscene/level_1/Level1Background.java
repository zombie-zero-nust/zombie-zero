package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.statics.Tree;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public final class Level1Background
{
    private static final GameLogger LOGGER = GameLogger.getLogger(Level1Background.class);

    private static final Vector2D[] TREE_POSITIONS = {
            new Vector2D(17, 133),
            new Vector2D(50, 136),
            new Vector2D(16, 189),
            new Vector2D(24, 224),
            new Vector2D(50, 254),
            new Vector2D(21, 276),
            new Vector2D(20, 320),
            new Vector2D(43, 323),
            new Vector2D(18, 357),
            new Vector2D(46, 394),
            new Vector2D(19, 452),
            new Vector2D(49, 484),
            new Vector2D(13, 520),
            new Vector2D(47, 543),
            new Vector2D(20, 581),
            new Vector2D(38, 600),
            new Vector2D(17, 588),
            new Vector2D(22, 611),
            new Vector2D(48, 627),
            new Vector2D(16, 640),
    };

    private Level1Background() { }

    public static GameObject[] getObjects(Player player)
    {
        try
        {
            final ArrayList<GameObject> objects = new ArrayList<>();
            objects.add(backgroundGO());
            Arrays.stream(TREE_POSITIONS).forEach(pos -> objects.add(Tree.at(pos, player)));
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
        object.getTransform().setAnchorTopLeft().setPosition(0, 0);
        return object;
    }
}
