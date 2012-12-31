
public class Wilderness extends SpecialFaction {
	
	public Wilderness(FactionManager fManager) {
		super(fManager);
	}

	@Override
	public String getName() {
		return Colors.Green + "Wilderness";
	}

	@Override
	public boolean isMember(String player) {
		return fManager.getFaction(player).equals(this);
	}
}
