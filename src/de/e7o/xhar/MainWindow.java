/*
    xHar - tool for extracting files out of htt archives (.har)
    Copyright (C) 2016 sven@e7o.de
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.e7o.xhar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MainWindow extends JFrame
{
	private static MainWindow windowInstance;
	
	private JLabel dropLabel;
	private JScrollPane fileListPane;
	private TableData data;
	private boolean openFile = false;
	private JButton export;
	
	public static void main(String args[])
	{
		windowInstance = new MainWindow();
		windowInstance.setVisible(true);
	}
	
	public MainWindow()
	{
		setTitle("xHar | extractor for http archives");
		setSize(800,  600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		addMenus();
		addContent();
	}
	
	private void addMenus()
	{
		JMenu m;
		JMenuItem mi;
		JMenuBar menu = new JMenuBar();
		
		m = new JMenu("File");
		menu.add(m);
		
		mi = new JMenuItem("Open ...");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(windowInstance, "TODO", "xHar", JOptionPane.OK_OPTION);
			}
		});
		m.add(mi);
		
		m.add(new JSeparator());
		
		mi = new JMenuItem("Close");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(windowInstance, "TODO", "xHar", JOptionPane.OK_OPTION);
			}
		});
		m.add(mi);
		
		m.add(new JSeparator());
		
		mi = new JMenuItem("Exit");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowInstance.setVisible(false);
				windowInstance.dispose();
			}
		});
		m.add(mi);
		
		m = new JMenu("?");
		menu.add(m);
		
		mi = new JMenuItem("About ...");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
					windowInstance,
					"(c) 2016 by Sven Herzky (sven@e7o.de).\nLicense: GPLv3 or later, see code for details.",
					"xHar",
					JOptionPane.OK_OPTION
				);
			}
		});
		m.add(mi);
		
		this.setJMenuBar(menu);
	}
	
	private void addContent()
	{
		dropLabel = new JLabel("Drop a .har file here to view its content");
		dropLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dropLabel.setTransferHandler(new javax.swing.TransferHandler() {
			public boolean canImport(JComponent component, DataFlavor transferFlavors[])
			{
				for (DataFlavor d : transferFlavors) {
					if (d.isFlavorJavaFileListType()) {
						return true;
					}
				}
				
				return false;
	      	}
			
			public boolean importData(JComponent component, java.awt.datatransfer.Transferable transferable)
			{
				try {
					List l = (List)transferable.getTransferData(DataFlavor.javaFileListFlavor);
					File f = (File)l.get(0);
					openFile(f);
					return true;
				} catch (Exception e) {
					dropLabel.setText(e.getMessage());
				}
				
				return false;
			}
		});
		
		data = new TableData();
		JTable fileList = new JTable(data);
		
		// TODO
		fileList.getColumnModel().getColumn(0).setWidth(50);
		fileList.getColumnModel().getColumn(1).setWidth(50);
		fileList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		fileListPane = new JScrollPane(fileList);
		fileList.setFillsViewportHeight(true);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		export = new JButton();
		export.setText("Export");
		export.setPreferredSize(new Dimension(200, 32));
		export.setEnabled(false);
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				Path p;
				String s;
				TableData.DataRow row;
				String exportTo = DialogHelper.selectDirectory("Export to which directory?", windowInstance);
				for (int i = 0; i < data.getRowCount(); i++) {
					row = data.getDataRow(i);
					if (row.export) {
						p = Paths.get(exportTo + "/export_" + i + ".tmp");
						System.out.println(p);
						try {
							s = ((JSONObject)row.response.get("content")).get("text").toString();
							if (s.length() > 0) {
								Files.write(p, s.getBytes());
							}
						} catch (IOException e) {
							// TODO
							System.out.println(e.getMessage());
						}
					}
				}
			}
		});
		bottom.add(export);
		
		this.getContentPane().add(bottom, BorderLayout.SOUTH);
		
		// TODO Statusbar
		
		switchOpenClose(true);
	}
	
	private void switchOpenClose(boolean initialCall)
	{
		if (initialCall) {
			openFile = true;
		}
		
		if (openFile) {
			// Close everything, restore to default view
			getContentPane().add(dropLabel, BorderLayout.CENTER);
			if (!initialCall) {
				getContentPane().remove(fileListPane);
			}
		} else {
			// Open a file, remove default view
			getContentPane().remove(dropLabel);
			getContentPane().add(fileListPane, BorderLayout.CENTER);
		}
		openFile = !openFile;
		
		export.setEnabled(openFile);
	}
	
	private void openFile(File f) throws Exception
	{
		switchOpenClose(false);
		data.clearRows();
		
		JSONParser parser = new JSONParser();
		JSONObject all = (JSONObject)parser.parse(new FileReader(f));
		JSONArray entries = (JSONArray)((JSONObject)all.get("log")).get("entries");
		
		for (Object o : entries) {
			JSONObject j = (JSONObject)o;
			JSONObject request = (JSONObject)j.get("request");
			JSONObject response = (JSONObject)j.get("response");
			data.addRow(
				true,
				new String[] {
					request.get("method").toString(),
					response.get("status").toString(),
					request.get("url").toString(),
				},
				response
			);
		}
		
		getContentPane().revalidate();
	}
}