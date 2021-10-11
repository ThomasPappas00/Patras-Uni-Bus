import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

public class InitTransportation {

	public static void main(String[] args) throws SQLException, IOException {
		
		
		String connectionUrl =                 
				"jdbc:sqlserver://localhost:1433;"
                + "databaseName=CampusBusesDB;"
                + "integratedSecurity=true;";
		
		Connection connection = DriverManager.getConnection(connectionUrl); //make connection with local Microsoft SQL Database
		int buses1 = 5;
		int buses2 = 10;
		System.out.println("Campus connected to the SQL Database");	
		System.out.println("A campus with " + buses1 + " buses for line 1 and " + buses2 + " buses for line 2 is created");
		
		
		createDBTables(connection);
		makeBusStops(connection);
		makeBusesLine1(connection, buses1);
		makeBusesLine2(connection, buses2, buses1);
	}
	
	private static String getLine(String filename,int line) throws IOException {
		  String data = Files.readAllLines(Paths.get(filename)).get(line);
		  return data;
	}
	
	public static void createDBTables(Connection connection) throws SQLException {
		Statement st = connection.createStatement();
		String sql = "DROP TABLE IF EXISTS BUS";
		st.execute(sql);
		
		String sql1 =  "CREATE TABLE BUS "
				+ "(id INTEGER, "
				+ "location VARCHAR(200), "
				+ "passengers INTEGER, "
				+ "line INTEGER, "
				+ "makeStop BIT, "
				+ "highTemp INTEGER,"
				+ "stall INTEGER, "
				+ "nextStop NVARCHAR(30)"				
				+ "PRIMARY KEY(id))";
		st.execute(sql1);
		
		String sql2 = "DROP TABLE IF EXISTS BUSSTOP";
		st.execute(sql2);
		
		String sql3 = "CREATE TABLE BUSSTOP "
				+ "(id INTEGER,"
				+ "name NVARCHAR(30), "
				+ "location VARCHAR(200), "
				+ "pplWaiting INTEGER, "
				+ "line INTEGER, "
				+ "PRIMARY KEY(id))";
		st.execute(sql3);
	}
	
	
	
	public static void makeBusStops(Connection connection) throws SQLException, IOException {
		for(int i=0;i<7;i++) {
			String sql = "INSERT INTO BUSSTOP (id,name,location,line)"
					+ "VALUES (?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, i+1);
			ps.setNString(2, getLine("line1stopsnames.txt", i));
			ps.setString(3, getLine("line1stops.txt", i));
			ps.setInt(4, 1);
			ps.executeUpdate();
		}
		
		for(int i=0;i<7;i++) {
			String sql = "INSERT INTO BUSSTOP (id,name,location,pplWaiting,line)"
					+ "VALUES (?,?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, i+8);
			ps.setNString(2, getLine("line2stopsnames.txt", i));
			ps.setString(3, getLine("line2stops.txt", i));
			ps.setInt(4, ThreadLocalRandom.current().nextInt(5, 10 + 1));
			ps.setInt(5, 2);
			ps.executeUpdate();
		}
	}
	
	public static void makeBusesLine1(Connection connection, int quantity) throws SQLException, IOException {
		for(int i=0; i<quantity ; i++) {
			String sql = "INSERT INTO BUS (id,line,stall,nextStop)"
					+ "VALUES (?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, i+1);
			ps.setInt(2, 1);
			ps.setInt(3, 5);
			ps.setNString(4, getLine("line1stopsnames.txt",0));
			ps.executeUpdate();
		}
	}
	
	public static void makeBusesLine2(Connection connection, int quantity, int buses1) throws SQLException, IOException {
		for(int i=0; i<quantity ; i++) {
			String sql = "INSERT INTO BUS (id,line,stall,nextStop)"
					+ "VALUES (?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, i+buses1+1);
			ps.setInt(2, 2);
			ps.setInt(3, 5);
			ps.setNString(4, getLine("line2stopsnames.txt",0));
			ps.executeUpdate();
		}
	}
	
	
}
