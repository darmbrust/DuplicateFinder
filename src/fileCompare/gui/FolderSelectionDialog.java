package fileCompare.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;
import fileCompare.processor.*;

/**
 * <pre>
 *  Copyright (c) 2010  Daniel Armbrust.  All Rights Reserved.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  The license was included with the download.
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * </pre>
 * 
 * @author <A HREF="mailto:daniel.armbrust@gmail.com">Daniel Armbrust</A>
 */

public class FolderSelectionDialog extends JFrame
{
	JList				folders;
	JButton				add;
	JFileChooser		directoryChooser;
	JButton				remove;
	JButton				findDups;
	JCheckBox			recursive;
	JCheckBox			byteForByteCheck;
	JButton				exit;
	private Icon		folder;
	private ArrayList	filesToProcess	= new ArrayList();
	
	public ErrorLog errorLog_;

	/**
	 * This is the first screen displayed for the dupliate finder. Allows you
	 * to select folders to check for duplcates.
	 */
	public FolderSelectionDialog()
	{
		super();
		try
		{
			UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
		}
		catch (Exception e)
		{
			// should error log this somewhere
			e.printStackTrace();
		}
		this.setTitle("Duplicate File Remover");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buildParts();
		buildMenus();
		JComponent panel = buildPanel();
		this.getContentPane().add(panel);
		ImageIcon appIcon = new ImageIcon(this.getClass().getResource("/fileCompare/icons/title_icon.gif"));
		this.setIconImage(appIcon.getImage());
		this.setSize(640, 480);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		this.show();
		errorLog_ = new ErrorLog();
	}

	/**
	 * Build the gui.
	 *  
	 */
	public void buildParts()
	{
		folders = new JList();
		folders.setCellRenderer(new MyCellRenderer());
		directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directoryChooser.setMultiSelectionEnabled(true);
		directoryChooser.setFileHidingEnabled(false);
		add = new JButton("Add Folder");
		add.setToolTipText("Add folders to search for duplicate files.");
		add.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				File tempDir = directoryChooser.getCurrentDirectory();
				directoryChooser.setCurrentDirectory(tempDir.getParentFile());
				directoryChooser.setCurrentDirectory(tempDir);
				int returnVal = directoryChooser.showOpenDialog(FolderSelectionDialog.this);
				if (returnVal != 0)
				{
					return;
				}
				File[] temp = directoryChooser.getSelectedFiles();
				if (folder == null)
				{
					folder = directoryChooser.getIcon(temp[0]);
				}
				FolderSelectionDialog.this.addFiles(temp);
				folders.setListData(FolderSelectionDialog.this.getFilesToProcess());
			}
		});
		remove = new JButton("Remove Selected");
		remove.setToolTipText("<html>This removes the selected folders from the list of folders to search for duplicates.<br>" +
							  "This does not delete any of your files.</html>");
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FolderSelectionDialog.this.removeFiles(folders.getSelectedIndices());
				folders.setListData(FolderSelectionDialog.this.getFilesToProcess());
			}
		});
		findDups = new JButton("Find Duplicates");
		findDups.setToolTipText("Search these folders for duplicate files");
		findDups.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					DuplicateFinder finder = new DuplicateFinder(FolderSelectionDialog.this);
					Component glassPane = FolderSelectionDialog.this.getGlassPane();
					glassPane.addMouseListener(new MouseListener()
					{
						public void mouseClicked(MouseEvent ex)
						{
							ex.consume();
						}

						public void mousePressed(MouseEvent ex)
						{
							ex.consume();
						}

						public void mouseReleased(MouseEvent ex)
						{
							ex.consume();
						}

						public void mouseEntered(MouseEvent ex)
						{
							ex.consume();
						}

						public void mouseExited(MouseEvent ex)
						{
							ex.consume();
						}
					});
					glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					glassPane.setVisible(true);
					boolean completed = finder.go();
					glassPane.setVisible(false);
					if (completed)
					{
						FolderSelectionDialog.this.displayDuplicates(finder.getDuplicateFiles());
					}
				}
				catch (Exception e1)
				{
					FolderSelectionDialog.this.errorLog_.errorLog_.append("Error scanning files: \n");
					FolderSelectionDialog.this.errorLog_.errorLog_.append(e1.toString() + "\n");
					FolderSelectionDialog.this.errorLog_.setVisible(true);
					FolderSelectionDialog.this.errorLog_.toFront();
				}
			}
		});
		recursive = new JCheckBox("Recursive");
		recursive.setSelected(true);
		recursive.setToolTipText("Process the contents of all subfolders");
		
		byteForByteCheck = new JCheckBox("Byte for Byte Check");
		byteForByteCheck.setSelected(false);
		byteForByteCheck.setToolTipText("<html>Check any files initially thought to be duplicates at the byte level.<br> " +
				" This is for the paranoid only - it is not necessary, and will make the process much slower.</html>");
		exit = new JButton("Exit");
		exit.setToolTipText("Exit the program");
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
	}

	/*
	 * Display the found duplicates. @param duplicates
	 */
	private void displayDuplicates(ArrayList duplicates)
	{
		if (duplicates.size() < 1)
		{
			JOptionPane.showMessageDialog(this, "There were no duplicate files found.");
			return;
		}
		new DisplayDuplicates(duplicates, this);
	}

	public JComponent buildPanel()
	{
		FormLayout layout = new FormLayout("pref:grow, 4dlu, pref",
				"pref, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu:grow, pref, 4dlu, pref");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		int y = 1;
		builder.addSeparator("Folders to search for duplicates in:", cc.xywh(1, y++, 3, 1));
		builder.add(new JScrollPane(folders), cc.xywh(1, y, 1, 11));
		builder.add(add, cc.xy(3, y++));
		y++;
		builder.add(remove, cc.xy(3, y++));
		y++;
		builder.add(recursive, cc.xy(3, y++));
		y++;
		builder.add(byteForByteCheck, cc.xy(3, y++));
		y++;
		builder.add(findDups, cc.xy(3, y++));
		y++;
		builder.add(exit, cc.xy(3, y++));
		return builder.getPanel();
	}

	private void addFiles(File[] files)
	{
		for (int i = 0; i < files.length; i++)
		{
			File current = files[i];
			boolean alreadyExists = false;
			for (int j = 0; j < filesToProcess.size(); j++)
			{
				if (current.getAbsolutePath().equals(((File) filesToProcess.get(j)).getAbsolutePath()))
				{
					alreadyExists = true;
					break;
				}
			}
			if (!alreadyExists)
			{
				filesToProcess.add(current);
			}
		}
	}

	private void removeFiles(int[] files)
	{
		for (int i = 0; i < files.length; i++)
		{
			filesToProcess.remove(files[i] - i);
		}
	}

	private void buildMenus()
	{
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem fileMenuExit = new MenuItem("Exit");
		fileMenuExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		Menu viewMenu = new Menu("View");
		MenuItem viewMenuViewErrors = new MenuItem("Display Error Log");
		viewMenuViewErrors.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				errorLog_.setVisible(true);
			}
		});
		Menu helpMenu = new Menu("Help");
		MenuItem helpMenuAbout = new MenuItem("About...");
		helpMenuAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(FolderSelectionDialog.this, "Duplicate Finder 1.1\n"
						+ "Copyright 2004, Daniel Armbrust All Rights Reserved\n"
						+ "This code is licensed under the Apache License - v 2.0. \n"
						+ "A full copy of the license has been included with the distribution.\n"
						+ "E-Mail me at daniel.armbrust@gmail.com.\n" + "or visit http://armbrust.webhop.net/",
												"Duplicate Finder 1.1", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		fileMenu.add(fileMenuExit);
		viewMenu.add(viewMenuViewErrors);
		helpMenu.add(helpMenuAbout);
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);
		FolderSelectionDialog.this.setMenuBar(menuBar);
	}

	public File[] getFilesToProcess()
	{
		return (File[]) filesToProcess.toArray(new File[filesToProcess.size()]);
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value, // value to display
				int index, // cell index
				boolean isSelected, // is the cell selected
				boolean cellHasFocus) // the list and the cell have the focus
		{
			String s = value.toString();
			setText(s);
			setIcon(folder);
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}

	public static void main(String[] args) throws Exception
	{
		new FolderSelectionDialog();
	}

	/**
	 * @return
	 */
	public JCheckBox getByteForByteCheck()
	{
		return byteForByteCheck;
	}

	/**
	 * @return
	 */
	public JCheckBox getRecursive()
	{
		return recursive;
	}
}
