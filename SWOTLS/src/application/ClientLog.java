package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientLog {
	public static final String filenameTemplate = "/logs/clientLog_";
	public static String getDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		return dtf.format(localDate); 
	}
	public static String getTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	public static void logLine(String type, String msg) 
	{ 
		String filename=filenameTemplate+getDate()+".log";
		String line = "[$time][$type]: $msg";
		line = line.replace("$time", getTime());
		line = line.replace("$type", type);
		line = line.replace("$msg", msg);
		try {  
			File f = new File("./logs/");
			f.mkdir();
			BufferedWriter out = new BufferedWriter(new FileWriter("./"+filename,true)); 
			out.write(line); 
			out.newLine();
			out.close(); 
		} 
			catch (IOException e) { 
			System.out.println("Couldn't create log file"); 
		} 
	}
}
