package edu.nust.engine.resources;

import javafx.scene.image.Image;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;

public final class Resources
{
    public static final String BASE_PATH = "/edu/nust/game/";

    // no instance
    private Resources() {}

    /* UNSAFE */

    /**
     * Attempts to retrieve a resource relative to the base path "/edu/nust/game/", is {@code null} if not found.
     * <p>
     * For example, calling:
     * <pre>
     *     tryGetResource("assets", "images", "player.png")
     * </pre>
     * will look for the resource at:
     * <pre>
     *     /edu/nust/game/assets/images/player.png
     * </pre>
     *
     * @param path Path segments leading to the resource.
     *
     * @return URL of the resource at the specified path, or {@code null} if not found.
     */
    public static @Nullable URL tryGetResource(String... path)
    {
        return Resources.class.getResource(resolvePath(path));
    }

    /* SAFE */

    /**
     * Attempts to retrieve a resource relative to the base path "/edu/nust/game/", throws if not found.
     * <p>
     * For example, calling:
     * <pre>
     *     tryGetResource("assets", "images", "player.png")
     * </pre>
     * will look for the resource at:
     * <pre>
     *     /edu/nust/game/assets/images/player.png
     * </pre>
     *
     * @param path Path segments leading to the resource.
     *
     * @return URL of the resource at the specified path.
     */
    public static URL getResource(String... path)
    {
        String resolved = resolvePath(path);
        URL url = Resources.class.getResource(resolved);

        if (url == null)
        {
            throw new RuntimeException("Resource not found: " + resolved);
        }

        return url;
    }

    /* UTILITIES */

    /**
     * Resolves a path relative to the base path "/edu/nust/game/".
     * <p>
     * For example, calling:
     * <pre>
     *     resolvePath("assets", "images", "player.png")
     * </pre>
     * will return:
     * <pre>
     *     /edu/nust/game/assets/images/player.png
     * </pre>
     *
     * @param path Path segments to resolve.
     *
     * @return Resolved path string.
     */
    public static String resolvePath(String... path)
    {
        URI uri = URI.create(BASE_PATH);

        for (String segment : path)
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

    public static Image loadImage(String... path)
    {
        return new Image(getResource(path).toExternalForm());
    }
}