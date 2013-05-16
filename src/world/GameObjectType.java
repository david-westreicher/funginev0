package world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rendering.GameObjectRenderer;
import rendering.RenderState;
import script.GameScript;

import com.bulletphysics.collision.shapes.CollisionShape;

public class GameObjectType extends VariableHolder {
	private static Map<String, GameObjectType> allTypes = new HashMap<String, GameObjectType>();

	/**
	 * @uml.property name="renderer"
	 * @uml.associationEnd
	 */
	public GameObjectRenderer renderer = null;
	/**
	 * @uml.property name="script"
	 * @uml.associationEnd
	 */
	public GameScript script = null;
	/**
	 * @uml.property name="shape"
	 * @uml.associationEnd
	 */
	public CollisionShape shape = null;
	/**
	 * @uml.property name="name"
	 */
	public String name;

	public float shininess = (float) (Math.random() * 2000);
	public float reflective = 0;
	public boolean airShader = false;
	public RenderState renderState = new RenderState();

	public GameObjectType(String name) {
		allTypes.put(name, this);
		this.name = name;
	}

	public static GameObjectType getType(String name) {
		return allTypes.get(name);
	}

	@Override
	public String toString() {
		return "GameObjectType [renderer=" + renderer + ", script=" + script
				+ ", shape=" + shape + ", name=" + name + ", shininess="
				+ shininess + "]";
	}

	public static Collection<GameObjectType> getTypes() {
		return allTypes.values();
	}
}
