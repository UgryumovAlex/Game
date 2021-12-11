package ru.ugryumov.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.pool.impl.BulletPool;
import ru.ugryumov.pool.impl.ExplosionPool;
import ru.ugryumov.sprite.impl.Bullet;
import ru.ugryumov.sprite.impl.Explosion;

public class Ship extends Sprite {

    private static final float DAMAGE_ANIMATE_INTERVAL = 0.1f;

    protected Vector2 v_speed; //вектор перемещения
    protected float   v0;      //скорость

    protected ExplosionPool explosionPool;

    //Параметры оружия корабля
    protected BulletPool bulletPool;
    protected TextureRegion bulletRegion;
    protected Vector2 bulletV;
    protected float bulletHeight;
    protected Sound bulletSound;
    protected int damage;
    protected float reloadTimer;
    protected float reloadInterval;
    protected boolean auto_shooting = true; //по умолчанию автострельба включена

    protected Rect worldBounds;

    protected int hp; //живучесть корабля

    protected float occurenceBoost; //Ускорение при появлении на игровом поле

    private float damageAnimateTimer = DAMAGE_ANIMATE_INTERVAL;

    public Ship() {
    }

    public Ship(TextureRegion region, int rows, int cols, int frames) {
        super(region, rows, cols, frames);
    }

    @Override
    public void update(float delta) {
        if (this.getTop() > worldBounds.getTop()) {
            pos.mulAdd(v_speed, delta*occurenceBoost); //Ускоряемся для появлении на игровом поле
        } else {
            pos.mulAdd(v_speed, delta);
            reloadTimer += delta;
            if (reloadTimer > reloadInterval) {
                reloadTimer = 0f;
                if (auto_shooting) {
                    shoot();
                }
            }
        }

        damageAnimateTimer += delta;
        if (damageAnimateTimer >= DAMAGE_ANIMATE_INTERVAL) {
            frame = 0;
        }
    }

    protected void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, bulletHeight, worldBounds, damage);
        bulletSound.play();
    }

    public void damage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            destroy();
        }

        damageAnimateTimer = 0f;
        frame = 1;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public void destroy() {
        super.destroy();
        boom();
    }

    public void clear() {
        super.destroy();
    }

    private void boom() {
        Explosion explosion = explosionPool.obtain();
        explosion.set(pos, getHeight());
    }
}
