package rendering;

import game.Game;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import util.Log;
import util.Util;

public class OpenGLRendering {

	private GLCanvas canvas;
	private Frame frame;

	public OpenGLRendering(GLEventListener r) {
		GLProfile.initSingleton(true);
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		caps.setStencilBits(1);
		caps.setSampleBuffers(true);
		// caps.setStencilBits(8);
		canvas = new GLCanvas(caps);
		canvas.addGLEventListener(r);
		frame = Util.createFrame(canvas);
	}

	public GLCanvas getCanvas() {
		return canvas;
	}

	public void dispose() {
		canvas.destroy();
		frame.dispose();
	}

}
