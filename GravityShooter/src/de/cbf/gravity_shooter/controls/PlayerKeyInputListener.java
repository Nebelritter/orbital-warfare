/**
 * 
 */
package de.cbf.gravity_shooter.controls;

import java.util.logging.Logger;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import de.cbf.gravity_shooter.Main;

/**
 * @author Alti
 *
 */
public class PlayerKeyInputListener implements ActionListener {
	private static final Logger LOGGER = Logger.getLogger(PlayerKeyInputListener.class.getName());
	
	private static final String PLAYER_MOVEMENT_FORWARD = "player.move.forward";
	private static final String PLAYER_ROTATION_RIGHT = "player.rotation.right";
	private static final String PLAYER_ROTATION_LEFT = "player.rotation.left";
	
	private float rotationValue;
	private float accelerationValue;
		
	private float desiredVelocity;
	//split if both keys are pressed, then rotation should not occur
	private float desiredRotationRight;
	private float desiredRotationLeft;

	
	private static final String[] MAPPINGS = new String[]{
		PLAYER_MOVEMENT_FORWARD,
		PLAYER_ROTATION_RIGHT,
		PLAYER_ROTATION_LEFT
	};
	/**
	 * 
	 */
	public PlayerKeyInputListener() {

	}

	/* (non-Javadoc)
	 * @see com.jme3.input.controls.ActionListener#onAction(java.lang.String, boolean, float)
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
		case PLAYER_MOVEMENT_FORWARD:
			if(isPressed){
				desiredVelocity += accelerationValue * tpf;				
			}else{				
				desiredVelocity = 0;
			}
			break;
		case PLAYER_ROTATION_RIGHT:
			if(isPressed){
				desiredRotationRight = - rotationValue * tpf;
			}else{
				desiredRotationRight = 0;
			}
			break;
		case PLAYER_ROTATION_LEFT:
			if(isPressed){
				desiredRotationLeft = rotationValue * tpf;
			}else{
				desiredRotationLeft = 0;
			}
			break;
		default:
			break;
		}

	}

	public void registerWithInputManager(InputManager inputManager) {
		inputManager.addMapping(PLAYER_MOVEMENT_FORWARD,new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(PLAYER_ROTATION_RIGHT,new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(PLAYER_ROTATION_LEFT,new KeyTrigger(KeyInput.KEY_A));
		
		inputManager.addListener(this, MAPPINGS);
	}

	public float getDesiredVelocity() {			
		return desiredVelocity;
	}

	/**
	 *  <li>+ direction is right</li>
	 *  <li>- direction is left</li>
	 * @return
	 */
	public float getDesiredRotationRads() {
		float resultRotation = desiredRotationLeft+desiredRotationRight;
		return resultRotation;
	}

	/**
	 * @return the rotationValue
	 */
	public float getRotationValue() {
		return rotationValue;
	}

	/**
	 * @param rotationValue the rotationValue to set
	 */
	public void setRotationValue(float rotationValue) {
		this.rotationValue = rotationValue;
	}

	/**
	 * @return the accelerationValue
	 */
	public float getAccelerationValue() {
		return accelerationValue;
	}

	/**
	 * @param accelerationValue the accelerationValue to set
	 */
	public void setAccelerationValue(float accelerationValue) {
		this.accelerationValue = accelerationValue;
	}

}
