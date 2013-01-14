import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
		for(String s : msgs) {
			mr.notify(s);
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
	 * Returns a green 'Yes' if the boolean is true, and a red 'No' if it's not.
	 * 
	 * @param b The boolean to read.
	 * @return String
	 */
	public static String readBool(boolean b) {
		return b ? Colors.Green + "Yes" : Colors.Red + "No";
	}
	
	/**
	 * Returns a green 'ON' if the boolean is true, and a red 'OFF' if it's not.
	 * 
	 * @param b The boolean to read.
	 * @return String
	 */
	public static String readBoolS(boolean b) {
		return b ? Colors.Green + "ON" : Colors.Red + "OFF";
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
		if(!(player instanceof Player) || ((Player) player).canUseCommand("/fadmin")) {
			return FactionCommand.CommandUsageRank.SERVER_ADMIN;
		}
		String pName = player.getName();
		Faction faction = plugin.getFactionManager().getFaction(pName);
		if(faction == null) {
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
		FactionManager fManager = plugin.getFactionManager();
		fManager.save();
    	fManager.par.getPlayerManager().save();
    	fManager.par.getRelationManager().save();
	}
}
