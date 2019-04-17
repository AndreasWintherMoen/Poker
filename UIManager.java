package poker;

import java.io.File;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import poker.Enums.Cards;

public class UIManager
{
	@FXML ImageView background;
	@FXML ImageView opponentFirstCard;
	@FXML ImageView opponentSecondCard;
	@FXML ImageView playerFirstCard;
	@FXML ImageView playerSecondCard;
	@FXML ImageView tableCard1;
	@FXML ImageView tableCard2;
	@FXML ImageView tableCard3;
	@FXML ImageView tableCard4;
	@FXML ImageView tableCard5;
	
	@FXML Button restartButton;
	@FXML Button foldButton;
	@FXML Button checkButton;
	@FXML Button callButton;
	@FXML Button raiseButton;
	
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
		displayTableCards();
		
		instance = this;
		
		new GameManager();
	}
	
	public void displayPlayerCards(Card[] cards)
	{
		displayCard(cards[0], playerFirstCard);
		displayCard(cards[1], playerSecondCard);
	}
	
	public void displayOpponentCards(Card[] cards)
	{
		displayCard(cards[0], opponentFirstCard);
		displayCard(cards[1], opponentSecondCard);
	}
	
	public void displayTableCards()
	{
		// Displays empty table cards
		displayCard(null, tableCard1);
		displayCard(null, tableCard2);
		displayCard(null, tableCard3);
		displayCard(null, tableCard4);
		displayCard(null, tableCard5);
	}
	
	@FXML
	protected void clickRestart()
	{
		GameManager.instance.newGame();
	}
	
	@FXML
	protected void clickFold()
	{
		System.out.println("Not implemented");
	}

	@FXML
	protected void clickCheck()
	{
		System.out.println("Not implemented");
	}

	@FXML
	protected void clickCall()
	{
		System.out.println("Not implemented");
	}

	@FXML
	protected void clickRaise()
	{
		System.out.println("Not implemented");
	}
	
	private void displayCard(Card card, ImageView cardDisplay)
	{
		loadImage(PokerUtil.getFilePath(card), cardDisplay);
	}
	
	private void displayTestCards()
	{
		// The background is temporary
		// TODO: Find a better background
		loadImage("src/poker/resources/TableBackground.jpg", background); 
		
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