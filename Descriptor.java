// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.*;
import java.io.*;
import java.util.*;

public class Descriptor implements Serializable
{
	final public static int NumberField = 0;
	final public static int StringField = 1;
	final public static int ImageField = 2;
	
	final public static int scheduleOnce = 0;
	final public static int scheduleRepeat = 1;
	final public static int scheduleByHour = 2;
	final public static int scheduleByMinute = 3;
	
	public String title;
	
	public String url;
	public boolean urlValid;
	
	public String filename;
	public boolean filenameValid;
	
	public ArrayList<Rectangle> zones;
	public ArrayList<String> zoneNames;
	public ArrayList<Integer> zoneTypes;
	
	public int scheduleType;
	public int scheduleDay;
	public int scheduleHour;
	public int scheduleMinute;
	
	public boolean jobStarted;
	
	public static Descriptor Default()
	{
		Descriptor desc = new Descriptor();
		
		desc.title = "New Job";
		
		desc.url = "Image URL";
		desc.urlValid = false;
		
		desc.filename = "Output File";
		desc.filenameValid = false;
		
		desc.zones = new ArrayList<Rectangle>();
		desc.zones.add(null);
		desc.zoneNames = new ArrayList<String>();
		desc.zoneNames.add("Untitled");
		desc.zoneTypes = new ArrayList<Integer>();
		desc.zoneTypes.add(new Integer(0));
		
		desc.scheduleType = scheduleOnce;
		desc.scheduleDay = 0;
		desc.scheduleHour = 0;
		desc.scheduleMinute = 30;
		
		desc.jobStarted = false;
		
		return desc;
	}
	
	public String summary() {
		String str = "--- JOB : " + title + " ---\n";
		str += "Image URL: " + url + "\n";
		str += "URL Valid? " + urlValid + "\n";
		str += "Filename: " + filename + "\n";
		str += "Filename Valid? " + filenameValid + "\n";
		str += "OCR Zone List: \n";
		for(int i = 0; i < zones.size(); i++)
			str += zoneNames.get(i) + " " + zoneTypes.get(i) + " " + zones.get(i) + "\n";
		str += "Schedule: " + scheduleType + " - " + scheduleDay + "days, " + scheduleHour + "hrs, " + scheduleMinute + "min.\n";
		str += "Job Started? " + jobStarted + "\n";
		return str;
	}
}
