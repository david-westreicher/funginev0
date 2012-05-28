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

	/**
	 * @uml.property  name="renderer"
	 * @uml.associationEnd  
	 */
	public GameObjectRenderer renderer = null;
	/**
	 * @uml.property  name="script"
	 * @uml.associationEnd  
	 */
	public GameScript script = null;
	/**
	 * @uml.property  name="shape"
	 * @uml.associationEnd  
	 */
	public CollisionShape shape = null;
	/**
	 * @uml.property  name="name"
	 */
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
