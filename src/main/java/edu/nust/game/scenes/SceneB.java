package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class SceneB extends GameScene
{
    private boolean isAnimating = false;
    private double time = 0.0;
    private static final double CYCLE_DURATION = 3.0; // seconds (R->G->B)

    /* FXML Elements */

    @FXML private Label messageLabel;
    @FXML private Canvas canvas;

    /* CONSTRUCTOR */

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
    protected void onStart()
    {
        drawCurrentColor(Color.RED);
    }

    @Override
    protected void onUpdate(double deltaTime)
    {
        if (!isAnimating) return;

        time += deltaTime;

        double t = (time % CYCLE_DURATION) / CYCLE_DURATION;

        Color color = computeColor(t);
        drawCurrentColor(color);
    }

    private void drawCurrentColor(Color color)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(color);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /* FXML Button Callbacks */

    @FXML
    private void drawImage()
    {
        isAnimating = !isAnimating;
        messageLabel.setText(isAnimating ? "Animating..." : "Stopped");
    }

    @FXML
    private void switchToSceneA()
    {
        this.getWorld().setCurrentGameScene(new SceneA(this.getWorld()));
    }

    /* UTILS */

    private static Color computeColor(double t)
    {
        // 0.0 → 1.0 split into 3 segments
        double segment = t * 3.0;

        if (segment < 1.0)
        {
            return lerp(Color.RED, Color.GREEN, segment);
        }
        else if (segment < 2.0)
        {
            return lerp(Color.GREEN, Color.BLUE, segment - 1.0);
        }
        else
        {
            return lerp(Color.BLUE, Color.RED, segment - 2.0);
        }
    }

    private static Color lerp(Color a, Color b, double t)
    {
        double r = a.getRed() + (b.getRed() - a.getRed()) * t;
        double g = a.getGreen() + (b.getGreen() - a.getGreen()) * t;
        double bl = a.getBlue() + (b.getBlue() - a.getBlue()) * t;

        return new Color(r, g, bl, 1.0);
    }
}