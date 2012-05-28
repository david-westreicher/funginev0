package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import world.GameObject;
import world.Light;

public class Factory {
	public static final Factory INSTANCE = new Factory();

	public Object create(String str, Object[][] args) {
		try {
			Class<?> c = Class.forName(str);
			return createObject(c, args);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public GameObject createGameObject(String name) {
		if(name.equals("Light"))
			return new Light(name);
		return new GameObject(name);
	}

	public Object create(String str) {
		try {
			Class<?> c = Class.forName(str);
			return createObject(c);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object createObject(Class<?> c) {
		Constructor<?> constructor = null;
		try {
			constructor = c.getConstructor();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		Object object = null;
		try {
			object = constructor.newInstance();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return object;
	}

	public static Object createObject(Class<?> c, Object[][] args) {
		Class<?>[] classes = new Class<?>[args.length];
		Object[] args2 = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			try {
				classes[i] = Class.forName((String) args[i][1]);
				if (Integer.class.equals(classes[i]))
					args2[i] = (int) Double.parseDouble(args[i][0].toString());
				else
					args2[i] = classes[i].cast(args[i][0]);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			Constructor<?> constructor = c.getConstructor(classes);
			try {
				Object object = constructor.newInstance(args2);
				return object;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}

		return null;
	}
}
