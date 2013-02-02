import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
//import java.util.HashMap;
//import java.util.Map.Entry;

/**
 * Manages the plugin's configuration.
 * 
 * @author gregthegeek
 *
 */
public class Config {
	public static final String FOLDER = etc.getInstance().getConfigFolder() + "gFactions/";
	private final AdvancedPropertiesFile props = getProps(FOLDER + "config.txt");
	
	private enum DataSourceEnum {
		OODB,
		DB4O,
		FILE,
		FLAT_FILE,
		SQL,
		MYSQL;
	}
	
	private enum EconomyEnum {
		NONE,
		INTEGRATED,
		BUILT_IN,
		DCONOMY,
		EXTERNAL;
	}
	
	public Config() {
		new File(FOLDER).mkdirs();
		
		if(props == null) {
			return;
		}
		
		if(props.getHeader() == null) {
			props.setHeader("Main configuration file for gFactions.");
		}
		
		props.getEnum("data-source", DataSourceEnum.OODB, "Available options are OODB, DB4O, FILE, FLAT-FILE, SQL, and MYSQL.");
		props.getInt("start-power", 10, "The power new players are given when they join the server.");
		props.getBoolean("faction-open-by-default", false, "Whether or not new factions allow anyone to join them.");
		props.getString("default-faction-desc", "Default faction description", "The description new factions are set to.");
		props.getInt("save-interval", -1, "The seconds in between server saves. Values <0 will auto save whenever changes occur.");
		props.getInt("power-regen-interval", 300, "The amount of seconds it takes for a player to regain one power.");
		props.getEnum("economy", EconomyEnum.NONE, "Available options are NONE, INTEGRATED, BUILT_IN, DCONOMY, and EXTERNAL.");
		props.getInt("power-loss-on-death", 4, "The amount of power lost by a player when he dies.");
		props.getInt("power-loss-on-death-warzone", 0, "The amount of power lost by a player when he dies in a war zone.");
		props.getInt("home-land-dmg-reduce", 30, "The percentage damage is reduced by when attacked by another player while in owned territory.");
		props.getBoolean("f-home-on-death", true, "Whether or not players respawn at their factions' homes.");
		props.getInt("start-money", 10, "The amount of money new players are given when they join the server. Only used with built-in economy.");
		props.getInt("start-money-faction", 0, "The amount of money new factions are given when they are first created.");
		
		try {
			props.save();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Returns the datasource set by config.txt
	 * 
	 * @return Datasource
	 * @throws DatasourceException
	 */
	public Datasource getDataSource() throws DatasourceException {
		switch(props.getEnum("data-source", DataSourceEnum.class)) {
		case OODB:
		case DB4O:
			try {
				((MyClassLoader) Utils.plugin.getClass().getClassLoader()).addURL(new File("db4o.jar").toURI().toURL());
				return new OODBSource();
			} catch (MalformedURLException e) {
				throw new DatasourceException(e);
			}
		case FILE:
		case FLAT_FILE:
			return new FileSource();
		case SQL:
		case MYSQL:
			return new SQLSource();
		default:
			throw new DatasourceException("Error retrieving datasource!");
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
		switch(props.getEnum("economy", EconomyEnum.class)) {
		case INTEGRATED:
		case BUILT_IN:
			return new IntegratedEconomy();
		case DCONOMY:
		case EXTERNAL:
			return new ExternalEconomy();
		default:
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
	 * @return boolean
	 */
	public boolean factionHomeOnDeath() {
		return props.getBoolean("f-home-on-death");
	}
	
	/**
	 * Returns the money new players start with.
	 * 
	 * @return int
	 */
	public int getStartMoney() {
		return props.getInt("start-money");
	}
	
	private static AdvancedPropertiesFile getProps(String path) {
		try {
			return new AdvancedPropertiesFile(path);
		} catch (IOException e) {
			return null;
		}
	}
}
