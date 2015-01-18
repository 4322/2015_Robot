/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team4322.recycleRush;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.text.SimpleDateFormat;

import edu.wpi.first.wpilibj.*;

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
	private FileOutputStream logfile = null;
	public final String LOG_FILE = "/home/lvuser/FRC4322Log.txt",
			Robot_Disabled_Log = "/home/lvuser/FRC4322Disabled.txt",
			Robot_Auto_Log = "/home/lvuser/FRC4322AutoLog.txt",
			Robot_Teleop_Log = "/home/lvuser/FRC4322TeleopLog.txt",
			Robot_Test_Log = "/home/lvuser/FRC4322TestLog.txt";
	private File oldLog = null;
	private boolean closed = true;

	// Instance for Java Logging Class (in case this logger is bad)
	
	
	// Instances for ZIP File
	private FileInputStream in = null;
	private FileOutputStream out = null;
	private ZipOutputStream newZip = null;
	private final String LOGS_ZIP_FILE = "/home/lvuser/FRC4322Logs.zip";

	// Get Date Format
	private static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");

	// Constants for file
	private final long MAX_FILE_LENGTH = 1073741824; // 1 GiB = 1024^3 or 2^30
	// http://highscalability.com/blog/2012/9/11/how-big-is-a-petabyte-exabyte-zettabyte-or-a-yottabyte.html
	// <-- must visit
	private final byte[] textFormat = ("\n [" + timeToString(true) + "] - Robot4322: ").getBytes();

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

	/*
	 * If there already is a file, write the data to it.
	 * If there is not, create the file.
	 * If the file is too big, add it to a ZIP file.
	 * 
	 * TODO If ZIP file already exists, add new file to it.
	 */
	public void writeToFile(final String msg)
	{
		if (!closed)
		{
			try
			{
				// Get the correct file
				String file = LOG_FILE;
				if (m_ds.isDisabled())
					file = Robot_Disabled_Log;
				if (m_ds.isAutonomous())
					file = Robot_Auto_Log;
				if (m_ds.isOperatorControl())
					file = Robot_Teleop_Log;
				if (m_ds.isTest())
					file = Robot_Test_Log;

				oldLog = new File(file);

				if (oldLog.exists())
				{ // If it exists, append it. If not, create it.
					// Get the file length in bytes
					long fileLength = oldLog.length();
					// If the file length is below the max, add the msg to it.
					if (fileLength < MAX_FILE_LENGTH)
					{
						// Get the appendable file
						logfile = new FileOutputStream(oldLog, true);
					}
					// File length is too long: add it to a zip.
					else
					{
						addFileToZip(oldLog, LOGS_ZIP_FILE);
						// Create a new txt file
						logfile = new FileOutputStream(oldLog, false);
					}
				}
				// File does not exist
				else
				{
					// Create a new txt file
					logfile = new FileOutputStream(oldLog, false);
				}
				// Write the message to the respective log
				logfile.write(textFormat);
				logfile.write(msg.getBytes());
			}
			catch (IOException e)
			{
				// TODO handling
			}
			finally
			{
				try
				{
					if (logfile != null)
					{
						logfile.close();
						closed = true;
					}
				}
				catch (IOException e)
				{
					// TODO handling
				}
			}
		} // if the file is closed, you can't write to it
	}

	// Writes the throwable error to the .txt log file
	public void writeErrorToFile(final String method, final Throwable t)
	{
		if (!closed)
		{
			String msg = "Exception in " + method + ": " + getString(t);
			writeToFile(msg);
		}
	}

	// Sends message to console and .txt log file
	public void sendToConsole(String thisMessage)
	{
		// Prevent different threads from interrupting log entries
		synchronized (System.out)
		{
			// Output logging messages to the console with a standard format
			System.out.println(timeToString(true) + " - Robot4322: " + thisMessage);
			// Output logging messages to a .txt log file
			closed = false;
			writeToFile(thisMessage);
		}
	}

	// Adds given file to given ZIP Folder
	public void addFileToZip(File file, String zipfile)
	{
		try
		{
			// Open an OutputStream
			out = new FileOutputStream(zipfile);
			// Open a ZipOutputStream
			newZip = new ZipOutputStream(out);
			// Prepare a ZipEntry
			newZip.putNextEntry(new ZipEntry(file.getName()));
	
			// Write the file data in chunks
			in = new FileInputStream(file);
			// newZip.write(Files.readAllBytes(Paths.get(oldLog)));
			byte[] chardata = new byte[1024];
			int bytesRead = in.read(chardata);
			do
			{
				newZip.write(chardata);
			} while (bytesRead > 0);
			// Close the zip entry
			newZip.closeEntry();
			// Close the ZipOutputStream
			newZip.close();
			// Close the OutputStream
			out.close();
		}
		catch (FileNotFoundException fnfe)
		{
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	private String timeToString(boolean date)
	{
		// Create a time stamp that looks like 12:34:56.999
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		int ms = c.get(Calendar.MILLISECOND);
		String msFormat = Integer.toString(ms);
		if (ms < 10)
		{
			msFormat = "00" + msFormat;
		}
		else if (ms < 100)
		{
			msFormat = "0" + msFormat;
		}
		// If the user wants a date, format is yyyy-MM-dd
		String t = (date ? "<" + CurrentReadable_Date() + "> " : "") +
				// Time stamp
				(h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s + "." + msFormat;
		return t;
	}

	public static String CurrentReadable_Date()
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
			}
		}
		return retValue;
	}

}