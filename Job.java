// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;

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
		
		if(desc.filenameValid)
			view.fileDescription.setText(desc.filename);
		
		fillScheduleDetails();
		
		fillJobStatusDetails();
	}
	
	private boolean fillImage(String urlString) {
		try {
			URL url = new URL(urlString);
			BufferedImage image = ImageIO.read(url);
			if(image == null)
				throw new IOException();
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
	
	private void fillJobStatusDetails() {
		if(desc.jobStarted) {
			view.startButton.setVisible(false);
			view.jobNotc.setVisible(true);
			view.jobNotc.setText("Job Started");
			
			view.imageDescription.setEnabled(false);
			view.setImageButton.setEnabled(false);
			view.setFileButton.setEnabled(false);
			view.radioGroup.setEnabled(false);
			view.addZoneButton.setEnabled(false);
			view.fieldTypeSelect.setEnabled(false);
			view.fieldNameBox.setEnabled(false);
			view.imagePanel.setEnabled(false);
			view.dayField.setEnabled(false);
			view.hourField.setEnabled(false);
			view.minuteField.setEnabled(false);
		}
	}
	
	public void boundsUpdated(Rectangle bounds) {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		desc.zones.set(zoneIndex, bounds);
		
		saveSettings();
	}
	
	public void updateStatus(String message) {
		view.jobNotc.setText("Job Started - " + message);
	}

	private void actionZoneChanged() {
		int zoneIndex = view.zoneSelect.getSelectedIndex();
		fillZoneDetails(zoneIndex);
	}
	
	private void actionSetImage() {
		String urlString = view.imageDescription.getText();
		
		if(fillImage(urlString)) {
			desc.url = urlString;
			desc.urlValid = true;
			app.viewResized();
			saveSettings();
		}
		else {
			JOptionPane.showMessageDialog(view, "Couldn't load image from that URL.", "OCR Logger", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void actionSetFile() {
		JFileChooser chooser = new JFileChooser();
		
		if(chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION){ 
			try {
				File outFile = chooser.getSelectedFile();
				if(!outFile.createNewFile())
					throw new IOException();
				desc.filename = outFile.getAbsolutePath();
				desc.filenameValid = true;
				desc.title = outFile.getName();
				app.jobRenamed(this);
				view.fileDescription.setText(desc.filename);
				saveSettings();
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(view,
					"Please name a writeable file which does not yet exist.",
					"OCR Logger",
					JOptionPane.ERROR_MESSAGE);
			}
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
		if(desc.urlValid && desc.filenameValid) {
			app.jobStarted(this);
			desc.jobStarted = true;
			fillJobStatusDetails();
			saveSettings();
		}
		else {
			JOptionPane.showMessageDialog(view,
				"Valid image and output file required to start job.",
				"OCR Logger",
				JOptionPane.ERROR_MESSAGE);
		}
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
		
		int day = confirmFieldLimits(view.dayField.getText(), 0, 99, "days");
		if(day >= 0) {
			desc.scheduleDay = day;
			saveSettings();
		}
	}
	
	private void actionScheduleHourChanged() {
		if(ignoreActions)
			return;
		
		int hour = confirmFieldLimits(view.hourField.getText(), 0, 23, "hours");
		if(hour >= 0) {
			desc.scheduleHour = hour;
			saveSettings();
		}
	}
	
	private void actionScheduleMinuteChanged() {
		if(ignoreActions)
			return;
		
		int minute = confirmFieldLimits(view.minuteField.getText(), 1, 59, "minutes");
		if(minute >= 0) {
			desc.scheduleMinute = minute;
			saveSettings();
		}
	}
	
	private int confirmFieldLimits(String text, int min, int max, String fieldName) {
		try {
			int value = Integer.parseInt(text);
			if(value < min || value > max)
				throw new Exception();
			return value;
		}
		catch(Exception ex) {
			JOptionPane.showMessageDialog(view,
				"For " + fieldName + " please enter an integer from " + min + "-" + max + ".",
				"OCR Logger",
				JOptionPane.ERROR_MESSAGE);
			fillScheduleDetails();
			return -1;
		}
	}

	private void saveSettings() {
		app.saveSettings(this);
	}

	private void setActionListeners() {
		view.setImageButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionSetImage(); }});
		
		view.setFileButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionSetFile(); }});
		
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
