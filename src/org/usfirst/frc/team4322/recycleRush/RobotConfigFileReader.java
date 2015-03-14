package org.usfirst.frc.team4322.recycleRush;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.*;
public class RobotConfigFileReader
{
	private static RobotConfigFileReader _instance = null;
	private static Pattern arrayFinder = Pattern.compile("\\{\\s*([^}]+)\\s*\\}");
    public final String CONFIG_FILE = "/home/lvuser/robotConfig.ini";
    private static Map<Class<?>,Method> primitiveMap = new HashMap<Class<?>,Method>();
    static {
	try {
		primitiveMap.put(boolean.class, Boolean.class.getMethod("parseBoolean",String.class));
		primitiveMap.put(byte.class, Byte.class.getMethod("parseByte",String.class));
		primitiveMap.put(short.class, Short.class.getMethod("parseShort",String.class));
		primitiveMap.put(int.class, Integer.class.getMethod("parseInt",String.class));
		primitiveMap.put(long.class, Long.class.getMethod("parseLong",String.class));
		primitiveMap.put(float.class, Float.class.getMethod("parseFloat",String.class));
		primitiveMap.put(double.class, Double.class.getMethod("parseDouble",String.class));
	} catch (NoSuchMethodException | SecurityException ex) {
		RobotLogger.getInstance().writeErrorToFile("Exception caught in RobotConfigFileReader", ex);
	}  
    }
    public static RobotConfigFileReader getInstance()
    {
		// Look to see if the instance has already been created
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotConfigFileReader();
        }
        // Return the singleton instance to the caller.
        return _instance;
    }
    
    /**
     * This method only reads values that may be changed during the
     * course of the competition. Please avoid adding port / CAN
     * addresses and other fixed values, so as to limit the errors
     * from reading from this file. Variable values include those
     * used for autonomous, driving, and elevator control.
     */
    public void runRobotFileReader()
    {
    	// Maybe only read the files necessary for the competition mode..
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(CONFIG_FILE));
        }
        catch (IOException ex)
        {
            System.out.println("Failed to load robotConfig.ini");
            return;
        }
    	try
    	{
//    		RobotMap.LAST_BUILD_TIME = p.getProperty("LAST_BUILD_TIME");
//    		/**
//    		 * AUTO MODE
//    		 */
//    		// Autonomous Driving Limits
//    		RobotMap.AUTONOMOUS_DRIVE_SPEED = Double.parseDouble(p.getProperty("AUTONOMOUS_DRIVE_SPEED"));
//	    	RobotMap.AUTONOMOUS_REVERSE_SPEED = Double.parseDouble(p.getProperty("AUTONOMOUS_REVERSE_SPEED"));
//	    	RobotMap.BACK_AWAY_FROM_TOTE_SPEED = Double.parseDouble(p.getProperty("BACK_AWAY_FROM_TOTE_SPEED"));
//	    	// P constant for autonomous driving
//	    	RobotMap.AUTONOMOUS_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("AUTONOMOUS_P_CONTROL_VALUE_GYRO"));
//	    	// Autonomous Drive Encoder Values
//	    	RobotMap.BACK_AWAY_FROM_TOTE_DISTANCE = Integer.parseInt(p.getProperty("BACK_AWAY_FROM_TOTE_DISTANCE"));
//	    	RobotMap.AUTO_DRIVE_FORWARD_DISTANCE = Integer.parseInt(p.getProperty("AUTO_DRIVE_FORWARD_DISTANCE"));
//	    	RobotMap.AUTO_DRIVE_STRAFE_DISTANCE = Integer.parseInt(p.getProperty("AUTO_DRIVE_STRAFE_DISTANCE"));
//	    	RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER = Integer.parseInt(p.getProperty("AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER"));
//	    	RobotMap.AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER_OVER_PLATFORM = Integer.parseInt(p.getProperty("AUTO_DRIVE_BACKWARD_WITH_TOTE_CONTAINER_OVER_PLATFORM"));
//
//	    	/**
//	    	 * TELEOP DRIVE MODE
//	    	 */
//    		// Acceleration Ramps
//	    	RobotMap.THROTTLE_RAMP = Double.parseDouble(p.getProperty("THROTTLE_RAMP"));
//	    	RobotMap.STEERING_RAMP = Double.parseDouble(p.getProperty("STEERING_RAMP"));
//	    	RobotMap.STRAFE_RAMP = Double.parseDouble(p.getProperty("STRAFE_RAMP"));
//	    	// Drive Power Limits
//	    	RobotMap.THROTTLE_LIMIT = Double.parseDouble(p.getProperty("THROTTLE_LIMIT"));
//	    	RobotMap.STEERING_LIMIT = Double.parseDouble(p.getProperty("STEERING_LIMIT"));
//	    	RobotMap.STRAFE_LIMIT = Double.parseDouble(p.getProperty("STRAFE_LIMIT"));
//	    	// Controller deadband
//	    	RobotMap.STEERING_DEADBAND = Double.parseDouble(p.getProperty("STEERING_DEADBAND"));
//	    	RobotMap.THROTTLE_DEADBAND = Double.parseDouble(p.getProperty("THROTTLE_DEADBAND"));
//	    	// P constants for driving
//	    	RobotMap.TELEOP_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("TELEOP_P_CONTROL_VALUE_GYRO"));
//	    	RobotMap.TELEOP_STRAFE_P_CONTROL_VALUE_GYRO = Double.parseDouble(p.getProperty("TELEOP_STRAFE_P_CONTROL_VALUE_GYRO"));
//	    	// Accelerometer Deadband values per axis
//	    	RobotMap.ACCELEROMETER_DEADBAND_X = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_X"));
//	    	RobotMap.ACCELEROMETER_DEADBAND_Y = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_Y"));
//	    	RobotMap.ACCELEROMETER_DEADBAND_Z = Double.parseDouble(p.getProperty("ACCELEROMETER_DEADBAND_Z"));
//	    	RobotMap.ACCELEROMETER_DEADBAND_COUNTDOWN = Integer.parseInt(p.getProperty("ACCELEROMETER_DEADBAND_COUNTDOWN"));
//	    	// dual rates (crawl mode) //motor power is divided by this value
//	    	RobotMap.CREEP_DRIVE_SPEED = Double.parseDouble(p.getProperty("CREEP_DRIVE_SPEED"));
//	    	RobotMap.THROTTLE_DUAL_RATE = Double.parseDouble(p.getProperty("THROTTLE_DUAL_RATE"));
//	    	RobotMap.STEERING_DUAL_RATE = Double.parseDouble(p.getProperty("STEERING_DUAL_RATE"));
//	    	RobotMap.STRAFE_DUAL_RATE = Double.parseDouble(p.getProperty("STRAFE_DUAL_RATE"));
//	    	
//	    	/**
//	    	 * TELEOP AUTO ALIGNMENT
//	    	 */
//	    	// Auto Alignment Speeds
//	    	RobotMap.AUTO_ALIGN_STRAFE_SPEED = Double.parseDouble(p.getProperty("AUTO_ALIGN_STRAFE_SPEED"));
//	    	RobotMap.AUTO_ALIGN_DRIVE_FORWARD_SPEED = Double.parseDouble(p.getProperty("AUTO_ALIGN_DRIVE_FORWARD_SPEED"));
//	    	RobotMap.AUTO_ALIGN_STRAFE_RAMP = Double.parseDouble(p.getProperty("AUTO_ALIGN_STRAFE_RAMP"));
//	    	// Auto Alignment Distances
//	    	RobotMap.STRAFE_ENCODER_TOTE_ALIGNMENT_DRIVE_DISTANCE = Double.parseDouble(p.getProperty("STRAFE_ENCODER_TOTE_ALIGNMENT_DRIVE_DISTANCE"));
//	    	
//	    	// Infrared Sensor Error
//	    	RobotMap.PROXIMITY_SENSOR_ERROR_VALUE = Double.parseDouble(p.getProperty("PROXIMITY_SENSOR_ERROR_VALUE"));
//	    	RobotMap.EXPECTED_TOTE_DISTANCE = Double.parseDouble(p.getProperty("EXPECTED_TOTE_DISTANCE"));
//	    	RobotMap.MAX_EXPECTED_TOTE_DISTANCE = Integer.parseInt(p.getProperty("MAX_EXPECTED_TOTE_DISTANCE"));
//	    	
//	    	/**
//	    	 * TELEOP PID Control
//	    	 */
//	    	// Tote Elevator PID Constants
//	    	RobotMap.ELEVATOR_P_VALUE = Double.parseDouble(p.getProperty("ELEVATOR_P_VALUE"));
//	    	RobotMap.ELEVATOR_I_VALUE = Double.parseDouble(p.getProperty("ELEVATOR_I_VALUE"));
//	    	RobotMap.ELEVATOR_D_VALUE = Double.parseDouble(p.getProperty("ELEVATOR_D_VALUE"));
//	    	RobotMap.ELEVATOR_F_VALUE = Integer.parseInt(p.getProperty("ELEVATOR_F_VALUE"));
//	    	RobotMap.ELEVATOR_IZONE_VALUE = Integer.parseInt(p.getProperty("ELEVATOR_IZONE_VALUE"));
//	    	RobotMap.ELEVATOR_RAMPRATE_VALUE = Integer.parseInt(p.getProperty("ELEVATOR_RAMPRATE_VALUE"));
//	    	RobotMap.ELEVATOR_ANALOG_STICK_DEADBAND = Double.parseDouble(p.getProperty("ELEVATOR_ANALOG_STICK_DEADBAND"));
//	    	// Tote Elevator positions
//	    	RobotMap.ELEVATOR_JOYSTICK_DEADBAND = Double.parseDouble(p.getProperty("ELEVATOR_JOYSTICK_DEADBAND"));
//	    	RobotMap.ELEVATOR_POSITIONS[0] = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_0"));
//	    	RobotMap.ELEVATOR_POSITIONS[1] = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_1"));
//	    	RobotMap.ELEVATOR_POSITIONS[2] = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_2"));
//	    	RobotMap.ELEVATOR_POSITIONS[3] = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_3"));
//	    	RobotMap.ELEVATOR_POSITIONS[4] = Integer.parseInt(p.getProperty("ELEVATOR_POSITION_4"));
//	    	// Tote Stack positions
//	    	RobotMap.ELEVATOR_STACK_POSITIONS[0] = Integer.parseInt(p.getProperty("ELEVATOR_STACK_POSITION_0"));
//	    	RobotMap.ELEVATOR_STACK_POSITIONS[1] = Integer.parseInt(p.getProperty("ELEVATOR_STACK_POSITION_1"));
//	    	RobotMap.ELEVATOR_STACK_POSITIONS[2] = Integer.parseInt(p.getProperty("ELEVATOR_STACK_POSITION_2"));
//	    	RobotMap.ELEVATOR_STACK_POSITIONS[3] = Integer.parseInt(p.getProperty("ELEVATOR_STACK_POSITION_3"));
//	    	RobotMap.ELEVATOR_STACK_POSITIONS[4] = Integer.parseInt(p.getProperty("ELEVATOR_STACK_POSITION_4"));
//	    	// Container pickup positions
//	    	RobotMap.ELEVATOR_CONTAINER_POSITIONS[0] = Integer.parseInt(p.getProperty("ELEVATOR_CONTAINER_POSITION_1"));
//	    	RobotMap.ELEVATOR_CONTAINER_POSITIONS[1] = Integer.parseInt(p.getProperty("ELEVATOR_CONTAINER_POSITION_2"));
    		Enumeration<?> enumeration = p.propertyNames();
    	    while (enumeration.hasMoreElements()) {
    	        String key = (String) enumeration.nextElement();
    	        String value = p.getProperty(key);
    	        Field current = null;
    	        try
    	        {
    	        current = RobotMap.class.getField(key);
    	        }
    	        catch(NoSuchFieldException ex)
    	        {
    	        	RobotLogger.getInstance().sendToConsole("The field \"%s\" doesnt exist in RobotMap!", key);
    	        	RobotLogger.getInstance().writeErrorToFile("RobotConfigFileReader.runRobotFileReader()", ex);
    	        	continue;
    	        }
    	        if(current.getType().isArray())
    	        {
    	        	Matcher m = arrayFinder.matcher(value);
    	        	m.find();
    	        	String[] arrayValues = m.group().split("[\\s,]+");
    	        	arrayValues[0] = arrayValues[0].replace("{", "");
    	        	arrayValues[arrayValues.length-1] = arrayValues[arrayValues.length-1].replace("}", "");
    	        	Object elementArray = Array.newInstance(current.getType().getComponentType(), arrayValues.length);
    	        	try
    	        	{
    	        		for(int i = 0; i< arrayValues.length;i++)
    	        		{
    	        			if(current.getType().getComponentType() == String.class)
    	        			{
    	        				Array.set(elementArray, i,arrayValues[i]);
    	        			}
    	        			else
    	        			{
    	        				Array.set(elementArray, i, primitiveMap.get(current.getType().getComponentType()).invoke(null, arrayValues[i]));
    	        			}
    	        		}
    	        	}
    	        	catch(InvocationTargetException ex)
    	        	{
    	        		RobotLogger.getInstance().sendToConsole("Unable to set property \"%s\" to \"%s\". Target type was %s[].", key,value,current.getType().getComponentType().getSimpleName());
    	        		RobotLogger.getInstance().writeErrorToFile("RobotConfigFileReader.runRobotFileReader()", ex);
    	        		continue;
    	        	}
	        		current.set(null,elementArray);
    	        }
    	        else
    	        {
    	        	
    	        	Object finalValue = null;
    	        	if(current.getType() == String.class)
    	        	{
    	        		finalValue = value;
    	        	}
    	        	else
    	        	{
    	        		try
    	        		{
    	        			finalValue = primitiveMap.get(current.getType()).invoke(null, value);
    	        		}
    	        		catch(InvocationTargetException ex)
    	        		{
    	        			RobotLogger.getInstance().sendToConsole("Unable to set property \"%s\" to \"%s\". Target type was %s.", key,value,current.getType().getSimpleName());
    	        			RobotLogger.getInstance().writeErrorToFile("RobotConfigFileReader.runRobotFileReader()", ex);
    	        			continue;
    	        		}
    	        	}
    	        	current.set(null, finalValue);       
    	        }
    	    }
    	} 
    	catch (IllegalArgumentException ex) 
    	{
    		RobotLogger.getInstance().writeErrorToFile("Exception caught in runRobotFileReader()", ex);
		}
    	catch (IllegalAccessException ex) 
    	{
			RobotLogger.getInstance().writeErrorToFile("Exception caught in runRobotFileReader()", ex);
		} 
    	catch (SecurityException ex	)
    	{
			RobotLogger.getInstance().writeErrorToFile("Exception caught in runRobotFileReader()", ex);
		}
   }
}