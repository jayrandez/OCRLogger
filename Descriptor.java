import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

public class Descriptor implements Serializable
{
	final static int NumberField = 0;
	final static int StringField = 1;
	final static int ImageField = 2;
	
	public String title;
	public String url;
	public boolean urlValid;
	public ArrayList<Rectangle> zones;
	public ArrayList<String> zoneNames;
	public ArrayList<Integer> zoneTypes;
	
	public static Descriptor Default()
	{
		Descriptor desc = new Descriptor();
		
		desc.title = "New Job";
		
		desc.url = "Image URL";
		desc.urlValid = false;
		
		desc.zones = new ArrayList<Rectangle>();
		desc.zones.add(null);
		desc.zoneNames = new ArrayList<String>();
		desc.zoneNames.add("Untitled");
		desc.zoneTypes = new ArrayList<Integer>();
		desc.zoneTypes.add(new Integer(0));
		
		
		
		return desc;
	}
	
	public String summary() {
		String str = "--- JOB : " + title + " ---\n";
		str += "Image URL: " + url + "\n";
		str += "URL Valid? " + urlValid + "\n";
		str += "OCR Zone List: \n";
		for(int i = 0; i < zones.size(); i++)
			str += zoneNames.get(i) + " " + zoneTypes.get(i) + " " + zones.get(i) + "\n";
		
		return str;
	}
}
