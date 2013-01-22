/**
 * Manages the retrieval and storage of data in a MySQL database.
 * 
 * @author gregthegeek
 *
 */
public class SQLSource implements Datasource {

	@Override
	public CachedFaction getFaction(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Faction[] getAllFactions() { //return some lazy stuff
		//TODO
		return null;
	}

	@Override
	public void fix() {
		// TODO Auto-generated method stub
	}

	@Override
	public gPlayer getPlayer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
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
	public Relation[] getAllRelations() {
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
}
