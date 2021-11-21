package ru.ugryumov;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import ru.ugryumov.screen.impl.MenuScreen;

public class SpaceBattle extends Game {

	@Override
	public void create() {
		setScreen(new MenuScreen());
	}
}
