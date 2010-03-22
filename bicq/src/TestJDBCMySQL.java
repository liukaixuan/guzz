import java.sql.*;

import com.mysql.jdbc.Driver ;

public class TestJDBCMySQL{
	 public static void main(String[] args) throws Exception 
	 { 
	 	Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bicq?user=root&password=root");
	    String sql = "select * from user ";
	    Statement stmt = con.createStatement();
	    stmt.execute(sql); ResultSet rs = stmt.getResultSet();
		for(int i = 1;i <= rs.getMetaData().getColumnCount();i++){ 
	      System.out.print(rs.getMetaData().getColumnName(i) + "\t") ; 
		} 
	    System.out.println( System.getProperty("line.separator")+"********************"); 
	    while (rs.next()) { 
	      for(int i = 1;i <= rs.getMetaData().getColumnCount();i++) { 
	      System.out.print(rs.getString(i) + "\t"); } System.out.println(); 
	    }
	    
	 }
}

