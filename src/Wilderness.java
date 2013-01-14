
public class Wilderness extends SpecialFaction {

	@Override
	public String getName() {
		return Colors.Green + "Wilderness";
	}

	@Override
	public boolean isMember(String player) {
		return Utils.plugin.getFactionManager().getFaction(player).equals(this);
	}
}
