package en.gregthegeek.gfactions.faction;

/**
 * Represents a SafeZone.
 * 
 * @author gregthegeek
 *
 */
public class SafeZone extends ZoneFaction {

	@Override
	public int getId() {
		return -2;
	}

	@Override
	public String getName() {
		return Colors.Gold + "Safe Zone";
	}

	@Override
	public String getDescription() {
		return "Fighting disabled.";
	}

	@Override
	public String getColorRelative(Faction to) {
		return Colors.Gold;
	}
}
