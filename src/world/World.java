package world;

import game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rendering.RenderUpdater;
import util.Log;

public class World {
	/**
	 * @uml.property  name="gameObjects"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="world.GameObject"
	 */
	private List<GameObject> gameObjects = new ArrayList<GameObject>();

	public World() {
	}

	public void add(GameObject go) {
		gameObjects.add(go);
	}

	public void remove(GameObject go) {
		gameObjects.remove(go);
	}

	public Map<String, List<GameObject>> getVisibleObjects() {
		Map<String, List<GameObject>> visibleObjs = new HashMap<String, List<GameObject>>();
		// TODO add only visibles
		for (GameObject go : gameObjects) {
			List<GameObject> list = visibleObjs.get(go.getType());
			if (list == null) {
				list = new ArrayList<GameObject>();
				visibleObjs.put(go.getType(), list);
			}
			list.add(go);
		}
		return visibleObjs;
	}

	public Map<String, List<GameObject>> getAllObjects() {
		Map<String, List<GameObject>> visibleObjs = new HashMap<String, List<GameObject>>();
		// TODO add only visibles
		for (GameObject go : gameObjects) {
			List<GameObject> list = visibleObjs.get(go.getType());
			if (list == null) {
				list = new ArrayList<GameObject>();
				visibleObjs.put(go.getType(), list);
			}
			list.add(go);
		}
		return visibleObjs;
	}

	public int getObjectNum() {
		return gameObjects.size();
	}
}
