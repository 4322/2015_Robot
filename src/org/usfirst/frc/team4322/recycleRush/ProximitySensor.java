package org.usfirst.frc.team4322.recycleRush;
import edu.wpi.first.wpilibj.*;

public class ProximitySensor extends SensorBase {
	
	private AnalogInput sensorPort = null;
	
	public ProximitySensor(int port)
	{
		sensorPort = new AnalogInput(port);
	}
	
    double GetVoltage()
    {
        return sensorPort.getAverageVoltage();
    }
	
	public double getDistance()
	{
		return Math.pow(GetVoltage(), -0.867) * 539.98 / 100;
	}
}
