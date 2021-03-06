import java.sql.*;
import java.io.*;
import java.util.*;

class transactionProcessor {

	public static void delete(Statement stmt, String[] args) throws SQLException {
		System.out.println(">>>deleting pid " + args[1] + "\n");
		stmt.executeUpdate("DELETE FROM parts WHERE pid = " + args[1]);
		stmt.executeUpdate("DELETE FROM subpart_of WHERE pid = " + args[1] + " OR mid = " + args[1]);
	}

	public static void insert(Statement stmt, String[] args) throws SQLException {
		System.out.println(">>>inserting into parts (" + args[1] + ", '" + args[2] + "', " + args[3] + ")");
		stmt.executeUpdate("INSERT INTO parts VALUES(" + args[1] + ", '" + args[2] + "', " + args[3] + ")");

		//check for subparts
		if (args.length > 4) {
			for (int i = 4; i < args.length; i++) {
				ResultSet rs = stmt.executeQuery("SELECT * FROM parts WHERE pid = " + args[i]);
				if (rs.next()) {
					System.out.println(">>>inserting into subpart_of (" + args[1] + ", " + args[i] + ")");
					stmt.executeUpdate("INSERT INTO subpart_of VALUES(" + args[1] + ", " + args[i] + ")");
				} else {
					System.out.println(">>>inserting into subpart_of (" + args[1] + ", " + args[i]
							+ ")... ERROR: mid does not exist in parts");
				}
			}
		}
	}

	public static void average(Statement stmt) throws SQLException {
		System.out.println("averaging all prices:");
		ResultSet rs = stmt.executeQuery("SELECT avg(price) FROM parts");
		System.out.println(">>>" + rs.getInt(1));
	}

	public static void all_subparts(Statement stmt, String[] args) throws SQLException {
		System.out.println("finding all subparts of pid = " + args[1]);
		ResultSet rs = stmt.executeQuery("SELECT * FROM subpart_of WHERE mid = " + args[1]);
		List<Integer> subparts = new ArrayList<Integer>();
		while (rs.next()) {
			System.out.println(">>>subpart found: (" + rs.getInt(1) + ")");
			subparts.add(rs.getInt(1));
		}

		//search for subparts of subparts
		for (int i=0; i<subparts.size(); i++) {
			ResultSet ss = stmt.executeQuery("SELECT * FROM subpart_of WHERE mid = " + subparts.get(i));
			while (ss.next()) {
				if (!subparts.contains(ss.getInt(1))) {
					System.out.println(">>>subpart found: (" + ss.getInt(1) + ")");
					subparts.add(ss.getInt(1));
				}
			}
		}
	}

	public static void subpart_average(Statement stmt, String[] args) throws SQLException {
		System.out.println("averaging prices of all subparts of pid = " + args[1]);
		ResultSet rs = stmt.executeQuery("SELECT * FROM subpart_of WHERE mid = " + args[1]);
		List<Integer> subparts = new ArrayList<Integer>();
		while (rs.next()) {
			subparts.add(rs.getInt(1));
		}

		//search for subparts of subparts
		for (int i=0; i<subparts.size(); i++) {
			ResultSet ss = stmt.executeQuery("SELECT * FROM subpart_of WHERE mid = " + subparts.get(i));
			while (ss.next()) {
				if (!subparts.contains(ss.getInt(1))) {
					subparts.add(ss.getInt(1));
				}
			}
		}

		//find average price and print:
		Integer sum = 0;
		for (Integer pid : subparts){
			rs = stmt.executeQuery("SELECT * FROM parts WHERE pid = " + pid.toString());
			sum += rs.getInt(3);
		}
		
		System.out.println(">>>avg price of subparts " + subparts.toString() + ": " + (sum/subparts.size()));
	}

	public static void is_multiple_subpart(Statement stmt, String[] args) throws SQLException {
		System.out.println("finding all parts that are subparts of multiple parts:");
		ResultSet rs = stmt.executeQuery("SELECT name " +
										 "FROM parts JOIN 	(SELECT pid, count(pid) as count " +
						 								  	"FROM subpart_of " +
						 									"GROUP BY pid) as multiple " +
										 "ON parts.pid = multiple.pid " +
										 "WHERE count>1");
		while (rs.next()) {
			System.out.println(">>>" + rs.getString("name"));
		}
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

			//read file input
			try (BufferedReader br = new BufferedReader(new FileReader("transfile.txt"))) {
				String line = br.readLine();

				while (line != null) {

					String[] arguments = line.split(" ");
					System.out.println("\nArguments:" + Arrays.toString(arguments));
					// System.out.println("--Argument length: " + arguments.length);
					// System.out.println(arguments[0].getClass().getSimpleName());

					if (arguments[0].equals("1")) {
						delete(stmt, arguments);
					} else if (arguments[0].equals("2")) {
						insert(stmt, arguments);
					} else if (arguments[0].equals("3")) {
						average(stmt);
					} else if (arguments[0].equals("4")) {
						all_subparts(stmt, arguments);
					} else if (arguments[0].equals("5")) {
						subpart_average(stmt, arguments);
					} else {
						is_multiple_subpart(stmt, arguments);

					}
					line = br.readLine();
				}
			}

			//clear tables
			stmt.executeUpdate("drop table if exists parts");
			stmt.executeUpdate("drop table if exists subpart_of");

			con.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}