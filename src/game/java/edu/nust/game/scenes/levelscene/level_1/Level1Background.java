package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.Cone;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.SerializablePlacement;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObjectFactory;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObjectType;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    private static final List<Vector2D> CONE_POSITIONS = List.of(
            new Vector2D(1682, 12),
            new Vector2D(1682, 24),
            new Vector2D(1682, 36),
            new Vector2D(1682, 48)
    );

    public static GameObject[] getObjects(final LevelScene scene)
    {
        final ArrayList<GameObject> objects = new ArrayList<>(loadPlacements(scene));
        // MARK: To regenerate objects_placement.txt, comment above and uncomment below
        // final ArrayList<GameObject> objects = new ArrayList<>();
        // placementFilePreBuilder(scene, objects);
        // objects.addAll(loadPlacements(scene));

        try
        {
            objects.add(backgroundGO());
            CONE_POSITIONS.forEach(pos -> objects.add(new Cone(pos.getX(), pos.getY())));
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

    /* LOADER */

    public static List<GameObject> loadPlacements(LevelScene scene)
    {
        List<GameObject> objects = new ArrayList<>();
        LogProgress progress = new LogProgress("LOADPOS", LOGGER);
        progress.begin("Loading placements from file...");

        try
        {
            List<String> lines;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Resources.getResourceOrThrow("scenes", "LevelScene", "objects_placements.txt").openStream(),
                    StandardCharsets.UTF_8
            )))
            {
                lines = reader.lines().toList();
            }

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

    /* BUILDER */

    private static void generateTreePositionsFile(List<SerializablePlacement> placements)
    {
        LogProgress progress = new LogProgress("SAVEPOS", LOGGER);
        progress.begin("Generating formatted placement file...");

        StringBuilder sb = new StringBuilder();

        Rectangle lastRect = null;
        int rectIndex = 1;

        for (SerializablePlacement p : placements)
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
            Path path = Paths.get("src/game/resources/edu/nust/game/scenes/LevelScene/objects_placements.txt");
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

    private static void placementFilePreBuilder(LevelScene scene, ArrayList<GameObject> objectsRef)
    {
        final List<SpawnZone> zones = getSpawnZones();

        final Random random = new Random(3);
        final List<SerializablePlacement> placements = new ArrayList<>();
        zones.forEach((zone) -> {
            Rectangle rectangle = zone.rectangle();
            final int stepX = (int) zone.spacing().getX();
            final int stepY = (int) zone.spacing().getX();
            final int offset = (int) zone.offset();

            for (int x = (int) rectangle.getLeft(); x < ((int) rectangle.getRight()); x += stepX)
            {
                for (int y = (int) rectangle.getTop(); y < ((int) rectangle.getBottom()); y += stepY)
                {
                    StaticObject item = StaticObjectFactory.randomStaticAt(
                            x, y, zone.options, //
                            scene.getPlayer(), random
                    );
                    int offsetX = random.nextInt(-offset, offset + 1);
                    int offsetY = random.nextInt(-offset, offset + 1);

                    item.getTransform().getPosition().addSelf(offsetX, offsetY);

                    placements.add(new SerializablePlacement(
                            rectangle,
                            item.getTransform().getPosition(),
                            StaticObjectType.getType(item)
                                    .orElseThrow(() -> new IllegalStateException("Unknown static object type for " + item.getClass()
                                            .getSimpleName())),
                            item.getVariant()
                    ));
                    objectsRef.add(item);
                }
            }
        });

        generateTreePositionsFile(placements);
    }

    private static @NotNull List<SpawnZone> getSpawnZones()
    {
        final List<SpawnZone> zones = new ArrayList<>();
        // Full Map
        zones.add(new SpawnZone(
                List.of(StaticObjectType.GROUND_GRASS), Level1CollisionMask.getMapBounds(), //
                new Vector2D(24, 24), 13
        ));
        // Ground Left-Middle
        zones.addAll(SpawnZone.groundZones(4, 108, 68, 661));
        // Ground Top Left 1
        zones.addAll(SpawnZone.groundZones(108, 4, 852, 308));
        // Ground Top Left 2
        zones.addAll(SpawnZone.groundZones(892, 4, 1444, 308));
        // Ground Right-Middle
        zones.addAll(SpawnZone.groundZones(1484, 76, 1600, 724));
        // Ground Bottom Left 1
        zones.addAll(SpawnZone.groundZones(108, 364, 204, 660));
        zones.addAll(SpawnZone.groundZones(204, 364, 612, 794));
        zones.addAll(SpawnZone.groundZones(612, 364, 660, 644));
        // Ground Bottom Left 2
        zones.addAll(SpawnZone.groundZones(700, 364, 876, 644));
        zones.addAll(SpawnZone.groundZones(876, 364, 1060, 794));
        zones.addAll(SpawnZone.groundZones(1060, 364, 1444, 564));
        zones.addAll(SpawnZone.groundZones(1060, 604, 1444, 794));
        // City Ground
        zones.addAll(SpawnZone.groundZones(1648, 304, 2000, 752));
        return zones;
    }

    /* UTILITIES */

    private record SpawnZone(List<StaticObjectType> options, Rectangle rectangle, Vector2D spacing, double offset)
    {
        public static SpawnZone create(List<StaticObjectType> types, double sx, double sy, double ex, double ey, double spaceX, double spaceY, double offset)
        {
            return new SpawnZone(
                    types, Rectangle.fromCorners(sx, sy, ex, ey).shrunk(8, 8), //
                    new Vector2D(spaceX, spaceY), offset
            );
        }

        public static SpawnZone create(StaticObjectType type, double sx, double sy, double ex, double ey, double spaceX, double spaceY, double offset)
        {
            return create(List.of(type), sx, sy, ex, ey, spaceX, spaceY, offset);
        }

        /* PREDEFINED */

        public static List<SpawnZone> groundZones(double sx, double sy, double ex, double ey)
        {
            Rectangle area = Rectangle.fromCorners(sx, sy, ex, ey);

            // tree
            Rectangle treesArea = area.shrunk(24, 24);
            Vector2D treeSpacing = new Vector2D(64, 64);
            double treeOffset = 23;
            SpawnZone trees = new SpawnZone(
                    List.of(StaticObjectType.TREE, StaticObjectType.TREE_STUMP, StaticObjectType.FALLEN_TREE), //
                    treesArea, treeSpacing, treeOffset
            );

            // grass and stuff
            Rectangle grassArea = area.shrunk(8, 8);
            Vector2D grassSpacing = new Vector2D(24, 16);
            double grassOffset = 13;
            SpawnZone grass = new SpawnZone(
                    List.of(
                            StaticObjectType.GRASS,
                            StaticObjectType.FLOWER,
                            StaticObjectType.ROCK,
                            StaticObjectType.STICK
                    ), grassArea, grassSpacing, grassOffset
            );

            // garbage
            Rectangle garbageArea = area.grown(4, 4);
            Vector2D garbageSpacing = new Vector2D(64, 64);
            double garbageOffset = 24;
            SpawnZone garbage = new SpawnZone(
                    List.of(StaticObjectType.GARBAGE), garbageArea, //
                    garbageSpacing, garbageOffset
            );

            return List.of(trees, grass, garbage);
        }
    }
}
