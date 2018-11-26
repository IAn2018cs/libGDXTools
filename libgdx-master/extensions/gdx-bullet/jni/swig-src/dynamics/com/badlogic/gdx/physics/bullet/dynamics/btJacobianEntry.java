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

public class btJacobianEntry extends BulletBase {
	private long swigCPtr;
	
	protected btJacobianEntry(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, cPtr, cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btJacobianEntry, normally you should not need this constructor it's intended for low-level usage. */ 
	public btJacobianEntry(long cPtr, boolean cMemoryOwn) {
		this("btJacobianEntry", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(swigCPtr = cPtr, cMemoryOwn);
	}
	
	public static long getCPtr(btJacobianEntry obj) {
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
				DynamicsJNI.delete_btJacobianEntry(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public btJacobianEntry() {
    this(DynamicsJNI.new_btJacobianEntry__SWIG_0(), true);
  }

  public btJacobianEntry(Matrix3 world2A, Matrix3 world2B, Vector3 rel_pos1, Vector3 rel_pos2, Vector3 jointAxis, Vector3 inertiaInvA, float massInvA, Vector3 inertiaInvB, float massInvB) {
    this(DynamicsJNI.new_btJacobianEntry__SWIG_1(world2A, world2B, rel_pos1, rel_pos2, jointAxis, inertiaInvA, massInvA, inertiaInvB, massInvB), true);
  }

  public btJacobianEntry(Vector3 jointAxis, Matrix3 world2A, Matrix3 world2B, Vector3 inertiaInvA, Vector3 inertiaInvB) {
    this(DynamicsJNI.new_btJacobianEntry__SWIG_2(jointAxis, world2A, world2B, inertiaInvA, inertiaInvB), true);
  }

  public btJacobianEntry(Vector3 axisInA, Vector3 axisInB, Vector3 inertiaInvA, Vector3 inertiaInvB) {
    this(DynamicsJNI.new_btJacobianEntry__SWIG_3(axisInA, axisInB, inertiaInvA, inertiaInvB), true);
  }

  public btJacobianEntry(Matrix3 world2A, Vector3 rel_pos1, Vector3 rel_pos2, Vector3 jointAxis, Vector3 inertiaInvA, float massInvA) {
    this(DynamicsJNI.new_btJacobianEntry__SWIG_4(world2A, rel_pos1, rel_pos2, jointAxis, inertiaInvA, massInvA), true);
  }

  public float getDiagonal() {
    return DynamicsJNI.btJacobianEntry_getDiagonal(swigCPtr, this);
  }

  public float getNonDiagonal(btJacobianEntry jacB, float massInvA) {
    return DynamicsJNI.btJacobianEntry_getNonDiagonal__SWIG_0(swigCPtr, this, btJacobianEntry.getCPtr(jacB), jacB, massInvA);
  }

  public float getNonDiagonal(btJacobianEntry jacB, float massInvA, float massInvB) {
    return DynamicsJNI.btJacobianEntry_getNonDiagonal__SWIG_1(swigCPtr, this, btJacobianEntry.getCPtr(jacB), jacB, massInvA, massInvB);
  }

  public float getRelativeVelocity(Vector3 linvelA, Vector3 angvelA, Vector3 linvelB, Vector3 angvelB) {
    return DynamicsJNI.btJacobianEntry_getRelativeVelocity(swigCPtr, this, linvelA, angvelA, linvelB, angvelB);
  }

  public void setLinearJointAxis(btVector3 value) {
    DynamicsJNI.btJacobianEntry_linearJointAxis_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 getLinearJointAxis() {
    long cPtr = DynamicsJNI.btJacobianEntry_linearJointAxis_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void setAJ(btVector3 value) {
    DynamicsJNI.btJacobianEntry_aJ_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 getAJ() {
    long cPtr = DynamicsJNI.btJacobianEntry_aJ_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void setBJ(btVector3 value) {
    DynamicsJNI.btJacobianEntry_bJ_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 getBJ() {
    long cPtr = DynamicsJNI.btJacobianEntry_bJ_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void set0MinvJt(btVector3 value) {
    DynamicsJNI.btJacobianEntry_0MinvJt_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 get0MinvJt() {
    long cPtr = DynamicsJNI.btJacobianEntry_0MinvJt_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void set1MinvJt(btVector3 value) {
    DynamicsJNI.btJacobianEntry_1MinvJt_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 get1MinvJt() {
    long cPtr = DynamicsJNI.btJacobianEntry_1MinvJt_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void setAdiag(float value) {
    DynamicsJNI.btJacobianEntry_Adiag_set(swigCPtr, this, value);
  }

  public float getAdiag() {
    return DynamicsJNI.btJacobianEntry_Adiag_get(swigCPtr, this);
  }

}
