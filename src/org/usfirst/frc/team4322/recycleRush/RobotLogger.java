/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team4322.recycleRush;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author FRC4322
 */

public class RobotLogger {

    // Instance for Singleton class
    static private RobotLogger _instance = null;

    // Instances for the log file
    private FileOutputStream logfile = null;
    private final String LOG_FILE = "var\\log\\FRC4322Log.txt"; //fix file path
    private File oldLog = null;
    private boolean closed = true;
    
    // Instances for Zip File
    private FileInputStream in = null;
    private FileOutputStream out = null;
    private ZipOutputStream newZip = null;
    private final String ZIP_FILE = "var\\log\\FRC4322Logs.zip";
    
    // Get Date Format
    private static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");
    
    // Constants for file
    private final long MAX_FILE_LENGTH = 1073741824; //1 GiB = 1024^3 or 2^30
    //http://highscalability.com/blog/2012/9/11/how-big-is-a-petabyte-exabyte-zettabyte-or-a-yottabyte.html <-- must visit
    private final byte[] textFormat = ("\n [" + timeToString(true) + "] - Robot4322: ").getBytes();

    public static RobotLogger getInstance() {
        if (_instance == null) {
            _instance = new RobotLogger();
        }

        return _instance;
    }

    /*
     * If there already is a file, write the data to it.
     * If there is not, create the file.
     * If the file is too big, add it to a ZIP file.
     *
     * TODO If zip file already exists, add new file to it.
     */
    public void writeToFile(String msg) {
        if(!closed) {
            try {
                // Get the default log file
                oldLog = new File(LOG_FILE);
                // If it exists, append it. If not, create it.
                if (oldLog.exists()) {
                    // Get the file length in bytes
                    long fileLength = oldLog.length();
                    // If the file length is below the max, add the msg to it.
                    if (fileLength < MAX_FILE_LENGTH) {
                        // Get the appendable file
                        logfile = new FileOutputStream(oldLog, true);
                        // Write the message to the log
                        logfile.write(textFormat);
                        logfile.write(msg.getBytes());
                    } else { // File length is too long: add it to a zip.
                        // Open an OutputStream
                        out = new FileOutputStream(ZIP_FILE);
                        // Open a ZipOutputStream
                        newZip = new ZipOutputStream(out);
                        // Prepare a ZipEntry
                        newZip.putNextEntry(new ZipEntry(oldLog.getName()));

                        // Write the file data in chunks
                        in = new FileInputStream(oldLog);
                        //newZip.write(Files.readAllBytes(Paths.get("/var/log/FRC4322Log.txt")));
                        byte[] chardata = new byte[1024];
                        int bytesRead = in.read(chardata);
                        do {
                            newZip.write(chardata);
                        } while (bytesRead > 0);
                        // Close the zip entry
                        newZip.closeEntry();
                        // Close the ZipOutputStream
                        newZip.close();
                        // Close the OutputStream
                        out.close();

                        // Create a new txt file
                        logfile = new FileOutputStream(oldLog, false);
                        // Write the message to the log
                        logfile.write(textFormat);
                        logfile.write(msg.getBytes());
                    }
                } else {
                    // Create a new txt file
                    logfile = new FileOutputStream(oldLog, false);
                    // Write the message to the log
                    logfile.write(textFormat);
                    logfile.write(msg.getBytes());
                }
            } catch (IOException e) {
            } finally {
                try {
                    if (logfile != null) {
                        logfile.close();
                        closed = true;
                    }
                } catch (IOException e) {
                }
            }
        } //if the file is closed, you can't write to it
    }

    // Make doubly certain the file is closed
    public void close() {
        if (closed) {
            return;
        }
        try {
            logfile.close();
            closed = true;
        } catch (IOException e) {
        }
    }

    public void sendToConsole(String thisMessage) {
        // Prevent different threads from interrupting log entries
        synchronized (System.out) {
            // Output logging messages to the console with a standard format
            System.out.println(timeToString(true) + " - Robot4322: " + thisMessage);
            // Output logging messages to a .txt log file
            closed = false;
            writeToFile(thisMessage);
        }
    }

    private String timeToString(boolean date) {
        // Create a time stamp that looks like 12:34:56.999
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        int s = c.get(Calendar.SECOND);
        int ms = c.get(Calendar.MILLISECOND);
        String msFormat = Integer.toString(ms);
        if (ms < 10) {
            msFormat = "00" + msFormat;
        } else if (ms < 100) {
            msFormat = "0" + msFormat;
        }
        // If the user wants a date, format is yyyy-MM-dd
        String t = (date ? "<" + CurrentReadable_Date() + "> " : "") + (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s + "." + msFormat;
        return t;
    }
    
    public static String CurrentReadable_Date() {
        return sdf_.format(Calendar.getInstance().getTime());
    }
    
}