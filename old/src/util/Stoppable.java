package util;

import java.util.ArrayList;
import java.util.List;

public class Stoppable extends Thread {
	private static List<Stoppable> allThreads = new ArrayList<Stoppable>();
	protected boolean running = true;

	public Stoppable() {
		allThreads.add(this);
	}

	public void stopThread() {
		running = false;
	}

	public static void stopAll() {
		Log.log("Stopping all Threads");
		for (Stoppable r : allThreads) {
			Log.log("Stopping "+r.getClass().getSimpleName());
			r.stopThread();
		}
		allThreads.clear();
	}
}
