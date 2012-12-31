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
		
		defaults.put("use-sql", new Boolean(false));
		defaults.put("start-power", new Integer(10));
		
		for(Entry<String, Object> e : defaults.entrySet()) {
			String key = e.getKey();
			if(!props.containsKey(key)) {
				Object val = e.getValue();
				if(val instanceof Boolean) {
					props.setBoolean(key, (Boolean) val);
				} else if(val instanceof Integer) {
					props.setInt(key, (Integer) val);
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
		return props.getBoolean("use-sql") ? new SQLSource() : new FileSource(fManager);
	}
	
	/**
	 * Returns the power level of new players.
	 * 
	 * @return int
	 */
	public int getStartPower() {
		return props.getInt("start-power");
	}
}
