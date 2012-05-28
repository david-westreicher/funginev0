package world;

import settings.Settings;

public class Camera extends GameObject {
	public float oldZoom = 1;
	public float zoom = 1;
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
