// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Settings
{
	private boolean sane;
	private boolean newFile;
	private Properties table;
	private OCRLogger app;
	
	public Settings(OCRLogger ol) {
		this.app = ol;
		
		try {
			loadSettings();
			this.sane = true;
		}
		catch(IOException ex) {
			this.sane = false;
		}
	}
	
	public boolean isNewFile() {
		return newFile;
	}

	public boolean storeJob(Job job) {
		if(!sane)
			return false;
		
		// Update table with new descriptor and save settings...
		
		return true;
	}
	
	public ArrayList<Job> getJobs() {
		if(!sane)
			return null;
		
		// Load all descriptors from settings table...
		
		ArrayList<Job> jobs = new ArrayList<Job>();
		jobs.add(new Job(app));
		return jobs;
	}

	private void loadSettings() throws IOException {
		if(!(new File("settings.txt").exists())) {
			this.newFile = true;
			saveDefaultSettings();
		}
		
		this.table = new Properties();
		FileInputStream in = new FileInputStream("settings.txt");
		table.load(in);
		in.close();
	}
	
	private void saveSettings(Properties settings) throws IOException {
		FileOutputStream out = new FileOutputStream("settings.txt");
		settings.store(out, "---No Comment---");
		out.close();
	}
		
	private void saveDefaultSettings() throws IOException {
		Properties defaults = new Properties();
		saveSettings(defaults);
	}
}
