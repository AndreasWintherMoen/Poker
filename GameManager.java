package poker;

public class GameManager
{
	private Deck deck;
	private Player player;
	private Player opponent;
	
	public static GameManager instance;
	
	public GameManager()
	{
		if (instance != null)
		{
			throw new RuntimeException("Tried to initialize more than one UIManager.");
		}
		instance = this;
		
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