package edu.nust.game.scenes.demo.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MovingObject extends GameObject
{
    private Vector2D startPosition;
    private Vector2D endPosition;
    private TimeSpan moveTime;

    private double size = 50;

    private TimeSpan elapsed = TimeSpan.zero();

    public MovingObject(Vector2D startPosition, Vector2D endPosition, TimeSpan time, Color color)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.moveTime = time;

        this.getTransform().setPosition(startPosition);
        this.addComponent(new BoxRenderer(size, size, color));
    }

    @Override
    public void onInit() { }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        // accumulate time
        elapsed = elapsed.add(deltaTime);

        // compute progress (clamped between 0 and 1)
        double raw = (elapsed.asSeconds() % (2 * moveTime.asSeconds())) / moveTime.asSeconds();
        double t = raw <= 1 ? raw : 2 - raw;

        // interpolate position
        Vector2D direction = endPosition.subtract(startPosition);
        Vector2D current = startPosition.add(direction.multiply(t));

        // apply position
        this.getTransform().setPosition(current);
    }

    @Override
    public void onRender(GraphicsContext context) { }

    public void setSize(double size) { this.size = size; }
}