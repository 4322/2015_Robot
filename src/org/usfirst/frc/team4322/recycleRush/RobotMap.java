package org.usfirst.frc.team4322.recycleRush;

public class RobotMap
{
	public static String LAST_BUILD_TIME = "Robot4322_2015: 3/14/2015 11:20 AM";
	
	// Initialize Joystick Ports
	public static int PILOT_CONTROLLER_JOYSTICK_PORT = 0;
	public static int COPILOT_CONTROLLER_JOYSTICK_PORT = 1;
	public static int COPILOT_CONTROL_PANEL_PORT = 2;
	
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
	public static double AUTO_ALIGN_STRAFE_RAMP = 0.5;
		
	// Drive Power Limits
	public static double THROTTLE_LIMIT = 1;//0.8;
	public static double STEERING_LIMIT = 1;//0.7;
	public static double STRAFE_LIMIT = -1;//0.8;  //lowered to make strafe speed match fw/reverse speed;
	public static double AUTONOMOUS_DRIVE_SPEED = 0.6;
	public static double AUTONOMOUS_REVERSE_SPEED =- .25;
	public static double STEERING_DEADBAND = 0.17; //0.04
	public static double THROTTLE_DEADBAND = 0.17;
	public static double CREEP_DRIVE_SPEED = -.30;
	public static double AUTO_ALIGN_STRAFE_SPEED = 0.3;
	public static double AUTO_ALIGN_DRIVE_FORWARD_SPEED = 0.3;
	public static double BACK_AWAY_FROM_TOTE_SPEED = -0.2;
	
	//dual rates (crawl mode) //motor power is divided by this value
	public static double THROTTLE_DUAL_RATE = 1.625;
	public static double STEERING_DUAL_RATE = 1.625;
	public static double STRAFE_DUAL_RATE = 1.625;
	
	// Drive Gyro Port
	public static int DRIVE_GYRO_ANALOG_PORT = 0;
	
	// Proximity Sensor Ports
	public static int DRIVE_PROXIMITY_SENSOR_1_ANALOG_PORT = 2;
	public static int DRIVE_PROXIMITY_SENSOR_2_ANALOG_PORT = 3;
	
	// Drive Encoder Values (250 counts / revolution)
	public static int DRIVE_ENCODER_A_GPIO_PORT = 0;
	public static int DRIVE_ENCODER_B_GPIO_PORT = 1;
	public static double ENCODER_DISTANCE_PER_TICK = 0.0523598776; //inches; diameter of wheel * pi / counts per revolution
	public static double BACK_AWAY_FROM_TOTE_DISTANCE = 10;
	public static double AUTO_DRIVE_FORWARD_DISTANCE = 60; //60-70
	public static double AUTO_DRIVE_STRAFE_DISTANCE = 200; //140
	public static double AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER = 115;
	public static double AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER_OVER_PLATFORM = 80;
	
	// Strafe Encoder Values (360 counts / revolution)
	public static int STRAFE_ENCODER_A_GPIO_PORT = 3;
	public static int STRAFE_ENCODER_B_GPIO_PORT = 2;
	public static double STRAFE_ENCODER_DISTANCE_PER_TICK = 0.0523598776; //inches; diameter of wheel * pi / counts per revolution
	public static double STRAFE_ENCODER_TOTE_ALIGNMENT_DRIVE_DISTANCE = 0.5; //inches
	
	// P constants for autonomous driving
	public static double AUTONOMOUS_P_CONTROL_VALUE_GYRO = 0.1;
	public static double TELEOP_P_CONTROL_VALUE_GYRO = 0.10375;
	public static double TELEOP_STRAFE_P_CONTROL_VALUE_GYRO = 0.15375;
	
	// Accelerometer Deadband values per axis
	public static double ACCELEROMETER_DEADBAND_X = 0.05; //0.4;
	public static double ACCELEROMETER_DEADBAND_Y = 0.4; //0.15; not used
	public static double ACCELEROMETER_DEADBAND_Z = 1; //pretty sensitive
	public static double ACCELEROMETER_DEADBAND_X_COMPRESSOR = 0.1;
	public static double ACCELEROMETER_DEADBAND_Y_COMPRESSOR = 0.8; //not used
	public static double ACCELEROMETER_DEADBAND_Z_COMPRESSOR = 1; //not used
	public static int ACCELEROMETER_DEADBAND_COUNTDOWN = 10;
	
	// Initialize Elevator CAN Addresses
	public static int TOTE_ELEVATOR_CONTROLLER_ADDRESS = 31;
	public static int TOTE_ELEVATOR_SLAVE_CONTROLLER_ADDRESS = 30;

	// Initialize Pneumatic Control Module Solenoid Ports
	public static int BRAKE_PISTON_FORWARD_PORT = 0;
	public static int BRAKE_PISTON_REVERSE_PORT = 1;
	
	public static int TILT_PISTON_FORWARD_PORT = 2;
	public static int TILT_PISTON_REVERSE_PORT = 3;
	
	// Tote Encoder Values
	public static int PULSES_PER_MOTOR_REVOLUTION = 7;
	public static double ELEVATOR_INITIAL_SEEK_SPEED = -0.5;
	
	// Tote Proximity Sensor Values
	public static double PROXIMITY_SENSOR_ERROR_VALUE = 0.1; // 1 inch error
	public static double EXPECTED_TOTE_DISTANCE = 7.25; // inches
	public static double MAX_EXPECTED_TOTE_DISTANCE = 10; //inches
	
	// Tote Elevator PID Constants
	public static double ELEVATOR_P_VALUE = 0.4;
	public static double ELEVATOR_I_VALUE = 0.00225;
	public static double ELEVATOR_D_VALUE = 0.4;
	public static double ELEVATOR_F_VALUE = 0;
	public static int ELEVATOR_IZONE_VALUE = 850;
	public static int ELEVATOR_RAMPRATE_VALUE = 0;
	public static double ELEVATOR_ANALOG_STICK_DEADBAND = 0.1;
	public static int ELEVATOR_POSITION_DISTANCE = 3125;
	public static double ELEVATOR_ALLOWED_CLOSED_LOOP_ERROR = 25;
	// Tote Elevator positions
	public static double ELEVATOR_JOYSTICK_DEADBAND = 0.1;
	public static double[] ELEVATOR_POSITIONS = {0,3228,6340,9478,11505};
	public static double[] ELEVATOR_STACK_POSITIONS = {0, 623, 3087, 5832, 8282};
	public static double[] ELEVATOR_CONTAINER_POSITIONS = {2694, 2070};
}