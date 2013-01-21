import java.util.ArrayList;

/**
 * Represents a chunk of land and stores ownership data for it.
 * 
 * @author gregthegeek
 *
 */
public class Land {
	private final int x;
	private final int z;
	private int faction = -1;
	private final ArrayList<String> owners = new ArrayList<String>();
	
	public Land(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	/**
	 * Returns this land's x chunk coordinate.
	 * 
	 * @return int
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns this land's z chunk coordinate.
	 * 
	 * @return int
	 */
	public int getZ() {
		return z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Land) {
			Land other = (Land) obj;
			return getX() == other.getX() && getZ() == other.getZ();
		}
		return false;
	}
	
	/**
	 * Returns the faction that owns this land.
	 * 
	 * @return Faction
	 */
	public Faction claimedBy() {
		if(faction < 0) {
			return null;
		}
		return Utils.plugin.getFactionManager().getFaction(faction);
	}
	
	/**
	 * Set this land to be claimed by the given faction.
	 * 
	 * @param faction The faction to claim the land.
	 */
	public void claim(Faction faction) {
		if(faction == null) {
			this.faction = -1;
			owners.clear();
		} else {
			this.faction = faction.getId();
		}
	}
	
	/**
	 * Returns the id of the faction that owns this land (faster than claimedBy()).
	 * 
	 * @return Faction
	 */
	public int getClaimerId() {
		return faction;
	}
	
	/**
	 * Returns true if this land is owned by something other than the wilderness.
	 * 
	 * @return boolean
	 */
	public boolean isClaimed() {
		return faction != -1;
	}
	
	/**
	 * Returns those who have build rights here.
	 * 
	 * @return String[]
	 */
	public String[] getOwners() {
		return owners.toArray(new String[0]);
	}
	
	/**
	 * Toggle whether or not a player can build here.
	 * 
	 * @param player The name of the player to toggle.
	 * @return boolean True if they can now build, false if they can't.
	 */
	public boolean toggleOwner(String player) {
		player = player.toLowerCase();
		if(owners.contains(player)) {
			owners.remove(player);
			return false;
		} else {
			owners.add(player);
			return true;
		}
	}
}
