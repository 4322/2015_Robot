 
package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.*;
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

	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	
    public void robotInit()
    {
	    try
	    {
	    	// Open and update the RobotLogger in case log files are too large
	    	RobotLogger.getInstance().update(false); //No zipping right now
	    	// Get information
	    	RobotConfigFileReader.getInstance().runRobotFileReader();
	    	RobotDriveBase.getInstance().initRobotDrive();
	    	RobotToteElevator.getInstance().initRobotToteElevator();
//			RobotVision.getInstance().initRobotVision();
	    	
	    	// Send success and last build time to log file
	    	RobotLogger.getInstance().sendToConsole("Robot Successfully Started. Last Build Time: " + RobotMap.LAST_BUILD_TIME);
	    	SmartDashboard.putString("Last Robot Build Time", RobotMap.LAST_BUILD_TIME);
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
    		RobotLogger.getInstance().close();
    		RobotLogger.getInstance().update(false);
	//    	RobotConfigFileReader.getInstance().runRobotFileReader();
    		RobotLogger.getInstance().sendToConsole("Robot Disabled.");
    		// Restart disabled
			disabledBegin = false;
	    	RobotAutonModes.getInstance().runAutoModeChoosers();
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
				RobotLogger.getInstance().sendToConsole("Robot Disabled Mode Begin.");
				disabledBegin = true;
	    		RobotLogger.getInstance().close();
			}			
 			if(PilotController.getInstance().getResetGyroButton())
 			{ //Reinitialize!
 				RobotDriveBase.getInstance().robotGyro = new Gyro(RobotMap.DRIVE_GYRO_ANALOG_PORT);
 			}
// 	    	SmartDashboard.putNumber("[CURRENT] Autonomous Mode: ", RobotAutonModes.getInstance().getAutoMode());

// 	        SmartDashboard.putNumber("Xbone Controller Right Stick X Axis", PilotController.getInstance().getDriveBaseSteeringStick());
			RobotAutonModes.getInstance().runAutoModeChoosers();
 			RobotAutonModes.getInstance().poll();
 		}
		catch (Exception ex)
		{
			RobotLogger.getInstance().writeErrorToFile("disabledPeriodic()", ex);
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
    		RobotLogger.getInstance().close();
	    	RobotLogger.getInstance().update(false);
	    	//RobotConfigFileReader.getInstance().runRobotFileReader();
	    	RobotToteElevator.getInstance().initAutonomous();
			RobotDriveBase.getInstance().initAutonomous();
			autoBegin = false;
	    	RobotAutonModes.getInstance().init();
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
			RobotAutonModes.getInstance().runRobotAutonMode(RobotAutonModes.getInstance().getAutoMode());
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
    		RobotLogger.getInstance().close();
			RobotLogger.getInstance().update(false);
//	    	RobotConfigFileReader.getInstance().runRobotFileReader();
			RobotDriveBase.getInstance().initTeleop();
			RobotToteElevator.getInstance().initTeleop();
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
			RobotToteElevator.getInstance().runTeleop();
			//RobotVision.getInstance().runTeleop();
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
