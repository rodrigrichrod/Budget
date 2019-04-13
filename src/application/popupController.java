package application;

import java.sql.Date;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class popupController extends BaseController{
	@FXML
	private DialogPane startPopupPane;
	
	public popupController() {
		super();
	}

	//write regular expression checks for non numerical entries
	@Override
	public boolean validate() {
		boolean isDataValid = true;
		if(amountField.getText() == null || amountField.getText().trim().isEmpty())
			isDataValid = setErrorTxt("You left a field empty");
		else if(!(Validation.validateAmount(amountField.getText())))
			isDataValid = setErrorTxt("Please enter a valid amount");	
		else if(dateField.getValue() == null)
			isDataValid = setErrorTxt("Please enter a valid date");	

		return isDataValid;
	}
	
	@Override
	public void processResults() {
		LocalDate dueDate = dateField.getValue();
		String month = dueDate.getMonth().name().trim();
		double startingAmount = monthlyData.round(Double.valueOf(amountField.getText()),2);
		int monthNum = dueDate.getMonthValue()-1;
		Database.getInstance().insertSavedData(startingAmount, dueDate, monthNum);
//		LocalDate start = LocalDate.of(2019, 01, 01);
//		while(start.isBefore(dueDate)) {
//			Database.getInstance().insertMonthlyData(start, Double.NaN);
//			start = start.plusDays(1);
//		}
		while(dueDate.isBefore(LocalDate.of(2020, 01, 01))) {
			Database.getInstance().insertMonthlyData(dueDate, startingAmount);
			dueDate = dueDate.plusDays(1);
		}
		
//		monthlyData.getInstance().setDate(dueDate);
//		monthlyData.getInstance().setStartingBalance(String.valueOf(monthlyData.round(Double.valueOf(amountField.getText()),2)));
//		monthlyData.getInstance().setPopupMonth(month);
	}
}
