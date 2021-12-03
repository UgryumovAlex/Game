package ru.ugryumov.screen.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.pool.impl.BulletPool;
import ru.ugryumov.screen.BaseScreen;
import ru.ugryumov.sprite.impl.Background;
import ru.ugryumov.sprite.impl.BattleShip;
import ru.ugryumov.sprite.impl.Star;

public class GameScreen extends BaseScreen {

    private static final int STAR_COUNT = 256;

    private Texture bg;
    private Background background;

    private TextureAtlas atlas;
    private Star[] stars;
    private BattleShip battleShip;

    private BulletPool bulletPool;

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/bg.png");
        background = new Background(bg);

        atlas = new TextureAtlas("textures/mainAtlas.tpack");

        stars = new Star[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(atlas);
        }

        bulletPool = new BulletPool();

        battleShip = new BattleShip(atlas, bulletPool);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        freeAllDestroyed();
        draw();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star star : stars) {
            star.resize(worldBounds);
        }
        battleShip.resize(worldBounds);
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        battleShip.touchDown(touch, pointer, button);
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        battleShip.touchUp(touch, pointer, button);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        battleShip.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        battleShip.keyUp(keycode);
        return false;
    }

    private void update(float delta) {
        for (Star star : stars) {
            star.update(delta);
        }
        battleShip.update(delta);
        bulletPool.updateActiveSprites(delta);
    }

    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyed();
    }

    private void draw() {
        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        battleShip.draw(batch);
        bulletPool.drawActiveSprites(batch);
        batch.end();
    }

}
