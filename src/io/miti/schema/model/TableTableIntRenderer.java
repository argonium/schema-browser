package io.miti.schema.model;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public final class TableTableIntRenderer extends DefaultTableCellRenderer
{
  private static final long serialVersionUID = 1L;

  public TableTableIntRenderer()
  {
    super();
    
    // Show integers on the left-side of the cell (default is right-side)
    setHorizontalAlignment(SwingConstants.LEFT);
  }
}
