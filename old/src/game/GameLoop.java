package game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rendering.RenderUpdater;
import settings.Settings;
import util.Log;
import util.RepeatedThreadExact;
import util.Stoppable;
import util.TickCounter;
import util.Util;
import world.RepeatedRunnable;

public class GameLoop extends RepeatedRunnable {
	public static int TICKS_PER_SECOND = 30;
	private int SKIP_TICKS;
	private final int MAX_FRAMESKIP = 5;
	private final int MAX_FPS = 200;
	private int renderloops = 0;
	public Updatable renderer;
	public Updatable mechanics;
	private long nextTick;
	private int loops;
	private float interpolation;
	public TickCounter currentFPS;
	public TickCounter currentTick;
	public int tick = 0;
	private boolean pauseLogic;
	public long timePerTick;

	public GameLoop() {
		setFPS(TICKS_PER_SECOND);
		currentFPS = new TickCounter();
		currentTick = new TickCounter();
	}

	public void setFPS(int fps) {
		TICKS_PER_SECOND = fps;
		SKIP_TICKS = 1000000000 / TICKS_PER_SECOND;
	}

	public void start() {
		nextTick = System.nanoTime();
		loops = 0;
		interpolation = 0;
		super.start();
	}

	@Override
	protected void executeRepeatedly() {
		loops = 0;
		while (!pauseLogic && System.nanoTime() > nextTick
				&& loops < MAX_FRAMESKIP) {
			long timePerTickStart = System.currentTimeMillis();
			if (mechanics != null)
				mechanics.update(0);
			timePerTick = System.currentTimeMillis()-timePerTickStart;
			nextTick += SKIP_TICKS;
			tick++;
			currentTick.tick();
			loops++;
			renderloops = 0;
		}
		if (renderer != null) {
			//if (renderloops < (float) MAX_FPS / TICKS_PER_SECOND) {
				currentFPS.tick();
				if (pauseLogic)
					interpolation = 0;
				else
					interpolation = (float) (System.nanoTime() + SKIP_TICKS - nextTick)
							/ (float) (SKIP_TICKS);
				renderer.update(interpolation);
				renderloops++;
			/*} else {
				Thread.yield();
				Util.sleep(1);
			}*/
		}
	}

	public void exit() {
		if (renderer != null)
			renderer.dispose();
		mechanics = null;
		renderer = null;
		tick = 0;
	}

	public void pauseLogic() {
		pauseLogic = true;
	}

	public boolean isPaused() {
		return pauseLogic;
	}

	public void continueLogic() {
		pauseLogic = false;
		nextTick = System.nanoTime();
	}

}
