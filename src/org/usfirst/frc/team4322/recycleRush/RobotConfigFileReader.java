package org.usfirst.frc.team4322.recycleRush;

import java.io.*;
import java.util.Properties;

public class RobotConfigFileReader
{
	private static RobotConfigFileReader _instance = null;

    public final String CONFIG_FILE = "/RobotConfig.ini";
        
    public static RobotConfigFileReader getInstance()
    {
		// Look to see if the instance has already been created...
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotConfigFileReader();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }
    
    public void runRobotFileReader()
    {
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream("/Users/David/Documents/TextFile.ini"));
        }
        catch (IOException ex)
        {
            System.out.println("Failed to load RobotConfig.ini");
            System.exit(0);
        }
    	try
    	{
	        // Joystick Ports
	        RobotMap.PILOT_CONTROLLER_JOYSTICK_PORT = Integer.parseInt(p.getProperty("PILOT_CONTROLLER_JOYSTICK_PORT"));
	        RobotMap.COPILOT_CONTROLLER_JOYSTICK_PORT = Integer.parseInt(p.getProperty("COPILOT_CONTROLLER_JOYSTICK_PORT"));
	        // Drive Train Motor Controllers
	        RobotMap.TALON_LEFT_DRIVE_CHANNEL = Integer.parseInt(p.getProperty("TALON_LEFT_DRIVE_CHANNEL"));
	        RobotMap.TALON_RIGHT_DRIVE_CHANNEL = Integer.parseInt(p.getProperty("TALON_RIGHT_DRIVE_CHANNEL"));
	        RobotMap.CANJAGUAR_SLIDE_2_DRIVE_ADDRESS = Integer.parseInt(p.getProperty("CANJAGUAR_SLIDE_2_DRIVE_ADDRESS"));
	        RobotMap.CANJAGUAR_SLIDE_1_DRIVE_ADDRESS = Integer.parseInt(p.getProperty("CANJAGUAR_SLIDE_1_DRIVE_ADDRESS"));
	        // Pneumatic Control Module Solenoid Ports
	        RobotMap.PISTON_FORWARD_PORT = Integer.parseInt(p.getProperty("PISTON_FORWARD_PORT"));
	        RobotMap.PISTON_REVERSE_PORT = Integer.parseInt(p.getProperty("PISTON_REVERSE_PORT"));
	    	// Acceleration Ramps
	    	RobotMap.THROTTLE_RAMP = Double.parseDouble(p.getProperty("THROTTLE_RAMP"));
	    	RobotMap.STEERING_RAMP = Double.parseDouble(p.getProperty("STEERING_RAMP"));
	    	RobotMap.STRAFE_RAMP = Double.parseDouble(p.getProperty("STRAFE_RAMP"));
	    	// Drive Power Limits
	    	RobotMap.THROTTLE_LIMIT = Double.parseDouble(p.getProperty("THROTTLE_LIMIT"));
	    	RobotMap.STEERING_LIMIT = Double.parseDouble(p.getProperty("STEERING_LIMIT"));
	    	RobotMap.STRAFE_LIMIT = Double.parseDouble(p.getProperty("STRAFE_LIMIT"));
	    	RobotMap.AUTONOMOUS_DRIVE_SPEED = Double.parseDouble(p.getProperty("AUTONOMOUS_DRIVE_SPEED"));
	    	RobotMap.AUTONOMOUS_REVERSE_SPEED = Double.parseDouble(p.getProperty("AUTONOMOUS_REVERSE_SPEED"));
	    	RobotMap.XBONE_RIGHT_X_DEADBAND = Double.parseDouble(p.getProperty("XBONE_RIGHT_X_DEADBAND"));
	    	// Drive Gyro Port
	    	RobotMap.DRIVE_GYRO_ANALOG_PORT = Integer.parseInt(p.getProperty("DRIVE_GYRO_ANALOG_PORT"));
	    	// Drive Encoder Values
	    	RobotMap.DRIVE_ENCODER_A_GPIO_PORT = Integer.parseInt(p.getProperty("DRIVE_ENCODER_A_GPIO_PORT"));
	    	RobotMap.DRIVE_ENCODER_B_GPIO_PORT = Integer.parseInt(p.getProperty("DRIVE_ENCODER_B_GPIO_PORT"));
	    	RobotMap.ENCODER_DISTANCE_PER_TICK = Double.parseDouble(p.getProperty("ENCODER_DISTANCE_PER_TICK"));
	    	RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE = Integer.parseInt(p.getProperty("ENCODER_AUTONOMOUS_DRIVE_DISTANCE"));
	    	// P constants for autonomous driving
	    	RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("AUTONOMOUS_P_CONTROL_VALUE_GYRO"));
	    	RobotMap.TELEOP_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("TELEOP_P_CONTROL_VALUE_GYRO"));
	    	RobotMap.TELEOP_STRAFE_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("TELEOP_STRAFE_P_CONTROL_VALUE_GYRO"));
	    	// Accelerometer Deadband values per axis
	    	RobotMap.ACCELEROMETER_DEADBAND_X = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_X"));
	    	RobotMap.ACCELEROMETER_DEADBAND_Y = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_Y"));
	    	RobotMap.ACCELEROMETER_DEADBAND_Z = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_Z"));
	    	
	    	// Initialize Elevator CAN Addresses
	    	RobotMap.TOTE_ELEVATOR_CONTROLLER_ADDRESS = Integer.parseInt(p.getProperty("TOTE_ELEVATOR_CONTROLLER_ADDRESS"));
	    	RobotMap.CONTAINER_ELEVATOR_CONTROLLER_ADDRESS = Integer.parseInt(p.getProperty("CONTAINER_ELEVATOR_CONTROLLER_ADDRESS"));
	    	
	    	// Tote Motor Speeds
	    	RobotMap.TOTE_LIFT_MOTOR_SPEED = Double.parseDouble(p.getProperty("TOTE_LIFT_MOTOR_SPEED"));
	    	RobotMap.TOTE_STACK_MOTOR_SPEED = Double.parseDouble(p.getProperty("TOTE_STACK_MOTOR_SPEED"));
	    	
	    	// Tote Encoder Values
	    	RobotMap.TOTE_ENCODER_A_GPIO_PORT = Integer.parseInt(p.getProperty("TOTE_ENCODER_A_GPIO_PORT"));
	    	RobotMap.TOTE_ENCODER_B_GPIO_PORT = Integer.parseInt(p.getProperty("TOTE_ENCODER_B_GPIO_PORT"));
	    	RobotMap.PULSES_PER_TOTE_SPROCKET_REVOLUTION = Integer.parseInt(p.getProperty("PULSES_PER_TOTE_SPROCKET_REVOLUTION"));
	    	RobotMap.DISTANCE_BETWEEN_TOTE_ARMS = Integer.parseInt(p.getProperty("DISTANCE_BETWEEN_TOTE_ARMS"));
	    	
	    	// Tote Motor PIDController Values
	    	RobotMap.TOTE_MOTOR_DEFAULT_SET_POINT = Double.parseDouble(p.getProperty("TOTE_MOTOR_DEFAULT_SET_POINT"));
	    	RobotMap.TOTE_MOTOR_P_VALUE = Double.parseDouble(p.getProperty("TOTE_MOTOR_P_VALUE"));
	    	RobotMap.TOTE_MOTOR_I_VALUE = Double.parseDouble(p.getProperty("TOTE_MOTOR_I_VALUE"));
	    	RobotMap.TOTE_MOTOR_D_VALUE = Double.parseDouble(p.getProperty("TOTE_MOTOR_D_VALUE"));
	    	RobotMap.TOTE_MOTOR_MAX_OUTPUT_VOLTAGE = Integer.parseInt(p.getProperty("TOTE_MOTOR_MAX_OUTPUT_VOLTAGE"));
	    	RobotMap.TOTE_MOTOR_SOFT_TOP_LIMIT_POSITION = Integer.parseInt(p.getProperty("TOTE_MOTOR_SOFT_TOP_LIMIT_POSITION"));
	    	RobotMap.TOTE_MOTOR_SOFT_BOTTOM_LIMIT_POSITION = Integer.parseInt(p.getProperty("TOTE_MOTOR_SOFT_BOTTOM_LIMIT_POSITION"));
	    }
	    catch (NumberFormatException ex)
	    {
	    	RobotLogger.getInstance().writeErrorToFile("Exception caught in runRobotFileReader()", ex);
	    }
    }
}
