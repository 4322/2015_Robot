package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
//import edu.wpi.first.wpilibj.GenericHID.Hand;

/*
 * This class is called for testing purposes
 */

public class TestRobot {
	
	//Initialize the CAN motor controllers
	private CANTalon toteElevatorController = null;
	boolean manualMode = false;
	private CANTalon containerElevatorController = null;
	private BuiltInAccelerometer accelerometer = null;
	private Gyro gyro = null;
	
	private DoubleSolenoid piston = null;
	
	//Initialize PCP
	private PowerDistributionPanel pdp = null;
	
	//Initialize Compressor
	private Compressor compressor = null;
	
	//Define the instance
	private static TestRobot _instance = null;
	
	int counter = 0;
	
	//Method to call the class
	public static TestRobot getInstance()
	{
		if(_instance == null)
		{
			_instance = new TestRobot();
		}
		return _instance;
	}
	
	public void init()
	{
		if(toteElevatorController == null)
		{
			toteElevatorController = new CANTalon(RobotMap.TOTE_ELEVATOR_CONTROLLER_ADDRESS);
		}
		if(containerElevatorController == null)
		{
			containerElevatorController = new CANTalon(RobotMap.TOTE_ELEVATOR_SLAVE_CONTROLLER_ADDRESS);
		}
		if(accelerometer == null)
		{
			accelerometer = new BuiltInAccelerometer();
		}
		if(gyro == null)
		{
			gyro = new Gyro(1); //AnalogIn Port
		}
		if(compressor == null)
		{
			compressor = new Compressor(); //assuming PCM ID is 0
			compressor.clearAllPCMStickyFaults();
		}
		if(piston == null)
		{
			piston = new DoubleSolenoid(1, 2); //assuming PCM ID is 0
			piston.set(Value.kReverse);
			piston.clearAllPCMStickyFaults();
		}
		if(pdp == null)
		{
			pdp = new PowerDistributionPanel();
			pdp.clearStickyFaults();
		}
	}
	
	public void run()
	{
		LiveWindow.setEnabled(true); //bleh
		counter++;
		if (counter == 5) {
//			RobotLogger.getInstance().sendToConsole("Compressor Current: " + compressor.getCompressorCurrent());
//			RobotLogger.getInstance().sendToConsole("Compressor Switch Value LOW: " + compressor.getPressureSwitchValue());
//			boolean highCurrent = compressor.getCompressorCurrentTooHighFault();
//			boolean stickyHighCurrent = compressor.getCompressorCurrentTooHighStickyFault();
//			boolean stickyShorted = compressor.getCompressorShortedStickyFault();
//			boolean shorted = compressor.getCompressorShortedFault();
//			boolean stickyNC = compressor.getCompressorNotConnectedStickyFault();
//			boolean NC = compressor.getCompressorNotConnectedFault();
//			RobotLogger.getInstance().sendToConsole("Compressor Error: " + (highCurrent || shorted || NC));
//			RobotLogger.getInstance().sendToConsole("Compressor Sticky: " + (stickyHighCurrent || stickyShorted || stickyNC));
			counter = 0;
		}
		SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle());
		SmartDashboard.putNumber("Gyro Rate:", gyro.getRate());
		
		//Gyro & Accelerometer
	}
	
}
