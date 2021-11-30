package ru.ugryumov.sprite.impl;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.sprite.Sprite;
/**
 * Управление с клавиатуры
 * 21 - влево
 * 19 - вверх
 * 22 - вправо
 * 20 - вниз
 *
 * */
public class BattleShip extends Sprite {

    private static final float HEIGHT = 0.25f;
    private static final float MARGIN = 0.03f;

    private Rect worldBounds;
    private Vector2 v_speed;

    public BattleShip(TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("main_ship"), 0, 0, 195, 287));
        v_speed = new Vector2();
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        setHeightProportion(HEIGHT);
        setBottom(worldBounds.getBottom() + MARGIN);
    }

    @Override
    public void update(float delta) {
        this.pos.add(v_speed);
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

    public void movementStart(int keycode) {
        switch (keycode) {
            case (19) : //стрелка вверх
                v_speed.set(0f, 0.005f);
                break;
            case (20) : //стрелка вниз
                v_speed.set(0f, -0.005f);
                break;
            case (21) : //стрелка влева
                v_speed.set(-0.005f, 0f);
                break;
            case (22) : //стрелка вправо
                v_speed.set(0.005f, 0f);
                break;
        }
    }

    public void movementStop() {
        v_speed.set(0f,0f);
    }
}
