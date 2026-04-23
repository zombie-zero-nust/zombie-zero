package edu.nust.game.systems.collision;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// currently only a framework
public class HitBox extends Component
{
    private Vector2D pos;
    private double halfH, halfW;
    private BoxRenderer box;
    private boolean visible = false;
    private boolean topTouching = false;
    private boolean bottomTouching = false;
    private boolean leftTouching = false;
    private boolean rightTouching = false;
    private double minX;
    private double minY;

    public HitBox(Vector2D pos, double halfH, double halfW)
    {
        this.pos = pos;
        this.halfW = halfW;
        this.halfH = halfH;
        box = new BoxRenderer(this.halfW, this.halfH, Color.RED);
    }


    public boolean isTouching(HitBox touchedHB)
    {

        double xDistance = this.pos.getX() - touchedHB.pos.getX();
        double yDistance = this.pos.getY() - touchedHB.pos.getY();


        minX = this.halfW + touchedHB.halfW;
        minY = this.halfH + touchedHB.halfH;
        double overlapX = minX - Math.abs(xDistance);
        double overlapY = minY - Math.abs(yDistance);


        if (Math.abs(xDistance) <= minX && Math.abs(yDistance) < minY)
        {

            if (overlapX < overlapY)
            {
                if (yDistance > 0)
                {
                    topTouching = true;

                }
                else
                {
                    bottomTouching = true;
                }
            }
            else
            {
                if (xDistance > 0)
                {
                    leftTouching = true;

                }
                else
                {
                    rightTouching = true;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public void onInit()
    {
        // Add BoxRenderer component when GameObject is ready
        if (this.gameObject != null) this.gameObject.addComponent(box).setVisible(visible);

    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        this.pos = this.getGameObject().getTransform().getPosition();
        this.setVisible(visible);
    }

    @Override
    public void onRender(GraphicsContext context) { }


    public void setPos(Vector2D pos)
    {
        this.pos = pos;
    }

    public boolean isLeftTouching()
    {
        return leftTouching;
    }

    public void setLeftTouching(boolean leftTouching)
    {
        this.leftTouching = leftTouching;
    }

    public boolean isRightTouching()
    {
        return rightTouching;
    }

    public void setRightTouching(boolean rightTouching)
    {
        this.rightTouching = rightTouching;
    }

    public void setMin(HitBox touchedHB)
    {
        minX = this.halfW + touchedHB.halfW;
        minY = this.halfH + touchedHB.halfH;
    }


    public void setTouchingFalse()
    {
        topTouching = false;
        bottomTouching = false;
        leftTouching = false;
        rightTouching = false;
    }

    public boolean isTopTouching()
    {
        return topTouching;
    }

    public void setTopTouching(boolean topTouching)
    {
        this.topTouching = topTouching;
    }

    public boolean isBottomTouching()
    {
        return bottomTouching;
    }

    public void setBottomTouching(boolean bottomTouching)
    {
        this.bottomTouching = bottomTouching;
    }

    public Vector2D getPos()
    {
        return pos;
    }

    public double getHalfH()
    {
        return halfH;
    }

    public double getHalfW()
    {
        return halfW;
    }
}
