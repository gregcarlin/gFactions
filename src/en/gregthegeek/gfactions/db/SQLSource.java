package en.gregthegeek.gfactions.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.canarymod.Canary;
import net.canarymod.api.world.DimensionType;
import net.canarymod.api.world.position.Location;
import net.visualillusionsent.utils.PropertiesFile;
import net.visualillusionsent.utils.UtilityException;

import en.gregthegeek.gfactions.faction.CachedFaction;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.gfactions.faction.FactionManager;
import en.gregthegeek.gfactions.faction.LazyFaction;
import en.gregthegeek.gfactions.faction.SpecialFaction;
import en.gregthegeek.gfactions.land.Land;
import en.gregthegeek.gfactions.player.gPlayer;
import en.gregthegeek.gfactions.relation.Relation;
import en.gregthegeek.util.Utils;


/**
 * Manages the retrieval and storage of data in a MySQL database.
 * 
 * @author gregthegeek
 *
 */
public class SQLSource implements Datasource {
	private final Connection conn = getConnection();
	
	private static Connection getConnection() {
	    PropertiesFile props = new PropertiesFile("config/db.cfg");
	    
	    try {
            return DriverManager.getConnection(props.getString("host") + ":" + props.getString("port"));
        } catch (UtilityException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    
	    Canary.logSevere("Error connecting to MySQL Server!");
	    return null;
	}
	
	public SQLSource() throws SQLException {
	    if(conn == null) return;
	    
		tryCreate("CREATE TABLE factions(`id` INT NOT NULL, PRIMARY_KEY(`id`), `name` VARCHAR(16) NOT NULL, UNIQUE(`name`), `desc` TEXT NOT NULL, `open` BIT(1) NOT NULL, `peaceful` BIT(1) NOT NULL, `home_x` INT, `home_y` INT, `home_z` INT, `home_dim` INT, `world` VARCHAR(32), `money` INT)");
		tryCreate("CREATE TABLE f_members(`name` VARCHAR(16) NOT NULL, PRIMARY_KEY(`name`), `power` SMALLINT NOT NULL, `bonus` SMALLINT NOT NULL, `faction_id` INT, `rank` TINYINT, `title` VARCHAR(16) NOT NULL, `money` INT)");
		tryCreate("CREATE TABLE f_land(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY_KEY(`id`), `world` VARCHAR(32) NOT NULL, `dim` TINYINT NOT NULL, `chunk_x` INT NOT NULL, UNIQUE(`chunk_x`), `chunk_z` INT NOT NULL, UNIQUE(`chunk_z`), `owner_id` INT)");
		tryCreate("CREATE TABLE f_relations(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY_KEY(`id`), `fac_one` INT NOT NULL, `fac_two` INT NOT NULL, `type` TINYINT)");
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
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * from `factions` WHERE `id` = ?");
			ps.setInt(1, id);
			
			ResultSet result = ps.executeQuery();
			if(result.next()) {
				PreparedStatement ps2 = conn.prepareStatement("SELECT `name` FROM `f_members` WHERE `faction_id` = ? AND `rank` = ?");
				ps2.setInt(1, id);
				ps2.setInt(2, Faction.PlayerRank.ADMIN.ordinal());
				ResultSet rs = ps2.executeQuery();
				rs.next();
				
				Location home = new Location(Canary.getServer().getWorld(result.getString("world")), result.getInt("home_x"), result.getInt("home_y"), result.getInt("home_z"), 0, 0);
				home.setType(DimensionType.fromId(result.getInt("home_dim")));
				return new CachedFaction(id, result.getString("name"), result.getString("desc"), result.getBoolean("open"), result.getBoolean("peaceful"), rs.getString("name"), home);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Faction[] getAllFactions() { // return some lazy stuff
		try {
			ResultSet rs = conn.prepareStatement("SELECT `id` from `factions`").executeQuery();
			ArrayList<LazyFaction> facs = new ArrayList<LazyFaction>();
			while(rs.next()) {
				facs.add(new LazyFaction(rs.getInt("id")));
			}
			return facs.toArray(new Faction[0]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Faction[0];
	}

	@Override
	public void fix() {
		// TODO Auto-generated method stub
	}

	@Override
	public gPlayer getPlayer(String name) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * from `f_members` WHERE `name` = ?");
			ps.setString(1, name);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				gPlayer gp = new gPlayer(rs.getString("name"), rs.getInt("power"));
				gp.bonusPower = rs.getInt("bonus");
				return gp;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		
	}

	@Override
	public void save(CachedFaction faction) {
		try {
			int fId = faction.getId();
			PreparedStatement test = conn.prepareStatement("SELECT `name` FROM `f_members` WHERE `faction_id` = ?");
			test.setInt(1, fId);
			ResultSet rs = test.executeQuery();
			
			PreparedStatement ps;
			int startIndex;
			if(rs.next()) { // faction already exists
				ArrayList<String> oldPlayers = new ArrayList<String>();
				do {
					oldPlayers.add(rs.getString("name"));
				} while (rs.next());
				for(String s : getRemoved(oldPlayers.toArray(new String[0]), faction.getAllMembers())) {
					PreparedStatement p = conn.prepareStatement("UPDATE `f_members` SET `faction_id` = NULL, `rank` = NULL, `title` = NULL WHERE `name` = ?");
					p.setString(1, s);
					p.execute();
				}
				
				ps = conn.prepareStatement("UPDATE `factions` SET `name` = ?, `desc` = ?, `open` = ?, `peaceful` = ?, `home_x` = ?, `home_y` = ?, `home_z` = ?, `home_dim` = ?, `world` = ? WHERE `id` = ?");
				startIndex = 1;
				ps.setInt(10, fId);
				ps.setInt(11, Utils.plugin.getEconomy().getBalance(faction));
			} else { // must create faction
				ps = conn.prepareStatement("INSERT INTO `factions` VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				startIndex = 2;
				ps.setInt(1, fId);
			}
			ps.setString(startIndex, faction.getName());
			ps.setString(startIndex + 1, faction.getDescription());
			ps.setBoolean(startIndex + 2, faction.isOpen());
			ps.setBoolean(startIndex + 3, faction.isPeaceful());
			Location home = faction.getHome();
			ps.setInt(startIndex + 4, home == null ? null : (int) home.getX());
			ps.setInt(startIndex + 5, home == null ? null : (int) home.getY());
			ps.setInt(startIndex + 6, home == null ? null : (int) home.getZ());
			ps.setInt(startIndex + 7, home == null ? null : (int) home.getType().getId());
			ps.setString(startIndex + 8, home == null ? null : home.getWorldName());
			
			ps.execute();
			
			setPlayers(new String[] {faction.getAdmin()}, Faction.PlayerRank.ADMIN, fId);
			setPlayers(faction.getMods(), Faction.PlayerRank.MODERATOR, fId);
			setPlayers(faction.getMembers(), Faction.PlayerRank.MEMBER, fId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void setPlayers(String[] players, Faction.PlayerRank rank, int fId) throws SQLException {
		for(String p : players) {
			PreparedStatement ps = conn.prepareStatement("UPDATE `f_members` SET `faction_id` = ?, `rank` = ? WHERE `name` = ?");
			ps.setInt(1, fId);
			ps.setInt(2, rank.ordinal());
			ps.setString(3, p);
			ps.execute();
		}
	}
	
	private String[] getRemoved(String[] one, String[] two) {
		ArrayList<String> removed = new ArrayList<String>();
		for(String o : one) {
			if(!Utils.arrayContains(two, o)) {
				removed.add(o);
			}
		}
		return removed.toArray(new String[0]);
	}

	@Override
	public void save(gPlayer[] players) {
		for(gPlayer gp : players) {
			try {
				String name = gp.getName();
				PreparedStatement test = conn.prepareStatement("SELECT `name` FROM `f_members` WHERE `name` = ?");
				test.setString(1, name);
				
				if(test.executeQuery().next()) { // player already exists
					PreparedStatement ps = conn.prepareStatement("UPDATE `f_members` SET `power` = ?, `bonus` = ?, `title` = ? WHERE `name` = ?");
					ps.setInt(1, gp.getRawPower());
					ps.setInt(2, gp.bonusPower);
					ps.setString(3, gp.getTitle());
					ps.setString(4, name);
					ps.execute();
				} else { // must create new player
					PreparedStatement ps = conn.prepareStatement("INSERT INTO `f_members` VALUES(?, ?, ?, ?, ?, ?)");
					ps.setString(1, name);
					ps.setInt(2, gp.getRawPower());
					ps.setInt(3, gp.bonusPower);
					ps.setString(6, gp.getTitle());
					Faction f = Utils.plugin.getFactionManager().getFaction(name);
					boolean isWild = f == null || f instanceof SpecialFaction;
					ps.setInt(4, isWild ? null : f.getId());
					ps.setInt(5, isWild ? null : f.getRank(name).ordinal());
					ps.execute();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void save(Relation[] relations) {
		for(Relation r : relations) {
			try {
				PreparedStatement test = conn.prepareStatement("SELECT `id` FROM `f_relations` WHERE `fac_one` = ? AND `fac_two` = ?");
				int id1 = r.getOne().getId();
				int id2 = r.getTwo().getId();
				test.setInt(1, id1);
				test.setInt(2, id2);
				ResultSet testr = test.executeQuery();
				
				if(testr.next()) { // relation already exists
					PreparedStatement ps = conn.prepareStatement("UPDATE `f_relations` SET `type` = ? WHERE `id` = ?");
					ps.setInt(1, r.type.ordinal());
					ps.setInt(2, testr.getInt("id"));
					ps.execute();
				} else { // must create new relation
					PreparedStatement ps = conn.prepareStatement("INSERT INTO `f_relations` VALUES(NULL, ?, ?, ?)");
					ps.setInt(1, id1);
					ps.setInt(2, id2);
					ps.setInt(3, r.type.ordinal());
					ps.execute();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void delete(Faction f) {
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM `factions` WHERE `id` = ?");
			ps.setInt(1, f.getId());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save(Land land) {
		try {
			String world = land.getWorld();
			int dim = land.getDimension();
			int x = land.getX();
			int z = land.getZ();
			
			PreparedStatement test = conn.prepareStatement("SELECT `id` FROM `f_land` WHERE `world` = ? AND `dim` = ? AND `chunk_x` = ? AND `chunk_z` = ?");
			test.setString(1, world);
			test.setInt(2, dim);
			test.setInt(3, x);
			test.setInt(4, z);
			ResultSet testr = test.executeQuery();
			
			if(testr.next()) { // land already exists
				PreparedStatement ps = conn.prepareStatement("UPDATE `f_land` SET `owner_id` = ? WHERE `id` = ?");
				ps.setInt(1, land.getClaimerId());
				ps.setInt(2, testr.getInt("id"));
			} else { // must create new land
				PreparedStatement ps = conn.prepareStatement("INSERT INTO `f_land` VALUES(NULL, ?, ?, ?, ?, ?)");
				ps.setString(1, world);
				ps.setInt(2, dim);
				ps.setInt(3, x);
				ps.setInt(4, z);
				ps.setInt(5, land.getClaimerId());
				ps.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Land[] getAllLand() {
		try {
			ResultSet rs = conn.prepareStatement("SELECT * FROM `f_land`").executeQuery();
			ArrayList<Land> rt = new ArrayList<Land>();
			while(rs.next()) {
				Land l = new Land(rs.getInt("chunk_x"), rs.getInt("chunk_z"), rs.getString("world"), rs.getInt("dim"));
				l.claim(rs.getInt("owner_id"));
				rt.add(l);
			}
			return rt.toArray(new Land[0]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Relation getRelation(Faction one, Faction two) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT `type` FROM `f_relations` WHERE `fac_one` = ? AND `fac_two` = ?");
			ps.setInt(1, one.getId());
			ps.setInt(2, two.getId());
			ResultSet rs = ps.executeQuery();
			return rs.next() ? new Relation(Relation.Type.values()[rs.getInt("type")], one, two) : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void delete(Land l) {
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM `f_land` WHERE `world` = ? AND `dim` = ? AND `chunk_x` = ? AND `chunk_z` = ?");
			ps.setString(1, l.getWorld());
			ps.setInt(2, l.getDimension());
			ps.setInt(3, l.getX());
			ps.setInt(4, l.getZ());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(Relation r) {
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM `f_relations` WHERE `fac_one` = ? AND `fac_two` = ?");
			ps.setInt(1, r.getOne().getId());
			ps.setInt(2, r.getTwo().getId());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getBalance(String player) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT `money` FROM `f_members` WHERE `name` = ?");
			ps.setString(1, player);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getInt("money") : 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getBalance(int fID) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT `money` FROM `factions` WHERE `id` = ?");
			ps.setInt(1, fID);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getInt("money") : 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void savePlayerBalances(HashMap<String, Integer> players) {
		for(Entry<String, Integer> e : players.entrySet()) {
			try {
				PreparedStatement ps = conn.prepareStatement("UPDATE `f_members` SET `money` = ? WHERE `name` = ?");
				ps.setInt(1, e.getValue());
				ps.setString(2, e.getKey());
				ps.execute();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void saveFactionBalances(HashMap<Integer, Integer> factions) {
		for(Entry<Integer, Integer> e : factions.entrySet()) {
			try {
				PreparedStatement ps = conn.prepareStatement("UPDATE `factions` SET `money` = ? WHERE `id` = ?");
				ps.setInt(1, e.getValue());
				ps.setInt(2, e.getKey());
				ps.execute();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public Relation[] getRelationsWith(Faction f) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM `f_relations` WHERE `fac_one` = ? OR `fac_two` = ?");
			int id = f.getId();
			ps.setInt(1, id);
			ps.setInt(2, id);
			ResultSet rs = ps.executeQuery();
			ArrayList<Relation> rt = new ArrayList<Relation>();
			FactionManager fm = Utils.plugin.getFactionManager();
			while(rs.next()) {
				rt.add(new Relation(Relation.Type.values()[rs.getInt("type")], fm.getFaction(rs.getInt("one")), fm.getFaction(rs.getInt("two"))));
			}
			return rt.toArray(new Relation[0]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
