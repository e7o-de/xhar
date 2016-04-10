// xHar (C) 2016 sven@e7o.de, License: GPLv3 or later
package de.e7o.xhar;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.json.simple.JSONObject;

class TableData implements TableModel
{
	protected class DataRow
	{
		public boolean export;
		public String data[];
		public JSONObject response;
		
		public DataRow(boolean active, String data[], JSONObject response)
		{
			this.export = active;
			this.data = data;
			this.response = response;
		}
	}
	
	private String columns[];
	private ArrayList<DataRow> rows;
	
	public TableData()
	{
		rows = new ArrayList<>();
		this.columns = new String[]{"Export", "Method", "Status", "URL"};
	}
	
	public void clearRows()
	{
		rows.clear();
	}
	
	public void addRow(boolean export, String data[], JSONObject response)
	{
		rows.add(new DataRow(export, data, response));
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return rows.get(rowIndex).export;
			default:
				return rows.get(rowIndex).data[columnIndex - 1];
		}
	}
	
	public DataRow getDataRow(int rowIndex)
	{
		return rows.get(rowIndex);
	}
	
	public int getRowCount()
	{
		return rows.size();
	}
	
	public String getColumnName(int columnIndex)
	{
		return columns[columnIndex];
	}
	
	public int getColumnCount()
	{
		return columns.length;
	}
	
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return Boolean.class;
			default:
				return String.class;
		}
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 0;
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				rows.get(rowIndex).export = ((Boolean)aValue).booleanValue();
		}
	}
	
	// TODO Works without right now, but to be save ... ;)
	public void addTableModelListener(TableModelListener l) {}
	public void removeTableModelListener(TableModelListener l) {}
}