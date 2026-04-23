package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;

public class Tree extends StaticObject
{
    private static final int PLAYER_DETECTION_RADIUS = 15;
    private static final TimeSpan OPACITY_CHANGE_DURATION = TimeSpan.fromMilliseconds(50);
    private static final double DEFAULT_OPACITY = 1.0;
    private static final double HIDDEN_OPACITY = 0.25;

    private final Player player;

    private boolean isInScreenCenter = false;
    private double currentOpacity = 1.0;

    public Tree(Player player)
    {
        this.player = player;
    }

    public static GameObject at(double x, double y, Player player)
    {
        return new Tree(player).getTransform().setPosition(x, y).getGameObject();
    }

    public static GameObject at(Vector2D pos, Player player) { return at(pos.getX(), pos.getY(), player); }

    @Override
    protected Random random() { return new Random(20); }

    @Override
    protected int numImages() { return 4; }

    @Override
    protected String folderName() { return "tree"; }

    @Override
    protected String filename(int index) { return "tree_" + index + ".png"; }

    /* LIFETIME */

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (player != null)
        {
            isInScreenCenter = this.getTransform()
                    .getPosition()
                    .subtract(player.getTransform().getPosition())
                    .magnitude() < PLAYER_DETECTION_RADIUS;
        }

        currentOpacity = lerpOpacity(currentOpacity, isInScreenCenter ? HIDDEN_OPACITY : DEFAULT_OPACITY, deltaTime);
    }

    @Override
    public void onRender(GraphicsContext context)
    {
        SpriteRenderer sprite = this.getFirstComponent(SpriteRenderer.class);
        if (sprite == null) return;
        sprite.setOpacity(this.currentOpacity);
    }

    /* HELPERS */

    private double lerpOpacity(double current, double target, TimeSpan deltaTime)
    {
        double t = deltaTime.asSeconds() / OPACITY_CHANGE_DURATION.asSeconds();

        // Clamp to avoid overshooting
        t = Math.min(t, 1.0);

        return current + (target - current) * t;
    }
}
