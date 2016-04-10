package de.e7o.xhar;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class DialogHelper
{
	public static String selectDirectory(String title, JFrame toWindow)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new java.io.File("."));
		if (chooser.showOpenDialog(toWindow) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}
}