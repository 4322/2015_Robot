package org.usfirst.frc.team4322.recycleRush;

/**
*
* @author FRC4322
*/

public class RobotContainerLift {

	// Instance for the Singleton Class
    private static RobotContainerLift _instance = null;
    
    // This is the static getInstance() method that provides easy access to the RobotContainerElevator singleton class.
    public static RobotContainerLift getInstance()
    {
        // Look to see if the instance has already been created...
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotContainerLift();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }
    
    // Main elevator initialization code should go here.
    public void initRobotContainerElevator()
    {
    	
    }
    
    // Container elevator code for disabled mode should go here.
    public void shutdownRobotContainerElevator()
    {
    	
    }
	
    // Initialization code for autonomous mode should go here.
    public void initAutonomous()
    {

    }

    // Initialization code for teleop mode should go here.
    public void initTeleop()
    {

    }

    // Periodic code for teleop mode should go here. This method is called ~50x per second.
    public void runTeleop()
    {
    	
    }
    
    // Initialization code for test mode should go here.
    public void initTest()
    {
    	
    }

    // Periodic code for test mode should go here. ~50x per second.
    public void runTest()
    {
    	
    }
}