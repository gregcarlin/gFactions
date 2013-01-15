import java.io.File;
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
	public FactionManager fManager;
	
	public Config() {
		new File(FOLDER).mkdirs();
		
		HashMap<String, Object> defaults = new HashMap<String, Object>();
		
		defaults.put("data-source", "oodb");
		defaults.put("start-power", new Integer(10));
		defaults.put("faction-open-by-default", new Boolean(false));
		defaults.put("default-faction-desc", "Default faction description :(");
		
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
		if(data.equals("oodb") || data.equals("db4o")) {
			return new OODBSource();
		} else if(data.equals("file") || data.equals("flat-file")) {
			return new FileSource(fManager);
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
}
