package game;

import java.util.List;
import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import physics.PhysicsTest;

import script.GameScript;
import script.Script;
import settings.Settings;
import util.Log;

import world.GameObject;
import world.GameObjectType;
import world.World;

public class GameMechanics implements Updatable {

	/**
	 * @uml.property name="physics"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	public PhysicsTest physics;

	public GameMechanics() {
	}

	@Override
	public void dispose() {
		if (physics != null)
			physics.dispose();
	}

	@Override
	public void update(float interp) {

		Map<String, List<GameObject>> objs = Game.INSTANCE.world
				.getAllObjects();
		int tick = Game.INSTANCE.loop.tick;

		Game.INSTANCE.cam.beforeUpdate();
		for (String type : objs.keySet()) {
			for (GameObject go : objs.get(type)) {
				go.beforeUpdate();
			}
		}

		try {
			Script.executeFunction(Settings.MAIN_SCRIPT, "update",
					Game.INSTANCE);
		} catch (ScriptException e1) {
			Log.err(e1.getFileName());
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}

		for (String type : objs.keySet()) {
			GameObjectType goType = GameObjectType.getType(type);
			GameScript script = goType.script;
			CompiledScript cScript = null;
			if (script != null) {
				cScript = script.script;
				for (GameObject go : objs.get(type)) {
					cScript.getEngine().put("object", go);
					try {
						cScript.eval();
					} catch (ScriptException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		}
		if (physics != null)
			physics.update(objs);

		if (Game.DEBUG)
			for (String type : objs.keySet()) {
				for (GameObject go : objs.get(type)) {
					go.updateBbox();
				}
			}

		Game.INSTANCE.input.mouse.update();
	}

}
