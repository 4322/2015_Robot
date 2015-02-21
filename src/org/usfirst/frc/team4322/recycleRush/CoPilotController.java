package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class CoPilotController {

	private static CoPilotController _instance = null;
	private XboxController controller = null;
	
	static CoPilotController getInstance()
	{
		if(_instance == null)
		{
			_instance = new CoPilotController();
			_instance.controller = new XboxController(RobotMap.COPILOT_CONTROLLER_JOYSTICK_PORT);
		}
		return _instance;
	}
	
	public boolean getElevatorSetPointUpButton()
	{
		return controller.getAButton();
	}
	public boolean getElevatorSetPointDownButton()
	{
		return controller.getYButton();
	}
	public double getElevatorDriveStick()
	{
		return controller.getY(Hand.kLeft);
	}
	public boolean getElevatorTiltButton()
	{
		return controller.getBumper(Hand.kLeft);
	}
	public boolean getAutoEmergencyOff()
	{
		return controller.getStart();
	}
}