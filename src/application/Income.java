package application;

import java.time.LocalDate;

public class Income {
	private double amount;
	private String nameType;
	private LocalDate dateReceived;
	private char repeatFlag;
	
	public Income(double amount, String nameType, LocalDate dateReceived, char repeatFlag) {
		super();
		this.amount = amount;
		this.nameType = nameType;
		this.dateReceived = dateReceived;
		this.repeatFlag = repeatFlag;
	}
	public char getRepeatFlag() {
		return repeatFlag;
	}
	public void setRepeatFlag(char repeatFlag) {
		this.repeatFlag = repeatFlag;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getNameType() {
		return nameType;
	}
	public void setNameType(String nameType) {
		this.nameType = nameType;
	}
	public LocalDate getDateReceived() {
		return dateReceived;
	}
	public void setDateReceived(LocalDate dateReceived) {
		this.dateReceived = dateReceived;
	}
	
	
}
