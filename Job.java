import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

// MIT License 2017
// Jay Randez, https://github.com/jayrandez

public class Job
{	
	private OCRLogger app;
	private Descriptor desc;
	private JobView view;

	public static class Descriptor
	{
		public String title;
	}
	
	public Job(OCRLogger ol) {
		this.app = ol;
		this.desc = new Descriptor();
		desc.title = "New Job";
	}
	
	public Job(OCRLogger ol, Descriptor d) {
		this.app = ol;
		this.desc = d;
	}
	
	public Descriptor getDescriptor() {
		return desc;
	}
	
	public JobView getView() {
		if(view == null) {
			this.view = new JobView();
			setActionListeners();
		}
		
		return view;
	}
	
	public String getTitle() {
		return desc.title;
	}
	
	private void actionSetImage() {
		try {
			URL url = new URL(view.imageDescription.getText());
			BufferedImage image = ImageIO.read(url);
			view.imagePanel.setImage(image);
			app.viewResized();
		}
		catch(IOException ex) {
			JOptionPane.showMessageDialog(view, "Couldn't load image from that URL.", "OCR Logger", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	private void setActionListeners() {
		view.setImageButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionSetImage(); }});
	}
}
