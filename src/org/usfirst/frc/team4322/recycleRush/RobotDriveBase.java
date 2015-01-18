/*
 * This singleton class will provide access to the Robot Drivebase.
 * FRC4322 will use the singleton pattern in the 2014 robot code to simplfy access to robot subsystems.
 * The robot will never have more than one instance of any subsystem, so the single pattern fits nicely.
 * http://en.wikipedia.org/wiki/Singleton_pattern
 */
package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
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
    private CANJaguar slideJaguar = null;
    private DoubleSolenoid slideActuatorPiston = null;
    private Compressor compressor = null;

    // Class declarations for the throttle and steering RobotDrive values
    private double throttleValue = 0;
    private double lastThrottleValue = 0;
    
    private double steeringValue = 0;
    private double lastSteeringValue = 0;
    
    private double strafingValue = 0;
    private double lastStrafingValue = 0;
    
    private RobotDrive robotDrive = null;

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
	
	        // Create the slideJaguar if it does not exist.
	        if(slideJaguar == null)
	        {
	        	slideJaguar = new CANJaguar(RobotMap.CANJAGUAR_SLIDE_DRIVE_ADDRESS);
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
	        
	        // Create robotDrive if it does not exist
	        if(robotDrive == null)
	        {
	        	robotDrive = new RobotDrive(leftTalon, rightTalon);
	        	robotDrive.setInvertedMotor(MotorType.kFrontLeft, true);
	        	robotDrive.setInvertedMotor(MotorType.kFrontRight, true);
	        	robotDrive.setInvertedMotor(MotorType.kRearLeft, true);
	        	robotDrive.setInvertedMotor(MotorType.kRearRight, true);
	        }
    	} catch (Exception ex) {
    		RobotLogger.getInstance().writeErrorToFile("initRobotDrive()", ex);
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
	        if(slideJaguar != null)
	        {
	            slideJaguar.set(0);
	        }
    	} catch (Exception ex) {
    		RobotLogger.getInstance().writeErrorToFile("shutdownRobotDrive()", ex);
    	}
    }

    // Initialization code for autonomous mode should go here.
    public void initAutonomous()
    {
    	
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
    	} catch (Exception ex) {
    		RobotLogger.getInstance().writeErrorToFile("RobotDriveBase.initTeleop()", ex);
    	}
    }

    // Periodic code for teleop mode should go here. This method is called ~50x per second.
    public void runTeleop()
    {
    	try
    	{
	        // Get the current throttle value from the Pilot Controller
	        throttleValue = PilotController.getInstance().getLeftJoystickYAxis() * RobotMap.THROTTLE_LIMIT;
	        
	        // Get the current steering value from the Pilot Controller
	        steeringValue = PilotController.getInstance().getRightJoystickXAxis() * RobotMap.STEERING_LIMIT;
	        
	        // Get the current strafing value from the Pilot Controller
	        strafingValue = PilotController.getInstance().getLeftJoystickXAxis() * RobotMap.STRAFE_LIMIT;
	        // Strafe motor reversal
	        strafingValue *= -1; 
	        
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
	        
	        // Driving Actions
	        robotDrive.arcadeDrive(throttleValue, steeringValue);
	        
	        // Holding down the left bumper allows you to drive sideways
	        if(PilotController.getInstance().getLeftBumper())
	        {
	        	slideActuatorPiston.set(Value.kForward);
	        	slideJaguar.set(strafingValue);
	        }
	        else
	        {
	        	slideActuatorPiston.set(Value.kReverse);
	        	slideJaguar.set(0);
	        }
    	} catch (Exception ex) {
    		RobotLogger.getInstance().writeErrorToFile("RobotDriveBase.runTeleop()", ex);
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