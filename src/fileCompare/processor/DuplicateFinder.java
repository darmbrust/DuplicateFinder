package fileCompare.processor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import jonelo.jacksum.algorithm.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;
import fileCompare.gui.*;
import foxtrot.*;

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
public class DuplicateFinder
{
	private AbstractChecksum		checksum_	= new MD("MD5");
	private Hashtable				md5s_		= new Hashtable();
	private FolderSelectionDialog	fsd_;
	private JLabel					status;
	private boolean					cancel_		= false;
	private JDialog					dialog;

	/**
	 * This class actually finds the duplicates, and displays some status in a
	 * dialog box while it is searching for them.
	 * 
	 * @param fsd
	 */
	public DuplicateFinder(FolderSelectionDialog fsd)
	{
		fsd_ = fsd;
		dialog = new JDialog(fsd_, "Processing....", false);
		FormLayout layout = new FormLayout("4dlu, c:pref:grow", "c:pref:grow, 4dlu, c:pref:grow");
		PanelBuilder builder = new PanelBuilder(layout);
		status = new JLabel("Preparing to process");
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				cancel_ = true;
			}
		});
		builder.add(status, new CellConstraints(2, 1, CellConstraints.LEFT, CellConstraints.CENTER));
		builder.nextLine(2);
		builder.nextColumn();
		builder.add(cancel);
		dialog.getContentPane().add(builder.getPanel());
		dialog.setSize(300, 150);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = dialog.getSize();
		dialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}

	/**
	 * Check the selected folders for duplicates.
	 * 
	 * @return
	 */
	public boolean go()
	{
		dialog.setVisible(true);
		try
		{
			Worker.post(new Task()
			{
				public Object run() throws Exception
				{
					File[] files = fsd_.getFilesToProcess();
					for (int i = 0; i < files.length; i++)
					{
						processFile(files[i]);
					}
					return null;
				}
			});
		}
		catch (Exception e)
		{
			fsd_.errorLog_.errorLog_.append("Problem starting duplicate finding thread: \n");
			fsd_.errorLog_.errorLog_.append(e.toString() + "\n");
			fsd_.errorLog_.setVisible(true);
			fsd_.errorLog_.toFront();
		}
		if (fsd_.getByteForByteCheck().isSelected())
		{
			status.setText("Doing byte for byte checks of found duplicates");
			doByteForByteCheck();
		}
		dialog.setVisible(false);
		return !cancel_;
	}

	/**
	 * Compare all of the files in the duplicate list byte for byte with each
	 * other. Remove any items from the dup list that fail the byte for byte
	 * check.
	 *  
	 */
	private void doByteForByteCheck()
	{
		ArrayList temp = getDuplicateFiles();
		for (int i = 0; i < temp.size(); i++)
		{
			if (cancel_)
				break;
			ArrayList inner = (ArrayList) temp.get(i);
			for (int j = 1; j < inner.size(); j++)
			{
				try
				{
					if (cancel_)
						break;
					FileInputStream fis1 = new FileInputStream((File) inner.get(0));
					FileInputStream fis2 = new FileInputStream((File) inner.get(j));
					int item = fis1.read();
					while (item != -1)
					{
						if (item != fis2.read())
						{
							inner.remove(j--); //remove and decrement j so it
											   // checks the next file
							break;
						}
						item = fis1.read();
					}
					fis1.close();
					fis2.close();
				}
				catch (FileNotFoundException e)
				{
					fsd_.errorLog_.errorLog_.append("Problem doing byte for byte check: \n");
					fsd_.errorLog_.errorLog_.append(e.toString() + "\n");
					fsd_.errorLog_.setVisible(true);
					fsd_.errorLog_.toFront();

				}
				catch (IOException e)
				{
					fsd_.errorLog_.errorLog_.append("Problem doing byte for byte check: \n");
					fsd_.errorLog_.errorLog_.append(e.toString() + "\n");
					fsd_.errorLog_.setVisible(true);
					fsd_.errorLog_.toFront();
				}
			}
		}
	}

	/**
	 * Get the arrayList (of arrayLists) of duplicate files.
	 * 
	 * @return
	 */
	public ArrayList getDuplicateFiles()
	{
		ArrayList temp = new ArrayList();
		Iterator iter = md5s_.values().iterator();
		while (iter.hasNext())
		{
			ArrayList element = (ArrayList) iter.next();
			if (element.size() > 1)
			{
				temp.add(element);
			}
		}
		return temp;
	}

	/**
	 * process a file or directory - recursively. Added md5 checksums for each
	 * file into a hasmap, if the checksum is already in the hashmap, adds the
	 * filename to the arraylist value for the checksum.
	 * 
	 * @param file
	 */
	private void processFile(File file)
	{
		File[] temp = file.listFiles();
		status.setText("Processing " + file.getName());
		for (int i = 0; i < temp.length; i++)
		{
			if (cancel_)
				break;
			File file2 = temp[i];
			if (file2.isDirectory())
			{
				if (fsd_.getRecursive().isSelected())
				{
					processFile(file2);
				}
			}
			else
			{
				try
				{
					checksum_.readFile(file2.getAbsolutePath());
					String checksum = checksum_.getHexValue();
					if (md5s_.get(checksum) == null)
					{
						ArrayList tempFiles = new ArrayList();
						tempFiles.add(file2);
						md5s_.put(checksum, tempFiles);
					}
					else
					{
						((ArrayList) md5s_.get(checksum)).add(file2);
					}
				}
				catch (IOException e)
				{
					fsd_.errorLog_.errorLog_.append("Problem getting check sum: \n");
					fsd_.errorLog_.errorLog_.append(e.toString() + "\n");
					fsd_.errorLog_.setVisible(true);
					fsd_.errorLog_.toFront();
				}
			}
		}
	}
}
