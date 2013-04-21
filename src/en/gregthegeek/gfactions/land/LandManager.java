package en.gregthegeek.gfactions.land;
import java.util.ArrayList;

import net.canarymod.api.world.position.Location;

import en.gregthegeek.gfactions.db.Datasource;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.util.Utils;

/**
 * Manages faction-owned land.
 * 
 * @author gregthegeek
 *
 */
public class LandManager {
	private ArrayList<Land> lands = new ArrayList<Land>();
	
	public LandManager() {
		Land[] land = Utils.plugin.getDataSource().getAllLand();
		lands.ensureCapacity(land.length);
		for(Land l : land) {
			lands.add(l);
		}
	}
	
	/**
	 * Returns the land at a given block location. Will be created if doesn't already exist.
	 * 
	 * @param location Block location.
	 * @return Land
	 */
	public Land getLandAt(Location location) {
		return getLandAt((int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getWorldName(), location.getType().getId());
	}
	
	/**
	 * Returns the land at a given block location. Will be created if doesn't already exist.
	 * 
	 * @param x Block x.
	 * @param y Block y (irrelevant).
	 * @param z Block z.
	 * @param world The name of the world.
	 * @param dim The dimension.
	 * @return Land
	 */
	public Land getLandAt(int x, int y, int z, String world, int dim) {
		return getLandAt(x / 16, z / 16, world, dim);
	}
	
	/**
	 * Returns the land at a given chunk location. Will be created if doesn't already exist.
	 * 
	 * @param x Chunk x.
	 * @param z Chunk z.
	 * @param world The name of the world.
	 * @param dim The dimension.
	 * @return Land
	 */
	public Land getLandAt(int x, int z, String world, int dim) {
		for(Land l : lands) {
			if(l.getX() == x && l.getZ() == z && l.getWorld().equals(world) && l.getDimension() == dim) {
				return l;
			}
		}
		
		Land l = new Land(x, z, world, dim);
		lands.add(l);
		return l;
	}
	
	/**
	 * Returns all the land owned by a given faction.
	 * 
	 * @param f The faction that claimed the land.
	 * @return Land[]
	 */
	public Land[] getOwnedBy(Faction f) {
		return getOwnedBy(f.getId());
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
	
	/**
	 * Saves all claimed land to the datasource.
	 */
	public void save() {
		Datasource ds = Utils.plugin.getDataSource();
		for(Land l : lands) {
			if(l.isClaimed()) {
				ds.save(l);
			}
		}
	}
}
