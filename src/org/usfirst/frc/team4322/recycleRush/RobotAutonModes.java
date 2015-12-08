package org.usfirst.frc.team4322.recycleRush;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotAutonModes
{

	// Instance for the Singleton Class
	private static RobotAutonModes _instance = null;

	// Initialize auto modes
	private SendableChooser autoChooser = null;
	private int autoMode = 0;
	private int lastAutoMode = 0;
	private static final int DO_NOTHING = 0;
	private static final int DRIVE_BACKWARD_LANDFILL_TO_AUTO = 10;
	private static final int DRIVE_FORWARD_LOADING_TO_AUTO = 20;
	private static final int PULL_TOTE = 30;
	private static final int PULL_CONTAINER = 40;
	private static final int PULL_TOTE_OVER_PLATFORM = 50;
	private static final int PULL_CONTAINER_OVER_PLATFORM = 60;
	private static final int PUSH_TOTE_AND_CONTAINER = 70;
	private static final int STACK_YELLOW_TOTES = 80;
	private static final int PICK_UP_GREY_TOTES = 90;
	// Autonomous Tote Pickup Mode
	public int autoPickUpMode = DRIVE_FORWARD_AND_PICK_UP_TOTE;
	public static final int DRIVE_FORWARD_AND_PICK_UP_TOTE = 10;
	public static final int DRIVE_BACKWARD_TO_AUTO = 20;
	public static final int GET_AWAY_FROM_TOTE = 30;
	public static final int AUTO_COMPLETE = 40;
	int delay = 10;
	int totesGrabbed = 0;

	private boolean grabbingTote;
	// This is the static getInstance() method that provides easy access to the RobotToteElevator singleton class.
	public static RobotAutonModes getInstance()
	{
		// Look to see if the instance has already been created...
		if(_instance == null)
		{
			// If the instance does not yet exist, create it.
			_instance = new RobotAutonModes();
		}
		// Return the singleton instance to the caller.
		return _instance;
	}

	/**
	 * This method displays the SendableChooser buttons
	 * available on the SmartDashboard to select a mode.
	 */
	public void runAutoModeChoosers()
	{
		if(autoChooser == null)
		{
			// Create the mode buttons on the SmartDashboard (does not work with new sfx currently)
			autoChooser = new SendableChooser();
			autoChooser.addDefault("Do Nothing (Auto 1)", new Integer(DO_NOTHING));
			autoChooser.addObject("Drive Backward into Auto (from Landfill)", new Integer(DRIVE_BACKWARD_LANDFILL_TO_AUTO));
			autoChooser.addObject("Drive Forward into Auto (from Loading)", new Integer(DRIVE_FORWARD_LOADING_TO_AUTO));
			autoChooser.addObject("Pick up and Pull Tote", new Integer(PULL_TOTE));
			autoChooser.addObject("Pick up and Pull Container", new Integer(PULL_CONTAINER));
			autoChooser.addObject("Pick up and Pull Tote [over platform]", new Integer(PULL_TOTE_OVER_PLATFORM));
			autoChooser.addObject("Pick up and Pull Container [over platform]", new Integer(PULL_CONTAINER_OVER_PLATFORM));
			autoChooser.addObject("Push Tote AND Container", new Integer(PUSH_TOTE_AND_CONTAINER));
			autoChooser.addObject("Stack Yellow Totes", new Integer(STACK_YELLOW_TOTES));
			autoChooser.addObject("Pick Up Grey Totes", new Integer(PICK_UP_GREY_TOTES));
			SmartDashboard.putData("Auto Mode Chooser", autoChooser);
			RobotLogger.getInstance().sendToConsole("Auto Chooser created.");
		}
		else
		{
			RobotLogger.getInstance().sendToConsole("Auto Chooser already exists.");
		}
	}

	/**
	 * Gets the current autonomous mode.
	 */
	public int getAutoMode()
	{
		// Get the selection
		autoMode = (Integer) autoChooser.getSelected();
		// Only record changes in auto modes
		if(lastAutoMode != autoMode)
		{
			RobotLogger.getInstance().sendToConsole("New Auto Mode: " + autoMode);
			lastAutoMode = autoMode;
		}
		return autoMode;
	}

	/**
	 * Initialize any variables before running
	 * a certain autonomous mode.
	 */
	public void initRobotAutonMode(int autoMode)
	{
		RobotLogger.getInstance().sendToConsole("Initializing Auto Mode: " + autoMode);
		autoPickUpMode = DRIVE_FORWARD_AND_PICK_UP_TOTE;
	}
	public void init()
	{
		initRobotAutonMode(autoMode);
	}

	public void poll()
	{
		autoMode = (Integer) autoChooser.getSelected();
	}
	/**
	 * Run the selected autonomous mode.
	 *
	 * @param autoMode
	 */
	public void runRobotAutonMode(int autoMode)
	{
		SmartDashboard.putNumber("[Running] Auto Mode: ", autoMode);
		switch(autoMode)
		{
			case DO_NOTHING:
				// Do absolutely nothing
				break;
			case DRIVE_BACKWARD_LANDFILL_TO_AUTO:
				// Start at the landfill zone, and drive backwards into the auto zone
				RobotDriveBase.getInstance().driveToAutoZone(false, RobotMap.AUTO_DRIVE_FORWARD_DISTANCE);
				break;
			case DRIVE_FORWARD_LOADING_TO_AUTO:
				// Start at the loading zone, and drive forwards into the auto zone
				RobotDriveBase.getInstance().driveToAutoZone(true, RobotMap.AUTO_DRIVE_FORWARD_DISTANCE);
				break;
			case PULL_TOTE:
				// Start at the loading zone, pick up a tote, drive backward with a tote/container into the auto zone
				runPullToteIntoAuto(false);
				break;
			case PULL_TOTE_OVER_PLATFORM:
				// Start at the loading zone, pick up a tote, drive backward with a tote/container over the platform into the auto zone
				runPullToteIntoAuto(true);
				break;
			case PULL_CONTAINER:
				// Start at the loading zone, pick up a container, drive backward with a container into the auto zone
				runPullToteIntoAuto(false);
				break;
			case PULL_CONTAINER_OVER_PLATFORM:
				// Start at the loading zone, pick up a container, drive backward with a container over the platform into the auto zone
				runPullToteIntoAuto(true);
				break;
			case PUSH_TOTE_AND_CONTAINER:
				// Start at the loading zone, and strafe while pushing a tote & container into the auto zone
				RobotDriveBase.getInstance().strafeToAutoZone(RobotMap.AUTO_DRIVE_STRAFE_DISTANCE);
				break;
			case STACK_YELLOW_TOTES: //?
				// Drive to yellow totes and stack them
				break;
			case PICK_UP_GREY_TOTES: //?
				runGetTotes();
				break;
			default:
				// Do nothing
				autoMode = DO_NOTHING;
				break;
		}
	}

	private void runGetTotes()
	{
		// Pick up a tote or container
		switch(autoPickUpMode)
		{
			case DRIVE_FORWARD_AND_PICK_UP_TOTE:
				double leftDistance = RobotDriveBase.getInstance().proximitySensorLeft.getFilteredDistance();
				double rightDistance = RobotDriveBase.getInstance().proximitySensorRight.getFilteredDistance();
				// Pick up the tote!
				if(RobotDriveBase.getInstance().calculateToteDistanceError(leftDistance, rightDistance) <= RobotMap.PROXIMITY_SENSOR_ERROR_VALUE
						&& rightDistance <= RobotMap.EXPECTED_TOTE_DISTANCE)
				{
					if(totesGrabbed != 0)
					{
						if(delay == 10) RobotToteElevator.getInstance().runAuto(false);

						if(delay == 0) autoPickUpMode = DRIVE_BACKWARD_TO_AUTO;
						else delay--;
					}
					else if(!grabbingTote)
					{

						RobotToteElevator.getInstance().startAutoLift();
						grabbingTote = true;
					}
				}
				// We need to keep driving
				else if(!grabbingTote)
				{
					RobotDriveBase.getInstance().autoDriveToTote();
				}
				break;
			case DRIVE_BACKWARD_TO_AUTO:
				// Drive backward to auto zone
				RobotDriveBase.getInstance().driveToAutoZone(true, RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER_OVER_PLATFORM);
				// Now we've gotta get away from the tote
				if(RobotDriveBase.getInstance().getAutoDriveDistanceTraveled() >= RobotMap.AUTO_DRIVE_FORWARD_DISTANCE)
				{
					RobotDriveBase.getInstance().driveEncoder.reset();
					autoPickUpMode = GET_AWAY_FROM_TOTE;
				}
				break;
			case GET_AWAY_FROM_TOTE:
				// If the distance traveled is less than 10 inches, move away!
				if(RobotDriveBase.getInstance().getAutoDriveDistanceTraveled() < RobotMap.BACK_AWAY_FROM_TOTE_DISTANCE)
				{
					RobotDriveBase.getInstance().getAwayFromTote(true);
				}
				// Otherwise, we're done
				else
				{
					autoPickUpMode = AUTO_COMPLETE;
				}
			case AUTO_COMPLETE:
				// Turn off robot drive
				//RobotDriveBase.getInstance().shutdownRobotDrive();
				break;
			default:
				break;
		}
		if(Math.abs(RobotToteElevator.getInstance().getCLError()) < RobotMap.ELEVATOR_ALLOWED_CLOSED_LOOP_ERROR && autoPickUpMode >= DRIVE_BACKWARD_TO_AUTO)
		{
			if(totesGrabbed != 0)
			{
				RobotToteElevator.getInstance().brake();
				RobotLogger.getInstance().sendToConsole("Braking!!!");
			}
			else
			{
				totesGrabbed++;
				grabbingTote = false;
			}
		}
	}

	public void runPullToteIntoAuto(boolean platform)
	{
		RobotLogger.getInstance().sendToConsole("Current Closed Loop Error is: %f.\n", RobotToteElevator.getInstance().getCLError());
		// Pick up a tote or container
		switch(autoPickUpMode)
		{
			case DRIVE_FORWARD_AND_PICK_UP_TOTE:
				double leftDistance = RobotDriveBase.getInstance().proximitySensorLeft.getFilteredDistance();
				double rightDistance = RobotDriveBase.getInstance().proximitySensorRight.getFilteredDistance();
				// Pick up the tote!
				if(RobotDriveBase.getInstance().calculateToteDistanceError(leftDistance, rightDistance) <= RobotMap.PROXIMITY_SENSOR_ERROR_VALUE
						&& rightDistance <= RobotMap.EXPECTED_TOTE_DISTANCE)
				{
					if(delay == 10) RobotToteElevator.getInstance().runAuto(false);

					else if(delay == 0) autoPickUpMode = DRIVE_BACKWARD_TO_AUTO;
					else delay--;
				}
				// We need to keep driving
				else
				{
					RobotDriveBase.getInstance().autoDriveToTote();
				}
				break;
			case DRIVE_BACKWARD_TO_AUTO:
				// Drive backward to auto zone
				RobotDriveBase.getInstance().driveToAutoZone(true, platform ? RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER_OVER_PLATFORM : RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER);
				// Now we've gotta get away from the tote
				if(RobotDriveBase.getInstance().getAutoDriveDistanceTraveled() >= RobotMap.AUTO_DRIVE_FORWARD_DISTANCE)
				{
					RobotDriveBase.getInstance().driveEncoder.reset();
					autoPickUpMode = GET_AWAY_FROM_TOTE;
				}
				break;
			case GET_AWAY_FROM_TOTE:
				// If the distance traveled is less than 10 inches, move away!
				if(RobotDriveBase.getInstance().getAutoDriveDistanceTraveled() < RobotMap.BACK_AWAY_FROM_TOTE_DISTANCE)
				{
					RobotDriveBase.getInstance().getAwayFromTote(true);
				}
				// Otherwise, we're done
				else
				{
					autoPickUpMode = AUTO_COMPLETE;
				}
			case AUTO_COMPLETE:
				// Turn off robot drive
				RobotDriveBase.getInstance().shutdownRobotDrive();
				break;
			default:
				break;
		}
		if(Math.abs(RobotToteElevator.getInstance().getCLError()) < RobotMap.ELEVATOR_ALLOWED_CLOSED_LOOP_ERROR && autoPickUpMode >= DRIVE_BACKWARD_TO_AUTO)
		{
			RobotToteElevator.getInstance().brake();
			RobotLogger.getInstance().sendToConsole("Braking!!!");
		}
	}
	
	public void runPullContainerIntoAuto(boolean platform)
	{
		RobotLogger.getInstance().sendToConsole("Current Closed Loop Error is: %f.\n", RobotToteElevator.getInstance().getCLError());
		// Pick up a tote or container
		switch(autoPickUpMode)
		{
			case DRIVE_FORWARD_AND_PICK_UP_TOTE:
				double leftDistance = RobotDriveBase.getInstance().proximitySensorLeft.getFilteredDistance();
				double rightDistance = RobotDriveBase.getInstance().proximitySensorRight.getFilteredDistance();
				// Pick up the tote!
				if(RobotDriveBase.getInstance().calculateToteDistanceError(leftDistance, rightDistance) <= RobotMap.PROXIMITY_SENSOR_ERROR_VALUE
						)
				{
					if(delay == 10) RobotToteElevator.getInstance().runAuto(true);

					else if(delay == 0) autoPickUpMode = DRIVE_BACKWARD_TO_AUTO;
					else delay--;
				}
				// We need to keep driving
				else
				{
					RobotDriveBase.getInstance().autoDriveToTote();
				}
				break;
			case DRIVE_BACKWARD_TO_AUTO:
				// Drive backward to auto zone
				RobotDriveBase.getInstance().driveToAutoZone(true, platform ? RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER_OVER_PLATFORM : RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER);
				// Now we've gotta get away from the tote
				if(RobotDriveBase.getInstance().getAutoDriveDistanceTraveled() >= RobotMap.AUTO_DRIVE_FORWARD_DISTANCE)
				{
					RobotDriveBase.getInstance().driveEncoder.reset();
					autoPickUpMode = GET_AWAY_FROM_TOTE;
				}
				break;
			case GET_AWAY_FROM_TOTE:
				// If the distance traveled is less than 10 inches, move away!
				if(RobotDriveBase.getInstance().getAutoDriveDistanceTraveled() < RobotMap.BACK_AWAY_FROM_TOTE_DISTANCE)
				{
					RobotDriveBase.getInstance().getAwayFromTote(true);
				}
				// Otherwise, we're done
				else
				{
					autoPickUpMode = AUTO_COMPLETE;
				}
			case AUTO_COMPLETE:
				// Turn off robot drive
				RobotDriveBase.getInstance().shutdownRobotDrive();
				break;
			default:
				break;
		}
		if(Math.abs(RobotToteElevator.getInstance().getCLError()) < RobotMap.ELEVATOR_ALLOWED_CLOSED_LOOP_ERROR && autoPickUpMode >= DRIVE_BACKWARD_TO_AUTO)
		{
			RobotToteElevator.getInstance().brake();
			RobotLogger.getInstance().sendToConsole("Braking!!!");
		}
	}
}
