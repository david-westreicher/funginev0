package input;

import game.Game;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class CanvasListener implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
	/**
	 * @uml.property  name="input"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Input input;

	public CanvasListener() {
		input = Game.INSTANCE.input;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		input.mouse.set(e.getX(), e.getY());

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		input.mouse.set(e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		input.mouse.set(e.getX(), e.getY());

	}

	@Override
	public void mouseExited(MouseEvent e) {
		input.mouse.set(e.getX(), e.getY());

	}

	@Override
	public void mousePressed(MouseEvent e) {
		input.mouse.down = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		input.mouse.down = false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		input.mouse.wheel = e.getWheelRotation();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		input.keyboard.pressed(e.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		input.keyboard.released(e.getKeyChar());
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
