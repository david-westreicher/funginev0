package test;

import game.Game;

import java.io.File;

import reflection.Reflection;
import settings.Settings;
import util.Log;
import util.Util;
import browser.AwesomiumHelper;

public class OpenGLTest {

	public static void main(String[] args) {
		if (args.length > 0)
			Settings.RESSOURCE_FOLDER = args[0] + File.separator;
		Log.log(OpenGLTest.class, "Ressource folder is: "
				+ Settings.RESSOURCE_FOLDER);

		Log.getInstance().excludeFromLogging(Reflection.class);
		Log.getInstance().excludeFromLogging(AwesomiumHelper.class);
		Game g = new Game();
		Util.sleep(500);
		g.start();
	}
}
