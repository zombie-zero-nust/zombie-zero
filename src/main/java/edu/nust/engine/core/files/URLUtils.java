package edu.nust.engine.core.files;

import java.net.URL;

public final class URLUtils
{
    /// Extracts the filename with extension from the given URL, e.g. {@code foo.wav}.
    ///
    /// @param url The URL to extract the filename from
    ///
    /// @return The filename with extension
    public static String getFileNameFromURL(URL url)
    {
        String path = url.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
