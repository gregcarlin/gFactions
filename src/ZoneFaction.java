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
}
