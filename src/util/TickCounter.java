package util;

public class TickCounter {

	/**
	 * @uml.property  name="fps"
	 */
	public float fps = 0;
	/**
	 * @uml.property  name="ticks"
	 */
	private long ticks;
	/**
	 * @uml.property  name="last"
	 */
	private long last;

	public void tick() {
		ticks++;
		long now = System.currentTimeMillis();
		long delta = now - last;
		if (delta > 500) {
			fps = (float) (ticks * 1000) / (float) delta;
			ticks = 0;
			last = now;
		}
	}

}
