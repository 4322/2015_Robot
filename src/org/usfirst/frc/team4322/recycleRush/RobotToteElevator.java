package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.CANTalon;

/**
 *
 * @author FRC4322
 */

public class RobotToteElevator {

	// Instance for the Singleton Class
    static private RobotToteElevator _instance = null;
    
    // Instance for tote lifting motor.
    private CANTalon toteMotor = null;
    
    // This is the static getInstance() method that provides easy access to the RobotToteElevator singleton class.
    public static RobotToteElevator getInstance()
    {
        // Look to see if the instance has already been created...
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotToteElevator();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }
    
    // Main elevator initialization code should go here.
    public void initRobotToteElevator()
    {
    	// Create toteMotor
    	if(toteMotor == null)
    	{
    		toteMotor = new CANTalon(RobotMap.TOTE_ELEVATOR_CONTROLLER_ADDRESS);
    	}
    }
    
    // Tote elevator code for disabled mode should go here.
    public void shutdownRobotToteElevator()
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
    	if(CoPilotController.getInstance().getYButton())
    	{
    		toteMotor.set(RobotMap.TOTE_LIFT_MOTOR_SPEED);
    	}
    	else if(CoPilotController.getInstance().getAButton())
    	{
    		toteMotor.set(RobotMap.TOTE_STACK_MOTOR_SPEED);
    	}
    	else
    	{
    		toteMotor.set(0);
    	}
    }
    
    // Initialization code for test mode should go here.
    public void initTest()
    {
    	
    }

    // Periodic code for test mode should go here. ~50x per second.
    public void runTest()
    {
    	
    }
    
    // Elevator PID Class
    
}
