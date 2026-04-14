package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;

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
                        if (obj == otherObj || obj.getHitBox() == null || otherObj.getHitBox() == null)
                            continue;

                        obj.getHitBox().setTouchingFalse();
                        obj.getHitBox().setMin(otherObj.getHitBox());

                        if (obj.getHitBox().isTouching(otherObj.getHitBox())) {
                            obj.triggerCollisionEffect();
                        }
                    }
                }
            }
        }
        for(Damageable obj : damageableObjs){
            if(obj != null && !obj.isDead()) {
                for (Damaging otherObj : damagingObjs) {
                    if(otherObj != null){
                        obj.takeDamage(otherObj.getDamage());
                        otherObj.destroy(otherObj.isDestroyable());
                    }
                }
            }
        }
   }
}
