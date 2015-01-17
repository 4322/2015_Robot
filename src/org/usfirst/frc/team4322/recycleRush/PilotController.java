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
	
	public boolean getAButton()
	{
		return controller.getAButton();
	}
	public boolean getBButton()
	{
		return controller.getBButton();
	}
	public boolean getXButton()
	{
		return controller.getXButton();
	}
	public boolean getYButton()
	{
		return controller.getYButton();
	}
	public double getLeftJoystickXAxis()
	{
		return controller.getX(Hand.kLeft);
	}
	public double getRightJoystickXAxis()
	{
		return controller.getX(Hand.kRight);
	}
	public double getLeftJoystickYAxis()
	{
		return controller.getY(Hand.kLeft);
	}
	public double getRightJoystickYAxis()
	{
		return controller.getY(Hand.kRight);
	}
	public double getDPadAxis()
	{
		return controller.getTwist();
	}
	public boolean getLeftBumper()
	{
		return controller.getBumper(Hand.kLeft);
	}
	public boolean getStartButton()
	{
		return controller.getStart();
	}
	public boolean getBackButton()
	{
		return controller.getBack();
	}
	public int getPOV()
	{
		return controller.getPOV();
	}
	public boolean getNorth()
	{
		return controller.getNorth();
	}
	public boolean getNortheast()
	{
		return controller.getNortheast();
	}
	public boolean getEast()
	{
		return controller.getEast();
	}
	public boolean getSoutheast()
	{
		return controller.getSoutheast();
	}
	public boolean getSouth()
	{
		return controller.getSouth();
	}
	public boolean getSouthwest()
	{
		return controller.getSouthwest();
	}
	public boolean getWest()
	{
		return controller.getWest();
	}
	public boolean getNorthwest()
	{
		return controller.getNorthwest();
	}
}
