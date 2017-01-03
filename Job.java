// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Job
{	
	private OCRLogger app;
	private Descriptor desc;
	private JobView view;
	private boolean ignoreActions;
	
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
		fillZoneDetails(0);

		view.imageDescription.setText(desc.url);
		if(desc.urlValid)
			fillImage(desc.url);
		
		fillScheduleDetails();
	}
	
	private boolean fillImage(String urlString) {
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
	
	private void fillZoneDetails(int zoneIndex) {
		ignoreActions = true;
		
		view.fieldNameBox.setText(desc.zoneNames.get(zoneIndex));
		view.fieldTypeSelect.setSelectedIndex(desc.zoneTypes.get(zoneIndex));
		Rectangle bounds = desc.zones.get(zoneIndex);
		view.imagePanel.setBoundingRect(bounds);
		
		ignoreActions = false;
	}
	
	private void fillScheduleDetails() {
		ignoreActions = true;
		
		view.radioGroup.setSelectedIndex(desc.scheduleType);
		view.dayField.setText("" + desc.scheduleDay);
		view.hourField.setText("" + desc.scheduleHour);
		view.minuteField.setText("" + desc.scheduleMinute);
		
		ignoreActions = false;
	}
	
	private void actionZoneChanged() {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		fillZoneDetails(zoneIndex);
	}
	
	public void boundsUpdated(Rectangle bounds) {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		desc.zones.set(zoneIndex, bounds);
		
		saveSettings();
	}
	
	private void actionSetImage() {
		String urlString = view.imageDescription.getText();
		
		if(fillImage(urlString)) {
			desc.urlValid = true;
			app.viewResized();
			saveSettings();
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

		saveSettings();
	}
	
	private void actionStartJob() {
		System.out.println("Starting job");
	}

	private void actionZoneNameChanged() {
		if(ignoreActions)
			return;
		
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		desc.zoneNames.set(zoneIndex, view.fieldNameBox.getText());
		saveSettings();
	}
	
	private void actionZoneTypeChanged() {
		if(ignoreActions)
			return;
		
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		desc.zoneTypes.set(zoneIndex, view.fieldTypeSelect.getSelectedIndex());
		saveSettings();
	}
	
	private void actionScheduleTypeChanged() {
		if(ignoreActions)
			return;
		
		int index = view.radioGroup.getSelectedIndex();
		desc.scheduleType = index;
		saveSettings();
	}
	
	private void actionScheduleDayChanged() {
		if(ignoreActions)
			return;
		
		try {
			int day = Integer.parseInt(view.dayField.getText());
			if(day < 0 || day > 99)
				throw new Exception();
			desc.scheduleDay = day;
			saveSettings();
		}
		catch(Exception ex) {
			JOptionPane.showMessageDialog(view, "For days please enter an integer from 0-99.", "OCR Logger", JOptionPane.ERROR_MESSAGE);
			fillScheduleDetails();
		}
	}
	
	private void actionScheduleHourChanged() {
		if(ignoreActions)
			return;
		
		try {
			int hour = Integer.parseInt(view.hourField.getText());
			if(hour < 0 || hour > 23)
				throw new Exception();
			desc.scheduleHour = hour;
			saveSettings();
		}
		catch(Exception ex) {
			JOptionPane.showMessageDialog(view, "For hours please enter an integer from 0-23.", "OCR Logger", JOptionPane.ERROR_MESSAGE);
			fillScheduleDetails();
		}
	}
	
	private void actionScheduleMinuteChanged() {
		if(ignoreActions)
			return;
		
		try {
			int minute = Integer.parseInt(view.minuteField.getText());
			if(minute < 0 || minute > 59)
				throw new Exception();
			desc.scheduleMinute = minute;
			saveSettings();
		}
		catch(Exception ex) {
			JOptionPane.showMessageDialog(view, "For minutes please enter an integer from 0-59.", "OCR Logger", JOptionPane.ERROR_MESSAGE);
			fillScheduleDetails();
		}
	}

	private void saveSettings() {
		app.saveSettings(this);
	}

	private void setActionListeners() {
		view.setImageButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionSetImage(); }});
		
		view.addZoneButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionAddZone(); }});
		
		view.zoneSelect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionZoneChanged(); }});
		
		view.fieldNameBox.setModListener(new JTextFieldMod.Listener() {
		public void modified() { actionZoneNameChanged(); }});
		
		view.fieldTypeSelect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionZoneTypeChanged(); }});
		
		view.startButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionStartJob(); }});
		
		view.radioGroup.setModListener(new ButtonGroupMod.Listener() {
		public void action() { actionScheduleTypeChanged(); }});
		
		view.dayField.setModListener(new JTextFieldMod.Listener() {
		public void modified() { actionScheduleDayChanged(); }});
		
		view.hourField.setModListener(new JTextFieldMod.Listener() {
		public void modified() { actionScheduleHourChanged(); }});
		
		view.minuteField.setModListener(new JTextFieldMod.Listener() {
		public void modified() { actionScheduleMinuteChanged(); }});
	}
}
