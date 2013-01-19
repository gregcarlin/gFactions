import java.util.ArrayList;

/**
 * Manages faction-owned land.
 * 
 * @author gregthegeek
 *
 */
public class LandManager {
	private ArrayList<Land> lands = new ArrayList<Land>();
	
	/**
	 * Returns the land at a given block location. Will be created if doesn't already exist.
	 * 
	 * @param location Block location.
	 * @return Land
	 */
	public Land getLandAt(Location location) {
		return getLandAt((int) location.x, (int) location.y, (int) location.z);
	}
	
	/**
	 * Returns the land at a given block location. Will be created if doesn't already exist.
	 * 
	 * @param x Block x.
	 * @param y Block y (irrelevant).
	 * @param z Block z.
	 * @return Land
	 */
	public Land getLandAt(int x, int y, int z) {
		return getLandAt(x / 16, z / 16);
	}
	
	/**
	 * Returns the land at a given chunk location. Will be created if doesn't already exist.
	 * 
	 * @param x Chunk x.
	 * @param z Chunk z.
	 * @return Land
	 */
	public Land getLandAt(int x, int z) {
		for(Land l : lands) {
			if(l.getX() == x && l.getZ() == z) {
				return l;
			}
		}
		
		Land l = new Land(x, z);
		lands.add(l);
		return l;
	}
	
	/**
	 * Returns all the land owned by a given faction.
	 * 
	 * @param fId The id of the owner faction.
	 * @return Land[]
	 */
	public Land[] getOwnedBy(int fId) {
		ArrayList<Land> subSet = new ArrayList<Land>();
		for(Land l : lands) {
			if(l.getClaimerId() == fId) {
				subSet.add(l);
			}
		}
		return subSet.toArray(new Land[0]);
	}
}
