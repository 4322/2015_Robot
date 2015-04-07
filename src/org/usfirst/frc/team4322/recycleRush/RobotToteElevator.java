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

	private enum setPointChange //enum of elevator movements
	{
		ARB, UP, DOWN,CONTAINER,STACK //arb = going to arbitrary index, up = incrementing index, down = decrementing index, container = next container position, stack = stack position
	};
	
	// Instance for the Singleton Class
    private static RobotToteElevator _instance = null;
    
    private int brakeDelay = RobotMap.ELEVATOR_BRAKE_DELAY;
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
	setPointChange setPointDelta = setPointChange.ARB;
	long currentPosition = 0;
	int targetIndex = 0;
	boolean newStack = false;
	boolean stackDone = false;
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
	    	// Create slave motor
	    	if(toteSlave == null)
	    	{
	    		toteSlave = new CANTalon(RobotMap.TOTE_ELEVATOR_SLAVE_CONTROLLER_ADDRESS);
	    		toteSlave.changeControlMode(edu.wpi.first.wpilibj.CANTalon.ControlMode.Follower);
	    		toteSlave.set(toteMotor.getDeviceID());
	    		toteSlave.enableControl();	    		
	    	}
	    	// Create brake Soleniod
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
	    	setPointDelta = setPointChange.ARB;
	    	tiltMode = true; // We want the elevator to be tilted at startup
	    	RobotLogger.getInstance().sendToConsole("RobotToteElevator.initTeleop() successfully run.");
    	}
    	catch (Exception ex)
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotToteElevator.initTeleop()", ex);
    	}
    }
    private void setupStates()
    {
    	newSetpoint = true;
		setPointSelectButtonPressed = true;
		if(autoDriveMode) controlModeV = true;
		autoDriveMode = true;
		brakeSolenoid.set(Value.kForward);
		stackDone = true;
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
        			setupStates();
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPointUpButton() || autoLift) //up button or auto align 
        		{
        			setPointDelta = setPointChange.UP;
        			setupStates();
        			// Reset autoLift boolean
        			autoLift = false;
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint0Button())
        		{        			
        			targetIndex = 0;
        			setupStates();
        			RobotLogger.getInstance().sendToConsole("Set Point 0 Button.");
        			setPointDelta = setPointChange.ARB;  //going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint1Button())
        		{
        			targetIndex = 1;
        			setupStates();
        			RobotLogger.getInstance().sendToConsole("Set Point 1 Button.");
        			setPointDelta = setPointChange.ARB;  //going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint2Button())
        		{
        			targetIndex = 2;
        			setupStates();
        			RobotLogger.getInstance().sendToConsole("Set Point 2 Button.");
        			setPointDelta = setPointChange.ARB;  //going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint3Button())
        		{
        			targetIndex = 3;
        			setupStates();
        			RobotLogger.getInstance().sendToConsole("Set Point 3 Button.");
        			setPointDelta = setPointChange.ARB;  //going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPoint4Button())
        		{
        			targetIndex = 4;
        			setupStates();
        			RobotLogger.getInstance().sendToConsole("Set Point 4 Button.");
        			setPointDelta = setPointChange.ARB;  //going to an arbitrary index; not incrementing or decrementing
        		}
        		else if(CoPilotController.getInstance().getElevatorSetPointContainerButton())
        		{
        			tiltMode = false;
        			setupStates();
        			RobotLogger.getInstance().sendToConsole("Set Point Container Button.");
        			setPointDelta = setPointChange.CONTAINER;//going to a container position
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
        			setPointDelta = setPointChange.STACK;
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
    public void runAuto()
    {
    	targetIndex++;
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
    		//if we are moving up or down
    		if(setPointDelta == setPointChange.UP || setPointDelta == setPointChange.DOWN) 
    		{
    			//calculate setpoint
    			targetIndex = (int) Math.round((setPointDelta == setPointChange.DOWN) ? Math.floor((toteMotor.get() - RobotMap.ELEVATOR_DOWN_DEADBAND) / RobotMap.ELEVATOR_POSITION_DISTANCE) : Math.ceil((toteMotor.get() + RobotMap.ELEVATOR_UP_DEADBAND) / RobotMap.ELEVATOR_POSITION_DISTANCE));
    		}
    		//ensure setpoint is in bounds
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
    		//set appropriate PID Profile
    		toteMotor.setProfile(targetIndex >= 3 ? 1 : 0);
    		//If we are not stacking or moving the container
    		if(setPointDelta != setPointChange.CONTAINER && setPointDelta != setPointChange.STACK)
    		{
    			toteMotor.set(RobotMap.ELEVATOR_POSITIONS[targetIndex]);
    		}
    		//If we are moving the container
    		else if (setPointDelta == setPointChange.CONTAINER) 
    		{
    			RobotLogger.getInstance().sendToConsole("In container Mode. Going to Container Index %d",containerModeTargetIndex);
    			//go to the container position
    			toteMotor.set(RobotMap.ELEVATOR_CONTAINER_POSITIONS[containerModeTargetIndex++]);
    			//keep container position in bounds
    			if(containerModeTargetIndex > RobotMap.ELEVATOR_CONTAINER_POSITIONS.length-1) containerModeTargetIndex = 0;
    		}
    		//if stacking
    		else if(setPointDelta == setPointChange.STACK)
    		{
    			toteMotor.set(RobotMap.ELEVATOR_STACK_POSITIONS[targetIndex]);
    		}
    		setPointDelta = setPointChange.ARB;
    		newSetpoint = false;
    		return;
    	}
    	// If we're within the error range on PID, close the disk brake
    	if(Math.abs(toteMotor.getClosedLoopError()) < RobotMap.ELEVATOR_ALLOWED_CLOSED_LOOP_ERROR)
    	{
    		if(brakeDelay == 0)
    		{
    			//engage brake
    			brakeSolenoid.set(Value.kReverse);
    			//exit automode
        		autoDriveMode = false;
        		//say we are done stacking
        		if(!stackDone) stackDone = true;
        		RobotLogger.getInstance().sendToConsole("Exited AutoDriveMode.");
        		//reset brakeDelay
        		brakeDelay = RobotMap.ELEVATOR_BRAKE_DELAY;
    		}
    		else brakeDelay--;

    	}
    	//exit if controller moves
    	if(Math.abs(CoPilotController.getInstance().getElevatorDriveStick()) > RobotMap.ELEVATOR_ANALOG_STICK_DEADBAND) autoDriveMode = false;
    	
    }
    public double getCLError()
    {
    	return toteMotor.getClosedLoopError();
    }
}