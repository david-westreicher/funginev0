package rendering;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import game.Game;
import game.GameLoop;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import util.Log;
import util.Util;
import world.GameObject;
import world.GameObjectType;

import manager.SpriteManager;

import com.jogamp.opengl.util.texture.Texture;

public class AnimationRenderer extends SpriteRenderer {

	private float vSprites = 7;
	private float hSprites = 10;
	private float nvSprites = 1 / vSprites;
	private float nhSprites = 1 / hSprites;
	// private int currentSprite = 0;
	private long lastTime;
	private long speed = 10;
	private Map<Integer, float[]> subSprites = new HashMap<Integer, float[]>();
	private int num = (int) (vSprites * hSprites);

	public AnimationRenderer(String str) {
		super(str);
		for (int i = 0; i < num; i++) {
			int currentSprite = i;
			int x = (int) (currentSprite % hSprites);
			int y = (int) (currentSprite / hSprites);
			float[] rec = new float[] { x / hSprites, y / vSprites,
					x / hSprites + nhSprites, y / vSprites + nvSprites };
			subSprites.put(currentSprite, rec);
		}
	}

	public void init(GL2 gl) {
		super.init(gl);
	}

	@Override
	public void draw(GL2 gl) {
		gl.glColor4f(1, 1, 1, RenderUpdater.cgo.alpha);
		drawQuadAnimated(gl);
	}

	private void drawQuadAnimated(GL2 gl) {
		float[] rec = subSprites.get((int) Math
				.abs(RenderUpdater.cgo.animSprite) % num);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(rec[0], rec[1]);
		gl.glVertex3f(-0.5f, -0.5f, 0);
		gl.glTexCoord2f(rec[2], rec[1]);
		gl.glVertex3f(+0.5f, -0.5f, 0);
		gl.glTexCoord2f(rec[2], rec[3]);
		gl.glVertex3f(+0.5f, 0.5f, 0);
		gl.glTexCoord2f(rec[0], rec[3]);
		gl.glVertex3f(-0.5f, 0.5f, 0);
		gl.glEnd();
	}

}
