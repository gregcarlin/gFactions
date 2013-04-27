package en.gregthegeek.gfactions.faction;

import net.canarymod.chat.TextFormat;

/**
 * Represents a WarZone.
 * 
 * @author gregthegeek
 *
 */
public class WarZone extends ZoneFaction {

	@Override
	public int getId() {
		return -3;
	}

	@Override
	public String getName() {
		return TextFormat.RED + "War Zone";
	}

	@Override
	public String getDescription() {
		return "Not the safest place to be.";
	}

	@Override
	public String getColorRelative(Faction to) {
		return TextFormat.RED;
	}
}
