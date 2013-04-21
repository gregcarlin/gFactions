package en.gregthegeek.gfactions.faction;

import java.util.ArrayList;

import en.gregthegeek.gfactions.Config;
import en.gregthegeek.gfactions.db.Datasource;
import en.gregthegeek.gfactions.economy.Economy;
import en.gregthegeek.gfactions.land.Land;
import en.gregthegeek.gfactions.player.gPlayerManager;
import en.gregthegeek.util.Utils;

/**
 * Manages factions and provides an interface for accessing them.
 * 
 * @author gregthegeek
 *
 */
public class FactionManager {
	private static final int PAGESIZE = 8;
	private final Faction wilderness = new Wilderness();
	private final Faction safezone = new SafeZone();
	private final Faction warzone = new WarZone();
	private ArrayList<Faction> factions = new ArrayList<Faction>(); // Should have every faction on the server
	
	public FactionManager() {
		Faction[] facs = Utils.plugin.getDataSource().getAllFactions();
		factions.ensureCapacity(facs.length);
		Economy e = Utils.plugin.getEconomy();
		for(Faction f : facs) {
			factions.add(f);
			e.initFaction(f.getId());
		}
	}
	
	/**
	 * Returns a list of server factions.
	 * 
	 * @param page The page of the list to show.
	 * @param relativeTo The faction to display names relative to.
	 * @return String[]
	 */
	public String[] getList(int page, Faction relativeTo) {
		String[] rt = new String[PAGESIZE + 2];
		int max = factions.size();
		rt[0] = String.format("%s---------- Factions | Page %d/%d ----------", Colors.Gold, page + 1, max / PAGESIZE + 1);
		for(int i=0; i<rt.length-2; i++) {
			int index = page * PAGESIZE + i;
			if(index < max && index >= 0) {
				Faction f = factions.get(index);
				rt[i + 1] = String.format("%s (%d/%d/%d)", f.getNameRelative(relativeTo), f.getLand().length, f.getPower(), f.getMaxPower());
			} else {
				rt[i + 1] = "No more.";
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
		// check to see if we already have a cached version.
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
		
		// update our list to hold the cached version of the faction.
		CachedFaction cache = Utils.plugin.getDataSource().getFaction(id);
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
		if(id == -1) {return wilderness;}
		if(id == -2) {return safezone;}
		if(id == -3) {return warzone;}
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
		Datasource ds = Utils.plugin.getDataSource();
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
		Config config = Utils.plugin.getConfig();
		CachedFaction f = new CachedFaction(getNextId(), factionName, config.getDefaultFactionDesc(), config.isDefaultFactionOpen(), false, creator, null);
		factions.add(f);
		Utils.plugin.getEconomy().initFaction(f.getId());
		
		if(config.getSaveInterval() < 0) {
			Utils.plugin.getDataSource().save(f);
		}
		
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
		
		for(Land l : Utils.plugin.getLandManager().getOwnedBy(f)) {
			l.claim(null);
		}
		
		gPlayerManager gpm = Utils.plugin.getPlayerManager();
		for(String m : f.getMembers()) {
			gpm.getPlayer(m).setTitle("");
		}
		
		Utils.plugin.getRelationManager().removeAll(f);
		
		Utils.plugin.getDataSource().delete(f);
	}
}
