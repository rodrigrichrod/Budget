package application;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

public class ExpenseController extends BaseController{

	@FXML
	private DialogPane expenseDialogPane;
	@FXML
	private TextField billTypeField;
	@FXML
	private ChoiceBox<String> expenseBox;
	
	public ExpenseController() {
		super();

	}
	
	
	@FXML
	public void initialize() {
		expenseBox.setItems(FXCollections.observableArrayList("One Time", "Monthly"));
		expenseBox.getSelectionModel().selectFirst();
	}
	
	@Override
	@FXML
	public boolean validate() {
		boolean isDataEntered = true;
		LocalDate startDate = Database.getInstance().getStartingDate();
		if(amountField.getText() == null || amountField.getText().trim().isEmpty()   ) {
			isDataEntered = setErrorTxt("You left the amount field empty.");
		}
		else if(!(Validation.validateAmount(amountField.getText())))
			isDataEntered = setErrorTxt("Please enter a valid amount");
		else if(billTypeField.getText() == null || billTypeField.getText().trim().isEmpty()) {
			isDataEntered = setErrorTxt("Please enter the type of bill");
		}
		else if(!(Validation.validateChars(billTypeField.getText())))
			isDataEntered = setErrorTxt("Please only enter valid characters");
		else if(dateField.getValue() == null) {
			isDataEntered = setErrorTxt("Please enter a valid date");
		}
		else if(dateField.getValue().isBefore(startDate))
			isDataEntered = setErrorTxt("Sorry, the date you entered is before the starting date.");
		return isDataEntered;
	}
		
	public char translateFlag(int flag) {
		char repeatFlag = 'O';
		if(flag == 1)
			repeatFlag = 'M';
		return repeatFlag;
	}
	
	@Override
	public void processResults() {

		int expenseFlag = 0;
		int repeatMonthlyFlag = expenseBox.getSelectionModel().getSelectedIndex();  //= 5;
		double amount = monthlyData.round(Double.valueOf(amountField.getText().trim()),2);
		String billtype = billTypeField.getText().trim();
		LocalDate dueDate = dateField.getValue();
		char repeatFlag = translateFlag(repeatMonthlyFlag);
		Database.getInstance().insertExpenses(amount, billtype, dueDate, repeatFlag);
//		ExpenseData.getInstance().addExpense(new Expenses(amount,billtype,dueDate, repeatFlag));
		monthlyData.getInstance().updateCurrentMonthData(dueDate, amount, expenseFlag, repeatMonthlyFlag);
		monthlyData.getInstance().updateRemainingMonthData(dueDate, amount, expenseFlag, repeatMonthlyFlag);
		monthlyData.getInstance().setRepeatMultiplier(1);
	}	
}
