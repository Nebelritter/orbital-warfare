/**
 * 
 */
package de.cbf.gravity_shooter.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import de.cbf.gravity_shooter.entity.GravityPoint;

/**
 * @author Alti
 * control to calculate gravity vector
 * , add this first
 */
public class GravityControl extends AbstractControl {
	private static final Logger LOGGER = Logger.getLogger(GravityControl.class.getName());

	private List<GravityPoint> gravityPoints = new ArrayList<GravityPoint>();
	private Vector3f gravityVector;
	
	/**
	 * amount of force where no impact should be felt
	 */
	private float epsilon;
	
	private Float maxForce;
	
	public GravityControl(List<GravityPoint> gravityPoints) {
		super();
		this.gravityPoints = gravityPoints;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if(isEnabled()){
			gravityVector = new Vector3f();
			// calcuate gravity vector
			for (GravityPoint gravityPoint : gravityPoints) {
				Vector3f forceVectorForPoint = calculateOneVector(gravityPoint);
				LOGGER.finest("ForceVector:"+forceVectorForPoint);
				if(forceVectorForPoint != null){
					gravityVector.add(forceVectorForPoint, gravityVector);
				}
			}
			//tpf the vector
			gravityVector.mult(tpf, gravityVector);
			if(maxForce != null){
				float maxForceTpF = maxForce * tpf;
				if(gravityVector.length() > maxForceTpF){
					float correctionFactor = maxForceTpF / gravityVector.length();
					gravityVector.mult(correctionFactor , gravityVector);
					LOGGER.info("Force corrected, factor:"+correctionFactor);
				}
				LOGGER.fine("EndForceVector:"+gravityVector);
			}			
		}		
	}

	private Vector3f calculateOneVector(GravityPoint gravityPoint) {
		//calculate vector to gravity point
		Vector3f ownPosition = getSpatial().getWorldTranslation();
		Vector3f gravityPosition = gravityPoint.getPosition();
		Vector3f directionVector = gravityPosition.subtract(ownPosition);
		float distance = directionVector.length();
		//normalize vector
		directionVector = directionVector.normalize();
		//multiply by force
		float force = calculateForceByDistance(distance,gravityPoint.getForce());
		if(force == 0){
			//no forces are felt
			return null;
		}
		directionVector = directionVector.mult(force);
		return directionVector;
	}

	private float calculateForceByDistance(float distance,float force) {
		float result = force / (distance * distance);
		//if impact is too small set it to zero to save minimal calculations
		if(result < epsilon){
			LOGGER.info("result below epsilon("+ epsilon+"):"+result);
			result = 0;
		}
		return result;
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		
	}
	
	/**
	 * @return the gravityPoints
	 */
	public List<GravityPoint> getGravityPoints() {
		return gravityPoints;
	}

	/**
	 * @param gravityPoints the gravityPoints to set
	 */
	public void setGravityPoints(List<GravityPoint> gravityPoints) {
		this.gravityPoints = gravityPoints;
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

	/**
	 * @return the epsilon
	 */
	public float getEpsilon() {
		return epsilon;
	}

	/**
	 * @param epsilon the epsilon to set
	 */
	public void setEpsilon(float epsilon) {
		this.epsilon = epsilon;
	}

	/**
	 * @return the maxForce
	 */
	public float getMaxForce() {
		return maxForce;
	}

	/**
	 * @param maxForce the maxForce to set
	 */
	public void setMaxForce(Float maxForce) {
		this.maxForce = maxForce;
	}

}
