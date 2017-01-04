// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.Image;
import java.io.*;
import java.util.*;

public class Scheduler
{
	private Job job;
	private Descriptor desc;
	private TesseractManager tess;
	private Timer timer;
	private TimerTask task;
	
	public Scheduler(Job j, TesseractManager t) {
		this.job = j;
		this.desc = job.getDescriptor();
		this.tess = t;
		this.timer = new Timer();
		
		try {
			File outFile = new File(desc.filename);
			outFile.createNewFile(); // Ensure file exists
			if(tail(new File(desc.filename)).equals(""))
				writeFileHeader(job);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			job.updateStatus("File Write Error. Ensure output file is writeable and restart.");
			return;
		}

		this.task = new TimerTask() {
		public void run() {
			long executionTime = this.scheduledExecutionTime();
			Date thisRun = new Date(executionTime);
			System.out.println("OCR task is running now - " + thisRun);
			log(thisRun);
		}};
		
		scheduleNextRun();
	}
	
	private void scheduleNextRun() {
		if(desc.scheduleType == Descriptor.scheduleOnce) {
			if(getLastRun() != null)
				return;
			
			Date nextRun = new Date(); // Now
			
			System.out.println("Scheduling task for " + nextRun);
			timer.schedule(task, nextRun);
		}
		else if(desc.scheduleType == Descriptor.scheduleRepeat) {
			Date lastRun = getLastRun();
			
			long hours = (desc.scheduleDay * 24) + desc.scheduleHour;
			long minutes = (hours * 60) + desc.scheduleMinute;
			long seconds = minutes * 60;
			long millis = seconds * 1000;
			
			Date nextRun;
			if(lastRun == null)
				nextRun = new Date(); // Now
			else
				nextRun = new Date(lastRun.getTime() + millis); // Last execution + period

			System.out.println("Scheduling task with period " + millis + " for " + nextRun);
			timer.schedule(task, nextRun, millis);
		}
		else if(desc.scheduleType == Descriptor.scheduleByHour) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.add(Calendar.HOUR_OF_DAY, 1);
			Date nextRun = cal.getTime();
			
			long millis = 60 * 60 * 1000; // 60 Min/Hr * 60 Sec/Min * 1000 Millis/Sec
			
			System.out.println("Scheduling task with period " + millis + " for " + nextRun);
			timer.schedule(task, nextRun, millis);
		}
		else if(desc.scheduleType == Descriptor.scheduleByMinute) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.MINUTE, 1);
			Date nextRun = cal.getTime();
			
			long millis = 60 * 1000; // 60 Sec/Min * 1000 Millis/Sec
			
			System.out.println("Scheduling task with period " + millis + " for " + nextRun);
			timer.schedule(task, nextRun, millis);
		}
	}
	
	private void log(Date thisRun) {
		try {
			job.updateStatus("Last run was " + thisRun);
			Object[] results = tess.runOCR(desc);
			if(results.length == 0) {
				job.updateStatus("Fatal job error. No result recorded to log.");
				return;
			}
			
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(desc.filename, true)));
			writer.write("" + thisRun + ",");

			for(int i = 0; i < results.length; i++) {
				Object result = results[i];
				
				if(result instanceof String) {
					writer.write((String)result);
				}
				else if(result instanceof Number) {
					writer.write("" + (Number)result);
				}
				else if(result instanceof Image) {
					writer.write("Image");
				}
				else {
					// OCR Output was invalid, if unexpected then complain
					writer.write("Unknown");
					if(desc.zones.get(i) != null) {
						job.updateStatus("OCR Error. Log results invalid.");
					}
				}
				
				if(i < results.length - 1)
					writer.write(",");
			}
			
			writer.write("\n");
			writer.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
			job.updateStatus("File Write Error. Ensure output file is writeable.");
		}
	}
	
	public void writeFileHeader(Job job) throws IOException {
		Descriptor desc = job.getDescriptor();

		PrintWriter writer = new PrintWriter(new FileWriter(desc.filename));
		writer.write("Date,");
		int numFields = desc.zoneNames.size();
		for(int i = 0; i < numFields; i++) {
			writer.write(desc.zoneNames.get(i));
			if(i < numFields - 1)
				writer.write(",");
		}
		writer.write("\n");
		writer.close();
	}
	
	@SuppressWarnings("deprecation")
	private Date getLastRun() {
		File log = new File(desc.filename);
		String lastLine = tail(log);

		if(lastLine.substring(0, 5).equals("Date,"))
			return null;

		String[] parts = lastLine.split(",");
		return new Date(parts[0]);
	}
	
	// CREDIT Eric Leschinski, http://stackoverflow.com/questions/686231/quickly-read-the-last-line-of-a-text-file
	private static String tail(File file) {
	    RandomAccessFile fileHandler = null;
	    try {
	        fileHandler = new RandomAccessFile( file, "r" );
	        long fileLength = fileHandler.length() - 1;
	        StringBuilder sb = new StringBuilder();

	        for(long filePointer = fileLength; filePointer != -1; filePointer--){
	            fileHandler.seek( filePointer );
	            int readByte = fileHandler.readByte();

	            if( readByte == 0xA ) {
	                if( filePointer == fileLength ) {
	                    continue;
	                }
	                break;

	            } else if( readByte == 0xD ) {
	                if( filePointer == fileLength - 1 ) {
	                    continue;
	                }
	                break;
	            }

	            sb.append( ( char ) readByte );
	        }

	        String lastLine = sb.reverse().toString();
	        return lastLine;
	    } catch( java.io.FileNotFoundException e ) {
	        e.printStackTrace();
	        return null;
	    } catch( java.io.IOException e ) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        if (fileHandler != null )
	            try {
	                fileHandler.close();
	            } catch (IOException e) {
	                /* ignore */
	            }
	    }
	}
}
