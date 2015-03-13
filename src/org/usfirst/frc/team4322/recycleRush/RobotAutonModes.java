package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotAutonModes {

	// Instance for the Singleton Class
    private static RobotAutonModes _instance = null;
    
    // Instance for ProximitySensor
    private ProximitySensor proximitySensorLeft = null;
    private ProximitySensor proximitySensorRight = null;

    // Initialize auto modes
	private SendableChooser autoChooser = null;
    private int autoMode = 0;
    private int lastAutoMode = 0;
    private static final int DO_NOTHING = 0;
    private static final int DRIVE_BACKWARD_LANDFILL_TO_AUTO = 10;
    private static final int DRIVE_FORWARD_LOADING_TO_AUTO = 20;
    private static final int PULL_TOTE_OR_CONTAINER = 30;
    private static final int PULL_TOTE_OR_CONTAINER_OVER_PLATFORM = 40;
    private static final int PUSH_TOTE_AND_CONTAINER = 50;
    private static final int STACK_YELLOW_TOTES = 60;
    private static final int PICK_UP_GREY_TOTES = 70;
    
    // Autonomous Tote Pickup Mode
    public int autoPickUpMode = DRIVE_FORWARD_AND_PICK_UP_TOTE;
    public static final int DRIVE_FORWARD_AND_PICK_UP_TOTE = 10;
    public static final int DRIVE_BACKWARD_TO_AUTO = 20;
    public static final int GET_AWAY_FROM_TOTE = 30;
    public static final int AUTO_COMPLETE = 40;
	
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
    
    /**
     * This method displays the SendableChooser buttons
     * available on the SmartDashboard to select a mode.
     */    
    public void runAutoModeChoosers()
    {
    	// Create the mode buttons on the SmartDashboard (does not work with new sfx currently)
    	autoChooser = new SendableChooser();
    	autoChooser.addDefault("Do Nothing (Auto 1)", new Integer(DO_NOTHING));
    	autoChooser.addObject("Drive Backward into Auto (from Landfill)", new Integer(DRIVE_BACKWARD_LANDFILL_TO_AUTO));
    	autoChooser.addObject("Drive Forward into Auto (from Loading)", new Integer(DRIVE_FORWARD_LOADING_TO_AUTO));
    	autoChooser.addObject("Push Tote / Container", new Integer(PULL_TOTE_OR_CONTAINER));
    	autoChooser.addObject("Push Tote / Container [over platform]", new Integer(PULL_TOTE_OR_CONTAINER_OVER_PLATFORM));
    	autoChooser.addObject("Push Tote AND Container", new Integer(PUSH_TOTE_AND_CONTAINER));
    	autoChooser.addObject("Stack Yellow Totes", new Integer(STACK_YELLOW_TOTES));
    	autoChooser.addObject("Pick Up Grey Totes", new Integer(PICK_UP_GREY_TOTES));
    	
    	SmartDashboard.putData("Auto Mode Chooser", autoChooser);
    }
    
    /**
     * Gets the current autonomous mode.
     */
    public int getAutoMode()
    {
    	// Get the selection
		autoMode = (Integer) autoChooser.getSelected();
		// Only record changes in auto modes
		if(lastAutoMode != autoMode)
		{
			RobotLogger.getInstance().sendToConsole("Auto Mode: " + autoMode);
			lastAutoMode = autoMode;
		}
		return autoMode;
    }
    
    /**
     * Initialize any variables before running
     * a certain autonomous mode.
     */
    public void initRobotAutonMode(int autoMode)
    {
		RobotLogger.getInstance().sendToConsole("Initializing Auto Mode: " + autoMode);
		
        // Create proximity sensors if they do not exist
        if(proximitySensorRight == null)
        {
        	proximitySensorRight = new ProximitySensor(RobotMap.DRIVE_PROXIMITY_SENSOR_1_ANALOG_PORT);
        }
        if(proximitySensorLeft == null)
        {
        	proximitySensorLeft = new ProximitySensor(RobotMap.DRIVE_PROXIMITY_SENSOR_2_ANALOG_PORT);
        }
    }
    
    /**
     * Run the selected autonomous mode.
     * @param autoMode
     */
    public void runRobotAutonMode(int autoMode)
    {
    	SmartDashboard.putNumber("AutoMode: ", autoMode);
    	RobotLogger.getInstance().sendToConsole("Initialize Auto Mode: " + autoMode);
    	switch(autoMode)
    	{
    	case DO_NOTHING:
    		// Do absolutely nothing
    		break;
    	case DRIVE_BACKWARD_LANDFILL_TO_AUTO:
    		// Start at the landfill zone, and drive backwards into the auto zone
    		RobotDriveBase.getInstance().driveToAutoZone(false, RobotMap.AUTO_DRIVE_FORWARD_DISTANCE);
    		break;
    	case DRIVE_FORWARD_LOADING_TO_AUTO:
    		// Start at the loading zone, and drive forwards into the auto zone
    		RobotDriveBase.getInstance().driveToAutoZone(true, RobotMap.AUTO_DRIVE_FORWARD_DISTANCE);
    		break;
    	case PULL_TOTE_OR_CONTAINER:
    		// Start at the loading zone, pick up a tote, drive backward with a tote/container into the auto zone
    		runPullToteIntoAuto(false);
    		break;
    	case PULL_TOTE_OR_CONTAINER_OVER_PLATFORM:
    		// Start at the loading zone, pick up a tote, drive backward with a tote/container over the platform into the auto zone
    		runPullToteIntoAuto(true);
    		break;
    	case PUSH_TOTE_AND_CONTAINER:
    		// Start at the loading zone, and strafe while pushing a tote & container into the auto zone
    		RobotDriveBase.getInstance().strafeToAutoZone(RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER);
    		break;
    	case STACK_YELLOW_TOTES: //?
    		// Drive to yellow totes and stack them
    		break;
    	case PICK_UP_GREY_TOTES: //?
    		// Ignore the auto points (except robot set) and pick up some grey totes!
    		break;
    	default:
    		// Do nothing
    		autoMode = DO_NOTHING;
    		break;
    	}
    }
    
    public void runPullToteIntoAuto(boolean platform)
    {
    	// Pick up a tote or container
    	switch(autoPickUpMode)
    	{
    	case DRIVE_FORWARD_AND_PICK_UP_TOTE:
    	    double leftDistance = proximitySensorLeft.getDistance();
    	    double rightDistance = proximitySensorRight.getDistance();
    	    // Pick up the tote!
    	    if(RobotDriveBase.getInstance().calculateToteDistanceError(leftDistance, rightDistance) <= RobotMap.PROXIMITY_SENSOR_ERROR_VALUE
    	    		&& rightDistance <= RobotMap.EXPECTED_TOTE_DISTANCE)
    	    {
    	    	RobotToteElevator.getInstance().startAutoLift();
    	    	autoPickUpMode = DRIVE_BACKWARD_TO_AUTO;
    	    }
    	    // We need to keep driving
    	    else
    	    {
    	    	RobotDriveBase.getInstance().autoDriveToTote();
    	    }
    		break;
    	case DRIVE_BACKWARD_TO_AUTO:
    		// Drive backward to auto zone
    		RobotDriveBase.getInstance().driveToAutoZone(false, platform ? RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER_OVER_PLATFORM : RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER);
    		autoPickUpMode = GET_AWAY_FROM_TOTE;
    		break;
    	case GET_AWAY_FROM_TOTE:
    		// If the distance traveled is less than 10 inches, move away!
    		if(RobotDriveBase.getInstance().getAutoDriveDistanceTraveled() < RobotMap.BACK_AWAY_FROM_TOTE_DISTANCE)
    		{
    			RobotDriveBase.getInstance().getAwayFromTote(true);
    		}
    		// Otherwise, shut the drive down; we're done
    		else
    		{
    			RobotDriveBase.getInstance().shutdownRobotDrive();
    			autoPickUpMode = AUTO_COMPLETE;
    		}
    	case AUTO_COMPLETE:
   		default:
   			break;
    	}
    }
}
