package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bulletphysics.collision.shapes.CollisionShape;

import rendering.GameObjectRenderer;
import script.GameScript;
import util.Log;

import game.Component;
import game.Updatable;

public class GameObjectType extends VariableHolder {
	private static Map<String, GameObjectType> allTypes = new HashMap<String, GameObjectType>();

	public GameObjectRenderer renderer = null;
	public GameScript script = null;
	public CollisionShape shape = null;
	public String name;

	public GameObjectType(String name) {
		allTypes.put(name, this);
		this.name = name;
	}

	public static GameObjectType getType(String name) {
		return allTypes.get(name);
	}

	public String toString() {
		return name + ", " + script + ", " + renderer;
	}

}
