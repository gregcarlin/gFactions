package en.gregthegeek.gfactions;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import en.gregthegeek.gfactions.db.Datasource;
import en.gregthegeek.gfactions.db.DatasourceException;
import en.gregthegeek.gfactions.db.FileSource;
import en.gregthegeek.gfactions.db.OODBSource;
import en.gregthegeek.gfactions.db.SQLSource;
import en.gregthegeek.gfactions.economy.Economy;
import en.gregthegeek.gfactions.economy.InactiveEconomy;
import en.gregthegeek.gfactions.economy.IntegratedEconomy;
import en.gregthegeek.util.AdvancedPropertiesFile;
import en.gregthegeek.util.Utils;

/**
 * Manages the plugin's configuration.
 * 
 * @author gregthegeek
 *
 */
public class Config {
	public static final String FOLDER = "config/gFactions/";
	private final AdvancedPropertiesFile props = getProps(FOLDER + "config.txt");
	private final AdvancedPropertiesFile prices = getProps(FOLDER + "prices.txt");
	
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
		
		props.getEnum("data-source", DataSourceEnum.OODB, "Available options are OODB and DB4O. Flatfile and SQL support is planned.");
		props.getInt("start-power", 10, "The power new players are given when they join the server.");
		props.getBoolean("faction-open-by-default", false, "Whether or not new factions allow anyone to join them.");
		props.getString("default-faction-desc", "Default faction description", "The description new factions are set to.");
		props.getInt("save-interval", -1, "The seconds in between server saves. Values <0 will auto save whenever changes occur.");
		props.getInt("power-regen-interval", 300, "The amount of seconds it takes for a player to regain one power.");
		EconomyEnum e = props.getEnum("economy", EconomyEnum.NONE, "Available options are NONE, INTEGRATED, and BUILT_IN. dConomy 3 support is planned.");
		props.getInt("power-loss-on-death", 4, "The amount of power lost by a player when he dies.");
		props.getInt("power-loss-on-death-warzone", 0, "The amount of power lost by a player when he dies in a war zone.");
		props.getInt("home-land-dmg-reduce", 30, "The percentage damage is reduced by when attacked by another player while in owned territory.");
		props.getBoolean("f-home-on-death", true, "Whether or not players respawn at their factions' homes.");
		props.getInt("start-money", 10, "The amount of money new players are given when they join the server. Only used with built-in economy.");
		props.getInt("start-money-faction", 0, "The amount of money new factions are given when they are first created.");
		props.getBoolean("tags-in-chat", true, "Whether or not public chat shows faction tags.");
		props.getInt("no-tp-enemy-dist", 32, "Enemies must be this distance away or more in order to teleport.");
		props.getInt("max_power", 10, "The maximum power level players can attain.");
		
		try {
			props.save();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(prices == null || e == null || e == EconomyEnum.NONE) {
			return; // only populate prices file if economy is on
		}
		
		prices.getInt("claim-from-wild", 30, "The cost of claiming wild territory.");
		prices.getInt("claim-from-fac", 0, "The cost of claiming faction owned territory.");
		prices.getDouble("claim-mulitplier", 0.5, "For each piece of land claimed, the cost is increased by (original_cost * multiplier).");
		prices.getInt("claim-refund", 70, "The percentage of claiming cost is returned for unclaimed land. 100 = full refund.");
		prices.getInt("f-create", 100, "The amount of money required to start a faction.");
		prices.getInt("f-set-home", 30, "The cost of using /f sethome.");
		prices.getInt("f-join", 0, "The cost of joining a faction.");
		prices.getInt("f-leave", 0, "The cost of leaving a faction.");
		prices.getInt("f-kick", 0, "The cost of kicking someone from the faction.");
		prices.getInt("f-invite", 0, "The cost of inviting someone to the faction.");
		prices.getInt("f-home", 0, "The cost of using /f home.");
		prices.getInt("f-rename", 0, "The cost of renaming a faction.");
		prices.getInt("f-desc", 0, "The cost of setting the faction description.");
		prices.getInt("title", 0, "The cost of setting a player's title.");
		prices.getInt("f-open", 0, "The cost of opening a faction.");
		prices.getInt("f-close", 0, "The cost of closing a faction.");
		prices.getInt("ally", 0, "The cost of allying another faction.");
		prices.getInt("enemy", 0, "The cost of enemying another faction.");
		prices.getInt("neutral", 0, "The cost of becoming neutral with another faction.");
		
		try {
			prices.save();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Returns the datasource set by config.txt.
	 * Should only be used once per start up.
	 * 
	 * @return Datasource
	 * @throws DatasourceException
	 */
	public Datasource getDataSource() throws DatasourceException {
		switch(props.getEnum("data-source", DataSourceEnum.class)) {
		case OODB:
		case DB4O:
			try {
				((net.canarymod.plugin.CanaryClassLoader) Utils.plugin.getClass().getClassLoader()).addURL(new File("db4o.jar").toURI().toURL());
				return new OODBSource();
			} catch (MalformedURLException e) {
				throw new DatasourceException(e);
			}
		case FILE:
		case FLAT_FILE:
			return new FileSource();
		case SQL:
		case MYSQL:
			try {
				return new SQLSource();
			} catch (SQLException e) {
				throw new DatasourceException(e);
			}
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
			Utils.warning("dConomy not yet supported!");
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
	
	/**
	 * Whether or not faction tags appear in public chat.
	 * 
	 * @return boolean
	 */
	public boolean tagsInChat() {
		return props.getBoolean("tags-in-chat");
	}
	
	/**
	 * The distance enemies must be in order to teleport.
	 * 
	 * @return int
	 */
	public int getMinEnemyDist() {
		return props.getInt("no-tp-enemy-dist");
	}
	
	/**
	 * The maximum power players can attain.
	 * 
	 * @return int
	 */
	public int getMaxPower() {
		return props.getInt("max_power");
	}
	
	// start prices
	
	/**
	 * Returns the cost of claiming some land.
	 * 
	 * @param wild Whether or not the land being claimed is wilderness.
	 * @param landAmt The amount of land owned by the faction before claiming this piece.
	 * @return int
	 */
	public int getClaimCost(boolean wild, int landAmt) {
		int i = prices.getInt(wild ? "claim-from-wild" : "claim-from-fac");
		i += prices.getDouble("claim-mulitplier") * i * landAmt;
		return i;
	}
	
	/**
	 * Returns the amount of money gained when a piece of land is unclaimed.
	 * 
	 * @return int
	 */
	public int getLandRefund() {
		return prices.getInt("claim-refund") * prices.getInt("claim-from-wild") / 100;
	}
	
	/**
	 * Returns the cost of creating a faction.
	 * 
	 * @return int
	 */
	public int getFactionCreateCost() {
		return prices.getInt("f-create");
	}
	
	/**
	 * Returns the cost of setting the faction home.
	 * 
	 * @return int
	 */
	public int getSetHomeCost() {
		return prices.getInt("f-set-home");
	}
	
	/**
	 * Returns the cost of joining a faction.
	 * 
	 * @return int
	 */
	public int getJoinCost() {
		return prices.getInt("f-join");
	}
	
	/**
	 * Returns the cost of leaving a faction.
	 * 
	 * @return int
	 */
	public int getLeaveCost() {
		return prices.getInt("f-leave");
	}
	
	/**
	 * Returns the cost of kicking a player from a faction.
	 * 
	 * @return int
	 */
	public int getKickCost() {
		return prices.getInt("f-kick");
	}
	
	/**
	 * Returns the cost of inviting a player to a faction.
	 * 
	 * @return int
	 */
	public int getInviteCost() {
		return prices.getInt("f-invite");
	}
	
	/**
	 * Returns the cost of using /f home
	 * 
	 * @return int
	 */
	public int getTpHomeCost() {
		return prices.getInt("f-home");
	}
	
	/**
	 * Returns the cost of renaming the faction.
	 * 
	 * @return int
	 */
	public int getRenameCost() {
		return prices.getInt("f-rename");
	}
	
	/**
	 * Returns the cost of setting the faction description.
	 * 
	 * @return int
	 */
	public int getDescCost() {
		return prices.getInt("f-desc");
	}
	
	/**
	 * Returns the cost of setting the faction title.
	 * 
	 * @return int
	 */
	public int getTitleCost() {
		return prices.getInt("title");
	}
	
	/**
	 * Returns the cost of using /f open.
	 * 
	 * @return int
	 */
	public int getOpenCost() {
		return prices.getInt("f-open");
	}
	
	/**
	 * Returns the cost of using /f close.
	 * 
	 * @return int
	 */
	public int getCloseCost() {
		return prices.getInt("f-close");
	}
	
	/**
	 * Returns the cost of allying another faction.
	 * 
	 * @return int
	 */
	public int getAllyCost() {
		return prices.getInt("ally");
	}
	
	/**
	 * Returns the cost of enemying another faction.
	 * 
	 * @return int
	 */
	public int getEnemyCost() {
		return prices.getInt("enemy");
	}
	
	/**
	 * Returns the cost of neutraling another faction.
	 * 
	 * @return int
	 */
	public int getNeutralCost() {
		return prices.getInt("neutral");
	}
	
	private static AdvancedPropertiesFile getProps(String path) {
		try {
			return new AdvancedPropertiesFile(path);
		} catch (IOException e) {
			Utils.warning("Error getting settings at %s!", path);
			return null;
		}
	}
}
