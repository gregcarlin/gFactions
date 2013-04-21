package en.gregthegeek.util;
import java.util.HashMap;

import en.gregthegeek.gfactions.faction.Faction;

/**
 * Assists in the generation of ASCII maps by organizing icon use.
 * 
 * @author gregthegeek
 *
 */
public class MapIconGen {
	private static final char[] baseIcons = {'\\', '/', '#', '?', '$', '@', '%', '&', '*', '=', '|', '<', '>', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	private int index = -1;
	private final HashMap<Integer, Character> factions = new HashMap<Integer, Character>();
	
	/**
	 * Returns the next unused char and increases the index by one.
	 * 
	 * @param fac The faction that is represented by the char.
	 * @return char
	 */
	public char nextChar(Faction fac) {
		int id = fac.getId();
		if(factions.containsKey(id)) {
			return factions.get(id);
		}
		index++;
		if(index >= baseIcons.length) {
			Utils.warning("Too many factions in one place! ASCII mapping must repeat characters!");
			index = 0;
		}
		factions.put(id, baseIcons[index]);
		return baseIcons[index];
	}
	
	/**
	 * Returns a map of all used factions (ids) to their symbols (chars)
	 * 
	 * @return HashMap<Integer, Character>
	 */
	public HashMap<Integer, Character> getFactionMap() {
		return factions;
	}
}
