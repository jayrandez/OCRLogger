// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.image.BufferedImage;

public class Tesseract
{
	public Object[] runOCR(Descriptor desc) {
		Object[] output = new Object[3];
		output[0] = new Double(3.2);
		output[1] = "String";
		output[2] = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
		
		return output;
	}
}
