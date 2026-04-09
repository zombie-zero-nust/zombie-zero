package edu.nust.game.gameobjects;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

//currently only a framework
public class HitBox extends Component {
    private Vector2D pos;
    private double height,width;
    private BoxRenderer box;
    private boolean visible = false;
    private boolean topTouching = false;
    private boolean bottomTouching = false;
    private boolean leftTouching = false;
    private boolean rightTouching = false;
    private boolean touching = false;
    private Vector2D topLeftCorner;
    private Vector2D topRightCorner;
    private Vector2D bottomLeftCorner;
    private Vector2D bottomRightCorner;

    public HitBox(Vector2D pos,double height,double width){
        this.pos = pos;
        this.width = width;
        this.height = height;
        box = new BoxRenderer(width,height,Color.RED);
        this.gameObject.addComponent(box).setVisible(visible);
    }


    public void getDamage(GameObject damageableObj,GameObject damagingObj){

    }

    public boolean isTouching(HitBox touchedHB){
        return false;
    }
    public boolean isTopTouching(HitBox touchedHB) {

        return false;
    }
    public boolean isBottomTouching(HitBox touchedHB){
        return false;
    }
    public boolean isLeftTouching(HitBox touchedHB){
        return false;
    }
    public boolean isRightTouching(HitBox touchedHB){
        return false;
    }

    @Override
    public void onInit(){

    }
    @Override
    public void onUpdate(TimeSpan deltaTime){
        this.setVisible(visible);
    }

    @Override
    public void onRender(GraphicsContext context){}

    public void changeVisible() {

        visible = !visible;
    }

    public void setPos(Vector2D pos){
        this.pos = pos;
    }


    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public boolean getTopTouching() {
        return topTouching;
    }

    public void setTopTouching(boolean topTouching) {
        this.topTouching = topTouching;
    }

    public boolean getBottomTouching() {
        return bottomTouching;
    }

    public void setBottomTouching(boolean bottomTouching) {
        this.bottomTouching = bottomTouching;
    }

    public boolean getLeftTouching() {
        return leftTouching;
    }

    public void setLeftTouching(boolean leftTouching) {
        this.leftTouching = leftTouching;
    }

    public boolean getRightTouching() {
        return rightTouching;
    }

    public void setRightTouching(boolean rightTouching) {
        this.rightTouching = rightTouching;
    }

    public boolean getTouching() {
        return touching;
    }

    public void setTouching(boolean touching) {
        this.touching = touching;
    }
}
