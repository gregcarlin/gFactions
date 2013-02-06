/**
 * Interface for the server's economy.
 * 
 * @author gregthegeek
 *
 */
public interface Economy {
	/**
	 * Initializes a player in economy storage.
	 * 
	 * @param player The player to initialize.
	 */
	public void initPlayer(String player);
	
	/**
	 * Initializes a faction in economy storage.
	 * 
	 * @param id The faction to initialize.
	 */
	public void initFaction(int id);
	
	/**
	 * Modifies a player's balance only if the player can afford it.
	 * 
	 * @param player The player being charged/payed.
	 * @param amount The amount of money being charged (negative value) or payed (positive value).
	 * @return boolean Whether or not the player could afford the transaction.
	 */
	public boolean modifyBalance(String player, int amount);
	
	/**
	 * Gets a player's balance.
	 * 
	 * @param player The player to get the balance of.
	 * @return int The balance.
	 */
	public int getBalance(String player);
	
	/**
	 * Modifies a faction's balance only if the faction can afford it.
	 * 
	 * @param fac The faction being charged/payed.
	 * @param amount The amount of money being charged (negative value) or payed (positive value).
	 * @return boolean Whether or not the faction could afford the transaction.
	 */
	public boolean modifyBalance(Faction fac, int amount);
	
	/**
	 * Gets a faction's balance.
	 * 
	 * @param fac The faction to get the balance of.
	 * @return int The balance.
	 */
	public int getBalance(Faction fac);
	
	/**
	 * Saves the economy files.
	 */
	public void save();
}
