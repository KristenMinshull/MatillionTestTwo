/**
 * This class allows a user to query the FoodMart mySQL database in the Java console
 * Created using MAMP to host mySQL database and Eclipse IDE
 * @author Kristen Minshull
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Scanner;

public class FoodMartQuerying {
	private static final String USER = "root";
	private static final String PASS = "root";
	private static final String CONN = "jdbc:mysql://localhost:3306/foodmart";
	/**
	 * Main method which runs the console method
	 * @param args
	 * @throws SQLException Gives method the expectation of a SQL exception
	 * @throws Exception Gives method the expectation of a normal exception
	 */
	public static void main(String[] args) throws SQLException, Exception {
		input();
	}
	/**
	 * This method allows the user to navigate the console to 
	 * filter the database select query
	 * @throws Exception gives method exception due to the {@link #select(String, String, String)} method requiring this
	 */
	public static void input() throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.println("-MENU- \nEnter '1': to QUERY the database or '2': to EXIT");
		String menuOption = scanner.nextLine();
		if(menuOption.equals("1")) {
		System.out.println("Please specify a department by ID to filter...");
		String department = scanner.nextLine();
		System.out.println("Please specify a pay type to filter...\n'1': Hourly, '2': Monthly");
		String payType = scanner.nextLine();
		if (payType.equals("1")) {
			payType = "Hourly";
		} else if (payType.equals("2")) {
			payType = "Monthly";
		} else {
			payType = "";
		}
		System.out.println("Please specify an education level to filter by...");
		String edLevel = scanner.nextLine();
		select(department, payType, edLevel);
		} else if (menuOption.equals("2")) {
			scanner.close();
			System.exit(0);
		} else {
			input();
		}
	}
	/**
	 * This method returns employee full_name's as an array list and
	 * constructs a select statement based on {@link #input()} method
	 * and also contains error checking.
	 * @param department the department_id from console input
	 * @param payType the pay_type from console input
	 * @param edLevel the education_level from console input
	 * @return the full name of every employee from select statement
	 * @throws Exception exception is given if ArrayList or results are empty from select
	 */
	public static ArrayList<String> select(String department, String payType, String edLevel) throws Exception {
		try {
			Connection conn = getConnection();
			String select = "SELECT full_name FROM employee";
			String where = " WHERE department_id = ";
			PreparedStatement st;
			if (department.equals("") && payType.equals("") && edLevel.equals("")) {
				st = conn.prepareStatement(select);
			} else if (payType.equals("")){
				if (department.equals("")){
					st = conn.prepareStatement(select + " WHERE education_level = '" + edLevel+"'");
					} else if (edLevel.equals("")) {
						st = conn.prepareStatement(select + where + department);
					}
				else {
				st = conn.prepareStatement(select + where + department + " AND education_level = '" + edLevel+"'");
				}
			} else if (department.equals("")){
				if (edLevel.equals("")){
					st = conn.prepareStatement(select + ",position" + " WHERE employee.position_id = position.position_id AND position.pay_type = '" + payType+"'");
				} else {
					st = conn.prepareStatement(select + " WHERE education_level = '" + edLevel+"'" + " AND employee.position_id = position.position_id AND position.pay_type = '" + payType+"'");
					}
				}
			else if (edLevel.equals("")){
				st = conn.prepareStatement(select + ",position" + where + department + " AND employee.position_id = position.position_id AND position.pay_type = '" + payType+"'");
			} else {
				st = conn.prepareStatement(select + ",position" + where + department + " AND education_level = '" + edLevel+ "'" + " AND employee.position_id = position.position_id AND position.pay_type = '" + payType+"'");
			}
			ResultSet rs = st.executeQuery();
			
			ArrayList<String> selectArray = new ArrayList<String>();
			if(!rs.isBeforeFirst()) {
				System.out.println("No results found using select statement");
				input();
			}
			while (rs.next()) {
				selectArray.add(rs.getString("full_name"));
			}
			conn.close();
			input();
			return selectArray;
			} catch(Exception e) {
				System.out.println(e);
		}
		return null;
	
	}
	/**
	 * 
	 * @return returns established connection 
	 * @throws SQLException give method the expectation of possible database connection error
	 * @throws Exception give method the expectation of possible error when returning connection
	 */
	public static Connection getConnection() throws SQLException, Exception {
		try {
			Connection conn = null;
			conn = DriverManager.getConnection(CONN, USER, PASS);
			return conn;
		} catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}

}
