package edu.nust.game.scenes;


import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.math.TimeSpan;
import edu.nust.game.gameobjects.MovingTag;
import edu.nust.game.gameobjects.Player;
import edu.nust.game.gameobjects.PlayerTag;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class LevelScene extends GameScene {
    @FXML
    private StackPane pauseOverlay;
    @FXML private VBox helpTextContainer;

    private boolean isPaused = false;
    private Player player= new Player();

    public LevelScene(GameWorld level){
        super(level);

    }

    @Override
    public void onInit(){
        this.addGameObject(player.addTag(PlayerTag.class));
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
        this.player.movement(event);
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


