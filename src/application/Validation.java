package application;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
 
public class Validation {
	
	public static boolean validateAmount(String amount) {		
		boolean match = amount.matches("[+-]?\\d+\\.?(\\d+)?");
		return match;
	}
	
	public static boolean validateChars(String type) {
		boolean match = type.matches("[a-zA-z\\s]*");
		return match;
	}
	
	public static int findNumDaysInMonth(int month) {
		// Get the number of days in that month
		YearMonth yearMonthObject = YearMonth.of(2019, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
//		yearMonthObject.
		return daysInMonth;		
	}
	
	public static String getMonth(int month) {
		YearMonth yearMonthObject = YearMonth.of(2019, month);
		return yearMonthObject.getMonth().name();
	}
	
	public static int findDayOfWeek(int month) {
		LocalDate date = LocalDate.of(2019, month, 1); 		
	    DayOfWeek dayOfWeek = date.getDayOfWeek();
	    
	    int dayOfWeekIntValue = dayOfWeek.getValue(); 
	    if(dayOfWeekIntValue==7)
	    	dayOfWeekIntValue=0;
		return dayOfWeekIntValue;
	}
	
	public static LocalDate findNextWeekInMonth(LocalDate date) {
		int weeks = 0;
		LocalDate newMonthWeek = date;
		while(date.getMonthValue() >= newMonthWeek.plusWeeks(weeks).getMonthValue()) {
			weeks = weeks + 2;
		}
		return newMonthWeek.plusWeeks(weeks);		
	}
	
	public static String convertDateToMonth(LocalDate date) {
		String month = date.getMonth().name().trim();
		return month;
	}
}
