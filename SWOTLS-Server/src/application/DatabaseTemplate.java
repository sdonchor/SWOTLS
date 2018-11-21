package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseTemplate {
	private static String sql;
	public DatabaseTemplate() {
		
	}
	public static String GetScript() {
		if(sql==null)
		{
			try {
				sql = new String(Files.readAllBytes(Paths.get("sql/template.sql")), StandardCharsets.UTF_8);
				System.out.println(sql);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Error";
			}
		}
		return sql;
	}
}
