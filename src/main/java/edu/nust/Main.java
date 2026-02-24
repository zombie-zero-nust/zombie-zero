package edu.nust;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        URL fxmlUrl = getUrl("/edu/nust/main.fxml");
        URL cssUrl = getUrl("/edu/nust/style.css");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(cssUrl.toExternalForm());

        stage.setTitle("JavaFX Example");
        stage.setScene(scene);

        stage.setWidth(300);
        stage.setHeight(200);
        stage.centerOnScreen();

        stage.show();
    }

    private static URL getUrl(String name)
    {
        URL fxmlUrl = Main.class.getResource(name);
        if (fxmlUrl == null)
        {
            throw new RuntimeException("File not found: " + name);
        }
        return fxmlUrl;
    }

    public static void main(String[] args)
    {
        launch();
    }
}