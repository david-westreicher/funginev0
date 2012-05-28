package rendering;

import game.Game;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

import shader.ShaderScript;
import util.Log;

public class ShadowMapRenderer extends RenderUpdater {
	private ShaderScript shadowShader;
	private List<Integer[]> fobs = new ArrayList<Integer[]>();

	public ShadowMapRenderer() {
		shadowShader = new ShaderScript("shader\\shadowMap.glsl");
		super.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				createTex(0);
				createShadowFob(1);
			}
		});
	}

	public void renderObjects() {
		rttStart(fobs.get(0)[1]);
		
		super.renderObjects();
		rttEnd();
	}

	private void rttStart(int buf) {
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, buf);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	private void rttEnd() {
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
	}


	private void createTex(int fobNum) {
		int[] fboId = new int[1];
		int[] texId = new int[1];
		int[] depId = new int[1];
		gl.glGenFramebuffers(1, fboId, 0);
		gl.glGenTextures(1, texId, 0);
		gl.glGenRenderbuffers(1, depId, 0);

		gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, depId[0]);
		gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT,
				width, height);
		

		gl.glBindTexture(GL.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0,
				GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
				Buffers.newDirectByteBuffer(width * height * 4));

		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fboId[0]);
		gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
				GL.GL_TEXTURE_2D, texId[0], 0);
		gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER,
				GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, depId[0]);

		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		Integer[] fob = new Integer[] { texId[0], fboId[0], depId[0] };
		if (fobs.size() < fobNum)
			fobs.set(fobNum, fob);
		else
			fobs.add(fob);
		int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		if (status == GL2.GL_FRAMEBUFFER_COMPLETE) {
			gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
			Log.log(this, "Frame buffer object successfully created");
		} else {
			throw new IllegalStateException("Frame Buffer Oject not created.");
		}
	}

	private void createShadowFob(int fobNum) {
		int[] fboId = new int[1];
		int[] texId = new int[1];
		gl.glGenFramebuffers(1, fboId, 0);
		gl.glGenTextures(1, texId, 0);

		gl.glBindTexture(GL.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		//gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0,
		//		GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
		//		Buffers.newDirectByteBuffer(width * height * 4));
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT, width,
				height, 0, GL2.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE,
				Buffers.newDirectByteBuffer(width * height * 4));

		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fboId[0]);

		gl.glDrawBuffer(GL2.GL_NONE);
		gl.glReadBuffer(GL2.GL_NONE);

		gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT,
				GL.GL_TEXTURE_2D, texId[0], 0);

		Integer[] fob = new Integer[] { texId[0], fboId[0] };
		if (fobs.size() < fobNum)
			fobs.set(fobNum, fob);
		else
			fobs.add(fob);
		int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		if (status == GL2.GL_FRAMEBUFFER_COMPLETE) {
			gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
			Log.log(this, "Frame buffer object successfully created");
		} else {
			throw new IllegalStateException("Frame Buffer Oject not created.");
		}
	}
}
