package io.miti.schema.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Utility methods.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Utility
{  
  /**
   * Number of milliseconds per minute.
   */
  private static final long MSECS_PER_MIN  = 60000L;
  
  /**
   * Number of milliseconds per hour.
   */
  private static final long MSECS_PER_HOUR = MSECS_PER_MIN * 60L;
  
  /**
   * Number of milliseconds per day.
   */
  private static final long MSECS_PER_DAY  = MSECS_PER_HOUR * 24L;
  
  /**
   * Number of milliseconds per week.
   */
  private static final long MSECS_PER_WEEK  = MSECS_PER_DAY * 7L;
  
  /**
   * Number of milliseconds per year.
   */
  private static final long MSECS_PER_YEAR = 31556926000L;
  
  /**
   * Whether to read input files as a stream.
   */
  private static boolean readAsStream = false;
  
  /**
   * The line separator for this OS.
   */
  private static String lineSep = null;
  
  /**
   * The default background color for the forms.
   */
  private static final Color backgroundFormColor;
  
  /**
   * The string description of the default background color.  Used
   * by the HTML-based forms.
   */
  private static final String backgroundFormColorDesc;
  
  /**
   * Declare the list of illegal Windows filenames.
   */
  private static final String[] illegalNames = {".", "..",
    "CON", "PRN", "AUX", "CLOCK$", "NUL", "COM0", "COM1", "COM2", "COM3",
    "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT0", "LPT1", "LPT2",
    "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
  
  /**
   * Static block to initialize at startup.
   */
  static
  {
    backgroundFormColor = new Color(240, 240, 240);
    backgroundFormColorDesc = "\"rgb(240, 240, 240)\"";
  }
  
  
  /**
   * Default constructor.
   */
  private Utility()
  {
    super();
  }
  
  
  /**
   * Return the application name.
   * 
   * @return the application name
   */
  public static String getAppName()
  {
    return "SchemaBrowser";
  }
  
  
  /**
   * Return the line separator for this OS.
   * 
   * @return the line separator for this OS
   */
  public static String getLineSeparator()
  {
    // See if it's been initialized
    if (lineSep == null)
    {
      lineSep = "\r\n"; // System.getProperty("line.separator");
    }
    
    return lineSep;
  }
  
  
  /**
   * Whether to read content files as a stream.  This
   * is used when running the program as a standalone
   * jar file.
   * 
   * @param useStream whether to read files via a stream
   */
  public static void readFilesAsStream(final boolean useStream)
  {
    readAsStream = useStream;
  }
  
  
  /**
   * Whether to read content files as a stream.
   * 
   * @return whether to read content files as a stream
   */
  public static boolean readFilesAsStream()
  {
    return readAsStream;
  }
  
  
  /**
   * Sleep for the specified number of milliseconds.
   * 
   * @param time the number of milliseconds to sleep
   */
  public static void sleep(final long time)
  {
    try
    {
      Thread.sleep(time);
    }
    catch (InterruptedException e)
    {
      Logger.error(e);
    }
  }
  
  
  /**
   * Return whether this number is effectively zero.
   * 
   * @param value the input value
   * @return whether this is effectively zero
   */
  public static boolean isZero(final double value)
  {
    return ((Math.abs(value) < Constants.FLOATPRECISION));
  }
  
  
  /**
   * Return whether the two double values are equal.
   * 
   * @param dValue1 value 1
   * @param dValue2 value 2
   * @return whether values 1 and 2 are equal
   */
  public static boolean isEqual(final double dValue1, final double dValue2)
  {
    return (Math.abs(dValue1 - dValue2) < Constants.FLOATPRECISION);
  }
  
  
  /**
   * Return a colon-separated string as two integers, in a Point.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @return a point containing the two integers
   */
  public static Point getStringAsPoint(final String sInput,
                                       final int defaultValue)
  {
    // Declare the return value
    Point pt = new Point(defaultValue, defaultValue);
    
    // Check the input
    if (sInput == null)
    {
      return pt;
    }
    
    // Find the first colon
    final int colonIndex = sInput.indexOf(':');
    if (colonIndex < 0)
    {
      // There is no colon, so save the whole string
      // as the x value and return
      pt.x = Utility.getStringAsInteger(sInput, defaultValue, defaultValue);
      return pt;
    }
    
    // Get the two values.  Everything before the colon
    // is returned in x, and everything after the colon
    // is returned in y.
    pt.x = Utility.getStringAsInteger(sInput.substring(0, colonIndex),
                                      defaultValue, defaultValue);
    pt.y = Utility.getStringAsInteger(sInput.substring(colonIndex + 1),
                                      defaultValue, defaultValue);
    
    // Return the value
    return pt;
  }
  
  
  /**
   * Convert a string into an integer.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as an integer
   */
  public static int getStringAsInteger(final String sInput,
                                       final int defaultValue,
                                       final int emptyValue)
  {
    // This is the variable that gets returned
    int value = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return emptyValue;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return emptyValue;
    }
    
    // Convert the number
    try
    {
      value = Integer.parseInt(inStr);
    }
    catch (NumberFormatException nfe)
    {
      value = defaultValue;
    }
    
    // Return the value
    return value;
  }
  
  
  /**
   * Convert a string into a floating point number.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as a float
   */
  public static float getStringAsFloat(final String sInput,
                                       final float defaultValue,
                                       final float emptyValue)
  {
    // This is the variable that gets returned
    float fValue = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return emptyValue;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return emptyValue;
    }
    
    // Convert the number
    try
    {
      fValue = Float.parseFloat(inStr);
    }
    catch (NumberFormatException nfe)
    {
      fValue = defaultValue;
    }
    
    // Return the value
    return fValue;
  }
  
  
  /**
   * Convert a string into a double.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as a double
   */
  public static double getStringAsDouble(final String sInput,
                                         final double defaultValue,
                                         final double emptyValue)
  {
    // This is the variable that gets returned
    double value = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return emptyValue;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return emptyValue;
    }
    
    // Convert the number
    try
    {
      value = Double.parseDouble(inStr);
    }
    catch (NumberFormatException nfe)
    {
      value = defaultValue;
    }
    
    // Return the value
    return value;
  }
  
  
  /**
   * Returns the input value as a string with the default number of
   * digits in the mantissa.
   * 
   * @param value the input value
   * @return the score as a String
   */
  public static String toString(final double value)
  {
    return Utility.toString(value, 5);
  }
  
  
  /**
   * Returns the input value as a string with a specified number of
   * digits in the mantissa.
   * 
   * @param value the input value
   * @param nMantissaDigits the number of digits to the right of the decimal
   * @return the score as a String
   */
  public static String toString(final double value, final int nMantissaDigits)
  {
    // Set reasonable bounds for the precision
    final int nPrecision = Math.min(10, Math.max(0, nMantissaDigits));
    
    // Construct the StringBuffer to hold the formatting string.
    // This is necessary since the format string is variable due
    // to the nMantissaDigits value.  The "-" means the string
    // is left-justified.
    StringBuilder buf = new StringBuilder(10);
    buf.append("%-5.").append(Integer.toString(nPrecision))
       .append("f");
    
    // Construct the string and return it to the caller.
    // Trim it since the output string may have trailing
    // spaces.
    final String result = String.format(buf.toString(), value).trim();
    
    // Save the length
    final int len = result.length();
    if (len < 3)
    {
      return result;
    }
    
    // Check the position of any decimals
    final int decPos = result.lastIndexOf('.');
    if ((decPos < 0) || (decPos >= (len - 2)))
    {
      return result;
    }
    
    // Trim any trailing zeros, except one just after the decimal
    int index = len - 1;
    buf = new StringBuilder(result);
    while ((index > (decPos + 1)) && (result.charAt(index) == '0'))
    {
      // Delete the character
      buf.deleteCharAt(index);
      
      // Decrement the index
      --index;
    }
    
    return buf.toString();
  }
  
  
  /**
   * Turn the string into a title, using HTML.  Intended for JLabel strings.
   * 
   * @param msg the title string
   * @return the input string as an HTML title
   */
  public static String makeTitle(final String msg)
  {
    // Check the input
    if (msg == null)
    {
      return msg;
    }
    
    // Declare the string builder
    StringBuilder sb = new StringBuilder(100);
    
    // Build the string
    sb.append("<html><body><font color=\"#494590\" size=+1>")
      .append(msg)
      .append("</font></body></html>");
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Return whether the string is null or has no length.
   * 
   * @param msg the input string
   * @return whether the string is null or has no length
   */
  public static boolean isStringEmpty(final String msg)
  {
    return ((msg == null) || (msg.length() == 0));
  }
  
  
  /**
   * Strip the html tags from a string.
   * 
   * @param string the string with html tags
   * @return the string without html tags
   */
  public static String stripHtmlFromString(final String string)
  {
    String result = null;
    
    result = string.replaceAll("&nbsp;", "");
    return result.replaceAll("\\<.*?>", "");
  }
  
  
  /**
   * Make the application compatible with Apple Macs.
   */
  public static void makeMacCompatible()
  {
    // Set the system properties that a Mac uses
    System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
    System.setProperty("apple.awt.brushMetalLook", "true");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.showGrowBox", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                       getAppName());
  }
  
  
  /**
   * Verify the user has the minimum version of the JVM
   * needed to run the application.
   * 
   * @return whether the JVM is the minimum supported version
   */
  public static boolean hasRequiredJVMVersion()
  {
    // The value that gets returned
    boolean status = true;
    
    // Check the version number
    status = SystemInfo.isJava5orHigher();
    if (!status)
    {
      // This will hold the error string
      StringBuilder sb = new StringBuilder(100);
      
      sb.append("This application requires Java 1.5 (5.0) or later")
        .append(".\nYour installed version of Java is ")
        .append(SystemInfo.getCurrentJavaVersionAsString())
        .append('.');
      
      // Show an error message to the user
      JOptionPane.showMessageDialog(null, sb.toString(),
                         "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Return the status code
    return status;
  }
  
  
  /**
   * Center the application on the screen.
   * 
   * @param comp the component to center on the screen
   */
  public static void centerOnScreen(final java.awt.Component comp)
  {
    // Get the size of the screen
    Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    
    // Determine the new location of the window
    int x = (screenDim.width - comp.getSize().width) / 2;
    int y = (screenDim.height - comp.getSize().height) / 2;
    
    // Move the window
    comp.setLocation(x, y);
  }
  
  
  /**
   * Return the cube root of the specified value.
   * 
   * @param value the input value
   * @return the value's cube root
   */
  public static double getCubeRoot(final double value)
  {
    return (Math.pow(value, 0.33333333333));
  }
  
  
  /**
   * Return an AWT image based on the filename.
   * 
   * @param fileName the image file name
   * @return the AWT image
   */
  public static java.awt.Image loadImageByName(final String fileName)
  {
    // The icon that gets returned
    Image icon = null;
    
    // Load the resource
    URL url = Utility.class.getResource(fileName);
    if (url != null)
    {
      icon = Toolkit.getDefaultToolkit().getImage(url);
    }
    else
    {
      Logger.error("Error: Unable to open the file " + fileName);
      return null;
    }
    
    // Return the AWT image
    return icon;
  }
  
  
  /**
   * Load a content resource as a file.
   * 
   * @param loc the name of the icon file
   * @return the file
   */
  public static File loadContentAsFile(final String loc)
  {
    // The file that gets returned
    File file = null;
    
    // Load the resource
    URL url = Utility.class.getResource(loc);
    if (url != null)
    {
      try
      {
        file = new File(url.toURI());
      }
      catch (URISyntaxException urise)
      {
        Logger.error(urise);
      }
    }
    else
    {
      Logger.error("Error: Unable to open the file " + loc);
      return null;
    }
    
    return file;
  }
  
  
  /**
   * Store the properties object to the filename.
   * 
   * @param filename name of the output file
   * @param props the properties to store
   */
  public static void storeProperties(final String filename,
                                     final Properties props)
  {
    // Write the properties to a file
    FileOutputStream outStream = null;
    try
    {
      // Open the output stream
      outStream = new FileOutputStream(filename);
      
      // Save the properties
      props.store(outStream, "Properties file for Olympus Console");
      
      // Close the stream
      outStream.close();
      outStream = null;
    }
    catch (FileNotFoundException fnfe)
    {
      Logger.error("File not found: " + fnfe.getMessage());
    }
    catch (IOException ioe)
    {
      Logger.error("IOException: " + ioe.getMessage());
    }
    finally
    {
      if (outStream != null)
      {
        try
        {
          outStream.close();
        }
        catch (IOException ioe)
        {
          Logger.error("IOException: " + ioe.getMessage());
        }
        
        outStream = null;
      }
    }
  }
  
  
  /**
   * Load the properties object.
   * 
   * @param filename the input file name
   * @return the loaded properties
   */
  public static Properties getProperties(final String filename)
  {
    // The object that gets returned
    Properties props = null;
    
    InputStream propStream = null;
    try
    {
      // Open the input stream as a file
      propStream = new FileInputStream(filename);
      
      // Check for an error
      if (propStream != null)
      {
        // Load the input stream
        props = new Properties();
        props.load(propStream);
        
        // Close the stream
        propStream.close();
        propStream = null;
      }
    }
    catch (IOException ioe)
    {
      props = null;
    }
    finally
    {
      // Make sure we close the stream
      if (propStream != null)
      {
        try
        {
          propStream.close();
        }
        catch (IOException e)
        {
          Logger.error(e.getMessage());
        }
        
        propStream = null;
      }
    }
    
    // Return the properties
    return props;
  }
  
  
  /**
   * Write out the contents of a hash map of mission tasks.
   * 
   * @param <T> the type of object in the hashmap
   * @param root the hashmap to print
   */
  public static <T> void printHashList(final HashMap<String, List<T>> root)
  {
    // Check if it's empty
    if (root == null)
    {
      Logger.info("[Null]");
      return;
    }
    
    // Iterate over the set of data in the input hash map
    for (Entry<String, List<T>> group : root.entrySet())
    {
      Logger.info(group.getKey());
      for (T task : group.getValue())
      {
        Logger.info("  " + task.toString());
      }
    }
  }
  
  
  /**
   * Sort the point array in ascending order.
   *
   * @param p array of point variable
   */
  public static void selectionSort(final Point[] p) 
  {
    for (int i = 0; i < p.length - 1; i++) 
    {
      int minI = i;
      for (int j = i + 1; j < p.length; j++) 
      {
        double
          currX = p[j].getX(),
          minX = p[minI].getX();
        if (currX < minX)
        {  
          minI = j;
        }  
      }
      Point temp = p[i];
      p[i] = p[minI];
      p[minI] = temp;
    }
  }  //selectionSort
  
  
  /**
   * Get a clean layer name from a given layer.
   * 
   * @param name the layer name
   * @return the clean layer name
   */
  public static String cleanLayerName(final String name)
  {
    // Strip the 'map_files/' directory out of the file name
    if (name.startsWith("map_files"))
    {
      return name.substring(10);
    }
    else
    {
      return name;
    }
  }
  
  
  /**
   * Get the specified date as a string.
   * 
   * @param time the date and time
   * @return the date as a string
   */
  public static String getDateTimeString(final long time)
  {
    // Check the input
    if (time <= 0)
    {
      return "Invalid time (" + Long.toString(time) + ")";
    }
    
    // Convert the time into a Date object
    Date date = new Date(time);
    
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    // Return the date/time as a string
    return formatter.format(date);
  }
  
  
  /**
   * Format the date as a string, using a standard format.
   * 
   * @param date the date to format
   * @return the date as a string
   */
  public static String getDateString(final Date date)
  {
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
    
    if (date == null)
    {
      return formatter.format(new Date());
    }
      
    // Return the date/time as a string
    return formatter.format(date);
  }
  
  
  /**
   * Format the date and time as a string, using a standard format.
   * 
   * @return the date as a string
   */
  public static String getDateTimeString()
  {
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    // Return the date/time as a string
    return formatter.format(new Date());
  }
  
  
  /**
   * Format the date and time as a string, in the form MM/DD/YYYY at HH:MM:SS.
   * 
   * @return the date as a string
   */
  public static String getDateTimeTitleString()
  {
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss");
    
    // Return the date/time as a string
    return formatter.format(new Date());
  }
  
  
  /**
   * Convert a boolean array into a string.  Used for database
   * access and the listRoles and listPrimaries arrays.
   * 
   * @param data the boolean array
   * @return the array as a string
   */
  public static String listToString(final boolean[] data)
  {
    StringBuilder sb = new StringBuilder(data.length);
    for (int i = 0; i < data.length; ++i)
    {
      sb.append((data[i]) ? '1' : '0');
    }
    return sb.toString();
  }
  
  
  /**
   * Convert a string into an array of booleans.
   * 
   * @param str the input string
   * @param listData the output array
   */
  public static void stringToBooleanList(final String str,
                                         final boolean[] listData)
  {
    // Check the input for a null or empty string, or no 1's
    if ((str == null) || (str.length() < 1) || (str.indexOf('1') < 0))
    {
      // Fill the array with false
      java.util.Arrays.fill(listData, false);
      return;
    }
    
    // Iterate over the string
    final int len = str.length();
    for (int i = 0; i < 6; ++i)
    {
      // Check if we're before the end of the string
      if (i < len)
      {
        // We are, so convert the character into a boolean
        listData[i] = (str.charAt(i) == '1');
      }
      else
      {
        // We're past the end of the array, so assume false
        listData[i] = false;
      }
    }
  }
  
  
  /**
   * Parse a string of time (in minutes and seconds) and return
   * the time in seconds.  Acceptable input formats are SS, :SS
   * and MM:SS.
   * 
   * @param timeStr the input time string
   * @return the number of seconds in the time
   */
  public static int getMSTimeInSeconds(final String timeStr)
  {
    // Check the input
    if ((timeStr == null) || (timeStr.trim().length() < 1))
    {
      // The input string is invalid
      return 0;
    }
    
    // Trim the string
    final String time = timeStr.trim();
    
    // Check for a colon
    final int colonIndex = time.indexOf(':');
    if (colonIndex < 0)
    {
      // There is no colon, so just parse the string as the
      // number of seconds
      return getStringAsInteger(time, 0, 0);
    }
    else if (colonIndex == 0)
    {
      // There is a colon at the start, so just parse the rest
      // of the string as the number of seconds
      return getStringAsInteger(time.substring(1), 0, 0);
    }
    
    // There is a colon inside the string, so parse the
    // minutes (before) and seconds (after), and then add
    int mins = 60 * (getStringAsInteger(time.substring(0, colonIndex), 0, 0));
    int secs = getStringAsInteger(time.substring(colonIndex + 1), 0, 0);
    return (mins + secs);
  }
  
  
  /**
   * Parse a string of time (in hours and minutes) and return
   * the time in minutes.  Acceptable input formats are MM, :MM
   * and HH:MM.
   * 
   * @param timeStr the input time string
   * @return the number of minutes in the time
   */
  public static int getHMTimeInMinutes(final String timeStr)
  {
    // Check the input
    if ((timeStr == null) || (timeStr.trim().length() < 1))
    {
      // The input string is invalid
      return 0;
    }
    
    // Trim the string
    final String time = timeStr.trim();
    
    // Check for a colon
    final int colonIndex = time.indexOf(':');
    if (colonIndex < 0)
    {
      // There is no colon, so just parse the string as the
      // number of minutes
      return getStringAsInteger(time, 0, 0);
    }
    else if (colonIndex == 0)
    {
      // There is a colon at the start, so just parse the rest
      // of the string as the number of minutes
      return getStringAsInteger(time.substring(1).trim(), 0, 0);
    }
    
    // There is a colon inside the string, so parse the
    // hours (before) and minutes (after), and then add
    // together after multiplying the hours by 60 (to put
    // in minutes)
    int hrs = 60 * (getStringAsInteger(time.substring(0, colonIndex), 0, 0));
    int mins = getStringAsInteger(time.substring(colonIndex + 1), 0, 0);
    return (hrs + mins);
  }
  
  
  /**
   * Return the default background color for forms.
   * 
   * @return the default background color for forms
   */
  public static Color getBackgroundColor()
  {
    return backgroundFormColor;
  }
  
  
  /**
   * Return the default background color description for forms.
   * 
   * @return the default background color description for forms
   */
  public static String getBackgroundColorDesc()
  {
    return backgroundFormColorDesc;
  }
  
  
  /**
   * Print the Swing component hierarchy for a JComponent.
   * 
   * @param comp the parent component
   */
  public static void printComponentHierarchy(final JComponent comp)
  {
    // Check the argument
    if (comp == null)
    {
      System.out.println("The component is null");
      return;
    }
    
    // Print out the hierarhcy
    printComponentChildren(comp, 0);
  }
  
  
  /**
   * Recursive method to print the hierarchy of component children.
   * 
   * @param comp the parent component
   * @param indent the number of spaces to indent
   */
  private static void printComponentChildren(final JComponent comp,
                                             final int indent)
  {
    // Check if we should indent
    if (indent > 0)
    {
      // Print a space for every requested indentation
      for (int i = 0; i < indent; ++i)
      {
        System.out.print(' ');
      }
    }
    
    // Get the component (child) count and print the name of the class
    // and the number of its children
    final int count = comp.getComponentCount();
    System.out.println("Component: " + comp.getClass().getName() + " (" + count + ")");
    
    // Iterate over each child and call this method recursively
    for (int i = 0; i < count; ++i)
    {
      if (comp.getComponent(i) instanceof JComponent)
      {
        printComponentChildren((JComponent) comp.getComponent(i), indent + 2);
      }
    }
  }
  
  /**
   * Top padding.
   * @return 0
   */
  public static int getSpaceAboveTitle()
  {
    return 0;
  }
  
  /**
   * Bottom padding.
   * @return 20
   */
  public static int getSpaceBelowTitle()
  {
    return 20;
  }
  
  
  /**
   * Left padding.
   * @return 10
   */
  public static int getLeftMargin()
  {
    return 20;
  }
  
  /**
   * Set Border of JPanel.
   * @param p panel
   * @param s string 
   */
  public static void setPanelBorder(final JPanel p, final String s)
  {
    p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(s),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
  }
  
  
  /**
   * Perform rot13 on an input string.
   * 
   * @param s the input string to rotate
   * @return the rotated string
   */
  public static String rot13(final String s)
  {
    // Check the input
    if ((s == null) || (s.length() < 1))
    {
      return s;
    }
    
    // Iterate over the input string, rotating any letter by 13
    // characters (wrapping around the alphabet)
    final int len = s.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; ++i)
    {
      // Get the current character and rotate by 13, based on
      // its value
      char c = s.charAt(i);
      if ((c >= 'a') && (c <= 'm'))
      {
        c += 13;
      }
      else if ((c >= 'n') && (c <= 'z'))
      {
        c -= 13;
      }
      else if ((c >= 'A') && (c <= 'M'))
      {
        c += 13;
      }
      else if ((c >= 'N') && (c <= 'Z'))
      {
        c -= 13;
      }
      
      sb.append(c);
    }
    
    return sb.toString();
  }
  
  
  /**
   * Check a string for a word containing a substring.  If found, it replaces
   * that word with the mine name.
   * 
   * @param inStr the input string to check
   * @param subStr the substring to search for
   * @param mineName the name of a mine
   * @return the output string
   */
  public static String replaceWordBySubstring(final String inStr,
                                              final String subStr,
                                              final String mineName)
  {
    // Check if the string contains the substring
    if (inStr.indexOf(subStr) < 0)
    {
      // Substring not found, so return the input string
      return inStr;
    }
    
    // If we reach this point, we know inStr has the substring
    // to match on.  There may or may not be any mines to process.
    
    // Parse inStr based on spaces
    StringTokenizer st = new StringTokenizer(inStr);
    StringBuilder sb = new StringBuilder(100);
    while (st.hasMoreTokens())
    {
      String word = st.nextToken();
      
      // Check if the current word contains the substring
      if (word.contains(subStr))
      {
        // Check for an empty mine name
        if (mineName.length() > 0)
        {
          // Prepend a space if the string already has text
          if (sb.length() > 0)
          {
            sb.append(' ');
          }
          
          // Add the mine name instead of 'word'
          sb.append(mineName);
        }
      }
      else
      {
        // Prepend a space if the string already has text
        if (sb.length() > 0)
        {
          sb.append(' ');
        }
        
        sb.append(word);
      }
    }
    
    // Create the string we'll return
    final String outStr = sb.toString().trim().toUpperCase();
    
    // Check for duplicate phrases at the end
    final int len = outStr.length();
    if ((len >= 24) && (outStr.endsWith(" LIMPET MINE LIMPET MINE")))
    {
      // Return everything before the last 'limpet mine'
      return outStr.substring(0, len - 12);
    }
    
    // Return the generated string
    return outStr;
  }
  
  
  /**
   * Convert the input argument into a string describing the time span.
   * 
   * @param interv the time interval, in milliseconds
   * @return a String describing the time span
   */
  public static String millisToTimeSpan(final long interv)
  {
    // Declare our string buffer
    StringBuffer buf = new StringBuffer(100);
    
    // Check for zero and negative values
    long lMillis = interv;
    if (lMillis <= 0L)
    {
      // The value is either illegal, or zero, so return
      buf.append("0 seconds");
      return buf.toString();
    }
    
    // Get the number of years
    final long lYears = (long) (lMillis / MSECS_PER_YEAR);
    lMillis = lMillis % MSECS_PER_YEAR;
    
    // Get the number of weeks
    final long lWeeks = (long) (lMillis / MSECS_PER_WEEK);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_WEEK;
    
    // Get the number of days
    final long lDays = (long) (lMillis / MSECS_PER_DAY);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_DAY;
    
    // Get the number of hours
    final long lHours = (long) (lMillis / MSECS_PER_HOUR);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_HOUR;
    
    // Get the number of minutes
    final long lMinutes = (long) (lMillis / MSECS_PER_MIN);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_MIN;
    
    // Get the number of seconds
    final float fSeconds = (float) (((float) lMillis) / 1000.0F);
    
    if (lYears > 0L)
    {
      // Add the number and unit
      buf.append(Long.toString(lYears)).append(" year");
      
      // Make the unit plural, if necessary
      if (lYears > 1L)
      {
        buf.append('s');
      }
    }
    
    // Now generate the string. First check if there are any weeks.
    if (lWeeks > 0L)
    {
      // Append a leading comma, if necessary, and then add the number and unit
      if (buf.length() > 0)
      {
        buf.append(", ");
      }
      
      // Add the number and unit
      buf.append(Long.toString(lWeeks)).append(" week");
      
      // Make the unit plural, if necessary
      if (lWeeks > 1L)
      {
        buf.append('s');
      }
    }
    
    // Check if there are any days.
    if (lDays > 0L)
    {
      // Append a leading comma, if necessary, and then add the number and unit
      if (buf.length() > 0)
      {
        buf.append(", ");
      }
      buf.append(Long.toString(lDays)).append(" day");
      
      // Make the unit plural, if necessary
      if (lDays > 1L)
      {
        buf.append('s');
      }
    }
    
    // Check if there are any hours.
    if (lHours > 0L)
    {
      // Append a leading comma, if necessary, and then add the number and unit
      if (buf.length() > 0)
      {
        buf.append(", ");
      }
      buf.append(Long.toString(lHours)).append(" hour");
      
      // Make the unit plural, if necessary
      if (lHours > 1L)
      {
        buf.append('s');
      }
    }
    
    // Check if there are any minutes.
    if (lMinutes > 0L)
    {
      // Append a leading comma, if necessary, and then add the number and unit
      if (buf.length() > 0)
      {
        buf.append(", ");
      }
      buf.append(Long.toString(lMinutes)).append(" minute");
      
      // Make the unit plural, if necessary
      if (lMinutes > 1L)
      {
        buf.append('s');
      }
    }
    
    // Check if there are any seconds.
    if (Float.compare(fSeconds, 0.0F) > 0)
    {
      // Append a leading comma, if necessary
      if (buf.length() > 0)
      {
        buf.append(", ");
      }
      
      // Format it because it's a floating point number
      DecimalFormat df = new DecimalFormat();
      df.setDecimalSeparatorAlwaysShown(false);
      df.setMaximumFractionDigits(3);
      buf.append(df.format((double) fSeconds)).append(" second");
      
      // Make the unit plural, if necessary (if the number is anything but 1.0)
      if (Float.compare(fSeconds, 1.0F) != 0)
      {
        buf.append('s');
      }
    }
    
    // Return the string
    return buf.toString();
  }
  
  
  /**
   * Remove illegal characters from a filename.
   * 
   * @param sText the input filename
   * @return the output filename
   */
  public static String cleanFilename(final String sText)
  {
    // Check the input
    if ((sText == null) || (sText.length() < 1))
    {
      return "a";
    }
    
    // Trim and shorten the string
    String text = sText.trim();
    if (text.length() > 255)
    {
      text = text.substring(0, 255);
    }
    
    // Declare a string of illegal characters in filenames
    final String badChars = "/\\`~!@#$%^&*():;'\"<>?|";
    
    // Iterate over the characters in the input string
    final int len = text.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; ++i)
    {
      // See if this character is in the list of illegal characters
      final char ch = text.charAt(i);
      if (badChars.indexOf(ch) < 0)
      {
        // It's not, so append it
        sb.append(ch);
      }
    }
    
    // Check for no length (all characters were illegal)
    if (sb.length() < 1)
    {
      return "a";
    }
    
    // Check if it matches any illegal filename
    boolean badFound = false;
    String tempName = sb.toString().toUpperCase();
    for (String name : illegalNames)
    {
      // Compare the two strings
      if (tempName.equals(name.toUpperCase()))
      {
        badFound = true;
        break;
      }
    }
    
    // Check for an error
    if (badFound)
    {
      return "a";
    }
    
    // Return the buffer as a string
    return sb.toString();
  }
  
  
  /**
   * Delete the specified directory recursively.
   * 
   * @param dir the directory to delete
   * @return whether it was successful
   */
  public static boolean deleteDir(final File dir)
  {
    if (dir.isDirectory())
    {
      String[] children = dir.list();
      if (children == null)
      {
        return true;
      }
      
      for (int i = 0; i < children.length; i++)
      {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success)
        {
          return false;
        }
      }
    }
    
    // The directory (or file) is now empty so delete it
    return dir.delete();
  }
  
  
  /**
   * Utility method to check for null strings.
   * 
   * @param string
   *          the string to check
   * @return
   * @return the string
   */
  public static String getString(final Object string)
  {
    if (string == null)
    {
      return "";
    }
    else
    {
      return string.toString();
    }
  }
  
  
  /**
   * Return the number of bytes as a descriptive string.
   * 
   * @param numBytes the number of bytes
   * @return a string representation
   */
  public static String getBytesString(final long numBytes)
  {
    // Set up the method variables
    final String[] sizes = {"bytes", "KB", "MB", "GB", "TB", "PB", "ZB", "big"};
    final double divisor = 1024.0;
    double currSize = (double) numBytes;
    
    // Check if we're under 1K
    if (currSize < divisor)
    {
      return String.format("%d %s", numBytes, sizes[0]);
    }
    
    // Keep dividing the number of bytes until we're under 1K
    int index = 0;
    while ((index < sizes.length - 1) && (currSize >= divisor))
    {
      // Increase the index into the array of descriptions
      ++index;
      currSize /= divisor;
    }
    
    // Generate the output string and return it
    return String.format("%.1f %s", currSize, sizes[index]);
  }
  
  
  /**
   * Return the number of bytes as a descriptive string.
   * 
   * @param numBytes the number of bytes
   * @return a string representation
   */
  public static String getShortBytesString(final long numBytes)
  {
    // Set up the method variables
    final String[] sizes = {"B", "K", "M", "G", "T", "P", "Z", "W"};
    final double divisor = 1024.0;
    double currSize = (double) numBytes;
    
    // Check if we're under 1K
    if (currSize < divisor)
    {
      return String.format("%d%s", numBytes, sizes[0]);
    }
    
    // Keep dividing the number of bytes until we're under 1K
    int index = 0;
    while ((index < sizes.length - 1) && (currSize >= divisor))
    {
      // Increase the index into the array of descriptions
      ++index;
      currSize /= divisor;
    }
    
    // Generate the output string and return it
    return String.format("%d%s", ((int) currSize), sizes[index]);
  }
  

  /**
   * Return the array of abbreviations for the 50 states.
   * 
   * @return the array of states' abbreviations
   */
  public static String[] getStateAbbreviations()
  {
    String[] states = {"--", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
        "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN",
        "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", 
        "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", 
        "WA", "WV", "WI", "WY"};
    
    return states;
  }
  
  
  /**
   * Get the extension of a file.
   * 
   * @param f the file
   * @return the extension of the file
   */
  public static String getExtension(final File f)
  {
    String ext = null;
    
    // Check the file
    if (f == null)
    {
      return ext;
    }
    
    String s = f.getName();
    if ((s == null) || (s.length() < 1))
    {
      return ext;
    }
    
    // Grab everything after the last period
    int i = s.lastIndexOf('.');
    if ((i > 0) &&  (i < s.length() - 1))
    {
      ext = s.substring(i+1).toLowerCase();
    }
    
    return ext;
  }
  
  
  /**
   * Initialize the application's Look And Feel with the default
   * for this OS.
   */
  public static void initLookAndFeel()
  {
    // Use the default look and feel
    try
    {
      javax.swing.UIManager.setLookAndFeel(
        javax.swing.UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      Logger.error("Exception: " + e.getMessage());
    }
  }
  
  
  /**
   * Copy text to the clipboard.
   * 
   * @param msg the string to copy to the clipboard
   */
  public static void copyToClipboard(final String msg)
  {
    StringSelection stringSelection = new StringSelection(msg);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  }
}
