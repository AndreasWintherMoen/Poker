package poker;

import poker.Enums.Moves;

public class Player
{
	private CardHolder cards;
	private boolean controlledByUser;
	private int money, bet;
	
	public Player(boolean controlledByUser, int money)
	{
		this.controlledByUser = controlledByUser;
		this.money = money;
	}
	
	@Override
	public String toString()
	{
		return (controlledByUser ? "Player" : "Opponent") + " cards: " + 
			this.cards.getCards()[0].toString() + ", " + this.cards.getCards()[1].toString();
	}
	
	public void drawNewCards(Deck deck)
	{
		this.cards = new CardHolder(deck);
		
		if (controlledByUser)
		{
			UIManager.instance.displayPlayerCards(this.cards.getCards());
		}
		else
		{
			UIManager.instance.displayOpponentCards(this.cards.getCards());
		}
		
		System.out.println(this);
	}
	
	public Card[] getCards()
	{
		return cards.getCards();
	}
	
	public int getMoney()
	{
		return this.money;
	}
	
	public int getBet()
	{
		return this.bet;
	}
	
	public void bet(int amount)
	{
		if (amount > this.money)
		{
			throw new IllegalArgumentException();
		}
		this.money -= amount;
		this.bet += amount;
	}
	
	public void addMoney(int amount)
	{
		this.money += amount;
	}
	
	public int resetBet()
	{
		int tmp = this.bet;
		this.bet = 0;
		return tmp;
	}
	
	public boolean canAfford(int amount)
	{
		return (this.money - amount >= 0);
	}
	
	public void fold()
	{
		System.out.println((controlledByUser ? "User - " : "AI - ") + "Player::fold");
		GameManager.instance.makeMove(Moves.FOLD, this);
	}
	
	public void check()
	{
		System.out.println((controlledByUser ? "User - " : "AI - ") + "Player::check");
		GameManager.instance.makeMove(Moves.CHECK, this);
	}
	
	public void call()
	{
		System.out.println((controlledByUser ? "User - " : "AI - ") + "Player::call");
		GameManager.instance.makeMove(Moves.CALL, this);
	}
	
	public void raise()
	{
		System.out.println((controlledByUser ? "User - " : "AI - ") + "Player::raise");
		GameManager.instance.makeMove(Moves.RAISE, this);
	}
}