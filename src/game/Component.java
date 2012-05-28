package game;

import java.util.HashMap;
import java.util.Map;

public abstract class Component<T> {
	/**
	 * @uml.property  name="components"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Object" qualifier="name:java.lang.String java.lang.Object"
	 */
	private Map<String, T> components = new HashMap<String, T>();
	/**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  
	 */
	private Component<?> parent = null;

	public Component() {
	}

	public Component(Component<?> parent) {
		this.parent = parent;
	}

	public Component<?> getParent() {
		return parent;
	}

	public void addComponent(String name, T c) {
		components.put(name, c);
	}

	public T getComponent(String name) {
		return components.get(name);
	}

	public void clear() {
		components.clear();
	}

}
