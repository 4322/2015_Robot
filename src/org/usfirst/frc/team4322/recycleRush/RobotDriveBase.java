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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
    private SendableChooser driverProfile = null;
   
    // Instances for slide drive
    private CANJaguar slideJaguar1 = null;
    private CANJaguar slideJaguar2 = null;
    private DoubleSolenoid slideActuatorPiston = null;
    private Compressor compressor = null;

    // Instance for Encoder
    public Encoder driveEncoder = null;
    
    // Class declarations for the throttle and steering RobotDrive values
    private double throttleValue = 0;
    private double lastThrottleValue = 0;
    
    private double steeringValue = 0;
    private double lastSteeringValue = 0;
    
    private double strafingValue = 0;
    private double lastStrafingValue = 0;
    
    public RobotDrive robotDrive = null;
    
    // Auton gyro
    public Gyro robotGyro = null;
    private boolean dirtyGyro = false; //true; <-- WAIT UNTIL ROBORIO LAYS FLAT
    
    // Auton Accelerometer
    private BuiltInAccelerometer robotAccelerometer = null;
    
    boolean strafeMode = true; // false; <-- Lets start in strafe mode, driver preference.
    boolean strafeStart = false;
    boolean strafePressed = false;
    boolean duelMode = false;
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
	        
	        // Create driverProfile if it doesnt exist
	        if(driverProfile == null)
	        {
	        	driverProfile = new SendableChooser();
	        	driverProfile.addDefault("Default Profile", false);
	        	driverProfile.addObject("Alternate Profile", true);
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
    		//RobotLogger.getInstance().writeErrorToFile("Exception caught in shutdownRobotDrive()", ex);
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
    		//RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotDriveBase.initAutonomous()", ex);
    	}
    }
    
    public void runAutonomous(int mode)
    {
    	try
    	{
		    double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
		    double distance = driveEncoder.getDistance();
		    if(distance <= RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE)
		    {
		    	robotDrive.drive((mode == 1) ? RobotMap.AUTONOMOUS_DRIVE_SPEED : -RobotMap.AUTONOMOUS_DRIVE_SPEED, correction);
		    }
		    else if(distance <= RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE + 4 && distance >= RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE - 4)
		    {
		    	robotDrive.drive(0, 0);
		    }
		    else if(distance >= RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE + 4)
		    {
		    	robotDrive.drive((mode == 1) ? RobotMap.AUTONOMOUS_REVERSE_SPEED : -RobotMap.AUTONOMOUS_REVERSE_SPEED, 0);
		    }
		    SmartDashboard.putNumber("Encoder Raw Tick Count", driveEncoder.getRaw());
		    SmartDashboard.putNumber("Encoder Distance", driveEncoder.getDistance());
		    SmartDashboard.putNumber("Gyro Angle", robotGyro.getAngle());
		    if(robotGyro.getAngle() < 0.05)
		    {
		       	SmartDashboard.putNumber("Autonomous Turning Correction Value", correction);
		     	SmartDashboard.putString("Autonomous Turning Correction", "Active");
		    }
		    else
		    {
		       	SmartDashboard.putString("Autonomous Turning Correction", "Currently Straight");
		       	SmartDashboard.putNumber("Autonomous Turning Correction Value", 0);
		    }
    	}
    	catch (Exception ex)
    	{
    		//RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotDriveBase.runAutonomous()", ex);
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
    		//PilotController.getInstance().setUseAlternateDriveProfile((boolean)driverProfile.getSelected());
    		duelMode = PilotController.getInstance().getDuelButton();
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

	        // Ramping Drive Values
	        if((Math.abs(throttleValue) - Math.abs(lastThrottleValue)) > RobotMap.THROTTLE_RAMP)
	        {
	        	if(throttleValue > lastThrottleValue)
	        	{
	        		throttleValue = lastThrottleValue + RobotMap.THROTTLE_RAMP;
	        	}
	        	else
	        	{
	        		throttleValue = lastThrottleValue - RobotMap.THROTTLE_RAMP;
	        	}
	        }
	        lastThrottleValue = throttleValue;
	        
	        // Ramping Steering Values
	        if((Math.abs(steeringValue) - Math.abs(lastSteeringValue)) > RobotMap.STEERING_RAMP)
	        {
	        	if(steeringValue > lastSteeringValue)
	        	{
	        		steeringValue = lastSteeringValue + RobotMap.STEERING_RAMP;
	        	}
	        	else
	        	{
	        		steeringValue = lastSteeringValue - RobotMap.STEERING_RAMP;
	        	}
	        }
	        lastSteeringValue = steeringValue;
	        
	        // Ramping Strafing Values
	        if((Math.abs(strafingValue) - Math.abs(lastStrafingValue)) > RobotMap.STRAFE_RAMP)
	        {
	        	if(strafingValue > lastStrafingValue)
	        	{
	        		strafingValue = lastStrafingValue + RobotMap.STRAFE_RAMP;
	        	}
	        	else
	        	{
	        		strafingValue = lastStrafingValue - RobotMap.STRAFE_RAMP;
	        	}
	        }
	        lastStrafingValue = strafingValue;
	        //Scale Values
	        throttleValue = (throttleValue * RobotMap.THROTTLE_LIMIT)  / (duelMode ? 2 : 1);
	        steeringValue  = (steeringValue * RobotMap.STEERING_LIMIT) / (duelMode ? 2 : 1);
	        strafingValue = (strafingValue * RobotMap.STRAFE_LIMIT) / (duelMode ? 1.625 : 1);
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
	    	
	    	// Handle DRIVING mode
	        if(!strafeMode)
	        {
	        	if(strafeStart) strafeStart = false;
	        	
	        	// Lift the SLIDE drive and shut off slide motors
	        	slideActuatorPiston.set(Value.kReverse);
	        	slideJaguar1.set(0);
	        	slideJaguar2.set(0);
	        	
	        	// Check to see if we are not steering and reset the gyro heading 
	            if(Math.abs(PilotController.getInstance().getDriveBaseSteeringStick()) < RobotMap.STEERING_DEADBAND)
	            {
	            	// If the Gyro is dirty, and we have stopped turning
	            	if(dirtyGyro && Math.abs(robotAccelerometer.getX()) < RobotMap.ACCELEROMETER_DEADBAND_X
	            			     && Math.abs(robotAccelerometer.getY()) < RobotMap.ACCELEROMETER_DEADBAND_Y)
	                {
	            		// Reset the Gyro ONLY ONCE per dirty
	                	robotGyro.reset();
	                	dirtyGyro = false;
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
//		            	RobotLogger.getInstance().sendToConsole("Gyro Compensation Value: " + compensatedSteeringValue);
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
	            	 //When steering, the gyro is always dirty
	            	robotGyro.reset();
	            	dirtyGyro = true;
	            }
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
	        	// Check to see if we are not steering and reset the gyro heading 
	        	if(Math.abs(PilotController.getInstance().getDriveBaseSteeringStick()) < RobotMap.STEERING_DEADBAND)
	        	{
	            	// If the Gyro is dirty, and we have stopped turning
	        		if(dirtyGyro && Math.abs(robotAccelerometer.getX()) < RobotMap.ACCELEROMETER_DEADBAND_X
	        				     && Math.abs(robotAccelerometer.getY()) < RobotMap.ACCELEROMETER_DEADBAND_Y)
	                {
	        			RobotLogger.getInstance().sendToConsole("ACCELEROMETER WITHIN DEADBAND");
	            		// Reset the Gyro ONLY ONCE per dirty 
	                	robotGyro.reset();
	                	dirtyGyro = false;
	                }
	        		// If the Gyro is dirty, no auto-correcting
	            	if(dirtyGyro)
	            	{
	            		gyroAngle = 0;
	            	}
	    	           	        
	            	double compensatedSteeringValue = gyroAngle * RobotMap.TELEOP_STRAFE_P_CONTROL_VALUE_GYRO * -1;            	
	            	robotDrive.arcadeDrive(throttleValue, compensatedSteeringValue);
	        	}
	        	else if(Math.abs(PilotController.getInstance().getDriveBaseStrafingStick()) < RobotMap.STEERING_DEADBAND && Math.abs(PilotController.getInstance().getDriveBaseThrottleStick()) < RobotMap.THROTTLE_DEADBAND)
	        	{
	        		robotDrive.arcadeDrive(throttleValue, steeringValue);
	            	// When steering, the gyro is always dirty
	        		robotGyro.reset();
	            	dirtyGyro = true;
	        	}
	        	else
	        	{
	        		robotDrive.arcadeDrive(throttleValue, steeringValue);
	            	// When steering, the gyro is always dirty
	        		robotGyro.reset();
	            	dirtyGyro = true;
	            }
	        	slideActuatorPiston.set(Value.kForward);
	        	slideJaguar1.set(strafingValue*-1);
	        	slideJaguar2.set(strafingValue*-1);
	        }

//	        SmartDashboard.putNumber("Encoder Distance", driveEncoder.getDistance());
//	        SmartDashboard.putNumber("Steering actual value:", PilotController.getInstance().getDriveBaseSteeringStick());
//	        SmartDashboard.putNumber("Throttle actual value:", PilotController.getInstance().getDriveBaseThrottleStick());
	        SmartDashboard.putNumber("Gyro Angle", gyroAngle);
//	    	SmartDashboard.putNumber("Teleop Turning Correction Value", gyroAngle * RobotMap.TELEOP_STRAFE_P_CONTROL_VALUE_GYRO * -1);
			SmartDashboard.putNumber("ACCEL (X)", robotAccelerometer.getX());
			SmartDashboard.putNumber("ACCEL (Y)", robotAccelerometer.getY());
			SmartDashboard.putNumber("ACCEL (Z)", robotAccelerometer.getZ());
//			SmartDashboard.putBoolean("Pressure Low:", compressor.getPressureSwitchValue());
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

}