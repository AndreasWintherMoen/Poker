package poker;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UIManager
{
	@FXML ImageView background;
	@FXML ImageView opponentFirstCard;
	@FXML ImageView opponentSecondCard;
	@FXML ImageView playerFirstCard;
	@FXML ImageView playerSecondCard;
	
	public static UIManager instance;
	
	
	
	@FXML
	protected void initialize()
	{
		if (instance != null)
		{
			throw new RuntimeException("Tried to initialize more than one UIManager.");
		}
		
		System.out.println("UI Manager initialized...");
		displayTestCards();
		new GameManager();
		
		instance = this;
	}
	
	private void displayTestCards()
	{
		// The background is temporary
		// TODO: Find a better background
		loadImage("src/poker/resources/cards/back_cards-07.png", background); 
		
		// TODO: Find better card images which are 100x140 pixels (at least 10:14 ratio)
		loadImage("src/poker/resources/cards/2C.png", playerFirstCard);
		loadImage("src/poker/resources/cards/10D.png", playerSecondCard);
		loadImage("src/poker/resources/cards/5H.png", opponentFirstCard);
		loadImage("src/poker/resources/cards/KS.png", opponentSecondCard);
	}
	
	private void loadImage(String filepath, ImageView view)
	{
		File file = new File(filepath);
		Image newImg = new Image(file.toURI().toString());
		view.setImage(newImg);
	}
}