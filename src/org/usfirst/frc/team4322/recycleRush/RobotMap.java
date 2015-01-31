package org.usfirst.frc.team4322.recycleRush;

public class RobotMap {
	
	public static int CANJAGUAR_SECONDARY_CONTROLLER_ADDRESS = 11;
	
	public static String LAST_BUILD_TIME = "Robot4322_2015: 1/25/2015 11:30 AM";
	
	// Initialize Joystick Ports
	public static int PILOT_CONTROLLER_JOYSTICK_PORT = 0;
	public static int COPILOT_CONTROLLER_JOYSTICK_PORT = 1;
	
	// Initialize RobotDriveBase Ports
	public static int TALON_LEFT_DRIVE_CHANNEL = 0;
	public static int TALON_RIGHT_DRIVE_CHANNEL = 1;
	
	// Initialize Slide Drive CAN Address
	public static int CANJAGUAR_SLIDE_2_DRIVE_ADDRESS = 10;
	public static int CANJAGUAR_SLIDE_1_DRIVE_ADDRESS = 11;
	
	// Initialize Pneumatic Control Module Solenoid Ports
	public static int PISTON_FORWARD_PORT = 1;
	public static int PISTON_REVERSE_PORT = 2;
	
	// Acceleration Ramps
	public static double THROTTLE_RAMP = 0.03;
	public static double STEERING_RAMP = 0.06;
	public static double STRAFE_RAMP = 0.03;
		
	// Drive Power Limits
	public static double THROTTLE_LIMIT = 0.8;
	public static double STEERING_LIMIT = 0.7;
	public static double STRAFE_LIMIT = 0.8;
	public static double AUTONOMOUS_DRIVE_SPEED = -0.4;
	public static double AUTONOMOUS_REVERSE_SPEED = .25;
	public static double XBONE_RIGHT_X_DEADBAND = 0.17; //0.04
	
	// Drive Gyro Port
	public static int DRIVE_GYRO_ANALOG_PORT = 1;
	
	// Drive Encoder Values
	public static int DRIVE_ENCODER_A_GPIO_PORT = 1;
	public static int DRIVE_ENCODER_B_GPIO_PORT = 0;
	public static double ENCODER_DISTANCE_PER_TICK = 0.07539822368615503772310344119871;
	public static double ENCODER_AUTONOMOUS_DRIVE_DISTANCE = 40; //132
	
	// P constants for autonomous driving
	public static double AUTONOMOUS_P_CONTROL_VALUE_GYRO = 0.1;
	public static double TELEOP_P_CONTROL_VALUE_GYRO = 0.125;
	public static double TELEOP_STRAFE_P_CONTROL_VALUE_GYRO = 0.125;
	
	// Accelerometer Deadband values per axis
	public static double ACCELEROMETER_DEADBAND_X = 1.0; //0.05;
	public static double ACCELEROMETER_DEADBAND_Y = 1.0; //0.05;
	public static double ACCELEROMETER_DEADBAND_Z = 1; //pretty sensitive
	
	// Initialize Elevator CAN Addresses
	public static int TOTE_ELEVATOR_CONTROLLER_ADDRESS = 12;
	public static int CONTAINER_ELEVATOR_CONTROLLER_ADDRESS = 31;
	
	// Tote Motor Speeds
	public static double TOTE_LIFT_MOTOR_SPEED = 0.7;
	public static double TOTE_STACK_MOTOR_SPEED = -0.7;
	
	// Tote Encoder Values
	public static int TOTE_ENCODER_A_GPIO_PORT = 2;
	public static int TOTE_ENCODER_B_GPIO_PORT = 3;
	public static int PULSES_PER_TOTE_SPROCKET_REVOLUTION = 497; //(7*71 - gearbox):24T:60T:120T
	public static int DISTANCE_BETWEEN_TOTE_ARMS = 10; //test: needs change
	
	// Tote Motor PIDController Values
	public static double TOTE_MOTOR_DEFAULT_SET_POINT = 1.0; //position mode
	public static double TOTE_MOTOR_P_VALUE = 0.1; //proportional gain
	public static double TOTE_MOTOR_I_VALUE = 0.01; //integral gain
	public static double TOTE_MOTOR_D_VALUE = 0.5; //differential gain
	public static double TOTE_MOTOR_MAX_OUTPUT_VOLTAGE = 10;
	public static double TOTE_MOTOR_SOFT_TOP_LIMIT_POSITION = 10;
	public static double TOTE_MOTOR_SOFT_BOTTOM_LIMIT_POSITION = 1;
}