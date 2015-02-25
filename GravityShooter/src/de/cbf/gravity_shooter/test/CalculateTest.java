/**
 * 
 */
package de.cbf.gravity_shooter.test;

import com.jme3.math.Vector3f;

/**
 * @author Alti
 *
 */
public class CalculateTest {
	
	private Vector3f desiredMovementVector = new Vector3f(80, 80, 0);
	private float maxVelocity = 6f;
	/**
	 * 
	 */
	public CalculateTest() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CalculateTest().startCalculation();
	}
	
	protected void startCalculation(){
		float speed = desiredMovementVector.length();
		//calc the speed that is build up too much
		float surmountSpeed =  speed - maxVelocity;
		if(surmountSpeed > 0 ){
			// we are going to fast
			//calculate amount in percent			
			float surmountfactor =  maxVelocity / speed;
			//multiply with axes to shorten vector by the given amount
			desiredMovementVector = desiredMovementVector.mult(surmountfactor);
			float newLength = desiredMovementVector.length();
			if(newLength != maxVelocity){
				System.err.println("calculation missed target by :"+(maxVelocity-newLength));
			}
		}else{
			//speed is okay
		}
	}

}
