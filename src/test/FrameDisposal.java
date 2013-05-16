package test;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import util.Log;

public class FrameDisposal extends JFrame {
	public FrameDisposal() {
		super("test");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.log(FrameDisposal.class, "close");
				WindowEvent wev = new WindowEvent(FrameDisposal.this,
						WindowEvent.WINDOW_CLOSING);
				Toolkit.getDefaultToolkit().getSystemEventQueue()
						.postEvent(wev);
			}
		}).start();
	}

	public static void main(String[] args) {
		FrameDisposal fr = new FrameDisposal();
		Log.log(FrameDisposal.class, "open");
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);

	}

}
