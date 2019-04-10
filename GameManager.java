package poker;

public class GameManager
{
	Deck deck;
	
	public GameManager()
	{
		System.out.println("Game Manager initialized...");
		deck = new Deck();
		System.out.println("Randomized deck: " + deck);
	}
}