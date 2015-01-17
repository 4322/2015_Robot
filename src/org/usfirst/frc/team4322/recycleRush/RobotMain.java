
package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.IterativeRobot;

		//FRC4322 - Changes last made: 1.17.15 @ 2:51pm

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
    	RobotDriveBase.getInstance().initRobotDrive();
    	RobotLogger.getInstance().open();
    	RobotLogger.getInstance().sendToConsole("Robot Successfully Started.");
    }
    
    @Override
	public void disabledInit() {
		// TODO Auto-generated method stub
		super.disabledInit();
		RobotLogger.getInstance().close();
	}
    
    @Override
	public void autonomousInit() {
		// TODO Auto-generated method stub
		super.autonomousInit();
		RobotLogger.getInstance().open();
	}
    
    /**
     * This function is called periodically during autonomous
     */
    
    public void autonomousPeriodic() {
    	
    }

    /**
     * This function is called right before operator control
     */
    
    public void teleopInit() {
    	RobotDriveBase.getInstance().initTeleop();
    	RobotLogger.getInstance().open();
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
    	//RobotLogger.getInstance().open();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	TestRobot.getInstance().run();
    }
    
}
