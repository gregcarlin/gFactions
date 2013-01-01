/**
 * Represents a faction that has all of its data in storage.
 * 
 * @author gregthegeek
 *
 */
public class LazyFaction extends Faction {
	private final int id;
	private CachedFaction alreadyCached; //caching the cache. wat.
	
	public LazyFaction(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return cache().getName();
	}
	
	private CachedFaction cache() {
		if(alreadyCached != null) {
			alreadyCached = Utils.fManager.cache(this);
		}
		return alreadyCached;
	}

	@Override
	public boolean isOpen() {
		return cache().isOpen();
	}

	@Override
	public boolean isPeaceful() {
		return cache().isPeaceful();
	}

	@Override
	public String getAdmin() {
		return cache().getAdmin();
	}

	@Override
	public Location getHome() {
		return cache().getHome();
	}
	
	@Override
	public boolean isMember(String player) {
		return cache().isMember(player);
	}
	
	@Override
	public String[] getWho(Faction relativeTo) {
		return cache().getWho(relativeTo);
	}

	@Override
	public String[] getMods() {
		return cache().getMods();
	}

	@Override
	public String[] getMembers() {
		return cache().getMembers();
	}
}
