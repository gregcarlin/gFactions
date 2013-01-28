import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Manages the plugin's configuration.
 * 
 * @author gregthegeek
 *
 */
public class Config {
	public static final String FOLDER = etc.getInstance().getConfigFolder() + "gFactions/";
	private final PropertiesFile props = new PropertiesFile(FOLDER + "config.txt");
	
	public Config() {
		new File(FOLDER).mkdirs();
		
		HashMap<String, Object> defaults = new HashMap<String, Object>();
		
		defaults.put("data-source", "db4o"); // available options: oodb,db4o,file,flat-file,sql,mysql
		defaults.put("start-power", new Integer(10));
		defaults.put("faction-open-by-default", new Boolean(false));
		defaults.put("default-faction-desc", "Default faction description :(");
		defaults.put("save-interval", new Integer(60)); // seconds
		defaults.put("power-regen-interval", new Integer(300)); // seconds
		defaults.put("economy", "none"); // available options: none,integrated,built-in,dconomy,external
		defaults.put("power-loss-on-death", new Integer(4));
		defaults.put("power-loss-on-death-warzone", new Integer(0));
		defaults.put("home-land-dmg-reduce", new Integer(30)); // percentage, 30 = 30% reduction
		defaults.put("f-home-on-death", new Boolean(true));
		
		for(Entry<String, Object> e : defaults.entrySet()) {
			String key = e.getKey();
			if(!props.containsKey(key)) {
				Object val = e.getValue();
				if(val instanceof Boolean) {
					props.setBoolean(key, (Boolean) val);
				} else if(val instanceof Integer) {
					props.setInt(key, (Integer) val);
				} else if(val instanceof String) {
					props.setString(key, (String) val);
				} else {
					Utils.warning("Unknown data type found in defaults. PM gregthegeek, he's retarted.");
				}
			}
		}
	}
	
	/**
	 * Returns the datasource set by config.txt
	 * 
	 * @return Datasource
	 * @throws DatasourceException
	 */
	public Datasource getDataSource() throws DatasourceException {
		String data = props.getString("data-source").toLowerCase();
		if(data.equals("db4o") || data.equals("oodb")) {
			try {
				((MyClassLoader) Utils.plugin.getClass().getClassLoader()).addURL(new File("db4o.jar").toURI().toURL());
				return new OODBSource();
			} catch (MalformedURLException e) {
				throw new DatasourceException(e);
			}
		} else if(data.equals("file") || data.equals("flat-file")) {
			return new FileSource();
		} else if(data.equals("sql") || data.equals("mysql")) {
			return new SQLSource();
		} else {
			throw new DatasourceException("%s is an invalid datasource!", data);
		}
	}
	
	/**
	 * Returns the power level of new players.
	 * 
	 * @return int
	 */
	public int getStartPower() {
		return props.getInt("start-power");
	}
	
	/**
	 * Returns whether or not newly created factions allow anyone to join.
	 * 
	 * @return boolean
	 */
	public boolean isDefaultFactionOpen() {
		return props.getBoolean("faction-open-by-default");
	}
	
	/**
	 * Returns the default faction description.
	 * 
	 * @return String
	 */
	public String getDefaultFactionDesc() {
		return props.getString("default-faction-desc");
	}
	
	/**
	 * Returns the save interval in milliseconds.
	 * 0 will not autosave.
	 * <0 will save on change.
	 * 
	 * @return int
	 */
	public int getSaveInterval() {
		return props.getInt("save-interval") * 1000;
	}
	
	/**
	 * Returns the power regeneration interval in milliseconds.
	 * 
	 * @return int
	 */
	public int getPowerRegenInterval() {
		return props.getInt("power-regen-interval") * 1000;
	}
	
	/**
	 * Returns an interface to the server economy.
	 * 
	 * @return Economy
	 */
	public Economy getEconomy() {
		String s = props.getString("economy").toLowerCase();
		if(s.equals("integrated") || s.equals("built-in")) {
			return new IntegratedEconomy();
		} else if(s.equals("dconomy") || s.equals("external")) {
			return new ExternalEconomy();
		} else {
			return new InactiveEconomy();
		}
	}
	
	/**
	 * Returns the amount of power lost when a player dies outside of a war or safe zone.
	 * 
	 * @return int
	 */
	public int getPowerLossOnDeath() {
		return props.getInt("power-loss-on-death");
	}
	
	/**
	 * Returns the amount of power lost when a player dies in a warzone.
	 * 
	 * @return int
	 */
	public int getPowerLossOnDeathWarzone() {
		return props.getInt("power-loss-on-death-warzone");
	}
	
	/**
	 * Returns the home land damage multiplier.
	 * 
	 * @return double
	 */
	public double getHomeLandDamageReduction() {
		return ((double) props.getInt("home-land-dmg-reduce")) / 100d;
	}
	
	/**
	 * Returns whether or not players should respawn at their faction homes.
	 * 
	 * @return
	 */
	public boolean factionHomeOnDeath() {
		return props.getBoolean("f-home-on-death");
	}
}
