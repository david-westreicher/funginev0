package test;

import java.io.File;

import settings.Settings;
import util.Log;
import util.Util;


import game.Game;

public class OpenGLTest {

	public static void main(String[] args) {
		Settings.RESSOURCE_FOLDER = args[0]+ File.separator;
		new Game();
	}


}
