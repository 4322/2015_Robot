package org.usfirst.frc.team4322.recycleRush;

public class RobotMap {
	
	public static final int CANJAGUAR_SECONDARY_CONTROLLER_ADDRESS = 11;
	
	public static final String LAST_BUILD_TIME = "Robot4322_2015: 1/18/2015 5:58 PM";
	
	// Initialize Joystick Ports
	public static final int PILOT_CONTROLLER_JOYSTICK_PORT = 0;
	public static final int COPILOT_CONTROLLER_JOYSTICK_PORT = 1;
	
	// Initialize RobotDriveBase Ports
	public static final int TALON_LEFT_DRIVE_CHANNEL = 1;
	public static final int TALON_RIGHT_DRIVE_CHANNEL = 0;
	
	// Initialize Slide Drive CAN Address
	public static final int CANJAGUAR_SLIDE_2_DRIVE_ADDRESS = 10;
	public static final int CANJAGUAR_SLIDE_1_DRIVE_ADDRESS = 11;
	
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
	public static final double AUTONOMOUS_DRIVE_SPEED = -0.4;
	public static final double AUTONOMOUS_REVERSE_SPEED = .25;
	public static final double XBONE_RIGHT_X_DEADBAND = 0.04;
	
	// Encoder Values
	public static final int ENCODER_A_GPIO_PORT = 1;
	public static final int ENCODER_B_GPIO_PORT = 0;
	public static final double ENCODER_DISTANCE_PER_TICK = 0.07539822368615503772310344119871;
	public static final double ENCODER_AUTONOMOUS_DRIVE_DISTANCE = 132;
	
	// P constants for autonomous driving
	public static final double AUTONOMOUS_P_CONTROL_VALUE_GYRO = 0.1;
	public static final double TELEOP_P_CONTROL_VALUE_GYRO = 0.25;
	public static final double TELEOP_STRAFE_P_CONTROL_VALUE_GYRO = 0.25;
	
	// Initialize Elevator CAN Addresses
	public static final int TOTE_ELEVATOR_CONTROLLER_ADDRESS = 30;
	public static final int CONTAINER_ELEVATOR_CONTROLLER_ADDRESS = 31;
}
