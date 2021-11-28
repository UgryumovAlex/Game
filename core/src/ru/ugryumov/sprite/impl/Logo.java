package ru.ugryumov.sprite.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.sprite.Sprite;

public class Logo extends Sprite {

    private Vector2 newPos;
    private Vector2 direction;
    private final float V_LEN = 0.01f;

    public Logo(Texture texture) {
        super(new TextureRegion(texture));
        newPos = new Vector2();
        direction = new Vector2();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setHeightProportion(0.1f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (pos.dst(newPos) > V_LEN) {
            pos.add(direction);
        } else {
            pos.set(newPos);
        }
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        newPos.set(touch);
        direction.set(newPos.cpy().sub(pos)).setLength(V_LEN);
        return false;
    }
}
