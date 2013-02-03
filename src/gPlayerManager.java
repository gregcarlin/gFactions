import java.util.ArrayList;

/**
 * Keeps track of power and titles.
 * 
 * @author gregthegeek
 *
 */
public class gPlayerManager {
	private ArrayList<gPlayer> players = new ArrayList<gPlayer>();
	
	public gPlayerManager() {
		
	}
	
	/**
	 * Returns the gPlayer for a player.
	 * 
	 * @param name The name of the player to return.
	 * @return gPlayer
	 */
	public gPlayer getPlayer(String name) {
		if(name == null) {
			return null;
		}
		
		for(gPlayer p : players) {
			if(p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		
		gPlayer gp = Utils.plugin.getDataSource().getPlayer(name);
		if(gp == null) {
			return null;
		}
		players.add(gp);
		if(gp.getPower() < gp.maxPower) {
			etc.getServer().addToServerQueue(new PowerAdder(gp), Utils.plugin.getConfig().getPowerRegenInterval());
		}
		
		return gp;
	}
	
	/**
	 * Should be called when a player logs in.
	 * 
	 * @param name
	 */
	public void initPlayer(String name) {
		if(getPlayer(name) == null) {
			Config config = Utils.plugin.getConfig();
			gPlayer gp = new gPlayer(name, config.getStartPower());
			players.add(gp);
			
			if(config.getSaveInterval() < 0) {
				Utils.plugin.getDataSource().save(new gPlayer[] {gp});
			}
			
			if(gp.getPower() < gp.maxPower) {
				etc.getServer().addToServerQueue(new PowerAdder(gp), config.getPowerRegenInterval());
			}
		}
	}
	
	/**
	 * Saves all gPlayers to the datasource.
	 */
	public void save() {
		Object[] or = players.toArray();
		gPlayer[] n = new gPlayer[or.length];
		for(int i=0; i<n.length; i++) {
			n[i] = (gPlayer) or[i];
		}
		Utils.plugin.getDataSource().save(n);
	}
	
	/**
	 * Returns an array of gPlayers that are online and have chatspy enabled.
	 * 
	 * @return gPlayer[]
	 */
	public gPlayer[] spying() {
		ArrayList<gPlayer> rt = new ArrayList<gPlayer>();
		for(gPlayer gp : players) {
			if(gp.chatSpy && gp.isOnline()) {
				rt.add(gp);
			}
		}
		return rt.toArray(new gPlayer[0]);
	}
}
