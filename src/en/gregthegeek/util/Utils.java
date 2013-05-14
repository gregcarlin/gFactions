package en.gregthegeek.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.chat.TextFormat;


import en.gregthegeek.gfactions.FactionCommand;
import en.gregthegeek.gfactions.gFactions;
import en.gregthegeek.gfactions.db.DatasourceException;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.gfactions.faction.SpecialFaction;
import en.gregthegeek.gfactions.relation.Relation;
import en.gregthegeek.gfactions.relation.RelationManager;


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
		return String.format("%s%s", TextFormat.LIGHT_RED, s);
	}
	
	/**
	 * Returns the given string colored rose with extra formatting.
	 * 
	 * @param s The string to color and format.
	 * @param objs The formatting options.
	 * @return String
	 */
	public static String rose(String s, Object... objs) {
		return String.format(TextFormat.LIGHT_RED + s, objs); //ehh
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
				mr.message(s);
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
		return b ? TextFormat.GREEN + yes : TextFormat.RED + no;
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
	 * Returns a file as a list of its lines.
	 * 
	 * @param path The path to the file.
	 * @return String[]
	 * @throws DatasourceException
	 */
	public static List<String> readFile(String path) throws DatasourceException {
		try {
			File file = new File(path);
			if(!file.exists()) {
				file.createNewFile();
				return new ArrayList<String>(0);
			}
			
			BufferedReader factionReader = new BufferedReader(new FileReader(file));
			String line = null;
			ArrayList<String> lines = new ArrayList<String>();
			while((line = factionReader.readLine()) != null) {
				lines.add(line);
			}
			factionReader.close();
			
			return lines;
		} catch (IOException e) {
			throw new DatasourceException(e);
		}
	}
	
	/**
	 * Writes the data in data to a file at the indicated path. All previous data will be overwritten.
	 * 
	 * @param path The path of the file to write to.
	 * @param data The data to write into the file.
	 * @throws DatasourceException 
	 */
	public static void writeFile(String path, List<String> data) throws DatasourceException {
	    try {
	        File file = new File(path);
	        if(!file.exists()) file.createNewFile();
	        
	        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	        for(String s : data) {
	            writer.write(s);
	            writer.newLine();
	        }
	        writer.close();
	        
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
			return ((Player) mr).hasPermission(cmd);
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
		} else if(((Player) player).hasPermission("gfactions.admin")) {
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
	    System.out.printf("Utils.java: plugin=%s%n", plugin);
	    System.out.printf("Utils.java: fManager=%s%n", plugin.getFactionManager());
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
	
	/**
	 * Returns the distance between two locations.
	 * Why was this removed from Location?
	 * 
	 * @param one The first location.
	 * @param two The second location.
	 * @return The distance.
	 */
	public static double distance(Location one, Location two) {
	    double x = one.getX() - two.getX();
	    double y = one.getY() - two.getY();
	    double z = one.getZ() - two.getZ();
	    return Math.sqrt(x * x + y * y + z * z);
	}
}
