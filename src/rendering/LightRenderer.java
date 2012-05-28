package rendering;

import javax.media.opengl.GL2;


public class LightRenderer extends GameObjectRenderer {

	@Override
	public void draw(GL2 gl) {
		gl.glColor3f(1, 1, 0);
		RenderUtil.drawSphere(0, 0, 1, null, gl);
	}

	@Override
	public void end(GL2 gl) {
	}

	@Override
	public void init(GL2 gl) {
	}

}
