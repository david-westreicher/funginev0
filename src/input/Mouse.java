package input;

import util.Log;

public class Mouse {

	/**
	 * @uml.property name="pos" multiplicity="(0 -1)" dimension="1"
	 */
	public int[] pos = new int[2];
	/**
	 * @uml.property name="down"
	 */
	public boolean down = false;
	/**
	 * @uml.property name="wheel"
	 */
	public int wheel = 0;

	public void set(int x, int y) {
		pos[0] = x;
		pos[1] = y;
	}

	public void update() {
		wheel = 0;
	}

}
