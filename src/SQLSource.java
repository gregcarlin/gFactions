import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Manages the retrieval and storage of data in a MySQL database.
 * 
 * @author gregthegeek
 *
 */
public class SQLSource implements Datasource {
	private final CanaryConnection conn;
	
	public SQLSource() throws SQLException {
		conn = etc.getConnection();
		
		tryCreate("CREATE TABLE factions(`id` INT NOT NULL, PRIMARY_KEY(`id`), `name` VARCHAR(16) NOT NULL, `desc` TEXT NOT NULL, `open` BIT(1) NOT NULL, `home_x` INT, `home_y` INT, `home_z` INT, `home_dim` INT, `world` VARCHAR(32), `money` INT(32))");
		tryCreate("CREATE TABLE f_members(`name` VARCHAR(16) NOT NULL, PRIMARY_KEY(`name`), `power` INT(2) NOT NULL, `max_power` INT(2), `bonus` INT(2), `faction_id` INT)");
		tryCreate("CREATE TABLE f_land(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY_KEY(`id`), `world` VARCHAR(32) NOT NULL, `dim` INT(1) NOT NULL, `chunk_x` INT(5) NOT NULL, `chunk_z` INT(5) NOT NULL, `owner_id` INT)");
		tryCreate("CREATE TABLE f_relations(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY_KEY(`id`), `fac_one` INT NOT NULL, `fac_two` INT NOT NULL, `type` INT(1))");
	}
	
	private void tryCreate(String statement) throws SQLException {
		try {
			conn.prepareStatement(statement).execute();
		} catch (SQLException e) {
			if(e.getErrorCode() != 1050) { // 1050 = table already exists
				throw e;
			}
		}
	}

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
