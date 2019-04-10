package poker;

public class GameManager
{
	Deck deck;
	Player player;
	Player opponent;
	
	public GameManager()
	{
		System.out.println("Game Manager initialized...");
		deck = new Deck();
		System.out.println("Randomized deck: " + deck);
		
		System.out.println("Creating player");
		player = new Player(deck, true);
		
		System.out.println("Creating opponent");
		opponent = new Player(deck, false);
	}
}