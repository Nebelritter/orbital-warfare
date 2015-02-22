/**
 * 
 */
package de.cbf.gravity_shooter.camera;

import com.jme3.collision.MotionAllowedListener;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * @author Alti
 *
 */
public class RTSCamera implements AnalogListener,ActionListener{
//	private static final Logger LOGGER = Logger.getLogger(RTSCamera.class.getName());
	
	public static final String CAMERA_ZOOM_IN = "CameraZoomIn";
    public static final String CAMERA_ZOOM_OUT = "CameraZoomOut";
    public static final String CAMERA_WEST = "CameraWest";
    public static final String CAMERA_EAST ="CameraEast";
    public static final String CAMERA_NORTH = "CameraNorth";
    public static final String CAMERA_SOUTH = "CameraSouth";
    public static final String CAMERA_RISE = "CameraRise";
    public static final String CAMERA_LOWER = "CameraLower";
    
    public static final String MIDDLE_MOUSE = "MiddleMouse";
	
    public static final String[] CAMERA_MAPPINGS = new String[]{
    	CAMERA_ZOOM_IN,CAMERA_ZOOM_OUT,
    	CAMERA_WEST,CAMERA_EAST,CAMERA_NORTH,CAMERA_SOUTH,
    	CAMERA_RISE,CAMERA_LOWER,
    	MIDDLE_MOUSE
    };
    
    
    protected Camera cam;
    protected Vector3f initialUpVec;
    protected float moveSpeed = 5f;    
    protected float zoomSpeed = 5f;
    protected boolean enabled = true;
    protected MotionAllowedListener motionAllowed = null;
    protected InputManager inputManager;
	private boolean middleMousePressed;
    
    public RTSCamera(Camera cam) {
		super();
		this.cam = cam;
        initialUpVec = cam.getUp().clone();
	}

	public void registerWithInput(InputManager inputManager) {
		this.inputManager = inputManager;
		
		inputManager.addMapping(MIDDLE_MOUSE, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
		
		inputManager.addMapping(CAMERA_ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(CAMERA_ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
	    
	    inputManager.addMapping(CAMERA_WEST, new MouseAxisTrigger(MouseInput.AXIS_X,false)); 
        inputManager.addMapping(CAMERA_EAST, new MouseAxisTrigger(MouseInput.AXIS_X,true));
        inputManager.addMapping(CAMERA_NORTH, new MouseAxisTrigger(MouseInput.AXIS_Y,true));
        inputManager.addMapping(CAMERA_SOUTH, new MouseAxisTrigger(MouseInput.AXIS_Y,false));
        inputManager.addMapping(CAMERA_RISE, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(CAMERA_LOWER, new KeyTrigger(KeyInput.KEY_Y));
        
        inputManager.addListener(this, CAMERA_MAPPINGS);
        
        inputManager.setCursorVisible(true);
	}
	
	 /**
     * Registers the FlyByCamera to receive input events from the provided
     * Dispatcher.
     * @param inputManager
     */
    public void unregisterInput(){
    
        if (inputManager == null) {
            return;
        }
    
        for (String s : CAMERA_MAPPINGS) {
            if (inputManager.hasMapping(s)) {
                inputManager.deleteMapping( s );
            }
        }

        inputManager.removeListener(this);        
    }

	protected void zoomCamera(float value){
        // derive fovY value
        float h = cam.getFrustumTop();
        float w = cam.getFrustumRight();
        float aspect = w / h;

        float near = cam.getFrustumNear();

        float fovY = FastMath.atan(h / near)
                  / (FastMath.DEG_TO_RAD * .5f);
        float newFovY = fovY + value * 0.1f * zoomSpeed;
        if (newFovY > 0f) {
            // Don't let the FOV go zero or negative.
            fovY = newFovY;
        }

        h = FastMath.tan( fovY * FastMath.DEG_TO_RAD * .5f) * near;
        w = h * aspect;

        cam.setFrustumTop(h);
        cam.setFrustumBottom(-h);
        cam.setFrustumLeft(-w);
        cam.setFrustumRight(w);
    }

    

    protected void moveCamera(float value,EAxis axis){
        Vector3f velocity = new Vector3f();
        Vector3f position = cam.getLocation().clone();

        switch (axis) {
		case NORTH_SOUTH:
			cam.getUp(velocity);
			break;
		case WEST_EAST:
			cam.getLeft(velocity);
			break;
		case UP_DOWN:
			cam.getDirection(velocity);
			break;			
		default:
			break;
		}

        velocity.multLocal(value * moveSpeed);

        if (motionAllowed != null)
            motionAllowed.checkMotionAllowed(position, velocity);
        else
            position.addLocal(velocity); //change position by adding velocity vector

        cam.setLocation(position);
    }



	@Override
	public void onAnalog(String name, float value, float tpf) {
//		LOGGER.log(Level.INFO, "AnalogInput! Name:"+name);
		if (!enabled)
            return;       
        if (middleMousePressed && CAMERA_NORTH.equals(name)){
        	moveCamera(value, EAxis.NORTH_SOUTH);           
        }else if (middleMousePressed && CAMERA_SOUTH.equals(name)){            
        	moveCamera(-value, EAxis.NORTH_SOUTH);
        }else if (middleMousePressed && CAMERA_WEST.equals(name)){
            moveCamera(value, EAxis.WEST_EAST);
        }else if (middleMousePressed && CAMERA_EAST.equals(name)){
            moveCamera(-value,EAxis.WEST_EAST);
        }else if (middleMousePressed && CAMERA_RISE.equals(name)){
        	moveCamera(value,EAxis.UP_DOWN);
        }else if (middleMousePressed && CAMERA_LOWER.equals(name)){        	
        	moveCamera(-value,EAxis.UP_DOWN);
        }else if (CAMERA_ZOOM_OUT.equals(name)){
            zoomCamera(value);
        }else if (CAMERA_ZOOM_IN.equals(name)){
            zoomCamera(-value);
        }
	}
	
	public void onAction(String name, boolean isPressed, float tpf){
		if(MIDDLE_MOUSE.equals(name)){
			middleMousePressed = isPressed;
		}
	};

    public void setMotionAllowedListener(MotionAllowedListener listener){
        this.motionAllowed = listener;
    }

    /**
     * Sets the zoom speed.
     * @param zoomSpeed 
     */
    public void setZoomSpeed(float zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }
    
    /**
     * Gets the zoom speed.  The speed is a multiplier to increase/decrease
     * the zoom rate.
     * @return zoomSpeed
     */
    public float getZoomSpeed() {
        return zoomSpeed;
    }

    /**
     * @param enable If false, the camera will ignore input.
     */
    public void setEnabled(boolean enable){
        if (enabled && !enable){
            if (inputManager!= null){
                inputManager.setCursorVisible(true);
            }
        }
        enabled = enable;
    }

    /**
     * @return If enabled
     * @see FlyByCamera#setEnabled(boolean)
     */
    public boolean isEnabled(){
        return enabled;
    }

    public enum EAxis{
    	NORTH_SOUTH,
    	WEST_EAST,
    	UP_DOWN
    }
}
