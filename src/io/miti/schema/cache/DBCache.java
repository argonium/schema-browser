package io.miti.schema.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.miti.schema.dbutil.TableInfo;

public final class DBCache
{
	/** The one instance of this class. */
	private static DBCache instance = null;
	
	/** Map of table name to columns in that table. */
	private Map<String, List<TableInfo>> tables = null;
	
	/** List of just the table names. */
	private List<String> tableNames = null;
	
	/** Map of column name to tables with that column name. */
	private Map<String, List<TableInfo>> columns = null;
	
	/** List of just the column names. */
	private List<String> columnNames = null;
	
	/**
	 * Default constructor.
	 */
	private DBCache()
	{
		super();
	}
	
	
	/**
	 * Return the one instance of this class.
	 * 
	 * @return the one instance of this class
	 */
	public static DBCache getInstance()
	{
		if (instance == null)
		{
			instance = new DBCache();
			instance.initCache();
		}
		
		return instance;
	}
	
	
	/**
	 * Initialize the cache by reading the db info file.
	 */
	private void initCache()
	{
		// Load the data from the xml file
	  tables = new DBFileParser().getTables();
		if (tables == null)
		{
			System.err.println("Error: the table map is null");
		}
		
		// Populate the other variable
		populateColumns();
		tableNames = getAllTables();
		columnNames = getAllColumns();
	}
	
	
	/**
	 * Populate the list of columns with info on the tables their in.
	 */
	private void populateColumns()
	{
	  columns = new HashMap<String, List<TableInfo>>(10);
	  
	  // Iterate over the list of tables
	  for (Entry<String, List<TableInfo>> entry : tables.entrySet())
	  {
	    List<TableInfo> cols = entry.getValue();
	    
	    // Copy the table info to columns
	    for (TableInfo info : cols)
	    {
	      if (columns.containsKey(info.columnName))
	      {
	        columns.get(info.columnName).add(info);
	      }
	      else
	      {
	        List<TableInfo> list = new ArrayList<TableInfo>(5);
	        list.add(info);
	        columns.put(info.columnName, list);
	      }
	    }
	  }
	}
	
	
	/**
	 * Check if it's valid.
	 */
	public boolean isValid()
	{
		return ((tables != null) && (columns != null));
	}
	
	
	/**
	 * Return all table names.
	 * 
	 * @return the list of all table names
	 */
	private List<String> getAllTables()
	{
		if (!isValid())
		{
			return null;
		}
		
		List<String> list = new ArrayList<String>(20);
		for (Entry<String, List<TableInfo>> entry : tables.entrySet())
		{
			list.add(entry.getKey());
		}
		
		Collections.sort(list);
		return list;
	}
	
	
	/**
	 * Return the column data for the specified table.
	 * 
	 * @param table the table name
	 * @return the data for that table
	 */
	public List<TableInfo> getTableInfo(final String table)
	{
	  List<TableInfo> list = tables.get(table);
	  return list;
	}
	
	
	/**
	 * Return a list of all columns.
	 * 
	 * @return all column names
	 */
	private List<String> getAllColumns()
	{
    if (!isValid())
    {
      return null;
    }
    
    List<String> list = new ArrayList<String>(20);
    for (Entry<String, List<TableInfo>> entry : columns.entrySet())
    {
      list.add(entry.getKey());
    }
    
    Collections.sort(list);
    return list;
	}
	
	
	/**
	 * Return the tables/column data matching on column name.
	 * 
	 * @param column the column name
	 * @return the tables with their data for that column
	 */
	public List<TableInfo> getColumnInfo(final String column)
	{
	  List<TableInfo> list = columns.get(column);
	  return list;
	}
	
	
	/**
	 * Get the iterator of all table names.
	 * 
	 * @return an iterator of table names
	 */
	public Iterator<String> getTableIterator()
	{
	  return tableNames.iterator();
	}
  
  
  /**
   * Get the iterator of all column names.
   * 
   * @return an iterator of column names
   */
  public Iterator<String> getColumnIterator()
  {
    return columnNames.iterator();
  }
  
  
  /**
   * Return the table at the specified index.
   * 
   * @param index the index to retrieve
   * @return the table name
   */
  public String getTable(Integer index)
  {
    return tableNames.get(index.intValue());
  }
  
  
  /**
   * Return the column at the specified index.
   * 
   * @param index the index to retrieve
   * @return the column name
   */
  public String getColumn(Integer index)
  {
    return columnNames.get(index.intValue());
  }
}
