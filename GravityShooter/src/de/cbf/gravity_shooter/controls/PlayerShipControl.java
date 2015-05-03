/**
 * 
 */
package de.cbf.gravity_shooter.controls;

import java.util.logging.Logger;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import de.cbf.gravity_shooter.Main;

/**
 * @author Alti
 *
 */
public class PlayerShipControl extends AbstractControl {
	private static final Logger LOGGER = Logger.getLogger(PlayerShipControl.class.getName());
	
	/** already tpf'ed	 */
	private float desiredVelocity;
	private Float maxVelocity;
	/** already tpf'ed	 */
	private float desiredRotationRads;
	
	private Vector3f gravityVector;

	private boolean	stopMovement; 
		
	
	/**
	 * 
	 */
	public PlayerShipControl() {
	}
	
	/** 
	 * @see com.jme3.scene.control.AbstractControl#controlUpdate(float)
	 * 
	 * this is called by update loop in row with other controls
	 */
	@Override
	protected void controlUpdate(float tpf) {
		if(isEnabled()){
			handleMovement(tpf);
			handleRotation(tpf);
		}
	}


	private void handleMovement(float tpf) {
		RigidBodyControl physicsControl = spatial.getControl(RigidBodyControl.class);
		
		if(stopMovement){
			physicsControl.clearForces();
			return;
		}
		Vector3f movementVector = Vector3f.ZERO.clone();			
		//set gravity influence 			
		if(gravityVector != null){		
			//gravity has been dealt with, reset it
			movementVector.addLocal(gravityVector);
			gravityVector = null;
		}
		if(desiredVelocity != 0){				
			Vector3f direction = spatial.getLocalRotation().getRotationColumn(0);
			Vector3f realMovement = direction.mult( - desiredVelocity);
			movementVector.addLocal(realMovement);
			desiredVelocity = 0;
		}
		physicsControl.applyCentralForce(movementVector);
				
	}

	private void handleRotation(float tpf) {
		//physical way
		RigidBodyControl physicsControl = spatial.getControl(RigidBodyControl.class);
		if(stopMovement){
			physicsControl.clearForces();
			return;
		}
		if(desiredRotationRads != 0){				
			physicsControl.applyTorque(new Vector3f(0,0,desiredRotationRads));	
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.jme3.scene.control.AbstractControl#controlRender(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort)
	 */
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// not necessary at the moment

	}
		
	/**
	 * @return the desiredVelocity
	 */
	public float getDesiredVelocity() {
		return desiredVelocity;
	}

	/**
	 * @param desiredVelocity the desiredVelocity to set
	 */
	public void setDesiredVelocity(float desiredVelocity) {
		this.desiredVelocity = desiredVelocity;
	}

	/**
	 * @return the desiredRotationRads
	 */
	public float getDesiredRotationRads() {
		return desiredRotationRads;
	}

	/**
	 * @param desiredRotationRads the desiredRotationRads to set
	 */
	public void setDesiredRotationRads(float desiredRotationRads) {
		this.desiredRotationRads = desiredRotationRads;
	}

	/**
	 * @return the maxVelocity
	 */
	public float getMaxVelocity() {
		return maxVelocity;
	}

	/**
	 * @param maxVelocity the maxVelocity to set
	 */
	public void setMaxVelocity(Float maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	/**
	 * @return the gravityVector
	 */
	public Vector3f getGravityVector() {
		return gravityVector;
	}

	/**
	 * @param gravityVector the gravityVector to set
	 */
	public void setGravityVector(Vector3f gravityVector) {
		this.gravityVector = gravityVector;
	}

	public void setStopMovement(boolean stopMovement) {
		this.stopMovement = stopMovement;		
	}

}
