package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CANJaguar.LimitMode;
import edu.wpi.first.wpilibj.CANJaguar.NeutralMode;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
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
    private CANJaguar toteMotor = null;
    
    //Instance for Brake Solenoid
    private DoubleSolenoid brakeSolenoid = null;
    
	boolean pressed = false;
	
	boolean stopped = false;
	
	int elevatorState = 0;
	
	int elevatorPosition = 0;

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
    	// Create toteMotor
    	if(toteMotor == null)
    	{
    		toteMotor = new CANJaguar(RobotMap.TOTE_ELEVATOR_CONTROLLER_ADDRESS);
    		toteMotor.configEncoderCodesPerRev(RobotMap.PULSES_PER_MOTOR_REVOLUTION);
    		toteMotor.configNeutralMode(NeutralMode.Coast);
    	}
    	if(brakeSolenoid == null)
    	{
    		brakeSolenoid = new DoubleSolenoid(RobotMap.ELEVATOR_PISTON_FORWARD_PORT,RobotMap.ELEVATOR_PISTON_REVERSE_PORT);
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
    	brakeSolenoid.set(Value.kForward);
    }

    // Periodic code for teleop mode should go here. This method is called ~50x per second.
    public void runTeleop()
    {
//    	switch(elevatorState)
//    	{
//    	case RobotMap.ELEVATOR_STATE_INIT:
//    		if(!toteMotor.getReverseLimitOK())
//    		{
//    			toteMotor.set(RobotMap.ELEVATOR_INITIAL_SEEK_SPEED);
//    		}
//    		else
//    		{
//    			toteMotor.disableControl();
//    			toteMotor.setPositionMode(CANJaguar.kQuadEncoder, 7, RobotMap.ELEVATOR_P_VALUE, RobotMap.ELEVATOR_I_VALUE, RobotMap.ELEVATOR_D_VALUE);
//    			toteMotor.enableControl();
//    			elevatorState = RobotMap.ELEVATOR_STATE_STOPPED;
//    		}
//    		break;
//    	case RobotMap.ELEVATOR_STATE_GOTO_TARGET:
//    		if(elevatorPosition == 0)
//    		{
//        		toteMotor.set(RobotMap.ELEVATOR_POSITION_1);
//        		if(Math.abs(toteMotor.getPosition() - RobotMap.ELEVATOR_POSITION_1) < 1) elevatorState = RobotMap.ELEVATOR_STATE_STOPPED;
//    		}
//    		else if(elevatorPosition == 1)
//    		{
//        		toteMotor.set(RobotMap.ELEVATOR_POSITION_2);
//        		if(Math.abs(toteMotor.getPosition() - RobotMap.ELEVATOR_POSITION_2) < 1) elevatorState = RobotMap.ELEVATOR_STATE_STOPPED;
//    		}
//    		else if(elevatorPosition == 2)
//    		{
//    			toteMotor.set(RobotMap.ELEVATOR_POSITION_3);
//    			if(Math.abs(toteMotor.getPosition() - RobotMap.ELEVATOR_POSITION_3) < 1) elevatorState = RobotMap.ELEVATOR_STATE_STOPPED;
//    		}
//       		else if(elevatorPosition == 3)
//    		{
//    			toteMotor.set(RobotMap.ELEVATOR_POSITION_4);
//    			if(Math.abs(toteMotor.getPosition() - RobotMap.ELEVATOR_POSITION_4) < 1) elevatorState = RobotMap.ELEVATOR_STATE_STOPPED;
//    		}
//    		break;
//    	case RobotMap.ELEVATOR_STATE_STOPPED:
//    		if(!stopped)
//    		{
//    			toteMotor.disableControl();
//    			brakeSolenoid.set(Value.kReverse);
//    			stopped = true;
//    		}
//    		break;
//    	}
//    	if(CoPilotController.getInstance().getAButton())
//    	{
//    		if(!pressed)
//    		{
//    			pressed = true;
//    			elevatorState = RobotMap.ELEVATOR_STATE_GOTO_TARGET;
//    			if(elevatorPosition < 3) elevatorPosition++;
//    			else elevatorPosition = 0;
//    		}
//    	}
    	toteMotor.set(CoPilotController.getInstance().getRightJoystickYAxis());
    	SmartDashboard.putNumber("Tote Lift Jaguar Position:", toteMotor.getPosition());
    	SmartDashboard.putBoolean("Tote Lift Jaguar Forward Limit OK:", toteMotor.getForwardLimitOK());
    	SmartDashboard.putBoolean("Tote Lift Jaguar Reverse Limit OK:", toteMotor.getReverseLimitOK());
    	System.out.printf("Tote Lift Jaguar Position: %f\n", toteMotor.getPosition());
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
