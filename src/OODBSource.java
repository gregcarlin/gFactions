import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

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
	
	public OODBSource() {
		
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
		ObjectSet<gPlayer> result = playerDB.queryByExample(new gPlayer(name, 0));
		return result.get(0);
	}

	@Override
	public void close() {
		factionDB.close();
		playerDB.close();
		relationDB.close();
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
}
