package rendering;

import game.Game;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

import util.Log;

public class TerrainRenderer extends ModelRenderer {

	public static final int STATIC_WIDTH = 2;
	public static final int STATIC_HEIGHT = 2;
	public int WIDTH;
	public int HEIGHT;
	private static final long SEED = 151654654;
	public static float NOISE = 1;
	private Vector3f[][] heightMap;

	private FloatBuffer vertices;
	private FloatBuffer normals;
	private IntBuffer[] indices = new IntBuffer[2];
	private FloatBuffer colors;
	private Random r;
	private ArrayList<Vector3f> normalArray;
	private static Vector3f[][] biggestHeightMap;
	private static int NUM = 0;

	public TerrainRenderer() {
		generate();
	}

	public void generate() {
		WIDTH = STATIC_WIDTH;
		HEIGHT = STATIC_HEIGHT;
		r = new Random(SEED);
		biggestHeightMap = computeHeightMap();
		// vertices = FloatBuffer
		// .allocate(((WIDTH + 1) * (HEIGHT + 1) + (WIDTH / 2 + 1)
		// * (HEIGHT / 2 + 1)) * 3);
		vertices = FloatBuffer.allocate((WIDTH + 1) * (HEIGHT + 1) * 3
				+ (WIDTH / 2 + 1) * (HEIGHT / 2 + 1) * 3);
		indices[0] = IntBuffer.allocate((WIDTH) * (HEIGHT) * 6);
		Log.log(this, vertices.capacity(), indices[0].capacity());
		// indices = IntBuffer.allocate(((WIDTH) * (HEIGHT) + (WIDTH / 2)
		// * (HEIGHT / 2)) * 6);
		normals = FloatBuffer
				.allocate(((WIDTH + 1) * (HEIGHT + 1) + (WIDTH / 2 + 1)
						* (HEIGHT / 2 + 1)) * 3);
		colors = FloatBuffer
				.allocate(((WIDTH + 1) * (HEIGHT + 1) + (WIDTH / 2 + 1)
						* (HEIGHT / 2 + 1)) * 3);
		getVertices(vertices);
		getIndices(indices[0], 0);
		getNormals(normals, indices[0], 0);
		getColors(colors);
		WIDTH = STATIC_WIDTH / 2;
		HEIGHT = STATIC_HEIGHT / 2;
		indices[1] = IntBuffer.allocate((WIDTH) * (HEIGHT) * 6);
		r = new Random(SEED);
		computeHeightMap();
		getVertices(vertices);
		getIndices(indices[1], (STATIC_WIDTH + 1) * (STATIC_HEIGHT + 1));
		getNormals(normals, indices[1], (STATIC_WIDTH + 1)
				* (STATIC_HEIGHT + 1));
		getColors(colors);
		vertices.rewind();
		indices[0].rewind();
		indices[1].rewind();
		// normals.rewind();
		// colors.rewind();
		init(vertices, null, null, indices);
		vertices.clear();
		// normals.clear();
		indices[0].clear();
		indices[1].clear();
		// colors.clear();
		vertices = null;
		normals = null;
		indices = null;
		colors = null;
	}

	private FloatBuffer getColors(FloatBuffer buf) {
		// int widthLength = heightMap.length;
		// int heightLength = heightMap[0].length;
		// FloatBuffer buf = FloatBuffer.allocate(widthLength * heightLength *
		// 3);
		int normalIndex = 0;
		Vector3f up = new Vector3f(0, 1, 0);
		for (int i = 0; i < WIDTH + 1; i++) {
			for (int j = 0; j < HEIGHT + 1; j++) {
				Vector3f normal = normalArray.get(normalIndex++);
				float height = heightMap[i][j].y;

				if (height < -0.04) {
					buf.put(0).put(0).put(1);
				} else if (height < -0.03) {
					buf.put(.5f).put(.5f).put(0);
				} else if (height < 0.03) {
					double angle = Math.acos(normal.dot(up));
					if (angle > Math.PI / 4) {
						buf.put(0.5f).put(0.25f).put(0.05f);
					} else
						buf.put(0).put(0.5f).put(0);
				} else {
					double angle = Math.acos(normal.dot(up));
					float darkness = 1.5f - (float) (Math.min(1,
							reverseInterp(angle, 0, Math.PI / 2)));
					buf.put(darkness).put(darkness).put(darkness);
				}

				// buf.put(0);
				// buf.put(Math.max( * 10, 0));
				// buf.put(heightMap[i][j].y * -5);// 0.1f - heightMap[i][j].y
				// // * 10);
			}
		}
		// buf.rewind();
		return buf;
	}

	private double reverseInterp(double val, double start, double end) {
		return (val - start) / (end - start);
	}

	private Vector3f[][] computeHeightMap() {
		heightMap = new Vector3f[WIDTH + 1][HEIGHT + 1];
		for (int i = 0; i < WIDTH + 1; i++)
			for (int j = 0; j < HEIGHT + 1; j++)
				heightMap[i][j] = new Vector3f(i, 0, j);
		int log = 0;
		int exp = 1;
		while (exp < WIDTH) {
			exp *= 2;
			log++;
		}
		displace(heightMap[0][0], heightMap[WIDTH][0], heightMap[0][HEIGHT],
				heightMap[WIDTH][HEIGHT], 1 / 2f, log);
		return heightMap;
	}

	private void displace(Vector3f a, Vector3f b, Vector3f c, Vector3f d,
			float noise, int depth) {
		if (depth == 0)
			return;
		Vector3f ePos = new Vector3f();
		ePos.add(a);
		ePos.add(b);
		ePos.add(c);
		ePos.add(d);
		ePos.scale(1 / 4f);
		float height = ePos.y + noise * (r.nextFloat() - 0.5f) * NOISE;
		Vector3f e = heightMap[(int) ePos.x][(int) ePos.z];
		e.y = height;
		Vector3f f = displace(a, c, noise, height);
		Vector3f g = displace(a, b, noise, height);
		Vector3f h = displace(b, d, noise, height);
		Vector3f i = displace(c, d, noise, height);
		float newNoise = (noise / 2);
		displace(a, g, f, e, newNoise, depth - 1);
		displace(g, b, e, h, newNoise, depth - 1);
		displace(f, e, c, i, newNoise, depth - 1);
		displace(e, h, i, d, newNoise, depth - 1);
	}

	private Vector3f displace(Vector3f a, Vector3f c, float noise, float e) {
		Vector3f ePos = new Vector3f();
		ePos.add(a);
		ePos.add(c);
		ePos.scale(1 / 2f);
		float newE = ePos.y;
		heightMap[(int) ePos.x][(int) ePos.z].y = newE;
		return heightMap[(int) ePos.x][(int) ePos.z];
	}

	private FloatBuffer getNormals(FloatBuffer buf, IntBuffer indices,
			int offset) {
		int size = (WIDTH + 1) * (HEIGHT + 1) * 3;
		normalArray = new ArrayList<Vector3f>(size);
		for (int i = 0; i < indices.capacity(); i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);
			Vector3f v0 = getVertex(i0 - offset);
			Vector3f v1 = getVertex(i1 - offset);
			Vector3f v2 = getVertex(i2 - offset);
			v1.sub(v0);
			v2.sub(v0);
			Vector3f normal = new Vector3f();
			normal.cross(v1, v2);
			normal.normalize();
			addTo(normalArray, normal, i0);
			addTo(normalArray, normal, i1);
			addTo(normalArray, normal, i2);
		}
		for (Vector3f n : normalArray) {
			n.normalize();
		}
		Log.log(this, buf.limit() / 3, normalArray.size(), size);
		for (Vector3f n : normalArray) {
			buf.put(n.x);
			buf.put(n.y);
			buf.put(n.z);
		}
		// indices.rewind();
		// buf.rewind();
		return buf;
	}

	private void addTo(List<Vector3f> normals, Vector3f normal, int i) {
		while (normals.size() <= i)
			normals.add(new Vector3f());
		Vector3f currNormal = normals.get(i);
		currNormal.add(normal);
	}

	private Vector3f getVertex(int i) {
		Vector3f vec = (Vector3f) heightMap[i / (HEIGHT + 1)][i % (HEIGHT + 1)]
				.clone();
		vec.y *= WIDTH + 1;
		return vec;
	}

	private IntBuffer getIndices(IntBuffer buf, int offset) {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				// v0
				int first = (HEIGHT + 1) * i + j;
				// v1
				int second = first + HEIGHT + 1;
				buf.put(first + offset);
				buf.put(second + 1 + offset);
				buf.put(second + offset);
				buf.put(first + offset);
				buf.put(first + 1 + offset);
				buf.put(second + 1 + offset);
			}

		}
		return buf;
	}

	private FloatBuffer getVertices(FloatBuffer buf) {
		float terrainOffset = 1.0f / (2 * (WIDTH + 1));
		for (int i = 0; i < WIDTH + 1; i++) {
			for (int j = 0; j < HEIGHT + 1; j++) {
				buf.put((float) i / (WIDTH + 1) - 0.5f + terrainOffset);
				buf.put(heightMap[i][j].y);
				buf.put((float) j / (HEIGHT + 1) - 0.5f + terrainOffset);
			}
		}
		return buf;
	}

	protected boolean isStatic() {
		return false;
	}

	public static float[] getHeightField() {
		float[] heights = new float[(STATIC_WIDTH + 1) * (STATIC_HEIGHT + 1)];
		for (int i = 0; i < STATIC_WIDTH + 1; i++)
			for (int j = 0; j < STATIC_HEIGHT + 1; j++)
				heights[i * (STATIC_WIDTH + 1) + j] = biggestHeightMap[j][i].y;
		return heights;
	}

	@Override
	public void init(GL2 gl) {
		// gl.glDisable(GL2.GL_CULL_FACE);
		// offset += 3 * 10;
		// if ((Game.INSTANCE.loop.tick % 1000) < 500) {
		// indexCount = (STATIC_WIDTH / 2) * (STATIC_HEIGHT / 2) * 6
		// + (STATIC_WIDTH) * (STATIC_HEIGHT) * 6;
		// offset = NUM;
		// } else {
		// indexCount = (STATIC_WIDTH) * (STATIC_HEIGHT) * 6;
		// offset = 0;
		// }
		super.init(gl);
	}

	@Override
	public void end(GL2 gl) {
		super.end(gl);
		// gl.glEnable(GL2.GL_CULL_FACE);
	}

	public int getIndexNumberToRender() {
		if ((Game.INSTANCE.loop.tick % 1000) < 500)
			return 0;
		else
			return 1;
	}

}
