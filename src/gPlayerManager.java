import java.util.ArrayList;

/**
 * Keeps track of power and titles.
 * 
 * @author gregthegeek
 *
 */
public class gPlayerManager {
	private ArrayList<gPlayer> players = new ArrayList<gPlayer>();
	private final gFactions gFac;
	
	public gPlayerManager(gFactions gFac) {
		this.gFac = gFac;
	}
	
	/**
	 * Returns the gPlayer for a player.
	 * 
	 * @param name The name of the player to return.
	 * @return gPlayer
	 */
	public gPlayer getPlayer(String name) {
		for(gPlayer p : players) {
			if(p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Saves all gPlayers to the datasource.
	 */
	public void save() {
		gFac.getDataSource().save(players.toArray(new gPlayer[0]));
	}
	
	/**
	 * Should be called when a player logs in.
	 * 
	 * @param name The name of the player.
	 */
	public void initPlayer(String name) {
		if(getPlayer(name) == null) {
			gPlayer rt = gFac.getDataSource().getPlayer(name);
			if(rt == null) {
				rt = new gPlayer(name, gFac.getConfig().getStartPower());
			}
			players.add(rt);
			
			if(rt.getPower() < rt.maxPower) {
				etc.getServer().addToServerQueue(new PowerAdder(rt));
			}
		}
	}
	
	private class PowerAdder implements Runnable {
		private final gPlayer gp;
		
		public PowerAdder(gPlayer gp) {
			this.gp = gp;
		}
		
		@Override
		public void run() {
			if(gp.isOnline() && !gp.increasePower()) { // power won't increase unless player is online
				etc.getServer().addToServerQueue(new PowerAdder(gp));
			}
		}
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
