/**
 * 
 */
package de.cbf.gravity_shooter.input;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 * @author Alti
 *
 */
public class RTSCameraAppState extends AbstractAppState {
	private Application app;
	private RTSCamera rtsCam;
	
	
	@Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = app;

        if (app.getInputManager() != null) {
        
            if (rtsCam == null) {
            	rtsCam = new RTSCamera(app.getCamera());
            }
            
            rtsCam.registerWithInput(app.getInputManager());            
        }               
    }
            
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        rtsCam.setEnabled(enabled);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();

        if (app.getInputManager() != null) {        
        	rtsCam.unregisterInput();
        }        
    }

    public RTSCamera getRtsCam() {
		return rtsCam;
	}

	public void setRtsCam(RTSCamera rtsCam) {
		this.rtsCam = rtsCam;
	}

}
