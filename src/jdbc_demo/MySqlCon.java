package jdbc_demo;

import java.sql.*;

class MySqlCon {
	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/myschema", "root", "password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from course");
			while (rs.next())
				System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getInt(3));
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}