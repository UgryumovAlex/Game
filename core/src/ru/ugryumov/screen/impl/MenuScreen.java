package ru.ugryumov.screen.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import ru.ugryumov.screen.BaseScreen;

public class MenuScreen extends BaseScreen {
    private Texture img;
    private Vector2 v_position; //Текущая позиция
    private Vector2 v_newPosition; //Точка, в которую движемся
    private Vector2 v_direction; //Вектор направления
    private int     moveCycles; //Рассчитаное количество циклов перемещения до нового месторасположения

    @Override
    public void show() {
        super.show();
        img = new Texture("backgroundSM.jpg");
        v_position    = new Vector2();
        v_newPosition = new Vector2();
        moveCycles = 0;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        batch.begin();
        batch.draw(img, v_position.x, v_position.y);
        batch.end();

        if (v_direction != null) { //Двигаемся, если задан вектор направления
            if (moveCycles > 0) { //Двигаемся, пока счётчик циклов перемещения ненулевой
                v_position.add(v_direction);
                moveCycles--;
            } else {
                v_direction = null; //Пришли в заданную позицию, обнуляем вектор направления
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        img.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        v_newPosition.set(screenX, Gdx.graphics.getHeight() - screenY);
        v_direction = v_newPosition.cpy().sub(v_position);
        v_direction.nor();

        moveCycles = Math.round( v_newPosition.cpy().sub(v_position).len());

        return super.touchDown(screenX, screenY, pointer, button);
    }
}
