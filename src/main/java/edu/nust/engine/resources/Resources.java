package edu.nust.engine.resources;

import javafx.scene.image.Image;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.Objects;

public final class Resources
{
    public static final String BASE_PATH = "/edu/nust/game/";

    // no instance
    private Resources() {}

    /* UNSAFE */

    /// **UNSAFE** : Returns null if file not found
    public static @Nullable URL tryGetResource(String... segments)
    {
        return Resources.class.getResource(resolvePath(segments));
    }

    /* SAFE */

    /// Throws when file not found
    public static URL getResource(String... segments)
    {
        String path = resolvePath(segments);
        URL url = Resources.class.getResource(path);

        if (url == null)
        {
            throw new RuntimeException("Resource not found: " + path);
        }

        return url;
    }

    /* UTILITIES */

    public static String resolvePath(String... segments)
    {
        URI uri = URI.create(BASE_PATH);

        for (String segment : segments)
        {
            uri = uri.resolve(segment + "/");
        }

        String result = uri.getPath();

        if (result.endsWith("/"))
        {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /* TYPES */

    public static Image loadImage(String path)
    {
        return new Image(Objects.requireNonNull(getResource(path)).toExternalForm());
    }
}