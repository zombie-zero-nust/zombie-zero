package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

//currently only a framework
public class HitBox extends GameObject {
    private Vector2D pos;
    private double height,width;
    private boolean visible = false;

    public HitBox(Vector2D pos,double height,double width){
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.addComponent(new BoxRenderer(this.width,this.height, Color.RED));
    }


    public void getDamage(){

    }

    @Override
    public void onInit(){

    }
    @Override
    public void onUpdate(TimeSpan deltaTime){
        this.getTransform().setPosition(pos);
    }

    @Override
    public void onRender(GraphicsContext context){}

    public void changeVisible() {
        visible = !visible;
    }

    public void setPos(Vector2D pos){
        this.pos = pos;
    }


}
