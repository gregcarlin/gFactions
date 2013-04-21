package en.gregthegeek.gfactions.faction;
import en.gregthegeek.gfactions.land.Land;

/**
 * Subclassed by Wilderness and ZoneFaction
 * 
 * @author gregthegeek
 *
 */
public abstract class SpecialFaction extends Faction {
	
	@Override
	public boolean isOpen() {
		return false;
	}
	
	@Override
	public boolean isPeaceful() {
		return true;
	}
	
	@Override
	public String getAdmin() {
		return null;
	}
	
	@Override
	public Location getHome() {
		return null;
	}
	
	@Override
	public String[] getWho(String playerRelativeTo) { //player is irrelevant
		return getWho((Faction) null);
	}
	
	@Override
	public String[] getWho(Faction relativeTo) { //faction is irrelevant
		return new String[] {String.format("%1$s-------------- %2$s %1$s--------------", Colors.Gold, getName())};
	}
	
	@Override
	public String[] getMods() {
		return new String[0];
	}
	
	@Override
	public String[] getMembers() {
		return new String[0];
	}
	
	@Override
	public void add(String player, PlayerRank rank) {
		
	}
	
	@Override
	public void remove(String player, PlayerRank oldRank) {
		
	}
	
	@Override
	public void setDescription(String desc) {
		
	}
	
	@Override
	public void setName(String name) {
		
	}
	
	@Override
	public void setOpen(boolean open) {
		
	}
	
	@Override
	public void setHome(Location home) {
		
	}
	
	@Override
	public void disband() {
		
	}
	
	@Override
	public void setAdmin(String admin) {
		
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(getClass());
	}
	
	@Override
	public String[] getAllMembers() {
		return new String[0];
	}
	
	@Override
	public PlayerRank getRank(String player) {
		return null;
	}
	
	@Override
	public boolean invite(String player) {
		return true;
	}
	
	@Override
	public boolean deinvite(String player) {
		return false;
	}
	
	@Override
	public boolean isInvited(String player) {
		return false;
	}
	
	@Override
	public void sendToMembers(String msg) {
		
	}
	
	@Override
	public boolean toggleMod(String player) {
		return false;
	}
	
	@Override
	public String getNameRelative(Faction to) {
		return getName();
	}
	
	@Override
	public Land[] getLand() {
		return new Land[0];
	}
}
