package ru.ugryumov.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.pool.impl.BulletPool;
import ru.ugryumov.sprite.impl.Bullet;

public class Ship extends Sprite {

    protected Vector2 v_speed; //вектор перемещения
    protected float   v0;      //скорость

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
    }

    protected void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, bulletHeight, worldBounds, damage);
        bulletSound.play();
    }
}
