package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author FRC4322
 */

public class RobotToteElevator {

	// Instance for the Singleton Class
    static private RobotToteElevator _instance = null;
    
    // Instance for tote lifting motor.
    //private CANJaguar toteMotor = null;
    private CANTalon toteMotor = null;
    
    //Instance for Brake Solenoid
    private DoubleSolenoid brakeSolenoid = null;
    
	boolean pressed = false;
	boolean auto = false;
	boolean switchPressed = false;
	boolean controlModeV = true; 
	int currentSetpoint = 0;
	
	// Instance for Tilt Solenoid
	private DoubleSolenoid tiltActuatorPiston = null;
    boolean tiltPressed = false;
    boolean tiltMode = true; // false; <-- Lets start in elevator not tilted mode
    
    // This is the static getInstance() method that provides easy access to the RobotToteElevator singleton class.
    public static RobotToteElevator getInstance()
    {
        // Look to see if the instance has already been created...
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotToteElevator();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }
    
    // Main elevator initialization code should go here.
    public void initRobotToteElevator()
    {
    	try
	    {
	    	// Create toteMotor
	    	if(toteMotor == null)
	    	{
	    		
	    		toteMotor = new CANTalon(RobotMap.TOTE_ELEVATOR_CONTROLLER_ADDRESS);
	    		toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	    		toteMotor.setPID( RobotMap.ELEVATOR_P_VALUE, RobotMap.ELEVATOR_I_VALUE,RobotMap.ELEVATOR_D_VALUE, RobotMap.ELEVATOR_F_VALUE,RobotMap.ELEVATOR_IZONE_VALUE, RobotMap.ELEVATOR_RAMPRATE_VALUE, 0);
	    		toteMotor.ClearIaccum();
	    		toteMotor.enableControl();
	    		RobotLogger.getInstance().sendToConsole("Elevator TalonSRX Firmware Version: " + toteMotor.GetFirmwareVersion());
	    	}
	    	if(brakeSolenoid == null)
	    	{
	    		brakeSolenoid = new DoubleSolenoid(RobotMap.BRAKE_PISTON_FORWARD_PORT,RobotMap.BRAKE_PISTON_REVERSE_PORT);
	    		// Value.kForward <-- BRAKE RELEASED
	    		// Value.kReverse <-- BRAKE ENGAGED
	    	}
	        // Create the piston to actuate the slide drive if it does not exist.
	        if(tiltActuatorPiston == null)
	        {
	        	tiltActuatorPiston = new DoubleSolenoid(RobotMap.TILT_PISTON_FORWARD_PORT, RobotMap.TILT_PISTON_REVERSE_PORT);
	        }
	        RobotLogger.getInstance().sendToConsole("Successfully started initRobotToteElevator().");
	    }
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in initRobotToteElevator()", ex);
    	}
    }
    
    // Tote elevator code for disabled mode should go here.
    public void shutdownRobotToteElevator()
    {
    	
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
	    	brakeSolenoid.set(Value.kReverse);
	    	toteMotor.clearStickyFaults();
	    	toteMotor.ClearIaccum();
	    	toteMotor.setPosition(0);
	    	auto = false;
	    	tiltMode = false;
	    	RobotLogger.getInstance().sendToConsole("RobotToteElevator.initTeleop() successfully run.");
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotToteElevator.initTeleop()", ex);
    	}
    }

    // Periodic code for teleop mode should go here. This method is called ~50x per second.
    public void runTeleop()
    {
    	try
    	{
    		// Check if the switch is pressed
    		if(!switchPressed)
        	{
    			// Switch to PID mode
        		if(CoPilotController.getInstance().getStartButton())
        		{
        			// Toggle PID on/off
        			auto = !auto;
        			switchPressed = true;
        		}
        	}
        	else
        	{
        		if(!CoPilotController.getInstance().getStartButton())
        		{
        			switchPressed = false;
        		}
        	}
    		
	        // Toggle Tilt Mode
	    	if(CoPilotController.getInstance().getLeftBumper())
	    	{
	    		if(!tiltPressed)
	    		{
	        		tiltMode = !tiltMode;
	        		tiltPressed = true;
	    		}
	    	}
	    	else
	    	{
	    		tiltPressed = false;
	    	}
	    	// Tilt the Piston
	    	if (tiltMode)
	    	{
	    		tiltActuatorPiston.set(Value.kReverse);
	    	}
	    	else
	    	{
	    		tiltActuatorPiston.set(Value.kForward);
	    	}
	    	
    		// PID Control
        	if(auto)
        	{
        		// Switch from voltage control (manual) to position control (automatic)
        		if(controlModeV)
        		{
        			// Disable voltage control
        			toteMotor.disableControl();
        			// Change to position mode
            		toteMotor.changeControlMode(ControlMode.Position);
            		toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            		toteMotor.enableControl();
            		controlModeV = false;
        		}
        		// Run PID control
        		autoDrive();
        	}
        	else
        	{
        		// Switch from position to voltage control
        		if(!controlModeV)
        		{
        			// Disable position control
        			toteMotor.disableControl();
        			// Change to voltage mode
        			toteMotor.changeControlMode(ControlMode.PercentVbus);
        			toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        			toteMotor.enableControl();
        			controlModeV = true;
        		}
        		toteMotor.set(-CoPilotController.getInstance().getLeftJoystickYAxis());
        		// Default state of the disk brake
        		if(Math.abs(CoPilotController.getInstance().getLeftJoystickYAxis()) < RobotMap.ELEVATOR_JOYSTICK_DEADBAND)
        		{
        			brakeSolenoid.set(Value.kReverse);
        		}
        		// Open the disk brake so we can move the elevator
        		else
        		{
        			brakeSolenoid.set(Value.kForward);
        		}
        	}
        	SmartDashboard.putBoolean("Auto Mode:", auto);
        	SmartDashboard.putNumber("Target Value:",toteMotor.getSetpoint());
        	SmartDashboard.putNumber("Error Value:",toteMotor.getClosedLoopError());
        	SmartDashboard.putBoolean("Tote Lift Forward Limit Closed:", toteMotor.isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tote Lift Reverse Limit Closed:", toteMotor.isRevLimitSwitchClosed());
        	SmartDashboard.putNumber("Elevator Joystick Value",CoPilotController.getInstance().getLeftJoystickYAxis());
        	SmartDashboard.putNumber("Elevator Drive Value",-1*CoPilotController.getInstance().getLeftJoystickYAxis());
        	SmartDashboard.putNumber("Current Position From Talon", toteMotor.getEncPosition());
        	SmartDashboard.putNumber("Talon Iaccum:", toteMotor.GetIaccum());
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotToteElevator.runTeleop()", ex);
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
    
    public void autoDrive()
    {
    	// Check for the setpoint buttons
    	if(!pressed)
    	{
    		if(CoPilotController.getInstance().getAButton())
    		{
    			// Decrement the set point
    			if(currentSetpoint != 0) currentSetpoint--;
    			pressed = true;
    		}
    		else if(CoPilotController.getInstance().getYButton())
    		{
    			// Increment the set point
    			if(currentSetpoint != 3) currentSetpoint++;
    			pressed = true;
    		}
    	}
    	switch(currentSetpoint)
	    {
	    	case 0:
	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_1);
	    	case 1:
	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_2);
	    	case 2:
	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_3);
	    	case 3:
	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_4);
	    		
	    }
    	// Making sure button pressing is handled correctly
    	if(pressed)
    	{
    		if(!CoPilotController.getInstance().getBButton()
    				&& !CoPilotController.getInstance().getXButton()
    				&& !CoPilotController.getInstance().getYButton())
    			pressed = false;
    	}
    	// If we're within the error range on PID, close the disk brake
    	if(Math.abs(toteMotor.getClosedLoopError()) < 25)
    	{
    		brakeSolenoid.set(Value.kReverse);
    		auto = false;
    	}
    	// If PID is running, the disk brake should be open
    	else
    	{
    		brakeSolenoid.set(Value.kForward);
    	}
    }
    
}
