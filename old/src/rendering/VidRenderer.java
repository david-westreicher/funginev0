package rendering;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import manager.SpriteManager;

import com.jogamp.opengl.util.texture.awt.AWTTextureData;

import experiment.OpenVC;
import game.Game;

public class VidRenderer extends SpriteRenderer {

	public VidRenderer(String str) {
		((SpriteManager) Game.INSTANCE.getManager("sprite")).getTextureVid(str,
				this);
	}

	public void init(GL2 gl) {
		if (texture != null) {
			texture.bind();
			synchronized (OpenVC.MUTEX) {
				/*
				 * gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, OpenVC.width/4,
				 * OpenVC.height, GL2.GL_BGR, GL2.GL_UNSIGNED_INT, OpenVC.data);
				 */
				texture.updateImage(new AWTTextureData(GLProfile.getDefault(),
						GL2.GL_BGR, GL2.GL_UNSIGNED_INT, false, OpenVC.bi));
			}
			gl.glCallList(CALL_LIST_NUM[0]);
		}

	}
}
