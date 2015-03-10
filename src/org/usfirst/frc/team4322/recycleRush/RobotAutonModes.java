package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotAutonModes {

	// Instance for the Singleton Class
    private static RobotAutonModes _instance = null;
    
    // Initialize auto modes
	private SendableChooser autoChooser = null;
    private int autoMode = 0;
    private static final int DO_NOTHING = 0;
    private static final int DRIVE_BACKWARD_LANDFILL_TO_AUTO = 10;
    private static final int DRIVE_FORWARD_LOADING_TO_AUTO = 20;
    private static final int PUSH_TOTE_OR_CONTAINER = 30;
    private static final int PUSH_TOTE_OR_CONTAINER_OVER_PLATFORM = 40;
    private static final int PUSH_TOTE_AND_CONTAINER = 50;
    private static final int STACK_YELLOW_TOTES = 60;
    private static final int PICK_UP_GREY_TOTES = 70;
	
	// This is the static getInstance() method that provides easy access to the RobotToteElevator singleton class.
    public static RobotAutonModes getInstance()
    {
        // Look to see if the instance has already been created...
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotAutonModes();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }
    
    public void runAutoModeChoosers()
    {
    	// Create the mode buttons on the SmartDashboard (does not work with new sfx currently)
    	autoChooser = new SendableChooser();
    	autoChooser.addDefault("Do Nothing (Auto 1)", DO_NOTHING);
    	autoChooser.addObject("Drive Backward into Auto (from Landfill)", DRIVE_BACKWARD_LANDFILL_TO_AUTO);
    	autoChooser.addObject("Drive Forward into Auto (from Loading)", DRIVE_FORWARD_LOADING_TO_AUTO);
    	autoChooser.addObject("Push Tote / Container", PUSH_TOTE_OR_CONTAINER);
    	autoChooser.addObject("Push Tote / Container [over platform]", PUSH_TOTE_OR_CONTAINER_OVER_PLATFORM);
    	autoChooser.addObject("Push Tote AND Container", PUSH_TOTE_AND_CONTAINER);
    	autoChooser.addObject("Stack Yellow Totes", STACK_YELLOW_TOTES);
    	autoChooser.addObject("Pick Up Grey Totes", PICK_UP_GREY_TOTES);
    	
    	SmartDashboard.putData("Auto Mode Chooser", autoChooser);
    	SmartDashboard.putNumber("[CURRENT] Autonomous Mode: ", autoMode);
    }
    
    public int getAutoMode()
    {
    	// Get the selected command and schedule it (sfx currently does not support SendableChoosers)
		autoMode = (Integer) autoChooser.getSelected();
		RobotLogger.getInstance().sendToConsole("Auto Mode: " + autoMode);
		
		return autoMode;
    }
    
    public void runRobotAutonMode(int autoMode)
    {
    	switch(autoMode)
    	{
    	case DO_NOTHING:
    		break;
    	case DRIVE_BACKWARD_LANDFILL_TO_AUTO:
    		// Start at the landfill zone, and drive backwards into the auto zone
    		RobotLogger.getInstance().sendToConsole("Auto Mode: 1");
    		break;
    	case DRIVE_FORWARD_LOADING_TO_AUTO:
    		// Start at the loading zone, and drive forwards into the auto zone
    		RobotLogger.getInstance().sendToConsole("Auto Mode: 2");
    		break;
    	case PUSH_TOTE_OR_CONTAINER:
    		// Start at the loading zone, and drive forwards while pushing a tote/container into the auto zone
    		RobotLogger.getInstance().sendToConsole("Auto Mode: 3");
    		break;
    	case PUSH_TOTE_OR_CONTAINER_OVER_PLATFORM:
    		// Start at the loading zone, and drive forwards while pushing a tote/container over the platform into the auto zone
    		RobotLogger.getInstance().sendToConsole("Auto Mode: 4");
    		break;
    	case PUSH_TOTE_AND_CONTAINER:
    		// Start at the loading zone, and drive forwards while pushing a tote & container into the auto zone
    		RobotLogger.getInstance().sendToConsole("Auto Mode: 5");
    		break;
    	case STACK_YELLOW_TOTES:
    		// Drive to yellow totes and stack them
    		RobotLogger.getInstance().sendToConsole("Auto Mode: 6");
    		break;
    	case PICK_UP_GREY_TOTES:
    		// Ignore the auto points (except robot set) and pick up some grey totes!
    		RobotLogger.getInstance().sendToConsole("Auto Mode: 7");
    		break;
    	default:
    		break;
    	
    	}
    }
}
