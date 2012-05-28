package rendering;

import experiment.OpenVC;
import game.Game;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import util.Log;
import util.Util;
import world.GameObjectType;

import manager.SpriteManager;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureData;

public class SpriteRenderer extends GameObjectRenderer {
	protected static int[] CALL_LIST_NUM = new int[3];
	static {
		createList();
	}

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
			texture.bind();
			gl.glCallList(CALL_LIST_NUM[0]);
		}
	}

	@Override
	public void draw(GL2 gl) {
		gl.glColor4f(1, 1, 1, RenderUpdater.cgo.alpha);
		gl.glCallList(CALL_LIST_NUM[1]);
	}

	@Override
	public void end(GL2 gl) {
		gl.glCallList(CALL_LIST_NUM[2]);
	}

	protected static void endCall(GL2 gl) {
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	protected static void initCall(GL2 gl) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_REPEAT);
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
