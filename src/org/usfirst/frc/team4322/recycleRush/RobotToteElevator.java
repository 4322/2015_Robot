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

	private enum setPointChange
	{
		NONE, UP, DOWN
	};
	// Instance for the Singleton Class
    static private RobotToteElevator _instance = null;
    
    // Instance for tote lifting motor.
    //private CANJaguar toteMotor = null;
    private CANTalon toteMotor = null;
    
    //Instance for Brake Solenoid
    private DoubleSolenoid brakeSolenoid = null;
    
	boolean setPointSelectButtonPressed = false;
	boolean autoDriveMode = false;
	boolean switchPressed = false;
	boolean controlModeV = true; 
	boolean newSetpoint =false;
	setPointChange setPointDelta = setPointChange.NONE;
	long currentPosition = 0;
	int targetIndex = 0;
	
	// Instance for Tilt Solenoid
	private DoubleSolenoid tiltActuatorPiston = null;
    boolean tiltPressed = false;
    // Set tiltMode = true for elevator tilted back
    boolean tiltMode = true; // <-- Lets start in elevator tilted mode
    
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
	    	autoDriveMode = false;
	    	currentPosition = 0;
	    	setPointDelta = setPointChange.NONE;
	    	tiltMode = true; // We want the elevator to be tilted at startup
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
    		currentPosition = toteMotor.getEncPosition();
    		//Check if auto mode
    		if(autoDriveMode)
        	{
    			// Check for emergency off button
        		if(CoPilotController.getInstance().getAutoEmergencyOff())
        		{
        			// Turn PID off
        			autoDriveMode = false;
        		}
        	}
    		
        	// Check for the setpoint buttons
        	if(!setPointSelectButtonPressed)
        	{
        		if(CoPilotController.getInstance().getElevatorSetPointDownButton())
        		{
        			// Decrement the set point
        			setPointDelta = setPointChange.DOWN;
        			newSetpoint = true;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Decreased PID index to " + currentPosition);
        			brakeSolenoid.set(Value.kForward);
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPointUpButton())
        		{
        			setPointDelta = setPointChange.UP;
        			newSetpoint = true;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Increased PID index to " + currentPosition);
        			brakeSolenoid.set(Value.kForward);
        		}
        	}
        	// Making sure button pressing is handled correctly
        	if(setPointSelectButtonPressed)
        	{
        		if(!CoPilotController.getInstance().getElevatorSetPointUpButton()
        		&& !CoPilotController.getInstance().getElevatorSetPointDownButton())
        		{
        			setPointSelectButtonPressed = false;
        			RobotLogger.getInstance().sendToConsole("Setpoint buttons released");
        		}
        	}
	        // Toggle Tilt Mode
	    	if(CoPilotController.getInstance().getElevatorTiltButton())
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
        	if(autoDriveMode)
        	{
        		// Switch from voltage control (manual) to position control (automatic)
        		if(controlModeV)
        		{
        			RobotLogger.getInstance().sendToConsole("Changing to PID position mode");
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
        			RobotLogger.getInstance().sendToConsole("Changing to PID voltage percent mode");
        			// Disable position control
        			toteMotor.disableControl();
        			// Change to voltage mode
        			toteMotor.changeControlMode(ControlMode.PercentVbus);
        			toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        			toteMotor.enableControl();
        			controlModeV = true;
        		}
        		toteMotor.set(-CoPilotController.getInstance().getElevatorDriveStick());
        		// Default state of the disk brake
        		if(Math.abs(CoPilotController.getInstance().getElevatorDriveStick()) < RobotMap.ELEVATOR_JOYSTICK_DEADBAND)
        		{
        			brakeSolenoid.set(Value.kReverse);
        		}
        		// Open the disk brake so we can move the elevator
        		else
        		{
        			brakeSolenoid.set(Value.kForward);
        		}
        	}
        	SmartDashboard.putBoolean("Auto Mode:", autoDriveMode);
        	SmartDashboard.putNumber("Target Encoder Value:",toteMotor.getSetpoint());
//        	SmartDashboard.putNumber("Error Value:",toteMotor.getClosedLoopError());
        	SmartDashboard.putBoolean("Tote Lift Forward Limit Closed:", toteMotor.isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tote Lift Reverse Limit Closed:", toteMotor.isRevLimitSwitchClosed());
//        	SmartDashboard.putNumber("Elevator Joystick Value",CoPilotController.getInstance().getElevatorDriveStick());
//        	SmartDashboard.putNumber("Elevator Drive Value",-1*CoPilotController.getInstance().getElevatorDriveStick());
        	SmartDashboard.putNumber("Current Encoder Value", toteMotor.getEncPosition());
//        	SmartDashboard.putNumber("Talon Iaccum:", toteMotor.GetIaccum());
//        	SmartDashboard.putNumber("Target Encoder Value", autoDriveMode ? toteMotor.get() : 0);
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
//    	// Check for the setpoint buttons
//    	if(!pressed)
//    	{
//    		if(CoPilotController.getInstance().getElevatorSetPointUpButton())
//    		{
//    			// Decrement the set point
//    			if(currentSetpoint != 0) currentSetpoint--;
//    			pressed = true;
//    		}
//    		else if(CoPilotController.getInstance().getElevatorSetPointDownButton())
//    		{
//    			// Increment the set point
//    			if(currentSetpoint != 3) currentSetpoint++;
//    			pressed = true;
//    		}
//    	}
    	//Ensure brake is open
		brakeSolenoid.set(Value.kForward);
//    	switch(currentSetpoint)
//	    {
//	    	case 0:
//	    		if(toteMotor.get() !=RobotMap.ELEVATOR_POSITION_1) RobotLogger.getInstance().sendToConsole("Set Talon to setpoint 1.");
//	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_1);
//	    		break;
//	    	case 1:
//	    		if(toteMotor.getSetpoint() !=RobotMap.ELEVATOR_POSITION_2) RobotLogger.getInstance().sendToConsole("Set Talon to setpoint 2.");
//	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_2);
//	    		break;
//	    	case 2:
//	    		if(toteMotor.getSetpoint() !=RobotMap.ELEVATOR_POSITION_3) RobotLogger.getInstance().sendToConsole("Set Talon to setpoint 3.");
//	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_3);
//	    		break;
//	    	case 3:
//	    		if(toteMotor.getSetpoint() !=RobotMap.ELEVATOR_POSITION_4) RobotLogger.getInstance().sendToConsole("Set Talon to setpoint 4.");
//	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_4);
//	    		break;
//	    	case 4:
//	    		if(toteMotor.getSetpoint() !=RobotMap.ELEVATOR_POSITION_5) RobotLogger.getInstance().sendToConsole("Set Talon to setpoint 5.");
//	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_5);
//	    		break;
//	    	case 5:
//	    		if(toteMotor.getSetpoint() != RobotMap.ELEVATOR_POSITION_6) RobotLogger.getInstance().sendToConsole("Set Talon to setpoint 6.");
//	    		toteMotor.set(RobotMap.ELEVATOR_POSITION_6);
//	    		break;
//	    }
		
    	if(newSetpoint)
    	{
    		targetIndex = (int) Math.round((setPointDelta == setPointChange.DOWN) ? Math.floor((toteMotor.get()-150) / 3150) : Math.ceil((toteMotor.get()+150) / 3150));
    		if(targetIndex >= RobotMap.ELEVATOR_POSITIONS.length) targetIndex = RobotMap.ELEVATOR_POSITIONS.length -1;
    		else if(targetIndex < 0) targetIndex = 1;
    		toteMotor.set(RobotMap.ELEVATOR_POSITIONS[targetIndex]);
    		setPointDelta = setPointChange.NONE;
    		newSetpoint = false;
    		return;
    	}
    	toteMotor.set(RobotMap.ELEVATOR_POSITIONS[targetIndex]);
    	// If we're within the error range on PID, close the disk brake
    	if(Math.abs(toteMotor.getClosedLoopError()) < 10)
    	{
    		brakeSolenoid.set(Value.kReverse);
    		autoDriveMode = false;
    		RobotLogger.getInstance().sendToConsole("Exited AutoDriveMode.");
    	}
    	if(Math.abs(CoPilotController.getInstance().getElevatorDriveStick()) > RobotMap.ELEVATOR_ANALOG_STICK_DEADBAND) autoDriveMode = false;
    }
}
