package rendering;

import game.Game;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import shader.ShaderScript;
import util.Log;
import world.GameObject;

import com.jogamp.common.nio.Buffers;

public class SimpleRenderer extends RenderUpdater {

	private static final int SHADOW_MAP_SIZE = 4096;
	private static final float SKYBOX_SCALE = 500;
	public static float OFFSET_A = 1;
	public static float OFFSET_B = 0;
	public static float FOV = 60;
	private List<Integer[]> fobs = new ArrayList<Integer[]>();
	private FloatBuffer lightMatrix = Buffers.newDirectFloatBuffer(16);
	private FloatBuffer projectionMatrix = Buffers.newDirectFloatBuffer(16);
	private FloatBuffer modelViewMatrix = Buffers.newDirectFloatBuffer(16);
	private double[] bias = new double[] { 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0,
			0.0, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0.5, 1.0 };
	private ShaderScript renderDepth;
	private ShaderScript shadowMapScript;
	private ShaderScript textureShader;
	private ShaderScript dofShader;

	public SimpleRenderer() {
		shadowMapScript = new ShaderScript("shader\\shadowMap.glsl");
		renderDepth = new ShaderScript("shader\\renderDepth.glsl");
		textureShader = new ShaderScript("shader\\textureShader.glsl");
		dofShader = new ShaderScript("shader\\glow.glsl");
		super.executeInOpenGLContext(new Runnable() {
			@Override
			public void run() {
				// blend map
				createTex(0);
				// shadow map
				createShadowFob(1, SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
				// depth map
				createShadowFob(2, width, height);
				// debug map
				createTex(3, width, height / 4);
				// dof shader map
				createTex(4);
			}
		});
		super.excludeGameObjectFromRendering("Skybox-b");
		super.excludeGameObjectFromRendering("Skybox-d");
		super.excludeGameObjectFromRendering("Skybox-f");
		super.excludeGameObjectFromRendering("Skybox-l");
		super.excludeGameObjectFromRendering("Skybox-r");
		super.excludeGameObjectFromRendering("Skybox-u");
	}

	protected void renderObjects() {
		if (fobs.size() == 5) {
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fobs.get(2)[1]);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
			gl.glColorMask(false, false, false, false);
			super.renderObjects();
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
			gl.glColorMask(true, true, true, true);

			if (Game.DEBUG) {
				rttStart(fobs.get(3)[1], true);
				rttEnd();
			}
			if (DOFRenderer.POST_PROCESS) {
				rttStart(fobs.get(4)[1], true);
				rttEnd();
			}

			rttStart(fobs.get(0)[1], false);
			renderSkyBox();
			rttEnd();
			List<GameObject> cameras = renderObjs.get("CamTest");
			int cameraNum = 0;
			if (cameras != null) {
				renderStrings.add("Lights    :  " + cameras.size());
				boolean isFirst = true;
				for (GameObject cam : cameras) {
					createShadowMap(cam);
					// rttStart(fobs.get(0)[1], true);
					gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fobs.get(0)[1]);
					gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
					{
						gl.glEnable(GL.GL_BLEND);
						gl.glBlendFunc(GL.GL_SRC_ALPHA,
								GL.GL_ONE_MINUS_SRC_ALPHA);
						setModelViewMatrix();
						shadowMapScript.execute(gl);
						ShaderScript.setUniformTexture("shadowTexture", 0,
								fobs.get(1)[0]);
						ShaderScript.setUniform("lightPos", cam.pos);
						// gl.glPolygonOffset(OFFSET_A, OFFSET_B);
						// gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
						super.renderObjects();
						// gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
						shadowMapScript.end(gl);
						gl.glDisable(GL.GL_BLEND);
					}
					rttEnd();
					super.startOrthoRender();
					{
						if (DOFRenderer.POST_PROCESS)
							gl.glBindFramebuffer(GL.GL_FRAMEBUFFER,
									fobs.get(4)[1]);
						{
							gl.glEnable(GL.GL_BLEND);
							gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
							gl.glBlendEquation(GL2.GL_FUNC_ADD);
							{
								drawTexture(0, 0, width, height,
										fobs.get(0)[0], false);
							}
							gl.glDisable(GL.GL_BLEND);
						}
						if (DOFRenderer.POST_PROCESS)
							rttEnd();
						if (Game.DEBUG) {
							gl.glBindFramebuffer(GL.GL_FRAMEBUFFER,
									fobs.get(3)[1]);
							renderDepth.execute(gl);
							ShaderScript.setUniform("zNear",
									RenderUpdater.ZNear);
							ShaderScript.setUniform("zFar", RenderUpdater.ZFar);
							ShaderScript.setUniformTexture("depth", 0,
									fobs.get(1)[0]);
							drawQuad(cameraNum * height / 4, 3 * height / 4,
									height / 4, height / 4, true);
							renderDepth.end(gl);
							rttEnd();
						}
						cameraNum++;
					}
					super.endOrthoRender();
					isFirst = false;
				}
			} else {
				renderStrings.add("Lights    :  " + 0);
				super.renderObjects();
			}

			if (DOFRenderer.POST_PROCESS || Game.DEBUG) {
				super.startOrthoRender();
				gl.glDisable(GL2.GL_DEPTH_TEST);
				if (DOFRenderer.POST_PROCESS) {
					dofShader.execute(gl);
					// float distanceFromCam =
					// PhysicsTest.getInstance().rayTest(
					// cam, ZFar);
					ShaderScript.setUniform("focus", cam.focus);
					ShaderScript.setUniform("zNear", ZNear);
					ShaderScript.setUniform("zFar", ZFar);
					ShaderScript.setUniform("aspectRatio", (float) width
							/ height);
					ShaderScript.setUniformTexture("depthTex", 0,
							fobs.get(2)[0]);
					ShaderScript.setUniformTexture("ambTex", 1, fobs.get(4)[0]);
					// gl.glEnable(GL.GL_BLEND);
					// gl.glBlendFunc(GL.GL_SRC_ALPHA,
					// GL.GL_ONE_MINUS_SRC_ALPHA);
					gl.glEnable(GL.GL_TEXTURE_2D);
					gl.glColor4f(1, 1, 1, 1);

					gl.glBegin(GL.GL_TRIANGLE_FAN);
					gl.glTexCoord2f(0, 1);
					gl.glVertex3f(0, 0, 0);
					gl.glTexCoord2f(0, 0);
					gl.glVertex3f(0, height, 0);
					gl.glTexCoord2f(1, 0);
					gl.glVertex3f(width, height, 0);
					gl.glTexCoord2f(1, 1);
					gl.glVertex3f(width, 0, 0);
					gl.glEnd();

					gl.glDisable(GL.GL_TEXTURE_2D);
					// gl.glDisable(GL.GL_BLEND);

					dofShader.end(gl);
				}
				if (Game.DEBUG) {
					drawTexture(0, 0, width, height / 4, fobs.get(3)[0], true);
				}
				gl.glEnable(GL2.GL_DEPTH_TEST);
				super.endOrthoRender();
			}
		}
	}

	private void renderSkyBox() {
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		super.setupLook(new float[] { 0, 0, 0 }, cam.rotation);
		gl.glScalef(SKYBOX_SCALE, SKYBOX_SCALE, SKYBOX_SCALE);
		super.renderExcludedObjects();
		gl.glPopMatrix();
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}

	private void drawTexture(int x, int y, int width, int height, int tex,
			boolean useBlending) {
		textureShader.execute(gl);
		ShaderScript.setUniformTexture("tex", 0, tex);
		drawQuad(x, y, width, height, useBlending);
		textureShader.end(gl);
	}

	private void drawQuad(int x, int y, int width, int height,
			boolean useBlending) {
		gl.glEnable(GL.GL_TEXTURE_2D);
		if (useBlending) {
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		}
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(x, y, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(x, y + height, 0);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(x + width, y + height, 0);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(x + width, y, 0);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);
		if (useBlending)
			gl.glDisable(GL.GL_BLEND);
	}

	private void drawQuad(float scale) {
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(0, height * scale, 0);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(height * scale, height * scale, 0);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(height * scale, 0, 0);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_BLEND);
	}

	private void setModelViewMatrix() {
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelViewMatrix);
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glActiveTexture(GL.GL_TEXTURE6);
		gl.glLoadIdentity();
		gl.glMultMatrixf(modelViewMatrix);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	private void setTextureMatrix() {
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, lightMatrix);
		gl.glGetFloatv(GLMatrixFunc.GL_PROJECTION_MATRIX, projectionMatrix);
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glActiveTexture(GL.GL_TEXTURE7);
		gl.glLoadIdentity();
		gl.glLoadMatrixd(bias, 0);
		gl.glMultMatrixf(projectionMatrix);
		gl.glMultMatrixf(lightMatrix);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	private void createShadowMap(GameObject go) {
		gl.glColorMask(false, false, false, false);
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fobs.get(1)[1]);
		{
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
			gl.glCullFace(GL.GL_FRONT);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glViewport(0, 0, SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(FOV, 1, ZNear, ZFar);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			super.setupLook(go);
			setTextureMatrix();
			gl.glPolygonOffset(OFFSET_A, OFFSET_B);
			gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			super.renderObjects();
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glPopMatrix();
			gl.glCullFace(GL.GL_BACK);
		}
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		gl.glColorMask(true, true, true, true);

		super.setProjection(width, height);
	}

	private void drawTex(int tex, float scale) {
		textureShader.execute(gl);
		ShaderScript.setUniformTexture("tex", 0, tex);
		drawQuad(scale);
		textureShader.end(gl);
	}

	private void rttStart(int buf, boolean clearDepth) {
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, buf);
		int bits = GL.GL_COLOR_BUFFER_BIT;
		if (clearDepth)
			bits |= GL.GL_DEPTH_BUFFER_BIT;
		gl.glClear(bits);
	}

	private void rttEnd() {
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
	}

	protected void createTex(int fobNum, int width, int height) {
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

	private void createTex(int fobNum) {
		createTex(fobNum, width, height);
	}

	private void createShadowFob(int fobNum, int width, int height) {
		int[] fboId = new int[1];
		int[] texId = new int[1];
		gl.glGenFramebuffers(1, fboId, 0);
		gl.glGenTextures(1, texId, 0);

		gl.glBindTexture(GL.GL_TEXTURE_2D, texId[0]);
		// new line
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_DEPTH_TEXTURE_MODE,
				GL2.GL_LUMINANCE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);// before:GL_NEAREST
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);

		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT32F, width,
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

	public void dispose() {
		Log.log(this, "Deleting shaders");
		/*
		 * renderDepth.deleteShader(gl); shadowMapScript.deleteShader(gl);
		 * Log.log(this, "Deleting textures"); gl.glDeleteTextures(2, new int[]
		 * { fobs.get(0)[0], fobs.get(1)[0] }, 0); Log.log(this,
		 * "Deleting render buffers"); gl.glDeleteRenderbuffers(1, new int[] {
		 * fobs.get(0)[2] }, 0); Log.log(this, "Deleting frame buffers");
		 * gl.glDeleteFramebuffers(2, new int[] { fobs.get(0)[1], fobs.get(1)[1]
		 * }, 0);
		 */
		super.dispose();
	}

}
