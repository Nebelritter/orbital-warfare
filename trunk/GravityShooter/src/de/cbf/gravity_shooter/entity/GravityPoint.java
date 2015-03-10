/**
 * 
 */
package de.cbf.gravity_shooter.entity;

import com.jme3.math.Vector3f;

/**
 * @author Alti
 *
 */
public class GravityPoint {

	private Vector3f position;
	private float force;
	/**
	 * 
	 */
	public GravityPoint() {
		
	}
	
	public GravityPoint(Vector3f position, float force) {
		super();
		this.position = position;
		this.force = force;
	}

	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	/**
	 * @return the force
	 */
	public float getForce() {
		return force;
	}
	/**
	 * @param force the force to set
	 */
	public void setForce(float force) {
		this.force = force;
	}

	
}
