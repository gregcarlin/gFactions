/**
 * Subclassed by SafeZone and WarZone
 * 
 * @author gregthegeek
 *
 */
public abstract class ZoneFaction extends SpecialFaction {
	
	@Override
	public boolean isMember(String member) {
		return false;
	}
	
	@Override
	public Player[] getOnlineMembers() {
		return new Player[0];
	}
	
	@Override
	public char getMapIcon(MapIconGen gen) {
		return '+';
	}
}
