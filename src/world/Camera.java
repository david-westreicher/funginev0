package world;

import javax.vecmath.Vector3f;

import settings.Settings;

public class Camera extends GameObject {
	/**
	 * @uml.property  name="oldZoom"
	 */
	public float oldZoom = 1;
	/**
	 * @uml.property  name="zoom"
	 */
	public float zoom = 1;
	/**
	 * @uml.property  name="focus"
	 */
	public float focus;

	static {
		new GameObjectType("cam");
	}
	
	public Camera() {
		super("cam");
		super.setPos(Settings.WIDTH / 2, Settings.HEIGHT / 2, 500);
	}

	public boolean zoomChanged() {
		if (oldZoom != zoom) {
			oldZoom = zoom;
			return true;
		}
		return false;
	}
	
}
