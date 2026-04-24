package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.statics.StaticObjectFactory;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObjectType;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StoredPlacement;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Level1Background
{
    private Level1Background() { }

    private static final GameLogger LOGGER = GameLogger.getLogger(Level1Background.class);

    public static GameObject[] getObjects(final LevelScene scene)
    {
        final ArrayList<GameObject> objects = new ArrayList<>(loadPlacements(scene));
        // final ArrayList<GameObject> objects = new ArrayList<>();
        // placementFilePreBuilder(scene, objects);
        // objects.addAll(loadPlacements(scene));

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

    private static void generateTreePositionsFile(List<StoredPlacement> placements)
    {
        LogProgress progress = new LogProgress("SAVEPOS", LOGGER);
        progress.begin("Generating formatted placement file...");

        StringBuilder sb = new StringBuilder();

        Rectangle lastRect = null;
        int rectIndex = 1;

        for (StoredPlacement p : placements)
        {
            if (!p.rect().equals(lastRect))
            {
                Rectangle r = p.rect();

                sb.append("// Rectangle ")
                        .append(rectIndex++)
                        .append(" [")
                        .append((int) r.getLeft())
                        .append(",")
                        .append((int) r.getTop())
                        .append(",")
                        .append((int) r.getRight())
                        .append(",")
                        .append((int) r.getBottom())
                        .append("]\n");

                lastRect = r;
            }

            int x = (int) p.position().getX();
            int y = (int) p.position().getY();

            String type = p.type().name();

            sb.append(String.format("{ %4d, %4d } = %s (%d)\n", x, y, type, p.variant()));

        }

        progress.log("{} placements saved.", placements.size());

        try
        {
            Path path = Paths.get("src/generated/level_1/placements.txt");
            Files.createDirectories(path.getParent());
            Files.writeString(path, sb.toString());
            progress.end("File generated.");
        }
        catch (Exception e)
        {
            progress.end("Failed.");
            LOGGER.logException(e);
        }
    }

    public static List<GameObject> loadPlacements(LevelScene scene)
    {
        List<GameObject> objects = new ArrayList<>();
        LogProgress progress = new LogProgress("LOADPOS", LOGGER);
        progress.begin("Loading placements from file...");

        try
        {
            Path path = Paths.get("src/generated/level_1/placements.txt");
            List<String> lines = Files.readAllLines(path);

            progress.log("Total lines to process: {}", lines.size());

            for (String line : lines)
            {
                line = line.trim();

                if (line.isEmpty()) continue;

                // skip rectangle
                if (line.startsWith("// Rectangle")) continue;

                // Placement line
                // { x , y } = TYPE (VARIANT)
                int braceStart = line.indexOf('{');
                int braceEnd = line.indexOf('}', braceStart);
                int typeStart = line.indexOf('=', braceEnd);
                int typeEnd = line.indexOf('(', typeStart);
                int variantStart = line.indexOf('(', typeEnd);
                int variantEnd = line.indexOf(')', variantStart);

                String[] posParts = line.substring(braceStart + 1, braceEnd).split(",");

                double x = Double.parseDouble(posParts[0].trim());
                double y = Double.parseDouble(posParts[1].trim());

                String typeStr = line.substring(typeStart + 1, typeEnd).trim();
                StaticObjectType type = StaticObjectType.valueOf(typeStr);

                int variant = Integer.parseInt(line.substring(variantStart + 1, variantEnd).trim());

                GameObject obj = StaticObjectFactory.staticAt(x, y, variant, type, scene.getPlayer());

                objects.add(obj);
            }

            progress.end("{} Placements loaded successfully.", objects.size());
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load placements.");
            LOGGER.logException(e);
            progress.end("Placements loading failed.");
        }

        return objects;
    }

    private static void placementFilePreBuilder(LevelScene scene, ArrayList<GameObject> objectsRef)
    {
        final Random random = new Random(3);
        final List<StoredPlacement> placements = new ArrayList<>();
        Level1CollisionMask.forEachInnerRect((rectangle) -> {
            rectangle.growSelf(20, 20);
            // scene.addDebugRectangle(rectangle, TimeSpan.fromDays(1));
            final int stepX = 18;
            final int stepY = 24;
            final int offset = 13;

            for (int x = (int) rectangle.getLeft(); x < ((int) rectangle.getRight()); x += stepX)
            {
                for (int y = (int) rectangle.getTop(); y < ((int) rectangle.getBottom()); y += stepY)
                {
                    StaticObject grass = StaticObjectFactory.randomStaticAt(x, y, scene.getPlayer(), random);
                    int offsetX = random.nextInt(-offset, offset + 1);
                    int offsetY = random.nextInt(-offset, offset + 1);

                    grass.getTransform().getPosition().addSelf(offsetX, offsetY);

                    // scene.addDebugPoint(grass.getTransform().getPosition(), TimeSpan.fromDays(1));

                    placements.add(new StoredPlacement(
                            rectangle,
                            grass.getTransform().getPosition(),
                            StaticObjectType.getType(grass).orElseThrow(),
                            grass.getVariant()
                    ));
                    objectsRef.add(grass);
                }
            }
        });

        generateTreePositionsFile(placements);
    }
}
