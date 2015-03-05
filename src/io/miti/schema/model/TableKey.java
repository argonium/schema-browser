package io.miti.schema.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The search key data for a table.
 * 
 * @author mike
 * @version 1.0
 */
public final class TableKey
{
	/**
	 * The list of key data.
	 */
	private List<String> key = new ArrayList<String>(10);
	
	/**
	 * Name of the table the key is based on.
	 */
	private String lastTable = null;
	
	
	/**
	 * Default constructor.
	 */
	public TableKey()
	{
	  super();
	}

	
	/**
	 * Initialize the key based on a new table name.
	 * 
	 * @param table the table name
	 */
	public TableKey(final String table)
	{
		key.clear();
		
		lastTable = table;
		
		buildKey(table);
	}
	
	
	/**
	 * Build the key using the code of table name.
	 * The data is appended to the key.
	 * 
	 * @param code the code to parse
	 */
	private void buildKey(final String code)
	{
		StringTokenizer st = new StringTokenizer(code, " ");
		while (st.hasMoreTokens())
		{
			String token = st.nextToken().trim();
			if (token.length() > 0)
			{
			  key.add(token.toUpperCase());
			}
		}
	}
	
	
	/**
	 * Check for a match of a table on the key.
	 * 
	 * @param table the table to check
	 * @return whether the table is a match
	 */
	public boolean tableMatches(final String table)
	{
		// Check if the table is a match on the key
		if ((key == null) || (key.isEmpty()))
		{
			return true;
		}
		
		boolean rc = true;
		for (String code : key)
		{
			if (!table.contains(code))
			{
				rc = false;
				break;
			}
		}
		
		return rc;
	}
	
	
	/**
	 * Print the key data.
	 */
	public void printKey()
	{
		System.out.println("Key...");
		for (String word : key)
		{
			System.out.println(" ==>" + word);
		}
	}

	
	/**
	 * Return whether the key is empty.
	 * 
	 * @return if the key is empty
	 */
	public boolean isEmpty()
	{
		return key.isEmpty();
	}

	
	/**
	 * Return whether this key is based on the supplied parameters.
	 * 
	 * @param table the table name
	 * @return whether this key is based on the parameters
	 */
	public boolean isBasedOn(final String table)
	{
		boolean rc = true;
		if (lastTable == null)
		{
			rc = (table == null);
		}
		else
		{
			rc = (table == null) ? false : table.equalsIgnoreCase(lastTable);
		}
		
		return rc;
	}
}
