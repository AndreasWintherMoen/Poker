package poker;

public class Player
{
	private CardHolder cards;
	
	public Player(Deck deck)
	{
		this.cards = new CardHolder(deck);
		UIManager.instance.displayPlayerCards(this.cards.getCards());
		System.out.println("Player cards: " + this.cards.getCards());
	}
}