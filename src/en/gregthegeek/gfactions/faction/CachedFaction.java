package en.gregthegeek.gfactions.faction;

import java.util.ArrayList;

import net.canarymod.Canary;
import net.canarymod.api.world.position.Location;
import net.canarymod.chat.TextFormat;

import en.gregthegeek.util.Utils;

/**
 * Represents a faction that has all its data loaded.
 * 
 * @author gregthegeek
 *
 */
public class CachedFaction extends Faction {
	private final int id;
	private String name;
	private String desc;
	private boolean isOpen;
	private boolean isPeaceful;
	private String admin;
	private Location home;
	private final ArrayList<String> mods = new ArrayList<String>();
	private final ArrayList<String> members = new ArrayList<String>();
	
	public CachedFaction(int id, String name, String desc, boolean isOpen, boolean isPeaceful, String admin, Location home) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.isOpen = isOpen;
		this.isPeaceful = isPeaceful;
		this.admin = admin;
		this.home = home;
	}
	
	/**
	 * Adds mods to the faction.
	 * 
	 * @param list The list of mods to add
	 */
	public void addMods(String... list) {
		mods.ensureCapacity(mods.size() + list.length);
		for(String s : list) {
			mods.add(s);
		}
		save();
	}
	
	/**
	 * Adds members to the faction.
	 * 
	 * @param list The list of members to add
	 */
	public void addMembers(String... list) {
		members.ensureCapacity(members.size() + list.length);
		for(String s : list) {
			members.add(s);
		}
		save();
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public boolean isPeaceful() {
		return isPeaceful;
	}

	@Override
	public String getAdmin() {
		return admin;
	}

	@Override
	public Location getHome() {
		return home;
	}
	
	@Override
	public boolean isMember(String player) {
		return player.equals(admin) || mods.contains(player) || members.contains(player);
	}
	
	@Override
	public String[] getWho(Faction relativeTo) {
		String[] rt = new String[6];
		String relationColor = Utils.plugin.getRelationManager().getRelation(this, relativeTo).getColor();
		rt[0] = String.format("%1$s------------ %2$s%3$s %1$s------------", TextFormat.ORANGE, relationColor, getName());
		rt[1] = String.format("%s%s", relationColor, getDescription());
		rt[2] = String.format("%1$sOpen: %2$s    %1$sPeaceful: %3$s", TextFormat.YELLOW, Utils.readBool(isOpen(), "Yes", "No"), Utils.readBool(isPeaceful(), "Yes", "No"));
		rt[3] = String.format("%sLand/Power/Maxpower: %d/%d/%d", TextFormat.YELLOW, getLand().length, getPower(), getMaxPower());
		String[] mems = getMembersFormatted(relationColor);
		assert mems.length == 2;
		rt[4] = String.format("%sMembers online: %s", TextFormat.YELLOW, mems[0]);
		rt[5] = String.format("%sMembers offline: %s", TextFormat.YELLOW, mems[1]);
		return rt;
	}
	
	/**
	 * Returns two formatted lists, one for online and one for offline players.
	 * 
	 * @param relationColor The color that is associated with the perspective being represented.
	 * @return String[]
	 */
	private String[] getMembersFormatted(String relationColor) {
		StringBuilder online = new StringBuilder();
		StringBuilder offline = new StringBuilder();
		for(String member : getAllMembers()) {
			String rank = getRank(member).getPrefix();
			String title = Utils.plugin.getPlayerManager().getPlayer(member).getTitle();
			if(title != null && !title.isEmpty()) {
				title += " ";
			}
			if(Canary.getServer().getPlayer(member) == null) { // offline
				offline.append(relationColor).append(title).append(rank).append(member).append(TextFormat.ORANGE).append(", ");
			} else { //online
				online.append(relationColor).append(title).append(rank).append(member).append(TextFormat.ORANGE).append(", ");
			}
		}
		int onLen = online.length();
		int ofLen = offline.length();
		return new String[] {onLen > 2 ? online.substring(0, onLen - 2) : "None", ofLen > 2 ? offline.substring(0, offline.length() - 2) : "None"};
	}
	
	@Override
	public String[] getMods() {
		return mods.toArray(new String[0]);
	}
	
	@Override
	public String[] getMembers() {
		return members.toArray(new String[0]);
	}

	@Override
	public void add(String player, PlayerRank rank) {
		switch(rank) {
		default:
		case MEMBER:
			members.add(player);
			break;
		case MODERATOR:
			mods.add(player);
			break;
		case ADMIN:
			setAdmin(player);
			break;
		}
		save();
	}

	@Override
	public void remove(String player, PlayerRank oldRank) {
		switch(oldRank) {
		case MEMBER:
			members.remove(player);
			save();
			break;
		case MODERATOR:
			mods.remove(player);
			save();
			break;
		case ADMIN:
			disband();
			break;
		}
	}

	@Override
	public void setDescription(String desc) {
		this.desc = desc;
		save();
	}

	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		save();
	}

	@Override
	public void setOpen(boolean open) {
		this.isOpen = open;
		save();
	}

	@Override
	public void setHome(Location home) {
		this.home = home;
		save();
	}

	@Override
	public void setAdmin(String admin) {
		this.admin = admin;
		save();
	}
	
	private void save() {
		if(Utils.plugin.getConfig().getSaveInterval() < 0) {
			Utils.plugin.getDataSource().save(this);
		}
	}
}
