package edu.nust.engine.core.gameobjects;

import edu.nust.engine.core.GameObject;


/**
 * A tag itself is as identifier
 * <br>
 * <br>
 * No instance, use the class itself as an identifier
 * <br>
 * e.g. {@code gameObject.addTag(PlayerTag.class);} etc.
 *
 * @see GameObject#addTag(Class)
 * @see GameObject#removeTag(Class)
 * @see GameObject#hasTag(Class)
 */
public abstract class Tag
{
    /**
     * No instance, use the class itself as an identifier
     * <br>
     * e.g. {@code gameObject.addTag(PlayerTag.class);} etc.
     *
     * @see GameObject#addTag(Class)
     * @see GameObject#removeTag(Class)
     * @see GameObject#hasTag(Class)
     */
    protected Tag() { }
    // protected constructor to prevent instance but allow subclasses
}
