// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Settings
{
	private OCRLogger app;
	private boolean sane;
	private boolean newFile;
	
	private ArrayList<Descriptor> descriptors;
	
	public Settings(OCRLogger ol) {
		this.app = ol;
		
		try {
			loadSettings();
			this.sane = true;
		}
		catch(IOException | ClassNotFoundException ex) {
			this.sane = false;
			ex.printStackTrace();
		}
	}
	
	public boolean isNewFile() {
		return newFile;
	}

	public boolean storeJob(Job job) {
		if(!sane)
			return false;
		
		try {
			int index = descriptors.indexOf(job.getDescriptor());
			if(index == -1)
				descriptors.add(job.getDescriptor());
			else
				descriptors.set(index, job.getDescriptor());
			saveSettings(descriptors);
			return true;
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public ArrayList<Job> getJobs() {
		if(!sane)
			return null;

		ArrayList<Job> jobs = new ArrayList<Job>();
		for(Descriptor desc : descriptors)
			jobs.add(new Job(app, desc));
		
		return jobs;
	}

	private void loadSettings() throws IOException, ClassNotFoundException {
		if(!(new File("settings.txt").exists())) {
			this.newFile = true;
			saveDefaultSettings();
		}

		FileInputStream fin = new FileInputStream("settings.txt");
		ObjectInputStream ois = new ObjectInputStream(fin);
		this.descriptors = (ArrayList<Descriptor>)ois.readObject();
		ois.close();
		fin.close();
	}
	
	private void saveSettings(ArrayList<Descriptor> descriptors) throws IOException {
		FileOutputStream fout = new FileOutputStream("settings.txt");
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(descriptors);
		oos.close();
		fout.close();
	}
		
	private void saveDefaultSettings() throws IOException {
		ArrayList<Descriptor> def = new ArrayList<Descriptor>(); 
		def.add(Descriptor.Default());
		saveSettings(def);
	}
}
