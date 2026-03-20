package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.Angle;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.paint.Color;

public class MovingObject extends GameObject
{
    private Vector2D startPosition;
    private Vector2D endPosition;
    private TimeSpan moveTime;

    private TimeSpan elapsed = TimeSpan.zero();

    public MovingObject(Vector2D startPosition, Vector2D endPosition, TimeSpan time, Color color)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.moveTime = time;

        this.getTransform().setPosition(startPosition);
        this.addComponent(new BoxRenderer(50, 50, color));
    }

    @Override
    protected void onUpdate(TimeSpan deltaTime)
    {
        super.onUpdate(deltaTime);

        // 1. Accumulate time
        elapsed = elapsed.add(deltaTime);

        // 2. Calculate completion ratio (0.0 to 1.0)
        // We use a triangle wave logic (ping-pong)
        double totalSeconds = elapsed.asSeconds();
        double duration = moveTime.asSeconds();

        // This creates a value that goes 0 -> 1 -> 0 -> 1 infinitely
        double t = (totalSeconds / duration) % 2.0;
        if (t > 1.0)
        {
            t = 2.0 - t;
        }

        // 3. Apply the Linear Interpolation (LERP)
        // Since 't' now bounces between 0 and 1, the object moves back and forth
        Vector2D currentPos = startPosition.lerp(endPosition, t);
        this.getTransform().setPosition(currentPos);
    }
}