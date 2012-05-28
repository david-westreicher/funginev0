package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.Game;
import io.IO;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import script.GameScript;
import util.Factory;
import util.Log;

public class ScriptManager extends Manager<GameScript> {

	public ScriptManager() {
		super("script");
	}

	@Override
	protected void updateObject(GameScript gs, String file) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			// engine put....
			engine.put("log", new Log());
			engine.put("game", Game.INSTANCE);
			engine.put("factory", Factory.INSTANCE);
			Compilable compEngine = (Compilable) engine;
			gs.script = compEngine.compile(IO.read(file));
		} catch (ScriptException e) {
			gs.script = null;
		}
	}
}
