package util;

import java.util.List;

public class Log {
	public void Log() {

	}

	public static void log(Object o) {
		System.out.println("Logger: " + o);
	}

	public static void log(Object inst, List<?> os) {
		System.out.print(inst.getClass().getName() + ": ");
		for (Object o : os) {
			System.out.print(print(o));
		}
		System.out.print("\n");
	}

	private static String print(Object o) {
		StringBuilder sb = new StringBuilder();
		if (o instanceof int[] || o instanceof Integer[]) {
			Integer[] oi = (Integer[]) o;
			sb.append("[");
			for (int i = 0; i < oi.length; i++) {
				if (i == oi.length - 1) {
					sb.append(oi[i]);
				} else
					sb.append(oi[i] + ", ");
			}
			sb.append("]");
		} else
			sb.append(o.toString());
		return sb.toString();
	}

	public static void log(Object inst, Object... o) {
		System.out.print(inst.getClass().getName() + ": ");
		for (int i = 0; i < o.length; i++)
			System.out.print(o[i] + ((i < o.length - 1) ? ", " : ""));
		System.out.print("\n");
	}
	

	public static void log(Object inst, int[] num) {
		System.out.print(inst.getClass().getName() + ": ");
		for (int i = 0; i < num.length; i++)
			System.out.print(num[i] + ((i < num.length - 1) ? ", " : ""));
		System.out.print("\n");
	}
	

	public static void log(Object inst, float[] num) {
		System.out.print(inst.getClass().getName() + ": ");
		for (int i = 0; i < num.length; i++)
			System.out.print(num[i] + ((i < num.length - 1) ? ", " : ""));
		System.out.print("\n");
	}
	

	public static void log(String[] num) {
		for (int i = 0; i < num.length; i++)
			System.out.print(num[i] + ((i < num.length - 1) ? ", " : ""));
		System.out.print("\n");
	}

	public static void err(String string) {
		System.err.println(string);

	}
}
