package io.miti.schema.model;

import javax.swing.DefaultListModel;

/**
 * The model for the JList showing the tables.
 * 
 * @author mike
 * @version 1.0
 */
public class TableListModel extends DefaultListModel
{
	/** Default serial ID. */
	private static final long serialVersionUID = 1L;
	
	/** The current subset key. */
	private TableSubset subset = null;
	
	/** Whether to store tables or columns. */
	private boolean storeTables = false;
	
	/**
	 * Default constructor.
	 */
	public TableListModel()
	{
		subset = new TableSubset(storeTables, "");
	}
	
	
	/**
	 * Constructor taking a flag for whether to show tables.
	 * 
	 * @param showTables if true, show tables, else columns
	 */
	public TableListModel(final boolean showTables)
	{
	  storeTables = showTables;
	  subset = new TableSubset(storeTables, "");
	}
	
	
	/**
	 * Constructor taking the table name.
	 */
	public TableListModel(final String table)
	{
		subset = new TableSubset(storeTables, table);
	}
	
	
	/**
	 * Update the key used to determine the subset of tables to display.
	 * 
	 * @param table the table name
	 * @return whether the table needs to be redrawn
	 */
	public boolean setSubsetKey(final String table)
	{
		// Optimize this to check for the same codes
		boolean redrawNeeded = false;
		if (!subset.isBasedOn(table))
		{
		  subset.clear();
		  subset = new TableSubset(storeTables, table);
		  
		  // The subset changed, so we need to redraw the list
		  redrawNeeded = true;
		  
		  // Let the model know the contents changed
		  fireContentsChanged(this, 0, subset.getCount());
		}
		
		// Return whether the table needs to be redrawn
		return redrawNeeded;
	}
	
	
	/**
	 * Return the table name at a specified index.
	 * 
	 * @param index the index of the table to return
	 */
	@Override
	public Object getElementAt(final int index)
	{
		String table = subset.getTable(index);
		return table;
	}

	
	/**
	 * Return the size of the list.
	 * 
	 * @return the size of the list
	 */
	@Override
	public int getSize()
	{
		return subset.getCount();
	}
	
	
	/**
	 * Find the closest index matching on row value or index.
	 * 
	 * @param prevIndex the previously selected index
	 * @param prevValue the previously selected value
	 * @return the closest index
	 */
  public int getClosestIndexFor(final int prevIndex,
                                final String prevValue)
  {
    // If nothing was selected before, stay that way
    if (prevIndex < 0)
    {
      return prevIndex;
    }
    
    // If there are no rows in the subset, return -1
    final int count = subset.getCount();
    if (count <= 0)
    {
      return -1;
    }
    
    // First try to find the row matching on prevValue
    if (prevValue != null)
    {
      int matchIndex = -1;
      for (int i = 0; i < count; ++i)
      {
        if (prevValue.equals(subset.getTable(i)))
        {
          matchIndex = i;
          break;
        }
      }
      
      if (matchIndex > 0)
      {
        return matchIndex;
      }
    }
    
    if (prevIndex >= count)
    {
      return (count - 1);
    }
    else if (prevIndex < count)
    {
      return count;
    }
    
    return 0;
  }
}
