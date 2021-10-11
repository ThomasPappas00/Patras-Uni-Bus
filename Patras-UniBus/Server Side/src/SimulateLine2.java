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

public class SimulateLine2 {

	public static void main(String[] args) throws SQLException, IOException, InterruptedException {
		
		String connectionUrl =                 
				"jdbc:sqlserver://localhost:1433;"
                + "databaseName=CampusBusesDB;"
                + "integratedSecurity=true;";
		Connection connection = DriverManager.getConnection(connectionUrl); //make connection with local Microsoft SQL Database
		System.out.println("Line 2 is running");
		int counterArrive = 60;
		int counterRushHour = 120;
		int counterSend = 10;
		boolean rush_hour = false;
		
		String sql = "SELECT location FROM BUSSTOP WHERE line = 2";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<String> stopLocations = new ArrayList<String>();   //busstops' locations
		while(rs.next()) {
			stopLocations.add(rs.getString("location"));
		}
		
	 
		while(true) {
 			if(counterRushHour == 120)
 				rush_hour = !rush_hour;
			if(counterArrive == 60)         
				pplArriveAtBusStops(connection, rush_hour);   //every sleep*counter seconds
			if(counterSend == 10) {
				boolean send_bus = needBus(connection);
				if(send_bus)
					insertBus(connection);
			}
			
			
			updateLocations(connection, stopLocations);
			TimeUnit.SECONDS.sleep(2);	
			
			
			
			counterArrive--;
			counterRushHour--;
			counterSend--;
			if(counterArrive == 0)
				counterArrive = 60;
			if(counterRushHour == 0)
				counterRushHour = 120;
			if(counterSend == 0)
				counterSend = 10;
		} 

	}
	
	private static void pplArriveAtBusStops(Connection connection, boolean rush_hour) throws SQLException {
		int pplComing;
		if(!rush_hour)
			pplComing = ThreadLocalRandom.current().nextInt(3, 15 + 1);
		else 
			pplComing = ThreadLocalRandom.current().nextInt(20, 35 + 1);
		
		String sql = "UPDATE BUSSTOP SET pplWaiting = pplWaiting + ? WHERE line = 2";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setInt(1, pplComing);
		ps.executeUpdate();
	}
	
	private static String getLine(String filename,int line) throws IOException {
		  String data = Files.readAllLines(Paths.get(filename)).get(line);
		  return data;
	}
	
	
	private static boolean needBus(Connection connection) throws SQLException {
		String sql = "SELECT SUM(pplWaiting) FROM BUSSTOP WHERE line = 2";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		int sumWaiting = 0;
		if(rs.next()) 
			sumWaiting = rs.getInt(1);
		
		String sql1 = "SELECT COUNT(id), SUM(passengers) FROM BUS WHERE line = 2 AND location IS NOT NULL";
		Statement stmt1 = connection.createStatement();
		ResultSet rs1 = stmt1.executeQuery(sql1);
		int buses = 0;
		int sumPassengers = 0;
		while(rs1.next()) {
			buses = rs1.getInt(1);
			sumPassengers = rs1.getInt(2);
		}
		int empty_seats = buses*100 - sumPassengers;
		
	//	System.out.println("sumWaiting = " + sumWaiting);
	//	System.out.println("buses = " + buses);
	//	System.out.println("empty_seats = " + empty_seats);
		if(sumWaiting > empty_seats)
			return true;
		else
			return false;
	}
	
	
	private static void insertBus(Connection connection) throws SQLException, IOException {
		String sql = "UPDATE BUS SET location = ?, passengers = ?, nextStop = ? WHERE ID = (SELECT TOP 1 id FROM BUS WHERE line = 2 AND location IS NULL)";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, getLine("line2.txt",0));
		ps.setInt(2, 0);
		ps.setNString(3, getLine("line2stopsnames.txt",0));
		ps.executeUpdate(); 
	}
	
	private static String nextLocation(String currentLoc) throws IOException {
		if(currentLoc.equals("38.29012798436425, 21.783770442144068")) {   //last location
			return null;
		}
		else {
			File file = new File("line2.txt");
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
		  String data = Files.readAllLines(Paths.get("line2.txt")).get(lineNum+1);
		  return data;	
		}
	}
	
	private static String updateNextStop(String loc, ArrayList<String> stopLocations) throws IOException {
		int index_old_next_stop = stopLocations.indexOf(loc);
		String next_stop = getLine("line2stopsnames.txt", index_old_next_stop + 1);
		return next_stop;
	}
	
	private static void checkMakeStop(Bus bus) {
		if((bus.passengers) >= 100) 
			bus.setMakeStop(false);
		else
			bus.setMakeStop(true);
	}
	
	private static int getPplWaiting(Connection connection,String loc) throws SQLException {
		String sql = "SELECT pplWaiting FROM BUSSTOP WHERE line = 2 AND location = ?";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setNString(1, loc);
		ResultSet rs = ps.executeQuery();
		int pplWaiting = 0;
		if(rs.next())
			pplWaiting = rs.getInt(1);
		return pplWaiting;
	}
	
	private static void leavingBusStop(Connection connection, String loc, int crowd) throws SQLException {
		String sql = "UPDATE BUSSTOP SET pplWaiting = pplWaiting - ? WHERE line = 2 AND location = ?";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setInt(1, crowd);
		ps.setNString(2, loc);
		ps.executeUpdate();
	}
	
	
	private static void updateLocations(Connection connection, ArrayList<String> stopLocations) throws SQLException, IOException {			

		String sql = "SELECT location,passengers,makeStop,highTemp,stall,nextStop FROM BUS WHERE line = 2 AND location IS NOT NULL";	
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			Bus bus = new Bus();
			String old_loc = rs.getString("location");
			int old_passengers = rs.getInt("passengers");
			boolean old_make_stop = rs.getBoolean("makeStop");
			int old_highTemp = rs.getInt("highTemp");
			int old_stall = rs.getInt("stall");
			String old_next_stop = rs.getNString("nextStop");
			
			bus.setPassengers(old_passengers);
			bus.setMakeStop(old_make_stop);
			bus.setHighTemp(old_highTemp);
			bus.setStall(old_stall);
			bus.setNextStop(old_next_stop);
			
			String new_loc = null;
			String new_next_stop = null;
			
			if(stopLocations.contains(old_loc) && old_stall > 0 && old_make_stop) {
				int pplWaiting = getPplWaiting(connection, old_loc);
				
				int sick = 0;
				if(old_stall==5) {
					sick = ThreadLocalRandom.current().nextInt(0, (pplWaiting/4) + 1);
					bus.setHighTemp(sick);
				}

				
				int pplEntered = bus.pplEntering(pplWaiting - sick);
				leavingBusStop(connection, old_loc, pplEntered + sick);		
				if(bus.getPassengers()<100)
					new_next_stop = updateNextStop(old_loc, stopLocations);
				else
					new_next_stop = "-Εκτός Πανεπιστημίου-";
				new_loc = old_loc;
			}
			else {
				new_loc = nextLocation(old_loc);
				checkMakeStop(bus);
				new_next_stop = old_next_stop;
				bus.setHighTemp(0);
				bus.setStall(5);
			}
			
			String sql2 = "UPDATE BUS SET location = ?, passengers = ?, makeStop = ?, highTemp = ?, stall = ?, nextStop = ? WHERE location = ? AND line = 2";
			PreparedStatement ps2 = connection.prepareStatement(sql2);
			ps2.setString(1, new_loc);
			ps2.setInt(2, bus.getPassengers());
			ps2.setBoolean(3, bus.getMakeStop());
			ps2.setInt(4, bus.getHighTemp());
			ps2.setInt(5, bus.getStall());
			ps2.setNString(6, new_next_stop);
			ps2.setString(7, old_loc);
			ps2.executeUpdate();
		}
	}

}
