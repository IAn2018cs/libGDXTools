/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

public class btSimpleDynamicsWorld extends btDynamicsWorld {
	private long swigCPtr;
	
	protected btSimpleDynamicsWorld(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, DynamicsJNI.btSimpleDynamicsWorld_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btSimpleDynamicsWorld, normally you should not need this constructor it's intended for low-level usage. */
	public btSimpleDynamicsWorld(long cPtr, boolean cMemoryOwn) {
		this("btSimpleDynamicsWorld", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(DynamicsJNI.btSimpleDynamicsWorld_SWIGUpcast(swigCPtr = cPtr), cMemoryOwn);
	}
	
	public static long getCPtr(btSimpleDynamicsWorld obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!destroyed)
			destroy();
		super.finalize();
	}

  @Override protected synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				DynamicsJNI.delete_btSimpleDynamicsWorld(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public btSimpleDynamicsWorld(btDispatcher dispatcher, btBroadphaseInterface pairCache, btConstraintSolver constraintSolver, btCollisionConfiguration collisionConfiguration) {
    this(DynamicsJNI.new_btSimpleDynamicsWorld(btDispatcher.getCPtr(dispatcher), dispatcher, btBroadphaseInterface.getCPtr(pairCache), pairCache, btConstraintSolver.getCPtr(constraintSolver), constraintSolver, btCollisionConfiguration.getCPtr(collisionConfiguration), collisionConfiguration), true);
  }

  public int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
    return DynamicsJNI.btSimpleDynamicsWorld_stepSimulation__SWIG_0(swigCPtr, this, timeStep, maxSubSteps, fixedTimeStep);
  }

  public int stepSimulation(float timeStep, int maxSubSteps) {
    return DynamicsJNI.btSimpleDynamicsWorld_stepSimulation__SWIG_1(swigCPtr, this, timeStep, maxSubSteps);
  }

  public int stepSimulation(float timeStep) {
    return DynamicsJNI.btSimpleDynamicsWorld_stepSimulation__SWIG_2(swigCPtr, this, timeStep);
  }

  public void addRigidBody(btRigidBody body) {
    DynamicsJNI.btSimpleDynamicsWorld_addRigidBody__SWIG_0(swigCPtr, this, btRigidBody.getCPtr(body), body);
  }

  public void addRigidBody(btRigidBody body, int group, int mask) {
    DynamicsJNI.btSimpleDynamicsWorld_addRigidBody__SWIG_1(swigCPtr, this, btRigidBody.getCPtr(body), body, group, mask);
  }

}
