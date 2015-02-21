package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class PilotController {

	private static PilotController _instance = null;
	private XboxController controller = null;
    
	static PilotController getInstance()
	{
		if(_instance == null)
		{
			_instance = new PilotController();
			_instance.controller = new XboxController(RobotMap.PILOT_CONTROLLER_JOYSTICK_PORT);
		}
		return _instance;
	}
	
	public boolean getResetGyroButton()
	{
		return controller.getAButton();
	}
	public boolean getAutoModeSelectDown()
	{
		return controller.getXButton();
	}
	public boolean getAutoModeSelectUp()
	{
		return controller.getYButton();
	}
	public double getDriveBaseStrafingStick()
	{
		return controller.getX(Hand.kLeft);
	}
	public double getDriveBaseSteeringStick()
	{
		return controller.getX(Hand.kRight);
	}
	public double getDriveBaseThrottleStick()
	{
		return controller.getY(Hand.kLeft);
	}
	public boolean getSlideDriveLiftButton()
	{
		return controller.getBumper(Hand.kLeft);
	}
	public boolean getNorth()
	{
		return controller.getNorth();
	}
		public boolean getSouth()
	{
		return controller.getSouth();
	}
}
