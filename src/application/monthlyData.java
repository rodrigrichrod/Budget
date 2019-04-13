package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.collections.FXCollections;

public class monthlyData {
	private static monthlyData instance = new monthlyData();
	private static String filename = "monthData.txt";
	private static String startingFile = "initialDate.txt";
	private static LocalDate date;
	private static String popupMonth;
	private static String startingBalance;
	private static Map<String, String[]> monthdata;
	private static String finalStartingMonthBalance;
	private static boolean firstMonthStarted;
	private static boolean monthlyDataPopulated;
	private static String modifiedMonthBalance;
	private static Map<Integer, String> months;
	private static boolean repeatingMonthly;
	private static int repeatMultiplier;
	private static int finalMonth;


	public Map<Integer, String> getMonths() {
		return months;
	}

	public int getRepeatMultiplier() {
		return repeatMultiplier;
	}

	public void setRepeatMultiplier(int repeatMultiplier) {
		monthlyData.repeatMultiplier = repeatMultiplier;
	}
	
	public void increaseRepeatMultiplier() {
		monthlyData.repeatMultiplier++;
	}

	public boolean isRepeatingMonthly() {
		return repeatingMonthly;
	}

	public void setRepeatingMonthly(boolean repeatingMonthly) {
		monthlyData.repeatingMonthly = repeatingMonthly;
	}

	public boolean isFirstMonthStarted() {
		return firstMonthStarted;
	}

	public boolean isMonthlyDataPopulated() {
		return monthlyDataPopulated;
	}

	public void setMonthlyDataPopulated(boolean monthlyDataPopulated) {
		monthlyData.monthlyDataPopulated = monthlyDataPopulated;
	}

	public void setFirstMonthStarted(boolean firstMonthStarted) {
		monthlyData.firstMonthStarted = firstMonthStarted;
	}

	public String getFinalStartingMonthBalance() {
		return finalStartingMonthBalance;
	}

	public void setFinalStartingMonthBalance(String finalStartingMonthBalance) {
		monthlyData.finalStartingMonthBalance = finalStartingMonthBalance;
	}

	public static monthlyData getInstance() {
		return instance;
	}
	
	public String getPopupMonth() {
		return popupMonth;
	}

	public void setPopupMonth(String month) {
		monthlyData.popupMonth = month;
	}

	public LocalDate getDate() {
		return monthlyData.date;
	}

	public void setDate(LocalDate date) {
		monthlyData.date = date;
	}
	
	private monthlyData() {
		monthdata = FXCollections.observableHashMap();
		firstMonthStarted = false;
		monthlyDataPopulated = false;
		repeatingMonthly = false;
		repeatMultiplier = 1;
		months = FXCollections.observableHashMap();
		finalMonth = 0;
		
		months.put(1, "January");
		months.put(2, "February");
		months.put(3, "March");
		months.put(4, "April");
		months.put(5, "May");
		months.put(6, "June");
		months.put(7, "July");
		months.put(8, "August");
		months.put(9, "September");
		months.put(10, "October");
		months.put(11, "November");
		months.put(12, "December");
		
	}
	
	public int getFinalMonth() {
		return finalMonth;
	}

	public void setFinalMonth(int finalMonth) {
		monthlyData.finalMonth = finalMonth;
	}

	public Map<String, String[]> getMonthlyData(){
		return monthdata;
	}
	public void addMonthData(String month, String [] amounts) {
		monthdata.put(month, amounts);
	}
	
	
	//0 = expenses, 1 = income
	//repeat type for income
	//0 = one time, 1 = bi weekly
	//repeat type for expenses
	//0 = one time, 1 = monthly

	public void updateCurrentMonthData(LocalDate date, double amount, int addOrSubtract, int repeatType) {
		String month = date.getMonth().name();
		String [] newMonthAmounts = monthdata.get(month);
		int modifiedDate = date.getDayOfMonth()-1;
		int monthNum = date.getMonthValue();
		
		if(isRepeatingMonthly() && addOrSubtract==0 && date.getMonthValue() > 1) {
			padPreviousExpense(0, modifiedDate, String.valueOf(amount), newMonthAmounts);
			increaseRepeatMultiplier();
			newMonthAmounts[modifiedDate] = String.valueOf(round(Double.valueOf(newMonthAmounts[modifiedDate]) - (amount * repeatMultiplier), 2));			
		}
		else if(isRepeatingMonthly() && addOrSubtract==1 && repeatType == 2) {
			padPreviousIncome(0, modifiedDate, String.valueOf(amount), newMonthAmounts);
			increaseRepeatMultiplier();
			newMonthAmounts[modifiedDate] = String.valueOf(round(Double.valueOf(newMonthAmounts[modifiedDate]) + (amount * repeatMultiplier), 2));
		}
		else if(isRepeatingMonthly() && addOrSubtract==1 && repeatType == 1) {
			newMonthAmounts[modifiedDate] = String.valueOf(round(Double.valueOf(newMonthAmounts[modifiedDate]) + (amount * repeatMultiplier), 2));
			increaseRepeatMultiplier();
		}
//		else if(addOrSubtract==1 && date.getMonthValue() > 1 && isRepeatingMonthly())
		
		else if(!isRepeatingMonthly()) {
			//expense item, so subtract amount
			if(addOrSubtract ==0) {
				newMonthAmounts[modifiedDate] = String.valueOf(round(Double.valueOf(newMonthAmounts[modifiedDate]) - (amount), 2));
//				increaseRepeatMultiplier();
			}
			//income, so add the amounts
			else {
				newMonthAmounts[modifiedDate] = String.valueOf(round(Double.valueOf(newMonthAmounts[modifiedDate]) + (amount), 2));
//				increaseRepeatMultiplier();
			}
		}
		
		setModifiedMonthBalance(newMonthAmounts[modifiedDate]);
		
		//need to update the month up to the next bi weekly date for current month
		if(repeatType == 1 && addOrSubtract==1 && date.plusWeeks(2).getMonthValue() == date.getMonthValue()) {
			int weeks = 2;
			int count = 0;
			LocalDate newDate = date;
			while(newDate.plusWeeks(weeks).getMonthValue() == newDate.getMonthValue()) {
				
				padPreviousIncome(newDate.getDayOfMonth(), newDate.plusWeeks(2).getDayOfMonth()-1, String.valueOf(amount), newMonthAmounts);
				increaseRepeatMultiplier();
				newMonthAmounts[newDate.plusWeeks(2).getDayOfMonth()-1] = String.valueOf(round(Double.valueOf(newMonthAmounts[newDate.plusWeeks(2).getDayOfMonth()-1]) + (amount * repeatMultiplier),2));
				
				setModifiedMonthBalance(newMonthAmounts[newDate.plusWeeks(2).getDayOfMonth()-1]);
				weeks = weeks + 2;
				if(newDate.plusWeeks(weeks).getMonthValue() == newDate.getMonthValue()) {	
					padPreviousIncome(newDate.plusWeeks(2).getDayOfMonth(), newDate.plusWeeks(4).getDayOfMonth()-1, String.valueOf(amount), newMonthAmounts);
					increaseRepeatMultiplier();
					newMonthAmounts[newDate.plusWeeks(4).getDayOfMonth()-1] = String.valueOf(round(Double.valueOf(newMonthAmounts[newDate.plusWeeks(4).getDayOfMonth()-1]) + (amount * repeatMultiplier),2));
					count++;
					setModifiedMonthBalance(newMonthAmounts[newDate.plusWeeks(4).getDayOfMonth()-1]);
					weeks = weeks + 2;
				}
			}
			if(count == 0) {				
				padFutureIncome(newDate.plusWeeks(2).getDayOfMonth(), findNumDaysInMonth(newDate.getMonthValue())+1, String.valueOf(amount), newMonthAmounts);
//				increaseRepeatMultiplier();
			}		
			else {
				padFutureIncome(newDate.plusWeeks(4).getDayOfMonth(), findNumDaysInMonth(newDate.getMonthValue())+1, String.valueOf(amount), newMonthAmounts);
//				increaseRepeatMultiplier();
			}
		}
		//pad right until get to next date
		//updates the rest of the amounts in the month to the modified day's amount
		else if(addOrSubtract == 1)
			updateModifiedMonthData(modifiedDate+1,  findNumDaysInMonth(date.getMonthValue()) , newMonthAmounts, amount);
		else if(addOrSubtract == 0)
			updateModifiedMonthDataExpense(modifiedDate+1,  findNumDaysInMonth(date.getMonthValue()) , newMonthAmounts, amount);
//		setRepeatMultiplier(1);
		monthdata.replace(month, newMonthAmounts);
	}
	
	public void setDayAmount(LocalDate date, double amount) {
		int day = date.getDayOfMonth()-1;
		getMonthlyData().get(date.getMonth().toString())[day] = String.valueOf(amount);
	}
	
	public double getDayAmount(LocalDate date) {
		int day = date.getDayOfMonth()-1;
		String amount = getMonthlyData().get(date.getMonth().toString())[day];
		return Double.valueOf(amount);
		
	}
	
	public void padPreviousIncome(int startIndex, int endIndex, String amount, String [] amounts) {
		for(int i =startIndex; i<endIndex; i++) {
				amounts[i] = String.valueOf(round(Double.valueOf(amounts[i]) + (Double.valueOf(amount) * repeatMultiplier),2));
		}	
	}
	
	public void padPreviousExpense(int startIndex, int endIndex, String amount, String [] amounts) {
		for(int i =startIndex; i<endIndex; i++) {
				amounts[i] = String.valueOf(round(Double.valueOf(amounts[i]) - (Double.valueOf(amount) * repeatMultiplier),2));
		}	
	}
	
	public void padFutureIncome(int startIndex, int endIndex, String amount, String [] amounts) {
		if(startIndex == endIndex) {
			//write code to take previous months amount
			amounts[startIndex] = String.valueOf(round(Double.valueOf(amount),2));
			startIndex++;
		}
		for(int i =startIndex; i<endIndex-1; i++) {
				amounts[i] = String.valueOf(round(Double.valueOf(amounts[i]) + (Double.valueOf(amount) * repeatMultiplier),2));
		}	
	}
			
	//update the remaining balances with the previous days data
	public void updateModifiedMonthData(int startIndex, int endIndex, String [] amounts, double amount) {
		for(int i =startIndex; i<endIndex; i++) {
				amounts[i] = String.valueOf(round((Double.valueOf(amounts[i])) + (amount * repeatMultiplier),2));
		}	
	}
	
	public void updateModifiedMonthDataExpense(int startIndex, int endIndex, String [] amounts, double amount) {
		for(int i =startIndex; i<endIndex; i++) {
				amounts[i] = String.valueOf(round((Double.valueOf(amounts[i])) - (amount * repeatMultiplier),2));
		}	
	}
	
	public void weeklyDataUpdate(LocalDate dueDate, double amount, int addOrSubtract) {
		LocalDate temp = dueDate.plusWeeks(1);
		while(dueDate.isBefore(LocalDate.of (2020, 01, 01))) {
			double currentAmount = getDayAmount(dueDate);
			if(addOrSubtract == 1) {				
				double newAmount = currentAmount+(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);

			}
			else if(addOrSubtract == 0) {
				double newAmount = currentAmount-(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);
			}				
			dueDate = dueDate.plusDays(1);
			//increase the multiplier
			//add a week to the temp
			if(dueDate.isEqual(temp)) {
				setRepeatMultiplier(getRepeatMultiplier()+1);
				temp = dueDate.plusWeeks(1);
			}
		}
		setRepeatMultiplier(1);
	}
	
	
	public void oneTimeUpdate(LocalDate dueDate, double amount, int addOrSubtract) {
		while(dueDate.isBefore(LocalDate.of (2020, 01, 01))) {
			double currentAmount = getDayAmount(dueDate);
			if(addOrSubtract == 1) {				
				double newAmount = currentAmount+(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);
			}
			else if(addOrSubtract == 0) {
				double newAmount = currentAmount-(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);
			}				
			dueDate = dueDate.plusDays(1);
		}
		setRepeatMultiplier(1);
	}
	
	//bi weekly update
	public void biWeeklyDataUpdate(LocalDate dueDate, double amount, int addOrSubtract) {
		LocalDate temp = dueDate.plusWeeks(2);
		while(dueDate.isBefore(LocalDate.of (2020, 01, 01))) {
			double currentAmount = getDayAmount(dueDate);
			if(addOrSubtract == 1) {				
				double newAmount = currentAmount+(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);

			}
			else if(addOrSubtract == 0) {
				double newAmount = currentAmount-(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);
			}				
			dueDate = dueDate.plusDays(1);
			//increase the multiplier
			//add a week to the temp
			if(dueDate.isEqual(temp)) {
				setRepeatMultiplier(getRepeatMultiplier()+1);
				temp = dueDate.plusWeeks(2);
			}
		}
		setRepeatMultiplier(1);
	}
	
	//refactor
	public void monthlyDataUpdate(LocalDate dueDate, double amount, int addOrSubtract) {
		LocalDate temp = dueDate.plusMonths(1);
		while(dueDate.isBefore(LocalDate.of (2020, 01, 01))) {
			double currentAmount = getDayAmount(dueDate);
			if(addOrSubtract == 1) {				
				double newAmount = currentAmount+(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);
			}
			else if(addOrSubtract == 0) {
				double newAmount = currentAmount-(amount*getRepeatMultiplier());
				setDayAmount(dueDate, newAmount);
			}
			dueDate = dueDate.plusDays(1);
			//increase the multiplier
			//add a week to the temp
			if(dueDate.isEqual(temp)) {
				setRepeatMultiplier(getRepeatMultiplier()+1);
				temp = dueDate.plusMonths(1);
			}
		}
		setRepeatMultiplier(1);
	}
	
	public void updateRemainingMonthData(LocalDate dueDate, double amount, int addOrSubtract, int repeatType) {
		LocalDate nextMonth = dueDate.plusMonths(1);
		int month = nextMonth.getMonthValue();
		
		
		//update monthly expenses
		if(addOrSubtract==0 && repeatType ==1) {
			//put code to pad all the months data before the repeated date
			while(nextMonth.isBefore(LocalDate.of(2019, 12, 31))) {
				setRepeatingMonthly(true);
				updateCurrentMonthData(nextMonth, amount, addOrSubtract, repeatType);
				nextMonth = nextMonth.plusMonths(1);
			}
		}
		else if(addOrSubtract == 1 && repeatType == 2) {
			while(nextMonth.isBefore(LocalDate.of(2019, 12, 31))) {
				setRepeatingMonthly(true);
				updateCurrentMonthData(nextMonth, amount, addOrSubtract, repeatType);
				nextMonth = nextMonth.plusMonths(1);
			}
		}
		//one time income
		else if(addOrSubtract == 1) {
			setRepeatingMonthly(true);
			updateAllIncome(nextMonth, amount);
		}
		//one time expense
		else if(addOrSubtract == 0){
			setRepeatingMonthly(true);
			updateAllExpenses(nextMonth, amount);
		}
		setRepeatingMonthly(false);
	}
	
	public void updateAllIncome(LocalDate dueDate, double amount) {
		while(dueDate.isBefore(LocalDate.of(2019, 12, 31))) {
			String month = dueDate.getMonth().name();
			String [] amounts = monthdata.get(month);
			int numDaysInMonth = findNumDaysInMonth(dueDate.getMonthValue());
			
			for(int i =0; i<numDaysInMonth; i++) {
				amounts[i] = String.valueOf(round(Double.valueOf(amounts[i]) + amount,2));
			}	
			monthdata.replace(month, amounts);
			dueDate = dueDate.plusMonths(1);
		}
	}
	
	public void updateAllExpenses(LocalDate dueDate, double amount) {
		while(dueDate.isBefore(LocalDate.of(2019, 12, 31))) {
			String month = dueDate.getMonth().name();
			String [] amounts = monthdata.get(month);
			int numDaysInMonth = findNumDaysInMonth(dueDate.getMonthValue());
			
			for(int i =0; i<numDaysInMonth; i++) {
				amounts[i] = String.valueOf(round(Double.valueOf(amounts[i]) - amount,2));
			}	
			monthdata.replace(month, amounts);
			dueDate = dueDate.plusMonths(1);
		}
	}
	
	public void biWeeklyUpdates(LocalDate dueDate, double amount) {
		LocalDate newUpdate = dueDate;
		final int weeks = 2;
		String month = newUpdate.getMonth().name();
		int monthValue = newUpdate.getMonthValue();
		boolean finishedCurrentMonth = false;
		String [] newMonthAmounts = monthdata.get(month);
		if(newUpdate.isBefore(LocalDate.of(2019, 12, 31)))
			setRepeatingMonthly(true);
		while(newUpdate.isBefore(LocalDate.of(2019, 12, 31))) {
				if(newUpdate.getMonthValue() != newUpdate.minusWeeks(weeks).getMonthValue()) {
					//pad left
					padPreviousIncome(0, newUpdate.getDayOfMonth()-1, String.valueOf(amount), newMonthAmounts);
					increaseRepeatMultiplier();
				    //add amount
					newMonthAmounts[newUpdate.getDayOfMonth()-1] = String.valueOf(round(Double.valueOf(newMonthAmounts[newUpdate.getDayOfMonth()-1]) + (amount * repeatMultiplier),2));
					
					//pad right 
					//2 weeks from now will still be in the same month
					if(newUpdate.getMonthValue() == newUpdate.plusWeeks(weeks).getMonthValue()) {
						padFutureIncome(newUpdate.getDayOfMonth(), newUpdate.plusWeeks(weeks).getDayOfMonth(), String.valueOf(amount), newMonthAmounts);
						increaseRepeatMultiplier();
					}
				}
				else if(newUpdate.getMonthValue() == newUpdate.plusWeeks(weeks).getMonthValue()) {
					//update amount
					newMonthAmounts[newUpdate.getDayOfMonth()-1] = String.valueOf(round(Double.valueOf(newMonthAmounts[newUpdate.getDayOfMonth()-1]) + (amount * repeatMultiplier),2));
					
					//pad right
					padFutureIncome(newUpdate.getDayOfMonth(), newUpdate.plusWeeks(weeks).getDayOfMonth(), String.valueOf(amount), newMonthAmounts);		
					increaseRepeatMultiplier();
				}			
				else  {
					//update then pad right
					newMonthAmounts[newUpdate.getDayOfMonth()-1] = String.valueOf(round(Double.valueOf(newMonthAmounts[newUpdate.getDayOfMonth()-1]) + (amount * repeatMultiplier),2));			
					//pad right until end of month
					padFutureIncome(newUpdate.getDayOfMonth(), findNumDaysInMonth(newUpdate.getMonthValue())+1, String.valueOf(amount), newMonthAmounts);
					monthdata.replace(month, newMonthAmounts);	
					finishedCurrentMonth = true;
				}		
				newUpdate = newUpdate.plusWeeks(weeks);
				if(finishedCurrentMonth) {
					newMonthAmounts = monthdata.get(newUpdate.getMonth().name());
					finishedCurrentMonth = false;
					monthValue = newUpdate.getMonthValue();
					month = newUpdate.getMonth().name();					
				}			
		}
		setRepeatingMonthly(false);
	}
	
	public String previousMonthBalance(int month) {
		String previousBalance = getMonthlyData().get(months.get(month).toUpperCase())[findNumDaysInMonth(month)-1];		
		return previousBalance;
	}
	
	public int findDayOfWeek(int month) {
		LocalDate date = LocalDate.of(2019, month, 1); 		
	    DayOfWeek dayOfWeek = date.getDayOfWeek();	    
	    int dayOfWeekIntValue = dayOfWeek.getValue(); 
	    if(dayOfWeekIntValue==7)
	    	dayOfWeekIntValue=0;

		return dayOfWeekIntValue;
	}
	
	public int findNumDaysInMonth(int month) {
		// Get the number of days in that month
		YearMonth yearMonthObject = YearMonth.of(2019, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		return daysInMonth;		
	}
	
	public String getMonth(int month) {
		YearMonth yearMonthObject = YearMonth.of(2019, month);
		return yearMonthObject.getMonth().name();
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	 
	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public void loadMonthlyAmounts() throws IOException{
		monthdata = FXCollections.observableHashMap();
		Path path = Paths.get(filename);
		BufferedReader br = Files.newBufferedReader(path);
		
		String input;
		try {
			while((input = br.readLine()) != null) {
				String[] items = input.split("\t");
				String month = items[0];
				String[] amounts = new String [items.length-1];
				for(int i=0; i<amounts.length; i++) {
					amounts[i]= items[i+1];
				}
				monthdata.put(month, amounts);
			}
		} finally {
			if(br != null) {
				br.close();
			}
		}		
	}
	
	//loading the monthly amounts from the database and storing it into memory(hash map)
	public void retrieveMonthlyAmounts(){
		monthdata = FXCollections.observableHashMap();
		TreeMap<LocalDate,Double> monthAmounts = Database.getInstance().loadMonthlyAmounts();
		LocalDate startDate = Database.getInstance().getStartingDate();

		int monthNum = startDate.getMonthValue();
		
		//need to fill all the data with empty amounts for any month that is before
		//the original balance date
		if(monthNum>1) {
			for(int i = 1; i<monthNum; i++) {
				String monthName = months.get(i);
				String [] amountData = new String[Validation.findNumDaysInMonth(i)];
				Arrays.fill(amountData, "");
				monthdata.put(monthName, amountData);
				}
		}
		
		//storing variables for the data for the month in the start date
		int numDaysInMonth = Validation.findNumDaysInMonth(monthNum);
		String [] startDateAmounts = new String [numDaysInMonth];
		String startMonth = startDate.getMonth().toString();
		int startDateDay = startDate.getDayOfMonth();
		
		//padding all the days of the month prior to the actual start date
		if(startDateDay>1) {
			for(int i = 0; i<startDateDay-1; i++) {
				startDateAmounts[i] = "";
			}
		}
		
		//fill in the rest of the starting month
		for(int i = startDateDay-1; i<numDaysInMonth; i++) {
			startDateAmounts[i] = monthAmounts.get(startDate).toString();
			startDate = startDate.plusDays(1);
		}
		
		monthdata.put(startMonth, startDateAmounts);
		int nextMonth = startDate.getMonthValue();
		
		for(int i = nextMonth; i<13;i++) {	
			String monthName = startDate.getMonth().toString();
			String [] amountData = new String[Validation.findNumDaysInMonth(i)];
			for(int j=0; j<amountData.length; j++) {
				amountData[j] = monthAmounts.get(startDate).toString();
				startDate = startDate.plusDays(1);
			}
			monthdata.put(monthName, amountData);			
		}
	}
	
//	updating the database with the amounts from memory
	//month = key in hash map
	//day = day-1 in string array
	public void updateAmountsToDB() {
		LocalDate startDate = Database.getInstance().getStartingDate();
		
		while(startDate.isBefore(LocalDate.of(2020, 01, 01))) {
			String month = startDate.getMonth().toString();
			int day = startDate.getDayOfMonth()-1;
			String amountOfDay = monthdata.get(month)[day];
			Database.getInstance().updateMonthData(startDate, Double.valueOf(amountOfDay));
			startDate = startDate.plusDays(1);
		}		
	}
	
	public void storeMonthlyAmounts() throws IOException{
		Path path = Paths.get(filename);
		BufferedWriter bw = Files.newBufferedWriter(path);
		
		try {
			Iterator<Entry<String, String[]>> iter = monthdata.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry<String, String[]> pairs = iter.next();
				bw.write(String.format("%s\t", 
						pairs.getKey()));
				for(int i=0; i<pairs.getValue().length;i++)
					bw.write(String.format("%s\t", pairs.getValue()[i]));
				bw.newLine();				
			}			
		} finally {
			if(bw != null) {
				bw.close();
			}
		}		
	}

	public void setStartingBalance(String balance) {
		// TODO Auto-generated method stub
	
		monthlyData.startingBalance = balance;
	}
	public String getStartingBalance() {
		return startingBalance;
	}

	public String getModifiedMonthBalance() {
		return modifiedMonthBalance;
	}

	public void setModifiedMonthBalance(String modifiedMonthBalance) {
		monthlyData.modifiedMonthBalance = modifiedMonthBalance;
	}
	
	public void loadStartingDate() throws IOException{
//		income = FXCollections.observableArrayList();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Path path = Paths.get(startingFile);
		BufferedReader br = Files.newBufferedReader(path);
		
		String input = br.readLine();
		
		try {
			String[] items = input.split("\t");
			String dateString = items[0];
			String month = items[1];
			date = LocalDate.parse(dateString, formatter);
			finalMonth = Integer.parseInt(month);
//			while((input = br.readLine()) != null) {
//				String[] items = input.split("\t");
//				
//				String amount = items[0];
//				String nameType = items[1];
//				String dateString = items[2];
//				LocalDate date = LocalDate.parse(dateString, formatter);
//				
//				Income incomeItem = new Income(Double.valueOf(amount), nameType, date);
//				income.add(incomeItem);
//			}
		} finally {
			if(br != null) {
				br.close();
			}
		}
	}
	
	public void storeStartingDate() throws IOException{
		Path path = Paths.get(startingFile);
		BufferedWriter bw = Files.newBufferedWriter(path);
		try {
//			Iterator<Income> iter = income.iterator();
//			while(iter.hasNext()) {
//				Income item = iter.next();
			if(date != null)
				bw.write(String.format("%s\t%s", date,finalMonth));
//				bw.newLine();
				
//			}
			
		} finally {
			if(bw != null) {
				bw.close();
			}
		}
	}
	
	
	public boolean isTxtFileEmpty(String filename) throws FileNotFoundException, IOException {
		boolean isEmpty = false;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		try  {
		    String line = br.readLine();
		    if (line == null || 
		        (line.length() == 0 && br.readLine() == null)) {
		    	isEmpty = true;
		        System.out.println("File is empty");
		    } else {
		        System.out.println("Successfully read file: " + filename);
		    }
		}
		finally {
			if(br != null) {
				br.close();
			}
		}
		return isEmpty;
	}
}
