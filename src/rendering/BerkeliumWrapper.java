package rendering;

import game.Game;

import java.awt.Event;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.Format;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.berkelium.java.api.Berkelium;
import org.berkelium.java.api.Rect;
import org.berkelium.java.api.Window;
import org.berkelium.java.api.WindowDelegate;
import org.berkelium.java.awt.BufferedImageAdapter;

import util.Log;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class BerkeliumWrapper {
	private static final int BK_KEYCODE_PRIOR = 0x21;
	private static final int BK_KEYCODE_NEXT = 0x22;
	private static final int BK_KEYCODE_END = 0x23;
	private static final int BK_KEYCODE_HOME = 0x24;
	private static final int BK_KEYCODE_LEFT = 0x25;
	private static final int BK_KEYCODE_UP = 0x26;
	private static final int BK_KEYCODE_RIGHT = 0x27;
	private static final int BK_KEYCODE_DOWN = 0x28;
	private static final int BK_KEYCODE_INSERT = 0x2D;
	private static final int BK_KEYCODE_DELETE = 0x2E;
	private static final int SCALE = 3;
	private static final int SECOND_SCALE = 2;
	private static BerkeliumWrapper instance;
	private static int key = 0;
	private Berkelium berk;
	private BufferedImageAdapter imageAdaper;
	private Window window;
	private Texture berkTexture;
	private BufferedImage img;
	private int width;
	private int height;
	private TextureData textureData;
	private boolean update;

	public BerkeliumWrapper(int width, int height) {
		this.width = width;
		this.height = height;
		instance = this;
		berk = Berkelium.createMultiThreadInstance();
		imageAdaper = new BufferedImageAdapter() {

			@Override
			public synchronized void onPaint(Window arg0,
					org.berkelium.java.api.Buffer arg1, Rect arg2, Rect[] arg3,
					final int x, final int y, Rect arg6) {
				super.onPaint(arg0, arg1, arg2, arg3, x, y, arg6);
				// berkTexture.updateImage(RenderUpdater.gl, textureData);
				RenderUpdater.executeInOpenGLContext(new Runnable() {

					@Override
					public void run() {
						berkTexture.updateSubImage(RenderUpdater.gl,
								textureData, 0, x, y);
					}
				});
				update = true;
			}

		};
		window = berk.createWindow();
		window.addDelegate(imageAdaper);
		window.focus();
		// window.setTransparent(true);
		imageAdaper.resize(width / SCALE, height / SCALE);
		window.resize(width / SCALE, height / SCALE);
		window.navigateTo(new File("ressources/gui/jquery/index.html")
				.getAbsolutePath());
		// window.navigateTo("https://www.google.at/");
		berk.update();
	}

	public BufferedImage getImage() {
		return imageAdaper.getImage();
	}

	public void render(GL2 gl) {
		img = imageAdaper.getImage();
		img.getData().getDataBuffer();
		if (img != null && berkTexture == null) {
			textureData = AWTTextureIO.newTextureData(gl.getGLProfile(), img,
					false);
			berkTexture = new Texture(gl, textureData);
		}
		if (berkTexture != null) {
			if (update) {
				// berkTexture.updateImage(RenderUpdater.gl, textureData);
				update = false;
			}
			berkTexture.bind(gl);
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glColor4f(1, 1, 1, 1);

			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(0, height / SECOND_SCALE, 0);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(width / SECOND_SCALE, height / SECOND_SCALE, 0);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(width / SECOND_SCALE, 0, 0);
			gl.glEnd();

			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glDisable(GL2.GL_BLEND);
		}
	}

	public void dispose(GL2 gl) {
		Log.log(this, "destroying berkelium");
		berkTexture.dispose(gl);
		// window.destroy();
		berk.destroy();
	}

	public static void mouseMoved(final int x, final int y) {
		if (instance != null) {
			instance.berk.execute(new Runnable() {
				@Override
				public void run() {
					instance.window.mouseMoved(x / (SCALE) * SECOND_SCALE, y
							/ (SCALE) * SECOND_SCALE);
				}
			});
		}
	}

	public static void mouseButton(final int button, final boolean down) {
		if (instance != null) {
			instance.berk.execute(new Runnable() {
				@Override
				public void run() {
					instance.window.mouseButton(button, down);
				}
			});
		}
	}

	public static void mouseWheel(MouseWheelEvent e) {
		if (instance != null) {
			if ((e.getModifiers() & Event.CTRL_MASK) != 0) {
				instance.zoom(e.getWheelRotation() < 0 ? 1 : -1);
				return;
			}
			boolean dir = (e.getModifiers() & Event.SHIFT_MASK) != 0;
			final int x = dir ? e.getWheelRotation() * -33 : 0;
			final int y = dir ? 0 : e.getWheelRotation() * -100;
			instance.berk.execute(new Runnable() {
				public void run() {
					instance.window.mouseWheel(x, y);
				}
			});
		}
	}

	private void zoom(final int mode) {
		instance.berk.execute(new Runnable() {
			public void run() {
				window.adjustZoom(mode);
			}
		});
	}

	public static void keyEvent(final KeyEvent e, final boolean b) {
		final int bKey = mapToBerkeliumKey(e.getKeyCode(), b);
		if (instance != null && bKey > 0) {
			instance.berk.execute(new Runnable() {
				public void run() {
					// Log.log(this, "keyEvent", b, bKey);
					instance.window.keyEvent(b, 0, bKey, 0);
				}
			});
		}
	}

	protected static int mapToBerkeliumKey(int keyCode, boolean b) {
		// return key++;
		switch (keyCode) {
		case KeyEvent.VK_BACK_SPACE:
			return 8;
		case KeyEvent.VK_TAB:
			return BK_KEYCODE_NEXT;
		case KeyEvent.VK_ESCAPE:
			return 27;
		case KeyEvent.VK_DELETE:
			return BK_KEYCODE_DELETE;
		case KeyEvent.VK_RIGHT:
			return BK_KEYCODE_RIGHT;
		case KeyEvent.VK_LEFT:
			return BK_KEYCODE_LEFT;
		case KeyEvent.VK_UP:
			return BK_KEYCODE_UP;
		case KeyEvent.VK_DOWN:
			return BK_KEYCODE_DOWN;
		default:
			return 0;
		}
	}

	public static void keyTyped(final KeyEvent e) {
		if (instance != null && mapToBerkeliumKey(e.getKeyCode(), true) == 0)
			instance.berk.execute(new Runnable() {
				public void run() {
					String text = Character.toString(e.getKeyChar());
					if (text.startsWith("\n"))
						text = "\r";
					instance.window.textEvent(text);
				}
			});
	}
}
