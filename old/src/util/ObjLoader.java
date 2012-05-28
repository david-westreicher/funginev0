package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import settings.Settings;
import io.IO;

public class ObjLoader {

	public List<Float> verticesList = new ArrayList<Float>();
	public List<Integer> indicesList = new ArrayList<Integer>();
	public FloatBuffer vertices;
	public IntBuffer indices;
	public int vertexCount;

	public ObjLoader(String s) {
		BufferedReader br = IO.read(s);
		String line;
		int polCount = 0;
		try {
			while ((line = br.readLine()) != null && !line.startsWith("e")) {
				if (line.startsWith("v ")) {
					String[] split = line.split("\\s+");
					for (int i = 0; i < 3; i++) {
						verticesList.add(Float.parseFloat(split[i + 1]));
					}
				}
				if (line.startsWith("f")) {
					String[] split = line.split("\\s+");
					if (split.length == 4) {
						for (int i = 0; i < 3; i++) {
							indicesList.add(Integer.parseInt(split[i + 1]
									.split("/")[0]) - 1);
						}
					}
					if (split.length == 5) {
						for (int i = 0; i < 3; i++) {
							indicesList.add(Integer.parseInt(split[i + 1]
									.split("/")[0]) - 1);
						}
						for (int i = 1; i < 4; i++) {
							indicesList.add(Integer.parseInt(split[2]
									.split("/")[0]) - 1);
							indicesList.add(Integer.parseInt(split[4]
									.split("/")[0]) - 1);
							indicesList.add(Integer.parseInt(split[3]
									.split("/")[0]) - 1);
						}
					}
					polCount++;
				}
			}
			normalize(verticesList);
			vertices = FloatBuffer.allocate(verticesList.size());
			for (Float f : verticesList)
				vertices.put(f);
			vertices.rewind();
			indices = IntBuffer.allocate(indicesList.size());
			for (Integer f : indicesList)
				indices.put(f);
			indices.rewind();
			Log.log(this, verticesList.size() / 3, indicesList.size() / 3,
					polCount);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void normalize(List<Float> vertices) {
		// xyz minMax bounds
		float[][] bounds = new float[][] { new float[2], new float[2],
				new float[2] };
		int i = 0;
		for (Float v : vertices) {
			check(v, bounds[i++ % 3]);
		}
		// move with map[0], scale with map[1]
		float[][] map = new float[3][];
		for (i = 0; i < 3; i++) {
			float length = bounds[i][1] - bounds[i][0];
			map[i] = new float[] { -bounds[i][0] - length / 2, length };
		}
		float length = Math.max(Math.max(map[0][1], map[1][1]), map[2][1]);
		Log.log(this, "normalizing: " + length);
		for (i = 0; i < vertices.size(); i++) {
			Float curr = vertices.get(i);
			float[] m = map[i % 3];
			vertices.set(i, (curr + m[0]) / length);
		}
	}

	private float dist(float x, float y, float z) {

		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	private void check(Float v, float[] xMM) {
		if (xMM[0] > v)
			xMM[0] = v;
		if (xMM[1] < v)
			xMM[1] = v;
	}

}
