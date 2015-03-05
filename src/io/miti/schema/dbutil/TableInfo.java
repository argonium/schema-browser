package io.miti.schema.dbutil;

/**
 * Class to hold information read from the database info file.
 */
public final class TableInfo implements Comparable<TableInfo>
{
  public int order = 0;
  public String tableName = null;
	public String columnName = null;
	public String columnType = null;
	public boolean isNullable = false;
	public boolean isPK = false;
	
	/**
	 * Default constructor.
	 */
	public TableInfo()
	{
		super();
	}
	
	
	/**
	 * Override of toString().
	 */
	@Override
	public String toString()
	{
		return Integer.toString(order) + "/" + tableName + "/" + columnName +
		          "/" + columnType + "/" + isNullable + "/" + isPK;
	}
	
	
  @Override
  public int compareTo(final TableInfo o)
  {
    return tableName.compareTo(o.tableName);
  }
}
