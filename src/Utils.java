import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public abstract class Utils {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static gFactions plugin;
	
	/**
	 * Returns the given string colored rose.
	 * 
	 * @param s The string to color.
	 * @return String
	 */
	public static String rose(String s) {
		return String.format("%s%s", Colors.Rose, s);
	}
	
	/**
	 * Returns the given string colored rose with extra formatting.
	 * 
	 * @param s The string to color and format.
	 * @param objs The formatting options.
	 * @return String
	 */
	public static String rose(String s, Object... objs) {
		return String.format(Colors.Rose + s, objs); //ehh
	}
	
	/**
	 * Sends an array of messages to a MessageReceiver.
	 * 
	 * @param mr The MessageReceiver to send to.
	 * @param msgs The messages to send.
	 */
	public static void sendMsgs(MessageReceiver mr, String[] msgs) {
		if(msgs == null) {
			return;
		}
		if(mr instanceof Player) { // because notify adds a Colors.Rose for some reason.
			Player p = (Player) mr;
			for(String s : msgs) {
				p.sendMessage(s);
			}
		} else {
			for(String s : msgs) {
				mr.notify(s);
			}
		}
	}
	
	/**
	 * Logs a warning to the server log.
	 * 
	 * @param msg The message to log.
	 * @param objs Formatting options for the message.
	 */
	public static void warning(String msg, Object... objs) {
		log.warning(String.format(msg, objs));
	}
	
	/**
	 * Returns the yes value in green if true, and the no value in red if it's not.
	 * 
	 * @param b The boolean to read.
	 * @param yes The value to return if true.
	 * @param no The value to return if false.
	 * @return String
	 */
	public static String readBool(boolean b, String yes, String no) {
		return b ? Colors.Green + yes : Colors.Red + no;
	}
	
	/**
	 * Returns whether or not a given array contains a given object.
	 * 
	 * @param arr The array to check.
	 * @param o The object to search for.
	 * @return boolean
	 */
	public static boolean arrayContains(Object[] arr, Object o) {
		for(Object obj : arr) {
			if(o.equals(obj)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a file as an array of its lines.
	 * 
	 * @param path The path to the file.
	 * @return String[]
	 * @throws DatasourceException
	 */
	public static String[] readFile(String path) throws DatasourceException {
		try {
			File file = new File(path);
			if(!file.exists()) {
				file.createNewFile();
				return new String[0];
			}
			BufferedReader factionReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
			String line = null;
			ArrayList<String> lines = new ArrayList<String>();
			while((line = factionReader.readLine()) != null) {
				lines.add(line);
			}
			factionReader.close();
			return lines.toArray(new String[0]);
		} catch (FileNotFoundException e) {
			throw new DatasourceException(e);
		} catch (IOException e) {
			throw new DatasourceException(e);
		}
	}
	
	/**
	 * Returns whether or not this messagereceiver has permission to use this command.
	 * 
	 * @param mr The messagereceiver to check.
	 * @param cmd The command to check.
	 * @return boolean
	 */
	public static boolean permCheck(MessageReceiver mr, String cmd) {
		if(mr instanceof Player) {
			return ((Player) mr).canUseCommand(cmd);
		}
		return true;
	}
	
	/**
	 * Returns the commandusagerank of a messagereceiver
	 * 
	 * @param player The messagereceiver to check.
	 * @return FactionCommand.CommandUsageRank
	 */
	public static FactionCommand.CommandUsageRank getCommandRank(MessageReceiver player) {
		if(!(player instanceof Player)) {
			return FactionCommand.CommandUsageRank.NO_FACTION;
		} else if(((Player) player).canUseCommand("/fadmin")) {
			return FactionCommand.CommandUsageRank.SERVER_ADMIN;
		}
		String pName = player.getName();
		Faction faction = plugin.getFactionManager().getFaction(pName);
		if(faction == null || faction instanceof SpecialFaction) {
			return FactionCommand.CommandUsageRank.NO_FACTION;
		}
		return faction.getRank(pName).getCommandRank();
	}
	
	/**
	 * Returns part of a given string array.
	 * 
	 * @param arr The array to trim.
	 * @param start The starting point of the trim.
	 * @return String[]
	 */
	public static String[] trim(String[] arr, int start) {
		String[] rt = new String[arr.length - start];
		for(int i=0; i<rt.length; i++) {
			rt[i] = arr[i + start];
		}
		return rt;
	}
	
	/**
	 * Saves all data.
	 */
	public static void saveAll() {
		plugin.getFactionManager().save();
    	plugin.getPlayerManager().save();
    	plugin.getRelationManager().save();
    	plugin.getLandManager().save();
    	plugin.getEconomy().save();
	}
	
	/**
	 * Adds all the items in an array to an ArrayList.
	 * 
	 * @param from The array to add items from.
	 * @param to The ArrayList to add items to.
	 */
	public static <T> void addItems(T[] from, ArrayList<T> to) {
		for(T obj : from) {
			to.add(obj);
		}
	}
	
	/**
	 * Returns whether or not this player is in admin bypass mode.
	 * 
	 * @param p The player to check.
	 * @return boolean
	 */
	public static boolean isBypass(Player p) {
		return plugin.getPlayerManager().getPlayer(p.getName()).adminBypass;
	}
	
	/**
	 * Returns an array of the currently online players, sorted by relation.
	 * 
	 * @param basedOn Players are sorted by their relation to this faction.
	 * @return Player[][] - [0] = neutral, [1] = allies, [2] = enemies
	 */
	public static Player[][] getOnlinePlayersSorted(Faction basedOn) {
		RelationManager rManager = plugin.getRelationManager();
		
		ArrayList<Player> allies = new ArrayList<Player>();
		for(Faction f : rManager.getRelations(basedOn, Relation.Type.ALLY)) {
			for(Player p : f.getOnlineMembers()) {
				allies.add(p);
			}
		}
		
		ArrayList<Player> enemies = new ArrayList<Player>();
		for(Faction f : rManager.getRelations(basedOn, Relation.Type.ENEMY)) {
			for(Player p : f.getOnlineMembers()) {
				enemies.add(p);
			}
		}
		
		List<Player> neutral = Arrays.asList(plugin.getFactionManager().getFaction(-1).getOnlineMembers());
		for(Faction f : rManager.getRelations(basedOn, Relation.Type.NEUTRAL)) {
			for(Player p : f.getOnlineMembers()) {
				neutral.add(p);
			}
		}
		
		return new Player[][] {neutral.toArray(new Player[0]), allies.toArray(new Player[0]), enemies.toArray(new Player[0])};
	}
}
