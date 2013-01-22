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
	 * Reads information for all land.
	 * 
	 * @return Land[]
	 */
	public Land[] getAllLand();
	
	/**
	 * Reads information for all the relations in the server.
	 * 
	 * @return Relation[]
	 */
	public Relation[] getAllRelations();
	
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
	
	/**
	 * Closes all open resources used by the datasource.
	 */
	public void close();
	
	/**
	 * Saves the given faction to storage.
	 * 
	 * @param faction The CachedFaction to save.
	 */
	public void save(CachedFaction faction);
	
	/**
	 * Saves the given gPlayers to storage.
	 * 
	 * @param players The gPlayers to save.
	 */
	public void save(gPlayer[] players);
	
	/**
	 * Saves the given relations to storage.
	 * 
	 * @param relations The relations to save.
	 */
	public void save(Relation[] relations);
	
	/**
	 * Saves the given land chunk to storage.
	 * 
	 * @param land The land to save.
	 */
	public void save(Land land);
	
	/**
	 * Deletes a faction from storage.
	 * 
	 * @param f The faction to delete.
	 */
	public void delete(Faction f);
	
	/**
	 * Deletes a land parcel from storage.
	 * 
	 * @param l The land to delete.
	 */
	public void delete(Land l);
	
	/**
	 * Deletes a relation from storage.
	 * 
	 * @param r The relation to delete.
	 */
	public void delete(Relation r);
}
