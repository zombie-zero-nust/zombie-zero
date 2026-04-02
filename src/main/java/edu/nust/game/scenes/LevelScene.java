package edu.nust.game.scenes;


import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.Player;
import edu.nust.game.gameobjects.PlayerTag;
import edu.nust.game.gameobjects.OrbitingBox;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LevelScene extends GameScene {
    @FXML
    private StackPane pauseOverlay;
    @FXML private VBox helpTextContainer;

    private boolean isPaused = false;
    private Player player;
    private GameObject weaponBox;
    private OrbitingBox weaponComponent;
    private Vector2D mousePosition = Vector2D.zero();

    public LevelScene(GameWorld level){
        super(level);

    }

    @Override
    public void onInit(){
        // Create character (logo)
        Player player = new Player(new Vector2D(0,0),100,10, true);
        this.player = player;
        this.addGameObject(player.addTag(PlayerTag.class));

        // Create weapon box
        weaponBox = GameObject.create();
        weaponComponent = new OrbitingBox(80); // orbit distance from character
        BoxRenderer boxRenderer = new BoxRenderer(40, 40, Color.CYAN);
        weaponBox.addComponent(weaponComponent);
        weaponBox.addComponent(boxRenderer);
        this.addGameObject(weaponBox);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime){
        // Update handled in lateUpdate
    }

    @Override
    public void lateUpdate(TimeSpan deltaTime){
        GameObject character = this.getFirstWithTag(PlayerTag.class);

        if(character == null) return;

        // Update weapon position and rotation based on mouse
        if (weaponComponent != null)
        {
            weaponComponent.updateMousePosition(mousePosition);
            weaponComponent.updatePositionBasedOnMouse(character.getTransform().getPosition());
        }

        this.getWorldCamera().setPosition(character.getTransform().getPosition());
    }
    @Override
    public void onKeyPressed(KeyEvent event){

        player.keyPress(event.getCode());

        if(event.getCode()== KeyCode.ESCAPE){
            setPaused(true);
        }
        else if(event.getCode() == KeyCode.G){
            this.toggleDebugGrid();
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event){
        player.keyRelease(event.getCode());
    }

    @Override
    public void onMouseMoved(MouseEvent event)
    {
        // Track mouse position in world coordinates
        double screenX = event.getX();
        double screenY = event.getY();

        // Get canvas dimensions
        double canvasW = this.getWorldLayer().getWidth();
        double canvasH = this.getWorldLayer().getHeight();

        // Get camera info
        Vector2D cameraPos = this.getWorldCamera().getPosition();
        double zoom = this.getWorldCamera().getZoom();

        // Convert screen to world coordinates
        double worldX = cameraPos.getX() + (screenX - canvasW / 2) / zoom;
        double worldY = cameraPos.getY() + (screenY - canvasH / 2) / zoom;

        this.mousePosition = new Vector2D(worldX, worldY);
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


