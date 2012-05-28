package rendering;

import game.Game;

import java.util.List;

import javax.media.opengl.GL2;

import world.GameObject;
import world.GameObjectType;

public abstract class GameObjectRenderer {
	/**
	 * @uml.property  name="isSimple"
	 */
	private boolean isSimple;

	public GameObjectRenderer(boolean simple) {
		isSimple = simple;
	}

	public GameObjectRenderer() {
		this(false);
	}

	public abstract void init(GL2 gl);

	public abstract void draw(GL2 gl);

	public abstract void end(GL2 gl);

	public void draw(GL2 gl, List<GameObject> objs, float interp) {
	}

	/**
	 * @return
	 * @uml.property  name="isSimple"
	 */
	public boolean isSimple() {
		return isSimple;
	}

}
