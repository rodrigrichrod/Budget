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
  
public class IncomeData {
	private static IncomeData instance = new IncomeData();
	private static String filename = "IncomeItems.txt";
	
	private List<Income> income;
	private DateTimeFormatter formatter;
	
	public static IncomeData getInstance() {
		return instance;
	}
	
	private IncomeData() {
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		income = FXCollections.observableArrayList();
	}
	
	public void addIncome(Income credit) {
		income.add(credit);
	}
	
	public List<Income> getIncome(){
		return income;
	}
	
	public void setIncome(List<Income> income) {
		this.income = income;
	}
	
	public void loadIncome() throws IOException{
		income = FXCollections.observableArrayList();
		Path path = Paths.get(filename);
		BufferedReader br = Files.newBufferedReader(path);
		
		String input;
		
		try {
			while((input = br.readLine()) != null) {
				String[] items = input.split("\t");
				
				String amount = items[0];
				String nameType = items[1];
				String dateString = items[2];
				LocalDate date = LocalDate.parse(dateString, formatter);
				String repeat = items[3];
				char repeatType = repeat.charAt(0);
				
				Income incomeItem = new Income(Double.valueOf(amount), nameType, date, repeatType);
				income.add(incomeItem);
			}
		} finally {
			if(br != null) {
				br.close();
			}
		}
	}
	
	public void storeIncome() throws IOException{
		Path path = Paths.get(filename);
		BufferedWriter bw = Files.newBufferedWriter(path);
		try {
			Iterator<Income> iter = income.iterator();
			while(iter.hasNext()) {
				Income item = iter.next();
				bw.write(String.format("%s\t%s\t%s\t%s", 
						item.getAmount(),
						item.getNameType(),
						item.getDateReceived(),
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































