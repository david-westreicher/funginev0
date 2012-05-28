package game;

import input.Input;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;
import javax.swing.SwingUtilities;

import experiment.OpenVC;

import manager.Manageable;
import manager.Manager;
import manager.ScriptManager;
import manager.SpriteManager;
import rendering.DOFRenderer;
import rendering.RenderUpdater;
import rendering.SimpleRenderer;
import rendering.SpriteRenderer;
import rendering.TestUpdater;
import script.Script;
import settings.Settings;
import util.Factory;
import util.FolderWatcher;
import util.Log;
import util.Stoppable;
import util.Util;
import util.XMLToObjectParser;
import world.Camera;
import world.GameObjectType;
import world.World;

public class Game {
	public static boolean WIREFRAME = false;
	public static Game INSTANCE;
	public GameLoop loop = new GameLoop();
	public World world;
	public Input input = new Input();
	public static boolean DEBUG = true;
	public Camera cam = new Camera();
	public Map<String, Float> booleans = new HashMap<String, Float>();
	private Robot r;

	public Game() {
		INSTANCE = this;
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		FolderWatcher f = new FolderWatcher(Settings.RESSOURCE_FOLDER);
		f.addFolderListener(new GameWatcher(this));
		f.start();
		loop.startPause();
		loop.start();
		Util.sleep(100);
		start();
	}

	public void parseXML() {
		new XMLToObjectParser().parse();
	}

	public void restart() {
		Log.log(this, "Restarting!");
		// TODO restart the whole shit
		Manager.restartManager();
		Util.sleep(10);
		loop.startPause();
		loop.exit();
		Script.restart();
		SpriteRenderer.createList();
		Util.sleep(100);
		start();
	}

	private void start() {
		world = new World();
		parseXML();
		try {
			Script.executeFunction(Settings.INIT_SCRIPT, "init", this,
					Factory.INSTANCE);
		} catch (ScriptException e) {
			loop.startPause();
			System.err.println("Couldn't parse the init script!");
			System.err.println(e.getMessage());
		} catch (NoSuchMethodException e) {
			loop.startPause();
			System.err.println("Couldn't parse the init script!");
			System.err.println(e.getMessage());
		}
		try {
			Script.executeFunction(Settings.MAIN_SCRIPT, "init", this,
					Factory.INSTANCE);
		} catch (ScriptException e) {
			loop.startPause();
			System.err.println("Couldn't parse the main script!");
			System.err.println(e.getMessage());
		} catch (NoSuchMethodException e) {
			loop.startPause();
			System.err.println("Couldn't parse the main script!");
			System.err.println(e.getMessage());
		}
		Util.sleep(1000);
		loop.endPause();
	}

	public void addComponent(String c) {
		c = c.toLowerCase();
		if (c.equals("renderer")) {
			loop.renderer = new SimpleRenderer();
		} else if (c.equals("gamemechanics")) {
			loop.mechanics = new GameMechanics();
		}
	}

	public void exit() {
		Stoppable.stopAll();
		loop.exit();
	}

	public Manageable getManager(String name) {
		return Manager.getManager(name);
	}

	public void setBoolean(String s, float b) {
		booleans.put(s, b);
	}

	public float getBoolean(String s) {
		Float f = booleans.get(s);
		if (f == null)
			return 0;
		return f;
	}

	public void hideMouse() {
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		Util.c.setCursor(blankCursor);
	}

	public void centerMouse() {
		Point pt = new Point(Util.c.getLocation());
		SwingUtilities.convertPointToScreen(pt, Util.c);
		r.mouseMove((int) pt.getX() + Settings.WIDTH / 2, (int) pt.getY()
				+ Settings.HEIGHT / 2);
	}

}
