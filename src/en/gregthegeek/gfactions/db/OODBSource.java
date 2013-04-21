package en.gregthegeek.gfactions.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import en.gregthegeek.gfactions.Config;
import en.gregthegeek.gfactions.faction.CachedFaction;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.gfactions.land.Land;
import en.gregthegeek.gfactions.player.gPlayer;
import en.gregthegeek.gfactions.relation.Relation;
import en.gregthegeek.util.Utils;

/**
 * Manages the retrieval and storage of persistent object data.
 * 
 * @author gregthegeek
 *
 */
public class OODBSource implements Datasource {
	private final ObjectContainer factionDB = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Config.FOLDER + "factions.db");
	private final ObjectContainer playerDB = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Config.FOLDER + "players.db");
	private final ObjectContainer relationDB = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Config.FOLDER + "relations.db");
	private final ObjectContainer landDB = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Config.FOLDER + "land.db");
	
	private final HashMap<String, Integer> pBalances;
	private final HashMap<Integer, Integer> fBalances;
	
	public OODBSource() {
		ObjectSet<HashMap<String, Integer>> rt = playerDB.queryByExample(new HashMap<String, Integer>());
		pBalances = rt.size() <= 0 ? new HashMap<String, Integer>() : rt.get(0);
		
		ObjectSet<HashMap<Integer, Integer>> rt2 = factionDB.queryByExample(new HashMap<Integer, Integer>());
		fBalances = rt2.size() <= 0 ? new HashMap<Integer, Integer>() : rt2.get(0);
	}

	@Override
	public CachedFaction getFaction(int id) {
		ObjectSet<CachedFaction> result = factionDB.queryByExample(new CachedFaction(id, null, null, false, false, null, null));
		return result.get(0);
	}

	@Override
	public Faction[] getAllFactions() {
		ObjectSet<CachedFaction> result = factionDB.queryByExample(new CachedFaction(0, null, null, false, false, null, null));
		return result.toArray(new Faction[0]);
	}

	@Override
	public void fix() {
		// TODO not sure if needed
	}

	@Override
	public gPlayer getPlayer(String name) {
		ObjectSet<gPlayer> result = playerDB.query(gPlayer.class);
		for(gPlayer gp : result) {
			if(gp.getName().equals(name)) {
				return gp;
			}
		}
		return null;
	}

	@Override
	public void close() {
		factionDB.close();
		playerDB.close();
		relationDB.close();
		landDB.close();
	}

	@Override
	public void save(CachedFaction faction) {
		factionDB.store(faction);
	}

	@Override
	public void save(gPlayer[] players) {
		for(gPlayer gp : players) {
			playerDB.store(gp);
		}
	}

	@Override
	public void save(Relation[] relations) {
		for(Relation r : relations) {
			relationDB.store(r);
		}
	}

	@Override
	public void delete(Faction f) {
		factionDB.delete(f);
	}

	@Override
	public void save(Land land) {
		landDB.store(land);
	}

	@Override
	public Land[] getAllLand() {
		ObjectSet<Land> result = landDB.query(Land.class);
		return result.toArray(new Land[0]);
	}

	@Override
	public Relation getRelation(Faction one, Faction two) {
		ObjectSet<Relation> result = relationDB.queryByExample(new Relation(null, one, two));
		return result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public void delete(Land l) {
		landDB.delete(l);
	}

	@Override
	public void delete(Relation r) {
		relationDB.delete(r);
	}

	@Override
	public int getBalance(String player) {
		return pBalances.containsKey(player) ? pBalances.get(player) : Utils.plugin.getConfig().getStartMoney();
	}

	@Override
	public int getBalance(int fID) {
		return fBalances.containsKey(fID) ? fBalances.get(fID) : Utils.plugin.getConfig().getStartMoney();
	}

	@Override
	public void savePlayerBalances(HashMap<String, Integer> players) {
		playerDB.store(players);
	}

	@Override
	public void saveFactionBalances(HashMap<Integer, Integer> factions) {
		factionDB.store(factions);
	}

	@Override
	public Relation[] getRelationsWith(Faction f) {
		ObjectSet<Relation> result = relationDB.query(Relation.class);
		ArrayList<Relation> rt = new ArrayList<Relation>();
		for(Relation r : result) {
			if(r.isInvolved(f)) {
				rt.add(r);
			}
		}
		return rt.toArray(new Relation[0]);
	}
}
