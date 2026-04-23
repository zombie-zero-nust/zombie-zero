package edu.nust.game.scenes.levelscene.components;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.Nullable;

public class SeeThroughComponent extends Component
{
    private static final int PLAYER_DETECTION_RADIUS = 15;
    private static final TimeSpan OPACITY_CHANGE_DURATION = TimeSpan.fromMilliseconds(50);
    private static final double DEFAULT_OPACITY = 1.0;
    private static final double HIDDEN_OPACITY = 0.25;

    // opacity state
    private boolean isInScreenCenter = false;
    private double currentOpacity = 1.0;

    private @Nullable Player player;
    private @Nullable SpriteRenderer renderer;

    public SeeThroughComponent setPlayer(@Nullable Player player)
    {
        this.player = player;
        return this;
    }

    /* LIFETIME */

    @Override
    public void onInit()
    {
        renderer = getGameObject().getFirstComponent(SpriteRenderer.class);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        // TODO: Replace with bounding box
        if (player != null) isInScreenCenter = this.gameObject.getTransform()
                .getPosition()
                .subtract(player.getTransform().getPosition())
                .magnitude() < PLAYER_DETECTION_RADIUS;

        currentOpacity = lerpOpacity(currentOpacity, isInScreenCenter ? HIDDEN_OPACITY : DEFAULT_OPACITY, deltaTime);
    }

    @Override
    public void onRender(GraphicsContext context)
    {
        if (renderer != null) renderer.setOpacity(this.currentOpacity);
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
