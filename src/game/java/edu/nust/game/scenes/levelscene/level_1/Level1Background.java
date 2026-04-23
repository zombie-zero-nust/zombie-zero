package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.statics.StaticObjectFactory;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public final class Level1Background
{
    private Level1Background() { }

    private static final GameLogger LOGGER = GameLogger.getLogger(Level1Background.class);

    public static GameObject[] getObjects(final LevelScene scene)
    {
        final Random random = new Random(1);
        final ArrayList<GameObject> objects = new ArrayList<>();
        final ArrayList<ArrayList<Vector2D>> groupedPositions = new ArrayList<>();

        Level1CollisionMask.forEachInnerRect((rectangle) -> {
            rectangle.growSelf(20, 20);
            //scene.addDebugRectangle(rectangle, TimeSpan.fromDays(1));
            final int stepX = 18;
            final int stepY = 24;
            final int offset = 13;

            ArrayList<Vector2D> rectPositions = new ArrayList<>();

            for (int x = (int) rectangle.getLeft(); x < ((int) rectangle.getRight()); x += stepX)
            {
                for (int y = (int) rectangle.getTop(); y < ((int) rectangle.getBottom()); y += stepY)
                {
                    GameObject grass = StaticObjectFactory.randomStaticAt(x, y, scene.getPlayer(), random);
                    int offsetX = random.nextInt(-offset, offset + 1);
                    int offsetY = random.nextInt(-offset, offset + 1);

                    grass.getTransform().getPosition().addSelf(offsetX, offsetY);

                    rectPositions.add(grass.getTransform().getPosition().copy());
                    //scene.addDebugPoint(grass.getTransform().getPosition(), TimeSpan.fromDays(1));
                    objects.add(grass);
                }
            }

            groupedPositions.add(rectPositions);
        });

        generateTreePositionsFile(groupedPositions);

        try
        {
            objects.add(backgroundGO());
            return objects.toArray(new GameObject[0]);
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to create level 1 background objects.");
            LOGGER.logException(e);
        }

        return new GameObject[]{};
    }

    /* GAME OBJECTS */

    private static GameObject backgroundGO() throws FileNotFoundException
    {
        GameObject object = GameObject.create().setRenderLayer(-2);
        Image bgImage = Resources.loadImageOrThrow("assets", "scenes", "level_1", "background.png");
        object.addComponent(new SpriteRenderer(bgImage));
        object.getTransform().setAnchorTopLeft().setPosition(0, 0);
        return object;
    }

    /* BUILDER */

    private static void generateTreePositionsFile(ArrayList<ArrayList<Vector2D>> groupedPositions)
    {
        LogProgress progress = new LogProgress("SAVEPOS", LOGGER);

        progress.begin("Generating tree positions file...");

        StringBuilder sb = new StringBuilder();

        sb.append("//@formatter:off\n");
        sb.append("private static final Vector2D[] TREE_POSITIONS = new Vector2D[]{\n");

        for (int r = 0; r < groupedPositions.size(); r++)
        {
            ArrayList<Vector2D> rectPositions = groupedPositions.get(r);

            sb.append("\t// Rectangle ").append(r + 1).append("\n");

            for (int i = 0; i < rectPositions.size(); i++)
            {
                Vector2D v = rectPositions.get(i);

                sb.append("\tnew Vector2D(")
                        .append(v.getX())
                        .append(", ")
                        .append(v.getY())
                        .append(")");

                if (i < rectPositions.size() - 1 || r < groupedPositions.size() - 1)
                    sb.append(",");

                sb.append("\n");

                progress.log("Rectangle {}: added position ({}, {})", r + 1, v.getX(), v.getY());
            }
        }

        sb.append("};\n");
        sb.append("//@formatter:on\n");

        try
        {
            Path path = Paths.get("src/generated/level_1/tree_positions.gen.txt");
            Files.createDirectories(path.getParent());
            Files.writeString(path, sb.toString());
            progress.end("Generated tree position file successfully.");
        }
        catch (Exception e)
        {
            progress.end("Failed to generate tree position file.");
            LOGGER.error(false, "Failed to write tree positions to file.");
            LOGGER.logException(e);
        }
    }
}
