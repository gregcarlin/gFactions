
public abstract class SpecialFaction extends Faction {
	
	@Override
	public int getId() {
		return -1;
	}
	
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
}
