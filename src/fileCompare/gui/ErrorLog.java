package fileCompare.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

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
public class ErrorLog extends JFrame
{
	public ErrorLog()
	{
		this.setTitle("Error Log");
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		buildParts();
		JComponent panel = buildPanel();
		this.getContentPane().add(panel);
		ImageIcon appIcon = new ImageIcon(this.getClass().getResource("/fileCompare/icons/title_icon.gif"));
		this.setIconImage(appIcon.getImage());
		this.setSize(400, 300);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
	JButton				clear_, close_;
	public JTextArea	errorLog_;

	public void buildParts()
	{
		errorLog_ = new JTextArea();
		errorLog_.setWrapStyleWord(true);
		errorLog_.setLineWrap(true);
		errorLog_.setEditable(false);
		
		clear_ = new JButton("Clear");
		clear_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				errorLog_.setText("");
			}
		});
		
		close_ = new JButton("Close");
		close_.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ErrorLog.this.setVisible(false);
			}
		});
	}

	public JComponent buildPanel()
	{
		FormLayout layout = new FormLayout("m:grow, m:grow", //cols
				"m:grow, 4dlu, pref"); //rows
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		int y = 1;
		builder.add(new JScrollPane(errorLog_), cc.xywh(1, y++, 2, 1, "fill, fill"));
		y++;
		builder.add(clear_, cc.xy(1, y, "center, fill"));
		builder.add(close_, cc.xy(2, y++, "center, fill"));
		return builder.getPanel();
	}
}
