package poker.Enums;

public enum Round
{
	PREFLOP,
	FLOP,
	TURN,
	RIVER {
		@Override
		public Round next() {
            return null; 
        };
	};
	
	public Round next()
	{
		return values()[ordinal() + 1];
	}
}