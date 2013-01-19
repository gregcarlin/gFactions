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
	public final ArrayList<String> owners = new ArrayList<String>();
	
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
}
