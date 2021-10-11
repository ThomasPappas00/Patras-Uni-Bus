import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SimulateLine1 {

	public static void main(String[] args) throws SQLException, IOException, InterruptedException {
			
		String connectionUrl =                 
				"jdbc:sqlserver://localhost:1433;"
                + "databaseName=CampusBusesDB;"
                + "integratedSecurity=true;";
		Connection connection = DriverManager.getConnection(connectionUrl); //make connection with local Microsoft SQL Database
		System.out.println("Line 1 is running");
		int counter = 40;
		
		String sql1 = "SELECT location FROM BUSSTOP WHERE line = 1";
		Statement stmt1 = connection.createStatement();
		ResultSet rs1 = stmt1.executeQuery(sql1);
		ArrayList<String> stopLocations = new ArrayList<String>();
		while(rs1.next()) {
			stopLocations.add(rs1.getString("location"));
		}
		
		
		while(true) {
			if(counter == 40)         
				insertBus(connection);   //every sleep*counter seconds
 			
			
			
			updateLocations(connection, stopLocations);
			TimeUnit.SECONDS.sleep(2);	
			
			
			
			counter--;
			if(counter == 0)
				counter = 40;
		}  
		

	}
	
	private static String getLine(String filename,int line) throws IOException {
		  String data = Files.readAllLines(Paths.get(filename)).get(line);
		  return data;
	}
	
	private static void insertBus(Connection connection) throws SQLException, IOException {
		String sql = "UPDATE BUS SET location = ?, passengers = ?, nextStop = ? WHERE ID = (SELECT TOP 1 id FROM BUS WHERE line = 1 AND location IS NULL)";
		PreparedStatement ps = connection.prepareStatement(sql);
		String initLoc1 = getLine("line1.txt",0);
		ps.setString(1, initLoc1);
		ps.setInt(2, ThreadLocalRandom.current().nextInt(50, 80 + 1));
		ps.setNString(3, getLine("line1stopsnames.txt",0));
		ps.executeUpdate(); 
	}
	
	private static String nextLocation(String currentLoc) throws IOException {
		if(currentLoc.equals("38.296534240407006, 21.795201375525227")) {   //last location
			return null;
		}
		else {
			File file = new File("line1.txt");
		    int lineNum = 0;
			try {
				Scanner scanner = new Scanner(file);
			    while (scanner.hasNextLine()) {
			        String line = scanner.nextLine();
			        if(currentLoc.equals(line)) { 
			        	scanner.close();
			            break;
			        }
			        lineNum++;    
			    }
			} catch(FileNotFoundException e) { 
			   System.out.println(e);
			}
		  String data = Files.readAllLines(Paths.get("line1.txt")).get(lineNum+1);
		  return data;	
		}
	}
	
	private static String updateNextStop(String loc, ArrayList<String> stopLocations) throws IOException {
		int index_old_next_stop = stopLocations.indexOf(loc);
		String next_stop = getLine("line1stopsnames.txt", index_old_next_stop + 1);
		return next_stop;
	}
	
	
	private static void updateLocations(Connection connection, ArrayList<String> stopLocations) throws SQLException, IOException {			
			
		String sql = "SELECT location,passengers,stall,nextStop FROM BUS WHERE line = 1 AND location IS NOT NULL";	
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			Bus bus = new Bus();
			String old_loc = rs.getString("location");
			int old_passengers = rs.getInt("passengers");
			int old_stall = rs.getInt("stall");
			String old_next_stop = rs.getNString("nextStop");
			bus.setPassengers(old_passengers);
			bus.setStall(old_stall);
			String new_loc = null;
			String new_next_stop = null;
			
			if(stopLocations.contains(old_loc) && old_stall > 0) {
				bus.passengersLeaving(2);
				new_loc = old_loc;
				new_next_stop = updateNextStop(old_loc, stopLocations);
			}
			else {
				new_loc = nextLocation(old_loc);	
				new_next_stop = old_next_stop;
				bus.setStall(5);
			}
			
			String sql2 = "UPDATE BUS SET location = ?, passengers = ?, stall = ?, nextStop = ? WHERE location = ? AND line = 1";
			PreparedStatement ps2 = connection.prepareStatement(sql2);
			ps2.setString(1, new_loc);
			ps2.setInt(2, bus.getPassengers());
			ps2.setInt(3, bus.getStall());
			ps2.setNString(4, new_next_stop);
			ps2.setString(5, old_loc);
			ps2.executeUpdate();
		}
	}
	
	
}
