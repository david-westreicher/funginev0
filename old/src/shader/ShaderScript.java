package shader;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import rendering.RenderUpdater;

import util.Log;

import game.Game;
import manager.ShaderManager;
import manager.SpriteManager;

public class ShaderScript {

	public int shaderNum;

	public ShaderScript(String file) {
		((ShaderManager) Game.INSTANCE.getManager("shader")).update(file, this);
	}

	public void execute(GL2 gl) {
		if (shaderNum != 0) {
			gl.glUseProgram(shaderNum);
		}
	}

	public void end(GL2 gl) {
		gl.glUseProgram(0);
	}

	public void setUniform(String str, float[] pos) {
		int location = RenderUpdater.gl.glGetUniformLocation(shaderNum, str);
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

	public void setUniform(String str, float time) {
		int location = RenderUpdater.gl.glGetUniformLocation(shaderNum, str);
		RenderUpdater.gl.glUniform1f(location, time);
	}

	public void setUniform(String str, int time) {
		int location = RenderUpdater.gl.glGetUniformLocation(shaderNum, str);
		RenderUpdater.gl.glUniform1i(location, time);
	}

	public void setUniform3fv(String str, float[] scales) {
		int location = RenderUpdater.gl.glGetUniformLocation(shaderNum, str);
		RenderUpdater.gl.glUniform3fv(location, scales.length / 3, scales, 0);
	}

	public void setUniform3fv(String str, FloatBuffer scales) {
		int location = RenderUpdater.gl.glGetUniformLocation(shaderNum, str);
		RenderUpdater.gl.glUniform3fv(location, scales.limit() / 3, scales);
	}

	public void setUniformTexture(String string, int num, int texId) {
		int location = RenderUpdater.gl.glGetUniformLocation(shaderNum, string);
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

}
