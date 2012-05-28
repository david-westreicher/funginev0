package util;

public class Matrix {
	/**
	 * @uml.property  name="m" multiplicity="(0 -1)" dimension="2"
	 */
	public float[][] m;

	public Matrix(int n, int mn) {
		m = new float[n][mn];
	}

	public Matrix(float[][] elements) {
		this.m = elements;
	}

	public Matrix(Matrix mat) {
		m = mat.m;
	}

	public Matrix multiply(Matrix mo) {
		float[][] e = m;
		float[][] o = mo.m;
		if (e[0].length != o.length)
			throw new RuntimeException("matrices have non-matching dimensions");
		int newR = e.length;
		int newS = o[0].length;
		Matrix out = new Matrix(newR, newS);
		float[][] res = out.m;
		for (int i = 0; i < newR; i++) {
			for (int j = 0; j < newS; j++) {
				float sum = 0;
				for (int k = 0; k < o.length; k++) {
					sum += e[i][k] * o[k][j];
				}
				res[i][j] = sum;
			}
		}
		return out;
	}

	public Matrix rotate(float[] rot) {
		/*
		 * 
		 * mat4 R = mat4( vec4(c2*c3,-c2*s3,s2,0),
		 * vec4(c1*s3+c3*s1*s2,c1*c3-s1*s2*s3,-c2*s1,0),
		 * vec4(s1*s3-c1*c3*s2,c1*s2*s3+c3*s1,c1*c2,0), vec4(0,0,0,1));
		 */
		float c1 = (float) Math.cos(rot[0]);
		float s1 = (float) Math.sin(rot[0]);
		float c2 = (float) Math.cos(rot[1]);
		float s2 = (float) Math.sin(rot[1]);
		float c3 = (float) Math.cos(rot[2]);
		float s3 = (float) Math.sin(rot[2]);
		float mat[][] = new float[][] {
				new float[] { c2 * c3, -c2 * s3, s2 },
				new float[] { c1 * s3 + c3 * s1 * s2, c1 * c3 - s1 * s2 * s3,
						-c2 * s1 },
				new float[] { s1 * s3 - c1 * c3 * s2, c1 * s2 * s3 + c3 * s1,
						c1 * c2 } };
		Matrix rotMat = new Matrix(mat);
		return rotMat.multiply(this);
	}

	public Matrix rotate2D(float[] rot) {
		float c1 = (float) Math.cos(rot[1]);
		float s1 = (float) Math.sin(rot[1]);
		float c2 = (float) Math.cos(rot[0]);
		float s2 = (float) Math.sin(rot[0]);
		float mat[][] = new float[][] { new float[] { c2, s2 * s1, -s2 * c1 },
				new float[] { 0, c1, s1 },
				new float[] { s2, -s1 * c2, c2 * c1 } };
		Matrix rotMat = new Matrix(mat);
		return rotMat.multiply(this);
	}

	public boolean equals(Object o) {
		if (o instanceof Matrix) {
			Matrix ot = (Matrix) o;
			if (ot.m.length == m.length && ot.m[0].length == m[0].length) {
				for (int i = 0; i < m.length; i++) {
					for (int j = 0; j < m[0].length; j++) {
						if (ot.m[i][j] != m[i][j])
							return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				sb.append(m[i][j]);
				sb.append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
