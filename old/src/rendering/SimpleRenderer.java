package rendering;

import game.Game;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

import shader.ShaderScript;
import util.Log;
import world.GameObject;

public class SimpleRenderer extends RenderUpdater {

	private List<Integer[]> fobs = new ArrayList<Integer[]>();
	private ShaderScript sscript;
	private ShaderScript sscript2;

	public SimpleRenderer() {
		sscript = new ShaderScript("shader\\shadowMap.glsl");
		sscript2 = new ShaderScript("shader\\renderDepth.glsl");
		super.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				createTex(0);
				createShadowFob(1);
			}
		});
	}

	@Override
	protected void renderObjects() {

		rttStart(fobs.get(0)[1]);
		{
			super.renderObjects();
		}
		rttEnd();
		List<GameObject> list = renderObjs.get("CamTest");
		if (list != null && list.size() > 0) {
			GameObject go = list.get(0);
			createShadowMap(go);
			setupTextureMatrix(go);
			drawScene();
			gl.glColorMask(true, true, true, true);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			drawScene();
			super.startOrthoRender();
			{
				sscript2.execute(gl);
				drawTex(fobs.get(1)[0], 0.25f);
				sscript2.end(gl);
			}
			super.endOrthoRender();
			resetTextureMatrix();
		}

	}

	private void drawScene() {
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, fobs.get(1)[0]);
		sscript.execute(gl);
		super.renderObjects();
		sscript.end(gl);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	private void resetTextureMatrix() {
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	private void createShadowMap(GameObject go) {
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fobs.get(1)[1]);
		{
			gl.glPushMatrix();
			gl.glLoadIdentity();
			super.setupLook(go);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
			gl.glColorMask(false, false, false, false);
			super.renderObjects();
			gl.glPopMatrix();
		}
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
	}

	private void setupTextureMatrix(GameObject go) {
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glTranslatef(0.5f, 0.5f, 0.5f);
		gl.glScalef(0.5f, 0.5f, 0.5f);
		glu.gluPerspective(FOV_Y, (float) width / height, ZNear, ZFar);
		super.setupLook(go);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	private void drawTex(int tex, float scale) {
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1, 1, 1, 1);

		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(width * scale, 0, 0);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(width * scale, height * scale, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(0, height * scale, 0);
		gl.glEnd();

		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_BLEND);

	}

	private void rttStart(int buf) {
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, buf);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	private void rttEnd() {
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
	}

	private void drawTexture(int tex) {
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(width, 0, 0);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(width, height, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(0, height, 0);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_BLEND);
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
		;

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
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP_TO_EDGE);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_DEPTH_TEXTURE_MODE,
				GL2.GL_LUMINANCE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE,
				GL2.GL_COMPARE_R_TO_TEXTURE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_FUNC,
				GL2.GL_LEQUAL);

		/*
		 * gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0,
		 * GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, Buffers.newDirectByteBuffer(width *
		 * height * 4));
		 */
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
