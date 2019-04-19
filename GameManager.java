package poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Functions;

import javafx.util.Pair;
import poker.Enums.*;

public class GameManager
{
	public boolean usersTurn;
	private Deck deck;
	private List<Card> tableCards;
	private Player user;
	private Player opponent;
	private AI ai;
	private boolean userIsDealer;
	private Round currentRound;
	private int pot, currentBet;
	private int bigBlind = 10;
	private int smallBlind = 5;
	
	public static GameManager instance;
	
	public GameManager()
	{
		if (instance != null)
		{
			throw new RuntimeException("Tried to initialize more than one UIManager.");
		}
		instance = this;
		
		initialize();
		
		newGame();
	}
	
	private void initialize()
	{
		System.out.println("Creating player");
		user = new Player(true, 1000);
		
		System.out.println("Creating opponent");
		opponent = new Player(false, 1000);
		
		ai = new AI(opponent);
	}

	public void newGame()
	{
		System.out.println("\n***NEW GAME***\n");
		this.deck = null;
		
		System.out.println("Game Manager initialized...");
		deck = new Deck();
		System.out.println("Randomized deck: " + deck);
		
		user.drawNewCards(deck);
		opponent.drawNewCards(deck);
		
		tableCards = new ArrayList<Card>();
		
		userIsDealer = !userIsDealer;
		currentBet = bigBlind;
		pot = 0;
		
		if (userIsDealer)
		{
			opponent.bet(smallBlind);
			user.bet(bigBlind);
			usersTurn = false;
		}
		else
		{
			opponent.bet(bigBlind);
			user.bet(smallBlind);
			usersTurn = true;
		}
		
		currentRound = Round.PREFLOP;
		
		UIManager.instance.updateUI();
		
		callForAiMove();
	}
	
	public int getRaiseAmount()
	{
		if (currentRound == Round.PREFLOP || currentRound == Round.FLOP)
		{
			return this.smallBlind;
		}
		else
		{
			return this.bigBlind;
		}
	}
	
	public int getBigBlind()
	{
		return this.bigBlind;
	}
	
	public int getSmallBlind()
	{
		return this.smallBlind;
	}
	
	public int getPot()
	{
		return this.pot;
	}
	
	public Player getPlayer()
	{
		return user;
	}
	
	public Player getOpponent()
	{
		return opponent;
	}
	
	public List<Card> getTableCards()
	{
		return this.tableCards;
	}
	
	public boolean isDealer(Player player)
	{
		if (player == this.user)
		{
			return userIsDealer;
		}
		else
		{
			return (userIsDealer == false);
		}
	}
	
	public void makeMove(Moves move, Player player)
	{
		try
		{
			switch (move)
			{
			case FOLD:
				fold(player);
				break;
			case CHECK:
				check(player);
				break;
			case CALL:
				call(player);
				break;
			case RAISE:
				raise(player);
				break;
			default:
				throw new IllegalArgumentException("Invalid move enum in GameManager::makeMove");
			}			
		} catch (RuntimeException e)
		{
			e.printStackTrace();
			System.out.println("RuntimeException");
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Unknown exception");
		}
	}
	
	public boolean canFold(Player player)
	{
		return isPlayersTurn(player);
	}
	
	public boolean canCheck(Player player)
	{
		if (isPlayersTurn(player) == false) return false;
		
		if (currentBet == player.getBet()) return true;
		
		return false;
	}
	
	public boolean canCall(Player player)
	{
		if (isPlayersTurn(player) == false) return false;
		if (currentBet > player.getBet() && player.canAfford(currentBet - player.getBet())) return true;
		
		return false;
	}
	
	public boolean canRaise(Player player)
	{
		if (isPlayersTurn(player) == false) return false;
		
		if (player.canAfford(currentBet - player.getBet() + getRaiseAmount())) return true;
		
		return false;
	}
	
	private void fold(Player player) throws RuntimeException
	{
		if (canFold(player) == false) throw new RuntimeException();
		
		Player otherPlayer = getOtherPlayer(player);
		
		gatherBets();
		
		otherPlayer.addMoney(this.pot);
		this.pot = 0;
		
		newGame();
	}
	
	private void check(Player player) throws RuntimeException
	{
		if (canCheck(player) == false) throw new RuntimeException();
		
		if (isDealer(player))
		{
			// The player has checked back, so we go to the next round
			nextRound();
		}
		else
		{
			// This is the first check, so we go to the other player
			nextPlayer();
		}
	}
	
	private void call(Player player) throws RuntimeException
	{
		if (canCall(player) == false) throw new RuntimeException();
		
		player.bet(currentBet - player.getBet());

		if (isDealer(player) == false && currentRound == Round.PREFLOP)
		{
			// The non-dealer has limped pre-flop, so the dealer gets an opportunity to raise
			nextPlayer();
		}
		else
		{
			System.out.println(currentRound);
			// The call is to a voluntary bet, so we go to the next round
			nextRound();
		}
	}
	
	private void raise(Player player) throws RuntimeException
	{
		if (canRaise(player) == false) throw new RuntimeException();
		
		System.out.println("GameManager::raise, " + player.toString());
		int raiseAmount = getRaiseAmount();
		currentBet += raiseAmount;
		player.bet(currentBet - player.getBet());
		nextPlayer();
	}
	
	private List<Card> drawTableCards(Round newRound)
	{
		if (newRound == Round.PREFLOP) return null;

		List<Card> output = new ArrayList();
		
		output.add(deck.drawCard());
		
		if (newRound == Round.FLOP)
		{
			output.add(deck.drawCard());
			output.add(deck.drawCard());
		}
		
		return output;
	}
	
	private Player getOtherPlayer(Player player)
	{
		if (this.user == player)
		{
			return this.opponent;
		}
		else if (this.opponent == player)
		{
			return this.user;
		}
		
		throw new IllegalArgumentException("Could not find player: " + player.toString());
	}

	private void nextPlayer()
	{
		System.out.println("GameManager::nextPlayer");
		usersTurn = !usersTurn;
		UIManager.instance.updateUI();
		callForAiMove();
	}
	
	private void nextRound()
	{
		System.out.println("GameManager::nextRound");
		gatherBets();
		
		currentRound = currentRound.next();
		if (currentRound == null)
		{
			determineWinner().addMoney(pot);
			newGame();
		}
		else
		{
			List<Card> newCards = drawTableCards(currentRound);
			newCards.forEach(x -> tableCards.add(x));
			usersTurn = userIsDealer ? false : true;
			UIManager.instance.updateUI();
			callForAiMove();
		}
	}
	
	private void callForAiMove()
	{
		System.out.println("GameManager::callForAiMove");
		if (usersTurn) return;
		ai.doAction();
	}
	
	private void gatherBets()
	{
		this.pot += user.resetBet();
		this.pot += opponent.resetBet();
		currentBet = 0;
	}
	
	private boolean isPlayersTurn(Player player)
	{
		if (player == user)
		{
			return usersTurn;
		}
		else
		{
			return !usersTurn;
		}
	}
	
	private Player determineWinner()
	{
		System.out.println("*** GameManager::determineWinner ***");
		
		List<Card> availableOpponentCards = Stream
				.concat(Arrays.asList(opponent.getCards()).stream(), tableCards.stream())
				.collect(Collectors.toList());
		
		System.out.println("- Opponent Cards: ");
		availableOpponentCards.stream().forEach(card -> System.out.print(card.toString() + ", "));
		System.out.println("");
		System.out.println("isFlush:\t" + isFlush(availableOpponentCards));
		System.out.println("isStraight:\t" + isStraight(availableOpponentCards));
		System.out.println("isFourOfAKind:\t" + isFourOfAKind(availableOpponentCards));
		System.out.println("isThreeOfAKind:\t" + isThreeOfAKind(availableOpponentCards));
		System.out.println("isPair:\t\t" + isPair(availableOpponentCards));
		
		List<Card> availableUserCards = Stream
				.concat(Arrays.asList(user.getCards()).stream(), tableCards.stream())
				.collect(Collectors.toList());
		
		System.out.println("- User Cards: ");
		availableUserCards.stream().forEach(card -> System.out.print(card.toString() + ", "));
		System.out.println("");
		System.out.println("isFlush:\t" + isFlush(availableUserCards));
		System.out.println("isStraight:\t" + isStraight(availableUserCards));
		System.out.println("isFourOfAKind:\t" + isFourOfAKind(availableUserCards));
		System.out.println("isThreeOfAKind:\t" + isThreeOfAKind(availableUserCards));
		System.out.println("isPair:\t\t" + isPair(availableUserCards));

		return this.user;
	}
	
	// Some of these stream implementations may seem a bit unnecessary. This project is something I've done to
	// practice for my exams and stream is something I need to practice, so I'm overdoing it a bit here. Sorry :)
	
	private boolean isFlush(List<Card> cards)
	{
		return cards.stream()
			.map(card -> card.getSuit())
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
			.values()
			.stream()
			.mapToLong(c -> c)
			.max()
			.getAsLong() >= 5;
	}
	
	private boolean isStraight(List<Card> cards)
	{
		List<Integer> cardValues = cards.stream()
			.map(card -> card.getValue())
			.sorted()
			.distinct()
			.collect(Collectors.toList());
		
		int elementsInARow = 1;
		for (int i = 1; i < cardValues.size(); i++)
		{
			// If ith element is 1 higher than the i-1th element, and special case with ace and king
			if (cardValues.get(i) - cardValues.get(i - 1) == 1)
			{
				elementsInARow++;
				
				if (cardValues.get(i) == 13 && cardValues.get(0) == 1)
				{
					elementsInARow++;
				}
				if (elementsInARow == 5) break;
			}
			else
			{
				elementsInARow = 1;
			}
		}
		return (elementsInARow == 5);
	}
	
	private boolean isPair(List<Card> cards)
	{
		return cards.stream()
				.map(card -> card.getValue())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.values()
				.stream()
				.mapToLong(c -> c)
				.max()
				.getAsLong() == 2;
	}
	
	private boolean isThreeOfAKind(List<Card> cards)
	{
		return cards.stream()
				.map(card -> card.getValue())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.values()
				.stream()
				.mapToLong(c -> c)
				.max()
				.getAsLong() == 3;
	}
	
	private boolean isFourOfAKind(List<Card> cards)
	{
		return cards.stream()
				.map(card -> card.getValue())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.values()
				.stream()
				.mapToLong(c -> c)
				.max()
				.getAsLong() == 4;
	}
}
