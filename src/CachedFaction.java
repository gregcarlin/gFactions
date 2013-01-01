import java.util.ArrayList;

/**
 * Represents a faction that has all its data loaded.
 * 
 * @author gregthegeek
 *
 */
public class CachedFaction extends Faction {
	private final int id;
	private String name;
	private boolean isOpen;
	private boolean isPeaceful;
	private String admin;
	private final Location home;
	private final ArrayList<String> mods = new ArrayList<String>();
	private final ArrayList<String> members = new ArrayList<String>();
	private final ArrayList<String> log = new ArrayList<String>();
	
	public CachedFaction(int id, String name, boolean isOpen, boolean isPeaceful, String admin, Location home) {
		this.id = id;
		this.name = name;
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
	}
	
	/**
	 * Adds messages to the faction log.
	 * 
	 * @param msgs The list of messages to add
	 */
	public void log(String... msgs) {
		log.ensureCapacity(log.size() + msgs.length);
		for(String m : msgs) {
			log.add(m);
		}
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
		String[] rt = new String[5];
		String relationColor = Utils.fManager.par.getRelationManager().getRelation(this, relativeTo).getColor();
		rt[0] = String.format("%1$s------------ %2$s%3$s %1$s------------", Colors.Gold, relationColor, relativeTo.getName());
		rt[1] = String.format("%1$sOpen: %2$s    %1$sPeaceful: %3$s", Colors.Yellow, Utils.readBool(isOpen()), Utils.readBool(isPeaceful()));
		rt[2] = "TODO"; //TODO: land/power/maxpower
		String[] mems = getMembersFormatted(relationColor);
		assert mems.length == 2;
		rt[3] = String.format("%sMembers online: %s", Colors.Yellow, mems[0]);
		rt[4] = String.format("%sMembers offline: %s", Colors.Yellow, mems[1]);
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
			String title = Utils.fManager.par.getPlayerManager().getPlayer(member).title;
			if(title != null && !title.isEmpty()) {
				title += " ";
			}
			if(etc.getServer().getPlayer(member) == null) { //offline
				offline.append(relationColor).append(title).append(rank).append(member).append(Colors.Gold).append(", ");
			} else { //online
				online.append(relationColor).append(title).append(rank).append(member).append(Colors.Gold).append(", ");
			}
		}
		return new String[] {online.substring(0, online.length() - 2), offline.substring(0, offline.length() - 2)};
	}
	
	@Override
	public String[] getMods() {
		return mods.toArray(new String[0]);
	}
	
	@Override
	public String[] getMembers() {
		return members.toArray(new String[0]);
	}
}
