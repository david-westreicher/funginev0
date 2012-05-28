package game;

import javax.script.ScriptException;

import manager.ScriptManager;
import manager.SpriteManager;
import script.Script;
import settings.Settings;
import util.Factory;
import util.FolderListener;
import util.Log;
import util.Util;
import world.GameObjectType;
import world.World;

public class GameWatcher implements FolderListener {

	/**
	 * @uml.property  name="game"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Game game;

	public GameWatcher(Game g) {
		this.game = g;
	}

	@Override
	public void added(String s) {
	}

	@Override
	public void changed(String s) {
		Log.log(this, s + " changed");
		if (s.equals(Settings.INIT_SCRIPT)) {
			game.restart();
		} else if (s.equals(Settings.MAIN_SCRIPT)) {
			Script.scripts.remove(Settings.MAIN_SCRIPT);
			GameLoop gl = game.loop;
			gl.startPause();
			Util.sleep(10);
			Game.INSTANCE.world = new World();
			try {
				Script.executeFunction(Settings.MAIN_SCRIPT, "init",
						Game.INSTANCE, Factory.INSTANCE);
			} catch (ScriptException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			gl.endPause();
		}
		if (s.equals(Settings.OBJECTS_XML)) {
			GameLoop gl = game.loop;
			gl.startPause();
			Util.sleep(10);
			((SpriteManager) game.getManager("sprite")).textures.clear();
			game.parseXML();
			gl.endPause();
		} else {
			String folder = s.split("\\\\")[0];
			if (folder.equals("scripts")) {
				game.getManager("script").changed(s);
			} else if (folder.equals("img")) {
				game.getManager("sprite").changed(s);
			} else if (folder.equals("shader")) {
				game.getManager("shader").changed(s);
			}
		}
	}

	@Override
	public void removed(String s) {

	}

}
