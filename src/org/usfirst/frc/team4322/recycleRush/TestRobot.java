package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * This class is called for testing purposes
 */

public class TestRobot {
	
	private CANTalon toteElevatorController = null;
	boolean manualMode = false;
	private CANTalon containerElevatorController = null;

	private BuiltInAccelerometer accelerometer = null;
	private Gyro gyro = null;
	
	private CameraServer camera = CameraServer.getInstance();
	
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
		if(pdp == null)
		{
			pdp = new PowerDistributionPanel();
			pdp.clearStickyFaults();
		}
		camera.setQuality(70);
		camera.startAutomaticCapture("cam0");
	}
	
	public void run()
	{
		LiveWindow.setEnabled(true);
		counter++;
		if (counter == 5)
		{
			SmartDashboard.putNumber("ACCEL (X)", accelerometer.getX());
			SmartDashboard.putNumber("ACCEL (Y)", accelerometer.getY());
			SmartDashboard.putNumber("ACCEL (Z)" , accelerometer.getZ());
			SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle());
			SmartDashboard.putNumber("Gyro Rate:", gyro.getRate());
			
			LiveWindow.addSensor("ToteElevator", "Acceleration", accelerometer);
			LiveWindow.addSensor("ToteElevator", "Rotation", gyro);
			
			counter = 0;
		}
	}
	
}
