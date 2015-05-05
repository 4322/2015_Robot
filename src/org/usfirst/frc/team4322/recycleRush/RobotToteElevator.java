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

	private enum setPointChange // initialize enum to keep track of which way the elevator should be going
	{
		NONE, UP, DOWN //none = going to arbitrary index, up = incrementing index, down = decrementing index
	};
	
	// Instance for the Singleton Class
    private static RobotToteElevator _instance = null;
    
    private int brakeDelay = 4;
    // Instance for tote lifting motor.
    private CANTalon toteMotor = null;
    private CANTalon toteSlave = null;
    
    // Instance for Brake Solenoid
    private DoubleSolenoid brakeSolenoid = null;
    
    // Instances for PID Control
	boolean setPointSelectButtonPressed = false;
	public boolean autoDriveMode = false;
	boolean switchPressed = false;
	boolean controlModeV = true; 
	boolean newSetpoint =false;
	setPointChange setPointDelta = setPointChange.NONE;
	long currentPosition = 0;
	int targetIndex = 0;
	boolean newStack = false;
	boolean stackDone = false;

	boolean containerMode = false; 
	int containerModeTargetIndex = 0;
	
	// Instance for tote lifting after auto-align
	boolean autoLift = false;
	
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
	    		toteMotor.setPID(RobotMap.ELEVATOR_PROFILE_1_P_VALUE, RobotMap.ELEVATOR_PROFILE_1_I_VALUE,RobotMap.ELEVATOR_PROFILE_1_D_VALUE, RobotMap.ELEVATOR_PROFILE_1_F_VALUE,RobotMap.ELEVATOR_PROFILE_1_IZONE_VALUE, RobotMap.ELEVATOR_RAMPRATE_VALUE, 0);
	    		toteMotor.setPID(RobotMap.ELEVATOR_PROFILE_2_P_VALUE, RobotMap.ELEVATOR_PROFILE_2_I_VALUE,RobotMap.ELEVATOR_PROFILE_2_D_VALUE, RobotMap.ELEVATOR_PROFILE_2_F_VALUE,RobotMap.ELEVATOR_PROFILE_2_IZONE_VALUE, RobotMap.ELEVATOR_RAMPRATE_VALUE, 1);
	    		toteMotor.ClearIaccum();
	    		toteMotor.enableControl();
	    		RobotLogger.getInstance().sendToConsole("Elevator TalonSRX Firmware Version: " + toteMotor.GetFirmwareVersion());
	    	}
	    	if(toteSlave == null)
	    	{
	    		toteSlave = new CANTalon(RobotMap.TOTE_ELEVATOR_SLAVE_CONTROLLER_ADDRESS);
	    		toteSlave.changeControlMode(edu.wpi.first.wpilibj.CANTalon.ControlMode.Follower);
	    		toteSlave.set(toteMotor.getDeviceID());
	    		toteSlave.enableControl();	    		
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
    	toteMotor.setPosition(0);
    }

    // Initialization code for teleop mode should go here.
    public void initTeleop()
    {
    	try
    	{
	    	brakeSolenoid.set(Value.kReverse);
	    	toteMotor.clearStickyFaults();
	    	toteMotor.ClearIaccum();
	    	toteSlave.clearStickyFaults();	    	autoDriveMode = false;
	    	currentPosition = 0;
	    	if(!toteMotor.isControlEnabled()) toteMotor.enableControl();
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
    		// Check if auto mode
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
        			brakeSolenoid.set(Value.kForward);
        			stackDone = true;
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPointUpButton() || autoLift) //up button or auto align 
        		{
        			setPointDelta = setPointChange.UP;
        			newSetpoint = true;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			brakeSolenoid.set(Value.kForward);
        			// Reset autoLift boolean
        			autoLift = false;
        			stackDone = true;
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint0Button())
        		{
        			newSetpoint = true;
        			targetIndex = 0;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Set Point 0 Button.");
        			brakeSolenoid.set(Value.kForward);
        			stackDone = true;
        			setPointDelta = setPointChange.NONE;  //going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint1Button())
        		{
        			newSetpoint = true;
        			targetIndex = 1;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Set Point 1 Button.");
        			brakeSolenoid.set(Value.kForward);
        			stackDone = true;
        			setPointDelta = setPointChange.NONE;//going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint2Button())
        		{
        			newSetpoint = true;
        			targetIndex = 2;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Set Point 2 Button.");
        			brakeSolenoid.set(Value.kForward);
        			stackDone = true;
        			setPointDelta = setPointChange.NONE;//going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint3Button())
        		{
        			newSetpoint = true;
        			targetIndex = 3;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Set Point 3 Button.");
        			brakeSolenoid.set(Value.kForward);
        			stackDone = true;
        			setPointDelta = setPointChange.NONE;//going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint4Button())
        		{
        			newSetpoint = true;
        			targetIndex = 4;
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Set Point 4 Button.");
        			brakeSolenoid.set(Value.kForward);
        			stackDone = true;
        			setPointDelta = setPointChange.NONE;//going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPointContainerButton())
        		{
        			tiltMode = false;
        			containerMode = true;
        			newSetpoint = true;
        			//targetIndex = 4; //target index doesn't matter
        			setPointSelectButtonPressed = true;
        			if(autoDriveMode) controlModeV = true;
        			autoDriveMode = true;
        			RobotLogger.getInstance().sendToConsole("Set Point Container Button.");
        			brakeSolenoid.set(Value.kForward);
        			stackDone = true;
        			setPointDelta = setPointChange.NONE;//going to an arbitrary index; not incrementing or decrementing
        		}
        	}
        	// Making sure button pressing is handled correctly
        	if(setPointSelectButtonPressed)
        	{
        		if(!CoPilotController.getInstance().getElevatorSetPointUpButton()
        		&& !CoPilotController.getInstance().getElevatorSetPointDownButton()
        		&& !CoPilotController.getInstance().getElevatorSetPoint0Button()
        		&& !CoPilotController.getInstance().getElevatorSetPoint1Button()
        		&& !CoPilotController.getInstance().getElevatorSetPoint3Button()
        		&& !CoPilotController.getInstance().getElevatorSetPoint2Button()
        		&& !CoPilotController.getInstance().getElevatorSetPoint4Button()
        		&& !CoPilotController.getInstance().getElevatorSetPointContainerButton()
        		)
        		{
        			setPointSelectButtonPressed = false;
        			RobotLogger.getInstance().sendToConsole("Setpoint buttons released");
        		}
        	}
    		if(CoPilotController.getInstance().getStackButton())
        	{
        		if(!newStack)
        		{
        			newStack = true;
        			RobotLogger.getInstance().sendToConsole("Set newStack True!\n");
        			stackDone = false;
        			autoDriveMode = true;
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
        		// Switch from voltagecontrol (manual) to position control (automatic)
        		if(controlModeV)
        		{
        			RobotLogger.getInstance().sendToConsole("Changing to PID position mode");
        			// Disable voltage control
        			toteMotor.disableControl();
        			// Change to position mode
            		toteMotor.changeControlMode(ControlMode.Position);
            		toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            		toteMotor.enableControl();
//            		toteSlave.disableControl();
//    	    		toteSlave.changeControlMode(edu.wpi.first.wpilibj.CANTalon.ControlMode.Follower);
//    	    		toteSlave.set(toteMotor.getDeviceID());
//    	    		toteSlave.enableControl();	    
            		controlModeV = false;
        		}
            	if(!stackDone)
            	{
            		if(newStack)
            		{
            			tiltActuatorPiston.set(Value.kForward);
            			tiltMode = false;
            			toteMotor.set(RobotMap.ELEVATOR_STACK_POSITIONS[targetIndex]);
            			RobotLogger.getInstance().sendToConsole("Set AutoStack!\n Target Position number is :%d, and target encoder position is %f.\n",targetIndex,RobotMap.ELEVATOR_STACK_POSITIONS[targetIndex]);
            			newStack = false;
            		}
            		else
            		{
            			if(toteMotor.getClosedLoopError() > 10)
            			{
            				stackDone = true;
                			RobotLogger.getInstance().sendToConsole("Set StackDone True\n");
            			}
            			if(Math.abs(CoPilotController.getInstance().getElevatorDriveStick()) > RobotMap.ELEVATOR_ANALOG_STICK_DEADBAND)
            			{
            				stackDone = true;
            			}
            		}
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
//            		toteSlave.disableControl();
//    	    		toteSlave.changeControlMode(edu.wpi.first.wpilibj.CANTalon.ControlMode.Follower);
//    	    		toteSlave.set(toteMotor.getDeviceID());
//    	    		toteSlave.enableControl();	    
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
//        	SmartDashboard.putBoolean("Auto Mode:", autoDriveMode);
//        	SmartDashboard.putBoolean("Auto Lifting: ", autoLift);
        	SmartDashboard.putNumber("Target Encoder Value:", toteMotor.getSetpoint());
        	SmartDashboard.putNumber("[Elevator] Current Encoder Value", toteMotor.getEncPosition());
        	SmartDashboard.putNumber("Error Value:", toteMotor.getClosedLoopError());
        	SmartDashboard.putBoolean("Tote Lift Forward Limit Closed:", toteMotor.isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tote Lift Reverse Limit Closed:", toteMotor.isRevLimitSwitchClosed());
//        	SmartDashboard.putNumber("Elevator Joystick Value", CoPilotController.getInstance().getElevatorDriveStick());
//        	SmartDashboard.putNumber("Control Panel POV Value", CoPilotController.getInstance().getPOV());
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
    public void startAutoLift()
    {
    	autoLift = true;
    }
    public void runAuto(boolean container)
    {
    	if(!container)
    	{
        	targetIndex++;
    	}
    	else
    	{
    		targetIndex = 5;
    	}
    	toteMotor.disableControl();
		// Change to position mode
		toteMotor.changeControlMode(ControlMode.Position);
		toteMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		toteMotor.enableControl();
		controlModeV=false;
		autoDriveMode=true;
		toteMotor.set(RobotMap.ELEVATOR_POSITIONS[targetIndex]);
    }
    
    public void brake()
    {
		brakeSolenoid.set(Value.kReverse);
		if(toteMotor.isControlEnabled()) toteMotor.disableControl();
    }
    public void autoDrive()
    {
    	//Ensure brake is open
		brakeSolenoid.set(Value.kForward);
		
    	if(newSetpoint)
    	{
    		if(setPointDelta != setPointChange.NONE) 
    		{
    			targetIndex = (int) Math.round((setPointDelta == setPointChange.DOWN) ? Math.floor((toteMotor.get() - RobotMap.ELEVATOR_DOWN_DEADBAND) / RobotMap.ELEVATOR_POSITION_DISTANCE) : Math.ceil((toteMotor.get() + RobotMap.ELEVATOR_UP_DEADBAND) / RobotMap.ELEVATOR_POSITION_DISTANCE));
    		}
    		if(targetIndex >= RobotMap.ELEVATOR_POSITIONS.length) targetIndex = RobotMap.ELEVATOR_POSITIONS.length -1;
    		else if(targetIndex < 0) targetIndex = 1;
    		RobotLogger.getInstance().sendToConsole("Going to index %d", targetIndex);
    		if(setPointDelta == setPointChange.DOWN)
    		{
    			RobotLogger.getInstance().sendToConsole("Set Point Down Button: Current Encoder Position is " + toteMotor.get() + " and moving to " + RobotMap.ELEVATOR_POSITIONS[targetIndex]);
    		}
    		else if(setPointDelta == setPointChange.UP)
    		{
    			RobotLogger.getInstance().sendToConsole("Set Point Up Button: Current Encoder Position is " + toteMotor.get() + " and moving to " + RobotMap.ELEVATOR_POSITIONS[targetIndex]);
    		}
    		if(targetIndex >= 3)
    		{
    			toteMotor.setProfile(1);
    		}
    		else
    		{
    			toteMotor.setProfile(0);
    		}
    		if(!containerMode)
    		toteMotor.set(RobotMap.ELEVATOR_POSITIONS[targetIndex]);
    		else if (containerMode) 
    		{
    			RobotLogger.getInstance().sendToConsole("In container Mode. Going to Container Index %d",containerModeTargetIndex);
    			toteMotor.set(RobotMap.ELEVATOR_CONTAINER_POSITIONS[containerModeTargetIndex]);
    			containerModeTargetIndex++;
    			if(containerModeTargetIndex > RobotMap.ELEVATOR_CONTAINER_POSITIONS.length-1) containerModeTargetIndex = 0;
    			containerMode = false;
    		}
    		setPointDelta = setPointChange.NONE;
    		newSetpoint = false;
    		return;
    	}
    	//toteMotor.set(RobotMap.ELEVATOR_POSITIONS[targetIndex]); // don't call this every loop. use above function
    	// If we're within the error range on PID, close the disk brake
    	if(Math.abs(toteMotor.getClosedLoopError()) < RobotMap.ELEVATOR_ALLOWED_CLOSED_LOOP_ERROR)
    	{
    		if(brakeDelay == 0)
    		{
    			brakeSolenoid.set(Value.kReverse);
        		autoDriveMode = false;
        		RobotLogger.getInstance().sendToConsole("Exited AutoDriveMode.");
        		brakeDelay = 4;
    		}
    		else brakeDelay--;

    	}
    	if(Math.abs(CoPilotController.getInstance().getElevatorDriveStick()) > RobotMap.ELEVATOR_ANALOG_STICK_DEADBAND) autoDriveMode = false;
    	
    }
    public int getAmountOfTotes()
    {
    	return targetIndex;
    }
    public double getCLError()
    {
    	return toteMotor.getClosedLoopError();
    }
}