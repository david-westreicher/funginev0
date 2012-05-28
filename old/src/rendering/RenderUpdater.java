package rendering;

import game.Game;
import game.GameLoop;
import game.Updatable;

import java.awt.Component;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import settings.Settings;
import util.Log;
import util.Matrix;
import util.Util;
import util.Vector;
import world.Camera;
import world.GameObject;
import world.GameObjectType;

import com.jogamp.opengl.util.gl2.GLUT;

public class RenderUpdater implements Updatable, GLEventListener {
	private static final boolean USE_OBJECT_INTERP = false;
	private static final boolean SMOOTHSTEP_INTERP = false;
	public static double FOV_Y = 60;
	private GLCanvas canvas;
	private OpenGLRendering renderer;
	public static GL2 gl;
	public static GLUT glut = new GLUT();
	public static GLU glu = new GLU();
	protected float interp;
	private List<String> renderStrings = new ArrayList<String>();
	private Cursor hiddenCursor;
	public static GameObject cgo = null;
	// public float z = 499;
	protected int width;
	protected int height;
	protected Map<String, List<GameObject>> renderObjs;
	private Camera cam = Game.INSTANCE.cam;
	public static float ZFar;
	public static float ZNear;
	private static List<Runnable> runnables = new ArrayList<Runnable>();

	public RenderUpdater() {
		renderer = new OpenGLRendering(this);
		canvas = renderer.getCanvas();
		hiddenCursor = Util.getHiddenCursor();
	}

	@Override
	public void update(float interp) {
		this.interp = interp;
		canvas.display();
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		gl = arg0.getGL().getGL2();

		if (runnables.size() > 0) {
			for (Runnable r : runnables)
				r.run();
			runnables.clear();
		}
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		// renderOBJECTS
		if (!Game.INSTANCE.loop.isPausing()) {
			renderObjs = Game.INSTANCE.world.getVisibleObjects();
			gl.glEnable(GL2.GL_DEPTH_TEST);

			// CAMERA
			setupLook(cam);

			// popMatrix
			renderObjects();

			gl.glDisable(GL2.GL_DEPTH_TEST);
			if (Game.DEBUG) {
				renderDebug();
			}

			startOrthoRender();

			renderText();
			endOrthoRender();
		}
		gl.glFlush();
		arg0.swapBuffers();
	}

	protected void setupLook(GameObject go) {
		float pos[] = interp(go.pos, go.oldPos, interp);
		float rot[] = interp(go.rotation, go.oldRotation, interp);
		Vector m = new Vector(new float[] { 0, 0, 1 }).rotate(rot);
		glu.gluLookAt(pos[0], pos[1], pos[2], pos[0] + m.v[0], pos[1] + m.v[1],
				pos[2] + m.v[2], 0, 1, 0);
	}

	private float[] interp(float[] pos, float[] oldPos, float interp) {
		float res[] = new float[3];
		if (SMOOTHSTEP_INTERP)
			interp = ((interp) * (interp) * (3 - 2 * (interp)));
		for (int i = 0; i < 3; i++)
			res[i] = pos[i] * interp + (oldPos[i] * (1 - interp));
		return res;
	}

	protected void endOrthoRender() {
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	protected void startOrthoRender() {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, width, height, 0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}

	private void renderText() {

		gl.glColor3f(1, 0, 0);
		GameLoop loop = Game.INSTANCE.loop;
		// text
		renderStrings.add("Render-FPS: "
				+ Util.roundDigits(loop.currentFPS.fps, 1));
		renderStrings.add("Tick-FPS  : "
				+ Util.roundDigits(loop.currentTick.fps, 1));
		renderStrings.add("TpT       : " + loop.timePerTick + "ms");
		renderStrings.add("#Objects  : " + Game.INSTANCE.world.getObjectNum());
		renderStrings.add("Zoom      : " + Util.roundDigits(cam.zoom, 2));
		int i = 1;
		for (String s : renderStrings) {
			gl.glRasterPos2f(0, 15 * i++);
			glut.glutBitmapString(GLUT.BITMAP_8_BY_13, s);
		}
		renderStrings.clear();
	}

	private void renderDebug() {
		// bboxes
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glBegin(GL2.GL_LINES);
		for (List<GameObject> list : renderObjs.values()) {
			for (GameObject go : list) {
				RenderUtil.drawLinedBox(go.bbox, gl);
			}
		}
		gl.glEnd();
		/*
		 * int num = 50; int size = 100; gl.glPushMatrix(); {
		 * gl.glTranslatef(-num * size / 2, 0, -num * size / 2);
		 * gl.glBegin(GL2.GL_LINES); for (int i = 0; i < num; i++) {
		 * gl.glVertex3f(i * size, 0, 0); gl.glVertex3f(i * size, 0, size *
		 * size); gl.glVertex3f(0, 0, i * size); gl.glVertex3f(size * size, 0, i
		 * * size); } gl.glEnd(); } gl.glPopMatrix();
		 */
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glColor4f(1, 0, 0, 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(100, 0, 0);
			gl.glColor4f(0, 1, 0, 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 100, 0);
			gl.glColor4f(0, 0, 1, 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 0, 100);
		}
		gl.glEnd();
		// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

		/*
		 * gl.glPushMatrix(); gl.glTranslatef(cam.pos[0], cam.pos[1], 0);
		 * gl.glScalef(cam.zoom, cam.zoom, 1); gl.glTranslatef(-width / 2,
		 * -height / 2, 0); // visible cam gl.glBegin(GL.GL_LINE_LOOP);
		 * gl.glVertex3f(0, 0, 0); gl.glVertex3f(width, 0, 0);
		 * gl.glVertex3f(width, height, 0); gl.glVertex3f(0, height, 0);
		 * gl.glEnd(); gl.glPopMatrix();
		 */
	}

	protected void renderObjects(String type) {
		GameObjectType goType = GameObjectType.getType(type);
		GameObjectRenderer renderer = goType.renderer;
		if (renderer != null) {
			renderer.init(gl);
			List<GameObject> objs = renderObjs.get(type);
			if (objs != null)
				if (renderer.isSimple())
					renderer.draw(gl, objs, interp);
				else
					for (GameObject go : objs) {
						cgo = go;
						gl.glPushMatrix();
						/*if (USE_OBJECT_INTERP) {
							// interp
							float xSpeed = go.pos[0] - go.oldPos[0];
							float ySpeed = go.pos[1] - go.oldPos[1];
							float zSpeed = go.pos[2] - go.oldPos[2];
							// TODO new interp rotation float rotSpeed =
							// go.rotation - go.oldRotation;
							gl.glTranslatef(go.pos[0] + xSpeed * interp,
									go.pos[1] + ySpeed * interp, go.pos[2]
											+ zSpeed * interp);
						} else
							gl.glTranslatef(go.pos[0], go.pos[1], go.pos[2]);*/
						/*
						 * gl.glRotatef(toDegree(go.angle), go.rotation[0],
						 * go.rotation[1], go.rotation[2]);
						 */
						/*if (goType.shape == null) {
							gl.glRotatef(toDegree(go.rotation[0]), -1, 0, 0);
							gl.glRotatef(toDegree(go.rotation[1]), 0, -1, 0);
							gl.glRotatef(toDegree(go.rotation[2]), 0, 0, -1);
						} else
							gl.glRotatef(toDegree(go.angle), go.rotation[0],
									go.rotation[1], go.rotation[2]);
						
						gl.glScalef(go.size[0], go.size[1], go.size[2]);*/
						gl.glMultMatrixf(go.transform, 0);
						renderer.draw(gl);
						gl.glPopMatrix();
					}
			renderer.end(gl);
		}
	}

	private float toDegree(float f) {
		return (float) (f * 180 / Math.PI);
	}

	protected void renderObjects() {
		// render this shit
		for (String type : renderObjs.keySet()) {
			renderObjects(type);
		}

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		gl = arg0.getGL().getGL2();
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		gl = arg0.getGL().getGL2();
		gl.glClearColor(1, 1, 1, 1);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		// culling
		// gl.glDisable(GL2.GL_CULL_FACE);
		// gl.glFrontFace(GL2.GL_CW);
		// gl.glEnable(GL2.GL_CULL_FACE);
		// gl.glCullFace(GL2.GL_BACK);
		// lighting
		/*
		 * float mat_specular[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f }; float
		 * mat_shininess[] = new float[] { 50.0f }; float light_position[] = new
		 * float[] { 1.0f, 1.0f, 1.0f, 0.0f };
		 * 
		 * gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
		 * gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);
		 * gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
		 * 
		 * gl.glEnable(GL2.GL_LIGHTING); gl.glEnable(GL2.GL_LIGHT0);
		 */
		// stencil
		gl.glStencilMask(1);
		gl.glClearStencil(0);

		setProjection(Settings.WIDTH, Settings.HEIGHT);
	}

	private void setProjection(int width, int height) {
		this.width = width;
		this.height = height;
		float z = ((float) height / 2)
				/ (float) Math.tan(FOV_Y * Math.PI / 360);
		gl.glViewport(0, 0, width, height);
		float zoom = Game.INSTANCE.cam.zoom * z;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		ZNear = 20;// Math.max(zoom - 500, 0);
		ZFar = zoom + 5000;
		glu.gluPerspective(FOV_Y, (float) width / height, ZNear, ZFar);
		// gl.glScalef(1, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int width, int height) {
		gl = arg0.getGL().getGL2();
		Log.log(this, "reshape:[" + width + "," + height + "]");
		setProjection(width, height);
	}

	@Override
	public void dispose() {
		renderer.dispose();
	}

	public static void executeInOpenGLContext(Runnable runnable) {
		runnables.add(runnable);
	}

	public static void createCallList(final Runnable r,
			final CallBack<Integer> c) {
		executeInOpenGLContext(new Runnable() {

			@Override
			public void run() {
				int num = gl.glGenLists(1);
				gl.glNewList(num, GL2.GL_COMPILE);
				r.run();
				gl.glEndList();
				c.returnVar(num);
			}
		});

	}

	public void hideCursor(boolean hide) {
		if (hide)
			canvas.setCursor(hiddenCursor);
		else
			canvas.setCursor(Cursor.getDefaultCursor());
	}

	public void setUpTranslate(GameObject go, float zOrdering) {
		float xSpeed = go.pos[0] - go.oldPos[0];
		float ySpeed = go.pos[1] - go.oldPos[1];
		float zSpeed = go.pos[2] - go.oldPos[2];
		// TODO new rotation interp float rotSpeed = go.rotation -
		// go.oldRotation;
		// gl.glTranslatef(go.pos[0] + xSpeed * interp, go.pos[1] + ySpeed
		// * interp, zOrdering + go.pos[2] + zSpeed * interp);
		// //gl.glRotatef(go.rotation + rotSpeed * interp, 0, 0, 1);
		gl.glRotatef(go.zrotation, 1, 0, 0);
	}

	protected void setUpTransform(GameObject go, float zOrdering) {
		setUpTranslate(go, zOrdering);
		gl.glScalef(go.size[0], go.size[1], go.size[2]);
	}

}
