package testMath;

import static org.junit.Assert.*;

import org.junit.Test;

import util.Matrix;

public class MatrixTest {
	@Test
	public void testMultiply() {
		int size = 3;
		float[][] el = new float[size][size];
		float[][] el2 = new float[size][size];
		for (int i = 0; i < size * size; i++) {
			el[(i / size)][(i % size)] = i;
			el2[(i / size)][(i % size)] = i;
		}
		Matrix m = new Matrix(el);
		Matrix m2 = new Matrix(el2);
		Matrix m3 = new Matrix(new float[][] {
				new float[] { 15.0f, 18.0f, 21.0f },
				new float[] { 42.0f, 54.0f, 66.0f },
				new float[] { 69.0f, 90.0f, 111.0f } });
		assertEquals(m.multiply(m2), m3);
	}
	
	@Test
	public void testRotation() {
		int size = 3;
		float[][] el = new float[size][size];
		float[][] el2 = new float[size][size];
		for (int i = 0; i < size * size; i++) {
			el[(i / size)][(i % size)] = i;
			el2[(i / size)][(i % size)] = i;
		}
		Matrix m = new Matrix(el);
		Matrix m2 = new Matrix(el2);
		Matrix m3 = new Matrix(new float[][] {
				new float[] { 15.0f, 18.0f, 21.0f },
				new float[] { 42.0f, 54.0f, 66.0f },
				new float[] { 69.0f, 90.0f, 111.0f } });
		assertEquals(m.multiply(m2), m3);
	}
}
