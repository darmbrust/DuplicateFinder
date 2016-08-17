package fileCompare.gui.preview;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import fileCompare.gui.*;

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
public class Preview extends JPanel
{
	private FolderSelectionDialog fsd_;
	
	public Preview(FolderSelectionDialog fsd)
	{
		super();
		fsd_ = fsd;
		this.setLayout(new GridLayout());
	}

	public void preview(File file)
	{
		this.removeAll();
		String extension = getFileExtension(file.getName());
		if (extension.equalsIgnoreCase("jpg") || //animanted gifs crash this
				extension.equalsIgnoreCase("jpeg"))
		{
			this.add(new ImagePreview(file));
		}
		else if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("css")
				|| extension.equalsIgnoreCase("java") || extension.equalsIgnoreCase("log")
				|| extension.equalsIgnoreCase("out") || extension.equalsIgnoreCase("bas")
				|| extension.equalsIgnoreCase("bat") || extension.equalsIgnoreCase("c")
				|| extension.equalsIgnoreCase("h") || extension.equalsIgnoreCase("ini")
				|| extension.equalsIgnoreCase("py"))
		{
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuffer buffer = new StringBuffer();
				String temp = reader.readLine();
				while (temp != null)
				{
					buffer.append(temp);
					temp = reader.readLine();
				}
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				textArea.setText(buffer.toString());
			}
			catch (Exception e)
			{
				fsd_.errorLog_.errorLog_.append("Error trying to display txt file: \n");
				fsd_.errorLog_.errorLog_.append(e.toString() + "\n");
				fsd_.errorLog_.setVisible(true);
				fsd_.errorLog_.toFront();

			}
			this.add(new JScrollPane(textArea));
		}
		else if (extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("htm"))
		{
			JEditorPane editorPane = new JEditorPane();
			editorPane.setContentType("text/html");
			editorPane.setEditable(false);
			StringBuffer buffer = new StringBuffer();
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String temp = reader.readLine();
				while (temp != null)
				{
					buffer.append(temp);
					temp = reader.readLine();
				}
			}
			catch (Exception e)
			{
				fsd_.errorLog_.errorLog_.append("Error trying to display html file: \n");
				fsd_.errorLog_.errorLog_.append(e.toString() + "\n");
				fsd_.errorLog_.setVisible(true);
				fsd_.errorLog_.toFront();

			}
			editorPane.setText(buffer.toString());
			this.add(new JScrollPane(editorPane));
			try
			{ //hack to try to figure out if the editor pane is not rendering
			  // the html
				editorPane.select(0, 1);
				if (editorPane.getSelectedText() == null)
				{
					editorPane.setContentType("text");
					editorPane.setText(buffer.toString());
				}
			}
			catch (RuntimeException e1)
			{
				fsd_.errorLog_.errorLog_.append("Error trying to display html file: \n");
				fsd_.errorLog_.errorLog_.append(e1.toString() + "\n");
				fsd_.errorLog_.setVisible(true);
				fsd_.errorLog_.toFront();
			}
		}
		else
		{
			JTextArea textArea = new JTextArea();
			textArea
					.setText("No preview available for this file type.  If you think there should be a preview"
							+ " for this file type, feel free to implement it and submit it back to me, or buy me a dvd or something in exchange for"
							+ " writing it for you :)  http://armbrust.webhop.net/");
			textArea.setEditable(false);
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);
			this.add(new JScrollPane(textArea));
		}

		this.invalidate();
	}

	private String getFileExtension(String fileName)
	{
		int index = fileName.lastIndexOf(".");
		if (index == -1 || index + 1 == fileName.length())
		{
			return "";
		}
		else
		{
			return fileName.substring(index + 1, fileName.length());
		}
	}
}
