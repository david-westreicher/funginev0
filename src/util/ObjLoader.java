package util;

import io.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjLoader {

	/**
	 * @uml.property name="verticesList"
	 */
	public Map<String, Integer> indicesMap = new HashMap<String, Integer>();
	public List<Float> correctVertices = new ArrayList<Float>();
	public List<Float> correctNormals = new ArrayList<Float>();
	public List<Integer> correctIndices = new ArrayList<Integer>();
	public List<Float> normalList = new ArrayList<Float>();
	public List<Float> verticesList = new ArrayList<Float>();
	/**
	 * @uml.property name="indicesList"
	 */
	public List<Integer> indicesList = new ArrayList<Integer>();
	/**
	 * @uml.property name="vertices"
	 */
	public FloatBuffer vertices;
	/**
	 * @uml.property name="indices"
	 */
	public IntBuffer indices;
	public FloatBuffer normals;
	/**
	 * @uml.property name="vertexCount"
	 */
	public int vertexCount;
	private boolean hasNormals;

	public ObjLoader(String s) {
		BufferedReader br = IO.read(s);
		String line;
		int polCount = 0;
		try {
			while ((line = br.readLine()) != null && !line.startsWith("e")) {
				String[] split = line.split("\\s+");
				if (line.startsWith("v ")) {
					for (int i = 0; i < 3; i++) {
						verticesList.add(Float.parseFloat(split[i + 1]));
					}
				}
				if (line.startsWith("vn")) {
					hasNormals = true;
					
					for (int i = 0; i < 3; i++) {
						normalList.add(Float.parseFloat(split[i + 1]));
					}
				}
				if (line.startsWith("f")) {
					if (split.length == 4) {
						for (int i = 0; i < 3; i++) {
							if (hasNormals)
								put(split[i + 1]);
							indicesList.add(Integer.parseInt(split[i + 1]
									.split("/")[0]) - 1);
						}
					}
					if (split.length == 5) {
						for (int i = 0; i < 3; i++) {
							if (hasNormals)
								put(split[i + 1]);
							indicesList.add(Integer.parseInt(split[i + 1]
									.split("/")[0]) - 1);
						}

						if (hasNormals) {
							put(split[2]);
							put(split[4]);
							put(split[3]);
						}
						indicesList
								.add(Integer.parseInt(split[2].split("/")[0]) - 1);
						indicesList
								.add(Integer.parseInt(split[4].split("/")[0]) - 1);
						indicesList
								.add(Integer.parseInt(split[3].split("/")[0]) - 1);
					}
					polCount++;
				}
			}
			if (hasNormals) {
				normalize(correctVertices);
				//Log.log(this, "normallist: " + normalList);
				//Log.log(this, "normals: " + correctNormals);
				//Log.log(this, "vertices: " + correctVertices);
				//Log.log(this, "indices: " + correctIndices);
				vertices = fillFloat(correctVertices);
				indices = fillInt(correctIndices);
				normals = fillFloat(correctNormals);
			} else {
				normalize(verticesList);
				vertices = fillFloat(verticesList);
				indices = fillInt(indicesList);
			}
			/*
			 * normals = IntBuffer.allocate(normalList.size()); for (Integer i :
			 * normalList) Log.log(this, verticesList.size() / 3,
			 * indicesList.size() / 3, polCount);
			 */
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

	private IntBuffer fillInt(List<Integer> arr) {
		IntBuffer vertices = IntBuffer.allocate(arr.size());
		for (Integer i : arr)
			vertices.put(i);
		vertices.rewind();
		return vertices;
	}

	private FloatBuffer fillFloat(List<Float> arr) {
		FloatBuffer vertices = FloatBuffer.allocate(arr.size());
		for (Float f : arr)
			vertices.put(f);
		vertices.rewind();
		return vertices;
	}

	private void put(String val) {
		Integer index = indicesMap.get(val);
		if (index == null) {
			index = indicesMap.size();
			indicesMap.put(val, index);
			int normalIndex = Integer.parseInt(val.split("/")[2]) - 1;
			int vertexIndex = Integer.parseInt(val.split("/")[0]) - 1;
			for (int i = 0; i < 3; i++) {
				correctNormals.add(normalList.get(normalIndex * 3 + i));
				correctVertices.add(verticesList.get(vertexIndex * 3 + i));
			}
		}
		correctIndices.add(index);
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
