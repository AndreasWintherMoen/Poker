package poker;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AI extends Player
{
	// This will be the computer player.
	// The current implementation is incredibly simple
	// TODO: Make a better AI
	
//	private Player aiPlayer;
	private int raiseCounter = 0;

	
	public AI(int money)
	{
		super(false, money);
//		this.aiPlayer = player;
	}
	
	@Override
	public void drawNewCards(Deck deck)
	{
		this.raiseCounter = 0;
		
		super.drawNewCards(deck);
		
		GameManager.instance.canCall(this);
	}
	
	public void doAction()
	{
		// This is obviously a very temporary solution, only made for debugging purpouses
		System.out.println("AI::doAction - started timer");
		new Timer().schedule(
			    new TimerTask() {

			        @Override
			        public void run() {
			    		System.out.println("AI::doAction - doing action");	
			    		doActionAfterDelay();
			        }
			    }, 1000);
	}
	
	private void doActionAfterDelay()
	{
		List<Card> holeCards = Arrays.asList(getCards());
		List<Card> allCards = Stream.concat(GameManager.instance.getTableCards().stream(), holeCards.stream())
				.collect(Collectors.toList());
		
		Integer[] holeCardsValue = DetermineWinner.getPlayerScore(holeCards);
		Integer[] handValue = DetermineWinner.getPlayerScore(allCards);
		int currentRound = GameManager.instance.getCurrentRound().ordinal();
		double value = Math.random();
		
		
		System.out.println("AI CAN CALL: " + GameManager.instance.canCall(this));
		
		if (GameManager.instance.canCall(this))
		{
			// The fact that we can call means the player's bet is larger than the AI's bet, and that the AI can afford
			// the player's bet. Thus, we can either call, raise or fold. 
			if (handValue[0] > 5)
			{
				// We have a flush or better, so just bet. Unless there have been more than 4 betting rounds + current
				// round as int, just to prevent (close to) infinite betting.
				if (raiseCounter > 4 + currentRound)
				{
					call();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 4)
			{
				// We have a straight, bet up to 3 times + current round.
				if (raiseCounter > 3 + currentRound)
				{
					call();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 3)
			{
				// Three of a kind
				if (raiseCounter > 2 + currentRound)
				{
					call();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 2)
			{
				// Two pair
				if (raiseCounter > 1 + currentRound)
				{
					call();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 1)
			{
				// One pair
				
				
				if (currentRound == 0)
				{
					// We have a preflop pair, so let's raise up to 3 times
					if (raiseCounter > 3)
					{
						call();
					}
					else
					{
						raise();
					}
					return;
				}
				
				// We have a pair, but it's post-flop, so let's first check if we even use our own cards to form the 
				// pair, or if it's only by the table. 
				if (handValue[1] != holeCards.get(0).getValue() && handValue[1] != holeCards.get(1).getValue())
				{
					// Our hand is not contributing to the pair, so let's pretend like we don't have a pair.
					// We're most likely going to fold at this point
					value -= currentRound * .1d;
					value += (holeCards.get(0).getValue() - 9) * .01d; // Can get up to +.1 depending on high cards
					value += (holeCards.get(1).getValue() - 9) * .01d;
				}
			}
			else
			{
				// High card
				if (currentRound == 0)
				{
					// Pre-flop
					// We don't have a pair, so let's not raise. If the high cards are good we call, either we fold
					// (with randomness of course)
					int highestCard = Math.max(holeCards.get(0).getValue(), holeCards.get(1).getValue());
					int lowestCard = Math.min(holeCards.get(0).getValue(), holeCards.get(1).getValue());
					value += (highestCard - 13) * .1d;
					
					// If the lowest card is close to the highest card, i.e. we are more likely to get a straight, we
					// want to add a little bit of value
					if (highestCard - lowestCard < 4)
					{
						value += (4 - (highestCard - lowestCard)) * .05d;
					}
				}
				else
				{
					// We're post-flop and only have a high card, which is quite bad. We're reacting to a player bet,
					// so we're most likely going to fold.
					// In the future we should add a little bit of value if the board is connected or flushy and we're
					// contributing to the straight or flush.
					// If river, fold
					if (currentRound == 3)
					{
						value -= 1d;
					}
					else
					{
						value -= currentRound * .15d;
					}
				}
			}
			
			if (value > 1)
			{
				raise();
			}
			else if (value > 0.5d)
			{
				call();
			}
			else
			{
				fold();
			}
		}
		else if (GameManager.instance.canCheck(this))
		{
			// If we get here it means the player's bet equals the AI's bet, and that we can either raise or check. 
			
			// The fact that we can call means the player's bet is larger than the AI's bet, and that the AI can afford
			// the player's bet. Thus, we can either call, raise or fold. 
			if (handValue[0] > 5)
			{
				// We have a flush or better, so just bet. Unless there have been more than 5 betting rounds + current
				// round as int, just to prevent (close to) infinite betting.
				if (raiseCounter > 5 + currentRound)
				{
					check();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 4)
			{
				// We have a straight, bet up to 3 times + current round.
				if (raiseCounter > 4 + currentRound)
				{
					check();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 3)
			{
				// Three of a kind
				if (raiseCounter > 3 + currentRound || value < .05d)
				{
					check();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 2)
			{
				// Two pair
				if (raiseCounter > 2 + currentRound || value < .1d)
				{
					check();
				}
				else
				{
					raise();
				}
				return;
			}
			else if (handValue[0] == 1)
			{
				// One pair
				
				
				if (currentRound == 0)
				{
					// We have a preflop pair, so let's raise up to 3 times
					if (raiseCounter > 3)
					{
						check();
					}
					else
					{
						raise();
					}
					return;
				}
				
				// We have a pair, but it's post-flop, so let's first check if we even use our own cards to form the 
				// pair, or if it's only by the table. 
				if (handValue[1] != holeCards.get(0).getValue() && handValue[1] != holeCards.get(1).getValue())
				{
					// Our hand is not contributing to the pair, so let's pretend like we don't have a pair.
					// We're most likely going to fold at this point
					value -= currentRound * .12d;
					value += (holeCards.get(0).getValue() - 9) * .013d; // Can get up to +.1 depending on high cards
					value += (holeCards.get(1).getValue() - 9) * .013d;
				}
			}
			else
			{
				// High card
				if (currentRound == 0)
				{
					// Pre-flop
					// We don't have a pair, so we determine what to do based on high card values
					int highestCard = Math.max(holeCards.get(0).getValue(), holeCards.get(1).getValue());
					int lowestCard = Math.min(holeCards.get(0).getValue(), holeCards.get(1).getValue());
					value += (highestCard - 8) * .05d;
					
					if (highestCard == 14)
					{
						value += .05d; // Add a little bit extra if we have an ace. 
					}
					
					// If the lowest card is close to the highest card, i.e. we are more likely to get a straight, we
					// want to add a little bit of value
					if (highestCard - lowestCard < 4)
					{
						value += (4 - (highestCard - lowestCard)) * .05d;
					}
					
					// If there has been a lot of betting, we want to be more inclined to just check
					value -= raiseCounter * .2d;
				}
				else
				{
					// We're post-flop and only have a high card, which is quite bad. We're reacting to a player bet,
					// so we're most likely going to check.
					// In the future we should add a little bit of value if the board is connected or flushy and we're
					// contributing to the straight or flush.
					// However, we shouldn't only be checking, because we want to bluff occasionally
					value -= .25d;
					value -= currentRound * .05d;
					
					// Add .5 because this calculation was based on if value > .5 raise, and it's supposed to be 
					// if value > 1.
					value += .5d;
				}
			}
			
			if (value > 1)
			{
				raise();
			}
			else
			{
				check();
			}
		}
		else
		{
			System.out.println("AI Cannot move");
			// If we get here the AI likely cannot afford the player's bet. 
			// TODO: Figure out what to do here
		}
		
		
	}
	
	@Override
	public void raise()
	{
		this.raiseCounter++;
		
		super.raise();
	}
}