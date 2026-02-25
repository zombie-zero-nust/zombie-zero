package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class TestScene extends GameScene
{
    public TestScene()
    {
        super();
    }

    @Override
    protected void initScene()
    {
        URL fxmlUrl = getClass().getResource("TestScene/main.fxml");
        URL cssUrl = getClass().getResource("TestScene/style.css");

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
