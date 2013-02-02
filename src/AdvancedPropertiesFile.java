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
 * Alternative to a PropertiesFile with added features. Methods are similarly named for compatibility.
 * 
 * @author gregthegeek
 *
 */
public class AdvancedPropertiesFile {
	private final Logger log = Logger.getLogger("Minecraft");
	private final String filePath;
	private final HashMap<String, Property> map = new HashMap<String, Property>();
	private String header;
	
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
			writer.append(String.format("##%s\n", header));
		}
		for(Entry<String, Property> e : map.entrySet()) {
			Property p = e.getValue();
			String comment = p.getComment();
			if(comment != null) {
				writer.append(String.format("#%s\n", comment));
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
	 * Gets a string property.
	 * 
	 * @param key The key of the property to get.
	 * @return String
	 */
	public String getString(String key) {
		return this.getProperty(key).valueAsString();
	}
	
	/**
	 * Gets a string property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property.
	 * @return String
	 */
	public String getString(String key, String defaultVal) {
		return getProperty(key, defaultVal).valueAsString();
	}
	
	/**
	 * Gets a string property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property.
	 * @param defaultComment The default comment of the property.
	 * @return String
	 */
	public String getString(String key, String defaultVal, String defaultComment) {
		return getProperty(key, defaultVal, defaultComment).valueAsString();
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
	
	// no getNumber()
	
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
	 * Gets an integer property.
	 * 
	 * @param key The key of the property.
	 * @return Integer
	 */
	public int getInt(String key) {
		return getProperty(key).valueAsInt();
	}
	
	/**
	 * Gets an integer property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @return Integer
	 */
	public int getInt(String key, Integer defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsInt();
	}
	
	/**
	 * Gets an integer property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @param defaultComment The default comment of the property.
	 * @return Integer
	 */
	public int getInt(String key, Integer defaultVal, String defaultComment) {
		return getProperty(key, defaultVal.toString(), defaultComment).valueAsInt();
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
	 * Gets a double property.
	 * 
	 * @param key The key of the property to get.
	 * @return Double
	 */
	public double getDouble(String key) {
		return getProperty(key).valueAsDouble();
	}
	
	/**
	 * Gets a double property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property to get.
	 * @return Double
	 */
	public double getDouble(String key, Double defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsDouble();
	}
	
	/**
	 * Gets a double property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property to get.
	 * @param defaultComment The default comment of the property.
	 * @return Double
	 */
	public double getDouble(String key, Double defaultVal, String defaultComment) {
		return getProperty(key, defaultVal.toString(), defaultComment).valueAsDouble();
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
	 * Gets a long property.
	 * 
	 * @param key The key of the property to get.
	 * @return Long
	 */
	public long getLong(String key) {
		return getProperty(key).valueAsLong();
	}
	
	/**
	 * Gets a long property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property to get.
	 * @return Long
	 */
	public long getLong(String key, Long defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsLong();
	}
	
	/**
	 * Gets a long property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property to get.
	 * @param defaultComment The default comment of the property.
	 * @return Long
	 */
	public long getLong(String key, Long defaultVal, String defaultComment) {
		return getProperty(key, defaultVal.toString(), defaultComment).valueAsLong();
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
	 * @param defaultComment The comment of the property to set.
	 */
	public void setBoolean(String key, Boolean value, String defaultComment) {
		setProperty(key, value.toString(), defaultComment);
	}
	
	/**
	 * Gets a boolean property.
	 * 
	 * @param key The key of the property to get.
	 * @return Boolean
	 */
	public boolean getBoolean(String key) {
		return getProperty(key).valueAsBool();
	}
	
	/**
	 * Gets a boolean property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property to get.
	 * @return Boolean
	 */
	public boolean getBoolean(String key, Boolean defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsBool();
	}
	
	/**
	 * Gets a boolean property.
	 * 
	 * @param key The key of the property to get.
	 * @param defaultVal The default value of the property to get.
	 * @param comment The default comment of the property.
	 * @return Boolean
	 */
	public boolean getBoolean(String key, Boolean defaultVal, String comment) {
		return getProperty(key, defaultVal.toString(), comment).valueAsBool();
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
	
	/**
	 * Gets an array property.
	 * 
	 * @param key The key of the property.
	 * @return String[]
	 */
	public String[] getArray(String key) {
		return getProperty(key).valueAsArray();
	}
	
	/**
	 * Gets an array property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @return String[]
	 */
	public String[] getArray(String key, Object[] defaultVal) {
		return getProperty(key, serialize(defaultVal)).valueAsArray();
	}
	
	/**
	 * Gets an array property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @param comment The default comment of the property.
	 * @return String[]
	 */
	public String[] getArray(String key, Object[] defaultVal, String comment) {
		return getProperty(key, serialize(defaultVal), comment).valueAsArray();
	}
	
	private String serialize(Object[] array) { // easy to parse, ugly to human eyes
		StringBuilder sb = new StringBuilder();
		for(Object o : array) {
			sb.append(o.toString()).append(",");
		}
		return sb.toString();
	}
	
	/**
	 * Sets an enum property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 */
	public void setEnum(String key, Enum<?> value) {
		setString(key, value.toString());
	}
	
	/**
	 * Sets an enum property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 * @param comment The comment of the property.
	 */
	public void setEnum(String key, Enum<?> value, String comment) {
		setString(key, value.toString(), comment);
	}
	
	/**
	 * Gets an enum property.
	 * 
	 * @param key The key of the property.
	 * @param enumClass The class of the enum to return.
	 * @return Enum
	 */
	public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
		return getProperty(key).valueAsEnum(enumClass);
	}
	
	/**
	 * Gets an enum property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @return Enum
	 */
	public <T extends Enum<T>> T getEnum(String key, T defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsEnum(defaultVal.getDeclaringClass());
	}
	
	/**
	 * Gets an enum property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @param defaultComment The default comment of the property.
	 * @return Enum
	 */
	public <T extends Enum<T>> T getEnum(String key, T defaultVal, String defaultComment) {
		return getProperty(key, defaultVal.toString(), defaultComment).valueAsEnum(defaultVal.getDeclaringClass());
	}
	
	/**
	 * Sets a byte property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 */
	public void setByte(String key, Byte value) {
		setNumber(key, value);
	}
	
	/**
	 * Sets a byte property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 * @param comment The comment of the property.
	 */
	public void setByte(String key, Byte value, String comment) {
		setNumber(key, value, comment);
	}
	
	/**
	 * Gets a byte property.
	 * 
	 * @param key The key of the property.
	 * @return byte
	 */
	public byte getByte(String key) {
		return getProperty(key).valueAsByte();
	}
	
	/**
	 * Gets a byte property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @return byte
	 */
	public byte getByte(String key, Byte defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsByte();
	}
	
	/**
	 * Gets a byte property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @param defaultComment The default comment of the property.
	 * @return byte
	 */
	public byte getByte(String key, Byte defaultVal, String defaultComment) {
		return getProperty(key, defaultVal.toString(), defaultComment).valueAsByte();
	}
	
	/**
	 * Sets a short property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 */
	public void setShort(String key, Short value) {
		setNumber(key, value);
	}
	
	/**
	 * Sets a short property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 * @param comment The comment of the property.
	 */
	public void setShort(String key, Short value, String comment) {
		setNumber(key, value, comment);
	}
	
	/**
	 * Gets a short property.
	 * 
	 * @param key The key of the property.
	 * @return short
	 */
	public short getShort(String key) {
		return getProperty(key).valueAsShort();
	}
	
	/**
	 * Gets a short property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @return short
	 */
	public short getShort(String key, Short defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsShort();
	}
	
	/**
	 * Gets a short property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @param defaultComment The default comment of the property.
	 * @return short
	 */
	public short getShort(String key, Short defaultVal, String defaultComment) {
		return getProperty(key, defaultVal.toString(), defaultComment).valueAsShort();
	}
	
	/**
	 * Sets a float property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 */
	public void setFloat(String key, Float value) {
		setNumber(key, value);
	}
	
	/**
	 * Sets a float property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 * @param comment The comment of the property.
	 */
	public void setFloat(String key, Float value, String comment) {
		setNumber(key, value, comment);
	}
	
	/**
	 * Gets a float property.
	 * 
	 * @param key The key of the property.
	 * @return float
	 */
	public float getFloat(String key) {
		return getProperty(key).valueAsFloat();
	}
	
	/**
	 * Gets a float property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @return float
	 */
	public float getFloat(String key, Float defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsFloat();
	}
	
	/**
	 * Gets a float property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @param defaultComment The default comment of the property.
	 * @return
	 */
	public float getFloat(String key, Float defaultVal, String defaultComment) {
		return getProperty(key, defaultVal.toString(), defaultComment).valueAsFloat();
	}
	
	/**
	 * Sets a character property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 */
	public void setCharacter(String key, Character value) {
		setProperty(key, value.toString());
	}
	
	/**
	 * Sets a character property.
	 * 
	 * @param key The key of the property.
	 * @param value The value of the property.
	 * @param comment The comment of the property.
	 */
	public void setCharacter(String key, Character value, String comment) {
		setProperty(key, value.toString(), comment);
	}
	
	/**
	 * Gets a character property.
	 * 
	 * @param key The key of the property.
	 * @return char
	 */
	public char getCharacter(String key) {
		return getProperty(key).valueAsChar();
	}
	
	/**
	 * Gets a character property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @return char
	 */
	public char getCharacter(String key, Character defaultVal) {
		return getProperty(key, defaultVal.toString()).valueAsChar();
	}
	
	/**
	 * Gets a character property.
	 * 
	 * @param key The key of the property.
	 * @param defaultVal The default value of the property.
	 * @param comment The default comment of the property.
	 * @return char
	 */
	public char getCharacter(String key, Character defaultVal, String comment) {
		return getProperty(key, defaultVal.toString(), comment).valueAsChar();
	}
	
	@Override
	protected void finalize() { // not always called
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the header used at the top of the document. Null if no header.
	 * 
	 * @return String
	 */
	public String getHeader() {
		return header;
	}
	
	/**
	 * Sets the header used at the top of the document. Null if no header.
	 * 
	 * @param header The header.
	 */
	public void setHeader(String header) {
		this.header = header;
	}
	
	@Override
	public String toString() {
		return String.format("AdvancedPropertiesFile[path=%s]", filePath);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AdvancedPropertiesFile) {
			return filePath.equalsIgnoreCase(((AdvancedPropertiesFile) obj).filePath);
		}
		return false;
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
		
		public int valueAsInt() {
			return Integer.parseInt(value);
		}
		
		public double valueAsDouble() {
			return Double.parseDouble(value);
		}
		
		public long valueAsLong() {
			return Long.parseLong(value);
		}
		
		public boolean valueAsBool() {
			return Boolean.parseBoolean(value);
		}
		
		public String[] valueAsArray() {
			return value.split(",");
		}
		
		public <T extends Enum<T>> T valueAsEnum(Class<T> enumClass) {
			return Enum.valueOf(enumClass, value);
		}
		
		public byte valueAsByte() {
			return Byte.parseByte(value);
		}
		
		public short valueAsShort() {
			return Short.parseShort(value);
		}
		
		public float valueAsFloat() {
			return Float.parseFloat(value);
		}
		
		public char valueAsChar() {
			return value.charAt(0);
		}
	}
}
