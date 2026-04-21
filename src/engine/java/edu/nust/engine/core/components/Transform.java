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

    public Transform setPosition(Vector2D position)
    {
        this.position = position;
        return this;
    }

    public Transform setPosition(double x, double y) { return setPosition(new Vector2D(x, y)); }

    public Transform setPositionX(double x) { return setPosition(new Vector2D(x, getPosition().getY())); }

    public Transform setPositionY(double y) { return setPosition(new Vector2D(getPosition().getX(), y)); }

    /* TRANSLATE */

    public Transform translate(double x, double y)
    {
        this.position.translate(x, y);
        return this;
    }

    public Transform translate(Vector2D translation) { return translate(translation.getX(), translation.getY()); }

    public Transform translateForward(double distance) { return translate(Vector2D.multiply(forward(), distance)); }

    public Transform translateRight(double distance) { return translate(Vector2D.multiply(right(), distance)); }

    public Transform translateBackward(double distance) { return translate(Vector2D.multiply(backward(), distance)); }

    public Transform translateLeft(double distance) { return translate(Vector2D.multiply(left(), distance)); }

    /* ANCHOR */

    public Vector2D getAnchor() { return anchor; }

    public Transform setAnchor(Vector2D anchor)
    {
        this.anchor = anchor;
        return this;
    }

    public Transform setAnchor(double x, double y) { return setAnchor(new Vector2D(x, y)); }

    public Transform setAnchorX(double x) { return setAnchor(new Vector2D(x, anchor.getY())); }

    public Transform setAnchorY(double y) { return setAnchor(new Vector2D(anchor.getX(), y)); }

    public Transform setAnchorCentered() { return setAnchor(new Vector2D(0.5, 0.5)); }

    public Transform setAnchorTopLeft() { return setAnchor(new Vector2D(0, 0)); }

    public Transform setAnchorTopRight() { return setAnchor(new Vector2D(1, 0)); }

    public Transform setAnchorBottomLeft() { return setAnchor(new Vector2D(0, 1)); }

    public Transform setAnchorBottomRight() { return setAnchor(new Vector2D(1, 1)); }

    public Transform setAnchorTopCenter() { return setAnchor(new Vector2D(0.5, 0)); }

    public Transform setAnchorBottomCenter() { return setAnchor(new Vector2D(0.5, 1)); }

    public Transform setAnchorLeftCenter() { return setAnchor(new Vector2D(0, 0.5)); }

    public Transform setAnchorRightCenter() { return setAnchor(new Vector2D(1, 0.5)); }

    /* ROTATION */

    public Angle getRotation() { return rotation; }

    public Transform setRotation(Angle rotation)
    {
        this.rotation = rotation;
        return this;
    }

    public Transform setRotationDegrees(double degrees) { return setRotation(Angle.fromDegrees(degrees)); }

    public Transform setRotationRadians(double radians) { return setRotation(Angle.fromRadians(radians)); }

    /* ROTATE */

    public Transform rotate(Angle rotation)
    {
        this.rotation = this.rotation.addSelf(rotation);
        return this;
    }

    public Transform rotateDegrees(double degrees) { return rotate(Angle.fromDegrees(degrees)); }

    public Transform rotateRadians(double radians) { return rotate(Angle.fromRadians(radians)); }

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

    public Transform lookAt(Vector2D target)
    {
        Vector2D direction = Vector2D.subtract(target, position);
        setRotation(Angle.fromRadians(Math.atan2(direction.getY(), direction.getX())));
        return this;
    }

    public Transform lookAt(double x, double y) { return lookAt(new Vector2D(x, y)); }

    public Transform lookUp() { return setRotation(Angle.fromRadians(Math.PI / 2)); }

    public Transform lookRight() { return setRotation(Angle.fromRadians(0)); }

    public Transform lookDown() { return setRotation(Angle.fromRadians(-Math.PI / 2)); }

    public Transform lookLeft() { return setRotation(Angle.fromRadians(Math.PI)); }

    public Transform setForward(Vector2D forward)
    {
        forward.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(forward.getY(), forward.getX())));
        return this;
    }

    public Transform setForward(double x, double y) { return setForward(new Vector2D(x, y)); }

    public Transform setRight(Vector2D right)
    {
        right.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(right.getY(), right.getX()) - Math.PI / 2));
        return this;
    }

    public Transform setRight(double x, double y) { return setRight(new Vector2D(x, y)); }

    public Transform setBackward(Vector2D backward)
    {
        backward.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(backward.getY(), backward.getX()) - Math.PI));
        return this;
    }

    public Transform setBackward(double x, double y) { return setBackward(new Vector2D(x, y)); }

    public Transform setLeft(Vector2D left)
    {
        left.normalizeSelf();
        setRotation(Angle.fromRadians(Math.atan2(left.getY(), left.getX()) + Math.PI / 2));
        return this;
    }

    public Transform setLeft(double x, double y) { return setLeft(new Vector2D(x, y)); }

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
