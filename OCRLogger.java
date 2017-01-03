// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class OCRLogger
{
	// This is the "View-controller" for the app, and ViewFrame is the "View"
	
	private ViewFrame view;
	private Settings settings;
	private ArrayList<Job> jobs;
	
	public OCRLogger() {
		this.view = new ViewFrame();
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setActionListeners();
		view.present();
		
		this.settings = new Settings(this);
		
		this.jobs = settings.getJobs();
		if(jobs == null) {
			JOptionPane.showMessageDialog(view, "Couldn't open/create settings file. Local dir permissions?", "OCR Logger", JOptionPane.ERROR_MESSAGE);
			return;
		}
		for(Job job : jobs) {
			this.view.addJobView(job.getView(), job.getTitle());
		}
		
		if(settings.isNewFile())
			view.messageLabel.setText("Created new settings file.");
		else
			view.messageLabel.setText("Loaded existing settings file.");
	}
	
	public void viewResized() {
		view.present();
	}
	
	private void actionAdd() {
		Job job = new Job(this);
		this.view.addJobView(job.getView(), job.getTitle());
	}
	
	private void setActionListeners() {
		view.addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { actionAdd (); }});
	}
	
	public static void main(String[] args) {
		new OCRLogger();
	}
}
