/*
2 * This singleton class will provide access to the Robot Drivebase.
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
    public ProximitySensor proximitySensorLeft = null;
    public ProximitySensor proximitySensorRight = null;
    
    // Class declarations for the throttle and steering RobotDrive values
    private double throttleValue = 0;
    private double lastThrottleValue = 0;
    
    private double steeringValue = 0;
    private double lastSteeringValue = 0;
    
    private double strafingValue = 0;
    private double lastStrafingValue = 0;
    
    // Gyro PID values
    private double gyroAngle = 0;
    private double steeringErrorSum = 0;
    private double steeringErrorDifference = 0;

    private double compensatedSteeringValue = 0;
    private Timer integralTimer = null;
    private double lastGyroAngle = 0;
    
    
    // Autonomous Gyro
    public Gyro robotGyro = null;
    private boolean dirtyGyro = true;
    
    // Autonomous Accelerometer
    private BuiltInAccelerometer robotAccelerometer = null;
    private int accelerometerDeadbandCount = 10;
    
    boolean strafeMode = true; // true; <-- Lets start in strafe mode, driver preference.
    boolean strafeStart = false;
    boolean strafePressed = false;
    
    // Auto alignment mode
    private boolean autoAlignButtonPressed = false;
	private double currentAutoAlignSpeed = 0.0;
	private double rightEncoderCount = 0.0;
	private double leftEncoderCount = 0.0;
	private double targetCenterEncoderCount = 0.0;
	private int driveDelay = 50;
	private int pickupDelay = 25;
	
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
	        if(integralTimer == null)
	        {
	        	integralTimer = new Timer();
	        	integralTimer.start();
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
    
    public void disabledPeriodic()
    {
//    	proximitySensorLeft.feedSensorBuffer();
//    	proximitySensorRight.feedSensorBuffer();
//    	SmartDashboard.putNumber("Left Proximity Sensor Voltage:",proximitySensorLeft.GetVoltage());
//		SmartDashboard.putNumber("Left Proximity Sensor Distance:",proximitySensorLeft.getFilteredDistance());
//		SmartDashboard.putNumber("Right Proximity Sensor Voltage:",proximitySensorRight.GetVoltage());
//		SmartDashboard.putNumber("Right Proximity Sensor Distance:",proximitySensorRight.getFilteredDistance());
    }
    
    // Initialization code for autonomous mode should go here.
    public void initAutonomous()
    {
    	try
    	{
	    	driveEncoder.reset();
	    	robotGyro.reset();
    		strafeEncoder.reset();
    		slideJaguar1.enableControl();
    		slideJaguar2.enableControl();
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotDriveBase.initAutonomous()", ex);
    	}
    }
    
    // Initialization code for teleop mode should go here.
    public void initTeleop()
    {
    	try
    	{
    		RobotLogger.getInstance().sendToConsole("Strafe Encoder Distance after auto was %f inches.", driveEncoder.getDistance());
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
    		//feed both distance sensors
        	proximitySensorLeft.feedSensorBuffer();
        	proximitySensorRight.feedSensorBuffer();
        	
   
    		
    		// You must press and hold the button to auto align with the tote
	        if(PilotController.getInstance().getAutoAlignButton()
	        || PilotController.getInstance().getQuickAutoAlignButton()
	        || PilotController.getInstance().getDriveAndAutoLiftButton()
	        || CoPilotController.getInstance().getDriveAndAutoLiftButton())
	        {
	    		if(!autoAlignButtonPressed)
	    		{
		        	if(!autoAlignButtonPressed) RobotLogger.getInstance().sendToConsole("*****AUTO ALIGNMENT BEGIN*****");
	//        		dirtyAngularAlignmentCount = 0;  // WE DECIDED TO NOT USE THIS
		        	autoAlignButtonPressed = true;
		        	// Choose to skip modes
		        	if(toteAlignmentMode == AUTO_ALIGN_COMPLETE)
		        	{
			        	if(PilotController.getInstance().getAutoAlignButton())
			        	{
			        		// Normal
			        		toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
			        	}
			        	else if(PilotController.getInstance().getDriveAndAutoLiftButton() || CoPilotController.getInstance().getDriveAndAutoLiftButton())
			        	{
			        		// Already align, just drive forward and lift
			        		toteAlignmentMode = DRIVE_FORWARD_TO_TOTE;
			        		driveDelay = 0x32; //HEX FTW!!!!
			        		pickupDelay = 0b11001; //BIN FTW!!!
			        	}
			        	else
			        	{
			        		// Skip to angular alignment
	//		        		dirtyAngularAlignmentCount++;
			        		toteAlignmentMode = ANGULAR_ALIGNMENT_WITH_TOTE;
			        	}
		        	}
		        }
	        }
	        // If we are not holding down the button, the driver may drive
	        else
	        {
	        	// If we just changed from auto align to normal drive
	        	if(autoAlignButtonPressed)
	        	{
	        		dirtyGyro = false;
	        		robotGyro.reset();
	        	}
	        	autoAlignButtonPressed = false;
	        	toteAlignmentMode = AUTO_ALIGN_COMPLETE;
	        }
	        
	        // Get Proximity Sensor Values for Left and Right
    		double toteDistanceRight = proximitySensorRight.getFilteredDistance();
	        double toteDistanceLeft = proximitySensorLeft.getFilteredDistance();
    		double toteInstantDistanceRight = proximitySensorRight.getInstantDistance();
	        double toteInstantDistanceLeft = proximitySensorLeft.getInstantDistance();
	        
	        // Driving
    		if(!autoAlignButtonPressed)
    		{
		        // Get the current throttle value from the Pilot Controller
		        throttleValue = getThrottleStick();
		        
		        // Get the current steering value from the Pilot Controller
		        steeringValue = getSteeringStick();
		        
		        // Get the current strafing value from the Pilot Controller
		        strafingValue = getStrafingStick();
		        
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
		        boolean dualRateEnabled = (!PilotController.getInstance().isPilotDriving) ? true : PilotController.getInstance().getDualRateButton();
		        
		        throttleValue = (throttleValue * RobotMap.THROTTLE_LIMIT)  / (dualRateEnabled ? RobotMap.THROTTLE_DUAL_RATE : 1);
		        steeringValue  = (steeringValue * RobotMap.STEERING_LIMIT) / (dualRateEnabled ? RobotMap.STEERING_DUAL_RATE : 1);
		        strafingValue = (strafingValue * RobotMap.STRAFE_LIMIT) / (dualRateEnabled ? RobotMap.STRAFE_DUAL_RATE : 1);
		        
		        // Toggle Strafe Mode
		    	if(getSlideDriveLiftButton())
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
		    	
		    	// get gyro and set to gyroAngle
		    	// this should be the only place we read the gyro
		    	try
		    	{
			    	gyroAngle = robotGyro.getAngle();
		    	}
		    	catch (Exception ex)
		    	{
		    		RobotLogger.getInstance().writeErrorToFile("Exception caught in runTeleop() robotGyro.getAngle()", ex);
		    	}
		    	
	        	//Calculate steering compensation for current cycle
		    	double integralTime =  integralTimer.get() / 1000;
		    	steeringErrorSum =+ gyroAngle * integralTime ;
                steeringErrorDifference = gyroAngle - lastGyroAngle;
                integralTimer.reset();
                
		    	compensatedSteeringValue =  gyroAngle * RobotMap.TELEOP_P_CONTROL_VALUE_GYRO + steeringErrorSum * RobotMap.TELEOP_I_CONTROL_VALUE_GYRO - steeringErrorDifference*RobotMap.TELEOP_D_CONTROL_VALUE_GYRO;
                
                
                //remember gyro angle for next cycle
                lastGyroAngle = gyroAngle;

		        SmartDashboard.putNumber("[Gyro] Steering Error Sum", steeringErrorSum);
		        SmartDashboard.putNumber("[Gyro] Steering Error Difference", steeringErrorDifference);
		        SmartDashboard.putNumber("[Gryo] Compensated Steering Value", compensatedSteeringValue);
		    	
	        	//get X axis of accelerometer
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
		           		if(Math.abs(accelerometerXAxis) < (compressor.enabled() ? RobotMap.ACCELEROMETER_DEADBAND_X_COMPRESSOR : RobotMap.ACCELEROMETER_DEADBAND_X))
		           		{
		           			if(accelerometerDeadbandCount <= 0)
		           			{
		    	           		// Reset the Gyro ONLY ONCE per dirty
		    	               	robotGyro.reset();
		    	               	integralTimer.reset();
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
		            	steeringErrorSum = 0;
		            	integralTimer.reset();
		            }
	    	        try
	            	{

	            		// Drive STRAIGHT and use the GYRO to keep us straight
     	
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
		        SmartDashboard.putNumber("[Drive] Encoder Distance", driveEncoder.getDistance());
		        SmartDashboard.putNumber("[Drive] Encoder Raw Ticks", driveEncoder.getRaw());
		        SmartDashboard.putNumber("[Strafe] Distance", strafeEncoder.getDistance());
		        SmartDashboard.putNumber("[Strafe] Distance (RAW)", strafeEncoder.getRaw());
		        SmartDashboard.putNumber("Gyro Angle", /*dirtyGyro ? 999999 : */gyroAngle);
//				SmartDashboard.putNumber("ACCEL (X)", robotAccelerometer.getX());
//				SmartDashboard.putBoolean("ACCEL Deadband", (Math.abs(accelerometerXAxis) < (compressor.enabled() ? RobotMap.ACCELEROMETER_DEADBAND_X_COMPRESSOR : RobotMap.ACCELEROMETER_DEADBAND_X)));
//				SmartDashboard.putBoolean("Strafing On", (Math.abs(strafingValue) > 0) ? true : false);
				
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
					        if(toteInstantDistanceRight > RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
					        {
					        	RobotLogger.getInstance().sendToConsole("(INITIALIZE_AUTO_ALIGNMENT) Robot too far to the RIGHT of tote.");
					        	// Move left
					        	currentAutoAlignSpeed = RobotMap.AUTO_ALIGN_STRAFE_SPEED * -1;
				        		toteAlignmentMode = FIND_TOTE_EDGES;
					        }
					        
					        // If the right sensor detects tote, but the left one doesn't
					        else if (toteInstantDistanceLeft > RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
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
			        		if(toteInstantDistanceLeft <= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        		{
			        			slideJaguar1.set(RobotMap.AUTO_ALIGN_STRAFE_SPEED*-1 );
			        			slideJaguar2.set(RobotMap.AUTO_ALIGN_STRAFE_SPEED*-1 );
			            		// Strafe STRAIGHT and use the GYRO to keep us straight
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
			        			if(leftEncoderCount == 0 && toteInstantDistanceLeft <= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        			{
			        				leftEncoderCount = strafeEncoder.getRaw();
						        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) LEFT Tote Edge Found! Encoder count: " + leftEncoderCount);
			        			}
			        			else if (rightEncoderCount == 0 && toteInstantDistanceRight >= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        			{
			        				rightEncoderCount = strafeEncoder.getRaw();
						        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) RIGHT Tote Edge Found! Encoder count: " + rightEncoderCount);
			        			}
			        		}
			        		
			        		else
			        		{
			        			if(leftEncoderCount == 0 && toteInstantDistanceLeft >= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
			        			{
			        				leftEncoderCount = strafeEncoder.getRaw();
						        	RobotLogger.getInstance().sendToConsole("(FIND_TOTE_EDGES) LEFT Tote Edge Found! Encoder count: " + leftEncoderCount);
			        			}
			        			else if (rightEncoderCount == 0 && toteInstantDistanceRight <= RobotMap.MAX_EXPECTED_TOTE_DISTANCE)
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
					            robotDrive.arcadeDrive(0, compensatedSteeringValue);
			        		}	
			        		break;
			        		
			        		
			        	case STRAFE_TO_TOTE_CENTER:
			        		// If we moved right, and have reached our position
			        		if((currentAutoAlignSpeed > 0 && strafeEncoder.getRaw() >= targetCenterEncoderCount)
			        		|| (currentAutoAlignSpeed < 0 && strafeEncoder.getRaw() <= targetCenterEncoderCount))
			        		{
					        	RobotLogger.getInstance().sendToConsole("(STRAFE_TO_TOTE_CENTER) WE HAVE BEEN CENTERED ON THE TOTE! Theoretically...");
					        	RobotLogger.getInstance().sendToConsole("(STRAFE_TO_TOTE_CENTER) End Encoder Count = " + strafeEncoder.getRaw());
			        			slideJaguar1.set(0);
				        		slideJaguar2.set(0);
				        		// DO NOT PROCEED TO DRIVE FORWARD, INSTEAD STOP THE STATE ENGINE (04/18/15)					        	
//				        		toteAlignmentMode = ANGULAR_ALIGNMENT_WITH_TOTE;
					        	RobotLogger.getInstance().sendToConsole("(STRAFE_TO_TOTE_CENTER) Tote centering complete.");
			        			toteAlignmentMode = AUTO_ALIGN_COMPLETE;
			        			driveDelay = 0;
			        		}
				        	// We have not reached the position
				        	else
				        	{
				        		// Keep driving until we have reached the center
				        		slideJaguar1.set(currentAutoAlignSpeed);
				        		slideJaguar2.set(currentAutoAlignSpeed);
			            		// Strafe STRAIGHT and use the GYRO to keep us straight
					            robotDrive.arcadeDrive(0, compensatedSteeringValue);
				        	}
			        		break;
			        	case ANGULAR_ALIGNMENT_WITH_TOTE:
			        		// Try to get the left and right tote distances within 1/2 inch of each other
//			        		double angularAlignment = (Math.PI/2) - Math.atan((toteDistanceLeft - toteDistanceRight)/RobotMap.INTER_SENSOR_DISTANCE); // Lose the trig. (04/18/15) 
//			        		angularAlignment = Math.min(angularAlignment, 5);
//			        		if(angularAlignment < RobotMap.ANGULAR_ALIGN_MAX_ERROR)
			        		double angularAlignment = Math.abs(toteDistanceLeft - toteDistanceRight); // Go back to the simple method (04/18/15)
			        		if(angularAlignment < .5)
			        		{
			        			// We have achieved angular alignment
					            robotDrive.arcadeDrive(0, 0);
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) WE HAVE ACHIEVED ANGLUAR ALIGNMENT WITH THE TOTE!");
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Within Angular Alignment = " + angularAlignment);
					        	// Look to see if we want to redo the lateral alignment, only once though
					        	// commented out to not laterally align after angular
//					        	if(dirtyAngularAlignmentCount == 1)
//					        	{
//						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Dirty Alignment requires starting over.");
//				        			toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
//					        	}
					        	//else
					        //	{
// DO NOT PROCEED TO DRIVE FORWARD, INSTEAD STOP THE STATE ENGINE (04/18/15)					        	
//						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Driving Forward to Tote.");
//				        			toteAlignmentMode = DRIVE_FORWARD_TO_TOTE;
						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Angular alignment complete.");
				        			toteAlignmentMode = AUTO_ALIGN_COMPLETE;
				        			driveDelay = 0;
//					        	}	
			        		}
			        		else
			        		{
					        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Out of Angular Alignment = " + angularAlignment);
					        	//dirtyAngularAlignmentCount++;
			        			// Determine which way to turn
					        	
			        			if(toteDistanceLeft < toteDistanceRight)
			        			{
			        				
			        				// Turn left (Negative Steering Value)
						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Turning Left...");
//						            robotDrive.arcadeDrive(0, -1 * Math.min(.5, RobotMap.AUTO_ALIGN_ANGULAR_P * angularAlignment));  // Lose the trig. (04/18/15)
						            robotDrive.arcadeDrive(0, RobotMap.AUTO_ALIGN_ANGULAR_POWER * -1); // Go back to the simple method (04/18/15)
			        			}
			        			else
			        			{
			        				// Turn right (Positive Steering Value)
						        	RobotLogger.getInstance().sendToConsole("(ANGULAR_ALIGNMENT_WITH_TOTE) Turning Right...");
//						            robotDrive.arcadeDrive(0, Math.min(.5, RobotMap.AUTO_ALIGN_ANGULAR_P * angularAlignment));  // Lose the trig. (04/18/15)
						            robotDrive.arcadeDrive(0, RobotMap.AUTO_ALIGN_ANGULAR_POWER); // Go back to the simple method (04/18/15)
			        			}
			        		}
			        		break;
			        	case DRIVE_FORWARD_TO_TOTE:
			    			// Once the robot is within reach, switch to next mode
				        	RobotLogger.getInstance().sendToConsole("(DRIVE_FORWARD_TO_TOTE) Left Tote Distance  = " + toteDistanceLeft);
				        	RobotLogger.getInstance().sendToConsole("(DRIVE_FORWARD_TO_TOTE) Right Tote Distance = " + toteDistanceRight);
				        	if(pickupDelay == 50 &&(toteDistanceLeft + toteDistanceRight) / 2 <= RobotMap.EXPECTED_TOTE_DISTANCE)
							{
				        		pickupDelay -=1;
							}
			    			else
			    			{
			    				if(pickupDelay == 0)
				        		{
				        			RobotLogger.getInstance().sendToConsole("(DRIVE_FORWARD_TO_TOTE) WE HAVE ACHIEVED LIFTING DISTANCE!");
				        			// Stop driving
//					        	    robotDrive.arcadeDrive(0, 0);
				        			// Begin tote lift
				        			RobotToteElevator.getInstance().startAutoLift();
				        			RobotLogger.getInstance().sendToConsole("(DRIVE_FORWARD_TO_TOTE) AUTO LIFT STARTED!");
					        		driveDelay = 100; // CYCLE
				        			toteAlignmentMode = AUTO_ALIGN_COMPLETE;
				        		}
				        		else
				        		{
				        			pickupDelay--;
				        		}
				        			// Drive Forward (Negative Value)
					            robotDrive.arcadeDrive(RobotMap.AUTO_ALIGN_DRIVE_FORWARD_SPEED, 0);
			    			}
							break;
			        	case AUTO_ALIGN_COMPLETE:
			        		if(driveDelay != 0)
			        		{
			        			robotDrive.arcadeDrive(RobotMap.AUTO_ALIGN_DRIVE_FORWARD_SPEED, 0);	
			        			driveDelay -= 1;
			        		}
			        		else
			        		{
			        			RobotLogger.getInstance().sendToConsole("(AUTO_ALIGN_COMPLETE) AUTO ALIGN IS FINISHED.");
			        			robotDrive.arcadeDrive(0,0);
			        		}
			        		break;
			        	default:
			        		// We've pressed the button, but don't know what mode we're in?
			        		toteAlignmentMode = INITIALIZE_AUTO_ALIGNMENT;
			        		break;
			    	}
			    }
		        else 
		        {
		        	RobotLogger.getInstance().sendToConsole("(AUTO-ALIGN) NO TOTE IN RANGE! (" + toteDistanceLeft + ", " + toteDistanceRight + ")");
		        }
			}
    		SmartDashboard.putNumber("Left Proximity Sensor Voltage:",proximitySensorLeft.GetVoltage());
    		SmartDashboard.putNumber("Left Proximity Sensor Distance:",proximitySensorLeft.getFilteredDistance());
    		SmartDashboard.putNumber("Right Proximity Sensor Voltage:",proximitySensorRight.GetVoltage());
    		SmartDashboard.putNumber("Right Proximity Sensor Distance:",proximitySensorRight.getFilteredDistance());
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
    
    public double getThrottleStick()
    {
    	if(PilotController.getInstance().isPilotDriving)
    	{
    		return PilotController.getInstance().getDriveBaseThrottleStick();
    	}
    	else
    	{
    		return CoPilotController.getInstance().getDriveBaseThrottleStick();
    	}
    }
    
    public double getSteeringStick()
    {
    	if(PilotController.getInstance().isPilotDriving)
    	{
    		return PilotController.getInstance().getDriveBaseSteeringStick();
    	}
    	else
    	{
    		return CoPilotController.getInstance().getDriveBaseSteeringStick();
    	}
    }

    public double getStrafingStick()
    {
    	if(PilotController.getInstance().isPilotDriving)
    	{
    		return PilotController.getInstance().getDriveBaseStrafingStick();
    	}
    	else
    	{
    		return CoPilotController.getInstance().getDriveBaseStrafingStick();
    	}
    }
    
    public boolean getSlideDriveLiftButton()
    {
    	if(PilotController.getInstance().isPilotDriving)
    	{
    		return PilotController.getInstance().getSlideDriveLiftButton();
    	}
    	else
    	{
    		return CoPilotController.getInstance().getSlideDriveLiftButton();
    	}
    }

    public double calculateToteDistanceError(double a, double b)
    {
    	// Returns the percent error of the two distances
    	return (a - b) / b;
    }
    
    public boolean expectedToteDistance(double a, double b, ProximitySensor right)
    {
    	// Make sure the robot is aligned and the distance is less than 8.5 inches
    	return (calculateToteDistanceError(a, b) < RobotMap.PROXIMITY_SENSOR_ERROR_VALUE && right.getFilteredDistance() < RobotMap.EXPECTED_TOTE_DISTANCE);
    }
    
    public boolean bothProximitySensorsWithinRange(ProximitySensor left, ProximitySensor right)
    {
    	// The sweet spot distance away from the tote
    	double expectedDistanceFromTote = RobotMap.EXPECTED_TOTE_DISTANCE;
    	// Both sensors need to be in range
    	return (left.getFilteredDistance() <= expectedDistanceFromTote && right.getFilteredDistance() <= expectedDistanceFromTote);
    }
    
    public void driveToAutoZone(boolean forward, double correctDistance)
    {
    	double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
	    double distance = Math.abs(driveEncoder.getDistance());
	    // Drive toward the auto zone
	    if(distance <= correctDistance)
	    {
	    	RobotLogger.getInstance().sendToConsole("Driving " + (forward ? "forward " : "backward " + "towards auto zone."));
	    	slideActuatorPiston.set(Value.kReverse);
	    	robotDrive.drive(forward ? RobotMap.AUTONOMOUS_DRIVE_SPEED : -RobotMap.AUTONOMOUS_DRIVE_SPEED, forward ? correction : -correction);
	    }
	    // We are at the correct distance; stop and wait
	    else if(distance <= correctDistance + 5 && distance >= correctDistance - 5)
	    {
	    	RobotLogger.getInstance().sendToConsole("Arrived in auto zone. Distance driven: " + distance + " inches.");
	    	slideActuatorPiston.set(Value.kForward);
	    	robotDrive.drive(0, 0);
	    }
	    // We went too far
	    else if(distance >= correctDistance)
	    {
	    	RobotLogger.getInstance().sendToConsole("Too far. Driving " + (!forward ? "forward " : "backward " + "towards auto zone."));
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
	    	RobotLogger.getInstance().sendToConsole("Strafing towards auto zone.");
	    	robotDrive.drive(0, correction);
	    	slideJaguar1.set(RobotMap.AUTONOMOUS_DRIVE_SPEED);
	    	slideJaguar2.set(RobotMap.AUTONOMOUS_DRIVE_SPEED);
	    }
	    // We are at the correct distance; stop and wait
	    else if(distance <= correctDistance + 5 && distance >= correctDistance - 5)
	    {
	    	RobotLogger.getInstance().sendToConsole("Arrived in auto zone. Distance driven: " + distance + " inches.");
	    	slideJaguar1.set(0);
	    	slideJaguar2.set(0);
	    }
	    // We went too far
	    else if(distance >= correctDistance)
	    {
	    	RobotLogger.getInstance().sendToConsole("Too far. Strafing other way towards auto zone.");
	    	slideJaguar1.set(RobotMap.AUTONOMOUS_REVERSE_SPEED);
	    	slideJaguar2.set(RobotMap.AUTONOMOUS_REVERSE_SPEED);
	    }
	    SmartDashboard.putNumber("[Auto] Strafe Encoder Raw Tick Count", strafeEncoder.getRaw());
	    SmartDashboard.putNumber("[Auto] Strafe Encoder Distance", strafeEncoder.getDistance());
    
    }
    
    public void autoDriveToTote()
    {
    	double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
	    double leftDistance = proximitySensorLeft.getFilteredDistance();
	    double rightDistance = proximitySensorRight.getFilteredDistance();
	    // We are at the correct distance; stop
	    if(calculateToteDistanceError(leftDistance, rightDistance) <= RobotMap.PROXIMITY_SENSOR_ERROR_VALUE
	    		&& rightDistance <= RobotMap.EXPECTED_TOTE_DISTANCE)
	    {
	    	RobotLogger.getInstance().sendToConsole("The left & right sensors have detected a tote.\n Left Distance: " + leftDistance + ", Right Distance:" + rightDistance);
	    	robotDrive.drive(0, 0);
	    }
	    // Drive toward the tote
	    else
	    {
	    	RobotLogger.getInstance().sendToConsole("Driving toward the tote");
	    	robotDrive.drive(-RobotMap.AUTONOMOUS_DRIVE_SPEED, -correction);
	    }
    }
    
    public void getAwayFromTote(boolean forward)
    {
    	double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
    	// We cannot be in contact with a tote / container at the end of auto mode
	    if(Math.abs(driveEncoder.getDistance()) < RobotMap.BACK_AWAY_FROM_TOTE_DISTANCE) 
	    {
	    	RobotLogger.getInstance().sendToConsole("Getting away from the tote.");
	    	robotDrive.drive(forward ? RobotMap.BACK_AWAY_FROM_TOTE_SPEED : -RobotMap.BACK_AWAY_FROM_TOTE_SPEED, forward ? -correction : correction);
	    }
	    else
	    {
	    	RobotLogger.getInstance().sendToConsole("AUTO MODE DONE! Distance driven: " + driveEncoder.getDistance());
	    	robotDrive.drive(0, 0);
	    }
    }
    
    public double getAutoDriveDistanceTraveled()
    {
    	return Math.abs(driveEncoder.getDistance());
    }

}