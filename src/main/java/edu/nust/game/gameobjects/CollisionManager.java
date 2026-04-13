package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;

import java.util.HashSet;
import java.util.Set;

public class CollisionManager {
   private final GameScene scene;
   private final Set<ConcreteObj> concreteObjs = new HashSet<>();
   private final Set<DamagingObj> damagingObjs = new HashSet<>();
   private final Set<DamageableObj> damageableObjs = new HashSet<>();
   public CollisionManager(GameScene scene){
       this.scene = scene;
   }

   private void getObjs(GameScene scene) {
       for (GameObject obj : this.scene.getAllGameObjects()) {
           if(obj instanceof ConcreteObj){
               concreteObjs.add((ConcreteObj) obj);
               if(obj instanceof DamagingObj){
                   damagingObjs.add((DamagingObj) obj);
               }
               if (obj instanceof  DamageableObj) {
                   damageableObjs.add((DamageableObj) obj);
               }
           }
       }
   }

   public void manageCollisions(GameScene scene){
        getObjs(scene);
        for(ConcreteObj obj : concreteObjs){
            if(obj != null){
                for(ConcreteObj otherObj : concreteObjs){
                    if(otherObj!=null) {
                        if (obj != otherObj && obj.getHitbox().isTouching(otherObj.getHitbox())) {
                            obj.triggerCollisionEffect();
                        }
                    }
                }
            }
        }
        for(DamageableObj obj : damageableObjs){
            if(obj != null && !obj.isDead()) {
                for (DamagingObj otherObj : damagingObjs) {
                    if(otherObj != null){
                        obj.takeDamage(otherObj.getDamage());
                        otherObj.destroy(otherObj.isDestroyable());
                    }
                }
            }
        }
   }
}
