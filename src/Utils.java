import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;


public class Utils {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static FactionManager fManager;
	
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
	 * Returns a green 'yes' if the boolean is true, and a red 'no' if it's not.
	 * 
	 * @param b The boolean to read.
	 * @return String
	 */
	public static String readBool(boolean b) {
		return b ? Colors.Green + "Yes" : Colors.Red + "No";
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
}
