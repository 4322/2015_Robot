
package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	private boolean disabledBegin = false;
	private boolean autoBegin = false;
	private boolean teleBegin = false;
	private boolean testBegin = false;
	
	// Sendables for choosing autonomous modes
	private Command autoCommand;
	private SendableChooser autoChooser;
	
	// Instance for autonomous mode (integer)
	private int autoMode = 1;
		
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	
    public void robotInit()
    {
	    try
	    {
	    	// Open and update the RobotLogger in case log files are too large
	    	RobotLogger.getInstance().update();

	    	// Turn on the camera and trap any exceptions if it is not available
		    try
		    {
		    	CameraServer camera = CameraServer.getInstance();
		    	camera.setQuality(50);
		    	camera.startAutomaticCapture("cam0");
			}
			catch (Exception ex)
			{
				RobotLogger.getInstance().writeErrorToFile("robotInit() Camera Server Startup: ", ex);
			}
	    	
	    	// Initiate each system on the robot
	    	RobotDriveBase.getInstance().initRobotDrive();
	    	
	    	// Create the Sendable Choosers on the SmartDashboard
	    	autoChooser = new SendableChooser();
	    	autoChooser.addDefault("DriveForward (Auto 1)", new DriveForward());
	    	autoChooser.addObject("DriveBackward (Auto 2)", new DriveBackward());
	    	SmartDashboard.putData("Auto Mode Chooser", autoChooser);
	    	
	    	// Send success and last build time to log file
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
    		// Restart disabled
			disabledBegin = false;
			RobotLogger.getInstance().sendToConsole("Robot Disabled.");
    		// Flush & close the FileWriter and BufferWriter
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
		try
		{
			// Log initiation only once
 			if (!disabledBegin)
			{
				RobotLogger.getInstance().sendToConsole("Robot Autonomous Mode Begin.");
				disabledBegin = true;
			}
 			if(PilotController.getInstance().getYButton())
 			{
 				if(autoMode < 2) //Max mode #
 					autoMode++;
 				else
 					autoMode = 1; //Min mode #
 			}
 			if(PilotController.getInstance().getXButton())
 			{
 				if(autoMode > 1) //Min mode #
 					autoMode--;
 				else
 					autoMode = 2; //Max mode #
 			}
 			SmartDashboard.putNumber("Autonomous Mode", autoMode);
		}
		catch (Exception ex)
		{
			RobotLogger.getInstance().writeErrorToFile("autonomousInit()", ex);
		}
	}

	/**
	 * This function is called right before autonomous
	 */
	
    @Override
	public void autonomousInit()
    {
    	try
		{
    		// Open and update the RobotLogger in case log files are too large
	    	RobotLogger.getInstance().update();
	    	
//			RobotDriveBase.getInstance().initAutonomous();
//			autoBegin = false;
	    	
	    	// Get the selected command and schedule it
			autoCommand = (Command) autoChooser.getSelected();
			autoCommand.start();
			
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
    		// Log initiation only once
			if (!autoBegin)
			{
				RobotLogger.getInstance().sendToConsole("Robot Autonomous Mode Begin.");
				autoBegin = true;
			}
//			RobotDriveBase.getInstance().runAutonomous(autoMode);
			
			/**
			 * RobotBuilder will generate code automatically that runs the
			 * scheduler every driver station update period (about every 20ms).
			 * This will cause the selected autonomous command to run.
			 */
			Scheduler.getInstance().run();
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
    		// Log initiation only once
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
    		// Log initiation only once
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
