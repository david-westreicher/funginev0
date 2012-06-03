package test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.berkelium.java.api.Berkelium;
import org.berkelium.java.api.Buffer;
import org.berkelium.java.api.Rect;
import org.berkelium.java.api.Window;
import org.berkelium.java.api.WindowDelegate;
import org.berkelium.java.awt.BufferedImageAdapter;
import org.berkelium.java.examples.awt.AwtExample;

public class BerkeliumTest extends JFrame implements WindowDelegate {
	private static final long serialVersionUID = 8835790859223385092L;
	private final Berkelium runtime = Berkelium.getInstance();
	private final Window win = runtime.createWindow();
	private final BufferedImageAdapter bia = new BufferedImageAdapter() {
		public void onPaintDone(Window win, final Rect rect) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					repaint(rect.left(), rect.top(), rect.right(),
							rect.bottom());
				}
			});
		};
	};
	private final int initialWidth = 640;
	private final int initialHeight = 480;

	public BerkeliumTest() {
		setTitle("AwtExample");
		setSize(new Dimension(initialWidth, initialHeight));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		win.addDelegate(this);

		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				handleMouseButtonEvent(e, false);
			}

			public void mousePressed(MouseEvent e) {
				handleMouseButtonEvent(e, true);
			}
		});
	}

	private void handleMouseButtonEvent(MouseEvent e, final boolean down) {
		final BufferedImage bi = bia.getImage();
		if (bia == null)
			return;
		final int x = e.getX() * bi.getWidth() / getWidth();
		final int y = e.getY() * bi.getHeight() / getHeight();
		final int b = e.getButton();

		// the event must be handled in the berkelium thread
		runtime.execute(new Runnable() {
			public void run() {
				win.mouseMoved(x, y);
				win.mouseButton(b, down);
			}
		});
	}

	public void paint(Graphics g) {
		BufferedImage img = bia.getImage();
		if (img != null) {
			// do not allow updates to the image while we draw it
			synchronized (bia) {
				g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			}
		}
	}

	public void run() throws Exception {
		synchronized (runtime) {
			win.setDelegate(bia);
			bia.resize(initialWidth, initialHeight);
			win.resize(initialWidth, initialHeight);
			win.navigateTo("http://www.youtube.com/");
			runtime.update();
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			System.out.println("initializing berkelium-java...");
			Berkelium.createMultiThreadInstance();
			new AwtExample().run();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void freeLastScriptAlert(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddressBarChanged(Window arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConsoleMessage(Window arg0, String arg1, String arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCrashed(Window arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCrashedPlugin(Window arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCrashedWorker(Window arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreatedWindow(Window arg0, Window arg1, Rect arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onExternalHost(Window arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJavascriptCallback(Window arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoad(Window arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadingStateChanged(Window arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onNavigationRequested(Window arg0, String arg1, String arg2,
			boolean arg3, boolean[] arg4) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPaint(Window arg0, Buffer arg1, Rect arg2, Rect[] arg3,
			int arg4, int arg5, Rect arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPaintDone(Window arg0, Rect arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProvisionalLoadError(Window arg0, String arg1, int arg2,
			boolean arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponsive(Window arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRunFileChooser(Window arg0, int arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScriptAlert(Window arg0, String arg1, String arg2,
			String arg3, int arg4, boolean[] arg5, String[] arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartLoading(Window arg0, String arg1) {
		System.out.println("startloading");

	}

	@Override
	public void onTitleChanged(Window arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTooltipChanged(Window arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnresponsive(Window arg0) {
		// TODO Auto-generated method stub

	}
}