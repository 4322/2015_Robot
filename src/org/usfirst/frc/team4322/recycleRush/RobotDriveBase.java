/*
 * This singleton class will provide access to the Robot Drivebase.
 * FRC4322 will use the singleton pattern in the 2014 robot code to simplfy access to robot subsystems.
 * The robot will never have more than one instance of any subsystem, so the single pattern fits nicely.
 * http://en.wikipedia.org/wiki/Singleton_pattern
 */
package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author FRC4322
 */

public class RobotDriveBase
{

    // Instance for the Singleton Class
    private static RobotDriveBase _instance = null;
    
	// Instance for Power Distribution Panel
	private PowerDistributionPanel pdp = null;

    // Instances for Talon motor controllers
    private Talon leftTalon = null;
    private Talon rightTalon = null;
    
    // Instance for drive
    public RobotDrive robotDrive = null;
    
    // Instances for slide drive
    private CANJaguar slideJaguar1 = null;
    private CANJaguar slideJaguar2 = null;
    private DoubleSolenoid slideActuatorPiston = null;
    private Compressor compressor = null;

    // Instance for Encoder
    public Encoder driveEncoder = null;
    public Encoder strafeEncoder = null;
    
    // Instance for ProximitySensor
    private ProximitySensor proximitySensorLeft = null;
    private ProximitySensor proximitySensorRight = null;
    
    // Class declarations for the throttle and steering RobotDrive values
    private double throttleValue = 0;
    private double lastThrottleValue = 0;
    
    private double steeringValue = 0;
    private double lastSteeringValue = 0;
    
    private double strafingValue = 0;
    private double lastStrafingValue = 0;
    
    // Autonomous Gyro
    public Gyro robotGyro = null;
    private boolean dirtyGyro = true;
    
    // Autonomous Accelerometer
    private BuiltInAccelerometer robotAccelerometer = null;
    private int accelerometerDeadbandCount = 10;
    
    boolean strafeMode = true; // false; <-- Lets start in strafe mode, driver preference.
    boolean strafeStart = false;
    boolean strafePressed = false;
    
    // Auto alignment mode
    private boolean autoAlignButtonPressed = false;
	private double currentAutoAlignSpeed = 0.0;
	private double rightEncoderCount = 0.0;
	private double leftEncoderCount = 0.0;
	private double targetCenterEncoderCount = 0.0;
	private int dirtyAngularAlignmentCount = 0;
	
	// Tote Alignment Modes
    private int toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
    private static final int INITIALIZE_AUTO_ALIGNMENT = 0;
    private static final int LOSE_TOTE_EDGE = 1;
    private static final int FIND_TOTE_EDGES = 10;
    private static final int STRAFE_TO_TOTE_CENTER = 20;
    private static final int ANGULAR_ALIGNMENT_WITH_TOTE = 30;
    private static final int DRIVE_FORWARD_TO_TOTE = 40;
    private static final int AUTO_ALIGN_COMPLETE = 50;
    
    // This is the static getInstance() method that provides easy access to the RobotDriveBase singleton class.
    public static RobotDriveBase getInstance()
    {
        // Look to see if the instance has already been created...
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotDriveBase();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }

    // Main drive base initialization code should go here.
    public void initRobotDrive()
    {
    	try
    	{
	    	// Create the Power Distribution Panel if it does not exist, simply to clear sticky faults
	    	if(pdp == null)
	    	{
	    		pdp = new PowerDistributionPanel(); //assuming PDP ID = 0
	    		pdp.clearStickyFaults();
	    	}
	    	
	        // Create the leftTalon if it does not exist.
	        if(leftTalon == null)
	        {
	            leftTalon = new Talon(RobotMap.TALON_LEFT_DRIVE_CHANNEL);
	        }
	       
	        // Create the rightTalon if it does not exist.
	        if(rightTalon == null)
	        {
	            rightTalon = new Talon(RobotMap.TALON_RIGHT_DRIVE_CHANNEL);
	        }
	
	        // Create slideJaguar1 if it does not exist.
	        if(slideJaguar1 == null)
	        {
	        	slideJaguar1 = new CANJaguar(RobotMap.CANJAGUAR_SLIDE_1_DRIVE_ADDRESS);
	        	RobotLogger.getInstance().sendToConsole("CANJaguar Firmware Version: " + slideJaguar1.getFirmwareVersion());
	        }
	        
	        // Create slideJaguar2 if it does not exist.
	        if(slideJaguar2 == null)
	        {
	        	slideJaguar2 = new CANJaguar(RobotMap.CANJAGUAR_SLIDE_2_DRIVE_ADDRESS);
	        	RobotLogger.getInstance().sendToConsole("CANJaguar Firmware Version: " + slideJaguar2.getFirmwareVersion());
	        }
	        
	        // Create the piston to actuate the slide drive if it does not exist.
	        if(slideActuatorPiston == null)
	        {
	        	slideActuatorPiston = new DoubleSolenoid(RobotMap.SLIDE_PISTON_FORWARD_PORT, RobotMap.SLIDE_PISTON_REVERSE_PORT);
	        }
	        
	        // Create compressor if it does not exist, simply to clear sticky faults
	        if(compressor == null)
			{
				compressor = new Compressor(); //assuming PCM ID = 0
				compressor.clearAllPCMStickyFaults();
			}
	        
	        // Create Encoder if it does not exist
	        if(driveEncoder == null)
	        {
	        	driveEncoder = new Encoder(RobotMap.DRIVE_ENCODER_A_GPIO_PORT, RobotMap.DRIVE_ENCODER_B_GPIO_PORT, false, EncodingType.k4X);
	        	driveEncoder.setDistancePerPulse(RobotMap.ENCODER_DISTANCE_PER_TICK);
	        	driveEncoder.setReverseDirection(true);
	        }
	        
	        if(strafeEncoder == null)
	        {
	        	strafeEncoder = new Encoder(RobotMap.STRAFE_ENCODER_A_GPIO_PORT, RobotMap.STRAFE_ENCODER_B_GPIO_PORT, false, EncodingType.k4X);
	        	strafeEncoder.setDistancePerPulse(RobotMap.STRAFE_ENCODER_DISTANCE_PER_TICK);
	        	strafeEncoder.setReverseDirection(true);
	        }
	        
	        // Create robotGyro if it does not exist
	        if(robotGyro == null)
	        {
	        	try
	        	{
		        	RobotLogger.getInstance().sendToConsole("robotGyro initializing...");
		        	robotGyro = new Gyro(RobotMap.DRIVE_GYRO_ANALOG_PORT);
		        	RobotLogger.getInstance().sendToConsole("robotGyro initialization complete.");
	        	}
	        	catch (Exception ex)
	        	{
	        		RobotLogger.getInstance().writeErrorToFile("Exception caught in initRobotDrive() Gyro Instanciation", ex);
	        	}
	        }
	        
	        // Create robotAccelerometer if it does not exist
	        if(robotAccelerometer == null)
	        {
	        	robotAccelerometer = new BuiltInAccelerometer();
	        }
	        
	        // Create proximity sensors if they do not exist
	        if(proximitySensorRight == null)
	        {
	        	proximitySensorRight = new ProximitySensor(RobotMap.DRIVE_PROXIMITY_SENSOR_1_ANALOG_PORT);
	        }
	        if(proximitySensorLeft == null)
	        {
	        	proximitySensorLeft = new ProximitySensor(RobotMap.DRIVE_PROXIMITY_SENSOR_2_ANALOG_PORT);
	        }
	        
	        // Create robotDrive if it does not exist
	        if(robotDrive == null)
	        {
	        	robotDrive = new RobotDrive(leftTalon, rightTalon);
	        	robotDrive.setInvertedMotor(MotorType.kFrontLeft, true);
	        	robotDrive.setInvertedMotor(MotorType.kFrontRight, true);
	        	robotDrive.setInvertedMotor(MotorType.kRearLeft, true);
	        	robotDrive.setInvertedMotor(MotorType.kRearRight, true);
	        }
	        RobotLogger.getInstance().sendToConsole("Successfully started initRobotDriveBase().");
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in initRobotDrive()", ex);
    	}
    }

    // Drivebase code for disabled mode should go here.
    public void shutdownRobotDrive()
    {
    	try
    	{
	        // If the leftTalon exists, set the power to zero
	        if(leftTalon != null)
	        {
	            leftTalon.set(0);
	        }
	        // If the rightTalon exists, set the power to zero
	        if(rightTalon != null)
	        {
	            rightTalon.set(0);
	        }
	        // If the slideJaguar exists, set the power to zero
	        if(slideJaguar1 != null)
	        {
	            slideJaguar1.set(0);
	        }
	        
	        // If the slideJaguar exists, set the power to zero
	        if(slideJaguar2 != null)
	        {
	            slideJaguar2.set(0);
	        }
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in shutdownRobotDrive()", ex);
    	}
    }

    // Initialization code for autonomous mode should go here.
    public void initAutonomous()
    {
    	try
    	{
	    	driveEncoder.reset();
	    	robotGyro.reset();
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotDriveBase.initAutonomous()", ex);
    	}
    }
    
    public void runAutonomous(int mode)
    {
    	try
    	{
		    // Everything is run in RobotAutonModes
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotDriveBase.runAutonomous()", ex);
    	}
    }

    // Initialization code for teleop mode should go here.
    public void initTeleop()
    {
    	try
    	{
	        // Reset throttle and steering values
	        throttleValue = 0;
	        steeringValue = 0;
	        strafingValue = 0;
	    	driveEncoder.reset();
	    	robotGyro.reset();
    		strafeEncoder.reset();
    		RobotLogger.getInstance().sendToConsole("RobotDriveBase.initTeleop() successfully run.");
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotDriveBase.initTeleop()", ex);
    	}
    }

    // Periodic code for teleop mode should go here. This method is called ~50x per second.
    public void runTeleop()
    {
    	try
    	{
    		// You must press and hold the button to auto align with the tote
	        if(PilotController.getInstance().getAutoAlignButton() || PilotController.getInstance().getQuickAutoAlignButton())
	        {
	        	if(!autoAlignButtonPressed) RobotLogger.getInstance().sendToConsole("*****AUTO ALIGNMENT BEGIN*****");
        		dirtyAngularAlignmentCount = 0;
	        	autoAlignButtonPressed = true;
	        	// Skip to angular alignment
	        	if(toteAlignmentMode == AUTO_ALIGN_COMPLETE)
	        	{
		        	if(PilotController.getInstance().getAutoAlignButton())
		        	{
		        		toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
		        	}
		        	else
		        	{
		        		dirtyAngularAlignmentCount++;
		        		toteAlignmentMode = ANGULAR_ALIGNMENT_WITH_TOTE;
		        	}
	        	}
	        }
	        // If we are not holding down the button, the driver may drive
	        else
	        {
	        	autoAlignButtonPressed = false;
	        	toteAlignmentMode = AUTO_ALIGN_COMPLETE;
	        }
	        
	        // Get Proximity Sensor Values for Left and Right
    		double toteDistanceRight = proximitySensorRight.getDistance();
	        double toteDistanceLeft = proximitySensorLeft.getDistance();
	        
//	        SmartDashboard.putBoolean("Picking up tote!", calculateToteDistanceError(toteDistanceRight, toteDistanceLeft) <= RobotMap.PROXIMITY_SENSOR_ERROR_VALUE && toteDistanceRight < 10.0 && toteDistanceLeft < 10.0);
	        SmartDashboard.putNumber("Tote Distance (Right)", toteDistanceRight);
	        SmartDashboard.putNumber("Tote Distance (Left)", toteDistanceLeft);
	        SmartDashboard.putBoolean("AutoAlignButton: ", autoAlignButtonPressed);
	        
	        // Driving
    		if(!autoAlignButtonPressed)
    		{
		        // Get the current throttle value from the Pilot Controller
		        throttleValue = PilotController.getInstance().getDriveBaseThrottleStick();
		        
		        // Get the current steering value from the Pilot Controller
		        steeringValue = PilotController.getInstance().getDriveBaseSteeringStick();
		        
		        // Get the current strafing value from the Pilot Controller
		        strafingValue = PilotController.getInstance().getDriveBaseStrafingStick();
		        
		        // Strafe motor reversal
		        strafingValue *= -1; 
		        
		        // Dump values to dashboard
		    	SmartDashboard.putNumber("Throttle: ", throttleValue);
		    	SmartDashboard.putNumber("Steering: ", steeringValue);
		    	SmartDashboard.putNumber("Strafing: ", strafingValue);
	
		        // Ramping Throttle Values
		    	rampThrottleValues(throttleValue, lastThrottleValue);
		        
		        // Ramping Steering Values
		        rampSteeringValues(steeringValue, lastSteeringValue);
		        
		        // Ramping Strafing Values
		        rampStrafingValues(strafingValue, lastStrafingValue);
		        
		        // Scale Values
		        boolean dualRateEnabled = PilotController.getInstance().getDualRateButton();
		        throttleValue = (throttleValue * RobotMap.THROTTLE_LIMIT)  / (dualRateEnabled ? RobotMap.THROTTLE_DUAL_RATE : 1);
		        steeringValue  = (steeringValue * RobotMap.STEERING_LIMIT) / (dualRateEnabled ? RobotMap.STEERING_DUAL_RATE : 1);
		        strafingValue = (strafingValue * RobotMap.STRAFE_LIMIT) / (dualRateEnabled ? RobotMap.STRAFE_DUAL_RATE : 1);
		        
		        // Toggle Strafe Mode
		    	if(PilotController.getInstance().getSlideDriveLiftButton())
		    	{
		    		if(!strafePressed)
		    		{
		        		strafeMode = !strafeMode;
		        		strafePressed = true;
		    		}
		    	}
		    	else
		    	{
		    		strafePressed = false;
		    	}
		    	
		    	double gyroAngle = 0;
		    	try
		    	{
			    	// Drive with Gyro Assist, get Angle
			    	gyroAngle = robotGyro.getAngle();
		    	}
		    	catch (Exception ex)
		    	{
		    		RobotLogger.getInstance().writeErrorToFile("Exception caught in runTeleop() robotGyro.getAngle()", ex);
		    	}
		    	
		    	double accelerometerXAxis = robotAccelerometer.getX();
		    	
		    	// Handle DRIVING mode
		        if(!strafeMode)
		        {
		        	if(strafeStart)
		        	{
		        		strafeStart = false;
		        	}
		        	// Lift the SLIDE drive and shut off slide motors
		        	slideActuatorPiston.set(Value.kReverse);
		        	slideJaguar1.set(0);
		        	slideJaguar2.set(0);
		        }
		        // Handle STRAFING mode
		        else if(strafeMode)
			    {
			        // Reset the gyro and get a new heading when we first enter STRAFE mode
			        if(!strafeStart)
			        {
			        	strafeStart = true;
			        	robotGyro.reset();
			        }
		        	slideActuatorPiston.set(Value.kForward);
		        	slideJaguar1.set(strafingValue * -1);
		        	slideJaguar2.set(strafingValue * -1);
			    }
		        
		        // Check to see if we are not steering and reset the gyro heading 
		        if(Math.abs(PilotController.getInstance().getDriveBaseSteeringStick()) < RobotMap.STEERING_DEADBAND)
		        {
		         	// If the Gyro X Axis is dirty, and we have stopped turning
	//	           	if(dirtyGyro && Math.abs(accelerometerXAxis) < RobotMap.ACCELEROMETER_DEADBAND_X)
	//	           			     && Math.abs(robotAccelerometer.getY()) < RobotMap.ACCELEROMETER_DEADBAND_Y)
		           	if(dirtyGyro)
		           	{
		           		if(Math.abs(accelerometerXAxis) < RobotMap.ACCELEROMETER_DEADBAND_X)
		           		{
		           			if(accelerometerDeadbandCount <= 0)
		           			{
		    	           		// Reset the Gyro ONLY ONCE per dirty
		    	               	robotGyro.reset();
		    	               	dirtyGyro = false;
		           			}
		           			else
		           			{
		           				accelerometerDeadbandCount--;
		           			}
		           		}
		            }
		           	// If the Gyro is dirty, no auto-correcting
		           	if(dirtyGyro)
		            {
		            	gyroAngle = 0;
		            }
	    	        try
	            	{
	            		// Drive STRAIGHT and use the GYRO to keep us straight
		            	double compensatedSteeringValue = gyroAngle * RobotMap.TELEOP_P_CONTROL_VALUE_GYRO * -1;            	
			            robotDrive.arcadeDrive(throttleValue, compensatedSteeringValue);
		            }
		            catch (Exception ex)
		            {
		            	RobotLogger.getInstance().writeErrorToFile("Exception caught in getting gyro compensation value", ex);
		            }
	            }
	            // Here, the driver is steering and we will NOT USE the gyro
	            else
	            {
	            	robotDrive.arcadeDrive(throttleValue, steeringValue);
	            	 // When steering, the gyro is always dirty
	            	robotGyro.reset();
	            	dirtyGyro = true;
	            	accelerometerDeadbandCount = RobotMap.ACCELEROMETER_DEADBAND_COUNTDOWN;
	            }
		        SmartDashboard.putNumber("Drive Encoder Distance", driveEncoder.getDistance());
		        SmartDashboard.putNumber("Strafe Distance", strafeEncoder.getDistance());
		        SmartDashboard.putNumber("Strafe Distance (RAW)", strafeEncoder.getRaw());
		        SmartDashboard.putNumber("Gyro Angle", dirtyGyro ? 999999 : gyroAngle);
				SmartDashboard.putNumber("ACCEL (X)", robotAccelerometer.getX());
				SmartDashboard.putBoolean("ACCEL Deadband", (Math.abs(accelerometerXAxis) < RobotMap.ACCELEROMETER_DEADBAND_X));
    		}
    		// If autoAlign is true, meaning the button is being pressed
    		else
    		{
    			// If there is a tote within range of either sensor
		        if(toteDistanceRight < RobotMap.MAX_EXPECTED_TOTE_DISTANCE || toteDistanceLeft < RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
		        {
			        // Run the State Engine
		        	switch(toteAlignmentMode)
		        	{
			        	case INITIALIZE_AUTO_ALIGNMENT:
			        		RobotLogger.getInstance().sendToConsole("(INITIALIZE_AUTO_ALIGNMENT) Initializing Auto Alignment Mode");
			        		// Initialize encoder counts
			        		rightEncoderCount = 0.0;
			        		leftEncoderCount = 0.0;
			        		targetCenterEncoderCount = 0.0;
			        		// Reset Strafe Encoder
			        		strafeEncoder.reset();
			        		// Reset Gyro
			        		robotGyro.reset();
			        		
					        // If the left sensor detects tote, but the right one doesn't
					        if(toteDistanceRight > RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
					        {
					        	RobotLogger.getInstance().sendToConsole("(INITIALIZE_AUTO_ALIGNMENT) Robot too far to the RIGHT of tote.");
					        	// Move left
					        	currentAutoAlignSpeed = RobotMap.AUTO_ALIGN_STRAFE_SPEED * -1;
				        		toteAlignmentMode = FIND_TOTE_EDGES;
					        }
					        // If the right sensor detects tote, but the left one doesn't
					        else if (toteDistanceLeft > RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
					        {
					        	RobotLogger.getInstance().sendToConsole("(INITIALIZE_AUTO_ALIGNMENT) Robot too far to the LEFT of tote.");
					        	// Move right
					        	currentAutoAlignSpeed = RobotMap.AUTO_ALIGN_STRAFE_SPEED;
				        		toteAlignmentMode = FIND_TOTE_EDGES;
					        }
					        // If both sensors detect a tote
					        else
					        {
					        	RobotLogger.getInstance().sendToConsole("(INITIALIZE_AUTO_ALIGNMENT) Robot needs to find a tote edge.");
					        	toteAlignmentMode = LOSE_TOTE_EDGE;
					        }
			        		break;
			        	// BOTH SENSORS ARE IN RANGE
			        	case LOSE_TOTE_EDGE:
			        		if(toteDistanceLeft <= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        		{
			        			slideJaguar1.set(RobotMap.AUTO_ALIGN_STRAFE_SPEED * -1);
			        			slideJaguar2.set(RobotMap.AUTO_ALIGN_STRAFE_SPEED * -1);
			            		// Strafe STRAIGHT and use the GYRO to keep us straight
						    	double gyroAngle = robotGyro.getAngle();
				            	double compensatedSteeringValue = gyroAngle * RobotMap.TELEOP_P_CONTROL_VALUE_GYRO * -1;            	
					            robotDrive.arcadeDrive(0, compensatedSteeringValue);
			        		}
			        		else
			        		{
					        	RobotLogger.getInstance().sendToConsole("(LOSE_TOTE_EDGE) We have lost the tote edge!");
			        			slideJaguar1.set(0);
			        			slideJaguar2.set(0);
			        			toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
			        		}
			        		break;
			        	case FIND_TOTE_EDGES:
			        		// If we're moving right
			        		if(currentAutoAlignSpeed > 0)
			        		{
			        			if(leftEncoderCount == 0 && toteDistanceLeft <= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        			{
			        				leftEncoderCount = strafeEncoder.getRaw();
						        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) LEFT Tote Edge Found! Encoder count: " + leftEncoderCount);
			        			}
			        			else if (rightEncoderCount == 0 && toteDistanceRight >= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        			{
			        				rightEncoderCount = strafeEncoder.getRaw();
						        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) RIGHT Tote Edge Found! Encoder count: " + rightEncoderCount);
			        			}
			        		}
			        		else
			        		{
			        			if(leftEncoderCount == 0 && toteDistanceLeft >= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        			{
			        				leftEncoderCount = strafeEncoder.getRaw();
						        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) LEFT Tote Edge Found! Encoder count: " + leftEncoderCount);
			        			}
			        			else if (rightEncoderCount == 0 && toteDistanceRight <= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        			{
			        				rightEncoderCount = strafeEncoder.getRaw();
						        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) RIGHT Tote Edge Found! Encoder count: " + rightEncoderCount);
			        			}
			        		}
			        		if(leftEncoderCount != 0 && rightEncoderCount != 0)
			        		{
					        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) BOTH EDGES FOUND. Calculating targetCenterEncoderCount...");
			        			slideJaguar1.set(0);
					        	slideJaguar2.set(0);
					        	currentAutoAlignSpeed = currentAutoAlignSpeed * -1;
					        	targetCenterEncoderCount = (leftEncoderCount + rightEncoderCount) / 2;
					        	toteAlignmentMode = STRAFE_TO_TOTE_CENTER;
					        	RobotLogger.getInstance().sendToConsole("Target encoder count: " + targetCenterEncoderCount);					        	
			        		}
			        		else
			        		{
			        			slideJaguar1.set(currentAutoAlignSpeed);
					        	slideJaguar2.set(currentAutoAlignSpeed);
			            		// Strafe STRAIGHT and use the GYRO to keep us straight
						    	double gyroAngle = robotGyro.getAngle();
				            	double compensatedSteeringValue = gyroAngle * RobotMap.TELEOP_P_CONTROL_VALUE_GYRO * -1;            	
					            robotDrive.arcadeDrive(0, compensatedSteeringValue);
			        		}	
			        		break;
			        	case STRAFE_TO_TOTE_CENTER:
			        		// If we moved right, and have reached our position
			        		if((currentAutoAlignSpeed > 0 && strafeEncoder.getRaw() <= targetCenterEncoderCount)
			        		|| (currentAutoAlignSpeed < 0 && strafeEncoder.getRaw() >= targetCenterEncoderCount))
			        		{
					        	RobotLogger.getInstance().sendToConsole("(STRAFE_TO_TOTE_CENTER) WE HAVE BEEN CENTERED ON THE TOTE! Theoretically...");
					        	RobotLogger.getInstance().sendToConsole("(STRAFE_TO_TOTE_CENTER) End Encoder Count = " + strafeEncoder.getRaw());
			        			slideJaguar1.set(0);
				        		slideJaguar2.set(0);
				        		toteAlignmentMode = ANGULAR_ALIGNMENT_WITH_TOTE;
			        		}
				        	// We have not reached the position
				        	else
				        	{
				        		// Keep driving until we have reached the center
				        		slideJaguar1.set(currentAutoAlignSpeed);
				        		slideJaguar2.set(currentAutoAlignSpeed);
			            		// Strafe STRAIGHT and use the GYRO to keep us straight
						    	double gyroAngle = robotGyro.getAngle();
				            	double compensatedSteeringValue = gyroAngle * RobotMap.TELEOP_P_CONTROL_VALUE_GYRO * -1;            	
					            robotDrive.arcadeDrive(0, compensatedSteeringValue);
				        	}
			        		break;
			        	case ANGULAR_ALIGNMENT_WITH_TOTE:
			        		// Try to get the left and right tote distances within 1/2 inch of each other
			        		double angularAlignment = Math.abs(toteDistanceLeft - toteDistanceRight); 
			        		if(angularAlignment < .5)
			        		{
			        			// We have acheived angular alignment
					            robotDrive.arcadeDrive(0, 0);
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) WE HAVE ACHIEVED ANGLUAR ALIGNMENT WITH THE TOTE!");
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Within Angular Alignment = " + angularAlignment);
					        	// Look to see if we want to redo the lateral alignment, only once though
					        	if(dirtyAngularAlignmentCount == 1)
					        	{
						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Dirty Alignment requires starting over.");
				        			toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
					        	}
					        	else
					        	{
						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Driving Forward to Tote.");
				        			toteAlignmentMode = DRIVE_FORWARD_TO_TOTE;
					        	}	
			        		}
			        		else
			        		{
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Out of Angular Alignment = " + angularAlignment);
					        	dirtyAngularAlignmentCount++;
			        			// Determine which way to turn
			        			if(toteDistanceLeft < toteDistanceRight)
			        			{
			        				// Turn left (Negative Steering Value)
						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Turning Left...");
						            robotDrive.arcadeDrive(0, RobotMap.AUTO_ALIGN_DRIVE_FORWARD_SPEED * -1.75);
			        			}
			        			else
			        			{
			        				// Turn right (Positive Steering Value)
						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Turning Right...");
						            robotDrive.arcadeDrive(0, RobotMap.AUTO_ALIGN_DRIVE_FORWARD_SPEED * 1.75);
			        			}
			        		}
			        		break;
			        	case DRIVE_FORWARD_TO_TOTE:
			    			// Once the robot is within reach, switch to next mode
			    			if(toteDistanceLeft <= RobotMap.EXPECTED_TOTE_DISTANCE && toteDistanceRight <= RobotMap.EXPECTED_TOTE_DISTANCE)
							{
					            robotDrive.arcadeDrive(0, 0);
					        	// Begin tote lift
					        	RobotToteElevator.getInstance().startAutoLift();
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) WE HAVE ACHIEVED ANGLUAR ALIGNMENT WITH THE TOTE!");
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Left Tote Distance  = " + toteDistanceLeft);
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Right Tote Distance = " + toteDistanceRight);
								toteAlignmentMode = AUTO_ALIGN_COMPLETE;
							}
			    			else
			    			{
			    				// Drive Forward (Negative Value)
					            robotDrive.arcadeDrive(RobotMap.AUTO_ALIGN_DRIVE_FORWARD_SPEED * -1.75, 0);
			    			}
							break;
			        	case AUTO_ALIGN_COMPLETE:
			        		break;
			        	default:
			        		// We've pressed the button, but don't know what mode we're in?
			        		toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
			        		break;
			    	}
			    }
		        else
		        {
		        	RobotLogger.getInstance().sendToConsole("(AUTO-ALIGN) NO TOTE IN RANGE!");
		        }
			}
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotDriveBase.runTeleop()", ex);
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

    public void rampThrottleValues(double throttle, double lastThrottle)
    {
    	// Ramping Drive Values
        if((Math.abs(throttle) - Math.abs(lastThrottle)) > RobotMap.THROTTLE_RAMP)
        {
        	if(throttle > lastThrottle)
        	{
        		throttle = lastThrottle + RobotMap.THROTTLE_RAMP;
        	}
        	else
        	{
        		throttle = lastThrottle - RobotMap.THROTTLE_RAMP;
        	}
        }
        lastThrottle = throttle;
    }
    
    public void rampSteeringValues(double steering, double lastSteering)
    {
    	// Ramping Steering Values
    	if((Math.abs(steering) - Math.abs(lastSteering)) > RobotMap.STEERING_RAMP)
        {
        	if(steering > lastSteering)
        	{
        		steering = lastSteering + RobotMap.STEERING_RAMP;
        	}
        	else
        	{
        		steering = lastSteering - RobotMap.STEERING_RAMP;
        	}
        }
    	lastSteering = steering;
    }
    
    public void rampStrafingValues(double strafing, double lastStrafing)
    {
	    if((Math.abs(strafing) - Math.abs(lastStrafing)) > RobotMap.STRAFE_RAMP)
	    {
	    	if(strafing > lastStrafing)
	    	{
	    		strafing = lastStrafing + RobotMap.STRAFE_RAMP;
	    	}
	    	else
	    	{
	    		strafing = lastStrafing - RobotMap.STRAFE_RAMP;
	    	}
	    }
	    lastStrafing = strafing;
    }
    
    public double calculateToteDistanceError(double a, double b)
    {
    	// Returns the percent error of the two distances
    	return (a - b) / b;
    }
    
    public boolean expectedToteDistance(double a, double b, ProximitySensor right)
    {
    	// Make sure the robot is aligned and the distance is less than 8.5 inches
    	return (calculateToteDistanceError(a, b) < RobotMap.PROXIMITY_SENSOR_ERROR_VALUE && right.getDistance() < 8.5);
    }
    
    public boolean bothProximitySensorsWithinRange(ProximitySensor left, ProximitySensor right)
    {
    	// The sweet spot distance away from the tote
    	double expectedDistanceFromTote = RobotMap.EXPECTED_TOTE_DISTANCE;
    	// Both sensors need to be in range
    	return (left.getDistance() <= expectedDistanceFromTote && right.getDistance() <= expectedDistanceFromTote);
    }
    
    public void driveToAutoZone(boolean forward, double correctDistance)
    {
    	double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
	    double distance = driveEncoder.getDistance();
	    // Drive toward the auto zone
	    if(distance <= correctDistance)
	    {
	    	robotDrive.drive(forward ? RobotMap.AUTONOMOUS_DRIVE_SPEED : -RobotMap.AUTONOMOUS_DRIVE_SPEED, forward ? correction : -correction);
	    }
	    // We are at the correct distance; stop and wait
	    else if(distance <= correctDistance + 5 && distance >= correctDistance - 5)
	    {
	    	robotDrive.drive(0, 0);
	    }
	    // We went too far
	    else if(distance >= correctDistance)
	    {
	    	robotDrive.drive(forward ? RobotMap.AUTONOMOUS_REVERSE_SPEED : -RobotMap.AUTONOMOUS_REVERSE_SPEED, 0);
	    }
	    SmartDashboard.putNumber("[Auto] Encoder Raw Tick Count: ", driveEncoder.getRaw());
	    SmartDashboard.putNumber("[Auto] Encoder Distance: ", driveEncoder.getDistance());
    }
    
    public void strafeToAutoZone(double correctDistance)
    {
    	double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
	    double distance = strafeEncoder.getDistance();
	    // Drive toward the auto zone
	    if(distance <= correctDistance)
	    {
	    	slideJaguar1.set(RobotMap.AUTONOMOUS_DRIVE_SPEED);
	    	slideJaguar2.set(RobotMap.AUTONOMOUS_DRIVE_SPEED);
	    }
	    // We are at the correct distance; stop and wait
	    else if(distance <= correctDistance + 5 && distance >= correctDistance - 5)
	    {
	    	slideJaguar1.set(0);
	    	slideJaguar2.set(0);
	    }
	    // We went too far
	    else if(distance >= correctDistance)
	    {
	    	slideJaguar1.set(RobotMap.AUTONOMOUS_REVERSE_SPEED);
	    	slideJaguar2.set(RobotMap.AUTONOMOUS_REVERSE_SPEED);
	    }
	    SmartDashboard.putNumber("[Auto] Strafe Encoder Raw Tick Count", strafeEncoder.getRaw());
	    SmartDashboard.putNumber("[Auto] Strafe Encoder Distance", strafeEncoder.getDistance());
    
    }
    
    public void autoDriveToTote()
    {
    	double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
	    double leftDistance = proximitySensorLeft.getDistance();
	    double rightDistance = proximitySensorRight.getDistance();
	    // Drive toward the auto zone
	    if(calculateToteDistanceError(leftDistance, rightDistance) <= RobotMap.PROXIMITY_SENSOR_ERROR_VALUE
	    		&& rightDistance <= RobotMap.EXPECTED_TOTE_DISTANCE)
	    {
	    	robotDrive.drive(0, 0);
	    }
	    // We are at the correct distance; stop and wait
	    else
	    {
	    	robotDrive.drive(RobotMap.AUTONOMOUS_DRIVE_SPEED, correction);
	    }
    }
    
    public void getAwayFromTote(boolean forward)
    {
    	double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
	    if(driveEncoder.getDistance() < RobotMap.BACK_AWAY_FROM_TOTE_DISTANCE)
	    {
	    	robotDrive.drive(forward ? RobotMap.BACK_AWAY_FROM_TOTE_SPEED : -RobotMap.BACK_AWAY_FROM_TOTE_SPEED, correction);
	    }
	    else
	    {
	    	robotDrive.drive(0, 0);
	    }
    }
    
    public double getAutoDriveDistanceTraveled()
    {
    	return driveEncoder.getDistance();
    }

}