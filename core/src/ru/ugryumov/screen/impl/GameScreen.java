package ru.ugryumov.screen.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import ru.ugryumov.font.Font;
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
import ru.ugryumov.sprite.impl.NewGame;
import ru.ugryumov.sprite.impl.Star;
import ru.ugryumov.util.EnemyEmitter;

public class GameScreen extends BaseScreen {

    private static final int STAR_COUNT = 64;
    private static final float MARGIN = 0.01f;
    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";
    private static final String BONUS = "bonus +10 HP";

    private Texture bg;
    private Background background;

    private GameOver gameOver;
    private NewGame newGame;

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

    private int frags;
    private Font font;
    private StringBuilder sbFrags;
    private StringBuilder sbHP;
    private StringBuilder sbLevel;

    private Vector2 bonusPos;
    private boolean showBonus = false;

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/earth.png");

        background = new Background(bg);

        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));

        font = new Font("font/font.fnt", "font/font.png");
        font.setSize(0.02f);
        sbFrags = new StringBuilder();
        sbHP = new StringBuilder();
        sbLevel = new StringBuilder();

        gameOver = new GameOver(atlas);
        newGame = new NewGame(atlas);

        explosionPool = new ExplosionPool(atlas, explosionSound);
        bulletPool = new BulletPool();
        enemyPool = new EnemyPool(explosionPool, bulletPool, bulletSound, worldBounds);

        stars = new Star[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(atlas);
        }

        battleShip = new BattleShip(atlas, explosionPool, bulletPool, laserSound);
        enemyEmitter = new EnemyEmitter(atlas, worldBounds, enemyPool);

        frags = 0;
        bonusPos = new Vector2();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);

        if (!battleShip.isDestroyed()) {
            checkShipCollisions();
            checkBulletCollisions();
            checkBonusVisible();
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
        newGame.resize(worldBounds);

        bonusPos.set(worldBounds.pos.x, worldBounds.getTop()-25*MARGIN);
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

    /**Для новой игры убираем все пули и вражеские корабли*/
    private void refreshGame() {
        frags = 0;

        for (EnemyShip enemyShip : enemyPool.getActiveObjects()) {
            enemyShip.clear();
        }
        enemyPool.freeAllDestroyed();

        for (Bullet bullet : bulletPool.getActiveObjects()) {
            bullet.destroy();
        }
        bulletPool.freeAllDestroyed();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        battleShip.touchDown(touch, pointer, button);
        if (newGame.touchDown(touch, pointer, button)) { //Если нажали на NewGame, стартуем новую игру
            refreshGame();
            battleShip.returnToGame();
        }
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

            if (showBonus) {
                bonusPos.y += 0.35f * MARGIN;
            }

           /*Добавляем бонус HP, если перешли на новый уровень начиная со второго*/
            if ((enemyEmitter.getLevel() < (frags/10+1)) && (frags > 10)) {
                battleShip.addHPBonus(10);
                showBonus = true;
                bonusPos.y = worldBounds.getTop()-25*MARGIN;
            }
            enemyEmitter.generate(delta, frags);
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
                    if (enemyShip.isDestroyed()) {
                        frags++;
                    }
                    bullet.destroy();
                }
            }
        }
    }

    private void checkBonusVisible() {
        if (bonusPos.y > worldBounds.getTop()) {
            showBonus = false;
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
//        powerShield.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        if (!battleShip.isDestroyed()) {
            battleShip.draw(batch);
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
        } else {
            gameOver.draw(batch);
            newGame.draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
        printInfo();
        if (showBonus) {
            printBonus(bonusPos);
        }
        batch.end();
    }

    private void printInfo() {
        sbFrags.setLength(0);
        font.draw(batch, sbFrags.append(FRAGS).append(frags),
                worldBounds.getLeft() + MARGIN, worldBounds.getTop() - MARGIN);
        sbHP.setLength(0);
        font.draw(batch, sbHP.append(HP).append(battleShip.getHp()),
                worldBounds.pos.x, worldBounds.getTop() - MARGIN, Align.center);
        sbLevel.setLength(0);
        font.draw(batch, sbLevel.append(LEVEL).append(enemyEmitter.getLevel()),
                worldBounds.getRight() - MARGIN, worldBounds.getTop() - MARGIN, Align.right);
    }

    private void printBonus(Vector2 bonusPos) {
        font.draw(batch, BONUS, bonusPos.x, bonusPos.y, Align.center);
    }

}
