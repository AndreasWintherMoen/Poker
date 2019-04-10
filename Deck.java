package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck
{
	public enum Cards 
	{
		S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13,
		H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12, H13,
		D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13,
		C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13
	}
	private List<Card> cards = new ArrayList<Card>();
	
	
	public Deck()
	{
		cards = fillDeck();
		shuffleDeck(cards);
	}
	
	public Card drawCard()
	{
		return cards.remove(cards.size() - 1);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		cards.forEach(c -> sb.append(c.toString() + ", "));
		return sb.toString();
	}
	
	private List<Card> fillDeck()
	{
		cards = new ArrayList<Card>();
		for (Cards c : Cards.values())
		{
			cards.add(enumToCard(c));
		}
		return cards;
	}
	
	private void shuffleDeck(List<Card> cards)
	{
		Collections.shuffle(cards);
	}
	
	private Card enumToCard(Cards enumValue)
	{
		switch(enumValue)
		{
		case C1:
			return new Card('C', 1);
		case C10:
			return new Card('C', 10);
		case C11:
			return new Card('C', 11);
		case C12:
			return new Card('C', 12);
		case C13:
			return new Card('C', 13);
		case C2:
			return new Card('C', 2);
		case C3:
			return new Card('C', 3);
		case C4:
			return new Card('C', 4);
		case C5:
			return new Card('C', 5);
		case C6:
			return new Card('C', 6);
		case C7:
			return new Card('C', 7);
		case C8:
			return new Card('C', 8);
		case C9:
			return new Card('C', 9);
		case D1:
			return new Card('D', 1);
		case D10:
			return new Card('D', 10);
		case D11:
			return new Card('D', 11);
		case D12:
			return new Card('D', 12);
		case D13:
			return new Card('D', 13);
		case D2:
			return new Card('D', 2);
		case D3:
			return new Card('D', 3);
		case D4:
			return new Card('D', 4);
		case D5:
			return new Card('D', 5);
		case D6:
			return new Card('D', 6);
		case D7:
			return new Card('D', 7);
		case D8:
			return new Card('D', 8);
		case D9:
			return new Card('D', 9);
		case H1:
			return new Card('H', 1);
		case H10:
			return new Card('H', 10);
		case H11:
			return new Card('H', 11);
		case H12:
			return new Card('H', 12);
		case H13:
			return new Card('H', 13);
		case H2:
			return new Card('H', 2);
		case H3:
			return new Card('H', 3);
		case H4:
			return new Card('H', 4);
		case H5:
			return new Card('H', 5);
		case H6:
			return new Card('H', 6);
		case H7:
			return new Card('H', 7);
		case H8:
			return new Card('H', 8);
		case H9:
			return new Card('H', 9);
		case S1:
			return new Card('S', 1);
		case S10:
			return new Card('S', 10);
		case S11:
			return new Card('S', 11);
		case S12:
			return new Card('S', 12);
		case S13:
			return new Card('S', 13);
		case S2:
			return new Card('S', 2);
		case S3:
			return new Card('S', 3);
		case S4:
			return new Card('S', 4);
		case S5:
			return new Card('S', 5);
		case S6:
			return new Card('S', 6);
		case S7:
			return new Card('S', 7);
		case S8:
			return new Card('S', 8);
		case S9:
			return new Card('S', 9);
		default:
			throw new IllegalStateException("Could not convert Card enum to Card object");
		}
	}
}