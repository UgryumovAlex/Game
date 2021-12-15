package ru.ugryumov.sprite.impl;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.pool.impl.BulletPool;
import ru.ugryumov.pool.impl.ExplosionPool;
import ru.ugryumov.sprite.Ship;

public class BattleShip extends Ship {

    private static final float HEIGHT = 0.1f;
    private static final float MARGIN = 0.03f;
    private static final float SHOOTING_INTERVAL = 0.2f;
    private static final float SPEED = 0.2f;

    private static final int INVALID_POINTER = -1;
    private static final byte STOP = 0;
    private static final byte LEFT = 1;
    private static final byte RIGHT = 2;
    private static final byte UP = 3;
    private static final byte DOWN = 4;

    private static final int BATTLE_SHIP_HP = 100;

    private boolean pressedLeft;
    private boolean pressedRight;
    private boolean pressedUp;
    private boolean pressedDown;

    private int leftPointer = INVALID_POINTER;
    private int rightPointer = INVALID_POINTER;

    public BattleShip(TextureAtlas atlas, ExplosionPool explosionPool, BulletPool bulletPool, Sound bulletSound) {
        super(atlas.findRegion("main_ship"), 1, 2, 2);
        this.v_speed = new Vector2();
        this.v0 = SPEED;
        this.explosionPool = explosionPool;
        this.bulletPool = bulletPool;
        this.bulletSound = bulletSound;
        this.bulletRegion = atlas.findRegion("bulletMainShip");
        this.bulletV = new Vector2(0, 0.5f);
        this.bulletHeight = 0.01f;
        this.damage = 1;
        this.reloadTimer = 0;
        this.reloadInterval = SHOOTING_INTERVAL;
        this.auto_shooting = false;
        this.hp = BATTLE_SHIP_HP;
    }

    /**Возвращаем корабль обратно в игру*/
    public void returnToGame() {
        this.hp = BATTLE_SHIP_HP;
        this.pos.set(0, worldBounds.getBottom() + MARGIN);
        flushDestroy();
        move(STOP);
        leftPointer = INVALID_POINTER;
        rightPointer = INVALID_POINTER;
        pressedLeft = false;
        pressedRight = false;
        pressedUp = false;
        pressedDown = false;
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        setHeightProportion(HEIGHT);
        setBottom(worldBounds.getBottom() + MARGIN);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        checkBounds();
    }

    private void checkBounds() {
        if (getLeft() < worldBounds.getLeft()) {
            setLeft(worldBounds.getLeft());
        }

        if (getRight() > worldBounds.getRight()) {
            setRight(worldBounds.getRight());
        }

        if (getTop() > worldBounds.getTop()) {
            setTop(worldBounds.getTop());
        }

        if (getBottom() < worldBounds.getBottom()) {
            setBottom(worldBounds.getBottom());
        }
    }

    private void move(byte direction) {
        switch (direction) {
            case (UP)    : v_speed.set(0f, v0);
                break;
            case (DOWN)  : v_speed.set(0f, -v0);
                break;
            case (LEFT)  : v_speed.set(-v0, 0f);
                break;
            case (RIGHT) : v_speed.set(v0, 0f);
                break;
            case (STOP)  : v_speed.set(0f,0f);
                break;
        }
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (isMe(touch)) {
            shoot();
            auto_shooting = !auto_shooting;
        } else {
            if (touch.x < worldBounds.pos.x) {
                if (leftPointer != INVALID_POINTER) {
                    return false;
                }
                leftPointer = pointer;
                move(LEFT);
            } else {
                if (rightPointer != INVALID_POINTER) {
                    return false;
                }
                rightPointer = pointer;
                move(RIGHT);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (pointer == leftPointer) {
            leftPointer = INVALID_POINTER;
            if (rightPointer != INVALID_POINTER) {
                move(RIGHT);
            } else {
                move(STOP);
            }
        } else if (pointer == rightPointer) {
            rightPointer = INVALID_POINTER;
            if (leftPointer != INVALID_POINTER) {
                move(LEFT);
            } else {
                move(STOP);
            }
        }
        return false;
    }

    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = true;
                move(LEFT);
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = true;
                move(RIGHT);
                break;
            case Input.Keys.UP:
            case Input.Keys.W:
                pressedUp = true;
                move(UP);
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                pressedDown = true;
                move(DOWN);
                break;

            case Input.Keys.SPACE:
                shoot();
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = false;
                if (pressedRight) {
                    move(RIGHT);
                } else if (pressedDown) {
                    move(DOWN);
                } else if (pressedUp) {
                    move(UP);
                } else {
                    move(STOP);
                }
                break;

            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = false;
                if (pressedLeft) {
                    move(LEFT);
                } else if (pressedDown) {
                    move(DOWN);
                } else if (pressedUp) {
                    move(UP);
                } else {
                    move(STOP);
                }
                break;

            case Input.Keys.W:
            case Input.Keys.UP:
                pressedUp = false;
                if (pressedLeft) {
                    move(LEFT);
                } else if (pressedRight) {
                    move(RIGHT);
                } else if (pressedDown) {
                    move(DOWN);
                } else {
                    move(STOP);
                }
                break;

            case Input.Keys.S:
            case Input.Keys.DOWN:
                pressedDown = false;
                if (pressedLeft) {
                    move(LEFT);
                } else if (pressedRight) {
                    move(RIGHT);
                } else if (pressedUp) {
                    move(UP);
                } else {
                    move(STOP);
                }
                break;
        }
        return false;
    }

    public boolean isBulletCollision(Bullet bullet) {
        return !(
                bullet.getRight() < getLeft()
                        || bullet.getLeft() > getRight()
                        || bullet.getBottom() > pos.y
                        || bullet.getTop() < getBottom()
        );
    }

    public void addHPBonus(int hpBonus) {
        this.hp += hpBonus;
    }
}
