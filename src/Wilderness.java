
public class Wilderness extends SpecialFaction {

	@Override
	public String getName() {
		return Colors.Green + "Wilderness";
	}

	@Override
	public boolean isMember(String player) {
		return Utils.fManager.getFaction(player).equals(this);
	}
}
