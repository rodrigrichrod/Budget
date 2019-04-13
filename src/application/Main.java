package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
			Scene scene = new Scene(root,1000,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop(){
//		try {
			
			//only store the initial date once
//			if(monthlyData.getInstance().isTxtFileEmpty("initialDate.txt"))
//			monthlyData.getInstance().storeStartingDate();
//			ExpenseData.getInstance().storeExpense();
//			IncomeData.getInstance().storeIncome();	
//			monthlyData.getInstance().storeMonthlyAmounts();
			//update all the amounts into the DB
			monthlyData.getInstance().updateAmountsToDB();
			Database.getInstance().close();
//		} catch(IOException e) {
//			System.out.println(e.getMessage());
//		}		
	}
	
	@Override
	public void init(){
//		try {
//			Database db = new Database();
			//only load the months when there is data in the file
			
			if(!(Database.getInstance().open())) {
				System.out.println("FATAL ERROR: Couldn't connect to DB!");
				Platform.exit();
			}
			
			if(Database.getInstance().getStartingAmount()!=-1) {
				monthlyData.getInstance().retrieveMonthlyAmounts();
			}
//			if(!(monthlyData.getInstance().isTxtFileEmpty("monthData.txt"))) {
//				monthlyData.getInstance().loadMonthlyAmounts();				
//			}
//			if(!(monthlyData.getInstance().isTxtFileEmpty("ExpenseItems.txt")))
//				ExpenseData.getInstance().loadExpense();
//			if(!(monthlyData.getInstance().isTxtFileEmpty("IncomeItems.txt")))
//				IncomeData.getInstance().loadIncome();
//			if(!(monthlyData.getInstance().isTxtFileEmpty("initialDate.txt"))) {
//				monthlyData.getInstance().loadStartingDate();
//			}
				
//		} catch(IOException e) {
//			System.out.println(e.getMessage());
//		}
	}
}
