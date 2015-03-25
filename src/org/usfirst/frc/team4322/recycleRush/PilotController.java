package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class PilotController {

	private static PilotController _instance = null;
	private XboxController controller = null;
	public boolean isPilotDriving = true;
	
	/** The Alternate Drive Profile swaps driving sticks
	 * Steering will be left stick X axis
	 * Throttle will be right stick Y axis
	 * Strafing will be right stick X axis
	 */
	// TODO: This value could be selected in a configuration file or using a SmartDashboard selector
	public boolean useAlternateDriveProfile = true; // Seth's preference
    
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

	public double getDriveBaseStrafingStick()
	{
		if(useAlternateDriveProfile)
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
		if(useAlternateDriveProfile)
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
		if(useAlternateDriveProfile)
		{
			return controller.getY(Hand.kRight);
		}
		else
		{
			return controller.getY(Hand.kLeft);
		}
	}
	
	
	public boolean getDualRateButton()
	{
		return controller.getBumper(Hand.kRight);
	}
	public boolean getSlideDriveLiftButton()
	{
		return controller.getBumper(Hand.kLeft);
	}
	
	
	public boolean getAutoAlignButton()
	{
		return controller.getYButton();
	}
	public boolean getQuickAutoAlignButton()
	{
		return controller.getBButton();
	}
	public boolean getDriveAndAutoLiftButton()
	{
		return controller.getXButton();
	}
	
	

	public void setUseAlternateDriveProfile(boolean useAlternateDriveProfile)
	{
		this.useAlternateDriveProfile = useAlternateDriveProfile;
	}
	
	
	
	public boolean getBuddyDriverTransferButton()
	{
		isPilotDriving = false; //CoPilot is driving now
		useAlternateDriveProfile = false; // Steven's preference
		return controller.getTrigger(Hand.kRight);
	}
	
	
}
