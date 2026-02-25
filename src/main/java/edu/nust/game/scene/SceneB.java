package edu.nust.game.scene;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.scene.controller.SceneBController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public class SceneB extends GameScene
{
    public SceneB(GameWorld world)
    {
        super(world);
    }

    @Override
    public String getName()
    {
        return "Scene B";
    }

    @Override
    protected void initScene()
    {
        URL fxmlUrl = getClass().getResource("SceneB/layout.fxml");
        URL cssUrl = getClass().getResource("SceneB/style.css");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        try
        {
            this.root = loader.load();

            SceneBController controller = loader.getController();
            controller.setWorld(this.getWorld());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load FXML file", e);
        }

        if (cssUrl == null)
        {
            throw new RuntimeException("CSS file not found");
        }

        root.getStylesheets().add(cssUrl.toExternalForm());
    }
}
