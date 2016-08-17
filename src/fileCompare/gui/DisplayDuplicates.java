package fileCompare.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.debug.*;
import com.jgoodies.forms.layout.*;
import fileCompare.gui.preview.*;

//TODO fix up system editor stuff
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
public class DisplayDuplicates extends JFrame
{
	private JLabel				dupLabel_;
	private JList				currentDuplicates_;
	private JButton				selectAll_;
	private JButton				openInSysEditor_;
	private JButton				deleteFileDuplicates_;
	private JButton				deleteSelected_;
	private JButton				previous_;
	private JLabel				selected_;
	private JButton				next_;
	private JButton				removeAll_;
	private JEditorPane			description_;
	private Preview				preview_;
	private ArrayList			duplicates_;
	private int					currentSelection_	= 0;
	private SimpleDateFormat	formatter_;
	private FolderSelectionDialog fsl_;

	public DisplayDuplicates(ArrayList duplicates, FolderSelectionDialog fsl)
	{
		super();
		fsl_ = fsl;
		duplicates_ = duplicates;
		this.setTitle("Duplicate File Remover - Results");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		buildParts();
		JComponent panel = buildPanel();
		this.getContentPane().add(panel);
		ImageIcon appIcon = new ImageIcon(this.getClass().getResource("/fileCompare/icons/title_icon.gif"));
		this.setIconImage(appIcon.getImage());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(800, 600);
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		this.setSize(frameSize);
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		this.show();
		currentDuplicates_.setSelectedIndex(0);
	}

	private void populateList(int index)
	{
		currentDuplicates_.setListData(((ArrayList) duplicates_.get(index)).toArray(new File[((ArrayList) duplicates_
				.get(index)).size()]));
		selected_.setText("Viewing all locations of file " + (currentSelection_ + 1) + " out of " + duplicates_.size());
		preview_.preview(((File) ((ArrayList) duplicates_.get(index)).get(0)));
		if (this.isVisible()) //workaround for some wierd bug were layout gets
		// messed up if this is done when its not visible
		{
			currentDuplicates_.setSelectedIndex(0);
		}
	}

	private void updateDescription()
	{
		StringBuffer temp = new StringBuffer();
		int[] selectedFiles = currentDuplicates_.getSelectedIndices();
		temp.append("<html>");
		for (int i = 0; i < selectedFiles.length; i++)
		{
			File currentFile = ((File) ((ArrayList) duplicates_.get(currentSelection_)).get(selectedFiles[i]));
			temp.append("<b>Filename: </b>" + currentFile.getName() + "<br>");
			temp.append("<b>In Folder: </b>" + currentFile.getParentFile().getAbsolutePath() + "<br>");
			temp.append("<b>Hidden: </b>" + (currentFile.isHidden() ? "yes" : "no") + "<br>");
			long length = currentFile.length();
			String formattedLength = length + " bytes";
			if (length > 1048576)
			{
				formattedLength = length / 1024 / 1024 + " MB";
			}
			else if (length > 1024)
			{
				formattedLength = length / 1024 + " KB";
			}
			temp.append("<b>Size: </b>" + formattedLength + "<br>");
			temp.append("<b>Last Modified: </b>" + formatter_.format(new Date(currentFile.lastModified())) + "<br>");
			temp.append("<br>");
		}
		temp.append("</html>");
		description_.setText(temp.toString());
	}

	private void buildParts()
	{
		dupLabel_ = new JLabel("Identical Files");
		currentDuplicates_ = new JList();
		currentDuplicates_.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				updateDescription();
			}
		});
		selectAll_ = new JButton("Select all duplicates");
		selectAll_.setToolTipText("Select all items, except 1");
		selectAll_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] temp = new int[((ArrayList) duplicates_.get(currentSelection_)).size() - 1];
				for (int i = 0; i < temp.length; i++)
				{
					temp[i] = i + 1;
				}
				currentDuplicates_.setSelectedIndices(temp);
			}
		});
		openInSysEditor_ = new JButton("Open in System Editor");
		openInSysEditor_.setToolTipText("Open this file in your system editor");
		openInSysEditor_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[] files = currentDuplicates_.getSelectedValues();
				for (int i = 0; i < files.length; i++)
				{
					try
					{
						String[] commandOptions = new String[]{"cmd", "/c", "explorer",
								((File) files[i]).getAbsolutePath()};
						Runtime.getRuntime().exec(commandOptions);
					}
					catch (IOException e1)
					{
						fsl_.errorLog_.errorLog_.append("Error launching system editor: \n");
						fsl_.errorLog_.errorLog_.append(e1.toString() + "\n");
						fsl_.errorLog_.setVisible(true);
						fsl_.errorLog_.toFront();
					}
				}
			}
		});
		deleteFileDuplicates_ = new JButton("Remove File Duplicates");
		deleteFileDuplicates_.setToolTipText("Delete all the duplicates of this file");
		deleteFileDuplicates_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectAll_.doClick();
				deleteSelected_.doClick();
			}
		});
		deleteSelected_ = new JButton("Delete selected duplicates");
		deleteSelected_.setToolTipText("Delete the selected files");
		deleteSelected_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selectedFiles = currentDuplicates_.getSelectedIndices();
				if (selectedFiles.length == ((ArrayList) duplicates_.get(currentSelection_)).size())
				{
					int result = JOptionPane
							.showConfirmDialog(DisplayDuplicates.this,
												"Do you really want to delete ALL instances of this file?",
												"Confirm File Delete", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.NO_OPTION)
					{
						return;
					}
				}
				for (int i = selectedFiles.length - 1; i >= 0; i--)
				{
					((File) ((ArrayList) duplicates_.get(currentSelection_)).get(selectedFiles[i])).delete();
					((ArrayList) duplicates_.get(currentSelection_)).remove(selectedFiles[i]);
				}
				if (((ArrayList) duplicates_.get(currentSelection_)).size() <= 1)
				{
					duplicates_.remove(currentSelection_);
					if (currentSelection_ >= duplicates_.size())
					{
						currentSelection_--;
					}
				}
				if (duplicates_.size() == 0)
				{
					JOptionPane.showMessageDialog(DisplayDuplicates.this, "All Duplicates have been removed.");
					DisplayDuplicates.this.dispose();
				}
				else
				{
					populateList(currentSelection_);
				}
			}
		});
		previous_ = new JButton("Previous");
		previous_.setEnabled(false);
		previous_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentSelection_--;
				populateList(currentSelection_);
				if (currentSelection_ == 0)
				{
					previous_.setEnabled(false);
				}
				next_.setEnabled(true);
			}
		});
		selected_ = new JLabel("");
		next_ = new JButton("Next");
		next_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentSelection_++;
				populateList(currentSelection_);
				if (currentSelection_ + 1 == duplicates_.size())
				{
					next_.setEnabled(false);
				}
				previous_.setEnabled(true);
			}
		});
		description_ = new JEditorPane();
		description_.setText("No File Selected");
		description_.setEditable(false);
		description_.setContentType("text/html");
		removeAll_ = new JButton("Remove All Duplicates");
		removeAll_.setToolTipText("Remove all duplicates of every file");
		removeAll_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int result = JOptionPane
						.showConfirmDialog(
											DisplayDuplicates.this,
											"Do you really want to remove every duplicate copy (leaving only one instance) of every duplicate file found?",
											"Confirm Multiple File Delete", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION)
				{
					return;
				}
				else
				{
					for (int i = 0; i < duplicates_.size(); i++)
					{
						ArrayList temp = (ArrayList) duplicates_.get(i);
						for (int j = 1; j < temp.size(); j++)
						{
							((File) temp.get(j)).delete();
						}
					}
					JOptionPane.showMessageDialog(DisplayDuplicates.this, "All Duplicates have been removed.");
					DisplayDuplicates.this.dispose();
				}
			}
		});
		preview_ = new Preview(fsl_);
		formatter_ = new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss a");
		populateList(0);
	}

	private JPanel buildPanel()
	{
		FormLayout layout = new FormLayout("50dlu, 4dlu, center:p:grow, 4dlu, 50dlu, 4dlu, 50dlu, 4dlu, 110dlu", //cols
				"p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, bottom:m:grow(.3), 4dlu, p, 4dlu, p, 4dlu, m:grow(.7)"); //rows
		//layout.setRowGroups(new int[][]{ {9, 15}});
		//PanelBuilder builder = new PanelBuilder(new FormDebugPanel(),
		// layout);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		int y = 1;
		builder.addSeparator("Duplicate Files", cc.xywh(1, y++, 7, 1));
		y++;
		JScrollPane temp = new JScrollPane(currentDuplicates_);
		temp.getViewport().setBackground(Color.white);
		builder.add(temp, cc.xywh(1, y, 7, 7, "fill, fill"));
		builder.add(selectAll_, cc.xy(9, y++));
		y++;
		builder.add(deleteSelected_, cc.xy(9, y++));
		y++;
		builder.add(deleteFileDuplicates_, cc.xy(9, y++));
		y++;
		builder.add(openInSysEditor_, cc.xy(9, y++));
		y++;
		builder.add(previous_, cc.xy(1, y));
		builder.add(selected_, cc.xywh(3, y, 3, 1, "center, center"));
		builder.add(next_, cc.xy(7, y));
		builder.add(removeAll_, cc.xy(9, y++));
		y++;
		builder.addSeparator("File Preview", cc.xywh(1, y, 3, 1));
		builder.addSeparator("File Details", cc.xywh(5, y++, 5, 1));
		y++;
		builder.add(preview_, cc.xywh(1, y, 3, 1, "fill, fill"));
		JScrollPane temp2 = new JScrollPane(description_);
		builder.add(temp2, cc.xywh(5, y++, 5, 1, "fill, fill"));
		return builder.getPanel();
	}
}
