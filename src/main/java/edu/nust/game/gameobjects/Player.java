package edu.nust.game.gameobjects;


import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
public class Player extends Character {

    private final Set<KeyCode> activeKeys = new HashSet<>();


    private double size = 50;
    private ArrayList<Image> images = new ArrayList<>();

    private TimeSpan moveTime = TimeSpan.fromSeconds(1);




    public Player(Vector2D pos,int health,int mSpeed,boolean moveable){
        super(pos,health,mSpeed,moveable);

        try
        {
            images.add( Resources.loadImageOrThrow("assets", "images", "test.png"));

        }
        catch (FileNotFoundException ignored)
        {
        }

        this.getTransform().setPosition(getSpawnPos());
        this.addComponent(new SpriteRenderer(50,50, images.getFirst()));
    }


    public void keyPress(KeyCode key) {
        activeKeys.add(key);
    }


    public void keyRelease(KeyCode key) {
        activeKeys.remove(key);
    }

    @Override
    public void onInit() { }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        movement();
        this.getTransform().setPosition(getMovePos());
    }

    public void movement(){
        double dx= 0;
        double dy=0;

        if (activeKeys.contains(KeyCode.W)) dy -= getMovementSpeed()* moveTime.asSeconds();
        if (activeKeys.contains(KeyCode.S)) dy += getMovementSpeed()*moveTime.asSeconds();
        if (activeKeys.contains(KeyCode.A)) dx -= getMovementSpeed()*moveTime.asSeconds();
        if (activeKeys.contains(KeyCode.D)) dx += getMovementSpeed()*moveTime.asSeconds();

        setY(getY()+dy);
        setX(getX()+dx);

    }


    @Override
    public void onRender(GraphicsContext context) { }

    public void setSize(double size) { this.size = size; }


}
