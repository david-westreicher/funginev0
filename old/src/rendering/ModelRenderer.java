package rendering;

import game.Game;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import settings.Settings;
import shader.ShaderScript;
import util.Log;
import util.ObjLoader;
import world.GameObject;
import world.GameObjectType;

public class ModelRenderer extends GameObjectRenderer {

	private int[] vboVertices = new int[1];
	private int[] vboIndices = new int[1];
	private int vertexCount;
	private int indexCount;
	private IntBuffer indices;
	private static final int NUM_RENDERED_INST = 40;
	private static ShaderScript transformShader = new ShaderScript(
			"shader\\transform.glsl");
	private FloatBuffer[] transformBuffers;

	public ModelRenderer(String s) {
		super(false);
		ObjLoader obj = new ObjLoader(s);
		final FloatBuffer vertices = obj.vertices;
		indices = obj.indices;
		vertexCount = vertices.capacity();
		indexCount = indices.capacity();
		RenderUpdater.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				GL2 gl = RenderUpdater.gl;
				int[] vars = new int[1];
				gl.glGetIntegerv(GL2.GL_MAX_VERTEX_UNIFORM_COMPONENTS, vars, 0);
				Log.log(this, "GL_MAX_VERTEX_UNIFORM_COMPONENTS:" + vars[0]);
				gl.glGenBuffers(1, vboVertices, 0);
				gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
				gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboVertices[0]);
				gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3
						* Buffers.SIZEOF_FLOAT, vertices, GL2.GL_STATIC_DRAW);
				gl.glGenBuffers(1, vboIndices, 0);
				gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vboIndices[0]);
				gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indexCount
						* Buffers.SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);
			}
		});
		allocateBuffers(6, NUM_RENDERED_INST * 3);
	}

	@Override
	public void init(GL2 gl) {
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboVertices[0]);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vboIndices[0]);
		if (Game.WIREFRAME)
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		else
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
	}

	@Override
	public void end(GL2 gl) {
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}

	@Override
	public void draw(GL2 gl, List<GameObject> gos, float interp) {
		// Optimize!!!!
		gl.glColor4f(1, 1, 1, 1);

		int start = 0;
		transformShader.execute(gl);
		while (start < gos.size()) {
			int instancesNum = 0;
			rewindBuffers();
			for (int i = start; i < Math.min(gos.size(), start
					+ NUM_RENDERED_INST); i++) {
				instancesNum++;
				GameObject go = gos.get(i);
				putBuffer(go.size, go.pos, go.oldPos, go.rotation,
						go.oldRotation, go.color);
			}
			rewindBuffers();
			transformShader.setUniform3fv("scaleArr", transformBuffers[0]);
			transformShader.setUniform3fv("translateArr", transformBuffers[1]);
			transformShader.setUniform3fv("translateOldArr",
					transformBuffers[2]);
			transformShader.setUniform3fv("rotationArr", transformBuffers[3]);
			transformShader
					.setUniform3fv("rotationOldArr", transformBuffers[4]);
			transformShader.setUniform3fv("colorArr", transformBuffers[5]);
			transformShader.setUniform("interp", interp);
			gl.glDrawElementsInstanced(GL2.GL_TRIANGLES, indexCount,
					GL2.GL_UNSIGNED_INT, null, instancesNum);
			start += NUM_RENDERED_INST;
		}
		transformShader.end(gl);
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
		transformBuffers = new FloatBuffer[num];
		for (int i = 0; i < num; i++)
			transformBuffers[i] = FloatBuffer.allocate(size);
	}

	@Override
	public void draw(GL2 gl) {
		gl.glColor3fv(RenderUpdater.cgo.color, 0);
		gl.glDrawElements(GL2.GL_TRIANGLES, indexCount, GL2.GL_UNSIGNED_INT, 0);
	}

}
