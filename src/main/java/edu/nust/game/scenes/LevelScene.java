package edu.nust.game.scenes;


import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.gameobjects.MovingObject;
import edu.nust.game.gameobjects.MovingTag;
import edu.nust.game.gameobjects.Player;
import edu.nust.game.gameobjects.PlayerTag;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;


public class LevelScene extends GameScene {
    @FXML
    private StackPane pauseOverlay;
    @FXML private VBox helpTextContainer;

    private boolean isPaused = false;
    private Player player;

    public LevelScene(GameWorld level){
        super(level);

    }

    @Override
    public void onInit(){
        Player player = new Player(new Vector2D(0,0),100,10, true);
        this.player=player;
        this.addGameObject(player.addTag(PlayerTag.class));
        this.addGameObject(new MovingObject(
                new Vector2D(300, 300),
                new Vector2D(300, 400),
                TimeSpan.fromSeconds(1),
                Color.ORANGE
        ));




    }

    @Override
    public void onUpdate(TimeSpan deltaTime){

    }

    @Override
    public void lateUpdate(TimeSpan deltaTime){
        GameObject player = this.getFirstWithTag(PlayerTag.class);

        if(player == null) return;
        this.getWorldCamera().setPosition(player.getTransform().getPosition());
    }
    @Override
    public void onKeyPressed(KeyEvent event){

        player.keyPress(event.getCode());

        if(event.getCode()== KeyCode.ESCAPE){
            setPaused(true);
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event){
        player.keyRelease(event.getCode());
    }
    private void setPaused(boolean newState)
    {

        this.isPaused = newState;
        pauseOverlay.setVisible(newState);
        pauseOverlay.setManaged(newState);
        this.setActive(!newState);
    }
    @FXML
    private void resumeGame()
    {
        setPaused(false);
    }

    @FXML
    private void exitToMainMenu()
    {
        this.getWindow().setScene(new StartScene(this.getWindow()));
    }

}


