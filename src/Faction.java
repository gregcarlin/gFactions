/**
 * An interface for all classes that represent a faction.
 * 
 * @author gregthegeek
 *
 */
public abstract class Faction {
	public enum PlayerRank {
		ADMIN("**"),
		MODERATOR("*"),
		MEMBER("");
		
		private final String prefix;
		
		private PlayerRank(String prefix) {
			this.prefix = prefix;
		}
		
		public String getPrefix() {
			return prefix;
		}
	}
	
	final FactionManager fManager;
	
	public Faction(FactionManager fManager) {
		this.fManager = fManager;
	}
	
	/**
	 * Returns the UUID for the faction.
	 * 
	 * @return int
	 */
	public abstract int getId();
	
	/**
	 * Returns the name (tag) of the faction.
	 * 
	 * @return String
	 */
	public abstract String getName();
	
	/**
	 * Returns whether or not the faction allows people to join without invitations.
	 * 
	 * @return boolean
	 */
	public abstract boolean isOpen();
	
	/**
	 * Returns whether or not the faction is peaceful.
	 * 
	 * @return boolean
	 */
	public abstract boolean isPeaceful();
	
	/**
	 * Returns the name of the faction owner.
	 * 
	 * @return String
	 */
	public abstract String getAdmin();
	
	/**
	 * Returns the location of the faction home.
	 * 
	 * @return Location
	 */
	public abstract Location getHome();
	
	/**
	 * Returns whether or not a player is in the faction.
	 * 
	 * @param player The player to check
	 * @return boolean
	 */
	public abstract boolean isMember(String player);
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Faction) {
			return ((Faction) obj).getId() == getId();
		}
		return false;
	}
	
	/**
	 * Returns an array of messages that describe the faction.
	 * Used by /f who
	 * @see FactionCommand#parseCommand(MessageReceiver, String[])
	 * 
	 * @param playerRelativeTo The player the messages are formatted relative to (for enemy/ally/neutral).
	 * @return String[]
	 */
	public String[] getWho(String playerRelativeTo) {
		return getWho(fManager.getFaction(playerRelativeTo));
	}
	
	/**
	 * Returns an array of messages that describe the faction.
	 * Used by /f who
	 * @see FactionCommand#parseCommand(MessageReceiver, String[])
	 * 
	 * @param relativeTo The faction the messages are formatted relative to (for enemy/ally/neutral).
	 * @return String[]
	 */
	public abstract String[] getWho(Faction relativeTo);
	
	/**
	 * Returns an array of the names of the moderators of the faction.
	 * 
	 * @return String[]
	 */
	public abstract String[] getMods();
	
	/**
	 * Returns an array of the names of the regular members (non-admin and non-mod) of the faction.
	 * 
	 * @return String[]
	 */
	public abstract String[] getMembers();
	
	/**
	 * Returns an array of the names of everyone in the faction.
	 * 
	 * @return String[]
	 */
	public String[] getAllMembers() {
		String[] mods = getMods();
		String[] members = getMembers();
		String[] rt = new String[mods.length + members.length + 1];
		
		rt[0] = getAdmin();
		for(int i=0; i<mods.length; i++) {
			rt[i + 1] = mods[i];
		}
		for(int i=0; i<members.length; i++) {
			rt[i + mods.length] = members[i];
		}
		
		return rt;
	}
	
	/**
	 * Returns the rank of a player.
	 * 
	 * @param player
	 * @return
	 */
	public PlayerRank getRank(String player) {
		if(getAdmin().equals(player)) {
			return PlayerRank.ADMIN;
		} else if(Utils.arrayContains(getMods(), player)) {
			return PlayerRank.MODERATOR;
		} else {
			return PlayerRank.MEMBER;
		}
	}
}
