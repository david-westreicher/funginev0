package world;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import util.Log;

public class GameObject extends VariableHolder {
	public float[] bbox = new float[6];
	public float[] pos = new float[3];
	public float[] oldPos = new float[3];
	public float[] size = new float[] { 10, 10, 10 };
	public float[] rotation = new float[3];
	public float[] oldRotation = new float[3];
	public float[] color = new float[] { (float) Math.random(),
			(float) Math.random(), (float) Math.random() };
	public float zrotation = 0;
	private String type;
	public float alpha = 1;
	public float animSprite = 0;
	public float angle;
	public float[] force = new float[3];
	public Matrix4f transform = new Matrix4f();

	public GameObject(String name) {
		setType(name);
	}

	public String getType() {
		return type;
	}

	public void beforeUpdate() {
		setTo(oldPos, pos);
		setTo(oldRotation, rotation);
		animSprite += 0.5;
	}

	private void setTo(float[] oldPos2, float[] pos2) {
		for (int i = 0; i < oldPos2.length; i++)
			oldPos2[i] = pos2[i];
	}

	public void updateBbox() {
		float radius = Math.max(Math.max(size[0], size[1]), size[2]);
		bbox[0] = pos[0] - radius;
		bbox[1] = pos[0] + radius;
		bbox[2] = pos[1] - radius;
		bbox[3] = pos[1] + radius;
		bbox[4] = pos[2] - radius;
		bbox[5] = pos[2] + radius;
	}

	public void setPos(float x, float y) {
		pos[0] = x;
		pos[1] = y;
		oldPos[0] = pos[0];
		oldPos[1] = pos[1];
	}

	public void setPos(float x, float y, float z) {
		setPos(x, y);
		pos[2] = z;
		oldPos[2] = pos[2];
	}

	public void setPos(float[] otherPos) {
		setTo(pos, otherPos);
		setTo(oldPos, pos);
	}

	public void setRotation(float x, float y, float z) {
		rotation[0] = x;
		rotation[1] = y;
		rotation[2] = z;
		setTo(oldRotation, rotation);
	}

	public String toString() {
		return type + ", pos:[" + pos[0] + "," + pos[1] + "]";
	}

	public void setType(String name) {
		GameObjectType goType = GameObjectType.getType(name);
		vals.putAll(goType.getVars());
		this.type = name;
	}

	public void resetForce() {
		for (int i = 0; i < 3; i++)
			force[i] = 0;
	}

	public void setForce(float x, float y, float z) {
		force[0] = x;
		force[1] = y;
		force[2] = z;
	}

	public void computeTransform() {
		Matrix4f r = new Matrix4f();
		r.setRotation(new AxisAngle4f(new Vector3f(rotation), angle));
		Matrix4f s = new Matrix4f();
		s.m00 = 1;
		s.m11 = 1;
		s.m22 = 1;
		s.m30 = size[0];
		s.m31 = size[1];
		s.m32 = size[2];
		s.m33 = 1;
		transform.setZero();
		transform.m00 = 1;
		transform.m11 = 1;
		transform.m22 = 1;
		transform.m33 = 1;
		transform.setTranslation(new Vector3f(pos));
		transform.mul(r);
		transform.mul(s);
	}

}
