import java.util.HashMap;

/**
 * Represents an economy managed by gFactions.
 * 
 * @author gregthegeek
 *
 */
public class IntegratedEconomy implements Economy {
	private final HashMap<String, Integer> players = new HashMap<String, Integer>(); // player name -> balance
	private final HashMap<Integer, Integer> factions = new HashMap<Integer, Integer>(); // faction id -> balance

	@Override
	public void initPlayer(String player) {
		if(!players.containsKey(player)) {
			players.put(player, Utils.plugin.getDataSource().getBalance(player));
		}
	}
	
	@Override
	public void initFaction(int id) {
		if(!factions.containsKey(id)) {
			factions.put(id, Utils.plugin.getDataSource().getBalance(id));
		}
	}
	
	@Override
	public boolean modifyBalance(String player, int amount) {
		assert players.containsKey(player);
		int newAmt = players.get(player) - amount;
		if(newAmt >= 0) {
			players.put(player, newAmt);
			return true;
		}
		return false;
	}

	@Override
	public int getBalance(String player) {
		assert players.containsKey(player);
		return players.get(player);
	}

	@Override
	public boolean modifyBalance(Faction fac, int amount) {
		int id = fac.getId();
		assert factions.containsKey(id);
		int newAmt = factions.get(id) - amount;
		if(newAmt >= 0) {
			factions.put(id, newAmt);
			return true;
		}
		return false;
	}

	@Override
	public int getBalance(Faction fac) {
		assert factions.containsKey(fac.getId());
		return factions.get(fac.getId());
	}
}
