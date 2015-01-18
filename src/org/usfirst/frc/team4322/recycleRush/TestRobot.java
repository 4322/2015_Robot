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
			containerElevatorController = new CANTalon(RobotMap.CONTAINER_ELEVATOR_CONTROLLER_ADDRESS);
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
//			System.out.println("Compressor Current: " + compressor.getCompressorCurrent());
//			System.out.println("Compressor Switch Value LOW: " + compressor.getPressureSwitchValue());
//			boolean highCurrent = compressor.getCompressorCurrentTooHighFault();
//			boolean stickyHighCurrent = compressor.getCompressorCurrentTooHighStickyFault();
//			boolean stickyShorted = compressor.getCompressorShortedStickyFault();
//			boolean shorted = compressor.getCompressorShortedFault();
//			boolean stickyNC = compressor.getCompressorNotConnectedStickyFault();
//			boolean NC = compressor.getCompressorNotConnectedFault();
//			System.out.println("Compressor Error: " + (highCurrent || shorted || NC));
//			System.out.println("Compressor Sticky: " + (stickyHighCurrent || stickyShorted || stickyNC));
			SmartDashboard.putNumber("ACCEL (X)", accelerometer.getX());
			SmartDashboard.putNumber("ACCEL (Y)", accelerometer.getY());
			SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle());
			SmartDashboard.putNumber("Gyro Rate:", gyro.getRate());
			counter = 0;
		}
		if(PilotController.getInstance().getNorth())
		{
			piston.set(Value.kForward);
		}
		if(PilotController.getInstance().getYButton())
		{
			piston.set(Value.kReverse);
		}
		if(PilotController.getInstance().getSouth())
		{
			piston.set(Value.kForward);
		}
		
		//Gyro & Accelerometer
	}
	
}
