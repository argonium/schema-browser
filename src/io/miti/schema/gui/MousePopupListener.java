package io.miti.schema.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import io.miti.schema.cache.DBCache;
import io.miti.schema.dbutil.TableInfo;
import io.miti.schema.model.TableListModel;
import io.miti.schema.util.ListFormatter;
import io.miti.schema.util.Utility;

public final class MousePopupListener extends MouseAdapter
{
  private boolean showTables = true;
  private JList tableList = null;
  private JPopupMenu menu = new JPopupMenu();
  private Point point = null;
  
  private static final String EOLN = "\r\n";
  
  /**
   * Default constructor.
   */
  public MousePopupListener()
  {
    super();
  }
  
  
  /**
   * Standard constructor for this class.
   * 
   * @param tableData whether we're showing table or column data
   * @param dataList the JList for this popup menu
   */
  public MousePopupListener(final boolean tableData,
                            final JList dataList)
  {
    showTables = tableData;
    tableList = dataList;
    buildPopup();
  }
  
  
  /**
   * Build the popup menu.
   */
  private void buildPopup()
  {
    final String text = showTables ? "table" : "column";
    
    JMenuItem m1 = new JMenuItem(String.format("Copy this %s", text));
    m1.addActionListener(new PopupAction(0));
    JMenuItem m2 = new JMenuItem(String.format("Copy selected %ss", text));
    m2.addActionListener(new PopupAction(1));
    JMenuItem m3 = new JMenuItem(String.format("Copy all %ss", text));
    m3.addActionListener(new PopupAction(2));
    
    JMenuItem m4 = new JMenuItem(String.format("Copy this %s and data", text));
    m4.addActionListener(new PopupAction(3));
    JMenuItem m5 = new JMenuItem(String.format("Copy selected %ss and data", text));
    m5.addActionListener(new PopupAction(4));
    JMenuItem m6 = new JMenuItem(String.format("Copy all %ss and data", text));
    m6.addActionListener(new PopupAction(5));
    
    menu.add(m1);
    menu.add(m2);
    menu.add(m3);
    
    menu.addSeparator();
    
    menu.add(m4);
    menu.add(m5);
    menu.add(m6);
  }
  
  
  @Override
  public void mouseClicked(final MouseEvent e)
  {
    checkPopup(e);
  }
  
  
  @Override
  public void mousePressed(final MouseEvent e)
  {
    checkPopup(e);
  }
  
  
  @Override
  public void mouseReleased(final MouseEvent e)
  {
    checkPopup(e);
  }
  
  
  /**
   * If the user invoked the popup trigger (right-click), show the popup menu.
   * 
   * @param e the mouse event
   */
  private void checkPopup(final MouseEvent e)
  {
    if (e.isPopupTrigger())
    {
      point = new Point(e.getX(), e.getY());
      updatePopupItems();
      menu.show(tableList, e.getX(), e.getY());
      // demoSel();
    }
  }
  
  
  /**
   * Enable and disable items in the popup menu as needed.
   */
  private void updatePopupItems()
  {
    // See if the list has any items
    final int len = ((TableListModel) tableList.getModel()).getSize();
    if (len < 1)
    {
      // No items in the list, so disable all menu items
      enableMenuItems(false, new int[] {0, 1, 2, 4, 5, 6});
      return;
    }
    else
    {
      // Enable the "all tables" menu items
      enableMenuItems(true, new int[] {2, 6});
      
      // Check if there's an item near the mouse click
      final int currItem = tableList.locationToIndex(point);
      enableMenuItems((currItem >= 0), new int[] {0, 4});
      
      // Check if any rows are selected
      final int[] sel = tableList.getSelectedIndices();
      enableMenuItems((sel.length > 0), new int[] {1, 5});
    }
  }
  
  
  /**
   * Enable or disable menu items in the popup menu.
   * 
   * @param enable whether to enable the items referenced by the list in ind
   * @param ind the list of indices of popup menu items to enable or disable
   */
  private void enableMenuItems(final boolean enable, final int[] ind)
  {
    final int num = ind.length;
    for (int i = 0; i < num; ++i)
    {
      menu.getComponent(ind[i]).setEnabled(enable);
    }
  }
  
  
  /**
   * The action listener for the items in the popup menu.
   */
  class PopupAction implements ActionListener
  {
    /** The mode for this action - copy one row, selected rows, or all rows. */
    private int mode = 0;
    
    
    /**
     * Constructor.
     * 
     * @param nMode the mode for this instance
     */
    public PopupAction(final int nMode)
    {
      mode = nMode;
    }
    
    
    /**
     * Handle the action.
     * 
     * @param evt the action event
     */
    @Override
    public void actionPerformed(final ActionEvent evt)
    {
      switch (mode)
      {
        case 0:
          copyObject(evt);
          break;
          
        case 1:
          copySelectedObject(evt);
          break;
          
        case 2:
          copyAllObjects(evt);
          break;
          
        case 3:
          copyObjectAndData(evt);
          break;
          
        case 4:
          copySelectedObjectAndData(evt);
          break;
          
        case 5:
        default:
          copyAllObjectsAndData(evt);
          break;
      }
    }
    
    
    /**
     * Copy the current object.
     * 
     * @param evt the action event
     */
    private void copyObject(final ActionEvent evt)
    {
      final int currItem = tableList.locationToIndex(point);
      if (currItem >= 0)
      {
        String item = (String) ((TableListModel)
            tableList.getModel()).getElementAt(currItem);
        Utility.copyToClipboard(item);
      }
    }
    
    
    /**
     * Copy the selected object.
     * 
     * @param evt the action event
     */
    private void copySelectedObject(final ActionEvent evt)
    {
      int[] sel = tableList.getSelectedIndices();
      final int len = sel.length;
      if (len > 0)
      {
        StringBuilder sb = new StringBuilder(50);
        
        String item = (String) ((TableListModel)
            tableList.getModel()).getElementAt(sel[0]);
        sb.append(item);
        
        for (int i = 1; i < len; ++i)
        {
          sb.append(EOLN);
          item = (String) ((TableListModel)
              tableList.getModel()).getElementAt(sel[i]);
          sb.append(item);
        }
        
        // System.out.println(sb.toString());
        Utility.copyToClipboard(sb.toString());
      }
    }
    
    
    /**
     * Copy all objects.
     * 
     * @param evt the action event
     */
    private void copyAllObjects(final ActionEvent evt)
    {
      final int len = ((TableListModel) tableList.getModel()).getSize();
      if (len > 0)
      {
        StringBuilder sb = new StringBuilder(50);
        
        String item = (String) ((TableListModel)
            tableList.getModel()).getElementAt(0);
        sb.append(item);
        
        for (int i = 1; i < len; ++i)
        {
          sb.append(EOLN);
          item = (String) ((TableListModel)
              tableList.getModel()).getElementAt(i);
          sb.append(item);
        }
        
        // System.out.println(sb.toString());
        Utility.copyToClipboard(sb.toString());
      }
    }
    
    
    /**
     * Copy the current object and its data.
     * 
     * @param evt the action event
     */
    private void copyObjectAndData(final ActionEvent evt)
    {
      final int currItem = tableList.locationToIndex(point);
      if (currItem >= 0)
      {
        // Get the field name
        String item = (String) ((TableListModel)
            tableList.getModel()).getElementAt(currItem);
        StringBuilder sb = new StringBuilder(100);
        sb.append(item).append(EOLN);
        sb.append(getItemDetails(item));
        
        // Copy it to the clipboard
        Utility.copyToClipboard(sb.toString());
      }
    }
    
    
    /**
     * Get the details for the specified item.
     * 
     * @param item the table or column name
     * @return the details table
     */
    private String getItemDetails(final String item)
    {
      // Get the info for this table/column
      List<TableInfo> tableInfo = showTables ? DBCache.getInstance().getTableInfo(item) :
        DBCache.getInstance().getColumnInfo(item);
      String table = new ListFormatter().getTable(tableInfo, getOutputColumns(), getOutputTitles());
      return table;
    }
    
    
    /**
     * Return the column titles for the output list.
     * 
     * @return the column titles
     */
    private String[] getOutputTitles()
    {
      String[] titles = new String[]{"#", (showTables ? "Column" : "Table"), "Column Type", "Can Be Null?", "Primary Key?"};
      return titles;
    }
    
    
    /**
     * Return the names of the fields to include in the output list.
     * 
     * @return the column fields
     */
    private String[] getOutputColumns()
    {
      String[] cols = new String[]{"order", (showTables ? "columnName" : "tableName"), "columnType", "isNullable", "isPK"};
      return cols;
    }
    
    
    /**
     * Copy the selected objects and data.
     * 
     * @param evt the action event
     */
    private void copySelectedObjectAndData(final ActionEvent evt)
    {
      int[] sel = tableList.getSelectedIndices();
      final int len = sel.length;
      if (len > 0)
      {
        StringBuilder sb = new StringBuilder(50);
        
        String item = (String) ((TableListModel)
            tableList.getModel()).getElementAt(sel[0]);
        sb.append(item).append(EOLN);
        sb.append(getItemDetails(item));
        
        for (int i = 1; i < len; ++i)
        {
          sb.append(EOLN);
          item = (String) ((TableListModel)
              tableList.getModel()).getElementAt(sel[i]);
          sb.append(item).append(EOLN);
          sb.append(getItemDetails(item));
        }
        
        // System.out.println(sb.toString());
        Utility.copyToClipboard(sb.toString());
      }
    }
    
    
    /**
     * Copy all objects and data.
     * 
     * @param evt the action event
     */
    private void copyAllObjectsAndData(final ActionEvent evt)
    {
      final int len = ((TableListModel) tableList.getModel()).getSize();
      if (len > 0)
      {
        StringBuilder sb = new StringBuilder(50);
        
        String item = (String) ((TableListModel)
            tableList.getModel()).getElementAt(0);
        sb.append(item).append(EOLN);
        sb.append(getItemDetails(item));
        
        for (int i = 1; i < len; ++i)
        {
          sb.append(EOLN);
          item = (String) ((TableListModel)
              tableList.getModel()).getElementAt(i);
          sb.append(item).append(EOLN);
          sb.append(getItemDetails(item));
        }
        
        // System.out.println(sb.toString());
        Utility.copyToClipboard(sb.toString());
      }
    }
  }
}
