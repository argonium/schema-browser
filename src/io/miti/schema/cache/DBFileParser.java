package io.miti.schema.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import io.miti.schema.dbutil.TableInfo;
import io.miti.schema.util.Content;

public final class DBFileParser extends DefaultHandler
{
  private int order = 0;
  private String tableName = null;
  private String colType = null;
  private boolean isNullable = false;
  private boolean isPK = false;
  private TableInfo info = null;
	private boolean inColSection = false;
	private boolean inLastRunSection = false;
	
	private Map<String, List<TableInfo>> map = null;
	
	
	/**
	 * Default constructor.
	 */
	public DBFileParser()
	{
		super();
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);
		
		if (qName.equals("table"))
		{
		  tableName = attributes.getValue("id").toUpperCase();
		}
		else if (qName.equals("col"))
		{
		  colType = attributes.getValue("type");
		  isNullable = attributes.getValue("nullable").equals("1");
		  isPK = attributes.getValue("pk").equals("1");
		  final String orderStr = attributes.getValue("order");
		  order = Integer.parseInt(orderStr);
			inColSection = true;
		}
		else if (qName.equals("lastrun"))
		{
		  inLastRunSection = true;
		}
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		super.endElement(uri, localName, qName);
		
		if (qName.equals("col"))
		{
		  if (map.containsKey(tableName))
		  {
		    map.get(tableName).add(info);
		  }
		  else
		  {
		    List<TableInfo> cols = new ArrayList<TableInfo>(5);
		    cols.add(info);
		    map.put(tableName, cols);
		  }
		}
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		super.characters(ch, start, length);
		String str = new String(ch, start, length).toUpperCase();
		
		if (inColSection)
		{
		  info = new TableInfo();
		  info.order = order;
		  info.columnName = str;
		  info.tableName = tableName;
		  info.isNullable = isNullable;
		  info.isPK = isPK;
		  info.columnType = colType;
			inColSection = false;
		}
		else if (inLastRunSection)
		{
		  DBMetadata.lastRun = str;
		  inLastRunSection = false;
		}
	}
	
	
	@SuppressWarnings("unused")
	private void printMap()
	{
		for (Entry<String, List<TableInfo>> entry : map.entrySet())
		{
			String key = entry.getKey();
			System.out.println("Table is " + key);
			List<TableInfo> cols = entry.getValue();
			for (TableInfo col : cols)
			{
			  System.out.println(col.toString());
			}
		}
	}
	
	
	public Map<String, List<TableInfo>> getTables()
	{
		// Get the input file stream
		final InputStream is = Content.getFileStream("tables.xml");
		
		// Instantiate the map that will hold the data from the file
		map = new HashMap<String, List<TableInfo>>(20);
		
		// Parse the file
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = null;
		try
		{
			// Parse the file using this instance of this class
			parser = factory.newSAXParser();
			parser.parse(is, this);
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		// Close the input stream
		try
		{
			is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
}
