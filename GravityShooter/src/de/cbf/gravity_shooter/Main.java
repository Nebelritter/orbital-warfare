package de.cbf.gravity_shooter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
 * @author Alti
 */
public class Main extends SimpleApplication {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static final String ACTION_SELECT = "action.select";
    public static final String ACTION_DE_SELECT = "action.deselect";

	private static final float MAX_VELOCITY = 20f;
	private static final Float MAX_FORCE = 100f;//maximum force for gravibodies
	private static final float ACCELERATION_VALUE = 40f;
	private static final float ROTATION_VALUE = FastMath.DEG_TO_RAD*180*5;

	public static final String SPACE_SHIP_NAME = "SpaceShip";

	private static final int GRAVIBODY_NUMBER = 3;
	
	public static void main(String[] args) {
        Main app = new Main();
        app.setPauseOnLostFocus(false);
        app.start();
    }

	protected BulletAppState bulletAppState;
	
	private PlayerKeyInputListener playerKeyListener;
	
	private Random random = new Random();

    @Override
    public void simpleInitApp() {
    	//remove camera movement with the mouse (we don't use fps controls)
    	initCameraControls();
    	initInputs();
    	initPhysics();
    	
    	List<GravityPoint> gravityPoints = initMap();
    	
//    	initGUI();
        
    	Material mat_default = new Material( 
                assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
    	
//    	generateNullPoint(mat_default);
    	
    	generateSpaceShip(mat_default,gravityPoints);
    }


	private void generateSpaceShip(Material mat_default, List<GravityPoint> gravityPoints) {
		float scaleFactor = 2f;
		
		Spatial spaceShip = assetManager.loadModel("Models/SpaceShips/SimpleSpaceShip.j3o");
        spaceShip.setName(SPACE_SHIP_NAME);
        spaceShip.setMaterial(mat_default);
        
        //rotate spaceShip to be at bottom pointing upwards
        float degToRad = FastMath.DEG_TO_RAD;
        
		spaceShip.scale(0.05f*scaleFactor);		
	
		//create physics properties
		CapsuleCollisionShape fuselageCollisionShape = new CapsuleCollisionShape(0.05f*scaleFactor,0.3f*scaleFactor,0);
		BoxCollisionShape wingsCollisionShape = new BoxCollisionShape(new Vector3f(0.1f*scaleFactor,0.01f*scaleFactor,0.25f*scaleFactor));
		CompoundCollisionShape starshipCollisionShape = new CompoundCollisionShape();
		starshipCollisionShape.addChildShape(fuselageCollisionShape, new Vector3f(0.1f*scaleFactor,0f,0f));
		starshipCollisionShape.addChildShape(wingsCollisionShape, new Vector3f(0.1f*scaleFactor,0f,0f));
		
    	RigidBodyControl physicsControl = new RigidBodyControl(starshipCollisionShape, 1);    	
    	physicsControl.setDamping(0f,0.2f);
		spaceShip.addControl(physicsControl);
		bulletAppState.getPhysicsSpace().add(physicsControl);
		
		Quaternion quarternion = new Quaternion().fromAngles(0,-90*degToRad, -90*degToRad);
		physicsControl.setPhysicsRotation(quarternion);
		
		//create controller, that will calculate gravity
		GravityControl playerGravityControl = new GravityControl(gravityPoints);
		playerGravityControl.setEpsilon(0.0f);
		playerGravityControl.setMaxForce(MAX_FORCE);		
		spaceShip.addControl(playerGravityControl);
		
		//create controller, that will move our ship
		PlayerShipControl playerShipControl = new PlayerShipControl();
		playerShipControl.setMaxVelocity(MAX_VELOCITY);
		//attach controller, that will move the playership
		spaceShip.addControl(playerShipControl);
    	
		rootNode.attachChild(spaceShip);		
	}


	protected void generateNullPoint(Material mat_default) {
		Sphere nullPoint = new Sphere(20, 20, 1f);    	
    	Geometry nullPointGeom = new Geometry("NullPoint", nullPoint);
    	nullPointGeom.setMaterial(mat_default);    
    	nullPointGeom.scale(0.1f);
    	rootNode.attachChild(nullPointGeom);    	    	
		
	}


	private List<GravityPoint> initMap() {
		List<GravityPoint> gravityPoints = new ArrayList<GravityPoint>();
		Material mat_default = new Material( 
                assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
    	
		for (int i = 0; i < GRAVIBODY_NUMBER; i++) {
			GravityPoint point = generateGravityPoint(mat_default);
			gravityPoints.add(point);
		}		
    	
		return gravityPoints;
	}

	private GravityPoint generateGravityPoint(Material mat_default) {
		
		float scaleFactor = 0.2f;
		
		float size = (random.nextFloat()+1)*scaleFactor;
		float force = 300f*size;
		float x = random.nextFloat()*5;
		float y = random.nextFloat()*5;
		
		//fixed point for testing
//		float size = (3)*scaleFactor;
//		float force = 100f*size;
//		float x = 0;
//		float y = 5;
		
		Vector3f location = new Vector3f(x,y,0);
		
		Sphere gravPoint = new Sphere(20, 20, size);    	
    	Geometry gravPointGeom = new Geometry("Star"+size, gravPoint);
    	gravPointGeom.setMaterial(mat_default);		
    	gravPointGeom.move(location);
    	    	
    	//make gravity points kinematic (not moved by physics, but collidable)
    	SphereCollisionShape sphereCollisionShape = new SphereCollisionShape(size);
    	RigidBodyControl physicsControl = new RigidBodyControl(sphereCollisionShape, force);
    	physicsControl.setKinematic(true);
    	gravPointGeom.addControl(physicsControl);
		bulletAppState.getPhysicsSpace().add(physicsControl);
    	
    	rootNode.attachChild(gravPointGeom);
    	
    	GravityPoint point = new GravityPoint(gravPointGeom.getWorldTranslation(), force);
    	
    	return point;     	
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
        
        //remove gravity of physics space
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO.clone());
        
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
		Spatial spaceShip = rootNode.getChild(SPACE_SHIP_NAME);
		GravityControl gravityControl = spaceShip.getControl(GravityControl.class);
		//obtain control to set desired movement/rotation that is created in inputlistener
		
		PlayerShipControl playerShipControl = spaceShip.getControl(PlayerShipControl.class);
		
		float desiredVelocity = playerKeyListener.getDesiredVelocity();
		float desiredRotationRads;
		desiredRotationRads = playerKeyListener.getDesiredRotationRads();		
		playerShipControl.setDesiredVelocity(desiredVelocity);		
		playerShipControl.setDesiredRotationRads(desiredRotationRads);
		playerShipControl.setGravityVector(gravityControl.getGravityVector());
		playerShipControl.setStopMovement(playerKeyListener.getStopMovement());
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
