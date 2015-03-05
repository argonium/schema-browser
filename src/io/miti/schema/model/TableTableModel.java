package io.miti.schema.model;

import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import io.miti.schema.dbutil.TableInfo;
import io.miti.schema.cache.DBCache;

public final class TableTableModel extends DefaultTableModel
{
  /** Default version UID. */
  private static final long serialVersionUID = 1L;
  
  /** Whether we're showing tables or columns. */
  private boolean showTables = false;
  
  /** The data we're showing. */
  private List<TableInfo> info = null;
  
  /** The number of rows. */
  private int rowCount = 0;
  
  
  /**
   * Default constructor.
   */
  public TableTableModel()
  {
    super();
  }
  
  
  /**
   * Constructor taking whether we're showing table data or column data.
   * 
   * @param storeTables what type of data we're showing
   */
  public TableTableModel(final boolean storeTables)
  {
    showTables = storeTables;
  }
  
  
  @Override
  public int getRowCount()
  {
    return rowCount;
  }


  @Override
  public int getColumnCount()
  {
    return 5;
  }
  
  
  @Override
  public String getColumnName(int column)
  {
    switch (column)
    {
      case 0: return "#";
      case 1: return (showTables ? "Column" : "Table");
      case 2: return "Column Type";
      case 3: return "Null";
      case 4: return "Primary Key";
      default: return "xxx";
    }
  }
  
  
  @Override
  public Class<?> getColumnClass(int column)
  {
    if (column == 0)
    {
      return Integer.class;
    }
    
    return String.class;
  }


  @Override
  public boolean isCellEditable(int row, int column)
  {
    return false;
  }
  
  
  @Override
  public Object getValueAt(int row, int column)
  {
    if (info == null)
    {
      return "";
    }
    
    final TableInfo item = info.get(row);
    switch (column)
    {
      case 0: return Integer.valueOf(item.order);
      case 1: return (showTables ? item.columnName : item.tableName);
      case 2: return item.columnType;
      case 3: return (item.isNullable ? "" : "NOT NULL");
      case 4: return (item.isPK ? "YES" : "");
      default: return "";
    }
  }
  
  
  /**
   * Clear the data since the list selection was cleared.
   */
  public void clearTable()
  {
    rowCount = 0;
    refresh();
  }
  
  
  /**
   * The list selection changed, so update the table.
   * 
   * @param key the table or column name
   */
  public void updateKey(final String key, final boolean sortList)
  {
    info = (showTables) ? DBCache.getInstance().getTableInfo(key) :
             DBCache.getInstance().getColumnInfo(key);
    if (sortList)
    {
      Collections.sort(info);
    }
    
    rowCount = info.size();
    refresh();
  }
  
  
  /**
   * Force a refresh of the table, since the data changed.
   */
  public void refresh()
  {
    fireTableDataChanged();
  }
}
