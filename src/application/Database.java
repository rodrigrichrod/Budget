package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javafx.collections.FXCollections;




public class Database {
	
	public static final String DB_NAME = "budget";
	public static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/" + DB_NAME;
	private static final String USERNAME = "";
	private static final String PASSWORD = "";
	
	public static final String TABLE_EXPENSES = "EXPENSES";
	public static final String TABLE_INCOME = "INCOME";
	public static final String TABLE_USERS = "USERS";
	public static final String TABLE_SAVED_DATA = "SAVED_DATA";
	public static final String TABLE_MONTHLY_DATA = "MONTHLY_DATA";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_AMOUNT = "amount";
	public static final String COLUMN_NAMETYPE = "nameType";
	public static final String COLUMN_DATERECEIVED = "dateReceived";
	public static final String COLUMN_REPEATFLAG = "repeatFlag";
	public static final String COLUMN_USERID = "user_id";
	public static final String COLUMN_CREATEDAT = "created_at";
	public static final String COLUMN_STARTINGAMOUNT = "startingAmount";
	public static final String COLUMN_STARTINGDATE = "startingDate";
	public static final String COLUMN_LASTMONTHVISITED = "lastMonthVisited";
	public static final String COLUMN_USERNAME = "user_name";
	public static final String COLUMN_PASSWORD = "pass_word";
	public static final String COLUMN_BDATE = "bDate";
	public static final String OPEN_PAREN = "( ";
	public static final String CLOSED_PAREN = ") ";
	public static final String COMMA = " ,";
	
	public static final String INSERT_EXPENSES = "INSERT INTO " + TABLE_EXPENSES + 
			'(' + COLUMN_AMOUNT + COMMA + COLUMN_NAMETYPE + COMMA + 
			COLUMN_DATERECEIVED + COMMA + COLUMN_REPEATFLAG + COMMA + 
			COLUMN_USERID + CLOSED_PAREN + " VALUES(?, ?, ?, ?, ?)";
	
	public static final String INSERT_INCOME = "INSERT INTO " + TABLE_INCOME + 
			'(' + COLUMN_AMOUNT + COMMA + COLUMN_NAMETYPE + COMMA + 
			COLUMN_DATERECEIVED + COMMA + COLUMN_REPEATFLAG + COMMA + 
			COLUMN_USERID + CLOSED_PAREN + " VALUES(?, ?, ?, ?, ?)";
	
	public static final String INSERT_USERS = "INSERT INTO " + TABLE_USERS + 
			'(' + COLUMN_USERNAME + COMMA + COLUMN_PASSWORD + CLOSED_PAREN + " VALUES(?, ?)";
	
	public static final String INSERT_SAVEDDATA = "INSERT INTO " + TABLE_SAVED_DATA + 
			'(' + COLUMN_STARTINGAMOUNT + COMMA + COLUMN_STARTINGDATE + COMMA + 
			COLUMN_LASTMONTHVISITED + COMMA + COLUMN_USERID + CLOSED_PAREN + 
			" VALUES(?, ?, ?, ?)";
	
	public static final String INSERT_MONTHLYDATA = "INSERT INTO " + TABLE_MONTHLY_DATA + 
			'(' + COLUMN_BDATE + COMMA + COLUMN_AMOUNT + COMMA + COLUMN_USERID + 
			CLOSED_PAREN + " VALUES(?, ?, ?)";
	
	
	public static final String UPDATE_SAVEDDATA = "UPDATE " + TABLE_SAVED_DATA + 
			" SET " + COLUMN_LASTMONTHVISITED + " = ? " + " WHERE " + COLUMN_USERID + 
			" = 1";
	
	public static final String UPDATE_MONTHLYDATA = "UPDATE " + TABLE_MONTHLY_DATA + 
			" SET " + COLUMN_AMOUNT + " = ? " + " WHERE " + COLUMN_BDATE + 
			" = ?";
	
	public static final String SELECT_LASTMONTHVISITED = "SELECT " + COLUMN_LASTMONTHVISITED + 
			" FROM " + TABLE_SAVED_DATA + " WHERE " + COLUMN_USERID + " = 1" ;
	
	public static final String SELECT_STARTINGDATE = "SELECT " + COLUMN_STARTINGDATE + 
			" FROM " + TABLE_SAVED_DATA + " WHERE " + COLUMN_USERID + " = 1" ;
	
	public static final String SELECT_STARTINGAMOUNT = "SELECT " + COLUMN_STARTINGAMOUNT + 
			" FROM " + TABLE_SAVED_DATA + " WHERE " + COLUMN_USERID + " = 1" ;
	
	public static final String SELECT_MONTHLYDATA = "SELECT " + COLUMN_BDATE + COMMA + COLUMN_AMOUNT +
			" FROM " + TABLE_MONTHLY_DATA + " WHERE " + COLUMN_USERID + " = 1" ;
		
	private Connection conn;
	private PreparedStatement insertIntoExpenses;
	private PreparedStatement insertIntoIncome;
	private PreparedStatement insertIntoUsers;
	private PreparedStatement insertIntoSavedData;
	private PreparedStatement updateLastMonthVisited;
	private PreparedStatement insertIntoMonthlyData;
	private PreparedStatement updateMonthlyData;
	private static Database instance = new Database();

	
	public static Database getInstance() {
		return instance;
	}
	
	public boolean open() {
		try {
			conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
			insertIntoIncome = conn.prepareStatement(INSERT_INCOME);
			insertIntoExpenses = conn.prepareStatement(INSERT_EXPENSES);
			insertIntoSavedData = conn.prepareStatement(INSERT_SAVEDDATA);
			insertIntoUsers = conn.prepareStatement(INSERT_USERS);
			insertIntoMonthlyData = conn.prepareStatement(INSERT_MONTHLYDATA);
			updateLastMonthVisited = conn.prepareStatement(UPDATE_SAVEDDATA);
			updateMonthlyData = conn.prepareStatement(UPDATE_MONTHLYDATA);
			return true;
		}
		catch(SQLException e) {
			System.out.println("Could not connect to DB." + e.getMessage());
			return false;
		}
	}
	
	public void close() {
		try {
			if(conn!=null)
				conn.close();
			if(insertIntoExpenses!=null)
				insertIntoExpenses.close();
			if(insertIntoIncome!=null)
				insertIntoIncome.close();
			if(insertIntoUsers!=null)
				insertIntoUsers.close();
			if(insertIntoSavedData!=null)
				insertIntoSavedData.close();
			if(updateLastMonthVisited!=null)
				updateLastMonthVisited.close();
			if(updateMonthlyData!=null)
				updateMonthlyData.close();
		}
		catch(SQLException e) {
			System.out.println("Could not close connection" + e.getMessage());
		}	
	}
	
	public void insertExpenses(double amount, String name, LocalDate date, char flag) {
		try {
			conn.setAutoCommit(false);
			insertIntoExpenses.setDouble(1, amount);
			insertIntoExpenses.setString(2, name);
			insertIntoExpenses.setDate(3, Date.valueOf(date));
			insertIntoExpenses.setString(4, String.valueOf(flag));
			insertIntoExpenses.setInt(5, 1);
			
			int affectedRows = insertIntoExpenses.executeUpdate();
			if(affectedRows ==1)
				conn.commit();
			else
				throw new SQLException("The expense insert failed");
			
		}
		catch(SQLException e) {
			System.out.println("Insert expense failed: " + e.getMessage());
			try {
				System.out.println("Performing rollback");
				conn.rollback();
			}
			catch(SQLException e2) {
				System.out.println("unable to rollback" + e2.getMessage());
			}
			
		}	
		finally {
			try {
				System.out.println("Resetting default commit behavior");
				conn.setAutoCommit(false);
			}
			catch(SQLException e) {
				System.out.println("couldn't reset auto committ" + e.getMessage());
			}
		}
	}	
	
	public void insertIncome(double amount, String name, LocalDate date, char flag) {
		try {
			conn.setAutoCommit(false);
			insertIntoIncome.setDouble(1, amount);
			insertIntoIncome.setString(2, name);
			insertIntoIncome.setDate(3, Date.valueOf(date));
			insertIntoIncome.setString(4, String.valueOf(flag));
			insertIntoIncome.setInt(5, 1);
			
			int affectedRows = insertIntoIncome.executeUpdate();
			if(affectedRows ==1)
				conn.commit();
			else
				throw new SQLException("The Income insert failed");
			
		}
		catch(SQLException e) {
			System.out.println("Insert Income failed: " + e.getMessage());
			try {
				System.out.println("Performing rollback");
				conn.rollback();
			}
			catch(SQLException e2) {
				System.out.println("unable to rollback" + e2.getMessage());
			}
			
		}	
		finally {
			try {
				System.out.println("Resetting default commit behavior");
				conn.setAutoCommit(false);
			}
			catch(SQLException e) {
				System.out.println("couldn't reset auto committ" + e.getMessage());
			}
		}
	}
	
	public void insertSavedData(double startingAmount, LocalDate startingDate, int lastMonthVisited) {
		try {
			conn.setAutoCommit(false);
			insertIntoSavedData.setDouble(1, startingAmount);
			insertIntoSavedData.setDate(2, Date.valueOf(startingDate));
			insertIntoSavedData.setInt(3, lastMonthVisited);
			insertIntoSavedData.setInt(4, 1);
			
			int affectedRows = insertIntoSavedData.executeUpdate();
			if(affectedRows ==1)
				conn.commit();
			else
				throw new SQLException("The Saved Data insert failed");			
		}
		catch(SQLException e) {
			System.out.println("Insert into Saved Data failed: " + e.getMessage());
			try {
				System.out.println("Performing rollback");
				conn.rollback();
			}
			catch(SQLException e2) {
				System.out.println("unable to rollback" + e2.getMessage());
			}
			
		}	
		finally {
			try {
				System.out.println("Resetting default commit behavior");
				conn.setAutoCommit(false);
			}
			catch(SQLException e) {
				System.out.println("couldn't reset auto committ" + e.getMessage());
			}
		}
	}
	
	public void insertMonthlyData(LocalDate date, double amount) {
		try {
			conn.setAutoCommit(false);
			insertIntoMonthlyData.setDate(1, Date.valueOf(date));
//			if(amount!=Double.NaN)
			insertIntoMonthlyData.setDouble(2, amount);
			insertIntoMonthlyData.setInt(3, 1);
			int affectedRows = insertIntoMonthlyData.executeUpdate();
			if(affectedRows ==1)
				conn.commit();
			else
				throw new SQLException("The monthly data insert failed");
			
		}
		catch(SQLException e) {
			System.out.println("Insert monthly data failed: " + e.getMessage());
			try {
				System.out.println("Performing rollback");
				conn.rollback();
			}
			catch(SQLException e2) {
				System.out.println("unable to rollback" + e2.getMessage());
			}
			
		}	
		finally {
			try {
				System.out.println("Resetting default commit behavior");
				conn.setAutoCommit(false);
			}
			catch(SQLException e) {
				System.out.println("couldn't reset auto committ" + e.getMessage());
			}
		}
	}
	
	public void updateMonthData(LocalDate date, double amount) {
		try {
			conn.setAutoCommit(false);
			
			updateMonthlyData.setDouble(1, amount);
			updateMonthlyData.setDate(2, Date.valueOf(date));
			
			int affectedRows = updateMonthlyData.executeUpdate();
			if(affectedRows ==1)
				conn.commit();
			else
				throw new SQLException("The update month data update failed");			
		}
		catch(SQLException e) {
			System.out.println("Update month data  to Monthly data failed: " + e.getMessage());
			try {
				System.out.println("Performing rollback");
				conn.rollback();
			}
			catch(SQLException e2) {
				System.out.println("unable to rollback" + e2.getMessage());
			}
			
		}	
		finally {
			try {
				System.out.println("Resetting default commit behavior");
				conn.setAutoCommit(false);
			}
			catch(SQLException e) {
				System.out.println("couldn't reset auto committ" + e.getMessage());
			}
		}
	}
	
	public void updateLastMonth(int lastMonthVisited) {
		try {
			conn.setAutoCommit(false);
			
			updateLastMonthVisited.setInt(1, lastMonthVisited);
			
			int affectedRows = updateLastMonthVisited.executeUpdate();
			if(affectedRows ==1)
				conn.commit();
			else
				throw new SQLException("The last month visited update failed");			
		}
		catch(SQLException e) {
			System.out.println("Update last month visited to Saved Data failed: " + e.getMessage());
			try {
				System.out.println("Performing rollback");
				conn.rollback();
			}
			catch(SQLException e2) {
				System.out.println("unable to rollback" + e2.getMessage());
			}
			
		}	
		finally {
			try {
				System.out.println("Resetting default commit behavior");
				conn.setAutoCommit(false);
			}
			catch(SQLException e) {
				System.out.println("couldn't reset auto committ" + e.getMessage());
			}
		}
	}
	
	public double getStartingAmount() {
		
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(SELECT_STARTINGAMOUNT);
			double startingAmount;
			results.next();
			startingAmount = results.getDouble(COLUMN_STARTINGAMOUNT);	
			return startingAmount;
		}
		catch(SQLException e) {
			System.out.println("unable to properly execute query" + e.getMessage());
			return -1;			
		}		
	}
	
	public LocalDate getStartingDate() {
		
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(SELECT_STARTINGDATE);
			LocalDate startingDate;
			results.next();
			startingDate = results.getDate(COLUMN_STARTINGDATE).toLocalDate();	
			return startingDate;
		}
		catch(SQLException e) {
			System.out.println("unable to properly execute query" + e.getMessage());
			return LocalDate.MIN;			
		}		
	}
	
	public int getLastMonthVisited() {
		
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(SELECT_LASTMONTHVISITED);
			int lastMonthVisited;
			results.next();
			lastMonthVisited = results.getInt(COLUMN_LASTMONTHVISITED);	
			return lastMonthVisited;
		}
		catch(SQLException e) {
			System.out.println("unable to properly execute query" + e.getMessage());
			return -1;			
		}		
	}
	
	public TreeMap<LocalDate, Double> loadMonthlyAmounts() {
		TreeMap<LocalDate, Double> amounts = new TreeMap<LocalDate, Double>();
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(SELECT_MONTHLYDATA);
			

			while(results.next()) {		
				amounts.put(results.getDate(COLUMN_BDATE).toLocalDate(), results.getDouble(COLUMN_AMOUNT));
//				Expenses exp = new Expenses(results.getDouble(COLUMN_AMOUNT), results.getString(COLUMN_NAMETYPE), results.getDate(COLUMN_DATERECEIVED).toLocalDate(), results.getString(COLUMN_REPEATFLAG).charAt(0));
//				ExpenseData.getInstance().addExpense(exp);
			}
			System.out.println("expenses stored");
//			for(Expenses e: ExpenseData.getInstance().getExpenses())
//				System.out.println(e.getBillName() + "\t" + e.getMonthlyPayment() + "\t" + e.getDayDue());
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return amounts;			
	}
}
