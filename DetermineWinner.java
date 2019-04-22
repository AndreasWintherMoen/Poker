package poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import javafx.util.Pair;

public class DetermineWinner
{
	public static Player determineWinner(Player firstPlayer, Player secondPlayer, List<Card> tableCards)
	{
		printOutcome(firstPlayer, secondPlayer, tableCards);
		
		List<Card> secondPlayerCards = Stream
				.concat(Arrays.asList(secondPlayer.getCards()).stream(), tableCards.stream())
				.collect(Collectors.toList());
		List<Card> firstPlayerCards = Stream
				.concat(Arrays.asList(firstPlayer.getCards()).stream(), tableCards.stream())
				.collect(Collectors.toList());
		
		Integer[] secondPlayerScore = getPlayerScore(secondPlayerCards);
		Integer[] firstPlayerScore = getPlayerScore(firstPlayerCards);
		
		System.out.println("Opponent score:\t" + Arrays.toString(secondPlayerScore));
		System.out.println("User score:    \t" + Arrays.toString(firstPlayerScore));
		
		for (int i = 0; i < secondPlayerScore.length; i++)
		{
			if (secondPlayerScore[i] > firstPlayerScore[i])
			{
				return secondPlayer;
			}
			else if (firstPlayerScore[i] > secondPlayerScore[i])
			{
				return firstPlayer;
			}
		}
		
		// If we get here it means the players have equal score
		return null;
	}
	
	private static Integer[] getPlayerScore(List<Card> cards)
	{
		// Returns a score of the cards as follows:
		// Royal Flush: 	9, 14, 13, 12, 11, 10
		// Straight Flush: 	8, X			 where X is the value of the highest card of the straight flush
		// Four of a Kind: 	7, X, Y			 where X is the value of the cards in the four of a kind and Y is the high card
		// Full House: 		6, X, Y			 where X is the value of the triplets and Y is the value of the pair
		// Flush:			5, X, X, X, X, X where X are the values of the cards in the flush
		// Straight: 		4, X 			 where X is the value of the highest card in the straight
		// Three of a Kind:	3, X, Y, Y		 where X is the value of the cards of the three of a kind and Y are the two high cards
		// Two pair: 		2, X, Y, Z		 where X is the value of the highest pair, Y the value of the second pair, and Z the high card
		// One pair: 		1, X, Y, Y, Y	 where X is the value of the pair and Y the 3 high cards
		// High card: 		0, X, X, X, X, X where X are the high cards
		
		if (isRoyalFlush(cards))
		{
			return new Integer[] {9, 14, 13, 12, 11, 10};
		}
		
		Pair<Boolean, Integer> straightFlush = isStraightFlush(cards);
		if (straightFlush.getKey())
		{
			return new Integer[] {8, straightFlush.getValue()};
		}
		
		Pair<Boolean, Integer> fourOfAKind = isFourOfAKind(cards);
		if (fourOfAKind.getKey())
		{
			List<Card> remainingCards = cards.stream()
					.filter(c -> c.getValue() != fourOfAKind.getValue())
					.collect(Collectors.toList());
			return new Integer[] {7, fourOfAKind.getValue(), getHighCards(remainingCards).get(0)};
		}
		
		Pair<Boolean, List<Integer>> fullHouse = isFullHouse(cards);
		if (fullHouse.getKey())
		{
			return new Integer[] {6, fullHouse.getValue().get(0), fullHouse.getValue().get(1)};
		}
		
		Pair<Boolean, List<Integer>> flush = isFlush(cards);
		if (flush.getKey())
		{
			List<Integer> output = new ArrayList<Integer>();
			output.add(5);
			flush.getValue().forEach(x -> output.add(x));
			return output.toArray(new Integer[0]);
		}
		
		Pair<Boolean, Integer> straight = isStraight(cards);
		if (straight.getKey())
		{
			return new Integer[] {4, straight.getValue()};
		}
		
		Pair<Boolean, Integer> threeOfAKind = isThreeOfAKind(cards);
		if (threeOfAKind.getKey())
		{
			List<Card> remainingCards = cards.stream()
					.filter(c -> c.getValue() != threeOfAKind.getValue())
					.collect(Collectors.toList());
			List<Integer> highCards = getHighCards(remainingCards);
			return new Integer[] {3, threeOfAKind.getValue(), highCards.get(0), highCards.get(1)};
		}
		
		Pair<Boolean, List<Integer>> twoPair = isTwoPair(cards);
		if (twoPair.getKey())
		{
			List<Card> remainingCards = cards.stream()
				.filter(c -> c.getValue() != twoPair.getValue().get(0) && c.getValue() != twoPair.getValue().get(1))
				.collect(Collectors.toList());
			return new Integer[] {2, twoPair.getValue().get(0), twoPair.getValue().get(1), getHighCards(remainingCards).get(0)};
		}
		
		Pair<Boolean, Integer> pair = isPair(cards);
		if (pair.getKey())
		{
			List<Card> remainingCards = cards.stream()
				.filter(c -> c.getValue() != pair.getValue())
				.collect(Collectors.toList());
			List<Integer> highCards = getHighCards(remainingCards);
			return new Integer[] {1, pair.getValue(), highCards.get(0), highCards.get(1), highCards.get(2)};
		}
		
		List<Integer> highCards = getHighCards(cards);
		return new Integer[] {0, highCards.get(0), highCards.get(1), highCards.get(2), highCards.get(3), highCards.get(4)};
	}
	
	private static void printOutcome(Player firstPlayer, Player secondPlayer, List<Card> tableCards)
	{
		System.out.println("*** GameManager::printOutcome ***");
		
		List<Card> availableOpponentCards = Stream
				.concat(Arrays.asList(secondPlayer.getCards()).stream(), tableCards.stream())
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
				.concat(Arrays.asList(firstPlayer.getCards()).stream(), tableCards.stream())
				.collect(Collectors.toList());
		
		System.out.println("- User Cards: ");
		availableUserCards.stream().forEach(card -> System.out.print(card.toString() + ", "));
		System.out.println("");
		System.out.println("isFlush:\t" + getFlush(availableUserCards));
		System.out.println("isStraight:\t" + getStraight(availableUserCards));
		System.out.println("isFourOfAKind:\t" + getFourOfAKind(availableUserCards));
		System.out.println("isThreeOfAKind:\t" + getThreesOfAKind(availableUserCards));
		System.out.println("isPair:\t\t" + getPairs(availableUserCards));
	}
	
	private static List<Integer> getHighCards(List<Card> cards)
	{
		// Returns the values of the input cards in sorted order from largest to smallest
		return Lists.reverse(cards.stream()
			.map(card -> card.getValue())
			.sorted()
			.collect(Collectors.toList()));
	}
	
	private static boolean isRoyalFlush(List<Card> cards)
	{
		Pair<Boolean, Integer> straightFlush = isStraightFlush(cards);
		return (straightFlush.getKey() && straightFlush.getValue() == 14);
	}
	
	private static Pair<Boolean, Integer> isStraightFlush(List<Card> cards)
	{
		// Determimnes if input cards contains a straight flush, and if true also returns the high card.
		// If royal flush, high card is set to 14. 
		
		Pair<Boolean, List<Card>> flush = getFlush(cards);
		if (flush.getKey() == false)
		{
			return new Pair<Boolean, Integer> (false, null);
		}
		
		Pair<Boolean, Integer> straightFlush = isStraight(flush.getValue());
		if (straightFlush.getKey() == false)
		{
			return new Pair<Boolean, Integer> (false, null);
		}
		return straightFlush;
	}
	
	private static Pair<Boolean, Integer> isFourOfAKind(List<Card> cards)
	{
		Pair<Boolean, List<Card>> fourOfAKindCards = getFourOfAKind(cards);
		if (fourOfAKindCards.getKey() == false)
		{
			return new Pair<Boolean, Integer> (false, null);
		}
		return new Pair<Boolean, Integer> (true, fourOfAKindCards.getValue().get(0).getValue());
	}
	
	private static Pair<Boolean, List<Integer>> isFullHouse(List<Card> cards)
	{
		// Returns true if full house, and a list of card values. 
		// The first is the value of triplets and second is value of pair
		Pair<Boolean, List<List<Card>>> threeOfAKind = getThreesOfAKind(cards);
		if (threeOfAKind.getKey() == false)
		{
			return new Pair<Boolean, List<Integer>> (false, null);
		}
		if (threeOfAKind.getValue().size() == 2)
		{
			// We have 2 threes of a kind, which makes a full house.
			// We must therefore return a list with the highest first and then the other
			Integer firstActualThreeOfAKind = threeOfAKind.getValue().get(0).get(0).getValue();
			Integer secondActualThreeOfAKind = threeOfAKind.getValue().get(1).get(0).getValue();
			List<Integer> output = new ArrayList<Integer>();
			output.add(Integer.max(firstActualThreeOfAKind, secondActualThreeOfAKind));
			output.add(Integer.min(firstActualThreeOfAKind, secondActualThreeOfAKind));
			return new Pair<Boolean, List<Integer>> (true, output);
		}
		// If we get here it means we have one three of a kind, so we must check if we also have a pair
		Pair<Boolean, List<List<Card>>> pairs = getPairs(cards);
		if (pairs.getKey() == false)
		{
			return new Pair<Boolean, List<Integer>> (false, null);
		}
		if (pairs.getValue().size() > 1)
		{
			// We have 2 pairs in addition to the three of a kind
			Integer highestPair = pairs.getValue().stream()
				.mapToInt(cardList -> cardList.get(0)
				.getValue())
				.max()
				.getAsInt();
			
			List<Integer> output = new ArrayList<Integer>();
			output.add(threeOfAKind.getValue().get(0).get(0).getValue());
			output.add(highestPair);
			
			return new Pair<Boolean, List<Integer>> (true, output);
		}
		
		// We only have 1 pair, so we return it and the three of a kind
		List<Integer> output = new ArrayList<Integer>();
		output.add(threeOfAKind.getValue().get(0).get(0).getValue());
		output.add(pairs.getValue().get(0).get(0).getValue());
		
		return new Pair<Boolean, List<Integer>> (true, output);
	}
	
	private static Pair<Boolean, List<Integer>> isFlush(List<Card> cards)
	{
		// Returns true if is flush, and the card values (in case both players have a flush)
		Pair<Boolean, List<Card>> flush = getFlush(cards);
		if (flush.getKey() == false)
		{
			return new Pair<Boolean, List<Integer>> (false, null);
		}
		return new Pair<Boolean, List<Integer>> 
			(true, Lists.reverse(flush.getValue().stream().map(Card::getValue).sorted().collect(Collectors.toList())));
	}
	
	private static Pair<Boolean, Integer> isStraight(List<Card> cards)
	{
		Pair<Boolean, List<Card>> straightCards = getStraight(cards);
		System.out.println(straightCards.getValue());
		if (straightCards.getKey() == false)
		{
			return new Pair<Boolean, Integer> (false, null);
		}
		if (straightCards.getValue().get(0).getValue() == 5 && 
				straightCards.getValue().get(straightCards.getValue().size() - 1).getValue() == 14)
		{
			// If there is a 5 and an ace, we have a A-5 straight (a wheel), so we return 5 as highest value
			return new Pair<Boolean, Integer> (true, 5);
		}
		return new Pair<Boolean, Integer> (true, straightCards.getValue().get(0).getValue());
	}
	
	private static Pair<Boolean, Integer> isThreeOfAKind(List<Card> cards)
	{
		Pair<Boolean, List<List<Card>>> threeOfAKindCards = getThreesOfAKind(cards);
		if (threeOfAKindCards.getKey() == false)
		{
			return new Pair<Boolean, Integer> (false, null);
		}
		if (threeOfAKindCards.getValue().size() == 1)
		{
			// Only one instance of three of a kind
			List<Card> actualCards = threeOfAKindCards.getValue().get(0);
			return new Pair<Boolean, Integer> (true, actualCards.get(actualCards.size() - 1).getValue());
		}
		List<Card> firstActualCards = threeOfAKindCards.getValue().get(0);
		List<Card> secondActualCards = threeOfAKindCards.getValue().get(0);
		if (firstActualCards.get(0).getValue() > secondActualCards.get(0).getValue())
		{
			return new Pair<Boolean, Integer> (true, firstActualCards.get(0).getValue());
		}
		else
		{
			return new Pair<Boolean, Integer> (true, secondActualCards.get(0).getValue());
		}
	}
	
	private static Pair<Boolean, List<Integer>> isTwoPair(List<Card> cards)
	{
		Pair<Boolean, List<List<Card>>> pairs = getPairs(cards);
		if (pairs.getKey() == false || pairs.getValue().size() < 2)
		{
			return new Pair<Boolean, List<Integer>> (false, null);
		}
		
		if (pairs.getValue().size() > 2)
		{
			// We have 3 pairs, so we have to find the 2 highest ones
			List<Integer> values = new ArrayList<Integer>();
			for (List<Card> pair : pairs.getValue())
			{
				values.add(pair.get(0).getValue());
			}
			Collections.sort(values);
			values.remove(0);
			values = Lists.reverse(values);
			return new Pair<Boolean, List<Integer>> (true, values);
		}
		
		// We have two pairs, so we return those
		List<Integer> values = Lists.reverse(pairs.getValue().stream()
				.map(pairList -> pairList.get(0).getValue())
				.sorted()
				.collect(Collectors.toList()));
				
		return new Pair<Boolean, List<Integer>> (true, values);
	}
	
	private static Pair<Boolean, Integer> isPair(List<Card> cards)
	{
		Pair<Boolean, List<List<Card>>> pair = getPairs(cards);
		if (pair.getKey() == false || pair.getValue().size() != 1)
		{
			return new Pair<Boolean, Integer> (false, null);
		}
		
		return new Pair<Boolean, Integer> (true, pair.getValue().get(0).get(0).getValue());
	}
	
	// Some of these stream implementations may seem a bit unnecessary. This project is something I've done to
	// practice for my exams and stream is something I need to practice, so I'm overdoing it a bit here. Sorry :)

	private static Pair<Boolean, List<Card>> getFlush(List<Card> cards)
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
				List<Card> flushCards = Lists.reverse(cards.stream()
						.filter(card -> card.getSuit() == flushSuit)
						.sorted()
						.collect(Collectors.toList()));
				return new Pair<Boolean, List<Card>> (true, flushCards);
			}
		}
		
		// We never get here, but Eclipse didn't let me compile without a definitive return statement
		return new Pair<Boolean, List<Card>> (false, null);
	}
	
	private static Pair<Boolean, List<Card>> getStraight(List<Card> cards)
	{
		cards = Lists.reverse(cards.stream()
			.sorted()
			.distinct()
			.collect(Collectors.toList()));
		
		List<Card> straightCards = new ArrayList<Card>();
		
		int elementsInARow = 1;
		for (int i = 1; i < cards.size(); i++)
		{
			if (cards.get(i - 1).getValue() - cards.get(i).getValue() == 1)
			{
				elementsInARow++;
				straightCards.add(cards.get(i - 1));
				straightCards.add(cards.get(i));

				if (elementsInARow == 5)
				{
					return new Pair<Boolean, List<Card>> (true, straightCards.stream().distinct().collect(Collectors.toList()));
				}
				
				if (elementsInARow == 4 && cards.get(i).getValue() == 2 && cards.get(0).getValue() == 14)
				{
					elementsInARow++;
					straightCards.add(cards.get(cards.size() - 1));
					return new Pair<Boolean, List<Card>> (true, straightCards.stream().distinct().collect(Collectors.toList()));
				}
			}
			else
			{
				elementsInARow = 1;
				straightCards = new ArrayList<Card>();
			}
		}
		return new Pair<Boolean, List<Card>> (false, null);
	}
	
	private static Pair<Boolean, List<List<Card>>> getPairs(List<Card> cards)
	{
		Map<Integer, Long> groupedCards = cards.stream()
				.collect(Collectors.groupingBy(Card::getValue, Collectors.counting()));
		
		if (groupedCards.values().stream()
			.mapToLong(c -> c)
			.noneMatch (x -> x == 2))
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
	
	private static Pair<Boolean, List<List<Card>>> getThreesOfAKind(List<Card> cards)
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
	
	private static Pair<Boolean, List<Card>> getFourOfAKind(List<Card> cards)
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