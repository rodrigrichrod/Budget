package application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.*;

public class Controller {

	@FXML
	private GridPane gridPane;
	@FXML
	private ChoiceBox<String> choiceBox;
	@FXML
	private Button addExpenseBtn;
	@FXML
	private Button addIncomeBtn;
	@FXML
	private Button newBtn;
	@FXML
	private BorderPane mainBorderPane;
	
	public Controller() {
		
	}
	
	@FXML
	public void initialize() {
		//create choice box for months
		choiceBox.setItems(FXCollections.observableArrayList("January", "February", "March", "April", 
						"May", "June", "July", "August", "September", "October", "November", "December"));
		
		
		//only need the initial popup if it is the first time user starts program
		if(!(monthlyData.getInstance().getMonthlyData().isEmpty())) {
			addIncomeBtn.setDisable(false);
			addExpenseBtn.setDisable(false);
			newBtn.setDisable(true);
			monthlyData.getInstance().setFirstMonthStarted(true);
			monthlyData.getInstance().setMonthlyDataPopulated(true);
			//select the last month visited
			int finalMonth = Database.getInstance().getLastMonthVisited();
			choiceBox.getSelectionModel().select(finalMonth);
			
		}
		
		//create grid pane with labels
		createGridPane();		
	}
	
	@FXML
	public void createGridPane() {
		//initialize local variables
		int rowIndex = 0;
		int columnIndex = 0;
		int dayInMonth = 1;
		boolean startMonth = false;		
		int monthValue = choiceBox.getSelectionModel().getSelectedIndex()+1;
		int dayOfWeek = Validation.findDayOfWeek(monthValue);
		int numDaysMonth = Validation.findNumDaysInMonth(monthValue);
		String selectedMonth = choiceBox.getSelectionModel().getSelectedItem();
		//when loading the program again
//		if(monthlyData.getInstance().isappStarted())
//			selectedMonth = monthlyData.getInstance().getMonths().get(monthlyData.getInstance().getFinalMonth()+1);
		String [] monthlyAmounts = new String [numDaysMonth];
//		if(monthlyData.getInstance().isMonthlyDataPopulated()) {
		String [] setMonthlyAmountText = monthlyData.getInstance().getMonthlyData().get(selectedMonth.toUpperCase());
//		}
//		System.out.println(setMonthlyAmountText);
				
		//creating the grid pane in the while loop
		while(columnIndex<7 && rowIndex<6) {
			Pane pane = new Pane();
			pane.getStyleClass().add("pane");
			pane.setPrefHeight(100.0);
			pane.setPrefWidth(100.0);
			
			//print the days
			//first part of condition is for first day of month   second part of condition is to number days in month
			if((dayOfWeek) == columnIndex && rowIndex==0 || (dayInMonth <= numDaysMonth && startMonth)) {
				//numbered days
				Label day = new Label("" + dayInMonth);
				day.setLayoutX(2);
				day.setLayoutY(2);
				pane.getChildren().add(day);
				
				//total amounts for each day
				Label label = new Label();
				label.setLayoutX(25);
				label.setLayoutY(40);
			
					//loading the months data; based on the selection in the choice box
					//this is only for the starting balanced month after the user first
					//uses the startup popup display when creating a new budget
					//the following is for the month with the starting balance
					LocalDate startingDate = Database.getInstance().getStartingDate();
					String popupMonth = Validation.convertDateToMonth(startingDate);
					if(newBtn.isDisabled() && !(monthlyData.getInstance().isFirstMonthStarted()) && popupMonth.equals(selectedMonth.toUpperCase())) {
						LocalDate startDate = Database.getInstance().getStartingDate();
						int daySelected = startDate.getDayOfMonth();
	
						//store the balance reported in the startup popup
						if(daySelected == dayInMonth) {
							String startingBalance = String.valueOf(Database.getInstance().getStartingAmount());
							String beginningBalance = startingBalance;//monthlyData.getInstance().getStartingBalance();
	//						System.out.println("fuck you " + beginningBalance.toString());
							label.setText(beginningBalance);
							monthlyAmounts[dayInMonth-1] = beginningBalance;
						}
						//retrieve previous day's balance
						else if(dayInMonth >daySelected) {
							monthlyAmounts[dayInMonth-1] = monthlyAmounts[dayInMonth-2];
							label.setText(monthlyAmounts[dayInMonth-1]);
						}
	//					don't want any null values, so set days before the starting balance date to empty string
						else if(daySelected > dayInMonth){
							monthlyAmounts[dayInMonth-1] = " ";
							label.setText(" ");
						}
						if(dayInMonth == numDaysMonth)
						{
							monthlyData.getInstance().setFirstMonthStarted(true);
						}
					}
					
				if(setMonthlyAmountText!=null) {
					//find selected month in hash map to retrieve all values and store in string []
					//set loop to set text for each day
					if(monthlyData.getInstance().isMonthlyDataPopulated()) {
	//					String [] setMonthlyAmountText = monthlyData.getInstance().getMonthlyData().get(selectedMonth.toUpperCase());
						label.setText(setMonthlyAmountText[dayInMonth-1]);
					}					
				}					
				pane.getChildren().add(label);
				gridPane.add(pane, columnIndex, rowIndex);	
				
				//increment days and set boolean value so remaining days will get populated
				dayInMonth++;
				startMonth = true;
			}								
			columnIndex++;
			
			//reached the end of the columns, so start over at the next row
			if(columnIndex==7) {
				rowIndex++;
				columnIndex = 0;
			}
			
			
			//reached the end of the grid
			if(rowIndex==6) {
				break;
			}
		}
		//this is a one time occurrence once the first month is populated
		//all remaining months will get saved into monthly instance
		if(monthlyData.getInstance().isFirstMonthStarted() && !(monthlyData.getInstance().isMonthlyDataPopulated())) {
			//after grid is created, save all the data for the current month
			monthlyData.getInstance().addMonthData(selectedMonth.toUpperCase(), monthlyAmounts);
			monthlyData.getInstance().setFinalStartingMonthBalance(monthlyAmounts[numDaysMonth-1]);
			saveAllMonthsData(monthValue);
//			monthlyData.getInstance().setFirstMonthStarted(true);
			monthlyData.getInstance().setMonthlyDataPopulated(true);
		}	
		//saving the last month the user sees on the screen
		int lastMonthVisited = choiceBox.getSelectionModel().getSelectedIndex();
		Database.getInstance().updateLastMonth(lastMonthVisited);
//		monthlyData.getInstance().setFinalMonth();
		
	}
	
	public void saveAllMonthsData(int previousMonth) {		
		//get all the data for the rest of the months given the current month
		while(previousMonth<12) {
			int currentMonth = previousMonth+1;
			String [] currentMonthAmounts = new String[Validation.findNumDaysInMonth(currentMonth)];
			Arrays.fill(currentMonthAmounts, monthlyData.getInstance().getFinalStartingMonthBalance());
			monthlyData.getInstance().addMonthData(Validation.getMonth(currentMonth), currentMonthAmounts);
			previousMonth++;				
		}		
	}
	
	//event handling for choice box when month is changed: reload grid with month selected
	@FXML
	private void folderByCategory(ActionEvent event) {
		//save the grid lines
//		Node node = gridPane.getChildren().get(0);
		//clear the grid
		gridPane.getChildren().clear();
		//add the grid lines back after clearing the grid; otherwise, no lines will be visible
//		gridPane.getChildren().add(0,node);
		//recreate the calendar grid
		createGridPane();
	}

////	//1 - income 2 - expense 3 - popup
//	public void setupDialog(String fxml, BaseController controller) {
//		ArrayList<BaseController> controllers = new ArrayList<BaseController>();
//		ExpenseController eController = null;
//		controllers.add(eController);
//		Dialog<ButtonType> dialog = new Dialog<>();
//		dialog.initOwner(mainBorderPane.getScene().getWindow());
//		FXMLLoader fxmlloader = new FXMLLoader();
//		fxmlloader.setLocation(getClass().getResource(fxml));
//		try {
//			dialog.getDialogPane().setContent(fxmlloader.load());
//		} catch(IOException e) {
//			System.out.println("Unable to load expense dialog");
//			e.printStackTrace();
//			return;
//		}
//
////		final BaseController control = controller;
//		controller = fxmlloader.getController();
//		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
//		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//		final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
//		 btOk.addEventFilter(ActionEvent.ACTION, event -> {
//		     if (!(controller.validate())) {
//		         event.consume();
//		     }
//		 });
//		
//		Optional<ButtonType> result = dialog.showAndWait();
//		
//		if(result.isPresent() && result.get() == ButtonType.OK) {
//
//			controller.processResults();
//			//save the grid lines
//			//clear the grid
//			gridPane.getChildren().clear();
//			//add the grid lines back after clearing the grid; otherwise, no lines will be visible
//			//recreate the calendar grid
//			createGridPane();
//			System.out.println("OK button pressed");
//		}
//		else
//			System.out.println("Cancel button pressed");	
//	}
	
	@FXML
	public void displayNewExpenseDialog() {
		
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		FXMLLoader fxmlloader = new FXMLLoader();
		fxmlloader.setLocation(getClass().getResource("ExpenseDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlloader.load());
		} catch(IOException e) {
			System.out.println("Unable to load expense dialog");
			e.printStackTrace();
			return;
		}
		ExpenseController eController = fxmlloader.getController();
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		 btOk.addEventFilter(ActionEvent.ACTION, event -> {
		     if (!(eController.validate())) {
		         event.consume();
		     }
		 });
		
		Optional<ButtonType> result = dialog.showAndWait();
		
		if(result.isPresent() && result.get() == ButtonType.OK) {
			eController.processResults();
			//save the grid lines
			//clear the grid
			gridPane.getChildren().clear();
			//add the grid lines back after clearing the grid; otherwise, no lines will be visible
			//recreate the calendar grid
			createGridPane();
			System.out.println("OK button pressed");
		}
		else
			System.out.println("Cancel button pressed");
	}
	
	@FXML
	public void displayNewIncomeDialog() {
		
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		FXMLLoader fxmlloader = new FXMLLoader();
		fxmlloader.setLocation(getClass().getResource("incomeDialog.fxml"));
		try {

			dialog.getDialogPane().setContent(fxmlloader.load());
		} catch(IOException e) {
			System.out.println("Unable to load income dialog");
			e.printStackTrace();
			return;
		}
		
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		IncomeController iController = fxmlloader.getController();
		final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		 btOk.addEventFilter(ActionEvent.ACTION, event -> {
		     if (!(iController.validate())) {
		         event.consume();
		     }
		 });
		Optional<ButtonType> result = dialog.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			iController.processResults();
			
			//save the grid lines
			//clear the grid
			gridPane.getChildren().clear();
			//add the grid lines back after clearing the grid; otherwise, no lines will be visible
			//recreate the calendar grid
			createGridPane();
//			System.out.println("OK button pressed");
		}
		else
			System.out.println("Cancel button pressed");
	}
	
	@FXML
	public void displayPopupDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		FXMLLoader fxmlloader = new FXMLLoader();
		fxmlloader.setLocation(getClass().getResource("startup.fxml"));
		try {

			dialog.getDialogPane().setContent(fxmlloader.load());
		} catch(IOException e) {
			System.out.println("Unable to load dialog");
			e.printStackTrace();
			return;
		}
		popupController controller = fxmlloader.getController();
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		
		final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		 btOk.addEventFilter(ActionEvent.ACTION, event -> {
		     if (!(controller.validate())) {
		         event.consume();
		     }
		 });
		
		
		Optional<ButtonType> result = dialog.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			
			controller.processResults();
			addIncomeBtn.setDisable(false);
			addExpenseBtn.setDisable(false);
			newBtn.setDisable(true);
			
			
//			choiceBox.getSelectionModel().select(monthlyData.getInstance().getDate().getMonthValue()-1);
			
			LocalDate startingDate = Database.getInstance().getStartingDate();			
			choiceBox.getSelectionModel().select(startingDate.getMonthValue()-1);
			if(startingDate==LocalDate.MIN)
				System.out.println("Error accessing Local Date from DB");
			
			//save the grid lines
			//clear the grid
			gridPane.getChildren().clear();
			//add the grid lines back after clearing the grid; otherwise, no lines will be visible
//			gridPane.getChildren().add(0,node);
			//recreate the calendar grid
			createGridPane();
//			System.out.println("OK button pressed");
		}
		else
			System.out.println("Cancel button pressed");
	}
}
