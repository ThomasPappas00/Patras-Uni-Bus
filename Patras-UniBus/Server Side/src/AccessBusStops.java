import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "busstops", urlPatterns = {"/busstops/*"})
public class AccessBusStops extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AccessBusStops() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String line = request.getParameter("line");
		String jsonOut = null;
		
		String connectionUrl =                 
				"jdbc:sqlserver://localhost:1433;"
                + "databaseName=CampusBusesDB;"
                + "integratedSecurity=true;";
		String sql = "DECLARE @ReturnJSON nvarchar(max) " + 
				"SET @ReturnJSON = (SELECT * FROM BUSSTOP WHERE LINE = ? FOR JSON AUTO); " + 
				"SELECT @ReturnJSON AS Result";
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection connection = DriverManager.getConnection(connectionUrl);
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, line);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				jsonOut = rs.getNString("Result");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods","GET, POST, DELETE");
		PrintWriter out = response.getWriter();
		out.println(jsonOut);

		
	}




}
