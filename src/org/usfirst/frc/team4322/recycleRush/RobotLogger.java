package org.usfirst.frc.team4322.recycleRush;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.*;

/**
 *
 * @author FRC4322
 */

public class RobotLogger
{
	
	// Instance for the Singleton Class
    static private RobotLogger _instance = null;
    
    // Instances for the log file & writer
    private FileWriter logfile = null;
    private BufferedWriter logWriter = null;
    private File oldLog = null;
    private boolean closed = true;
    
    // Instance for the Singleton Class
    public static RobotLogger getInstance()
    {
        if(_instance == null)
        {
            _instance = new RobotLogger();
            _instance.updateFile();
        }
        
        return _instance;
    }
    
    /*
     * If there already is a file, write the data to it.
     * If there is not, create the file.
     * If the file is too big, add it to a ZIP file.
     */
    
    public void updateFile()
    {
        try
        {
        	oldLog = new File("/var/log/FRC4322.log");
        	if(oldLog.exists())
        	{
        		long fileLength = oldLog.length();
        		if(fileLength < 1073741724)
        		{
        			logfile = new FileWriter("/var/log/FRC4322.log", true);
        			logWriter = new BufferedWriter(logfile);
        		}
        		else
        		{
        			ZipFile logArchive = new ZipFile("/var/log/FRC4322Logs.zip");
        	        InputStream zipData = logArchive.getInputStream(logArchive.getEntry("FRC4322.log"));
        	        logArchive.close();
        	        byte[] chardata = new byte[1673741824];
        	        DataInputStream dataIs = new DataInputStream(zipData);
        	        dataIs.readFully(chardata);
        	        
        	        // Open an OutputStream
        	        FileOutputStream outfile = new FileOutputStream("/var/log/FRC4322Logs.zip");
        	        // Open a ZipOutputStream
        	        ZipOutputStream newZip = new ZipOutputStream(outfile);
        	        // Prepare a ZipEntry
        	        newZip.putNextEntry(new ZipEntry("FRC4322.log"));
        	        // Write the file data
        	        newZip.write(chardata);
        	        newZip.write(Files.readAllBytes(Paths.get("/var/log/frc4322.log")));
        	        // Close the zip entry
        	        newZip.closeEntry();
        	        // Close the ZipOutputStream
        	        newZip.close();
        	        // Close the OutputStream
        	        outfile.close();
        	        
        	        logfile = new FileWriter("/var/log/FRC4322.log", false);
        	        logWriter = new BufferedWriter(logfile);
        		}
        	}
        	else
        	{
        		logfile = new FileWriter("/var/log/FRC4322.log", false);
        		logWriter = new BufferedWriter(logfile);
        	}
        	closed = false;
		}
        catch (IOException e)
        {
			e.printStackTrace();
		}
    }
    
    public void open()
    {
    	if(closed)
    		updateFile();
    }
    
    public void close()
    {
    	if(closed)
    		return;
    	try
    	{
			logWriter.close();
	    	logfile.close();
	    	closed = true;
		}
    	catch (IOException e)
    	{
			e.printStackTrace();
		}
    }
    
    public void sendToConsole(String thisMessage)
    {
    	// Prevent different threads from interrupting log entries
    	synchronized(System.out)
    	{
	    	// Output logging messages to the console with a standard format
			System.out.println(timeToString() + " - Robot4322: " + thisMessage);
    	}
    	
    	try
    	{
    		// Find the file
    		oldLog = new File("/var/log/FRC4322.log");
	    	if(oldLog.exists())
	    	{
	    		// Get the file length in bytes
	    		long fileLength = oldLog.length();
		    	if(fileLength < 1073741724)
				{
					logfile = new FileWriter("/var/log/FRC4322.log", true);
					logWriter = new BufferedWriter(logfile);
				}
		    	// Too big: create a new file
		    	else
		    	{
		    		logfile = new FileWriter("/var/log/FRC4322.log", false);
		    		logWriter = new BufferedWriter(logfile);
		    	}
	    	}
	    	// Create a new file
	    	else
	    	{
	    		logfile = new FileWriter("/var/log/FRC4322.log", false);
	    		logWriter = new BufferedWriter(logfile);
	    	}
	    	// Write data to the file
	    	synchronized(logWriter)
	    	{
	    		try
	    		{	
					logWriter.write(timeToString() + " - Robot4322: " + thisMessage);
					open();
				}
	    		catch (IOException e)
	    		{
					e.printStackTrace();
				}
	    	}
	    	close();
    	}
        catch (IOException e)
        {
			e.printStackTrace();
		}
    }
    
    private String timeToString()
    {
    	// Create a time stamp that looks like 12:34:56.999
	    Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    int h = c.get(Calendar.HOUR_OF_DAY);
	    int m = c.get(Calendar.MINUTE);
	    int s = c.get(Calendar.SECOND);
	    int ms = c.get(Calendar.MILLISECOND);
	    String msFormat = Integer.toString(ms);
	    if(ms < 10) msFormat = "00" + msFormat;
	    else if(ms < 100) msFormat = "0" + msFormat;
	    String t = (h<10? "0": "") + h + ":" + (m<10? "0": "") + m + ":" + (s<10? "0": "") + s + "." + msFormat ;
	    return t;
    }    

}