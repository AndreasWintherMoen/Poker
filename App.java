package poker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application{

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("Poker");
		primaryStage.setScene(new Scene(FXMLLoader.load(App.class.getResource("Poker.fxml"))));
		primaryStage.show();
		
		// Prevent fullscreen
		primaryStage.setResizable(false);
	}

	public static void main(final String[] args) {
		App.launch(args);
	}
}