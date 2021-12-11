package ru.ugryumov.sprite.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.pool.impl.BulletPool;
import ru.ugryumov.pool.impl.ExplosionPool;
import ru.ugryumov.sprite.Ship;

public class EnemyShip extends Ship {

    public EnemyShip(ExplosionPool explosionPool, BulletPool bulletPool, Sound bulletSound, Rect worldBounds) {
        this.explosionPool = explosionPool;
        this.bulletPool = bulletPool;
        this.bulletSound = bulletSound;
        this.worldBounds = worldBounds;
        this.v_speed = new Vector2();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (getBottom() < worldBounds.getBottom()) {
            destroy();
        }
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v,
            TextureRegion bulletRegion,
            float bulletHeight,
            Vector2 bulletV,
            int damage,
            float reloadInterval,
            float height,
            int hp,
            float occurenceBoost
    ) {
        this.regions = regions;
        this.v_speed.set(v);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletV = bulletV;
        this.damage = damage;
        this.reloadInterval = reloadInterval;
        this.reloadTimer = reloadInterval - Math.abs(v.y); //Пусть вражеский корабль стреляет сразу
        setHeightProportion(height);
        this.hp = hp;
        this.occurenceBoost = occurenceBoost;
    }

    public boolean isBulletCollision(Bullet bullet) {
        return !(
                     bullet.getRight() < getLeft()
                  || bullet.getLeft() > getRight()
                  || bullet.getBottom() > getTop()
                  || bullet.getTop() < pos.y
                );
    }
}
