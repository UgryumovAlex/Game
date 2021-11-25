package ru.ugryumov.sprite.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.ugryumov.math.Rect;
import ru.ugryumov.sprite.Sprite;

public class Background extends Sprite {

    public Background(Texture texture) {
        super(new TextureRegion(texture));
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(worldBounds.getHeight());
        this.pos.set(worldBounds.pos);
    }
}
