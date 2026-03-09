package edu.nust.engine.components;

import edu.nust.engine.core.Component;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoxRenderer extends Component
{
    private final double width;
    private final double height;

    public BoxRenderer(double width, double height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onRender(GraphicsContext context)
    {
        context.setFill(Color.BLUE);
        Transform transform = gameObject.getTransform();
        double x = transform.getPosition().getX() - width * transform.getAnchor().getX();
        double y = transform.getPosition().getY() - height * transform.getAnchor().getY();
        context.fillRect(x, y, width, height);
    }
}