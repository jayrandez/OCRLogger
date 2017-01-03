import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

// MIT License 2017
// Jay Randez, https://github.com/jayrandez

public class Job
{	
	private OCRLogger app;
	private Descriptor desc;
	private JobView view;
	
	public Job(OCRLogger ol, Descriptor d) {
		this.app = ol;
		this.desc = d;
	}
	
	public Descriptor getDescriptor() {
		return desc;
	}
	
	public JobView getView() {
		if(view == null) {
			this.view = new JobView(this);
			fillView();
			setActionListeners();
		}
		
		return view;
	}
	
	public String getTitle() {
		return desc.title;
	}
	
	private void fillView() {
		String[] zoneOpts = new String[desc.zones.size()];
		for(int i = 1; i < zoneOpts.length; i++) {
			view.zoneSelect.addItem("" + (i+1));
		}
		actionZoneChanged();

		view.imageDescription.setText(desc.url);
		if(desc.urlValid)
			setImage(desc.url);
	}
	
	private boolean setImage(String urlString) {
		try {
			URL url = new URL(urlString);
			BufferedImage image = ImageIO.read(url);
			view.imagePanel.setImage(image);
			return true;
		}
		catch(IOException ex) {
			return false;
		}
	}
	
	private void actionSetImage() {
		String urlString = view.imageDescription.getText();
		
		if(setImage(urlString)) {
			desc.url = urlString;
			desc.urlValid = true;
			app.viewResized();
			app.saveSettings(this);
		}
		else {
			JOptionPane.showMessageDialog(view, "Couldn't load image from that URL.", "OCR Logger", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void actionAddZone() {
		desc.zones.add(null);
		desc.zoneNames.add("Untitled");
		desc.zoneTypes.add(0);
		
		int nextIndex = view.zoneSelect.getItemCount();
		view.zoneSelect.addItem("" + (nextIndex + 1));
		view.zoneSelect.setSelectedIndex(nextIndex);

		app.saveSettings(this);
	}
	
	public void boundsUpdated(Rectangle bounds) {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		desc.zones.set(zoneIndex, bounds);
		
		app.saveSettings(this);
	}
	
	private void actionZoneNameChanged() {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		desc.zoneNames.set(zoneIndex, view.fieldNameBox.getText());
		
		app.saveSettings(this);
	}
	
	private void actionZoneTypeChanged() {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		desc.zoneTypes.set(zoneIndex, view.fieldTypeSelect.getSelectedIndex());
		
		app.saveSettings(this);
	}
	
	private void actionZoneChanged() {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		
		view.fieldNameBox.setText(desc.zoneNames.get(zoneIndex));
		view.fieldTypeSelect.setSelectedIndex(desc.zoneTypes.get(zoneIndex));
		Rectangle bounds = desc.zones.get(zoneIndex);
		view.imagePanel.setBoundingRect(bounds);
	}
	
	private void setActionListeners() {
		view.setImageButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionSetImage(); }});
		
		view.addZoneButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionAddZone(); }});
		
		view.zoneSelect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionZoneChanged(); }});
		
		view.fieldNameBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionZoneNameChanged(); }});
		
		view.fieldTypeSelect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionZoneTypeChanged(); }});
	}
}
