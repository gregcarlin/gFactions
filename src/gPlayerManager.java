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
			if(p.getName().equals(name)) {
				return p;
			}
		}
		
		gPlayer rt = gFac.getDataSource().getPlayer(name);
		if(rt == null) {
			rt = new gPlayer(name, gFac.getConfig().getStartPower());
		}
		players.add(rt);
		return rt;
	}
}
