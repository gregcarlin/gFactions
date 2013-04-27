package en.gregthegeek.gfactions.faction;

import java.util.ArrayList;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.TextFormat;

import en.gregthegeek.util.MapIconGen;
import en.gregthegeek.util.Utils;

/**
 * Represents the wilderness.
 * 
 * @author gregthegeek
 *
 */
public class Wilderness extends SpecialFaction {

	@Override
	public String getName() {
		return TextFormat.GREEN + "Wilderness";
	}

	@Override
	public boolean isMember(String player) {
		return Utils.plugin.getFactionManager().getFaction(player).equals(this);
	}
	
	@Override
	public String getDescription() {
		return "Pure anarchy.";
	}
	
	@Override
	public Player[] getOnlineMembers() {
		FactionManager fManager = Utils.plugin.getFactionManager();
		ArrayList<Player> rt = new ArrayList<Player>();
		for(Player p : Canary.getServer().getPlayerList()) {
			if(fManager.getFaction(p.getName()) instanceof Wilderness) {
				rt.add(p);
			}
		}
		return rt.toArray(new Player[0]);
	}
	
	@Override
	public int getId() {
		return -1;
	}
	
	@Override
	public String getColorRelative(Faction to) {
		return TextFormat.GRAY;
	}
	
	@Override
	public char getMapIcon(MapIconGen gen) {
		return '-';
	}
	
	@Override
	public int getPower() {
		return -1;
	}
}
