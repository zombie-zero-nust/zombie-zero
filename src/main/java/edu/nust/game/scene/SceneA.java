package edu.nust.game.scene;

import edu.nust.engine.core.GameScene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class SceneA extends GameScene
{
    @Override
    protected void initScene()
    {
        URL fxmlUrl = getClass().getResource("SceneA/layout.fxml");
        URL cssUrl = getClass().getResource("SceneA/style.css");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        try
        {
            this.scene = new Scene(loader.load());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load FXML file", e);
        }

        if (cssUrl == null)
        {
            throw new RuntimeException("CSS file not found");
        }

        scene.getStylesheets().add(cssUrl.toExternalForm());
    }
}
