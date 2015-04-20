package org.usfirst.frc.team4322.recycleRush;
import edu.wpi.first.wpilibj.*;
import java.util.Arrays;

public class ProximitySensor extends SensorBase {
	
	private int count = 0;
	private int countMax = 11;
	private double currentMedian = 0;
	private double sensorValues[] = new double[countMax];
	private double sortedSensorValues[] = new double[countMax];
	private AnalogInput sensorPort = null;
	
	
	//methods
	public ProximitySensor(int port)
	{
		sensorPort = new AnalogInput(port);
		}
	
    double GetVoltage()
    {
        return sensorPort.getAverageVoltage();
    }
	
    public void feedSensorBuffer()
    {
		sensorValues[count++]=Math.pow(GetVoltage(), -0.867) * 539.98 / 100;
    	if(count>=countMax) count = 0;	
    }
    
	public double getFilteredDistance()
	{
		sortedSensorValues = Arrays.copyOf(sensorValues, countMax);
		Arrays.sort(sortedSensorValues);
		currentMedian = sortedSensorValues[(int)Math.floor(countMax/2)];
		return currentMedian;
	}
	
	public double getInstantDistance()
	{
		return Math.pow(GetVoltage(), -0.867) * 539.98 / 100;
	}
}
