package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.statics.RandomGrass;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public final class Level1Background
{
    private Level1Background() { }

    private static final GameLogger LOGGER = GameLogger.getLogger(Level1Background.class);

    //@formatter:off
    //private static final List<Vector2D> TREE_POSITIONS = List.of(
    //        new Vector2D(17, 133), new Vector2D(50, 136), new Vector2D(16, 189),
    //        new Vector2D(24, 224), new Vector2D(50, 254), new Vector2D(21, 276),
    //        new Vector2D(20, 320), new Vector2D(43, 323), new Vector2D(18, 357),
    //        new Vector2D(46, 394), new Vector2D(19, 452), new Vector2D(49, 484),
    //        new Vector2D(13, 520), new Vector2D(47, 543), new Vector2D(20, 581),
    //        new Vector2D(38, 600), new Vector2D(17, 588), new Vector2D(22, 611),
    //        new Vector2D(48, 627), new Vector2D(16, 640)
    //);
    //@formatter:on

    public static GameObject[] getObjects(final LevelScene scene)
    {
        try
        {
            final ArrayList<GameObject> objects = new ArrayList<>();
            objects.add(backgroundGO());
            Level1CollisionMask.forEachInnerRect((rectangle) -> {
                rectangle.shrinkSelf(32, 4, 16, 16);
                final int stepX = 24;
                final int stepY = 32;
                final int offset = 7;

                for (int x = (int) rectangle.getLeft(); x < ((int) rectangle.getRight()); x += stepX)
                {
                    for (int y = (int) rectangle.getTop(); y < ((int) rectangle.getBottom()); y += stepY)
                    {
                        int offsetX = ThreadLocalRandom.current().nextInt(-offset, offset + 1);
                        int offsetY = ThreadLocalRandom.current().nextInt(-offset, offset + 1);

                        //scene.addDebugPoint(new Vector2D(x + offsetX, y + offsetY), TimeSpan.fromDays(1));
                        objects.add(RandomGrass.at(x + offsetX, y + offsetY));
                    }
                }
            });
            return objects.toArray(new GameObject[0]);
        }
        catch (Exception e) { LOGGER.logException(e); }

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
