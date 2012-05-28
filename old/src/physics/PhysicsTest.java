package physics;

import game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import util.Log;
import util.Util;
import world.GameObject;
import world.GameObjectType;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

public class PhysicsTest {
	Map<GameObject, RigidBody> ids = new HashMap<GameObject, RigidBody>();
	public static final float PHYSICS_SCALE = 1f / 100f;
	private DiscreteDynamicsWorld dynamicsWorld;
	private long last;

	public PhysicsTest() {
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		int maxProxies = 1024;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin,
				worldAabbMax, maxProxies);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,
				overlappingPairCache, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -5, 0));
		addFloor();
	}

	private void addFloor() {// create a few basic rigid bodies
		CollisionShape groundShape = new BoxShape(new Vector3f(500.f, 10.f,
				500.f));
		// keep track of the shapes, we release memory at exit.
		// make sure to re-use collision shapes among rigid bodies whenever
		// possible!
		ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();

		collisionShapes.add(groundShape);

		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(0.f, -10.f, 0.f));

		{
			float mass = 0f;

			// rigidbody is dynamic if and only if mass is non zero,
			// otherwise static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic) {
				groundShape.calculateLocalInertia(mass, localInertia);
			}

			// using motionstate is recommended, it provides interpolation
			// capabilities, and only synchronizes 'active' objects
			DefaultMotionState myMotionState = new DefaultMotionState(
					groundTransform);
			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
					mass, myMotionState, groundShape, localInertia);
			RigidBody body = new RigidBody(rbInfo);

			// add the body to the dynamics world
			dynamicsWorld.addRigidBody(body);
		}
	}

	public void update(Map<String, List<GameObject>> objs) {
		for (String s : objs.keySet()) {
			CollisionShape cs = GameObjectType.getType(s).shape;
			if (cs != null)
				for (GameObject go : objs.get(s)) {
					if (!ids.containsKey(go)) {
						ids.put(go, createRigid(cs, go));
					}
				}
		}

		// for (int i = 0; i < 3; i++)
		float delta = 0.03f;// ((float) (System.currentTimeMillis() - last) /
							// 1000f);
		dynamicsWorld.stepSimulation(delta, 5);
		last = System.currentTimeMillis();
		// Log.log( );

		for (List<GameObject> list : objs.values())
			for (GameObject go : list) {
				updateTransformation(go, ids.get(go));
			}
	}

	private void updateTransformation(GameObject go, RigidBody body) {
		if (body != null) {
			Transform trans = new Transform();
			body.getWorldTransform(trans);
			Quat4f o = new Quat4f();
			trans.getRotation(o);
			AxisAngle4f aa = new AxisAngle4f();
			aa.set(o);
			go.angle = aa.angle;
			go.rotation[0] = aa.x;
			go.rotation[1] = aa.y;
			go.rotation[2] = aa.z;
			// Util.toEuler(aa.x, aa.y, aa.z, aa.angle, go.rotation);
			// go.beforeUpdate();
			// Util.toEuler(trans.basis, go.rotation);

			go.pos[0] = trans.origin.x / PHYSICS_SCALE;
			go.pos[1] = trans.origin.y / PHYSICS_SCALE;
			go.pos[2] = trans.origin.z / PHYSICS_SCALE;
			// System.out.printf("world pos = %f,%f,%f\n", trans.origin.x,
			// trans.origin.y, trans.origin.z);
		}

	}

	private RigidBody createRigid(CollisionShape cs, GameObject go) {
		Transform startTransform = new Transform();
		startTransform.setIdentity();

		float mass = 1f;

		// rigidbody is dynamic if and only if mass is non zero,
		// otherwise static
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic) {
			cs.calculateLocalInertia(mass, localInertia);
		}

		startTransform.origin.set(new Vector3f(go.pos[0] * PHYSICS_SCALE,
				go.pos[1] * PHYSICS_SCALE, go.pos[2] * PHYSICS_SCALE));

		// using motionstate is recommended, it provides
		// interpolation capabilities, and only synchronizes
		// 'active' objects
		MotionState myMotionState = new DefaultMotionState(startTransform);

		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass,
				myMotionState, cs, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.applyCentralForce(new Vector3f(go.force));
		go.resetForce();

		dynamicsWorld.addRigidBody(body);
		return body;
	}

}
