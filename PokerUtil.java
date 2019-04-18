package poker;

import poker.Enums.Cards;

public class PokerUtil
{
	public static String getFilePath(Card card)
	{
		String filePath = "src/poker/resources/cards/";
		if (card == null)
		{
			filePath += "EmptyCard";
		}
		else
		{
			filePath += card.toString();
		}
		filePath += ".png";
		
		return filePath;
	}
	
	public static String getFilePath(Cards card)
	{
		return getFilePath(Card.enumToCard(card));
	}
}