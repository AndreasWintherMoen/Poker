package poker;

public class Player
{
	private CardHolder cards;
	
	public Player(Deck deck)
	{
		this.cards = new CardHolder(deck);
	}
}