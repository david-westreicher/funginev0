package util;

import java.util.ArrayList;
import java.util.List;

public class Stoppable extends Thread {
	private static List<Stoppable> allThreads = new ArrayList<Stoppable>();
	/**
	 * @uml.property name="running"
	 */
	protected boolean running = true;

	public Stoppable(String name) {
		super(name);
		allThreads.add(this);
	}

	public void stopThread() {
		synchronized (this) {
			this.notifyAll();
		}
		running = false;
	}

	public static void stopAll() {
		Log.log("Stopping all Threads");
		for (Stoppable r : allThreads) {
			Log.log("Stopping " + r.getClass().getSimpleName());
			r.stopThread();
		}
		allThreads.clear();
	}
}
