package rendering;

import game.Game;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.vecmath.Matrix3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import settings.Settings;
import shader.ShaderScript;
import util.Log;
import util.ObjLoader;
import world.GameObject;
import world.GameObjectType;

public class ModelRenderer extends GameObjectRenderer {

	/**
	 * @uml.property name="vboVertices" multiplicity="(0 -1)" dimension="1"
	 */
	private int[] vboVertices = new int[1];
	/**
	 * @uml.property name="vboIndices" multiplicity="(0 -1)" dimension="1"
	 */
	private int[] vboIndices;
	private int[] vboNormals = new int[1];
	private int[] vboColors = new int[1];
	/**
	 * @uml.property name="vertexCount"
	 */
	private int vertexCount;
	/**
	 * @uml.property name="indexCount"
	 */
	protected int[] indexCounts;
	/**
	 * @uml.property name="indices"
	 */
	private static final int NUM_RENDERED_INST = 30;
	private static ShaderScript transformShader = new ShaderScript(
			"shader\\transform.glsl");
	/**
	 * @uml.property name="transformBuffers" multiplicity="(0 -1)" dimension="1"
	 */
	private FloatBuffer[] transformBuffers;
	private boolean hasNormals;
	private boolean hasColors;
	private boolean hasIndices;

	public ModelRenderer() {
		super(true);
	}

	public ModelRenderer(String s) {
		this(new ObjLoader(s));
	}

	public ModelRenderer(final FloatBuffer vertices, final FloatBuffer normals,
			final IntBuffer... multiIndices) {
		super(true);
		init(vertices, normals, null, multiIndices);
	}

	protected void init(final FloatBuffer vertices, final FloatBuffer normals,
			final FloatBuffer colors, final IntBuffer... multiIndices) {
		hasNormals = normals != null;
		hasColors = colors != null;
		hasIndices = multiIndices != null;
		vertexCount = vertices.capacity() / 3;
		Log.log(this, "Vertices: " + vertexCount / 3);
		// multiple index buffers
		if (hasIndices) {
			vboIndices = new int[multiIndices.length];
			indexCounts = new int[multiIndices.length];
			int i = 0;
			for (IntBuffer indices : multiIndices) {
				Log.log(this, "indices: " + indices);
				indexCounts[i++] = indices.capacity();
			}
		}

		if (hasNormals)
			Log.log(this, "Normals: " + normals.capacity() / 3);
		final int staticDraw = isStatic() ? GL2.GL_STATIC_DRAW
				: GL2.GL_DYNAMIC_DRAW;
		RenderUpdater.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				GL2 gl = RenderUpdater.gl;
				if (vboVertices[0] == 0)
					gl.glGenBuffers(1, vboVertices, 0);
				gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboVertices[0]);
				gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3
						* Buffers.SIZEOF_FLOAT, vertices, staticDraw);
				// if (vboIndices[0] == 0)
				if (hasIndices) {
					gl.glGenBuffers(vboIndices.length, vboIndices, 0);
					for (int i = 0; i < vboIndices.length; i++) {
						gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,
								vboIndices[i]);
						gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,
								indexCounts[i] * Buffers.SIZEOF_INT,
								multiIndices[i], staticDraw);
					}
				}
				if (hasNormals) {
					if (vboNormals[0] == 0)
						gl.glGenBuffers(1, vboNormals, 0);
					gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboNormals[0]);
					gl.glBufferData(GL2.GL_ARRAY_BUFFER, normals.capacity()
							* Buffers.SIZEOF_FLOAT, normals, staticDraw);
				}
				if (hasColors) {
					if (vboColors[0] == 0)
						gl.glGenBuffers(1, vboColors, 0);
					gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboColors[0]);
					gl.glBufferData(GL2.GL_ARRAY_BUFFER, colors.capacity()
							* Buffers.SIZEOF_FLOAT, colors, staticDraw);
				}
			}
		});
		allocateBuffers(3, NUM_RENDERED_INST * 3);
	}

	protected boolean isStatic() {
		return true;
	}

	public ModelRenderer(ObjLoader loader) {
		this(loader.vertices, loader.normals, loader.indices);
	}

	@Override
	public void init(GL2 gl) {
		// Optimize!!!!
		gl.glColor4f(1, 1, 1, 1);
		if (Game.WIREFRAME)
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		else
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		if (hasIndices)
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,
					vboIndices[getIndexNumberToRender()]);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboVertices[0]);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
		if (hasNormals) {
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboNormals[0]);
			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL.GL_FLOAT, 0, 0);
		}
		if (hasColors) {
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboColors[0]);
			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl.glColorPointer(3, GL2.GL_FLOAT, 0, 0);
		}
	}

	@Override
	public void end(GL2 gl) {
		if (hasIndices)
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		if (hasNormals) {
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		}
		if (hasColors) {
			gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		}
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}

	@Override
	public void draw(GL2 gl, List<GameObject> gos, float interp) {

		int start = 0;
		if (!ShaderScript.isShaderActivated())
			transformShader.execute(gl);
		while (start < gos.size()) {
			int instancesNum = 0;
			rewindBuffers();
			for (int i = start; i < Math.min(gos.size(), start
					+ NUM_RENDERED_INST); i++) {
				instancesNum++;
				GameObject go = gos.get(i);
				putBuffer(go.size, go.pos, go.color, go.rotationMatrixArray);
				// Log.log(this,getRotMatrix(go));
			}
			rewindBuffers();
			ShaderScript.setUniform3fv("scaleArr", transformBuffers[0]);
			ShaderScript.setUniform3fv("translateArr", transformBuffers[1]);
			// transformShader.setUniform3fv("translateOldArr",
			// transformBuffers[2]);
			// transformShader.setUniform3fv("rotationArr",
			// transformBuffers[3]);
			// transformShader
			// .setUniform3fv("rotationOldArr", transformBuffers[4]);
			ShaderScript.setUniform3fv("colorArr", transformBuffers[2]);
			ShaderScript.setUniformMatrix("rotationMatrices",
					transformBuffers[3], true);
			// transformShader.setUniform("interp", interp);
			if (hasIndices)
				gl.glDrawElementsInstanced(GL2.GL_TRIANGLES,
						indexCounts[getIndexNumberToRender()],
						GL2.GL_UNSIGNED_INT, null, instancesNum);
			else
				gl.glDrawArraysInstanced(GL2.GL_POINTS, 0, vertexCount,
						instancesNum);
			start += NUM_RENDERED_INST;
		}
		if (ShaderScript.isShaderActivated(transformShader))
			transformShader.end(gl);
	}

	protected int getIndexNumberToRender() {
		return 0;
	}

	private void putBuffer(float[]... puts) {
		for (int i = 0; i < transformBuffers.length; i++)
			transformBuffers[i].put(puts[i]);
	}

	private void rewindBuffers() {
		for (int i = 0; i < transformBuffers.length; i++)
			transformBuffers[i].rewind();
	}

	private void allocateBuffers(int num, int size) {
		transformBuffers = new FloatBuffer[num + 1];
		for (int i = 0; i < num; i++)
			transformBuffers[i] = FloatBuffer.allocate(size);
		transformBuffers[num] = FloatBuffer.allocate(NUM_RENDERED_INST * 9);
	}

	@Override
	public void draw(GL2 gl) {
		gl.glColor3fv(RenderUpdater.cgo.color, 0);
		gl.glDrawElements(GL2.GL_TRIANGLES,
				indexCounts[getIndexNumberToRender()], GL2.GL_UNSIGNED_INT, 0);
	}

}
