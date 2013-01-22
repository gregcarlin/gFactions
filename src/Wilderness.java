import java.util.ArrayList;

/**
 * Represents the wilderness.
 * 
 * @author gregthegeek
 *
 */
public class Wilderness extends SpecialFaction {

	@Override
	public String getName() {
		return Colors.Green + "Wilderness";
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
		for(Player p : etc.getServer().getPlayerList()) {
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
		return Colors.Gray;
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
