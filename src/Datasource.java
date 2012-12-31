/**
 * Interface for data sources (eg. flatfile, mysql).
 * 
 * @author gregthegeek
 *
 */
public interface Datasource {
	/**
	 * Gets the data for a faction, puts it into an object, and returns it.
	 * 
	 * @param id The id of the faction to return.
	 * @return CachedFaction
	 */
	public CachedFaction getFaction(int id);
	
	/**
	 * Reads information for all the factions on the server.
	 * 
	 * @return Faction[]
	 */
	public Faction[] getAllFactions();
	
	/**
	 * Scans the saved data for errors and reverts them to their default values.
	 */
	public void fix();
	
	/**
	 * Gets the data for a player, puts it into an object, and returns it.
	 * 
	 * @param name The name of the player to find and return.
	 * @return gPlayer
	 */
	public gPlayer getPlayer(String name);
}
