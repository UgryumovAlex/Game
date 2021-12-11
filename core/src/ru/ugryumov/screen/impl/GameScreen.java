package ru.ugryumov.screen.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.math.Rect;
import ru.ugryumov.pool.impl.BulletPool;
import ru.ugryumov.pool.impl.EnemyPool;
import ru.ugryumov.pool.impl.ExplosionPool;
import ru.ugryumov.screen.BaseScreen;
import ru.ugryumov.sprite.impl.Background;
import ru.ugryumov.sprite.impl.BattleShip;
import ru.ugryumov.sprite.impl.Bullet;
import ru.ugryumov.sprite.impl.EnemyShip;
import ru.ugryumov.sprite.impl.GameOver;
import ru.ugryumov.sprite.impl.Star;
import ru.ugryumov.util.EnemyEmitter;

public class GameScreen extends BaseScreen {

    private static final int STAR_COUNT = 64;

    private Texture bg;
    private Background background;
    private GameOver gameOver;

    private TextureAtlas atlas;
    private Star[] stars;
    private BattleShip battleShip;

    private ExplosionPool explosionPool;
    private BulletPool bulletPool;
    private EnemyPool enemyPool;

    private Sound laserSound;
    private Sound bulletSound;
    private Sound explosionSound;

    private EnemyEmitter enemyEmitter;

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/bg.png");
        background = new Background(bg);
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));

        gameOver = new GameOver(atlas);

        explosionPool = new ExplosionPool(atlas, explosionSound);
        bulletPool = new BulletPool();
        enemyPool = new EnemyPool(explosionPool, bulletPool, bulletSound, worldBounds);

        stars = new Star[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(atlas);
        }

        battleShip = new BattleShip(atlas, explosionPool, bulletPool, laserSound);
        enemyEmitter = new EnemyEmitter(atlas, worldBounds, enemyPool);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);

        if (!battleShip.isDestroyed()) {
            checkShipCollisions();
            checkBulletCollisions();
        }

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
        gameOver.resize(worldBounds);
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        atlas.dispose();

        explosionPool.dispose();
        bulletPool.dispose();
        enemyPool.dispose();

        laserSound.dispose();
        bulletSound.dispose();
        explosionSound.dispose();
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
       if (!battleShip.isDestroyed()) {
            battleShip.update(delta);
            bulletPool.updateActiveSprites(delta);
            enemyPool.updateActiveSprites(delta);
            enemyEmitter.generate(delta);
       }

        explosionPool.updateActiveSprites(delta);
    }

    /*Проверка столкновения кореблей. Враг уничтожается*/
    private void checkShipCollisions() {

        for ( EnemyShip enemyShip : enemyPool.getActiveObjects() ) {
            if (enemyShip.isDestroyed()) {
                continue;
            }
            float minDist = (battleShip.getWidth() + enemyShip.getWidth()) * 0.5f;
            if (battleShip.pos.dst(enemyShip.pos) < minDist) {
                battleShip.damage(enemyShip.getHp());
                enemyShip.destroy();
            }
        }
    }

    private void checkBulletCollisions() {
        for ( Bullet bullet : bulletPool.getActiveObjects()) {
            if (bullet.isDestroyed()) {
                continue;
            }

            if (bullet.getOwner() != battleShip ) {
                if (battleShip.isBulletCollision(bullet)) {
                    battleShip.damage(bullet.getDamage());
                    bullet.destroy();
                }
                continue;
            }

            for ( EnemyShip enemyShip : enemyPool.getActiveObjects() ) {
                if (enemyShip.isDestroyed()) {
                    continue;
                }
                if (enemyShip.isBulletCollision(bullet)) {
                    enemyShip.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }
    }

    private void freeAllDestroyed() {
        explosionPool.freeAllDestroyed();
        bulletPool.freeAllDestroyed();
        enemyPool.freeAllDestroyed();
    }

    private void draw() {
        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        if (!battleShip.isDestroyed()) {
            battleShip.draw(batch);
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
        } else {
            gameOver.draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
        batch.end();
    }

}
