package org.usfirst.frc.team4322.recycleRush;

public class DriveForward extends RobotDriveBase
{

	public DriveForward()
	{
		double correction = -1 * robotGyro.getAngle() * RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO;
    	double distance = driveEncoder.getDistance();
    	if(distance <= RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE)
    	{
    		robotDrive.drive(RobotMap.AUTONOMOUS_DRIVE_SPEED, correction);
    	}
    	else if(distance <= (RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE + 4) && distance >= (RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE - 4))
    	{
    		robotDrive.drive(0, 0);
    	}
    	else if(distance >= (RobotMap.ENCODER_AUTONOMOUS_DRIVE_DISTANCE + 4))
    	{
    		robotDrive.drive(RobotMap.AUTONOMOUS_REVERSE_SPEED, 0);
    	}
	}
}
