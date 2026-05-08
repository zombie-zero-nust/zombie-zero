package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.Vector2D;
import javafx.scene.paint.Color;

public class RangeIndicator extends GameObject {
    private BoxRenderer boxRenderer;
    private static boolean rangeVisible = false;

    public static void toggleRange(boolean rangeVisibility) {
        rangeVisible = rangeVisibility;
    }

    public void showRange(Vector2D weaponPos, double bulletHeight, double weaponWidth, double range, Vector2D direction, double rotation) {
        if (boxRenderer == null) {
            boxRenderer = new BoxRenderer(range, bulletHeight, Color.RED);
            boxRenderer.setFillOpacity(0.5);
            this.addComponent(boxRenderer);
        }

        Vector2D offset = direction.multiply(weaponWidth/2 + range / 2.0);
        this.getTransform().setPosition(weaponPos.add(offset));
        this.getTransform().setRotationRadians(rotation);
        this.setVisible(rangeVisible);
    }
}