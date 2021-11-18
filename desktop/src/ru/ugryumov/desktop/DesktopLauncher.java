package ru.ugryumov.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.ugryumov.SpaceBattle;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 450;
		config.height = 600;
		new LwjglApplication(new SpaceBattle(), config);
	}
}
