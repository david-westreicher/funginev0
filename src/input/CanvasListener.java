package input;

import game.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import rendering.BerkeliumWrapper;

public class CanvasListener implements MouseMotionListener, MouseListener,
		MouseWheelListener, KeyListener {
	/**
	 * @uml.property name="input"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Input input;

	public CanvasListener() {
		input = Game.INSTANCE.input;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		input.mouse.set(x, y);
		BerkeliumWrapper.mouseMoved(x, y);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		input.mouse.set(e.getX(), e.getY());
		BerkeliumWrapper.mouseMoved(x, y);
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
		BerkeliumWrapper.mouseButton(e.getButton() - 1, true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		input.mouse.down = false;
		BerkeliumWrapper.mouseButton(e.getButton() - 1, false);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		input.mouse.wheel = e.getWheelRotation();
		BerkeliumWrapper.mouseWheel(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		input.keyboard.pressed(e.getKeyChar());
		BerkeliumWrapper.keyEvent(e, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		input.keyboard.released(e.getKeyChar());
		BerkeliumWrapper.keyEvent(e, false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		BerkeliumWrapper.keyTyped(e);
	}

}
