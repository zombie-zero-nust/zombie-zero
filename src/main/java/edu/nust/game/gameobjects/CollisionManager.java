package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import java.util.ArrayList;
import edu.nust.game.gameobjects.HitBox;

public class CollisionManager {
   private GameScene scene;
   private final ArrayList<HitBox> hitboxes = new ArrayList<>();
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

   public void manageCollisions(){
       HitBox h1;
       HitBox h2;
        for(int i =0; i< hitboxes.size();i++){
            for(int j =0;j<hitboxes.size();j++){
                h1 = hitboxes.get(i);
                h2 = hitboxes.get(j);
                if(h1 == h2){
                    if(h1.isTouching(h2)){
                        this.triggerCollisionEffect(h1,h2);
                    }
                }
            }
        }
   }

   public void triggerCollisionEffect(HitBox a, HitBox b){
       if(a.getGameObject().getClass() == Player.class){
           if(b.getGameObject().getClass() == Bullet.class){
               //trigger damage logic
           }
       }
   }


}
