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
    static private RobotDriveBase _instance = null;
    
	// Instance for Power Distribution Panel
	private PowerDistributionPanel pdp = null;

    // Instances for Talon motor controllers
    private Talon leftTalon = null; 
    private Talon rightTalon = null;
    
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
    	// Create the Power Distribution Panel if it does not exist, simply to clear sticky faults
    	if(pdp == null)
    	{
    		pdp = new PowerDistributionPanel(); //assuming PCM ID = 0
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
        }
        
        // Create slideJaguar2 if it does not exist.
        if(slideJaguar2 == null)
        {
        	slideJaguar2 = new CANJaguar(RobotMap.CANJAGUAR_SLIDE_2_DRIVE_ADDRESS);
        }
        
        // Create the piston to actuate the slide drive if it does not exist.
        if(slideActuatorPiston == null)
        {
        	slideActuatorPiston = new DoubleSolenoid(RobotMap.PISTON_FORWARD_PORT, RobotMap.PISTON_REVERSE_PORT);
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
        	RobotLogger.getInstance().sendToConsole("robotGyro initializing...");
        	robotGyro = new Gyro(RobotMap.DRIVE_GYRO_ANALOG_PORT);
        	RobotLogger.getInstance().sendToConsole("robotGyro initialization complete.");
        }
        
        // Create robotAccelerometer if it does not exist
        if(robotAccelerometer == null)
        {
        	robotAccelerometer = new BuiltInAccelerometer();
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
        
    }

    // Drivebase code for disabled mode should go here.
    public void shutdownRobotDrive()
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

    // Initialization code for autonomous mode should go here.
    public void initAutonomous()
    {
    	driveEncoder.reset();
    	robotGyro.reset();
    }
    
    public void runAutonomous(int mode)
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

    // Initialization code for teleop mode should go here.
    public void initTeleop()
    {
        // Reset throttle and steering values
        throttleValue = 0;
        steeringValue = 0;
        strafingValue = 0;
    	driveEncoder.reset();
    	robotGyro.reset();
    }

    // Periodic code for teleop mode should go here. This method is called ~50x per second.
    public void runTeleop()
    {
        // Get the current throttle value from the Pilot Controller
        throttleValue = PilotController.getInstance().getLeftJoystickYAxis() * RobotMap.THROTTLE_LIMIT;
        
        // Get the current steering value from the Pilot Controller
        steeringValue = PilotController.getInstance().getRightJoystickXAxis() * RobotMap.STEERING_LIMIT;
        
        // Get the current strafing value from the Pilot Controller
        strafingValue = PilotController.getInstance().getLeftJoystickXAxis() * RobotMap.STRAFE_LIMIT;
        // Strafe motor reversal
        strafingValue *= -1; 
        
        // Dump values to dashboard
    	SmartDashboard.getNumber("Throttle: ", throttleValue);
    	SmartDashboard.getNumber("Steering: ", steeringValue);
    	SmartDashboard.getNumber("Strafing: ", strafingValue);

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
        
        // Toggle Strafe Mode
    	if(PilotController.getInstance().getLeftBumper())
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
    	
    	// Drive with Gyro Assist, get Angle
    	double gyroAngle = robotGyro.getAngle();
    	
    	// Handle DRIVING mode
        if(!strafeMode)
        {
        	if(strafeStart) strafeStart = false;
        	
        	// Lift the SLIDE drive and shut off slide motors
        	slideActuatorPiston.set(Value.kReverse);
        	slideJaguar1.set(0);
        	slideJaguar2.set(0);
        	
        	// Check to see if we are not steering and reset the gyro heading 
            if(Math.abs(PilotController.getInstance().getRightJoystickXAxis()) < RobotMap.XBONE_RIGHT_X_DEADBAND)
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
            	// Drive STRAIGHT and use the GYRO to keep us straight
            	double compensatedSteeringValue = gyroAngle * RobotMap.TELEOP_P_CONTROL_VALUE_GYRO * -1;            	
            	//RobotLogger.getInstance().sendToConsole("Gyro Compensation Value: " + compensatedSteeringValue);
            	robotDrive.arcadeDrive(throttleValue, compensatedSteeringValue);
            }
            // Here, the driver is steering and we will NOT USE the gyro
            else
            {
            	robotDrive.arcadeDrive(throttleValue, steeringValue);
            	// When steering, the gyro is always dirty
            	//robotGyro.reset();
//            	dirtyGyro = true;  **** WAIT UNTIL ROBORIO LAYS FLAT
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
        	if(Math.abs(PilotController.getInstance().getRightJoystickXAxis()) < RobotMap.XBONE_RIGHT_X_DEADBAND)
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
            	double compensatedSteeringValue = gyroAngle * RobotMap.TELEOP_STRAFE_P_CONTROL_VALUE_GYRO * -1;            	
            	//RobotLogger.getInstance().sendToConsole("Gyro Compensation Value: " + compensatedSteeringValue);
            	robotDrive.arcadeDrive(throttleValue, compensatedSteeringValue);
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
        SmartDashboard.putNumber("Encoder Distance", driveEncoder.getDistance());
        SmartDashboard.putNumber("Xbone Controller Right Stick X Axis", PilotController.getInstance().getRightJoystickXAxis());
        SmartDashboard.putNumber("Gyro Angle", gyroAngle);
    	SmartDashboard.putNumber("Teleop Turning Correction Value", gyroAngle * RobotMap.TELEOP_STRAFE_P_CONTROL_VALUE_GYRO * -1);
		SmartDashboard.putNumber("ACCEL (X)", robotAccelerometer.getX());
		SmartDashboard.putNumber("ACCEL (Y)", robotAccelerometer.getY());
		SmartDashboard.putNumber("ACCEL (Z)", robotAccelerometer.getZ());
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