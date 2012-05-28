package util;

import game.Game;
import game.GameLoop;

import input.CanvasListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix3f;

import com.jogamp.opengl.util.texture.Texture;

import rendering.DOFRenderer;
import rendering.RenderUpdater;
import rendering.SimpleRenderer;
import settings.Settings;

public class Util {

	public static JFrame frame;
	public static Component c;

	public static void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Frame createFrame(Component comp) {
		// TODO fullscreen :D
		frame = new JFrame("Engine Test");
		c = comp;
		Container pane = frame.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		c.setMaximumSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
		c.setMinimumSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
		pane.add(c);
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setMinimumSize(new Dimension(200, Settings.HEIGHT));
		optionsPanel.setMaximumSize(new Dimension(200, Settings.HEIGHT));
		pane.add(optionsPanel);

		final JButton button = new JButton();
		button.setText(Game.INSTANCE.loop.isPaused() ? "Continue" : "Pause");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (button.getText().equals("Pause")) {
					Game.INSTANCE.loop.pauseLogic();
					button.setText("Continue");
				} else {
					Game.INSTANCE.loop.continueLogic();
					button.setText("Pause");
				}
			}
		});

		final JButton buttonr = new JButton("Restart");
		buttonr.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(buttonr);
		buttonr.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Game.INSTANCE.restart();
			}
		});

		final JCheckBox debug = new JCheckBox("Debug");
		debug.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(debug);
		debug.setSelected(Game.DEBUG);
		debug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Game.DEBUG = debug.isSelected();
			}
		});

		final JCheckBox wire = new JCheckBox("Wireframe");
		wire.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(wire);
		wire.setSelected(Game.WIREFRAME);
		wire.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Game.WIREFRAME = wire.isSelected();
			}
		});

		final JSlider slider = new JSlider();
		slider.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(slider);
		slider.setMinimum(1);
		slider.setValue(GameLoop.TICKS_PER_SECOND);
		slider.setMaximum(100);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Game.INSTANCE.loop.setFPS(slider.getValue());
			}
		});

		final JSlider sl = new JSlider();
		sl.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(sl);
		sl.setMinimum(0);
		sl.setValue((int) Game.INSTANCE.cam.focus);
		sl.setMaximum(100);
		sl.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Game.INSTANCE.cam.focus = (float) sl.getValue() / 100;
			}
		});

		final JCheckBox pp = new JCheckBox("Postprocess");
		pp.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(pp);
		pp.setSelected(DOFRenderer.POST_PROCESS);
		sl.setEnabled(DOFRenderer.POST_PROCESS);
		pp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DOFRenderer.POST_PROCESS = pp.isSelected();
				sl.setEnabled(DOFRenderer.POST_PROCESS);
			}
		});

		frame.setSize(Settings.WIDTH + 220, Settings.HEIGHT + 50);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setVisible(true);
		CanvasListener l = new CanvasListener();
		c.addMouseMotionListener(l);
		c.addMouseListener(l);
		c.addMouseWheelListener(l);
		c.addKeyListener(l);

		// by default, an AWT Frame doesn't do anything when you click
		// the close button; this bit of code will terminate the program when
		// the window is asked to close
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Game.INSTANCE.exit();
				sleep(1000);
				System.exit(0);
			}
		});
		return frame;
	}

	public static float roundDigits(float i, int j) {
		double ten = Math.pow(10, j);
		return (float) (Math.round(i * ten) / ten);
	}

	public static Cursor getHiddenCursor() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getBestCursorSize(1, 1);
		BufferedImage cursorImg = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = cursorImg.createGraphics();
		g2d.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		g2d.clearRect(0, 0, dim.width, dim.height);
		g2d.dispose();
		return toolkit.createCustomCursor(cursorImg, new Point(0, 0),
				"hiddenCursor");
	}

	public static Object getFileType(String file) {
		String[] strings = file.split("\\.");
		return strings[strings.length - 1];
	}

	public static void toEuler(float x, float y, float z, float angle,
			float[] out) {
		double s = Math.sin(angle);
		double c = Math.cos(angle);
		double t = 1 - c;
		// if axis is not already normalised then uncomment this
		// double magnitude = Math.sqrt(x*x + y*y + z*z);
		// if (magnitude==0) throw error;
		// x /= magnitude;
		// y /= magnitude;
		// z /= magnitude;
		if ((x * y * t + z * s) > 0.998) { // north pole singularity detected
			out[1] = (float) (2 * Math.atan2(x * Math.sin(angle / 2),
					Math.cos(angle / 2)));
			out[2] = (float) (Math.PI / 2);
			out[0] = 0;
			return;
		}
		if ((x * y * t + z * s) < -0.998) { // south pole singularity detected
			out[1] = (float) (-2 * Math.atan2(x * Math.sin(angle / 2),
					Math.cos(angle / 2)));
			out[2] = (float) (-Math.PI / 2);
			out[0] = 0;
			return;
		}
		out[1] = (float) Math.atan2(y * s - x * z * t, 1 - (y * y + z * z) * t);
		out[2] = (float) Math.asin(x * y * t + z * s);
		out[0] = (float) Math.atan2(x * s - y * z * t, 1 - (x * x + z * z) * t);
	}

	public static void toEuler(Matrix3f m, float[] rotation) {
		if (m.m10 > 0.998) { // singularity at north pole
			rotation[1] = (float) Math.atan2(m.m02, m.m22);
			rotation[2] = (float) (Math.PI / 2);
			rotation[0] = 0;
			return;
		}
		if (m.m10 < -0.998) { // singularity at south pole
			rotation[1] = (float) Math.atan2(m.m02, m.m22);
			rotation[2] = (float) (-Math.PI / 2);
			rotation[0] = 0;
			return;
		}
		rotation[1] = (float) Math.atan2(-m.m20, m.m00);
		rotation[2] = (float) Math.atan2(-m.m12, m.m11);
		rotation[0] = (float) Math.asin(m.m10);
	}
}
