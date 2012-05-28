package world;

import java.util.Arrays;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import physics.PhysicsTest;
import util.Log;

import com.bulletphysics.dynamics.RigidBody;

public class GameObject extends VariableHolder {
	public float[] bbox = new float[6];
	public float[] pos = new float[3];
	public float[] oldPos = new float[3];
	public float[] size = new float[] { 10, 10, 10 };
	public float[] rotation = new float[3];
	public float[] color = new float[] { (float) Math.random(),
			(float) Math.random(), (float) Math.random() };
	public float friction = 0;
	private String type;
	public float angle;
	public float[] force = new float[3];
	public boolean fixed = false;
	public float[] rotationMatrixArray = new float[9];
	private Matrix3f rotationMatrix = new Matrix3f();
	public float alpha = 1;
	private static Vector3f tmpVector = new Vector3f();
	private static Matrix3f tmpMatrix = new Matrix3f();

	public GameObject(String name) {
		setType(name);
	}

	/**
	 * @return
	 * @uml.property name="type"
	 */
	public String getType() {
		return type;
	}

	public void beforeUpdate() {
		if (GameObjectType.getType(type).shape == null) {
			rotationMatrix.setIdentity();
			rotationMatrix.rotX(rotation[0]);
			rotationMatrix.rotY(rotation[1]);
			rotationMatrix.rotZ(rotation[2]);
		}
		updateRotationMatrixArray();
		setTo(oldPos, pos);
	}

	private void updateRotationMatrixArray() {
		rotationMatrixArray[0] = rotationMatrix.m00;
		rotationMatrixArray[1] = rotationMatrix.m01;
		rotationMatrixArray[2] = rotationMatrix.m02;
		rotationMatrixArray[3] = rotationMatrix.m10;
		rotationMatrixArray[4] = rotationMatrix.m11;
		rotationMatrixArray[5] = rotationMatrix.m12;
		rotationMatrixArray[6] = rotationMatrix.m20;
		rotationMatrixArray[7] = rotationMatrix.m21;
		rotationMatrixArray[8] = rotationMatrix.m22;
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
	}

	public String toString2() {
		return type + ", pos:[" + pos[0] + "," + pos[1] + "]";
	}

	@Override
	public String toString() {
		return "GameObject [bbox=" + Arrays.toString(bbox) + ", pos="
				+ Arrays.toString(pos) + ", oldPos=" + Arrays.toString(oldPos)
				+ ", size=" + Arrays.toString(size) + ", rotation="
				+ Arrays.toString(rotation) + ", color="
				+ Arrays.toString(color) + ", friction=" + friction + ", type="
				+ type + ", angle=" + angle + ", force="
				+ Arrays.toString(force) + ", fixed=" + fixed
				+ ", rotationMatrixArray="
				+ Arrays.toString(rotationMatrixArray) + ", rotationMatrix="
				+ rotationMatrix + ", alpha=" + alpha + "]";
	}

	/**
	 * @param name
	 * @uml.property name="type"
	 */
	public void setType(String name) {
		GameObjectType goType = GameObjectType.getType(name);
		if (goType == null) {
			Log.err(this, "GameObjectType " + name + " doesn't exist!");
			return;
		}
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

	public void setFixed(boolean b) {
		fixed = b;
	}

	public void setLinearVelocity(float x, float y) {
		RigidBody rigidBody = getRigidBody();
		Vector3f vel = rigidBody.getLinearVelocity(new Vector3f());
		rigidBody.setLinearVelocity(new Vector3f(x, vel.y, y));
	}

	public void setLinearVelocity(float x, float y, float z) {
		RigidBody rigidBody = getRigidBody();
		Vector3f vel = rigidBody.getLinearVelocity(new Vector3f());
		rigidBody.setLinearVelocity(new Vector3f((x == 0) ? vel.x : x,
				(y == 0) ? vel.y : y, (z == 0) ? vel.z : z));
	}

	public RigidBody getRigidBody() {
		return PhysicsTest.ids.get(this);
	}

	public void setRotation(Quat4f o) {
		rotationMatrix.set(o);
		updateRotationMatrixArray();
	}

	public Vector3f getEyeVector() {
		tmpMatrix.setIdentity();
		tmpMatrix.rotZ(rotation[2]);
		tmpMatrix.rotX(rotation[0]);
		// tmpMatrix.rotZ(rotation[2]);
		tmpVector.set(0, 0, 1);
		tmpMatrix.transform(tmpVector);
		return tmpVector;
	}

}
