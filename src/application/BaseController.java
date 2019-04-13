package application;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class BaseController {
	@FXML
	protected TextField amountField;
	@FXML
	protected DatePicker dateField;
	@FXML
	protected Label errorTxt;

	public TextField getAmountField() {
		return amountField;
	}

	public void setAmountField(TextField amountField) {
		this.amountField = amountField;
	}

	public DatePicker getDateField() {
		return dateField;
	}

	public void setDateField(DatePicker dateField) {
		this.dateField = dateField;
	}

	public Label getErrorTxt() {
		return errorTxt;
	}

	public void setErrorTxt(Label errorTxt) {
		this.errorTxt = errorTxt;
	}

	public boolean setErrorTxt(String text) {
		errorTxt.setTextFill(Paint.valueOf("red"));
		errorTxt.setText(text);
		return false;
	}
	
	//write regular expression checks for non numerical entries
	public boolean validate() {
		return false;
	}
	
	public void processResults() {

	}
	
}
