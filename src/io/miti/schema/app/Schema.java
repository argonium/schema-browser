package io.miti.schema.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import io.miti.schema.gui.MousePopupListener;
import io.miti.schema.gui.StripeRenderer;
import io.miti.schema.model.TableListModel;
import io.miti.schema.model.TableTableIntRenderer;
import io.miti.schema.model.TableTableModel;
import io.miti.schema.util.Content;
import io.miti.schema.util.Utility;
import io.miti.schema.util.WindowState;
import io.miti.schema.cache.DBCache;
import io.miti.schema.cache.DBMetadata;

/**
 * This is the main class for the application.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Schema implements ClipboardOwner
{
  /** The name of the properties file. */
  public static final String PROPS_FILE_NAME = "schema.prop";
  
  /** Whether the right-click popup menu is enabled. */
  private static final boolean ENABLE_POPUP = true;
  
  /** The application frame. */
  private JFrame frame = null;
  
  /** The status bar. */
  private JLabel statusBar = null;
  
  /** The window state (position and size). */
  private WindowState windowState = null;
  
  /** The root of the name of the input data file. */
  private static final String DATA_FILE_NAME = "tables.xml";
  
  /** The GUI components. */
  private JPanel tableSearchPanel = null;
  private JPanel columnSearchPanel = null;
  private JPanel tableResultsPanel = null;
  private JPanel columnResultsPanel = null;
  private JPanel resultsPanel = null;
  private JList<String> tableList = null;
  private JList<String> columnList = null;
  private JTextField tfTable = null;
  private JTable tableTable = null;
  private JTable columnTable = null;
  
  /**
   * Default constructor.
   */
  private Schema()
  {
    super();
  }
  
  
  /**
   * Create the application's GUI.
   */
  private void createGUI()
  {
    // Load the properties file
    windowState = WindowState.getInstance();
    
    // See if the data file is external or internal (in the jar)
    checkInputFileSource();
    
    // Set up the frame
    setupFrame();
    
    // Create the empty middle window
    initScreen();
    
    // Load the data
    DBCache.getInstance();
    
    // Set up the status bar
    initStatusBar();
    
    // Set the frame icon
    frame.setIconImage(Content.getIcon("mainicon.png").getImage());
    
    // Display the window.
    frame.pack();
    frame.setVisible(true);
    
    // Set the divider location
    JSplitPane sp = (JSplitPane) ((JPanel) ((javax.swing.JLayeredPane)
       frame.getRootPane().getComponent(1)).getComponent(0)).getComponent(0);
    sp.setDividerLocation(0.33);
    
    // Select the first table and column
    if (((TableListModel) tableList.getModel()).getSize() > 0)
    {
      tableList.setSelectedIndex(0);
    }
    if (((TableListModel) columnList.getModel()).getSize() > 0)
    {
      columnList.setSelectedIndex(0);
    }
    
    // Give the focus to the text field for the table search
    tfTable.requestFocusInWindow();
  }


  /**
   * Check how the application is run and save information
   * about the input file.
   */
  private void checkInputFileSource()
  {
    // See if we can find the input file at the root. If the URL is not null,
    // we're in a jar file.  If it's null, we're in an IDE.
    final URL url = getClass().getResource("/" + DATA_FILE_NAME);
    if (url != null)
    {
      // We're running in a jar file
      Utility.readFilesAsStream(true);
    }
    else
    {
      // We're not running in a jar file
      Utility.readFilesAsStream(false);
    }
  }
  
  
  /**
   * Set up the application frame.
   */
  private void setupFrame()
  {
    // Create and set up the window.
    frame = new JFrame(Utility.getAppName());
    
    // Have the frame call exitApp() whenever it closes
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter()
    {
      /**
       * Close the windows.
       * 
       * @param e the event
       */
      @Override
      public void windowClosing(final WindowEvent e)
      {
        exitApp();
      }
    });
    
    // Set up the size of the frame
    frame.setPreferredSize(windowState.getSize());
    frame.setSize(windowState.getSize());
    
    // Set the position
    if (windowState.shouldCenter())
    {
      frame.setLocationRelativeTo(null);
    }
    else
    {
      frame.setLocation(windowState.getPosition());
    }
  }
  
  
  /**
   * Initialize the table search panel.
   */
  private void getTableSearchPanel()
  {
    tableSearchPanel = new JPanel(new BorderLayout());
    final boolean tablePage = true;
    
    // Set up the button to copy list items to the clipboard
//    final JButton btnPaste = new JButton(Content.getIcon("paste.png"));
//    btnPaste.setToolTipText("Copy the table names to the clipboard");
//    btnPaste.setMnemonic(KeyEvent.VK_C);
//    btnPaste.addActionListener(new ActionListener()
//    {
//      @Override
//      public void actionPerformed(final ActionEvent e)
//      {
//        // Copy the list of displayed tables to the clipboard
//        copyTablesToClipboard();
//      }
//    });
    
    // Set up the button to clear the search field
    final JButton btnClear = new JButton("X");
    btnClear.setToolTipText("Clear the search field");
    btnClear.setMnemonic(KeyEvent.VK_X);
    btnClear.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        tfTable.setText("");
        keyChanged("", tablePage);
      }
    });
    
    // Panel for the buttons
//    JPanel topRightPanel = new JPanel(new BorderLayout());
//    topRightPanel.add(btnClear, BorderLayout.WEST);
//    topRightPanel.add(btnPaste, BorderLayout.EAST);
    
    // Create the top panel - search and clear
    JPanel topPanel = new JPanel(new BorderLayout());
    tfTable = new JTextField();
    
    // Set up a key listener on the table
    tfTable.addKeyListener(new KeyListener()
    {
  		@Override
  		public void keyPressed(KeyEvent arg0)
  		{
  		}
  
  		@Override
  		public void keyReleased(KeyEvent arg0)
  		{
  		  keyChanged(tfTable.getText(), tablePage);
  		}
  
  		@Override
  		public void keyTyped(KeyEvent arg0)
  		{
  		}
    });
    
    JLabel lblSearch = new JLabel("Search: ");
    lblSearch.setDisplayedMnemonic(KeyEvent.VK_S);
    lblSearch.setLabelFor(tfTable);
    
    topPanel.add(lblSearch, BorderLayout.WEST);
    topPanel.add(tfTable, BorderLayout.CENTER);
    topPanel.add(btnClear, BorderLayout.EAST);
    tableSearchPanel.add(topPanel, BorderLayout.NORTH);
    
    // Create the list showing the search results
    tableList = new JList<String>();
    tableList.setModel(new TableListModel(true));
    tableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tableList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (!e.getValueIsAdjusting())
        {
          updateTable(true, e);
        }
      }
    });
    tableList.setCellRenderer(new StripeRenderer());
    
    if (ENABLE_POPUP)
    {
      tableList.addMouseListener(new MousePopupListener(true, tableList));
    }
    
    JScrollPane sp = new JScrollPane(tableList);
    tableSearchPanel.add(sp, BorderLayout.CENTER);
  }
  
  
  /**
   * Copy the displayed list of tables to the clipboard.
   */
  @SuppressWarnings("unused")
  private void copyTablesToClipboard()
  {
    // Get the list of tables
    StringBuilder sb = new StringBuilder();
    TableListModel tlm = (TableListModel) tableList.getModel();
    final int size = tlm.getSize();
    for (int i = 0; i < size; ++i)
    {
      // Add the table name to the string
      String row = (String) tlm.getElementAt(i);
      sb.append(row).append("\r\n");
    }
    
    // Copy the string to the clipboard
    StringSelection text = new StringSelection(sb.toString());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(text, this);
  }
  
  
  /**
   * Copy the displayed list of columns to the clipboard.
   */
  @SuppressWarnings("unused")
  private void copyColumnsToClipboard()
  {
    // Get the list of tables
    StringBuilder sb = new StringBuilder();
    TableListModel tlm = (TableListModel) columnList.getModel();
    final int size = tlm.getSize();
    for (int i = 0; i < size; ++i)
    {
      // Add the table name to the string
      String row = (String) tlm.getElementAt(i);
      sb.append(row).append("\r\n");
    }
    
    // Copy the string to the clipboard
    StringSelection text = new StringSelection(sb.toString());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(text, this);
  }
  
  
  /**
   * The selected search result changed, so update the results table.
   * 
   * @param tableData true if this is for table data, else columns
   * @param e the list selection event
   */
  private void updateTable(final boolean tableData,
                           final ListSelectionEvent e)
  {
    // See if we're updating the table with info on DB tables
    if (tableData)
    {
      // Get the selected index
      final int index = getSingleSelectedIndex(tableList);
      if (index < 0)
      {
        // Nothing selected, so empty the table
        ((TableTableModel) tableTable.getModel()).clearTable();
      }
      else
      {
        // Update the table with info for the new selection
        String item = (String) ((TableListModel)
            tableList.getModel()).getElementAt(index);
        ((TableTableModel) tableTable.getModel()).updateKey(item, false);
      }
    }
    else
    {
      // Get the selected index
      final int index = getSingleSelectedIndex(columnList);
      if (index < 0)
      {
        // Nothing selected, so empty the table
        ((TableTableModel) columnTable.getModel()).clearTable();
      }
      else
      {
        // Update the table with info for the new selection
        String item = (String) ((TableListModel)
            columnList.getModel()).getElementAt(index);
        ((TableTableModel) columnTable.getModel()).updateKey(item, true);
      }
    }
  }
  
  
  /**
   * Return the selected index from the list.  If more than one item is selected,
   * return -1.
   * 
   * @param list the list
   * @return the single selected index
   */
  private int getSingleSelectedIndex(final JList<String> list)
  {
    final int[] sel = list.getSelectedIndices();
    if ((sel == null) || (sel.length == 0) || (sel.length > 1))
    {
      return -1;
    }
    
    return sel[0];
  }
  
  
  /**
   * Handle a key change in the search field for tables
   * and columns.
   * 
   * @param str the new search string
   * @param tablePage whether this is for tables or columns
   */
  private void keyChanged(final String str, final boolean tablePage)
  {
    if (tablePage)
    {
      // Save the currently selected row value
      final int rowIndex = tableList.getSelectedIndex();
      final String rowValue =
          ((rowIndex < 0) || (tableList.getModel().getSize() == 0)) ? "null" : (String) tableList.getSelectedValue();
      
      // Update the list of tables in tableList
      ((TableListModel) tableList.getModel()).setSubsetKey(str);
      refreshList(tableList, rowIndex, rowValue);
      updateTable(tablePage, null);
    }
    else
    {
      // Save the currently selected row value
      final int rowIndex = columnList.getSelectedIndex();
      final String rowValue =
          ((rowIndex < 0) || (columnList.getModel().getSize() == 0)) ? "null" : (String) columnList.getSelectedValue();
      
      // Update the list of columns in columnList
      ((TableListModel) columnList.getModel()).setSubsetKey(str);
      refreshList(columnList, rowIndex, rowValue);
      updateTable(tablePage, null);
    }
  }
  
  
  /**
   * Method required for the class to implement the ClipboardOwner
   * interface.
   * 
   * @param clipboard the clipboard
   * @param contents the contents
   */
  @Override
  public void lostOwnership(final Clipboard clipboard,
                            final Transferable contents)
  {
    // Nothing to do here
  }
  
  
  /**
   * Refresh the table list based on changes to the search criteria
   * for the list.
   * 
   * @param list the list of interest
   * @param prevIndex the previously-selected index
   * @param prevValue the previously-selected row
   */
  private void refreshList(final JList<String> list,
                           final int prevIndex,
                           final String prevValue)
  {
    // If there are any matches, at least select the first one
    final int listSize = list.getModel().getSize();
    final boolean listHasRows = (listSize > 0);
    
    // Find the index of prevListValue in list, if it still
    // exists.  If so, select that row.  If it doesn't exist,
    // and the table has enough rows, select the closest match
    if (prevValue.equals("null"))
    {
      if (listHasRows)
      {
        list.setSelectedIndex(0);
        list.ensureIndexIsVisible(0);
      }
      else
      {
        list.clearSelection();
      }
      
      return;
    }
    
    final int index = ((TableListModel)
        list.getModel()).getClosestIndexFor(prevIndex, prevValue);
    if (index < 0)
    {
      if (listHasRows)
      {
        list.setSelectedIndex(0);
        list.ensureIndexIsVisible(0);
      }
      else
      {
        list.clearSelection();
      }
    }
    else
    {
      list.setSelectedIndex(index);
      list.ensureIndexIsVisible(index);
    }
  }
  
  
  /**
   * Initialize the column search panel.
   */
  private void getColumnSearchPanel()
  {
    columnSearchPanel = new JPanel(new BorderLayout());
    final boolean tablePage = false;
    
    // Create the top panel - search and clear
    JPanel topPanel = new JPanel(new BorderLayout());
    final JTextField tf = new JTextField();
    
    // Set up the button to copy list items to the clipboard
//    final JButton btnPaste = new JButton(Content.getIcon("paste.png"));
//    btnPaste.setToolTipText("Copy the column names to the clipboard");
//    btnPaste.setMnemonic(KeyEvent.VK_C);
//    btnPaste.addActionListener(new ActionListener()
//    {
//      @Override
//      public void actionPerformed(final ActionEvent e)
//      {
//        // Copy the list of displayed columns to the clipboard
//        copyColumnsToClipboard();
//      }
//    });
    
    // Set up the button to clear the search field
    JButton btnClear = new JButton("X");
    btnClear.setToolTipText("Clear the search field");
    btnClear.setMnemonic(KeyEvent.VK_X);
    btnClear.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        tf.setText("");
        keyChanged("", tablePage);
      }
    });
    
    // Panel for the buttons
//    JPanel topRightPanel = new JPanel(new BorderLayout());
//    topRightPanel.add(btnClear, BorderLayout.WEST);
//    topRightPanel.add(btnPaste, BorderLayout.EAST);
    
    // Set up a key listener on the table
    tf.addKeyListener(new KeyListener()
    {
      @Override
      public void keyPressed(KeyEvent arg0)
      {
      }
  
      @Override
      public void keyReleased(KeyEvent arg0)
      {
        keyChanged(tf.getText(), tablePage);
      }
  
      @Override
      public void keyTyped(KeyEvent arg0)
      {
      }
    });
  
    JLabel lblSearch = new JLabel("Search: ");
    lblSearch.setDisplayedMnemonic(KeyEvent.VK_S);
    lblSearch.setLabelFor(tf);
    
    topPanel.add(lblSearch, BorderLayout.WEST);
    topPanel.add(tf, BorderLayout.CENTER);
    topPanel.add(btnClear, BorderLayout.EAST);
    columnSearchPanel.add(topPanel, BorderLayout.NORTH);
    
    // Create the list showing the search results
    columnList = new JList<String>();
    columnList.setModel(new TableListModel(false));
    columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    columnList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        updateTable(false, e);
      }
    });
    columnList.setCellRenderer(new StripeRenderer());
    
    if (ENABLE_POPUP)
    {
      columnList.addMouseListener(new MousePopupListener(false, columnList));
    }
    
    JScrollPane sp = new JScrollPane(columnList);
    columnSearchPanel.add(sp, BorderLayout.CENTER);
  }
  
  
  /**
   * Initialize the table results panel.
   */
  private void getTableResultsPanel()
  {
    tableResultsPanel = new JPanel(new BorderLayout());
    
    tableTable = new JTable();
    tableTable.setAutoCreateRowSorter(true);
    tableTable.setModel(new TableTableModel(true));
    tableTable.setDefaultRenderer(Integer.class, new TableTableIntRenderer());
    JScrollPane sp = new JScrollPane(tableTable);
    tableResultsPanel.add(sp, BorderLayout.CENTER);
  }
  
  
  /**
   * Initialize the column results panel.
   */
  private void getColumnResultsPanel()
  {
    columnResultsPanel = new JPanel(new BorderLayout());
    
    columnTable = new JTable();
    columnTable.setAutoCreateRowSorter(true);
    columnTable.setModel(new TableTableModel(false));
    columnTable.setDefaultRenderer(Integer.class, new TableTableIntRenderer());
    JScrollPane sp = new JScrollPane(columnTable);
    columnResultsPanel.add(sp, BorderLayout.CENTER);
  }
  
  
  /**
   * Initialize the main screen (middle window).
   */
  private void initScreen()
  {
    // Set up the search panels
    getTableSearchPanel();
    getColumnSearchPanel();
    
    // Set up the results panels
    getTableResultsPanel();
    getColumnResultsPanel();
    
    // Create a card layout for the results panels
    resultsPanel = new JPanel(new CardLayout());
    resultsPanel.add(tableResultsPanel, "table");
    resultsPanel.add(columnResultsPanel, "column");
    showCard(true);
    
    // Set up the left panel's tabbed pane (search panels)
    JTabbedPane tp = new JTabbedPane();
    tp.insertTab("Tables", null, tableSearchPanel, "Table data", 0);
    tp.insertTab("Columns", null, columnSearchPanel, "Column data", 1);
    tp.setMnemonicAt(0, KeyEvent.VK_T);
    tp.setMnemonicAt(1, KeyEvent.VK_C);
    tp.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        // Show the table for the active tab
        int sel = ((JTabbedPane) e.getSource()).getSelectedIndex();
        showCard(sel == 0);
      }
    });
    
    // Populate the split pane
    JSplitPane sp = new JSplitPane();
    sp.setLeftComponent(tp);
    sp.setRightComponent(resultsPanel);
    
    // Put the contents in the frame
    frame.getContentPane().add(sp, BorderLayout.CENTER);
  }
  
  
  /**
   * Show the appropriate table data for the current tab.
   * 
   * @param showTable whether to show the data for tables
   */
  private void showCard(final boolean showTable)
  {
    final CardLayout cl = (CardLayout) resultsPanel.getLayout();
    cl.show(resultsPanel, showTable ? "table" : "column");
  }
  
  
  /**
   * Initialize the status bar.
   */
  private void initStatusBar()
  {
    // Get the text for the date from the input file
    String dateStr = DBMetadata.getLastRunData();
    
    // Instantiate the status bar
    statusBar = new JLabel();
    statusBar.setText((dateStr == null) ? "Ready" : dateStr);
    
    // Set the color and border
    statusBar.setForeground(Color.black);
    statusBar.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
                              new SoftBevelBorder(SoftBevelBorder.LOWERED)));
    
    // Add to the content pane
    frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
  }
  
  
  /**
   * Show the About dialog box.
   */
  @SuppressWarnings("unused")
  private void showAbout()
  {
    StringBuilder sb = new StringBuilder(100);
    sb.append("SchemaBrowser\nWritten by Mike Wallace");
    JOptionPane.showMessageDialog(frame, sb.toString(),
                  "About", JOptionPane.INFORMATION_MESSAGE);
  }
  
  
  /**
   * Exit the application.
   */
  private void exitApp()
  {
    // Store the window state in the properties file
    windowState.update(frame.getBounds());
    windowState.saveToFile(PROPS_FILE_NAME);
    
    // Close the application by disposing of the frame
    frame.dispose();
  }
  
  
  /**
   * Entry point to the application.
   * 
   * @param args arguments passed to the application
   */
  public static void main(final String[] args)
  {
    // Make the application Mac-compatible
    Utility.makeMacCompatible();
    
    // Load the properties file data
    WindowState.load(PROPS_FILE_NAME);
    
    // Initialize the look and feel to the default for this OS
    Utility.initLookAndFeel();
    
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        // Run the application
        new Schema().createGUI();
      }
    });
  }
}
