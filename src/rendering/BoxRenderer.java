package rendering;

import game.Game;

import javax.media.opengl.GL2;

import settings.Settings;
import util.Log;

public class BoxRenderer extends GameObjectRenderer {

	private static final float size = 0.5f;
	private static final float vertices[][] = { { -size, -size, -size },
			{ size, -size, -size }, { size, size, -size },
			{ -size, size, -size }, { -size, -size, size },
			{ size, -size, size }, { size, size, size }, { -size, size, size } };
	private static final int faces[][] = { { 4, 0, 5, 1, 5, 1, 6, 2, 6, 2, 7,
			3, 7, 3, 4, 0, 4, 0 } };
	private static final int normalNum[][] = { { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2,
			2, 2, 3, 3, 3, 3, 0, 0 } };
	private static final float normals[][] = { { 0, -1, 0 }, { 1, 0, 0 },
			{ 0, 1, 0 }, { -1, 0, 0 } };

	@Override
	public void draw(GL2 gl) {
		if (Game.WIREFRAME) {
			gl.glColor4d(0, 1, 0, 1);
			// degeneratedCube(gl, 0.5f);
			RenderUpdater.glut.glutWireCube(1);
		} else {
			gl.glColor4d(0, 1, 0, 1);
			gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL); // Avoid Stitching!
			gl.glPolygonOffset(-4f, -4f);
			gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
			RenderUpdater.glut.glutSolidCube(1);
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glColor3d(0, 0, 0);
			gl.glLineWidth(5);
			gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
			RenderUpdater.glut.glutWireCube(1);
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			gl.glLineWidth(1);
		}
	}

	public static void degeneratedCube(GL2 gl, float size) {
		gl.glColor4f(1, 0, 0, 1);
		int currentNormal = -1;
		for (int i = 0; i < faces.length; i++) {
			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			for (int j = 0; j < faces[i].length; j++) {
				if (currentNormal != normalNum[i][j]) {
					currentNormal = normalNum[i][j];
					gl.glNormal3fv(normals[currentNormal], 0);
				}
				gl.glVertex3fv(vertices[faces[i][j]], 0);
			}
			gl.glEnd();
		}
	}

	public static void drawDegeneratedBox(GL2 gl) {
		float z = -0.5f;
		if (Game.DEBUG)
			gl.glBegin(GL2.GL_LINE_LOOP);
		else
			gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(-1, 0, 0);
		gl.glVertex3f(-0.5f, -0.5f, z);
		gl.glNormal3f(0, -1, 0);
		gl.glVertex3f(-0.5f, -0.5f, z);
		gl.glNormal3f(0, -1, 0);
		gl.glVertex3f(0.5f, -0.5f, z);
		gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(0.5f, -0.5f, z);
		gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(0.5f, 0.5f, z);
		gl.glNormal3f(0, 1, 0);
		gl.glVertex3f(0.5f, 0.5f, z);
		gl.glNormal3f(0, 1, 0);
		gl.glVertex3f(-0.5f, 0.5f, z);
		gl.glNormal3f(-1, 0, 0);
		gl.glVertex3f(-0.5f, 0.5f, z);
		gl.glEnd();
	}

	@Override
	public void init(GL2 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void end(GL2 gl) {
		// TODO Auto-generated method stub

	}

}
