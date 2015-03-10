package de.cbf.gravity_shooter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import de.cbf.gravity_shooter.camera.RTSCamera;
import de.cbf.gravity_shooter.camera.RTSCameraAppState;
import de.cbf.gravity_shooter.controls.GravityControl;
import de.cbf.gravity_shooter.controls.PlayerKeyInputListener;
import de.cbf.gravity_shooter.controls.PlayerShipControl;
import de.cbf.gravity_shooter.entity.GravityPoint;
import de.cbf.gravity_shooter.gui.StartScreen;
import de.lessvoid.nifty.Nifty;


/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static final String ACTION_SELECT = "action.select";
    public static final String ACTION_DE_SELECT = "action.deselect";

	private static final float MAX_VELOCITY = 1f;
	private static final float ACCELERATION_VALUE = 0.01f;
	private static final float ROTATION_VALUE = FastMath.DEG_TO_RAD*180;

	public static final String SPACE_SHIP_NAME = "SpaceShip";

	private static final String SPACE_SHIP_MOVEMENT_NODE = "SpaceShipMovementNode";
	
	public static void main(String[] args) {
        Main app = new Main();
        app.setPauseOnLostFocus(false);
        app.start();
    }

	protected BulletAppState bulletAppState;
	private Node spaceShipMovementNode;

	private PlayerKeyInputListener playerKeyListener;

    @Override
    public void simpleInitApp() {
    	//remove camera movement with the mouse (we don't use fps controls)
    	
    	initCameraControls();
    	initInputs();
    	initPhysics();
//    	initGUI();
        
    	Material mat_default = new Material( 
                assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
    	
    	Sphere nullPoint = new Sphere(20, 20, 1f);    	
    	Geometry nullPointGeom = new Geometry("NullPoint", nullPoint);
    	nullPointGeom.setMaterial(mat_default);    
    	nullPointGeom.scale(0.1f);
    	rootNode.attachChild(nullPointGeom);
    	    	
        Spatial spaceShip = assetManager.loadModel("Models/SpaceShips/SimpleSpaceShip.j3o");
        spaceShip.setName(SPACE_SHIP_NAME);
        spaceShip.setMaterial(mat_default);
        
        //rotate spaceShip to be at bottom pointing upwards
        float degToRad = FastMath.DEG_TO_RAD;
        Quaternion quarternion = new Quaternion().fromAngles(0,-90*degToRad, -90*degToRad);
		spaceShip.setLocalRotation(quarternion);
		spaceShip.scale(0.05f);
		

		spaceShipMovementNode = new Node(SPACE_SHIP_MOVEMENT_NODE);
		
		List<GravityPoint> gravityPoints = new ArrayList<GravityPoint>();
		GravityPoint pointOne = new GravityPoint(nullPointGeom.getWorldTranslation(), 0.01f);
		gravityPoints.add(pointOne);
		//create controller, that will calculate gravity
		GravityControl playerGravityControl = new GravityControl(gravityPoints);
		playerGravityControl.setEpsilon(0.0f);
		spaceShipMovementNode.addControl(playerGravityControl);
		
		//create controller, that will move our ship
		PlayerShipControl playerShipControl = new PlayerShipControl();
		playerShipControl.setMaxVelocity(MAX_VELOCITY);
		//attach controller, that will move the playership
		spaceShipMovementNode.addControl(playerShipControl);
				
		spaceShipMovementNode.attachChild(spaceShip);
		spaceShipMovementNode.move(0, -3f, 0);
		
		rootNode.attachChild(spaceShipMovementNode);
    }

	
	protected void initGUI() {
		//init startscreen app state
		StartScreen startScreen = new StartScreen();
		stateManager.attach(startScreen);
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
		Nifty nifty = niftyDisplay.getNifty();
		nifty.fromXml("Interface/GUI/MyNiftyGUI.xml", "start", startScreen);

		// attach the nifty display to the gui view port as a processor
		guiViewPort.addProcessor(niftyDisplay);

		
	}


	protected void initInputs() {		
	    inputManager.addMapping(ACTION_SELECT,new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	    inputManager.addMapping(ACTION_DE_SELECT,new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));	 
	    
	    playerKeyListener = new PlayerKeyInputListener();
	    playerKeyListener.setAccelerationValue(ACCELERATION_VALUE);
	    playerKeyListener.setRotationValue(ROTATION_VALUE);
	    
	    playerKeyListener.registerWithInputManager(inputManager);
	            
        inputManager.addListener(actionListenerSelection, ACTION_SELECT,ACTION_DE_SELECT);        
	}

	protected void initCameraControls() {
		stateManager.detach( stateManager.getState(FlyCamAppState.class));
		
		float xCamLoc = 0f;		
		float yCamLoc = 0f;
		float zCamLoc = 10f;
		Vector3f camLocation = new Vector3f(xCamLoc, yCamLoc, zCamLoc);
		cam.setLocation(camLocation);
		
    	RTSCamera rtsCamera = new RTSCamera(cam);
    	RTSCameraAppState rtsCamAppState = new RTSCameraAppState();
    	rtsCamAppState.setRtsCam(rtsCamera);
    	stateManager.attach(rtsCamAppState);		
	}
    
    protected void initPhysics() {		
    	 /** Set up Physics Game */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);
	}
    
//    protected void initGUI() {
//    	Screen screen = new Screen(this);
//    	screen.initialize();
//    	guiNode.addControl(screen);		
//	}


	/**
	 * all changes between Frames
	 * 
	 * @see com.jme3.app.SimpleApplication#simpleUpdate(float)
	 */
	@Override
    public void simpleUpdate(float tpf) {
		GravityControl gravityControl = spaceShipMovementNode.getControl(GravityControl.class);
		//obtain control to set desired movement/rotation that is created in inputlistener
		
		PlayerShipControl playerShipControl = spaceShipMovementNode.getControl(PlayerShipControl.class);
		
		float desiredVelocity = playerKeyListener.getDesiredVelocity();
		float desiredRotationRads;
		desiredRotationRads = playerKeyListener.getDesiredRotationRads();		
		playerShipControl.setDesiredVelocity(desiredVelocity);		
		playerShipControl.setDesiredRotationRads(desiredRotationRads);
		playerShipControl.setGravityVector(gravityControl.getGravityVector());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
   
    private ActionListener actionListenerSelection = new ActionListener() {
      public void onAction(String name, boolean keyPressed, float tpf) {
        if (name.equals(ACTION_SELECT) && !keyPressed) {
        	handleSelectionAction();
        }
        if (name.equals(ACTION_DE_SELECT) && !keyPressed) {
            handleDeSelectionAction();
        }
      }
    };

	protected void handleSelectionAction() {		
	    // Reset results list.
	    CollisionResults results = new CollisionResults();
	    // Convert screen click to 3d position
	    Vector2f click2d = inputManager.getCursorPosition();
	    Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
	    Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
	    // Aim the ray from the clicked spot forwards.
	    Ray ray = new Ray(click3d, dir);
	    // Collect intersections between ray and all nodes in results list.
	    rootNode.collideWith(ray, results);
	    // (Print the results so we see what is going on:)
	    for (int i = 0; i < results.size(); i++) {
	      // (For each “hit”, we know distance, impact point, geometry.)
	      float dist = results.getCollision(i).getDistance();
	      Vector3f pt = results.getCollision(i).getContactPoint();
	      String target = results.getCollision(i).getGeometry().getName();
	      System.out.println("Selection #" + i + ": " + target + " at " + pt + ", " + dist + " WU away.");
	    }
	    // Use the results -- we rotate the selected geometry.
	    if (results.size() > 0) {
	      // The closest result is the target that the player picked:
	      Geometry target = results.getClosestCollision().getGeometry();
	      // Here comes the action:
	      LOGGER.log(Level.INFO, "You selected:"+target.getName());
	    }
	     
	}

	protected void handleDeSelectionAction() {
		// TODO Auto-generated method stub
		
	}
}
