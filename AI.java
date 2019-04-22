package poker;

import java.util.Timer;
import java.util.TimerTask;

public class AI
{
	// This will be the computer player.
	// The current implementation is incredibly simple
	// TODO: Make a better AI
	
	private Player aiPlayer;
	
	public AI(Player player)
	{
		this.aiPlayer = player;
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
			    		if (GameManager.instance.canCall(aiPlayer))
			    		{
			    			aiPlayer.call();
			    		}
			    		else if (GameManager.instance.canCheck(aiPlayer))
			    		{
			    			aiPlayer.check();
			    		}
			    		else
			    		{
			    			System.out.println("AI Cannot move");
			    		}
			        }
			    }, 1000);
	}
}