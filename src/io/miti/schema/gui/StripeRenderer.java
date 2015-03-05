package io.miti.schema.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public final class StripeRenderer extends DefaultListCellRenderer
{
  private static final long serialVersionUID = 1L;
  
  private static final Color SELECTED_COLOR = new Color(0, 0, 230);
  private static final Color ODD_COLOR = new Color(255, 255, 255);
  private static final Color EVEN_COLOR = new Color(230, 230, 255);
  
  @Override
  public Component getListCellRendererComponent(final JList list,
                                                final Object value,
                                                final int index,
                                                final boolean isSelected,
                                                final boolean cellHasFocus)
  {
    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
                                      isSelected, cellHasFocus);
    
    if (isSelected)
    {
      label.setBackground(SELECTED_COLOR);
    }
    else if (index % 2 == 0)
    {
      label.setBackground(EVEN_COLOR);
    }
    else
    {
      label.setBackground(ODD_COLOR);
    }
    
    return label;
  }
}
