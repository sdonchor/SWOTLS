package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DatabaseTemplate {
	private static String filename = "./sql/template.sql";
	private static String[] sql=null;
	/**
	 * Returns the template script of system's database.
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String[] GetScript() {
		if(sql==null)
		{
			try {
				String script = new Scanner(new File(filename)).useDelimiter("\\Z").next();
				sql = script.split(";");
			} catch (FileNotFoundException e) {
				System.out.println("Couldn't find SQL template file.");
			}	
		}
		return sql;
	}
}
