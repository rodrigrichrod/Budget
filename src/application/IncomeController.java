package application;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class IncomeController extends BaseController{

	@FXML
	private DialogPane incomeDialogPane;
	@FXML
	private TextField creditTextField;
	@FXML
	private ChoiceBox<String> incomeBox;
	private String type;
	
	public IncomeController() {
		super();
	}
	
	@FXML
	public void initialize() {
		incomeBox.setItems(FXCollections.observableArrayList("One Time", "Bi-Weekly","Monthly"));
		incomeBox.getSelectionModel().selectFirst();
	}
	
	public String getIncomeType() {
		return creditTextField.getText();
	}
	

	@Override
	@FXML
	public boolean validate() {
		
//		if(getDateField().getValue() != null)
//			System.out.println(getDateField().getValue());
//		if(monthlyData.getInstance().getDate() != null)
//			System.out.println(monthlyData.getInstance().getDate());
		
		boolean isDataEntered = true;
		LocalDate startDate = Database.getInstance().getStartingDate();
		System.out.println();
		if(amountField.getText() == null || amountField.getText().trim().isEmpty())
			isDataEntered = setErrorTxt("You left the amount field empty.");
		else if(!(Validation.validateAmount(amountField.getText())))
			isDataEntered = setErrorTxt("Please enter a valid amount");
		else if(creditTextField.getText() == null || creditTextField.getText().trim().isEmpty())
			isDataEntered = setErrorTxt("You left the credit text field empty.");
		else if(!(Validation.validateChars(creditTextField.getText())))
			isDataEntered = setErrorTxt("Please only enter valid characters");
		else if(dateField.getValue() == null) {
			isDataEntered = setErrorTxt("You left the date field empty.");			
		}
		else if(dateField.getValue().isBefore(startDate))
			isDataEntered = setErrorTxt("Sorry, the date you entered is before the starting date.");
		return isDataEntered;
	}
	
	public char translateFlag(int flag) {
		char repeatFlag = 'O';
		if(flag == 1)
			repeatFlag = 'B';
		else if(flag == 2) {
			repeatFlag = 'M';
		}
		return repeatFlag;
	}
	
	@Override
	public void processResults() {
		
		int incomeFlag = 1;
		int notRepeatableFlag = incomeBox.getSelectionModel().getSelectedIndex(); //bi-weekly used to be repeattype = 4
		char repeatFlag = translateFlag(notRepeatableFlag);
		double amount = monthlyData.round(Double.valueOf(amountField.getText().trim()),2);
		String billtype = creditTextField.getText().trim();
		LocalDate dueDate = dateField.getValue();
//		IncomeData.getInstance().addIncome(new Income(amount,billtype,dueDate, repeatFlag));		
		Database.getInstance().insertIncome(amount, billtype, dueDate, repeatFlag);
//		Database.getInstance().insertExpenses(amount, billtype, dueDate, repeatFlag);
		monthlyData.getInstance().updateCurrentMonthData(dueDate, amount, incomeFlag, notRepeatableFlag);
		if(notRepeatableFlag==1){
			LocalDate newDate = Validation.findNextWeekInMonth(dueDate);
			monthlyData.getInstance().biWeeklyUpdates(newDate, amount);
			
		}
		else if(notRepeatableFlag == 0 || notRepeatableFlag == 2)
			monthlyData.getInstance().updateRemainingMonthData(dueDate, amount, incomeFlag, notRepeatableFlag);

		monthlyData.getInstance().setRepeatMultiplier(1);		
	}	
}
