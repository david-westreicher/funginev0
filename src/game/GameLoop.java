package game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rendering.RenderUpdater;
import settings.Settings;
import util.Log;
import util.RepeatedRunnable;
import util.RepeatedThreadExact;
import util.Stoppable;
import util.TickCounter;
import util.Util;

public class GameLoop extends RepeatedRunnable {
	public static int TICKS_PER_SECOND = 60;
	/**
	 * @uml.property  name="sKIP_TICKS"
	 */
	private int SKIP_TICKS;
	/**
	 * @uml.property  name="mAX_FRAMESKIP"
	 */
	private final int MAX_FRAMESKIP = 5;
	/**
	 * @uml.property  name="mAX_FPS"
	 */
	private final int MAX_FPS = 200;
	/**
	 * @uml.property  name="renderloops"
	 */
	private int renderloops = 0;
	/**
	 * @uml.property  name="renderer"
	 * @uml.associationEnd  
	 */
	public Updatable renderer;
	/**
	 * @uml.property  name="mechanics"
	 * @uml.associationEnd  
	 */
	public Updatable mechanics;
	/**
	 * @uml.property  name="nextTick"
	 */
	private long nextTick;
	/**
	 * @uml.property  name="loops"
	 */
	private int loops;
	/**
	 * @uml.property  name="interpolation"
	 */
	private float interpolation;
	/**
	 * @uml.property  name="currentFPS"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public TickCounter currentFPS;
	/**
	 * @uml.property  name="currentTick"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public TickCounter currentTick;
	/**
	 * @uml.property  name="tick"
	 */
	public int tick = 0;
	/**
	 * @uml.property  name="pauseLogic"
	 */
	private boolean pauseLogic;
	/**
	 * @uml.property  name="timePerTick"
	 */
	public long timePerTick;

	public GameLoop() {
		super("GameLoopThread");
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
		Log.log(this,"exit");
		if (renderer != null)
			renderer.dispose();
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
