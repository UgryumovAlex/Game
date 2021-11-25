package ru.ugryumov;

import com.badlogic.gdx.Game;


import ru.ugryumov.screen.impl.MenuScreen;

public class SpaceBattle extends Game {

	@Override
	public void create() {
		setScreen(new MenuScreen());
	}
}
