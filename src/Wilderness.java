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
		return "";
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
}
