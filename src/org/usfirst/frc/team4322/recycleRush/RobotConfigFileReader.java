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
    	RobotLogger.getInstance().sendToConsole("Started Config Update.");
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
    	RobotLogger.getInstance().sendToConsole("Finished Config Update.");
   }
}