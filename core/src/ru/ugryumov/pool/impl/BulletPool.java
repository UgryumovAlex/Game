package ru.ugryumov.pool.impl;

import ru.ugryumov.pool.SpritesPool;
import ru.ugryumov.sprite.impl.Bullet;

public class BulletPool extends SpritesPool<Bullet> {
    @Override
    protected Bullet newObject() {
        return new Bullet();
    }
}
