
package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class RobotMain extends IterativeRobot {
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	
    public void robotInit() {
    	CameraServer camera = CameraServer.getInstance();
    	camera.setQuality(50);
    	camera.startAutomaticCapture("cam0");
    	RobotDriveBase.getInstance().initRobotDrive();
    	RobotLogger.getInstance().sendToConsole("Robot Successfully Started.");
    }
    
    @Override
	public void disabledInit() {
		// TODO Auto-generated method stub
		super.disabledInit();
	}
    
    @Override
	public void autonomousInit() {
		// TODO Auto-generated method stub
		super.autonomousInit();
		RobotDriveBase.getInstance().initAutonomous();
	}
    
    /**
     * This function is called periodically during autonomous
     */
    
    public void autonomousPeriodic() {
    	RobotDriveBase.getInstance().runAutonomous();
    }

    /**
     * This function is called right before operator control
     */
    
    public void teleopInit() {
    	RobotDriveBase.getInstance().initTeleop();
    }

    /**
     * This function is called periodically during operator control
     */
    
    public void teleopPeriodic() {
    	RobotDriveBase.getInstance().runTeleop();
    }
  
    @Override
	public void disabledPeriodic() {
		// TODO Auto-generated method stub
		super.disabledPeriodic();
	}
    
    public void testInit() {
    	TestRobot.getInstance().init();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	TestRobot.getInstance().run();
    }
    
}
