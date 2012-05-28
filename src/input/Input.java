package input;

public class Input {

	/**
	 * @uml.property  name="mouse"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public Mouse mouse;
	/**
	 * @uml.property  name="keyboard"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public KeyBoard keyboard;

	public Input() {
		mouse = new Mouse();
		keyboard = new KeyBoard();
	}

}
