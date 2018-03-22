
//package jdbc_demo;

import java.sql.*;

class transactionProcessor {
	public static void main(String args[]) throws ClassNotFoundException {

		Class.forName("org.sqlite.JDBC");

		try {

			Connection con = DriverManager.getConnection("jdbc:sqlite:transactions.db");
			Statement stmt = con.createStatement();
			stmt.executeUpdate("create table person (id integer, name string)");
			stmt.executeUpdate("insert into person values(1, 'leo')");
			stmt.executeUpdate("insert into person values(2, 'yui')");
			ResultSet rs = stmt.executeQuery("select * from person");
			while (rs.next())
				System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getInt(3));
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}