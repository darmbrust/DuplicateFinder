package fileCompare.gui.preview;

import java.awt.*;
import java.io.*;
import javax.swing.*;

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
public class ImagePreview extends JPanel
{
	private String	imageLocation	= "";

	public ImagePreview(File file)
	{
		this.setOpaque(false);
		this.imageLocation = file.getAbsolutePath();
		//this.repaint();
	}

	public void paintComponent(Graphics g)
	{
		ImageIcon img = new ImageIcon(imageLocation);
		ImageIcon fillImage = new ImageIcon(img.getImage().getScaledInstance(getWidth(), getHeight(),
																				Image.SCALE_REPLICATE));
		g.drawImage(fillImage.getImage(), 0, 0, this);
		super.paintComponent(g);
	}
	//	public static void main(String[] args)
	//	{
	//		JFrame frame = new JFrame();
	//		frame.setSize(300, 200);
	//		JPanel foo = new ImagePreview();
	//		foo.setOpaque(false);
	//		frame.setContentPane(foo);
	//		frame.setVisible(true);
	//	}
}