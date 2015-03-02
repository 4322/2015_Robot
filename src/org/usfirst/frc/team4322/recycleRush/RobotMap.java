package org.usfirst.frc.team4322.recycleRush;

public class RobotMap {
	
	public static String LAST_BUILD_TIME = "Robot4322_2015: 3/1/2015 5:00 PM";
	
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
	public static int SLIDE_PISTON_FORWARD_PORT = 4;
	public static int SLIDE_PISTON_REVERSE_PORT = 5;
	
	// Acceleration Ramps
	public static double THROTTLE_RAMP = 1;//0.08; //.03
	public static double STEERING_RAMP = 1;//0.12; //.06
	public static double STRAFE_RAMP = 1;//0.06; //03
		
	// Drive Power Limits
	public static double THROTTLE_LIMIT = 1;//0.8;
	public static double STEERING_LIMIT = .7;//0.7;
	public static double STRAFE_LIMIT = 1;//0.8;
	public static double AUTONOMOUS_DRIVE_SPEED = -0.4;
	public static double AUTONOMOUS_REVERSE_SPEED = .25;
	public static double STEERING_DEADBAND = 0.17; //0.04
	public static double THROTTLE_DEADBAND = 0.17;
	public static double CREEP_DRIVE_SPEED = -.50;
	
	// Drive Gyro Port
	public static int DRIVE_GYRO_ANALOG_PORT = 0;
	
	// Drive Encoder Values
	//TODO: Talon Drive encoder count: 250, Slide Drive encoder count: 360
	public static int DRIVE_ENCODER_A_GPIO_PORT = 1;
	public static int DRIVE_ENCODER_B_GPIO_PORT = 0;
	public static double ENCODER_DISTANCE_PER_TICK = 0.07539822368615503772310344119871; //inches
	public static double ENCODER_AUTONOMOUS_DRIVE_DISTANCE = 40; //132
	
	// P constants for autonomous driving
	public static double AUTONOMOUS_P_CONTROL_VALUE_GYRO = 0.1;
	public static double TELEOP_P_CONTROL_VALUE_GYRO = 0.07375;
	public static double TELEOP_STRAFE_P_CONTROL_VALUE_GYRO = 0.09375;
	
	// Accelerometer Deadband values per axis
	public static double ACCELEROMETER_DEADBAND_X = 0.4; //0.15
	public static double ACCELEROMETER_DEADBAND_Y = 0.4; //0.15
	public static double ACCELEROMETER_DEADBAND_Z = 1; //pretty sensitive
	
	// Initialize Elevator CAN Addresses
	public static int TOTE_ELEVATOR_CONTROLLER_ADDRESS = 31; //12;
	public static int CONTAINER_ELEVATOR_CONTROLLER_ADDRESS = 30; //31;
	
	// Initialize Pneumatic Control Module Solenoid Ports
	public static int BRAKE_PISTON_FORWARD_PORT = 0;
	public static int BRAKE_PISTON_REVERSE_PORT = 1;
	
	public static int TILT_PISTON_FORWARD_PORT = 2;
	public static int TILT_PISTON_REVERSE_PORT = 3;
	
	// Tote Encoder Values
	public static int PULSES_PER_MOTOR_REVOLUTION = 7;
	public static double ELEVATOR_INITIAL_SEEK_SPEED = -0.5;
	
	// Tote Elevator PID Constants
	public static double ELEVATOR_P_VALUE = 0.85;
	public static double ELEVATOR_I_VALUE = 0.00265;
	public static double ELEVATOR_D_VALUE = 0.4;
	public static double ELEVATOR_F_VALUE = 0;
	public static int ELEVATOR_IZONE_VALUE = 825;
	public static int ELEVATOR_RAMPRATE_VALUE = 0;
	public static double ELEVATOR_ANALOG_STICK_DEADBAND = 0.1;
	
	// Tote Elevator positions
	public static double ELEVATOR_JOYSTICK_DEADBAND = 0.1;
	public static double[] ELEVATOR_POSITIONS ={0,3095,6245,9430,12580};
}
