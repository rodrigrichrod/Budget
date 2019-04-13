package application;

import java.time.LocalDate;

public class Expenses {
//	private double currentBalance;
	private double monthlyPayment;
	private String billName;
	private LocalDate dueDate;
	private char repeatFlag;
	
//	private double minMonthlyPayment;
	public Expenses(double monthlyPayment, String billName, LocalDate dueDate, char repeatFlag) {
		super();
		this.monthlyPayment = monthlyPayment;
		this.billName = billName;
		this.dueDate = dueDate;
		this.repeatFlag = repeatFlag;		
	}
	
	public char getRepeatFlag() {
		return repeatFlag;
	}

	public void setRepeatFlag(char repeatFlag) {
		this.repeatFlag = repeatFlag;
	}

	public LocalDate getDayDue() {
		return dueDate;
	}
	public void setDayDue(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	public String getBillName() {
		return billName;
	}
	public void setBillName(String billName) {
		this.billName = billName;
	}
	public double getMonthlyPayment() {
		return monthlyPayment;
	}
	public void setMonthlyPayment(double monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}
	
}

