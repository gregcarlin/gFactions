
public abstract class ZoneFaction extends SpecialFaction {
	public ZoneFaction(FactionManager fManager) {
		super(fManager);
	}
	
	@Override
	public boolean isMember(String member) {
		return false;
	}
}
