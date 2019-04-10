package poker;

public class PokerUtil
{
	public static String getFilePath(Card card)
	{
		String filePath = "src/poker/resources/cards/";
		System.out.println(card.toString());
		filePath += card.toString();
		filePath += ".png";
		
		return filePath;
	}
	
	public static String getFilePath(Cards card)
	{
		return getFilePath(Card.enumToCard(card));
	}
}