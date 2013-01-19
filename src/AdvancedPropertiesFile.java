import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Alternative to a PropertiesFile with added features. Methods are similarly named.
 * 
 * @author gregthegeek
 *
 */
public class AdvancedPropertiesFile {
	private final Logger log = Logger.getLogger("Minecraft");
	private final String filePath;
	private final HashMap<String, Property> map = new HashMap<String, Property>();
	public String header;
	
	public AdvancedPropertiesFile(String filePath) throws IOException {
		this.filePath = filePath;
		
		File base = new File(filePath);
		if(base.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(base));
			ArrayList<String> lines = new ArrayList<String>();
			String line;
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			
			int size = lines.size();
			boolean justComment = false;
			for(int i=0; i<size; i++) {
				String l = lines.get(i);
				if(i == 0 && l.startsWith("##")) { // allows a header at the top of the file
					header = l.substring(1);
					continue;
				} else if(l.startsWith("#")) { // ignore comments at first
					justComment = true;
					continue;
				}
				
				String[] split = l.split(":");
				if(split.length < 2) { // skip malformed settings
					log.warning(String.format("Error reading line %d in %s.", i + 1, filePath));
					justComment = false;
					continue;
				}
				
				if(justComment) {
					map.put(split[0], new Property(split[1], lines.get(i-1).substring(1)));
					justComment = false;
				} else {
					map.put(split[0], new Property(split[1]));
				}
			}
		} else {
			base.createNewFile();
		}
	}
	
	/**
	 * Saves all data to file.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		File base = new File(filePath);
		BufferedWriter writer = new BufferedWriter(new FileWriter(base));
		if(header != null) {
			writer.append(String.format("#%s\n", header));
		}
		for(Entry<String, Property> e : map.entrySet()) {
			Property p = e.getValue();
			String comment = p.getComment();
			if(comment != null) {
				writer.append(String.format("##%s\n", header));
			}
			writer.append(String.format("%s:%s\n", e.getKey(), p.valueAsString()));
		}
		writer.close();
	}
	
	/**
	 * Returns a clone of the map used as data for this file.
	 * 
	 * @return Map<String, Property>
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Property> returnMap() {
		return (Map<String, Property>) map.clone();
	}
	
	/**
	 * Returns whether or not this properties file has the given key.
	 * 
	 * @param key The key to check.
	 * @return boolean
	 */
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}
	
	/**
	 * Removes a key-value pair from this file.
	 * 
	 * @param key The key of the pair to remove.
	 */
	public void removeKey(String key) {
		map.remove(key);
	}
	
	/**
	 * Returns the value associated with the given key.
	 * Null if the key does not exist.
	 * 
	 * @param key The key of the value to return.
	 * @return Property
	 */
	public Property getProperty(String key) {
		return map.get(key);
	}
	
	/**
	 * Retrieves a property that has been saved.
	 * 
	 * @param key The key of the property to retrieve.
	 * @param defaultVal If the key does not exist, it will be set to this and returned.
	 * @return Property
	 */
	public Property getProperty(String key, Property defaultVal) {
		Property val = getProperty(key);
		if(val != null) {
			return val;
		}
		
		setProperty(key, defaultVal);
		return defaultVal;
	}
	
	/**
	 * Retrieves a property that has been saved.
	 * 
	 * @param key The key of the property to retrieve.
	 * @param defaultVal If the key does not exist, it will be set to this and returned.
	 * @return Property
	 */
	public Property getProperty(String key, String defaultVal) {
		return getProperty(key, new Property(defaultVal));
	}
	
	/**
	 * Retrieves a property that has been saved.
	 * 
	 * @param key The key of the property to retrieve.
	 * @param defaultVal If the key does not exist, it will be set to this and returned.
	 * @param defaultComment If the key does not exist, the new property will have this comment.
	 * @return Property
	 */
	public Property getProperty(String key, String defaultVal, String defaultComment) {
		return getProperty(key, new Property(defaultVal, defaultComment));
	}
	
	/**
	 * Sets a property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The property to set.
	 */
	public void setProperty(String key, Property value) {
		map.put(key, value);
	}
	
	/**
	 * Sets a property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setProperty(String key, String value) {
		setProperty(key, new Property(value));
	}
	
	/**
	 * Sets a property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setProperty(String key, String value, String comment) {
		setProperty(key, new Property(value, comment));
	}
	
	/**
	 * Sets a string property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setString(String key, String value) {
		setProperty(key, value);
	}
	
	/**
	 * Sets a string property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setString(String key, String value, String comment) {
		setProperty(key, value, comment);
	}
	
	/**
	 * Sets a numerical property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setNumber(String key, Number value) {
		setProperty(key, value.toString());
	}
	
	/**
	 * Sets a numerical property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setNumber(String key, Number value, String comment) {
		setProperty(key, value.toString(), comment);
	}
	
	/**
	 * Sets an integer property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setInt(String key, Integer value) {
		setNumber(key, value);
	}
	
	/**
	 * Sets an integer property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setInt(String key, Integer value, String comment) {
		setNumber(key, value, comment);
	}
	
	/**
	 * Sets a double property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setDouble(String key, Double value) {
		setNumber(key, value);
	}
	
	/**
	 * Sets a double property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setDouble(String key, Double value, String comment) {
		setNumber(key, value, comment);
	}
	
	/**
	 * Sets a long property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setLong(String key, Long value) {
		setNumber(key, value);
	}
	
	/**
	 * Sets a long property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setLong(String key, Long value, String comment) {
		setNumber(key, value, comment);
	}
	
	/**
	 * Sets a boolean property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setBoolean(String key, Boolean value) {
		setProperty(key, value.toString());
	}
	
	/**
	 * Sets a boolean property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setBoolean(String key, Boolean value, String comment) {
		setProperty(key, value.toString(), comment);
	}
	
	/**
	 * Sets an array property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setArray(String key, Object[] value) {
		setProperty(key, serialize(value));
	}
	
	/**
	 * Sets an array property.
	 * 
	 * @param key The key of the property to set.
	 * @param value The value of the property to set.
	 * @param comment The comment of the property to set.
	 */
	public void setArray(String key, Object[] value, String comment) {
		setProperty(key, serialize(value), comment);
	}
	
	private String serialize(Object[] array) { // easy to parse, ugly to human eyes
		StringBuilder sb = new StringBuilder();
		for(Object o : array) {
			sb.append(o.toString()).append(",");
		}
		return sb.toString();
	}
	
	@Override
	public void finalize() { // does this actually work?
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class Property {
		private String value;
		private String comment;
		
		public Property(String value, String comment) {
			this.value = value;
			this.comment = comment;
		}
		
		public Property(String value) {
			this(value, null);
		}
		
		public String getComment() {
			return comment;
		}
		
		public void setComment(String comment) {
			this.comment = comment;
		}
		
		public String valueAsString() {
			return value;
		}
		
		public Integer valueAsInt() {
			return Integer.parseInt(value);
		}
		
		public Double valueAsDouble() {
			return Double.parseDouble(value);
		}
		
		public Long valueAsLong() {
			return Long.parseLong(value);
		}
		
		public Boolean valueAsBool() {
			return Boolean.parseBoolean(value);
		}
		
		public String[] valueAsArray() {
			return value.split(",");
		}
	}
}
