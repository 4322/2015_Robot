package org.usfirst.frc.team4322.recycleRush;

//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class RobotMain extends IterativeRobot
{

	// For logging the start of periodic mode
	private boolean autoBegin = false;
	private boolean teleBegin = false;
	private boolean testBegin = false;
	
//	private CameraServer camera = CameraServer.getInstance();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */

	public void robotInit()
	{
		try
		{
			RobotLogger.getInstance().update();
//			camera.setQuality(50);
//			camera.startAutomaticCapture("cam0");
			RobotDriveBase.getInstance().initRobotDrive();
			RobotLogger.getInstance().sendToConsole("Robot Successfully Started. Last Build Time: " + RobotMap.LAST_BUILD_TIME);
		}
		catch (Exception ex)
		{
			RobotLogger.getInstance().writeErrorToFile("robotInit()", ex);
		}
	}

	@Override
	public void disabledInit()
	{
		try
		{
			RobotLogger.getInstance().sendToConsole("Robot Disabled.");
			RobotLogger.getInstance().close();
		}
		catch (Exception ex)
		{
			RobotLogger.getInstance().writeErrorToFile("disabledInit()", ex);
		}
	}

	/**
	 * This function is called periodically when robot is disabled
	 */

	@Override
	public void disabledPeriodic()
	{
		// TODO Auto-generated method stub
		super.disabledPeriodic();
	}

	/**
	 * This function is called right before autonomous
	 */

	@Override
	public void autonomousInit()
	{
		try
		{
			super.autonomousInit();
			RobotLogger.getInstance().update();
			RobotDriveBase.getInstance().initAutonomous();
			autoBegin = false;
			RobotLogger.getInstance().sendToConsole("Robot Autonomous Mode Initialized.");
		}
		catch (Exception ex)
		{
			RobotLogger.getInstance().writeErrorToFile("autonomousInit()", ex);
		}
	}

	/**
	 * This function is called periodically during autonomous
	 */

	public void autonomousPeriodic()
	{
		try
		{
			if (!autoBegin)
			{
				RobotLogger.getInstance().sendToConsole("Robot Autonomous Mode Begin.");
				autoBegin = true;
			}
			RobotDriveBase.getInstance().runAutonomous();
		}
		catch (Exception ex)
		{
    		RobotLogger.getInstance().writeErrorToFile("autonomousPeriodic()", ex);
    	}
	}

	/**
	 * This function is called right before operator control
	 */

	public void teleopInit()
	{
		try
		{
			RobotLogger.getInstance().update();
			RobotDriveBase.getInstance().initTeleop();
			teleBegin = false;
			RobotLogger.getInstance().sendToConsole("Robot Teleop Mode Initialized.");
		}
		catch (Exception ex)
		{
    		RobotLogger.getInstance().writeErrorToFile("teleopInit()", ex);
    	}
	}

	/**
	 * This function is called periodically during operator control
	 */

	public void teleopPeriodic()
	{
		try
		{
			if (!teleBegin)
			{
				RobotLogger.getInstance().sendToConsole("Robot Teleop Mode Begin.");
				teleBegin = true;
			}
			RobotDriveBase.getInstance().runTeleop();
		}
		catch (Exception ex)
		{
    		RobotLogger.getInstance().writeErrorToFile("teleopPeriodic()", ex);
    	}
	}

	/**
	 * This function is called right before test mode
	 */

	public void testInit()
	{
		try
		{
			RobotLogger.getInstance().update();
			TestRobot.getInstance().init();
			testBegin = false;
			RobotLogger.getInstance().sendToConsole("Robot Test Mode Initialized.");
		}
		catch (Exception ex)
		{
    		RobotLogger.getInstance().writeErrorToFile("testInit()", ex);
    	}
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic()
	{
		try
		{
			if (!testBegin)
			{
				RobotLogger.getInstance().sendToConsole("Robot Test Mode Begin.");
				testBegin = true;
			}
			TestRobot.getInstance().run();
		}
		catch (Exception ex)
		{
    		RobotLogger.getInstance().writeErrorToFile("testPeriodic()", ex);
    	}
	}
}
