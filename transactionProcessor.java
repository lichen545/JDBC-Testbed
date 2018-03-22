import java.sql.*;
import java.io.*;
import java.util.Arrays;


class transactionProcessor {

	public static void delete(Statement stmt, String[] args) throws SQLException {
		// ResultSet rs = stmt.executeQuery("select * from parts");
		System.out.println("deleting...");
	}

	public static void insert(Statement stmt, String[] args) throws SQLException {
		// ResultSet rs = stmt.executeQuery("select * from parts");
		System.out.println("inserting...");
	}

	public static void average(Statement stmt) throws SQLException {
		// ResultSet rs = stmt.executeQuery("select avg(price) from parts");
		System.out.println("averaging all prices...");
	}
	
	public static void all_subparts(Statement stmt, String[] args) throws SQLException {
		// ResultSet rs = stmt.executeQuery("select * from parts");
		System.out.println("finding all subparts of given object...");	
	}
	
	public static void subpart_average(Statement stmt, String[] args) throws SQLException {
		// ResultSet rs = stmt.executeQuery("select * from parts");
		System.out.println("averaging prices of all subparts...");
			
	}
	
	public static void is_multiple_subpart(Statement stmt, String[] args) throws SQLException {
		// ResultSet rs = stmt.executeQuery("select * from parts");
		System.out.println("finding all parts that are subparts of multiple parts...");
	}

	public static void main(String args[]) throws ClassNotFoundException {

		Class.forName("org.sqlite.JDBC");

		try {

			Connection con = DriverManager.getConnection("jdbc:sqlite:transactions.db");
			Statement stmt = con.createStatement();

			//clear tables if previously created
			stmt.executeUpdate("drop table if exists parts");
			stmt.executeUpdate("drop table if exists subpart_of");

			//create table
			stmt.executeUpdate("create table parts (pid integer, name string, price integer, PRIMARY KEY(pid))");
			stmt.executeUpdate("create table subpart_of (pid integer, mid integer)");

			//test values
			stmt.executeUpdate("insert into parts values(1, 'nut', 10)");
			stmt.executeUpdate("insert into parts values(2, 'tire', 200)");
			stmt.executeUpdate("insert into parts values(3, 'rim', 100)");
			stmt.executeUpdate("insert into parts values(4, 'wheel', 340)");
			// ResultSet rs = stmt.executeQuery("select * from parts");
			// while (rs.next())
			// 	System.out.println(rs.getInt(1) + "  " + rs.getString(2));
			con.close();
			
			//read file input
			try (BufferedReader br = new BufferedReader(new FileReader("transfile.txt"))) {
				String line = br.readLine();

				while (line != null) {
					
					String[] arguments = line.split(" ");
					System.out.println(Arrays.toString(arguments));
					if (arguments[0]=="1") {
						delete(stmt, arguments);
					} else if (arguments[0]=="2") {
						insert(stmt, arguments);
					} else if (arguments[0]=="3") {
						average(stmt);
					} else if (arguments[0]=="4") {
						all_subparts(stmt, arguments);
					} else if (arguments[0]=="5") {
						subpart_average(stmt, arguments);
					} else {
						is_multiple_subpart(stmt, arguments);
					}
					line = br.readLine();
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}