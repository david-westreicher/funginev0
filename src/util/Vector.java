package util;

public class Vector extends Matrix  {

	/**
	 * @uml.property name="v" multiplicity="(0 -1)" dimension="1"
	 */
	public float[] v;

	public Vector(float[] elements) {
		super(elements.length, 1);
		for (int i = 0; i < elements.length; i++)
			super.m[i][0] = elements[i];
		this.v = elements;
	}

	public Vector(Matrix mat) {
		super(mat);
		if (mat.m[0].length > 1)
			throw new RuntimeException(mat + "not a vector");
		v = new float[mat.m.length];
		for (int i = 0; i < mat.m.length; i++)
			v[i] = mat.m[i][0];
	}

	public Vector rotate(float[] rot) {
		Matrix mat = super.rotate2D(rot);
		return new Vector(mat);
	}
}
