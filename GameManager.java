package poker;

public class GameManager
{
	private Deck deck;
	private Player player;
	private Player opponent;
	
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
	
	public void newRound()
	{
		this.deck = null;
		this.player = null;
		this.opponent = null;
		
		System.out.println("Game Manager initialized...");
		deck = new Deck();
		System.out.println("Randomized deck: " + deck);
		
		System.out.println("Creating player");
		player = new Player(deck, true);
		
		System.out.println("Creating opponent");
		opponent = new Player(deck, false);
	}
}