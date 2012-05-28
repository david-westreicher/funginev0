package rendering;

import game.Game;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import util.Log;

import manager.SpriteManager;

import com.jogamp.opengl.util.texture.Texture;

public class SpriteRenderer extends GameObjectRenderer {
	protected static int[] CALL_LIST_NUM = new int[3];

	static {
		createList();
	}

	/**
	 * @uml.property name="texture"
	 * @uml.associationEnd readOnly="true"
	 */
	public Texture texture;

	public SpriteRenderer() {
	}

	public SpriteRenderer(String str) {
		((SpriteManager) Game.INSTANCE.getManager("sprite")).getTexture(str,
				this);
	}

	public static void createList() {
		RenderUpdater.createCallList(new Runnable() {

			@Override
			public void run() {
				initCall(RenderUpdater.gl);
			}
		}, new CallBack<Integer>() {

			@Override
			public void returnVar(Integer i) {
				CALL_LIST_NUM[0] = i;
			}
		});
		RenderUpdater.createCallList(new Runnable() {

			@Override
			public void run() {
				drawQuad(RenderUpdater.gl);
			}
		}, new CallBack<Integer>() {

			@Override
			public void returnVar(Integer i) {
				CALL_LIST_NUM[1] = i;
			}
		});
		RenderUpdater.createCallList(new Runnable() {

			@Override
			public void run() {
				endCall(RenderUpdater.gl);
			}
		}, new CallBack<Integer>() {

			@Override
			public void returnVar(Integer i) {
				CALL_LIST_NUM[2] = i;
			}
		});

	}

	@Override
	public void init(GL2 gl) {
		if (texture != null) {
			// initCall(gl);
			texture.bind();
			gl.glCallList(CALL_LIST_NUM[0]);
		}
	}

	@Override
	public void draw(GL2 gl) {
		gl.glColor4f(1, 1, 1, RenderUpdater.cgo.alpha);
		// drawQuad(gl);
		gl.glCallList(CALL_LIST_NUM[1]);
	}

	@Override
	public void end(GL2 gl) {
		// endCall(gl);
		texture.disable();
		gl.glCallList(CALL_LIST_NUM[2]);
	}

	protected static void endCall(GL2 gl) {
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glEnable(GL2.GL_CULL_FACE);
	}

	protected static void initCall(GL2 gl) {
		gl.glDisable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_TEXTURE_2D);
	}

	protected static void drawQuad(GL2 gl) {
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(-0.5f, -0.5f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(0.5f, -0.5f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(0.5f, 0.5f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(-0.5f, 0.5f);
		gl.glEnd();
	}

}
