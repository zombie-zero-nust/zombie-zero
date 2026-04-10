package edu.nust.engine.core;

import edu.nust.engine.resources.Resources;

import java.net.URL;

public class GameURLs
{
    public static final String BASE_PATH = "/edu/nust/game/";

    public static final String SCENES_ROOT_DIR = "scenes";
    public static final String COMMON_CSS_FILENAME = "common.css";

    public static final String SCENE_FXML_FILENAME = "layout.fxml";
    public static final String SCENE_CSS_FILENAME = "style.css";

    public static final URL COMMON_CSS_URL = Resources.tryGetResource(SCENES_ROOT_DIR, COMMON_CSS_FILENAME);
}
