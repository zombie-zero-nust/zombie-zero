package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.nust.engine.math.TimeSpan;
import edu.nust.game.gameobjects.HitBox;

public class CollisionManager {
   private GameScene scene;
   private final Set<ConcreteObj> concreteObjs = new HashSet<>();
   private final Set<DamagingObj> damagingObjs = new HashSet<>();
   private final Set<DamageableObj> damageableObjs = new HashSet<>();
   public CollisionManager(GameScene scene){
       this.scene = scene;
   }

   private void setObjs(GameScene scene) {
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

   public void manageCollisions(){

   }

   public void triggerCollisionEffect(HitBox a, HitBox b){

   }


}
