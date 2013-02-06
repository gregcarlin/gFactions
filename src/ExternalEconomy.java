/**
 * Represents an economy powered by dConomy.
 * 
 * @author gregthegeek
 *
 */
public class ExternalEconomy implements Economy {

	@Override
	public void initPlayer(String player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initFaction(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean modifyBalance(String player, int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBalance(String player) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean modifyBalance(Faction fac, int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBalance(Faction fac) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
}
