package rendering;

import game.Game;

import javax.media.opengl.GL2;

public class TestRenderer extends GameObjectRenderer {
	private static int[] CALL_LIST_NUM = new int[1];
	static {
		createList();
	}

	/**
	 * @uml.property  name="red"
	 */
	private double red;
	/**
	 * @uml.property  name="blue"
	 */
	private double blue;

	@Override
	public void draw(GL2 gl) {
		gl.glColor3d(red,blue, 1);
		gl.glCallList(CALL_LIST_NUM[0]);
	}

	private static void createList() {
		RenderUpdater.createCallList(new Runnable() {

			@Override
			public void run() {
				drawCall(RenderUpdater.gl);
			}
		}, new CallBack<Integer>() {
			@Override
			public void returnVar(Integer i) {
				CALL_LIST_NUM[0] = i;
			}
		});
	}

	protected static void drawCall(GL2 gl) {
		RenderUpdater.glut.glutSolidTeapot(1);
		// RenderUpdater.glut.glutSolidSphere(1, 10, 10);
	}

	@Override
	public void end(GL2 gl) {

	}

	@Override
	public void init(GL2 gl) {
		float angle = (float)Game.INSTANCE.loop.tick/30;
		red = Math.cos(angle);
		blue =  Math.sin(angle);
	}

}
