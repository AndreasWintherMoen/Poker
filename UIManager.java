package poker;

import java.io.File;
import java.util.List;

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
	
	
	
	public void displayPlayerCards(List<Card> cards)
	{
		loadImage(PokerUtil.getFilePath(cards.get(0)), playerFirstCard);
		loadImage(PokerUtil.getFilePath(cards.get(1)), playerSecondCard);
	}
	
	public void displayOpponentCards(List<Card> cards)
	{
		loadImage(PokerUtil.getFilePath(cards.get(0)), opponentFirstCard);
		loadImage(PokerUtil.getFilePath(cards.get(1)), opponentSecondCard);
	}
	
	@FXML
	protected void initialize()
	{
		if (instance != null)
		{
			throw new RuntimeException("Tried to initialize more than one UIManager.");
		}
		
		System.out.println("UI Manager initialized...");
		displayTestCards();
		
		instance = this;

		new GameManager();
	}
	
	private void displayTestCards()
	{
		// The background is temporary
		// TODO: Find a better background
		loadImage("src/poker/resources/cards/back_cards-07.png", background); 
		
		// TODO: Find better card images which are 100x140 pixels (at least 10:14 ratio)
		loadImage(PokerUtil.getFilePath(Cards.C1), playerFirstCard);
		loadImage(PokerUtil.getFilePath(Cards.D5), playerSecondCard);
		loadImage(PokerUtil.getFilePath(Cards.H10), opponentFirstCard);
		loadImage(PokerUtil.getFilePath(Cards.S13), opponentSecondCard);
	}
	
	private void loadImage(String filepath, ImageView view)
	{
		File file = new File(filepath);
		Image newImg = new Image(file.toURI().toString());
		view.setImage(newImg);
	}
}