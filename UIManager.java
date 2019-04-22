package poker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import poker.Enums.Round;

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
	@FXML ImageView opponentDealerIcon;
	@FXML ImageView playerDealerIcon;
	
	@FXML Text statusMessage;
	@FXML Text totalPot;
	@FXML Text opponentMoney;
	@FXML Text opponentBet;
	@FXML Text playerBet;
	@FXML Text playerMoney;
	
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
		
		instance = this;

		new GameManager();

		displayBackground();
		loadDealerIcons();
		updateUI();
	}
	
	@FXML
	protected void clickRestart()
	{
		GameManager.instance.newGame();
	}
	
	@FXML
	protected void clickFold()
	{
		GameManager.instance.getPlayer().fold();
	}
	
	@FXML
	protected void clickCheck()
	{
		GameManager.instance.getPlayer().check();
	}
	
	@FXML
	protected void clickCall()
	{
		GameManager.instance.getPlayer().call();
	}
	
	@FXML
	protected void clickRaise()
	{
		GameManager.instance.getPlayer().raise();
	}

	public void displayPlayerCards(Card[] cards)
	{
		System.out.println("displayPlayerCards: " + PokerUtil.getFilePath(cards[0]));
		displayCard(cards[0], playerFirstCard);
		displayCard(cards[1], playerSecondCard);
	}
	
	public void displayOpponentCards(Card[] cards)
	{
		displayCard(cards[0], opponentFirstCard);
		displayCard(cards[1], opponentSecondCard);
	}
	
	public void updateUI()
	{
		if (GameManager.instance.usersTurn)
		{
			playerTurn();
		}
		else
		{
			opponentTurn();
		}
		
		displayTableCards();
		updateMoneyDisplay();
		updateDealerIcon();
		toggleButtons();
	}
	
	private void toggleButtons()
	{
		Player user = GameManager.instance.getPlayer();
		foldButton.setDisable(!GameManager.instance.canFold(user));	
		checkButton.setDisable(!GameManager.instance.canCheck(user));
		callButton.setDisable(!GameManager.instance.canCall(user));
		raiseButton.setDisable(!GameManager.instance.canRaise(user));
	}
	
	private void playerTurn()
	{
		statusMessage.setStyle("-fx-fill: #0090e0;");
		statusMessage.setText("Your Turn");
	}
	
	private void opponentTurn()
	{
		statusMessage.setStyle("-fx-fill: #c00033;");
		statusMessage.setText("Opponent's Turn");
	}
	
	private void displayTableCards()
	{
		List<Card> tableCards = GameManager.instance.getTableCards();
		List<ImageView> images = new ArrayList<ImageView>(Arrays.asList(
				tableCard1,tableCard2,tableCard3,tableCard4,tableCard5));
		for (int i = 0; i < 5; i++)
		{
			if (tableCards.size() <= i)
			{
				displayCard(null, images.get(i));
			}
			else
			{
				displayCard(tableCards.get(i), images.get(i));
			}
		}
	}
	
	private void updateDealerIcon()
	{
		if (GameManager.instance.isDealer(GameManager.instance.getPlayer()))
		{
			playerDealerIcon.setVisible(true);
			opponentDealerIcon.setVisible(false);
		}
		else
		{
			System.out.println("Opponent is dealer");
			playerDealerIcon.setVisible(false);
			opponentDealerIcon.setVisible(true);
		}
	}
	
	private void displayTableCards(Round round, List<Card> cards)
	{
		switch (round)
		{
		case PREFLOP:
			displayCard(null, tableCard1);
			displayCard(null, tableCard2);
			displayCard(null, tableCard3);
			displayCard(null, tableCard4);
			displayCard(null, tableCard5);
			break;
		case FLOP:
			displayCard(cards.get(0), tableCard1);
			displayCard(cards.get(1), tableCard2);
			displayCard(cards.get(2), tableCard3);
			break;
		case RIVER:
			displayCard(cards.get(0), tableCard4);
			break;
		case TURN:
			displayCard(cards.get(0), tableCard5);
			break;
		default:
			throw new IllegalArgumentException("Round enum case not implemented");
		}
	}
	
	private void updateMoneyDisplay()
	{
		Player opponent = GameManager.instance.getOpponent();
		opponentMoney.setText("$" + opponent.getMoney());
		int opponentBetInt = opponent.getBet();
		if (opponentBetInt > 0)
		{
			opponentBet.setText("$" + Integer.toString(opponentBetInt));			
		}
		else
		{
			opponentBet.setText("");
		}
		
		Player player = GameManager.instance.getPlayer();
		playerMoney.setText("$" + player.getMoney());
		int playerBetInt = player.getBet();
		if (playerBetInt > 0)
		{
			playerBet.setText("$" + Integer.toString(playerBetInt));			
		}
		else
		{
			playerBet.setText("");
		}
		
		totalPot.setText("$" + GameManager.instance.getPot());
	}
	
	private void displayCard(Card card, ImageView cardDisplay)
	{
		loadImage(PokerUtil.getFilePath(card), cardDisplay);
	}
	
	private void loadDealerIcons()
	{
		loadImage("src/poker/resources/dealer.png", opponentDealerIcon);
		loadImage("src/poker/resources/dealer.png", playerDealerIcon);
	}

	private void displayBackground()
	{
		// The background is temporary
		// TODO: Find a better background
		loadImage("src/poker/resources/TableBackground.jpg", background); 
	}
	
	private void loadImage(String filepath, ImageView view)
	{
		File file = new File(filepath);
		Image newImg = new Image(file.toURI().toString());
		view.setImage(newImg);
	}
}