package en.gregthegeek.gfactions.faction;

import java.util.ArrayList;

import net.canarymod.Canary;
import net.canarymod.api.Server;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.chat.MessageReceiver;

import en.gregthegeek.gfactions.FactionCommand;
import en.gregthegeek.gfactions.land.Land;
import en.gregthegeek.gfactions.player.gPlayerManager;
import en.gregthegeek.gfactions.relation.Relation;
import en.gregthegeek.util.MapIconGen;
import en.gregthegeek.util.Utils;


/**
 * An interface for all classes that represent a faction.
 * 
 * @author gregthegeek
 *
 */
public abstract class Faction {
	public enum PlayerRank {
		ADMIN("**", FactionCommand.CommandUsageRank.FACTION_ADMIN),
		MODERATOR("*", FactionCommand.CommandUsageRank.FACTION_MOD),
		MEMBER("", FactionCommand.CommandUsageRank.FACTION_MEMBER);
		
		private final String prefix;
		private final FactionCommand.CommandUsageRank cRank;
		
		private PlayerRank(String prefix, FactionCommand.CommandUsageRank cRank) {
			this.prefix = prefix;
			this.cRank = cRank;
		}
		
		public String getPrefix() {
			return prefix;
		}
		
		public FactionCommand.CommandUsageRank getCommandRank() {
			return cRank;
		}
	}
	
	private transient ArrayList<String> invited = new ArrayList<String>();
	
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
	 * Returns the name of this faction colored according to its relation with the given faction.
	 * 
	 * @param to The faction to color according to.
	 * @return String The faction name.
	 */
	public String getNameRelative(Faction to) {
		Relation.Type r = Utils.plugin.getRelationManager().getRelation(this, to);
		return r.getColor() + getName();
	}
	
	/**
	 * Set the name (tag) of the faction.
	 * 
	 * @param name The name to set.
	 */
	public abstract void setName(String name);
	
	/**
	 * Returns whether or not the faction allows people to join without invitations.
	 * 
	 * @return boolean
	 */
	public abstract boolean isOpen();
	
	/**
	 * Set whether or not the faction allows people to join without invitation.
	 * 
	 * @param open True if open, false if closed.
	 */
	public abstract void setOpen(boolean open);
	
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
	 * Transfers ownership of the faction.
	 * 
	 * @param admin The new faction owner.
	 */
	public abstract void setAdmin(String admin);
	
	/**
	 * Returns the location of the faction home.
	 * 
	 * @return Location
	 */
	public abstract Location getHome();
	
	/**
	 * Sets the location of the faction home.
	 * 
	 * @param home The new home.
	 */
	public abstract void setHome(Location home);
	
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
			int id = getId();
			return id == -1 ? ((Faction) obj).getName().equals(getName()) : ((Faction) obj).getId() == getId();
		}
		return false;
	}
	
	/**
	 * Returns an array of messages that describe the faction.
	 * Used by /f who
	 * 
	 * @param playerRelativeTo The player the messages are formatted relative to (for enemy/ally/neutral).
	 * @return String[]
	 */
	public String[] getWho(String playerRelativeTo) {
		return getWho(Utils.plugin.getFactionManager().getFaction(playerRelativeTo));
	}
	
	/**
	 * Returns an array of messages that describe the faction.
	 * Used by /f who
	 * 
	 * @param relativeTo
	 * @return
	 */
	public String[] getWho(MessageReceiver relativeTo) {
		return relativeTo instanceof Player ? getWho(((Player) relativeTo).getName()) : getWho((Faction) null);
	}
	
	/**
	 * Returns an array of messages that describe the faction.
	 * Used by /f who
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
			rt[i + mods.length + 1] = members[i];
		}
		
		return rt;
	}
	
	/**
	 * Returns the rank of a player.
	 * 
	 * @param player
	 * @return PlayerRank
	 */
	public PlayerRank getRank(String player) {
		if(getAdmin().equals(player)) {
			return PlayerRank.ADMIN;
		} else if(Utils.arrayContains(getMods(), player)) {
			return PlayerRank.MODERATOR;
		} else if(Utils.arrayContains(getMembers(), player)){
			return PlayerRank.MEMBER;
		} else {
			return null;
		}
	}
	
	/**
	 * Add a player to the list of those invited.
	 * 
	 * @param player The player to invite.
	 * @return boolean True if the player was already invited.
	 */
	public boolean invite(String player) {
		if(getInvited().contains(player)) {
			return true;
		}
		getInvited().add(player);
		return false;
	}
	
	/**
	 * Removes a player from the list of those invited.
	 * 
	 * @param player The player to deinvite.
	 * @return boolean Whether or not the player was on the list.
	 */
	public boolean deinvite(String player) {
		return getInvited().remove(player);
	}
	
	/**
	 * Returns whether or not a player is on the list of those invited.
	 * 
	 * @param player The player to check.
	 * @return boolean
	 */
	public boolean isInvited(String player) {
		return getInvited().contains(player);
	}
	
	private ArrayList<String> getInvited() {
		if(invited == null) {
			invited = new ArrayList<String>();
		}
		return invited;
	}
	
	/**
	 * Adds a player to the faction as a member.
	 * 
	 * @param player The name of the player to add.
	 */
	public void add(String player) {
		add(player, PlayerRank.MEMBER);
	}
	
	/**
	 * Adds a player to the faction.
	 * 
	 * @param player The name of the player to add.
	 * @param rank The rank the new player will have.
	 */
	public abstract void add(String player, PlayerRank rank);
	
	/**
	 * Removes a player from the faction.
	 * 
	 * @param player The name of the player to remove.
	 */
	public void remove(String player) {
		remove(player, getRank(player));
	}
	
	/**
	 * Removes a player from the faction.
	 * 
	 * @param player The name of the player to remove.
	 * @param oldRank The rank of the to be removed player.
	 */
	public abstract void remove(String player, PlayerRank oldRank);
	
	/**
	 * Sets the faction description.
	 * 
	 * @param desc The new description.
	 */
	public abstract void setDescription(String desc);
	
	/**
	 * Returns the faction description.
	 * 
	 * @return String
	 */
	public abstract String getDescription();
	
	/**
	 * Sends a message to all online faction members.
	 * 
	 * @param message The message to send.
	 */
	public void sendToMembers(String message) {
		Server server = Canary.getServer();
		for(String member : getAllMembers()) {
			Player p = server.getPlayer(member);
			if(p != null) {
				p.message(message);
			}
		}
	}
	
	/**
	 * Disbands the faction.
	 */
	public void disband() {
		Utils.plugin.getFactionManager().disband(this);
	}
	
	/**
	 * Returns an array of the online players in the faction.
	 * 
	 * @return Player[]
	 */
	public Player[] getOnlineMembers() {
		ArrayList<Player> online = new ArrayList<Player>();
		Server server = Canary.getServer();
		for(String member : getAllMembers()) {
			Player p = server.getPlayer(member);
			if(p != null) {
				online.add(p);
			}
		}
		return online.toArray(new Player[0]);
	}
	
	/**
	 * Toggle mod status of a player.
	 * 
	 * @param player The player to toggle.
	 * @return boolean True if now mod, false if now player.
	 */
	public boolean toggleMod(String player) {
		assert !getAdmin().equals(player);
		if(Utils.arrayContains(getMods(), player)) {
			remove(player, PlayerRank.MODERATOR);
			add(player, PlayerRank.MEMBER);
			return false;
		} else {
			remove(player, PlayerRank.MEMBER);
			add(player, PlayerRank.MODERATOR);
			return true;
		}
	}
	
	/**
	 * Returns the total power of the faction.
	 * 
	 * @return int
	 */
	public int getPower() {
		gPlayerManager pManager = Utils.plugin.getPlayerManager();
		int power = 0;
		for(String member : getAllMembers()) {
			power += pManager.getPlayer(member).getPower();
		}
		return power;
	}
	
	/**
	 * Returns the total max power of the faction.
	 * 
	 * @return int
	 */
	public int getMaxPower() {
		gPlayerManager pManager = Utils.plugin.getPlayerManager();
		int maxPower = 0;
		for(String member : getAllMembers()) {
			maxPower += pManager.getPlayer(member).getMaxPower();
		}
		return maxPower;
	}
	
	/**
	 * Returns the land owned by the faction.
	 * 
	 * @return Land[]
	 */
	public Land[] getLand() {
		return Utils.plugin.getLandManager().getOwnedBy(getId());
	}
	
	/**
	 * Returns the character that represents this faction on a map.
	 * 
	 * @param gen The map icon generator.
	 * @return char
	 */
	public char getMapIcon(MapIconGen gen) {
		return gen.nextChar(this);
	}
	
	/**
	 * Returns the color that represents the relationship between this faction and the given faction.
	 * 
	 * @param to The faction the color is relative to.
	 * @return String
	 */
	public String getColorRelative(Faction to) {
		return Utils.plugin.getRelationManager().getRelation(this, to).getColor();
	}
	
	/**
	 * Returns whether or not the given player is in this faction.
	 * 
	 * @param player The player to check.
	 * @return boolean
	 */
	public boolean has(String player) {
		for(String s : getAllMembers()) {
			if(s.equalsIgnoreCase(player)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the distance of the enemy closest to the given location.
	 * Will return Double.MAX_VALUE if there are no enemies in the same world as the given location.
	 * 
	 * @param from The location to find the distances from.
	 * @return double
	 */
	public double getNearestEnemyDist(Location from) {
		double closest = Double.MAX_VALUE;
		for(Faction f : Utils.plugin.getRelationManager().getRelations(this, Relation.Type.ENEMY)) {
			for(Player p : f.getOnlineMembers()) {
				Location l = p.getLocation();
				if(l.getWorldName().equals(from.getWorldName()) && l.getType().getId() == from.getType().getId()) {
					double d = Utils.distance(l, from);
					if(d < closest) {
						closest = d;
					}
				}
			}
		}
		return closest;
	}
	
	@Override
	public String toString() {
		return String.format("Faction[id=%d, name=%s]", getId(), getName());
	}
}