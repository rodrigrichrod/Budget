package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;

public class ExpenseData {
	private static ExpenseData instance = new ExpenseData();
	private static String filename = "ExpenseItems.txt";
	
	private List<Expenses> expense;
	private DateTimeFormatter formatter;
	
	public static ExpenseData getInstance() {
		return instance;
	}
	
	private ExpenseData() {
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		expense = FXCollections.observableArrayList();
	}
	
	public List<Expenses> getExpenses(){
		return expense;
	}
	public void addExpense(Expenses bill) {
		expense.add(bill);
	}
	
	public void setExpense(List<Expenses> expense) {
		this.expense = expense;
	}
	
	public void loadExpense() throws IOException{
		expense = FXCollections.observableArrayList();
		Path path = Paths.get(filename);
		BufferedReader br = Files.newBufferedReader(path);
		
		String input;
		
		try {
			while((input = br.readLine()) != null) {
				String[] items = input.split("\t");
				
				String amount = items[0];
				String nameType = items[1];
				String dateString = items[2];
				String repeat = items[3];
				LocalDate date = LocalDate.parse(dateString, formatter);
				char repeatType = repeat.charAt(0);
				Expenses ExpenseItem = new Expenses(Double.valueOf(amount), nameType, date,repeatType);
				expense.add(ExpenseItem);
			}
		} finally {
			if(br != null) {
				br.close();
			}
		}
	}
	
	public void storeExpense() throws IOException{
		Path path = Paths.get(filename);
		BufferedWriter bw = Files.newBufferedWriter(path);
		try {
			Iterator<Expenses> iter = expense.iterator();
			while(iter.hasNext()) {
				Expenses item = iter.next();
				bw.write(String.format("%s\t%s\t%s\t%s", 
						item.getMonthlyPayment(),
						item.getBillName(),
						item.getDayDue(),
						item.getRepeatFlag()));
				bw.newLine();
				
			}
			
		} finally {
			if(bw != null) {
				bw.close();
			}
		}
	}
}