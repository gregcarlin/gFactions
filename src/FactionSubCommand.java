/**
 * Outlines a subcommand (/f [subcommand])
 * 
 * @author gregthegeek
 *
 */
public abstract class FactionSubCommand {
	private final String[] aliases;
	private final String description;
	private final String argHelp;
	private final int minArgs;
	private final FactionCommand.CommandUsageRank minRank;
	
	public FactionSubCommand(String[] aliases, String description, String argHelp) {
		this(aliases, description, argHelp, FactionCommand.CommandUsageRank.NO_FACTION);
	}
	
	public FactionSubCommand(String[] aliases, String description, String argHelp, FactionCommand.CommandUsageRank minRank) {
		this(aliases, description, argHelp, 0, minRank);
	}
	
	public FactionSubCommand(String[] aliases, String description, String argHelp, int minArgs) {
		this(aliases, description, argHelp, minArgs, FactionCommand.CommandUsageRank.NO_FACTION);
	}
	
	public FactionSubCommand(String[] aliases, String description, String argHelp, int minArgs, FactionCommand.CommandUsageRank minRank) {
		this.aliases = aliases;
		this.description = description;
		this.argHelp = argHelp;
		this.minArgs = minArgs;
		this.minRank = minRank;
	}
	
	/**
	 * Is this subcommand called by the given alias?
	 * 
	 * @param alias The alias to check against this subcommand's aliases.
	 * @return boolean
	 */
	public boolean isCalledBy(String alias) {
		for(String s : aliases) {
			if(s.equalsIgnoreCase(alias)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks stuff before executing. Should be called externally.
	 * 
	 * @param caller The caller of the subcommand.
	 * @param args Command arguments.
	 * @return String[] messages to send back to the command caller.
	 */
	public String[] executeWrapper(MessageReceiver caller, String[] args) {
		if(Utils.getCommandRank(caller).ordinal() < minRank.ordinal()) {
			return new String[] {Utils.rose("Insufficient permissions: You must be a %s to use this command.", minRank.toString())};
		} else if(args.length < minArgs) {
			return new String[] {Utils.rose("/f %s %s", aliases[0], argHelp)};
		} else {
			return execute(caller, args);
		}
	}
	
	/**
	 * Executes this subcommand with the given arguments. Shouldn't be called externally, executeWrapper() should be.
	 * 
	 * @param caller The caller of the subcommand.
	 * @param args Command arguments.
	 * @return String[] messages to send back to the command caller.
	 */
	abstract String[] execute(MessageReceiver caller, String[] args);
	
	@Override
	public String toString() {
		return String.format("%s/f %s %s - %s", Colors.Yellow, aliases[0], argHelp, description);
	}
}
