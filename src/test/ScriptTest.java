package test;

import java.util.List;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import util.Log;

public class ScriptTest {
	/**
	 * @uml.property  name="script"
	 */
	private String script = "var x=0;function count(){x++;inst.lol(x);}";

	public ScriptTest() throws NoSuchMethodException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		engine.put("counter", 0);
		engine.put("inst", this);
		try {
			engine.eval(script);
			Invocable inv = (Invocable) engine;
			for (int i = 0; i < 10; i++)
				inv.invokeFunction("count");

			Compilable c = (Compilable) engine;
			CompiledScript cscript = c.compile(script + ";count()");
			for (int i = 0; i < 10; i++)
				cscript.eval();
		} catch (ScriptException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new ScriptTest();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void lol(Object o) {
		System.out.println(o);
	}

}
