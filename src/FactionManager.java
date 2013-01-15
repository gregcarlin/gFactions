import java.util.ArrayList;

/**
 * Manages factions and provides an interface for accessing them.
 * 
 * @author gregthegeek
 *
 */
public class FactionManager {
	private static final int PAGESIZE = 8;
	private final Faction wilderness = new Wilderness();
	private ArrayList<Faction> factions = new ArrayList<Faction>(); //Should have every faction on the server
	public final gFactions par;
	
	public FactionManager(gFactions par) {
		this.par = par;
		Faction[] facs = par.getDataSource().getAllFactions();
		factions.ensureCapacity(facs.length);
		for(Faction f : facs) {
			factions.add(f);
		}
	}
	
	public String[] getList(int page) {
		String[] rt = new String[PAGESIZE + 2];
		rt[0] = String.format("%s---------- Factions | Page %d/%d ----------", Colors.Gold, page, factions.size() / PAGESIZE + 1);
		for(int i=1; i<rt.length-1; i++) {
			if(i * PAGESIZE >= factions.size()) {
				rt[i] = "No more.";
			} else {
				rt[i] = factions.get(i * PAGESIZE).getName();
			}
		}
		rt[rt.length - 1] = String.format("%s--------------------------------------", Colors.Gold);
		return rt;
	}
	
	/**
	 * Retrieves the information for a LazyFaction.
	 * 
	 * @param f The LazyFaction to cache.
	 * @return CachedFaction
	 */
	public CachedFaction cache(LazyFaction f) {
		//check to see if we already have a cached version.
		Faction fac = null;
		int id = f.getId();
		int index = -1;
		for(int i=0; i<factions.size(); i++) {
			Faction fa = factions.get(i);
			if(fa.getId() == id) {
				fac = fa;
				index = i;
				break;
			}
		}
		if(fac instanceof CachedFaction) {
			return (CachedFaction) fac;
		}
		
		//update our list to hold the cached version of the faction.
		CachedFaction cache = par.getDataSource().getFaction(id);
		factions.set(index, cache);
		
		return cache;
	}
	
	/**
	 * Returns a faction, lazy or cached.
	 * 
	 * @param id The id of the faction to return.
	 * @return Faction
	 */
	public Faction getFaction(int id) {
		for(Faction f : factions) {
			if(f.getId() == id) {
				return f;
			}
		}
		return null;
	}
	
	/**
	 * Returns a faction, lazy or cached.
	 * If the faction returned is lazy, it will have a cached copy available.
	 * If the player has no faction, wilderness is returned.
	 * 
	 * @param member A member of the faction being returned
	 * @return Faction
	 */
	public Faction getFaction(String member) {
		for(Faction f : factions) {
			if(f.isMember(member)) {
				return f;
			}
		}
		return wilderness;
	}
	
	/**
	 * Returns a faction, lazy or cached.
	 * If the faction returned is lazy, it will have a cached copy available.
	 * If a faction with the name specified does not exist, null is returned.
	 * 
	 * @param name The name of the faction to return.
	 * @return Faction
	 */
	public Faction getFactionByName(String name) {
		for(Faction f : factions) {
			if(f.getName().equalsIgnoreCase(name)) {
				return f;
			}
		}
		return null;
	}
	
	/**
	 * Saves all CachedFactions to the datasource.
	 */
	public void save() {
		Datasource ds = par.getDataSource();
		for(Faction f : factions) {
			if(f instanceof CachedFaction) {
				ds.save((CachedFaction) f);
			}
		}
	}
	
	/**
	 * Creates a new faction.
	 * 
	 * @param creator The player that is creating the faction (will become the faction admin).
	 * @param factionName The name of the faction being created.
	 * @return true if successful, false if a faction with the given name already existed.
	 */
	public boolean createFaction(String creator, String factionName) {
		if(getFactionByName(factionName) != null) {
			return false;
		}
		Config config = par.getConfig();
		factions.add(new CachedFaction(getNextId(), factionName, config.getDefaultFactionDesc(), config.isDefaultFactionOpen(), false, creator, null));
		return true;
	}
	
	/**
	 * Returns the id for the next created faction.
	 * Fills in gaps created by deleted factions.
	 * 
	 * @return int
	 */
	private int getNextId() {
		int id = 0;
		while(getFaction(id) != null) {
			id++;
		}
		return id;
	}
	
	/**
	 * Disbands a faction.
	 * 
	 * @param f The faction to disband.
	 */
	public void disband(Faction f) {
		factions.remove(f);
	}
}
