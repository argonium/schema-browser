package io.miti.schema.cache;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DBMetadata
{
  /** The lastrun string from the input file. */
  public static String lastRun = null;
  
  
  /**
   * Return the lastrun date as a date string.
   * 
   * @return the lastrun date as a date string
   */
  public static String getLastRunData()
  {
    if ((lastRun == null) || (lastRun.length() < 1))
    {
      return null;
    }
    
    long date = 0L;
    try
    {
      date = Long.parseLong(lastRun);
    }
    catch (Exception e)
    {
      date = 0L;
      System.err.println("Error parsing date of " + lastRun);
    }
    
    if (date <= 0L)
    {
      return null;
    }
    
    // Format the date
    final SimpleDateFormat sdf =
        new SimpleDateFormat("'Schema generated on' MMMM dd, yyyy 'at' hh:mm:ss aa");
    String dateStr = sdf.format(new Date(date));
    return dateStr;
  }
}
