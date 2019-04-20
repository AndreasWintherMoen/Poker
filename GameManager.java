package poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
		System.out.println("isFlush:\t" + getFlush(availableOpponentCards));
		System.out.println("isStraight:\t" + getStraight(availableOpponentCards));
		System.out.println("isFourOfAKind:\t" + getFourOfAKind(availableOpponentCards));
		System.out.println("isThreeOfAKind:\t" + getThreesOfAKind(availableOpponentCards));
		System.out.println("isPair:\t\t" + getPairs(availableOpponentCards));
		
		List<Card> availableUserCards = Stream
				.concat(Arrays.asList(user.getCards()).stream(), tableCards.stream())
				.collect(Collectors.toList());
		
		System.out.println("- User Cards: ");
		availableUserCards.stream().forEach(card -> System.out.print(card.toString() + ", "));
		System.out.println("");
		System.out.println("isFlush:\t" + getFlush(availableUserCards));
		System.out.println("isStraight:\t" + getStraight(availableUserCards));
		System.out.println("isFourOfAKind:\t" + getFourOfAKind(availableUserCards));
		System.out.println("isThreeOfAKind:\t" + getThreesOfAKind(availableUserCards));
		System.out.println("isPair:\t\t" + getPairs(availableUserCards));

		return this.user;
	}
	
	// Some of these stream implementations may seem a bit unnecessary. This project is something I've done to
	// practice for my exams and stream is something I need to practice, so I'm overdoing it a bit here. Sorry :)

	private Pair<Boolean, List<Card>> getFlush(List<Card> cards)
	{
		// Group the cards by their suit
		Map<Character, Long> groupedCards = cards.stream()
				.collect(Collectors.groupingBy(Card::getSuit, Collectors.counting()));

		// If the max number of a suit is less than 5, we don't have a flush
		if (groupedCards.values().stream().mapToLong(c -> c).max().getAsLong() < 5)
		{
			return new Pair<Boolean, List<Card>> (false, null);
		}
		
		// If we get here it means there is a flush, so we must find the correct suit and return the flush cards
		for (Map.Entry<Character, Long> suitGroup : groupedCards.entrySet())
		{
			if (suitGroup.getValue() >= 5)
			{
				final char flushSuit = suitGroup.getKey();
				List<Card> flushCards = cards.stream()
						.filter(card -> card.getSuit() == flushSuit)
						.collect(Collectors.toList());
				return new Pair<Boolean, List<Card>> (true, flushCards);
			}
		}
		
		// We never get here, but Eclipse didn't let me compile without a definitive return statement
		return new Pair<Boolean, List<Card>> (false, null);
	}
	
	private Pair<Boolean, List<Card>> getStraight(List<Card> cards)
	{
		cards = cards.stream()
			.sorted()
			.distinct()
			.collect(Collectors.toList());
		
		List<Card> straightCards = new ArrayList<Card>();
		
		int elementsInARow = 1;
		for (int i = 1; i < cards.size(); i++)
		{
			// If ith element is 1 higher than the i-1th element, and special case with ace and king
			if (cards.get(i).getValue() - cards.get(i - 1).getValue() == 1)
			{
				elementsInARow++;
				straightCards.add(cards.get(i - 1));
				straightCards.add(cards.get(i));
				
				if (cards.get(i).getValue() == 13 && cards.get(0).getValue() == 1)
				{
					elementsInARow++;
					straightCards.add(cards.get(0));
					
					// This is the last element in the list, so we can return if we have at least 5 elementsInARow
					if (elementsInARow >= 5)
					{
						return new Pair<Boolean, List<Card>> (true, straightCards);
					}
				}
			}
			else if (elementsInARow >= 5)
			{
				return new Pair<Boolean, List<Card>> (true, straightCards);
			}
			else
			{
				elementsInARow = 1;
			}
		}
		return new Pair<Boolean, List<Card>> (false, null);
	}
	
	private Pair<Boolean, List<List<Card>>> getPairs(List<Card> cards)
	{
		Map<Integer, Long> groupedCards = cards.stream()
				.collect(Collectors.groupingBy(Card::getValue, Collectors.counting()));
		
		if (groupedCards.values().stream()
			.mapToLong(c -> c)
			.max()
			.getAsLong() != 2)
		{
			return new Pair<Boolean, List<List<Card>>> (false, null);
		}
		
		// If we get here it means there is a pair, so we must find the correct pairs and return the pair cards
		List<List<Card>> pairList = new ArrayList<List<Card>>();
		for (Map.Entry<Integer, Long> pairGroup : groupedCards.entrySet())
		{
			if (pairGroup.getValue() == 2)
			{
				final int pairValue = pairGroup.getKey();
				List<Card> pairCards = cards.stream()
						.filter(card -> card.getValue() == pairValue)
						.collect(Collectors.toList());
				pairList.add(pairCards);
			}
		}
		return new Pair<Boolean, List<List<Card>>> (true, pairList);
	}
	
	private Pair<Boolean, List<List<Card>>> getThreesOfAKind(List<Card> cards)
	{
		Map<Integer, Long> groupedCards = cards.stream()
				.collect(Collectors.groupingBy(Card::getValue, Collectors.counting()));
		
		if (groupedCards.values().stream()
			.mapToLong(c -> c)
			.max()
			.getAsLong() != 3)
		{
			return new Pair<Boolean, List<List<Card>>> (false, null);
		}
		
		// If we get here it means there is a pair, so we must find the correct pairs and return the pair cards
		List<List<Card>> pairList = new ArrayList<List<Card>>();
		for (Map.Entry<Integer, Long> pairGroup : groupedCards.entrySet())
		{
			if (pairGroup.getValue() == 3)
			{
				final int pairValue = pairGroup.getKey();
				List<Card> pairCards = cards.stream()
						.filter(card -> card.getValue() == pairValue)
						.collect(Collectors.toList());
				pairList.add(pairCards);
			}
		}
		return new Pair<Boolean, List<List<Card>>> (true, pairList);
	}
	
	private Pair<Boolean, List<Card>> getFourOfAKind(List<Card> cards)
	{
		Map<Integer, Long> groupedCards = cards.stream()
				.collect(Collectors.groupingBy(Card::getValue, Collectors.counting()));
		
		if (groupedCards.values().stream()
			.mapToLong(c -> c)
			.max()
			.getAsLong() != 4)
		{
			return new Pair<Boolean, List<Card>> (false, null);
		}
		
		// If we get here it means there is a pair, so we must find the correct pairs and return the pair cards
		for (Map.Entry<Integer, Long> pairGroup : groupedCards.entrySet())
		{
			if (pairGroup.getValue() == 4)
			{
				final int pairValue = pairGroup.getKey();
				List<Card> pairCards = cards.stream()
						.filter(card -> card.getValue() == pairValue)
						.collect(Collectors.toList());
				return new Pair<Boolean, List<Card>> (true, pairCards);
			}
		}
		
		// We never get here, but Eclipse doesn't compile unless it has a definitive return statement
		return new Pair<Boolean, List<Card>> (false, null);
	}
}
