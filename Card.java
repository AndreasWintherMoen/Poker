package poker;

public class Card
{
	private final char suit;
	private final int value;
	
	public Card(char suit, int value) throws IllegalArgumentException
	{
		suit = Character.toUpperCase(suit);
		if (validSuit(suit) == false)
		{
			throw new IllegalArgumentException("Illegal card suit");
		}
		if (validValue(value) == false)
		{
			throw new IllegalArgumentException("Illegal card value. Note that an Ace should have the value 1");
		}
		
		this.suit = suit;
		this.value = value;
	}
	
	
	@Override
	public String toString()
	{
		return String.format("%s%s", this.suit, intValueToString(this.value));
	}
	
	private String intValueToString(int value) throws IllegalStateException
	{
		if (value > 1 && value < 11)
		{
			return Integer.toString(value);
		}
		
		switch (value)
		{
		case 1:
			return "A";
		case 11:
			return "J";
		case 12:
			return "Q";
		case 13:
			return "K";
		default:
			throw new IllegalStateException("Illegal Card value: " + Integer.toString(value));
		}
	}
	
	private boolean validSuit(char suit)
	{
		if (suit == 'S' || suit == 'H' || suit == 'D' || suit == 'C') return true;
		return false;
	}
	
	private boolean validValue(int value)
	{
		if (value < 1 || value > 13) return false;
		return true;
	}
}