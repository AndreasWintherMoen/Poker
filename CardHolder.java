package poker;

import java.util.ArrayList;
import java.util.List;

public class CardHolder
{
//	private List<Card> cards = new ArrayList<Card>();
	private Card[] cards;
	
	public CardHolder(Deck deck)
	{
		cards = new Card[2];
		cards[0] = drawCard(deck);
		cards[1] = drawCard(deck);
	}
	
	private Card drawCard(Deck deck)
	{
		return deck.drawCard();
	}

	public Card[] getCards()
	{
		return this.cards;
	}
	
}