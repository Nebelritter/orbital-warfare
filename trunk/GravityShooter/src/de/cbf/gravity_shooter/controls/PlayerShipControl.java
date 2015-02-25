/**
 * 
 */
package de.cbf.gravity_shooter.controls;

import java.util.logging.Logger;

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
	private float maxVelocity;
	/** already tpf'ed	 */
	private float desiredRotationRads;
	
	
	private Vector3f movementVector = new Vector3f(0,0,0);
	
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
		Spatial spatial = getSpatial();
		//get direction from ship, not movement node
		if (spatial instanceof Node) {
			//get direction of spaceship facing
			if(desiredVelocity != 0){
				Node movementNode = (Node) spatial;
				Spatial spaceShip = movementNode.getChild(Main.SPACE_SHIP_NAME);
				Vector3f direction = spaceShip.getLocalRotation().getRotationColumn(0); //TODO is this correct?
										
				Vector3f realMovement = direction.mult( - desiredVelocity);
				
				movementVector.add(realMovement,movementVector);
				//calc if the speed that is build up too much
				float speed = movementVector.length();		
				float thisFrameMaxVelocity = maxVelocity * tpf;
				float surmountSpeed =  speed - thisFrameMaxVelocity;
				if(surmountSpeed > 0 ){
					// we are going to fast
					//calculate amount in percent			
					float surmountFactor =  thisFrameMaxVelocity / speed;
					//multiply with axes to shorten vector by the given amount
					movementVector = movementVector.mult(surmountFactor);
//					float newLength = movementVector.length();
//					if(newLength != thisFrameMaxVelocity){
//						System.out.println("calculation missed target by :"+(maxVelocity-newLength));
//					}
				}else{
					//speed is okay
				}
			}			
			//move in the direction of the vector			
			spatial.move(movementVector);
		}		
	}


	private void handleRotation(float tpf) {
		if (spatial instanceof Node) {
			//rotate child node for movement might be in other direction than facing
			Node movementNode = (Node) spatial;
			Spatial spaceShip = movementNode.getChild(Main.SPACE_SHIP_NAME);
			Quaternion rotationDelta = new Quaternion().fromAngles(0,desiredRotationRads,0);
			spaceShip.rotate(rotationDelta);
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
	public void setMaxVelocity(float maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

}
