package world;

import util.Log;
import util.Stoppable;

public abstract class RepeatedRunnable extends Stoppable {
	private boolean pause = false;

	@Override
	public void run() {
		while (running) {
			if (pause) {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			executeRepeatedly();
		}
	}

	protected abstract void executeRepeatedly();

	public void startPause() {
		Log.log(this, " paused!");
		pause = true;
	}

	public void endPause() {
		Log.log(this, " pause ended!");
		synchronized (this) {
			this.notify();
		}
		pause = false;
	}

	public boolean isPausing() {
		return pause;
	}

}
