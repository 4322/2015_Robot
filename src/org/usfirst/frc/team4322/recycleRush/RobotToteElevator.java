package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
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
	    		brakeSolenoid = new DoubleSolenoid(RobotMap.ELEVATOR_PISTON_FORWARD_PORT,RobotMap.ELEVATOR_PISTON_REVERSE_PORT);
	    		// Value.kForward <-- BRAKE RELEASED
	    		// Value.kReverse <-- BRAKE ENGAGED
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
	    	brakeSolenoid.set(Value.kForward);
	    	toteMotor.clearStickyFaults();
	    	toteMotor.ClearIaccum();
	    	toteMotor.setPosition(0);
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
    		if(!switchPressed)
        	{
        		if(CoPilotController.getInstance().getStartButton())
        		{
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
        	if(auto)
        	{
        		if(controlModeV)
        		{
        			toteMotor.disableControl();
            		toteMotor.changeControlMode(ControlMode.Position);
            		toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            		toteMotor.enableControl();
            		controlModeV = false;
        		}
        		autoDrive();
        	}
        	else
        	{
        		if(!controlModeV)
        		{
        			toteMotor.disableControl();
        			toteMotor.changeControlMode(ControlMode.PercentVbus);
        			toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        			toteMotor.enableControl();
        			controlModeV = true;
        		}
        		toteMotor.set(-1*CoPilotController.getInstance().getLeftJoystickYAxis());
        		if(CoPilotController.getInstance().getLeftJoystickYAxis() < .1)
        		{
        			brakeSolenoid.set(Value.kForward);
        		}
        	}
        	SmartDashboard.putBoolean("Auto Mode:", auto);
        	SmartDashboard.putNumber("Target Value:",toteMotor.getSetpoint());
        	SmartDashboard.putNumber("Error Value:",toteMotor.getClosedLoopError());
        	SmartDashboard.putBoolean("Tote Lift Forward Limit Closed:", toteMotor.isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tote Lift Reverse Limit Closed:", toteMotor.isRevLimitSwitchClosed());
        	SmartDashboard.putNumber("Joystick Value",CoPilotController.getInstance().getLeftJoystickYAxis());
        	SmartDashboard.putNumber("Drive Value",-1*CoPilotController.getInstance().getLeftJoystickYAxis());
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
    	if(!pressed)
    	{
    		if(CoPilotController.getInstance().getAButton())
    		{
    			if(currentSetpoint != 0) currentSetpoint--;
    			pressed = true;
    		}
    		else if(CoPilotController.getInstance().getYButton())
    		{
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
    	if(pressed)
    	{
    		if(!CoPilotController.getInstance().getBButton() && !CoPilotController.getInstance().getXButton() && !CoPilotController.getInstance().getYButton()) pressed = false;
    	}
    	if(Math.abs(toteMotor.getClosedLoopError()) < 2)
    	{
    		brakeSolenoid.set(Value.kForward);
    	}
    	else
    	{
    		brakeSolenoid.set(Value.kReverse);
    	}
    }
}
