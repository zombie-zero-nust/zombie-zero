package edu.nust.engine.core.components;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.Angle;
import edu.nust.engine.math.Vector2D;

public class Transform extends Component
{
    private Vector2D position = new Vector2D();
    private Vector2D anchor = new Vector2D(0.5, 0.5);
    private Angle rotation = Angle.zero();

    /* POSITION */

    public Vector2D getPosition() { return position; }

    public void setPosition(Vector2D position) { this.position = position; }

    public void setPosition(double x, double y) { setPosition(new Vector2D(x, y)); }

    public void setPositionX(double x) { setPosition(new Vector2D(x, getPosition().getY())); }

    public void setPositionY(double y) { setPosition(new Vector2D(getPosition().getX(), y)); }

    /* TRANSLATE */

    public void translate(double x, double y) { this.position.translate(x, y); }

    public void translate(Vector2D translation) { this.position.translate(translation.getX(), translation.getY()); }

    public void translateForward(double distance) { translate(Vector2D.multiply(forward(), distance)); }

    public void translateRight(double distance) { translate(Vector2D.multiply(right(), distance)); }

    public void translateBackward(double distance) { translate(Vector2D.multiply(backward(), distance)); }

    public void translateLeft(double distance) { translate(Vector2D.multiply(left(), distance)); }

    /* ANCHOR */

    public Vector2D getAnchor() { return anchor; }

    public void setAnchor(Vector2D anchor) { this.anchor = anchor; }

    public void setAnchor(double x, double y) { setAnchor(new Vector2D(x, y)); }

    public void setAnchorX(double x) { setAnchor(new Vector2D(x, anchor.getY())); }

    public void setAnchorY(double y) { setAnchor(new Vector2D(anchor.getX(), y)); }

    public void setAnchorCentered() { setAnchor(new Vector2D(0.5, 0.5)); }

    public void setAnchorTopLeft() { setAnchor(new Vector2D(0, 0)); }

    public void setAnchorTopRight() { setAnchor(new Vector2D(1, 0)); }

    public void setAnchorBottomLeft() { setAnchor(new Vector2D(0, 1)); }

    public void setAnchorBottomRight() { setAnchor(new Vector2D(1, 1)); }

    public void setAnchorTopCenter() { setAnchor(new Vector2D(0.5, 0)); }

    public void setAnchorBottomCenter() { setAnchor(new Vector2D(0.5, 1)); }

    public void setAnchorLeftCenter() { setAnchor(new Vector2D(0, 0.5)); }

    public void setAnchorRightCenter() { setAnchor(new Vector2D(1, 0.5)); }

    /* ROTATION */

    public Angle getRotation() { return rotation; }

    public void setRotation(Angle rotation) { this.rotation = rotation; }

    public void setRotationDegrees(double degrees) { setRotation(Angle.fromDegrees(degrees)); }

    public void setRotationRadians(double radians) { setRotation(Angle.fromRadians(radians)); }

    /* ROTATE */

    public void rotate(Angle rotation) { this.rotation = this.rotation.addSelf(rotation); }

    public void rotateDegrees(double degrees) { rotate(Angle.fromDegrees(degrees)); }

    public void rotateRadians(double radians) { rotate(Angle.fromRadians(radians)); }

    /* DIRECTION */

    public Vector2D forward()
    {
        return new Vector2D( //
                Math.cos(rotation.getRadians()), //
                Math.sin(rotation.getRadians())  //
        ).normalizeSelf();
    }

    public Vector2D right()
    {
        return new Vector2D(
                Math.cos(rotation.getRadians() + Math.PI / 2),
                Math.sin(rotation.getRadians() + Math.PI / 2)
        ).normalizeSelf();
    }

    public Vector2D backward()
    {
        return new Vector2D(
                Math.cos(rotation.getRadians() + Math.PI),
                Math.sin(rotation.getRadians() + Math.PI)
        ).normalizeSelf();
    }

    public Vector2D left()
    {
        return new Vector2D(
                Math.cos(rotation.getRadians() - Math.PI / 2),
                Math.sin(rotation.getRadians() - Math.PI / 2)
        ).normalizeSelf();
    }

    /* ORIENTATION */

    public void lookAt(Vector2D target)
    {
        Vector2D direction = Vector2D.subtract(target, position);
        setRotation(Angle.fromRadians(Math.atan2(direction.getY(), direction.getX())));
    }

    public void lookAt(double x, double y) { lookAt(new Vector2D(x, y)); }

    public void lookUp() { setRotation(Angle.fromRadians(Math.PI / 2)); }

    public void lookRight() { setRotation(Angle.fromRadians(0)); }

    public void lookDown() { setRotation(Angle.fromRadians(-Math.PI / 2)); }

    public void lookLeft() { setRotation(Angle.fromRadians(Math.PI)); }

    public void setForward(Vector2D forward)
    {
        forward.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(forward.getY(), forward.getX())));
    }

    public void setForward(double x, double y) { setForward(new Vector2D(x, y)); }

    public void setRight(Vector2D right)
    {
        right.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(right.getY(), right.getX()) - Math.PI / 2));
    }

    public void setRight(double x, double y) { setRight(new Vector2D(x, y)); }

    public void setBackward(Vector2D backward)
    {
        backward.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(backward.getY(), backward.getX()) - Math.PI));
    }

    public void setBackward(double x, double y) { setBackward(new Vector2D(x, y)); }

    public void setLeft(Vector2D left)
    {
        left.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(left.getY(), left.getX()) + Math.PI / 2));
    }

    public void setLeft(double x, double y) { setLeft(new Vector2D(x, y)); }

    /* DISTANCE & ANGLE */

    public double distanceTo(Vector2D other) { return position.distanceTo(other); }

    public double distanceTo(Transform other) { return position.distanceTo(other.position); }

    public double distanceTo(double x, double y) { return position.distanceTo(new Vector2D(x, y)); }

    public Angle angleTo(Vector2D other)
    {
        Vector2D direction = Vector2D.subtract(other, position);
        return Angle.fromRadians(Math.atan2(direction.getY(), direction.getX()));
    }

    public Angle angleTo(Transform other) { return angleTo(other.position); }

    public Angle angleTo(double x, double y) { return angleTo(new Vector2D(x, y)); }

    public boolean isFacing(Vector2D target, double toleranceDegrees)
    {
        Angle targetAngle = angleTo(target);
        double angleDifference = Math.abs(rotation.getDegrees() - targetAngle.getDegrees()) % 360;
        return angleDifference <= toleranceDegrees || angleDifference >= 360 - toleranceDegrees;
    }

    public boolean isFacing(Transform target, double toleranceDegrees)
    {
        return isFacing(target.position, toleranceDegrees);
    }

    public boolean isFacing(double x, double y, double toleranceDegrees)
    {
        return isFacing(new Vector2D(x, y), toleranceDegrees);
    }

    public boolean isNear(Vector2D target, double radius) { return distanceTo(target) <= radius; }

    public boolean isNear(Transform target, double radius) { return distanceTo(target) <= radius; }

    public boolean isNear(double x, double y, double radius) { return distanceTo(x, y) <= radius; }
}
