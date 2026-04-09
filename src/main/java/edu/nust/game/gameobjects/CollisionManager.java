package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import java.util.ArrayList;
import edu.nust.game.gameobjects.HitBox;

public class CollisionManager {
   private GameScene scene;
   private ArrayList<HitBox> hitboxes;
   public CollisionManager(GameScene scene){
       this.scene = scene;
       this.setHitboxes(scene);
   }

   private void setHitboxes(GameScene scene) {
       for (GameObject obj : this.scene.getAllGameObjects()) {
            HitBox hitbox = obj.getFirstComponent(HitBox.class);
            if(hitbox != null) hitboxes.add(hitbox);
       }
   }

   public static void manageCollisions(){

   }


}
