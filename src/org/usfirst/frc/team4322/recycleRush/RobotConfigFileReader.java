package org.usfirst.frc.team4322.recycleRush;

import java.io.*;
import java.util.Properties;

public class RobotConfigFileReader
{
	private static RobotConfigFileReader _instance = null;

    public final String CONFIG_FILE = "/RobotConfig.ini";
        
    public static RobotConfigFileReader getInstance()
    {
		// Look to see if the instance has already been created
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotConfigFileReader();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }
    
    /**
     * This method only reads values that may be changed during the
     * course of the competition. Please avoid adding port / CAN
     * addresses and other fixed values, so as to limit the errors
     * from reading from this file. Variable values include those
     * used for autonomous, driving, and elevator control.
     */
    public void runRobotFileReader()
    {
    	// Maybe only read the files necessary for the competition mode..
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(CONFIG_FILE));
        }
        catch (IOException ex)
        {
            System.out.println("Failed to load RobotConfig.ini");
            System.exit(0);
        }
    	try
    	{
    		// Autonomous Driving Limits
    		RobotMap.AUTONOMOUS_DRIVE_SPEED = Double.parseDouble(p.getProperty("AUTONOMOUS_DRIVE_SPEED"));
	    	RobotMap.AUTONOMOUS_REVERSE_SPEED = Double.parseDouble(p.getProperty("AUTONOMOUS_REVERSE_SPEED"));
	    	// P constant for autonomous driving
	    	RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("AUTONOMOUS_P_CONTROL_VALUE_GYRO"));
	    	// Drive Encoder Values
	    	RobotMap.ENCODER_DISTANCE_PER_TICK = Double.parseDouble(p.getProperty("ENCODER_DISTANCE_PER_TICK"));
	    	RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE = Integer.parseInt(p.getProperty("ENCODER_AUTONOMOUS_DRIVE_DISTANCE"));
	    	
    		// Acceleration Ramps
	    	RobotMap.THROTTLE_RAMP = Double.parseDouble(p.getProperty("THROTTLE_RAMP_VALUE"));
	    	RobotMap.STEERING_RAMP = Double.parseDouble(p.getProperty("STEERING_RAMP_VALUE"));
	    	RobotMap.STRAFE_RAMP = Double.parseDouble(p.getProperty("STRAFE_RAMP_VALUE"));
	    	// Drive Power Limits
	    	RobotMap.THROTTLE_LIMIT = Double.parseDouble(p.getProperty("THROTTLE_LIMIT"));
	    	RobotMap.STEERING_LIMIT = Double.parseDouble(p.getProperty("STEERING_LIMIT"));
	    	RobotMap.STRAFE_LIMIT = Double.parseDouble(p.getProperty("STRAFE_LIMIT"));
	    	// Drive Gyro Port
	    	RobotMap.DRIVE_GYRO_ANALOG_PORT = Integer.parseInt(p.getProperty("DRIVE_GYRO_ANALOG_PORT"));
	    	RobotMap.XBONE_RIGHT_X_DEADBAND = Double.parseDouble(p.getProperty("XBONE_RIGHT_X_DEADBAND"));
	    	// P constants for driving
	    	RobotMap.TELEOP_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("TELEOP_P_CONTROL_VALUE_GYRO"));
	    	RobotMap.TELEOP_STRAFE_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("TELEOP_STRAFE_P_CONTROL_VALUE_GYRO"));
	    	// Accelerometer Deadband values per axis
	    	RobotMap.ACCELEROMETER_DEADBAND_X = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_X"));
	    	RobotMap.ACCELEROMETER_DEADBAND_Y = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_Y"));
	    	RobotMap.ACCELEROMETER_DEADBAND_Z = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_Z"));
	    	// Tote Encoder Values
	    	RobotMap.ELEVATOR_INITIAL_SEEK_SPEED = Double.parseDouble(p.getProperty("ELEVATOR_INITIAL_SEEK_SPEED"));
	    	//Tote Elevator PID Constants
	    	RobotMap.ELEVATOR_P_VALUE = Double.parseDouble(p.getProperty("ELEVATOR_P_VALUE"));
	    	RobotMap.ELEVATOR_I_VALUE = Double.parseDouble(p.getProperty("ELEVATOR_I_VALUE"));
	    	RobotMap.ELEVATOR_D_VALUE = Double.parseDouble(p.getProperty("ELEVATOR_D_VALUE"));
	    	//Tote Elevator positions
	    	RobotMap.ELEVATOR_POSITION_1 = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_1"));
	    	RobotMap.ELEVATOR_POSITION_2 = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_2"));
	    	RobotMap.ELEVATOR_POSITION_3 = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_3"));
	    	RobotMap.ELEVATOR_POSITION_4 = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_4"));
	    }
	    catch (NumberFormatException ex)
	    {
	    	RobotLogger.getInstance().writeErrorToFile("Exception caught in runRobotFileReader()", ex);
	    }
    }
}