package en.gregthegeek.gfactions.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
 * relations.txt is stored in the following format:
 * type:f1:f2
 * f1 and f2 are faction ids, type is also an integer
 * 
 * land.txt is stored in the following format:
 * x:z:world_name:dim:fac_id:owners
 * 
 * balances.txt is stored in the following format:
 * f/p:id/name:amount
 * 
 * @author gregthegeek
 *
 */
public class FileSource implements Datasource {
    private static final String FACTION_PATH = Config.FOLDER + "factions.txt";
    private static final String PLAYER_PATH = Config.FOLDER + "players.txt";
    private static final String RELATION_PATH = Config.FOLDER + "relations.txt";
    private static final String LAND_PATH = Config.FOLDER + "land.txt";
    private static final String BALANCE_PATH = Config.FOLDER + "balances.txt";
	private final List<String> factionFile;
	private final List<String> playerFile;
	private final List<String> relationFile;
	private final List<String> landFile;
	private final List<String> balanceFile;
	
	public FileSource() throws DatasourceException {
		factionFile = Utils.readFile(FACTION_PATH);
		playerFile = Utils.readFile(PLAYER_PATH);
		relationFile = Utils.readFile(RELATION_PATH);
		landFile = Utils.readFile(LAND_PATH);
		balanceFile = Utils.readFile(BALANCE_PATH);
	}

	@Override
	public CachedFaction getFaction(int id) { // this should actually never be called, as factions are already all cached
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
		for(String s : playerFile) {
		    String[] split = s.split(":");
		    if(split.length >= 3 && split[0].equalsIgnoreCase(name)) {
		        gPlayer gp = new gPlayer(split[0], i(split[1]));
		        gp.setTitle(split[2]);
		        return gp;
		    }
		}
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
		// save data instead
	    saveFactions();
	    savePlayers();
	}
	
	private void saveFactions() {
	    save(FACTION_PATH, factionFile, "faction");
	}
	
	private void savePlayers() {
	    save(PLAYER_PATH, playerFile, "player");
	}
	
	private void saveRelations() {
	    save(RELATION_PATH, relationFile, "relation");
	}
	
	private void saveLand() {
	    save(LAND_PATH, landFile, "land");
	}
	
	private void save(String path, List<String> data, String name) {
	    try {
	        Utils.writeFile(path, data);
	    } catch (DatasourceException e) {
	        Canary.logSevere("Error saving " + name + " data.");
	    }
	}

	@Override
	public void save(CachedFaction faction) {
	    char midVal;
	    if(faction.isOpen() && faction.isPeaceful()) {
	        midVal = '3';
	    } else if(faction.isOpen()) {
	        midVal = '1';
	    } else if(faction.isPeaceful()) {
	        midVal = '2';
	    } else {
	        midVal = '0';
	    }
	    
		String serial = faction.getId() + ":" + faction.getName() + ":" + faction.getDescription() + ":" + midVal + ":" + faction.getAdmin() + ":" + serialize(faction.getHome()) + ":" + serialize(faction.getMods()) + ":" + serialize(faction.getMembers());
		factionFile.add(serial);
		
		saveFactions();
	}
	
	private static String serialize(String[] arr) {
	    StringBuilder sb = new StringBuilder();
	    for(String s : arr) {
	        sb.append(s).append(",");
	    }
	    return sb.substring(0, sb.length() - 1);
	}

	@Override
	public void save(gPlayer[] players) {
		for(gPlayer gp : players) {
		    playerFile.add(gp.getName() + ":" + gp.getPower() + ":" + gp.getTitle());
		}
		savePlayers();
	}

	@Override
	public void save(Relation[] relations) {
		for(Relation r : relations) {
		    relationFile.add(r.type.ordinal() + ":" + r.getOne().getId() + ":" + r.getTwo().getId());
		}
		saveRelations();
	}

	@Override
	public void delete(Faction f) {
	    String test = f.getId() + ":";
	    Iterator<String> it = factionFile.iterator();
		while(it.hasNext()) {
		    if(it.next().startsWith(test)) {
		        it.remove();
		        break;
		    }
		}
		saveFactions();
	}

	@Override
	public void save(Land land) {
		landFile.add(land.getX() + ":" + land.getZ() + ":" + land.getWorld() + ":" + land.getDimension() + ":" + serialize(land.getOwners()));
		saveLand();
	}

	@Override
	public Land[] getAllLand() {
		List<Land> rt = new ArrayList<Land>();
		for(String line : landFile) {
		    String[] split = line.split(":");
		    if(split.length < 5) continue;
		    try {
		        rt.add(new Land(Integer.parseInt(split[0]), Integer.parseInt(split[1]), split[2], Integer.parseInt(split[3]), split[4].split(",")));
		    } catch (NumberFormatException e) {
		        Canary.logSevere("land.txt is corrupted.");
		    }
		}
		return rt.toArray(new Land[0]);
	}

	@Override
	public Relation getRelation(Faction one, Faction two) {
	    int f1 = one.getId();
	    int f2 = two.getId();
		for(String line : relationFile) {
		    String[] split = line.split(":");
		    if(split.length < 3) continue;
		    try {
		        int i1 = Integer.parseInt(split[1]);
		        int i2 = Integer.parseInt(split[2]);
		        if((i1 == f1 && i2 == f2) || (i1 == f2 && i2 == f1)) {
		            return new Relation(Relation.Type.values()[Integer.parseInt(split[0])], one, two);
		        }
		    } catch (NumberFormatException e) {
		        Canary.logSevere("relations.txt is corrupted.");
		    }
		}
		return new Relation(Relation.Type.NEUTRAL, one, two);
	}

	@Override
	public void delete(Land l) {
	    String search = l.getX() + ":" + l.getZ() + ":";
		Iterator<String> it = landFile.iterator();
		while(it.hasNext()) {
		    String line = it.next();
		    if(line.startsWith(search)) {
		        it.remove();
		        return;
		    }
		}
	}

	@Override
	public void delete(Relation r) {
		Iterator<String> it = relationFile.iterator();
		while(it.hasNext()) {
		    String line = it.next();
		    String[] split = line.split(":");
		    if(split.length < 3) continue;
		    try {
		        int f1 = Integer.parseInt(split[1]);
		        int f2 = Integer.parseInt(split[2]);
		        int r1 = r.getOne().getId();
		        int r2 = r.getTwo().getId();
		        if((f1 == r1 && f2 == r2) || (f1 == r2 && f2 == r1)) {
		            it.remove();
		            return;
		        }
		    } catch (NumberFormatException e) {
		        Canary.logSevere(RELATION_PATH + " is corrupted.");
		    }
		}
	}

	@Override
	public int getBalance(String player) {
		return getBalanceHelper("p:" + player + ":");
	}

	@Override
	public int getBalance(int fID) {
	    return getBalanceHelper("f:" + fID + ":");
	}
	
	private int getBalanceHelper(String search) {
	    for(String line : balanceFile) {
            if(line.startsWith(search)) {
                try {
                    return Integer.parseInt(line.split(":")[2]);
                } catch (Exception e) { // aiming to catch NumberFormatException and ArrayIndexOutOfBoundsException
                    Canary.logSevere(BALANCE_PATH + " is corrupted.");
                    break;
                }
            }
        }
        return 0;
	}

	@Override
	public void savePlayerBalances(HashMap<String, Integer> players) {
		saveBalances(players, "p");
	}

	@Override
	public void saveFactionBalances(HashMap<Integer, Integer> factions) {
		saveBalances(factions, "f");
	}
	
	private void saveBalances(HashMap<?, ?> map, String lineStart) {
	    Iterator<String> it = balanceFile.iterator();
        while(it.hasNext()) {
            if(it.next().startsWith(lineStart)) {
                it.remove();
            }
        }
        
        for(Entry<?, ?> e : map.entrySet()) {
            balanceFile.add(lineStart + ":" + e.getKey() + ":" + e.getValue());
        }
        
        saveBalances();
	}
	
	private void saveBalances() {
	    save(BALANCE_PATH, balanceFile, "balance");
	}

	@Override
	public Relation[] getRelationsWith(Faction f) {
		List<Relation> rt = new ArrayList<Relation>();
		int id = f.getId();
		for(String line : relationFile) {
		    String[] split = line.split(":");
		    if(split.length < 3) continue;
		    try {
		        int f1 = Integer.parseInt(split[1]);
		        int f2 = Integer.parseInt(split[2]);
		        if(id == f1 || id == f2) {
		            rt.add(new Relation(Relation.Type.values()[Integer.parseInt(split[0])], f1, f2));
		        }
		    } catch (NumberFormatException e) {
		        Canary.logSevere("relations.txt is corrupted.");
		    }
		}
		return rt.toArray(new Relation[0]);
	}
}
