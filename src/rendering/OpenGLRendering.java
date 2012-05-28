package rendering;

import game.Game;

import java.awt.Frame;
import java.awt.Toolkit;
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

	/**
	 * @uml.property name="canvas"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private GLCanvas canvas;
	/**
	 * @uml.property name="frame"
	 */
	private Frame frame;

	public OpenGLRendering(GLEventListener r) {
		GLProfile.initSingleton(true);
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		caps.setSampleBuffers(true);
		// caps.setStencilBits(1);
		// caps.setStencilBits(8);
		canvas = new GLCanvas(caps);
		canvas.addGLEventListener(r);
		frame = Util.createFrame(canvas);
	}

	/**
	 * @return
	 * @uml.property name="canvas"
	 */
	public GLCanvas getCanvas() {
		return canvas;
	}

	public void dispose() {
		// canvas.destroy();
		// WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
		// Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		frame.setVisible(false);
		frame.dispose();
	}

}
