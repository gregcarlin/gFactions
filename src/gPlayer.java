/**
 * Holds extra player related data.
 * 
 * @author gregthegeek
 *
 */
public class gPlayer {
	public enum ChatChannel {
		PUBLIC(Colors.White),
		FACTION(Relation.Type.SAME.getColor()),
		ALLY(Relation.Type.ALLY.getColor());
		
		private final String color;
		
		private ChatChannel(String color) {
			this.color = color;
		}
		
		public String getColor() {
			return color;
		}
		
		public ChatChannel increment() {
			switch(this) {
			case PUBLIC:
				return FACTION;
			case FACTION:
				return ALLY;
			case ALLY:
			default:
				return PUBLIC;
			}
		}
		
		public static ChatChannel fromString(String s) {
			char c = Character.toLowerCase(s.charAt(0));
			switch(c) {
			case 'p':
				return PUBLIC;
			case 'f':
				return FACTION;
			case 'a':
				return ALLY;
			default:
				return null;
			}
		}
	}
	
	private final String name;
	private int power;
	public int maxPower = 10;
	private String title;
	private transient Object powerLock;
	public transient boolean adminBypass;
	private transient ChatChannel chatChannel;
	public transient boolean chatSpy;
	public transient boolean autoClaim;
	
	public gPlayer(String name, int power) {
		this.name = name;
		this.power = power;
		this.title = "";
		this.adminBypass = false;
		this.chatSpy = false;
		this.autoClaim = false;
	}
	
	/**
	 * Returns the name of this player (same as server name).
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the amount of power this player has.
	 * 
	 * @return int
	 */
	public int getPower() {
		synchronized(getPowerLock()) {
			return power;
		}
	}
	
	private Object getPowerLock() {
		if(powerLock == null) {
			powerLock = new Object();
		}
		return powerLock;
	}
	
	/**
	 * Returns this title, prefix, and name concatenated together.
	 * 
	 * @return String
	 */
	public String getFormattedName() {
		String prefix = "";
		Faction f = Utils.plugin.getFactionManager().getFaction(name);
		if(f != null && !(f instanceof SpecialFaction)) {
			prefix = f.getRank(name).getPrefix();
		}
		return title == null ? prefix + name : String.format("%s %s%s", title, prefix, name);
	}
	
	/**
	 * Increases power by one.
	 * 
	 * @return Whether or not power is now maxed out.
	 */
	public boolean increasePower() {
		synchronized(getPowerLock()) {
			power++;
			if(power > maxPower) { // don't think I need this, but better safe than sorry
				power = maxPower;
			}
		}
		save();
		return power == maxPower;
	}
	
	/**
	 * Decreases power by the amount specified in the config.
	 * 
	 * @param warzone Whether or not power loss is warzone or regular.
	 * @return Whether or not power is now zero.
	 */
	public boolean decreasePower(boolean warzone) {
		Config config = Utils.plugin.getConfig();
		if(power >= maxPower) { // if true, no power adder was running for this player
			etc.getServer().addToServerQueue(new PowerAdder(this), config.getPowerRegenInterval());
		}
		synchronized(getPowerLock()) {
			power -= warzone ? config.getPowerLossOnDeathWarzone() : config.getPowerLossOnDeath();
			if(power < 0) {
				power = 0;
			}
		}
		save();
		return power == 0;
	}
	
	/**
	 * Returns whether or not this player is currently online.
	 * 
	 * @return boolean
	 */
	public boolean isOnline() {
		return toPlayer() != null;
	}
	
	/**
	 * Returns this gPlayer as a regular player.
	 * 
	 * @return Player
	 */
	public Player toPlayer() {
		return etc.getServer().getPlayer(name);
	}
	
	/**
	 * Returns this gPlayer's current chat channel.
	 * 
	 * @return ChatChannel
	 */
	public ChatChannel getChatChannel() {
		if(chatChannel == null) {
			chatChannel = ChatChannel.PUBLIC;
		}
		return chatChannel;
	}
	
	/**
	 * Sets this gPlayer's current chat channel.
	 * 
	 * @param channel The new chat channel.
	 */
	public void setChatChannel(ChatChannel channel) {
		chatChannel = channel;
	}
	
	/**
	 * Get this player's title. Should never be null, should be empty string if no title.
	 * 
	 * @return String
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets this player's title. Should never be null, should use empty string if no title.
	 * 
	 * @param title The player's new title.
	 */
	public void setTitle(String title) {
		this.title = title;
		save();
	}
	
	private void save() {
		if(Utils.plugin.getConfig().getSaveInterval() < 0) {
			Utils.plugin.getDataSource().save(new gPlayer[] {this});
		}
	}
	
	@Override
	public String toString() {
		return String.format("gPlayer[name=%s, power=%d, title=%s]", name, power, title);
	}
}
