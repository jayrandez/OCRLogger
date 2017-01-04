// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import net.sourceforge.tess4j.*;

public class TesseractManager
{
	ITesseract inst;
	
	public TesseractManager() {
		this.inst = new Tesseract();
		// Make sure tessdata is in local directory!!!
		inst.setLanguage("eng");
	}
	
	public Object[] runOCR(Descriptor desc) {
		try {
			URL url = new URL(desc.url);
			BufferedImage image = ImageIO.read(url);
			if(image == null)
				throw new IOException();
			File imageFile = new File("image.png");

			int numObjects = desc.zoneNames.size();
			Object[] output = new Object[numObjects];
			
			for(int i = 0; i < desc.zoneNames.size(); i++) {
				Rectangle zone = desc.zones.get(i);
				int zoneType = desc.zoneTypes.get(i);
				
				if(zone == null) {
					// No zone was selected, output value is null
					output[i] = null;
				}
				else if(zoneType == Descriptor.ImageField) {
					// OCR is not run for this field, output value is BufferedImage
					output[i] = subImage(image, zone);
				}
				else {
					// OCR is run, output value is either String or Number
					ImageIO.write(subImage(image, zone), "png", imageFile);
					
					try {
						String result = inst.doOCR(imageFile);
						
						if(zoneType == Descriptor.StringField)
							output[i] = result.trim();
						else if(zoneType == Descriptor.NumberField)
							output[i] = Double.parseDouble(result);
					}
					catch(Exception ex) {
						// OCR failed, output value is null
						output[i] = null;
						ex.printStackTrace();
					}
				}
				
				System.out.println("Zone #" + i + " - " + output[i]);
			}
			
			return output;
		}
		catch(IOException ex) {
			// Couldn't load image from URL, return empty data set
			ex.printStackTrace();
			return new Object[] {};
		}
	}
	
	public BufferedImage subImage(BufferedImage image, Rectangle zone) {
		int x1 = zone.x - 10;
		int y1 = zone.y - 10;
		int x2 = x1 + zone.width;
		int y2 = y1 + zone.height;
		
		if(x1 > image.getWidth() - 1)
			x1 = image.getWidth() - 1;
		
		if(y1 > image.getHeight() - 1)
			y1 = image.getHeight() - 1;
		
		if(x2 > image.getWidth())
			x2 = image.getWidth();
		
		if(y2 > image.getHeight())
			y2 = image.getHeight();
		
		int width = x2 - x1;
		int height = y2 - y1;
		
		System.out.println("Subimage: (" + x1 + ", " + y1 + ") " + width + " X " + height);
		
		return image.getSubimage(x1, y1, width, height);
	}
}
