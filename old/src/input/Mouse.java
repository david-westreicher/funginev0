package input;

public class Mouse {

	public int[] pos = new int[2];
	public boolean down = false;
	public int wheel = 0;

	public void set(int x, int y) {
		pos[0] = x;
		pos[1] = y;
	}

	public void update() {
		wheel = 0;
	}

}
