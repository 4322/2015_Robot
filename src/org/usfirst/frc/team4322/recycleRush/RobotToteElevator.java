package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

/**
 *
 * @author FRC4322
 */

public class RobotToteElevator {

	// Instance for the Singleton Class
    private static RobotToteElevator _instance = null;
    
    // Instance for CANJaguar controller
    private CANJaguar toteMotor = null;
    
    // Instance for Encoder on Tote Lifting Motor
    private Encoder toteMotorEncoder = null;
        
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
		// Create the toteMotor CANJaguar if it does not exist.
    	if(toteMotor == null)
    	{
    		toteMotor = new CANJaguar(RobotMap.TOTE_ELEVATOR_CONTROLLER_ADDRESS);
    		// Using the parent system PIDOutput in CANJaguar subsystem
    		toteMotor.configMaxOutputVoltage(RobotMap.TOTE_MOTOR_MAX_OUTPUT_VOLTAGE);
    		toteMotor.configSoftPositionLimits(RobotMap.TOTE_MOTOR_SOFT_TOP_LIMIT_POSITION, RobotMap.TOTE_MOTOR_SOFT_BOTTOM_LIMIT_POSITION);
    		toteMotor.setPositionMode(CANJaguar.kQuadEncoder, RobotMap.PULSES_PER_TOTE_SPROCKET_REVOLUTION, RobotMap.TOTE_MOTOR_P_VALUE, RobotMap.TOTE_MOTOR_I_VALUE, RobotMap.TOTE_MOTOR_D_VALUE);
    	}
    	// Create the toteMotor quadrature encoder if it does not exist.
    	if(toteMotorEncoder == null)
    	{
    		toteMotorEncoder = new Encoder(RobotMap.TOTE_ENCODER_A_GPIO_PORT, RobotMap.TOTE_ENCODER_B_GPIO_PORT, false, EncodingType.k4X);
    		toteMotorEncoder.setDistancePerPulse(1 / RobotMap.PULSES_PER_TOTE_SPROCKET_REVOLUTION);
        	toteMotorEncoder.setReverseDirection(true); //need?
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
		toteMotor.set(RobotMap.TOTE_MOTOR_DEFAULT_SET_POINT);
		toteMotor.enableControl();
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
