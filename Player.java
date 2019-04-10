package poker;

public class Player
{
	private CardHolder cards;
	private boolean controlledByUser;
	
	public Player(Deck deck, boolean controlledByUser)
	{
		this.cards = new CardHolder(deck);
		this.controlledByUser = controlledByUser;

		if (controlledByUser)
		{
			UIManager.instance.displayPlayerCards(this.cards.getCards());
		}
		else
		{
			UIManager.instance.displayOpponentCards(this.cards.getCards());
		}
		
		System.out.println(this.toString());
	}
	
	@Override
	public String toString()
	{
		return (controlledByUser ? "Player" : "Opponent") + " cards: " + this.cards.getCards();
	}
}