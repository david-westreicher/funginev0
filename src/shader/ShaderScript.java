package shader;

import game.Game;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import manager.ShaderManager;
import rendering.RenderUpdater;
import util.Log;

public class ShaderScript {

	private static ShaderScript activatedShader = null;
	/**
	 * @uml.property name="shaderNum"
	 */
	public int shaderNum;
	private String name;

	public ShaderScript(String file) {
		this.name = file;
		((ShaderManager) Game.INSTANCE.getManager("shader")).update(file, this);
	}

	public void execute(GL2 gl) {
		if (shaderNum != 0) {
			if (activatedShader != null) {
				Log.log(this, activatedShader + " and " + this);
				throw new RuntimeException("activating shader in shader");
			}
			activatedShader = this;
			gl.glUseProgram(shaderNum);
		}
	}

	public void end(GL2 gl) {
		activatedShader = null;
		gl.glUseProgram(0);
	}

	public static void setUniform(String str, float[] pos) {
		int location = RenderUpdater.gl.glGetUniformLocation(getActiveShader(),
				str);
		switch (pos.length) {
		case 2:
			RenderUpdater.gl.glUniform2fv(location, 1, pos, 0);
			break;
		case 3:
			RenderUpdater.gl.glUniform3fv(location, 1, pos, 0);
			break;
		case 4:
			RenderUpdater.gl.glUniform4fv(location, 1, pos, 0);
			break;
		}
	}

	public static void setUniform(String str, float time) {
		int location = RenderUpdater.gl.glGetUniformLocation(getActiveShader(),
				str);
		RenderUpdater.gl.glUniform1f(location, time);
	}

	public static void setUniform(String str, int time) {
		int location = RenderUpdater.gl.glGetUniformLocation(getActiveShader(),
				str);
		RenderUpdater.gl.glUniform1i(location, time);
	}

	public static void setUniform3fv(String str, float[] scales) {
		int location = RenderUpdater.gl.glGetUniformLocation(getActiveShader(),
				str);
		RenderUpdater.gl.glUniform3fv(location, scales.length / 3, scales, 0);
	}

	public static void setUniformMatrix(String str, FloatBuffer matrix,
			boolean transpose) {
		int location = RenderUpdater.gl.glGetUniformLocation(getActiveShader(),
				str);
		int capacity = matrix.capacity();
		if (capacity % 3 == 0)
			RenderUpdater.gl.glUniformMatrix3fv(location, capacity / 9,
					transpose, matrix);
		else if (capacity % 4 == 0)
			RenderUpdater.gl.glUniformMatrix4fv(location, capacity / 16,
					transpose, matrix);
		else
			throw new RuntimeException("Capacity was " + matrix.capacity());
	}

	public static void setUniform3fv(String str, FloatBuffer scales) {
		int location = RenderUpdater.gl.glGetUniformLocation(getActiveShader(),
				str);
		RenderUpdater.gl.glUniform3fv(location, scales.limit() / 3, scales);
	}

	public static void setUniformTexture(String string, int num, int texId) {
		int location = RenderUpdater.gl.glGetUniformLocation(getActiveShader(),
				string);
		RenderUpdater.gl.glUniform1i(location, num);
		switch (num) {
		case 0:
			RenderUpdater.gl.glActiveTexture(GL2.GL_TEXTURE0);
			break;
		case 1:
			RenderUpdater.gl.glActiveTexture(GL2.GL_TEXTURE1);
			break;
		case 2:
			RenderUpdater.gl.glActiveTexture(GL2.GL_TEXTURE2);
			break;
		}
		RenderUpdater.gl.glBindTexture(GL2.GL_TEXTURE_2D, texId);
		RenderUpdater.gl.glActiveTexture(GL2.GL_TEXTURE0);
	}

	private static int getActiveShader() {
		return activatedShader.shaderNum;
	}

	public void deleteShader(GL2 gl) {
		gl.glDeleteShader(shaderNum);
	}

	public static boolean isShaderActivated(ShaderScript transformShader) {
		return activatedShader == transformShader;
	}

	public String toString() {
		return name;
	}

	public static boolean isShaderActivated() {
		return activatedShader != null;
	}

}
