package edu.nust.game.scene;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.scene.controller.SceneAController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public class SceneA extends GameScene
{
    public SceneA(GameWorld world)
    {
        super(world);
    }

    @Override
    public String getName()
    {
        return "Scene A";
    }

    @Override
    protected void initScene()
    {
        URL fxmlUrl = getClass().getResource("SceneA/layout.fxml");
        URL cssUrl = getClass().getResource("SceneA/style.css");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        try
        {
            this.root = loader.load();

            SceneAController controller = loader.getController();
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
