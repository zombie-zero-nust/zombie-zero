package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.game.gameobjects.interfaces.Concrete;
import edu.nust.game.gameobjects.interfaces.Damageable;
import edu.nust.game.gameobjects.interfaces.Damaging;

import java.util.HashSet;
import java.util.Set;

public class CollisionManager {
   private final GameScene scene;
   private final Set<Concrete> concreteObjs = new HashSet<>();
   private final Set<Damaging> damagingObjs = new HashSet<>();
   private final Set<Damageable> damageableObjs = new HashSet<>();
   public CollisionManager(GameScene scene){
       this.scene = scene;
   }

   private void getObjs() {
       concreteObjs.clear();
       damagingObjs.clear();
       damageableObjs.clear();

       for (GameObject obj : this.scene.getAllGameObjects()) {
           if(obj instanceof Concrete){
               concreteObjs.add((Concrete) obj);
           }

           if(obj instanceof Damaging){
               damagingObjs.add((Damaging) obj);
           }

           if (obj instanceof Damageable) {
               damageableObjs.add((Damageable) obj);
           }
       }
   }

   public void manageCollisions(){
        getObjs();
        for(Concrete obj : concreteObjs){
            if(obj != null){

                for(Concrete otherObj : concreteObjs){
                    if(otherObj!=null) {

                        if (obj == otherObj || obj.getHitbox() == null || otherObj.getHitbox() == null)
                            continue;

                        obj.getHitbox().setMin(otherObj.getHitbox());

                        if (obj.getHitbox().isTouching(otherObj.getHitbox())) {
                            obj.triggerCollisionEffect();
                        }
                    }
                }
            }
        }
        for(Damageable obj : damageableObjs){
            if(obj != null && !obj.isDead()) {
                for (Damaging otherObj : damagingObjs) {
                    if(otherObj != null && otherObj.getHitbox()!= null){
                        if(obj.getHitbox().isTouching(otherObj.getHitbox())) {
                            obj.getHitbox().setTouchingFalse();
                            obj.takeDamage(otherObj.getDamage());
                            otherObj.destroy(otherObj.isDestroyable());
                        }
                    }
                }
            }
        }
   }
}
