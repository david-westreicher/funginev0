package rendering;

import game.Game;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL3;


import com.jogamp.common.nio.Buffers;

import settings.Settings;
import shader.ShaderScript;
import util.Log;
import world.GameObject;
import world.GameObjectType;
import world.Light;

public class TestUpdater extends RenderUpdater {

	/**
	 * @uml.property  name="fobs"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.Integer"
	 */
	private List<Integer[]> fobs = new ArrayList<Integer[]>();
	/**
	 * @uml.property  name="sscript"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ShaderScript sscript;

	public TestUpdater() {
		sscript = new ShaderScript("shader\\shadow.glsl");
		super.executeInOpenGLContext(new Runnable() {

			@Override
			public void run() {
				createTex(0);
				String extensions = gl.glGetString(GL.GL_EXTENSIONS);
				System.out.println(extensions);
			}
		});
	}

	protected void renderObjects() {
		//gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fobs.get(0)[1]);
		gl.glEnable(GL2.GL_BLEND);

		//gl.glClear(GL.GL_COLOR_BUFFER_BIT );
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_DST_ALPHA);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		draw2DShadows();
		gl.glEnable(GL2.GL_DEPTH_TEST);

		//gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		//gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);

		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		super.renderObjects();

		gl.glDisable(GL2.GL_BLEND);
	}

	private void draw2DShadows() {
		List<GameObject> lights = renderObjs.get("Light");
		if (lights != null) {
			gl.glEnable(GL2.GL_STENCIL_TEST);
			for (GameObject lightGo : lights) {
				gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
				gl.glColorMask(false, false, false, false);
				gl.glStencilFunc(GL2.GL_ALWAYS, 1, 1);
				gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_REPLACE);
				sscript.execute(gl);
				Light light = (Light) lightGo;
				float[] pos = light.pos;
				float[] camPos = Game.INSTANCE.cam.pos;
				sscript.setUniform("lightPos", new float[] {
						pos[0] - camPos[0], pos[1] - camPos[1],
						pos[2] - camPos[2] });
				for (GameObject go : renderObjs.get("Box")) {
					cgo = go;
					gl.glPushMatrix();
					super.setUpTransform(go, 0);
					BoxRenderer.drawDegeneratedBox(gl);
					gl.glPopMatrix();
				}
				sscript.end(gl);
				gl.glColorMask(true, true, true, true);
				gl.glStencilFunc(GL2.GL_NOTEQUAL, 1, 1);
				gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_KEEP);
				gl.glColor4fv(light.color, 0);

				gl.glPushMatrix();
				super.setUpTranslate(light, 0);
				if (Game.DEBUG)
					RenderUtil.drawSphere(0, 0, light.radius, null, gl);
				else
					RenderUtil.drawSphere(0, 0, light.radius, light.color, gl);
				gl.glPopMatrix();
			}
			gl.glDisable(GL2.GL_STENCIL_TEST);
		}
	}

	private void drawShadows() {
		List<GameObject> lights = renderObjs.get("Light");
		if (lights != null) {
			// gl.glEnable(GL2.GL_STENCIL_TEST);
			for (GameObject lightGo : lights) {
				/*
				 * gl.glClear(GL.GL_STENCIL_BUFFER_BIT); gl.glColorMask(false,
				 * false, false, false); gl.glStencilFunc(GL2.GL_ALWAYS, 1, 1);
				 * gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_REPLACE);
				 */
				sscript.execute(gl);
				Light light = (Light) lightGo;
				float[] pos = light.pos;
				float[] camPos = Game.INSTANCE.cam.pos;
				sscript.setUniform("lightPos", new float[] {
						pos[0] - camPos[0], pos[1] - camPos[1],
						pos[2] - camPos[2] });
				for (GameObject go : renderObjs.get("Box")) {
					cgo = go;
					gl.glPushMatrix();
					super.setUpTransform(go, 0);
					BoxRenderer.degeneratedCube(gl, 0.5f);
					gl.glPopMatrix();
				}
				sscript.end(gl);
				/*
				 * gl.glColorMask(true, true, true, true);
				 * gl.glStencilFunc(GL2.GL_NOTEQUAL, 1, 1);
				 * gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_KEEP);
				 * gl.glColor4fv(light.color, 0);
				 * 
				 * gl.glPushMatrix(); super.setUpTranslate(light, 0); if
				 * (Game.DEBUG) RenderUtil.drawSphere(0, 0, light.radius, null,
				 * gl); else RenderUtil.drawSphere(0, 0, light.radius,
				 * light.color, gl); gl.glPopMatrix();
				 */
			}
			// gl.glDisable(GL2.GL_STENCIL_TEST);
		}
	}

	private void createTex(int fobNum) {
		int[] fboId = new int[1];
		int[] texId = new int[1];
		gl.glGenFramebuffers(1, fboId, 0);
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fboId[0]);
		gl.glGenTextures(1, texId, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId[0]);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0,
				GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, Buffers
						.newDirectByteBuffer(width * height * 4));
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
				GL.GL_TEXTURE_2D, texId[0], 0);
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
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

	/*protected void renderOrtho() {
		gl.glPushMatrix();
		gl.glColor4f(1, 1, 1, 1);
		gl.glBindTexture(GL.GL_TEXTURE_2D, fobs.get(0)[0]);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_REPEAT);
		gl.glTranslatef(400,300,0);
		gl.glScalef(400,300,1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(-0.5f, -0.5f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(0.5f, -0.5f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(0.5f, 0.5f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(-0.5f, 0.5f);
		gl.glEnd();
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl.glPopMatrix();
	}*/
}
