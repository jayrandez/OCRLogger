// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class OCRLogger
{
	private Tesseract tess;
	private ViewFrame view;
	private Settings settings;
	private ArrayList<Job> jobs;
	
	public OCRLogger() {
		this.tess = new Tesseract();
		
		this.view = new ViewFrame();
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setActionListeners();
		
		this.settings = new Settings(this);
		
		this.jobs = settings.getJobs();
		if(jobs == null) {
			JOptionPane.showMessageDialog(view, "Couldn't open/create settings file. Local dir permissions?", "OCR Logger", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		for(Job job : jobs) {
			this.view.addJobView(job.getView(), job.getTitle());
			if(job.getDescriptor().jobStarted)
				new Scheduler(job, tess);
			System.out.println(job.getDescriptor().summary() + "\n");
		}
		
		if(settings.isNewFile())
			view.messageLabel.setText("Created new settings file.");
		else
			view.messageLabel.setText("Loaded existing settings file.");
		
		view.present();
	}
	
	public void viewResized() {
		view.present();
	}
	
	public void jobRenamed(Job job) {
		this.view.setJobViewTitle(job.getView(), job.getTitle());
	}
	
	public void jobStarted(Job job) {
		new Scheduler(job, tess);
	}
	
	public void saveSettings(Job job) {
		if(!settings.storeJob(job)) {
			JOptionPane.showMessageDialog(view, "Unable to write settings file, changes unsaved.", "OCR Logger", JOptionPane.ERROR_MESSAGE);
		}
		else {
			System.out.println("Saved settings, Job index " + jobs.indexOf(job));
		}
	}

	private void actionAdd() {
		Job job = new Job(this, Descriptor.Default());
		this.jobs.add(job);
		this.view.addJobView(job.getView(), job.getTitle());
		saveSettings(job);
	}
	
	private void setActionListeners() {
		view.addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionAdd (); }});
	}
	
	public static void main(String[] args) {
		new OCRLogger();
	}
}
