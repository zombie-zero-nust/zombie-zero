package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class BulletImpact extends GameObject
{
    private static final TimeSpan ANIMATION_TIME = TimeSpan.fromMilliseconds(50);

    private final ImpactType type;

    private final long startTimeMs;

    public BulletImpact(Vector2D position, ImpactType type)
    {
        this.getTransform().setPosition(position.copy());
        this.getTransform().rotateDegrees(ThreadLocalRandom.current().nextDouble() * 360);
        this.type = type;
        this.startTimeMs = System.currentTimeMillis();
    }

    @Override
    public void onInit()
    {
        try
        {
            Image image = type.getImage().orElseThrow();
            SpriteRenderer renderer = new SpriteRenderer(
                    image.getWidth(), image.getHeight(), //
                    image, type.getFrameCountX(), 1
            );
            renderer.setOpacity(ThreadLocalRandom.current().nextDouble() * 0.25 + 0.25);
            renderer.setSize(image.getWidth() / type.getFrameCountX(), image.getHeight());
            renderer.setAnimating(true);
            renderer.setAnimationTime(ANIMATION_TIME);
            this.addComponent(renderer);
        }
        catch (Exception e)
        {
            logger.warn("Bullet impact error: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        long elapsedTime = System.currentTimeMillis() - startTimeMs;
        if (elapsedTime >= ANIMATION_TIME.multiply(type.getFrameCountX()).asMilliseconds())
        {
            this.destroy();
        }
    }

    public enum ImpactType
    {
        BLOOD(3, "scenes", "LevelScene", "impacts", "blood.png"),
        ENVIRONMENT(3, "scenes", "LevelScene", "impacts", "env.png");

        private final int frameCountX;
        private final @Nullable Image image;

        ImpactType(int frameCountX, String... path)
        {
            this.frameCountX = frameCountX;
            Image img;
            try { img = Resources.loadImageOrThrow(path); }
            catch (FileNotFoundException ignored) { img = null; }
            this.image = img;
        }

        public int getFrameCountX() { return frameCountX; }

        public Optional<Image> getImage() { return Optional.ofNullable(image); }
    }
}
