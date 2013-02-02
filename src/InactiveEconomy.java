/**
 * Represents an economy that has been disabled. So it does nothing.
 * 
 * @author gregthegeek
 *
 */
public class InactiveEconomy implements Economy {
	@Override
	public void initPlayer(String player) {
		
	}
	
	@Override
	public void initFaction(int id) {
		
	}

	@Override
	public boolean modifyBalance(String player, int amount) {
		return true;
	}

	@Override
	public int getBalance(String player) {
		return 0;
	}

	@Override
	public boolean modifyBalance(Faction fac, int amount) {
		return true;
	}

	@Override
	public int getBalance(Faction fac) {
		return 0;
	}

}
