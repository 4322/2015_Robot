package org.usfirst.frc.team4322.recycleRush;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.net.URI;
import java.text.SimpleDateFormat;

import edu.wpi.first.wpilibj.*;

/**
 * @NOTE: The following methods are used in this class:
 * 		getInstance() - gets the instance for the class (Singleton method)
 * 		update() - opens and updates the file system [FileWriter and BufferedWriter] ONLY when the system is initially closed, then sets closed to false
 * 		writeToFile(String) - writes message to log file ONLY when the system is open
 * 		writeErrorToFile(String, Throwable) - writes exception to log file
 * 		sendToConsole(String) - writes message to system output (Riolog) AND to the log file
 * 		addFileToZip(File, String) - sends a file to a zip folder
 * 		CurrentReadable_DateTime() - gets the current date and time
 * 		getString(Throwable) - gets a string out of a throwable
 * 		close() - closes the FileWriter and BufferedWriter
 */

/**
 *
 * @author FRC4322
 */

public class RobotLogger
{

	// Instance for Singleton class
	private static RobotLogger _instance = null;
	// Instance for Driver Station
	private DriverStation m_ds = DriverStation.getInstance();
	// Instances for the log files
	public final String logFolder = "/home/lvuser/logs/";
	public final String LOG_FILE = "FRC4322InitLog.txt",
			Robot_Disabled_Log = "FRC4322DisabledLog.txt",
			Robot_Auto_Log = "FRC4322AutoLog.txt",
			Robot_Teleop_Log = "FRC4322TeleopLog.txt",
			Robot_Test_Log = "FRC4322TestLog.txt";
	private File oldLog = null;
	private FileWriter fw = null;
	private BufferedWriter bw = null;
	private PrintWriter pw = null;
	private boolean closed = true;

	// Instances for ZIP File
	public Map<String, String> env = new HashMap<>();
	private final String LOGS_ZIP_FILE = "/home/lvuser/logs/FRC4322Logs.zip";

	// Get Date Format
	private static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//	private static final SimpleDateFormat FMSFormat = new SimpleDateFormat("MM/dd-hh:mm");

	// Constants for file
	private final long MAX_FILE_LENGTH = 10485760; //10MB, 1 MiB = 1024^2 or 2^20
	// http://highscalability.com/blog/2012/9/11/how-big-is-a-petabyte-exabyte-zettabyte-or-a-yottabyte.html <-- must visit

	 // This is the static getInstance() method that provides easy access to the RobotLogger singleton class.
	public static RobotLogger getInstance()
	{
		// Look to see if the instance has already been created...
        if(_instance == null)
        {
            // If the instance does not yet exist, create it.
            _instance = new RobotLogger();
        }
        // Return the singleton instance to the caller.
        return _instance;
	}
	
	private String getProperLogFile()
	{
		String file = logFolder + LOG_FILE;
//		if(m_ds.isFMSAttached())
//		{
//			return logFolder + FMSFormat.format(Calendar.getInstance().getTime()) + "MatchLog.txt";
//		}
		if (m_ds.isDisabled())
			return file = logFolder + Robot_Disabled_Log;
		if (m_ds.isAutonomous())
			file = logFolder + Robot_Auto_Log;
		if (m_ds.isOperatorControl())
			file = logFolder + Robot_Teleop_Log;
		if (m_ds.isTest())
			file = logFolder + Robot_Test_Log;
		// Default is initLog
		return file;
	}
	
	public void update(boolean zip)
	{
		if(closed)
		{
			try
			{
				// Get the correct file
				oldLog = new File(getProperLogFile());
				
				// Get the directory
				if(!oldLog.getParentFile().exists())
				{
					oldLog.getParentFile().mkdirs();
				}
				
				// If the file exists & the length is too long, send it to ZIP
				if(oldLog.exists())
				{
					if(oldLog.length() > MAX_FILE_LENGTH && zip)
					{
						addFileToZip(oldLog, LOGS_ZIP_FILE);
						// Create a new .txt file, and rename it
						oldLog.createNewFile();
						File newFile = new File(getProperLogFile().replace(".txt", "") + " [" + CurrentReadable_DateTime() + "]" + ".txt");
						oldLog.renameTo(newFile);
					}
				}
				// If log file does not exist, create it
				else
				{
					//oldLog.createNewFile();
					if(!oldLog.createNewFile()) 
						System.out.println("****************ERROR IN CREATING FILE: " + oldLog + " ***********");
				}
				closed = false;
			}
			catch (IOException ex)
			{
				writeErrorToFile("RobotLogger.update()", ex);
			}
			sendToConsole("Successfully updated logging file system.");
		}
		else
		{
			System.out.println("Failed to update file system.");
		}
	}
	/*
	 * If there already is a file, write the data to it.
	 * If there is not, create the file.
	 * If the file is too big, add it to a ZIP file.
	 */
	private void writeToFile(final String msg, Object... args)
	{
		try
		{
			if (!closed)
			{
				// Get the correct file
				oldLog = new File(getProperLogFile());
				
				// If file does not exist, create it.
				if (!oldLog.exists())
				{
					oldLog.createNewFile();
					System.out.println("Creating new file: " + getProperLogFile());
				}
				// Write data using buffered writer; enhances IO operations
				fw = new FileWriter(oldLog, true);
				bw = new BufferedWriter(fw);
				pw = new PrintWriter(bw);
			}
			pw.format(msg,args);
		}
		catch (IOException e)
		{
			System.out.println("IOException caught while writing to file: " + e);
		}
		finally
		{
			try
			{
				if(bw != null)
				{
					fw.flush();
					bw.flush();
					pw.flush();
				}				
			}
			catch (IOException ex)
			{
				System.out.println("IOException caught while flushing file: " + ex);
			}
		}
	}

	// Writes the throwable error to the .txt log file
	public void writeErrorToFile(final String method, final Throwable t)
	{
		if (closed)
		{
			update(false);
		}
		String msg = "\nException in " + method + ": " + getString(t);
		System.err.println(msg);
		writeToFile(msg);
	}
	
	//Writes throwable error to DS.
	public void writeErrorToDS(final String message, final Throwable thrown)
	{
		DriverStation.reportError("\nException in " + message + getString(thrown), false);
	}
	// Sends message to console and .txt log file
	public void sendToConsole(String thisMessage, Object... args)
	{
		// Prevent different threads from interrupting log entries
		synchronized (System.out)
		{
			// Output logging messages to the console with a standard format
			String datetimeFormat = "\n [" + CurrentReadable_DateTime() + "] - Robot4322: ";
			System.out.format(datetimeFormat + thisMessage+"\n", args);
			// Output logging messages to a .txt log file
			writeToFile(datetimeFormat + thisMessage+"\n",args);
		}
	}

	// Adds given file to given ZIP Folder
	public void addFileToZip(File file, String zipfile)
	{
		//TODO If there are more than 10 files in ZIP, delete them
		try
		{
			// In Java 7, we get to use the ZipFileSystem!
	        env.put("create", "true");

	        /* Get the uniform resource identifier, &
	         * locate file system by using the syntax
	         * defined in java.net.JarURLConnection.
	         */
	        URI uri = URI.create("jar:file:" + zipfile);
	        
	       
	        /* The ZipFileSystem treats jars and ZIPs as a file
	         * system, allowing the user to manipulate the contents
	         * in the ZIP as one would in a folder.
	         */ 
	        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env))
	        {
	        	// Get the log file
	            Path externalLogFile = Paths.get(file.getPath().replace(".txt", "") + " [" + CurrentReadable_DateTime() + "]" + ".txt" );
	            // Get the path the log file will be put into
	            Path pathInZipfile = zipfs.getPath(file.getName());
	            // Copy the log file into the ZIP, replace if necessary
	            Files.copy(externalLogFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
	            // Delete the log file
	            Files.delete(externalLogFile);
	        }
	        catch (FileSystemException e)
	        {
	        	writeErrorToFile("FileSystem, addFileToZip()", e);
	        }
		}
		catch (IOException e)
		{
			writeErrorToFile("addFileToZip()", e);
		}
	}

	// Gets the date in yyyy-MM-dd format
	public static String CurrentReadable_DateTime()
	{
		return sdf_.format(Calendar.getInstance().getTime());
	}

	// Creates a string out of a throwable
	public static String getString(final Throwable e)
	{
		String retValue = null;
		StringWriter sw = null;
		PrintWriter pw = null;
		try
		{
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			retValue = sw.toString();
		}
		finally
		{
			try
			{
				if (pw != null)
				{
					pw.close();
				}
				if (sw != null)
				{
					sw.close();
				}
			}
			catch (IOException ignore)
			{
				System.out.println("IOException caught while closing String/Print Writer: " + ignore);
			}
		}
		return retValue;
	}

	public void close()
	{
		if (closed)
			return;
		try
		{
			if(bw != null)
			{
				fw.flush();
				bw.flush();
				pw.flush();
				fw.close();
				bw.close();
				pw.close();
				closed = true;
			}
		}
		catch (IOException ex)
		{
			System.out.println("IOException caught while closing log files: " + ex);
		}
	}
}