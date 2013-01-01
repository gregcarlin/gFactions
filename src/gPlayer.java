/**
 * Holds extra player related data.
 * 
 * @author gregthegeek
 *
 */
public class gPlayer {
	private final String name;
	private int power;
	private final Object powerLock = new Object();
	public int maxPower = 10;
	public String title;
	
	public gPlayer(String name, int power) {
		this.name = name;
		this.power = power;
		this.title = "";
	}
	
	/**
	 * Returns the name of this player (same as server name).
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the amount of power this player has.
	 * 
	 * @return int
	 */
	public int getPower() {
		synchronized(powerLock) {
			return power;
		}
	}
	
	/**
	 * Returns this title, prefix, and name concatenated together.
	 * 
	 * @return String
	 */
	public String getFormattedName() {
		String prefix = "";
		Faction f = Utils.fManager.getFaction(name);
		if(f != null) {
			prefix = f.getRank(name).getPrefix();
		}
		return title == null ? prefix + name : String.format("%s %s%s", title, prefix, name);
	}
	
	/**
	 * Increases power by one.
	 * 
	 * @return Whether or not power is now maxed out.
	 */
	public boolean increasePower() {
		synchronized(powerLock) {
			power++;
			if(power > maxPower) { // don't think I need this, but better safe than sorry
				power = maxPower;
			}
			return power == maxPower;
		}
	}
	
	/**
	 * Returns whether or not this player is currently online.
	 * 
	 * @return boolean
	 */
	public boolean isOnline() {
		return etc.getServer().getPlayer(name) != null;
	}
}
