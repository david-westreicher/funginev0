package manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import experiment.OpenVC;

import rendering.RenderUpdater;
import rendering.SpriteRenderer;
import settings.Settings;
import util.Log;

public class SpriteManager extends Manager<SpriteRenderer> {
	public SpriteManager() {
		super("sprite");
	}

	public Map<String, Texture> textures = new HashMap<String, Texture>();
	private boolean vid;

	private void load(final String s, final SpriteRenderer sr) {
		Log.log(this, "loading texture: " + s);
		RenderUpdater.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				Texture text;
				try {
					text = TextureIO.newTexture(new File(
							Settings.RESSOURCE_FOLDER + s), false);
					text.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER,
							GL2.GL_NEAREST);
					text.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER,
							GL2.GL_NEAREST);
					sr.texture = text;
					textures.put(s, text);
				} catch (GLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void load(final BufferedImage bi, final SpriteRenderer sr,
			final String name) {
		Log.log(this, "loading vid: " + name);
		RenderUpdater.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				Texture text;
				try {
					text = AWTTextureIO.newTexture(GLProfile.getDefault(), bi,
							false);
					text.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER,
							GL2.GL_LINEAR);
					text.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER,
							GL2.GL_LINEAR);
					sr.texture = text;
					textures.put(name, text);
				} catch (GLException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void updateObject(SpriteRenderer t, String file) {
		if (vid)
			load(OpenVC.bi, t, file);
		else
			load(file, t);
	}

	public void restart() {
		super.restart();
		RenderUpdater.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				for (Texture t : textures.values()) {
					t.destroy(RenderUpdater.gl);
				}
			}
		});
		textures.clear();
	}

	public void getTexture(String str, SpriteRenderer spriteRenderer) {
		Texture tex = textures.get(str);
		if (tex == null) {
			super.update(str, spriteRenderer);
		}
	}

	public void getTextureVid(String str, SpriteRenderer spriteRenderer) {
		vid = true;
		getTexture(str, spriteRenderer);
		vid = false;
	}
}
