package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class CoPilotController {

	private static CoPilotController _instance = null;
	private XboxController controller = null;
	private RobotControlPanel controlPanel = null;
	
	static CoPilotController getInstance()
	{
		if(_instance == null)
		{
			_instance = new CoPilotController();
			_instance.controller = new XboxController(RobotMap.COPILOT_CONTROLLER_JOYSTICK_PORT);
			_instance.controlPanel = new RobotControlPanel(RobotMap.COPILOT_CONTROL_PANEL_PORT);
		}
		return _instance;
	}
	
	public boolean getElevatorSetPointUpButton()
	{
		return controller.getYButton() || controlPanel.getRawButton(4);
	}
	public boolean getElevatorSetPointDownButton()
	{
		return controller.getAButton() || controlPanel.getRawButton(2);
	}
	public double getElevatorDriveStick()
	{
		// You can ONLY control the elevator while you are NOT driving
		if(PilotController.getInstance().isPilotDriving)
		{
			return controller.getY(Hand.kLeft);
		}
		else
		{
			return 0.0;
		}
	}
	public boolean getElevatorTiltButton()
	{
		return controller.getBumper(Hand.kLeft) || controlPanel.getRawButton(1);
	}
	public boolean getAutoEmergencyOff()
	{
		return controller.getStart();
	}
	public boolean getElevatorSetPoint0Button()
	{
		return (controlPanel.getPOV() == 0);
	}
	public boolean getElevatorSetPoint1Button()
	{
		return (controlPanel.getPOV() == 90);
	}
	public boolean getElevatorSetPoint2Button()
	{
		return (controlPanel.getPOV() == 180);
	}
	public boolean getElevatorSetPoint3Button()
	{
		return controlPanel.getRawButton(3);
	}
	public boolean getElevatorSetPoint4Button()
	{
		return (controlPanel.getPOV() == 270);
	}
	public boolean getElevatorSetPointContainerButton()
	{
		return (controlPanel.getPOV() == 45);
	}
	public int getPOV()
	{
		return controlPanel.getPOV();
	}
	public boolean getStackButton()
	{
		return controller.getXButton() || controlPanel.getRawButton(5);
	}

	
	/**
	 * Driver transfer, only happens when driver holds down right trigger button
	 * 
	 * The current drive profile is (false):
	 * -Steering is right stick X axis
	 * -Strafing is left stick X axis
	 * -Throttle is left stick Y axis
	 */
	public double getDriveBaseStrafingStick()
	{
		if(PilotController.getInstance().useAlternateDriveProfile)
		{
			return controller.getX(Hand.kRight);
		}
		else
		{
			return controller.getX(Hand.kLeft);
		}
	}
	public double getDriveBaseSteeringStick()
	{
		if(PilotController.getInstance().useAlternateDriveProfile)
		{
			return controller.getX(Hand.kLeft);
		}
		else
		{
			return controller.getX(Hand.kRight);
		}
	}
	public double getDriveBaseThrottleStick()
	{
		if(PilotController.getInstance().useAlternateDriveProfile)
		{
			return controller.getY(Hand.kRight);
		}
		else
		{
			return controller.getY(Hand.kLeft);
		}
	}
	public boolean getSlideDriveLiftButton()
	{
		return controller.getBumper(Hand.kLeft);
	}
	public boolean getReloadConfigButton()
	{
		return controller.getBumper(Hand.kRight);
	}
}