/**
 * Handles the execution of commands.
 * 
 * @author gregthegeek
 *
 */
public class FactionCommand extends BaseCommand {
	private static final String[] c = new String[38]; // c = commands
	static {
		// commands for all players
		c[0] = "/f help - View this list of commands.";
		c[1] = "/f list (page) - Lists active factions.";
		c[2] = "/f show (faction) - Gives information about a faction.";
		c[3] = "/f map - Displays a map of nearby factions.";
		c[4] = "/f power (player) - Displays the power possesed by a player.";
		c[5] = "/f join [faction] - Join a faction.";
		c[6] = "/f leave - Leave your current faction.";
		c[7] = "/f chat (faction/ally/public) - Switch chat modes.";
		c[8] = "/f home - Teleport to your faction's home.";
		c[9] = "/f create [name] - Create a faction.";
		c[10] = "/f ownerlist - List the owners of a land plot.";
		c[11] = "/f money - View commands related to faction banking.";
		
		// commands for faction mods
		c[12] = "/f desc [desc] - Set your faction's description.";
		c[13] = "/f tag [tag] - Set your faction's tag (name).";
		c[14] = "/f open - Allow anyone to join your faction.";
		c[15] = "/f close - Only allow those invited to join your faction.";
		c[16] = "/f invite [player] - Invite a player to your faction.";
		c[17] = "/f deinvite [player] - Revoke a faction invitation.";
		c[18] = "/f sethome - Set the faction's home.";
		c[19] = "/f claim - Claim land for your faction.";
		c[20] = "/f autoclaim - Toggle the automatic claiming of land for your faction.";
		c[21] = "/f unclaim - Unclaim land for your faction.";
		c[21] = "/f unclaimall - Unclaim all faction-owned land.";
		c[22] = "/f owner (player) - Toggles build rights for players on land.";
		c[23] = "/f kick [player] - Kick a player from the faction.";
		c[24] = "/f title [player] [title] - Set a player's faction title.";
		c[25] = "/f ally [faction] - Ally another faction.";
		c[26] = "/f neutral [faction] - Dissolve relations with another faction.";
		c[27] = "/f enemy [faction] - Enemy another faction.";
		
		// commands for faction admins
		c[28] = "/f mod [player] - Toggle whether or not a player is a mod.";
		c[29] = "/f admin [player] - Transfer faction ownership to another player.";
		c[30] = "/f noboom - Toggle explosions is faction territory.";
		c[31] = "/f disband - Disband your faction.";
		c[32] = "/f peaceful - Toggle whether or not your faction is peaceful.";
		
		// commands for server admins
		c[33] = "/f bypass - Toggle admin bypass mode.";
		c[34] = "/f chatspy - Toggle chatspy.";
		c[35] = "/f permanentpower (faction) - Freeze a faction's power.";
		c[36] = "/f save - Save all faction and player data.";
		c[37] = "/f version - View the running version of gFactions.";
	}
	
	private final FactionManager fManager;
	
	public FactionCommand(FactionManager fManager) {
		super("- Base command for working with factions.", String.format("%s/f help %sfor a list of available commands.", Colors.Red, Colors.Rose), 2);
		this.fManager = fManager;
	}

	@Override
	protected void execute(MessageReceiver arg0, String[] args) {
		Object msg = executeWrapper(arg0, args);
		if(msg != null) {
			if(msg instanceof String) {
				arg0.notify((String) msg);
			} else if(msg instanceof String[]) {
				Utils.sendMsgs(arg0, (String[]) msg);
			}
		}
	}
	
	private Object executeWrapper(MessageReceiver arg0, String[] args) {
		if(!permCheck(arg0, "/f")) {
			return Utils.rose("You do not have permission to use this command.");
		}
		String[] lArgs = lower(args);
		if(lArgs[1].equals("help") || lArgs[1].equals("h") || lArgs[1].equals("?")) {
			try {
				int page = lArgs.length > 2 ? Integer.parseInt(lArgs[2]) : 0;
				int max = 0;
				if(arg0 instanceof Player) {
					if(((Player) arg0).canUseCommand("/fadmin")) {
						max = 37;
					} else {
						String pName = ((Player) arg0).getName();
						Faction f = fManager.getFaction(pName);
						if(f == null) {
							max = 11;
						} else {
							Faction.PlayerRank rank = f.getRank(pName);
							if(rank == Faction.PlayerRank.MEMBER) {
								max = 11;
							} else if(rank == Faction.PlayerRank.MODERATOR) {
								max = 27;
							} else {
								assert rank == Faction.PlayerRank.ADMIN;
								max = 32;
							}
						}
					}
				}
				String[] rt = new String[5];
				for(int i=0; i<rt.length; i++) {
					int index = page * 5 + i;
					rt[i] = index <= max ? c[index] : Utils.rose("No more!");
				}
				return rt;
			} catch (NumberFormatException e) {
				return Utils.rose("%d is not a number!", lArgs[2]);
			}
		} else if(lArgs[1].equals("list") || lArgs[1].equals("ls")) {
			try {
				int page = lArgs.length > 2 ? Integer.parseInt(lArgs[2]) : 0;
				return fManager.getList(page);
			} catch (NumberFormatException e) {
				return Utils.rose("%d is not a number!", lArgs[2]);
			}
		} else if(lArgs[1].equals("show") || lArgs[1].equals("who")) {
			if(lArgs.length > 2) { //other faction specified
				Faction f = fManager.getFactionByName(args[2]);
				if(f == null) {
					return Utils.rose("Faction %s was not found.", args[2]);
				} else {
					return f.getWho((Faction) null);
				}
			} else if(arg0 instanceof Player) { //tell player their faction
				Faction f = fManager.getFaction(((Player) arg0).getName());
				return f.getWho(f);
			} else { //retarded server owner using console
				return Utils.rose("Usage: /f %s [faction]", args[1]);
			}
		} else if(lArgs[1].equals("map")) {
			
		} else if(lArgs[1].equals("power") || lArgs[1].equals("pow")) {
			
		} else if(lArgs[1].equals("join")) {
			
		} else if(lArgs[1].equals("leave")) {
			
		} else if(lArgs[1].equals("chat") || lArgs[1].equals("c")) {
			
		} else if(lArgs[1].equals("home")) {
			
		} else if(lArgs[1].equals("create")) {
			
		} else if(lArgs[1].equals("desc")) {
			
		} else if(lArgs[1].equals("tag") || lArgs[1].equals("name")) {
			
		} else if(lArgs[1].equals("open")) {
			
		} else if(lArgs[1].equals("close")) {
			
		} else if(lArgs[1].equals("invite") || lArgs[1].equals("inv")) {
			
		} else if(lArgs[1].equals("deinvite") || lArgs[1].equals("deinv")) {
			
		} else if(lArgs[1].equals("sethome")) {
			
		} else if(lArgs[1].equals("claim")) {
			
		} else if(lArgs[1].equals("autoclaim")) {
			
		} else if(lArgs[1].equals("unclaim") || lArgs[1].equals("declaim")) {
			
		} else if(lArgs[1].equals("unclaimall") || lArgs[1].equals("declaimall")) {
			
		} else if(lArgs[1].equals("owner")) {
			
		} else if(lArgs[1].equals("ownerlist")) {
			
		} else if(lArgs[1].equals("kick")) {
			
		} else if(lArgs[1].equals("mod")) {
			
		} else if(lArgs[1].equals("admin")) {
			
		} else if(lArgs[1].equals("title")) {
			
		} else if(lArgs[1].equals("noboom")) {
			
		} else if(lArgs[1].equals("ally")) {
			
		} else if(lArgs[1].equals("neutral")) {
			
		} else if(lArgs[1].equals("enemy")) {
			
		} else if(lArgs[1].equals("money")) {
			
		} else if(lArgs[1].equals("disband")) {
			
		} else if(lArgs[1].equals("peaceful")) {
			
		} else if(permCheck(arg0, "/f admin")) {
			if(lArgs[1].equals("bypass")) {
				
			} else if(lArgs[1].equals("chatspy")) {
				
			} else if(lArgs[1].equals("permanentpower")) {
				
			} else if(lArgs[1].equals("save")) {
				
			} else if(lArgs[1].equals("version")) {
				return String.format("%sVersion: %s", Colors.Gold, gFactions.version);
			}
		}
		return String.format("%1$sInvalid command. %2$s/f help %1$sfor a list of available commands.", Colors.Rose, Colors.Red);
	}
	
	private static boolean permCheck(MessageReceiver mr, String cmd) {
		if(mr instanceof Player) {
			return ((Player) mr).canUseCommand(cmd);
		}
		return true;
	}
	
	private static String[] lower(String[] arr) {
		String[] rt = new String[arr.length];
		for(int i=0; i<rt.length; i++) {
			rt[i] = arr[i].toLowerCase();
		}
		return rt;
	}
}
