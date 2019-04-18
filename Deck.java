package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poker.Enums.Cards;

public class Deck
{
	
	private List<Card> cards = new ArrayList<Card>();
	
	
	public Deck()
	{
		cards = fillDeck();
		shuffleDeck(cards);
	}
	
	public Card drawCard()
	{
		return cards.remove(cards.size() - 1);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		cards.forEach(c -> sb.append(c.toString() + ", "));
		return sb.toString();
	}
	
	private List<Card> fillDeck()
	{
		cards = new ArrayList<Card>();
		for (Cards c : Cards.values())
		{
			cards.add(Card.enumToCard(c));
		}
		return cards;
	}
	
	private void shuffleDeck(List<Card> cards)
	{
		Collections.shuffle(cards);
	}
	
	
}