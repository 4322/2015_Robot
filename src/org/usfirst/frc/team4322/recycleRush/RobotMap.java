package org.usfirst.frc.team4322.recycleRush;

public class RobotMap {
	
	public static final int CANJAGUAR_SECONDARY_CONTROLLER_ADDRESS = 11;
	
	// Initialize Joystick Ports
	public static final int PILOT_CONTROLLER_JOYSTICK_PORT = 0;
	public static final int COPILOT_CONTROLLER_JOYSTICK_PORT = 1;
	
	// Initialize RobotDriveBase Ports
	public static final int TALON_LEFT_DRIVE_CHANNEL = 0;
	public static final int TALON_RIGHT_DRIVE_CHANNEL = 1;
	
	// Initialize Slide Drive CAN Address
	public static final int CANJAGUAR_SLIDE_DRIVE_ADDRESS = 10;
	
	// Initialize Pneumatic Control Module Solenoid Ports
	public static final int PISTON_FORWARD_PORT = 1;
	public static final int PISTON_REVERSE_PORT = 2;
	
	// Acceleration Ramps
	public static final double THROTTLE_RAMP = 0.03;
	public static final double STEERING_RAMP = 0.06;
	public static final double STRAFE_RAMP = 0.03;
		
	// Drive Power Limits
	public static final double THROTTLE_LIMIT = 0.8;
	public static final double STEERING_LIMIT = 0.7;
	public static final double STRAFE_LIMIT = 0.8;
		
	// Initialize Elevator CAN Addresses
	public static final int TOTE_ELEVATOR_CONTROLLER_ADDRESS = 30;
	public static final int CONTAINER_ELEVATOR_CONTROLLER_ADDRESS = 31;
}
