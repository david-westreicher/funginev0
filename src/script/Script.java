package script;

import java.util.HashMap;
import java.util.Map;

import game.Game;
import input.Input;
import io.IO;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import util.Factory;
import util.Log;

public class Script {

	public static Map<String, Invocable> scripts = new HashMap<String, Invocable>();

	public static void execute(String script, Object... vars)
			throws ScriptException {
		ScriptEngine engine = getEngine();
		for (int i = 0; i < vars.length; i += 2)
			engine.put((String) vars[i], vars[i + 1]);
		engine.eval(IO.read(script));
	}

	public static void executeFunction(String script, String func,
			Object... vars) throws ScriptException, NoSuchMethodException {
		Invocable inv = scripts.get(script);
		if (inv == null) {
			ScriptEngine engine = getEngine();
			engine.put("game", Game.INSTANCE);
			engine.put("factory", Factory.INSTANCE);
			engine.put("log", new Log());
			Object objects = engine.eval(IO.read(script));
			inv = (Invocable) engine;
			scripts.put(script, inv);
		}
		inv.invokeFunction(func, vars);
	}

	private static ScriptEngine getEngine() {
		ScriptEngineManager engineMgr = new ScriptEngineManager();
		ScriptEngine engine = engineMgr.getEngineByName("JavaScript");
		return engine;
	}

	public static void compile(String scriptFile) throws ScriptException {
		ScriptEngine engine = getEngine();
		CompiledScript script = ((Compilable) engine).compile(IO
				.read(scriptFile));
		// engine.
	}

	public static void restart() {
		scripts.clear();
	}
}
