package poker;

import java.util.ArrayList;
import java.util.List;

public class CardHolder
{
	private List<Card> cards = new ArrayList<Card>();
	
	public CardHolder(Deck deck)
	{
		drawCard(deck);
		drawCard(deck);
	}
	
	private void drawCard(Deck deck)
	{
		cards.add(deck.drawCard());
	}

	public List<Card> getCards()
	{
		return this.cards;
	}
	
}