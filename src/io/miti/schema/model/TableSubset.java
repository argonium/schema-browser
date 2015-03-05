package io.miti.schema.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.miti.schema.cache.DBCache;

/**
 * Define a subset of tables, based on search criteria.
 * 
 * @author mike
 * @version 1.0
 */
public final class TableSubset
{
	/** The key for this subset. */
	private TableKey key = null;
	
	/** The list of indexes into the table data, for this subset. */
	private List<Integer> lookup = new ArrayList<Integer>(50);
	
	/** Whether to show tables or columns. */
	private boolean storeTables = false;
	
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unused")
	private TableSubset()
	{
		super();
	}
	
	
	/**
	 * Constructor taking a table name.
	 * 
	 * @param table the table name
	 */
	public TableSubset(final boolean showTables, final String table)
	{
	  storeTables = showTables;
		buildKey(table);
		loadSubset();
	}
	
	
	/**
	 * Build the key using the table name.
	 * 
	 * @param table the table name
	 */
	private void buildKey(final String table)
	{
		key = new TableKey(table);
	}
	
	
	/**
	 * Load the subset.
	 */
	private void loadSubset()
	{
		// Iterate over the list and check for matches on the key
		int index = 0;
		Iterator<String> iter = storeTables ?
		      DBCache.getInstance().getTableIterator() :
		      DBCache.getInstance().getColumnIterator();
		while (iter.hasNext())
		{
			// Get the next table in the list
			final String table = iter.next();
			
			// Check for a match
			if (key.tableMatches(table))
			{
				lookup.add(Integer.valueOf(index));
			}
			
			++index;
		}
	}
	
	
	/**
	 * Return the size of the subset.
	 * 
	 * @return the size of the subset
	 */
	public int getCount()
	{
		return lookup.size();
	}

	
	/**
	 * Return the key data.
	 * 
	 * @return the key data
	 */
	public TableKey getKey()
	{
		return key;
	}
	
	
	/**
	 * Return the table at an index.
	 * 
	 * @param index the index of the table to return
	 * @return the table at the index
	 */
	public String getTable(final int index)
	{
	  return (storeTables ?
	      DBCache.getInstance().getTable(lookup.get(index)) :
	      DBCache.getInstance().getColumn(lookup.get(index)));
	}
	
	
	/**
	 * Return whether the key is empty.
	 * 
	 * @return if the key is empty
	 */
	public boolean isKeyEmpty()
	{
		return key.isEmpty();
	}
	
	
	/**
	 * Return whether the key is based on the supplied table name.
	 * 
	 * @param table the table name
	 * @return whether the key is based on the table
	 */
	public boolean isBasedOn(final String table)
	{
		return key.isBasedOn(table);
	}
	
	
	/**
	 * Empty the list.
	 */
	public void clear()
	{
		lookup.clear();
		lookup = null;
	}
}
