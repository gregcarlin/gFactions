package en.gregthegeek.gfactions.db;

import java.util.ArrayList;
import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.api.world.position.Location;

import en.gregthegeek.gfactions.Config;
import en.gregthegeek.gfactions.faction.CachedFaction;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.gfactions.land.Land;
import en.gregthegeek.gfactions.player.gPlayer;
import en.gregthegeek.gfactions.relation.Relation;
import en.gregthegeek.util.Utils;

/**
 * Manages the retrieval and storage of flat file data.
 * 
 * factions.txt is stored in the following format:
 * id:name:desc:open/peaceful/both:admin:home:mods:members
 * For open/peaceful/both, value is 0 if neither, 1 if just open, 2 if just peaceful, and 3 if both.
 * 
 * players.txt is stored in the following format:
 * name:power:title
 * 
 * @author gregthegeek
 *
 */
public class FileSource implements Datasource {
	private final String[] factionFile;
	private final String[] playerFile;
	
	public FileSource() throws DatasourceException {
		factionFile = Utils.readFile(Config.FOLDER + "factions.txt");
		playerFile = Utils.readFile(Config.FOLDER + "players.txt");
	}

	@Override
	public CachedFaction getFaction(int id) { //this should actually never be called, as factions are already all cached
		return (CachedFaction) Utils.plugin.getFactionManager().getFaction(id);
	}

	@Override
	public Faction[] getAllFactions() {
		ArrayList<Faction> rt = new ArrayList<Faction>();
		for(String s : factionFile) {
			String[] split = s.split(":");
			if(split.length < 8) {
				continue;
			}
			int oVal = Integer.parseInt(split[3]);
			boolean isOpen = oVal == 1 || oVal == 3;
			boolean isPeaceful = oVal >= 2;
			if(oVal < 0 || oVal > 3) {
				Utils.warning("%d is an invalid value for open/peaceful/both.", oVal);
			}
			CachedFaction f = new CachedFaction(Integer.parseInt(split[0]), split[1], split[2], isOpen, isPeaceful, split[4], expand(split[5]));
			f.addMods(split[6].split(","));
			f.addMembers(split[7].split(","));
			rt.add(f);
		}
		return rt.toArray(new Faction[0]);
	}
	
	@SuppressWarnings("unused")
	private static String serialize(Location location) {
		return String.format("%s,%d,%d,%d,%d,%d", location.getWorldName(), location.getX(), location.getY(), location.getZ(), location.getRotation(), location.getPitch());
	}
	
	private static Location expand(String s) {
		String[] split = s.split(",");
		Location rt = new Location(Canary.getServer().getWorld(split[0]), d(split[1]), d(split[2]), d(split[3]), f(split[4]), f(split[5]));
		return rt;
	}
	
	private static double d(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			Utils.warning("Error reading location, %s is not a valid double.", s);
			return 0;
		}
	}
	
	private static float f(String s) {
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			Utils.warning("Error reading location, %s is not a valid float.", s);
			return 0;
		}
	}
	
	private static int i(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			Utils.warning("Error reading location, %s is not a valid int.", s);
			return 0;
		}
	}

	@Override
	public void fix() {
		// TODO Auto-generated method stub
	}

	@Override
	public gPlayer getPlayer(String name) {
		return null;
	}
	
	public gPlayer[] getAllPlayers() {
		ArrayList<gPlayer> rt = new ArrayList<gPlayer>();
		for(String s : playerFile) {
			String[] split = s.split(":");
			if(split.length < 3) {
				continue;
			}
			gPlayer gp = new gPlayer(split[0], i(split[1]));
			gp.setTitle(split[2]);
			rt.add(gp);
		}
		return rt.toArray(new gPlayer[0]);
	}

	@Override
	public void close() {
		// everything is already closed
	}

	@Override
	public void save(CachedFaction faction) {
		// TODO Auto-generated method stub
	}

	@Override
	public void save(gPlayer[] players) {
		// TODO Auto-generated method stub
	}

	@Override
	public void save(Relation[] relations) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(Faction f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Land land) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Land[] getAllLand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Relation getRelation(Faction one, Faction two) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Land l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Relation r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBalance(String player) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBalance(int fID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void savePlayerBalances(HashMap<String, Integer> players) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveFactionBalances(HashMap<Integer, Integer> factions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Relation[] getRelationsWith(Faction f) {
		// TODO Auto-generated method stub
		return null;
	}
}
