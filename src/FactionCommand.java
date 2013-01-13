/**
 * Handles the execution of commands.
 * 
 * @author gregthegeek
 *
 */
public class FactionCommand extends BaseCommand {
	public enum CommandUsageRank {
		NO_FACTION(6),
		FACTION_MEMBER(11),
		FACTION_MOD(28),
		FACTION_ADMIN(33),
		SERVER_ADMIN(38);
		
		private final int commandMax;
		
		private CommandUsageRank(int commandMax) {
			this.commandMax = commandMax;
		}
		
		public int getListMax() {
			return commandMax;
		}
	}
	
	private static final String[] c = new String[38]; // c = commands
	static {
		// commands for all players
		c[0] = "/f help - View this list of commands.";
		c[1] = "/f list (page) - Lists active factions.";
		c[2] = "/f show (faction) - Gives information about a faction.";
		c[3] = "/f map - Displays a map of nearby factions.";
		c[4] = "/f power (player) - Displays the power possesed by a player.";
		c[5] = "/f join [faction] - Join a faction.";
		c[6] = "/f create [name] - Create a faction.";
		
		// commands for faction members
		c[7] = "/f leave - Leave your current faction.";
		c[8] = "/f chat (faction/ally/public) - Switch chat modes.";
		c[9] = "/f home - Teleport to your faction's home.";
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
	
	private static final FactionSubCommand[] subCommands = new FactionSubCommand[39];
	static {
		subCommands[0] = new FactionSubCommand(new String[] {"help", "h", "?"}, "View the list of commands.", "(page)") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				try {
					int page = args.length > 1 ? Integer.parseInt(args[0]) : 0;
					int max = Utils.getCommandRank(caller).getListMax();
					String[] rt = new String[5];
					for(int i=0; i<rt.length; i++) {
						int index = page * rt.length + i;
						rt[i] = index <= max ? subCommands[index].toString() : Utils.rose("No more!");
					}
					return rt;
				} catch (NumberFormatException e) {
					return new String[] {Utils.rose("%s is not a number!", args[0])};
				}
			}
		};
			
		subCommands[1] = new FactionSubCommand(new String[] {"list", "ls"}, "Lists active factions.", "(page)") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				try {
					int page = args.length > 1 ? Integer.parseInt(args[0]) : 0;
					return Utils.fManager.getList(page);
				} catch (NumberFormatException e) {
					return new String[] {Utils.rose("%s is not a number!", args[0])};
				}
			}
		};
		
		subCommands[2] = new FactionSubCommand(new String[] {"show", "who"}, "Gives information about a faction.", "(faction)") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				if(args.length > 0) { // other faction specified
					Faction f = Utils.fManager.getFactionByName(args[0]);
					if(f == null) {
						return new String[] {Utils.rose("Faction %s was not found.", args[0])};
					} else {
						return f.getWho(caller);
					}
				} else if(caller instanceof Player) {
					Faction f = Utils.fManager.getFaction(((Player) caller).getName());
					return f.getWho(f);
				} else {
					return new String[] {Utils.rose(toString())};
				}
			}
		};
		
		subCommands[3] = new FactionSubCommand(new String[] {"map"}, "Displays a map of nearby factions.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[4] = new FactionSubCommand(new String[] {"power", "pow"}, "Displays the power possessed by a player.", "(player)") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				/*if(args.length > 0) {
					gPlayer gp = Utils.fManager.par.getPlayerManager().getPlayer(args[1]);
					if(gp == null) {
						return new String[] {Utils.rose("Player %s%s %swas not found.", Colors.Red, args[0], Colors.Rose)};
					} else if(caller instanceof Player) {
						return new String[] {String.format("%s%s%s: %d/%d", Utils.fManager.par.getRelationManager().getRelation(((Player) caller).getName(), args[0]).getColor(), gp.getFormattedName(), Colors.Yellow, gp.getPower(), gp.maxPower)};
					} else {
						return new String[] {String.format("%s: %d/%d", gp.getFormattedName(), gp.getPower(), gp.maxPower)};
					}
				} else if(caller instanceof)*/
				String player = args.length > 0 ? args[0] : (caller instanceof Player ? ((Player) caller).getName() : null);
				gPlayer gp = Utils.fManager.par.getPlayerManager().getPlayer(player);
				if(player == null) {
					return new String[] {Utils.rose("Usage: /f power [player]")};
				} else if(caller instanceof Player) {
					return new String[] {String.format("%s%s%s: %d/%d", Utils.fManager.par.getRelationManager().getRelation(((Player) caller).getName(), player).getColor(), gp.getFormattedName(), Colors.Yellow, gp.getPower(), gp.maxPower)};
				} else {
					return new String[] {String.format("%s: %d/%d", gp.getFormattedName(), gp.getPower(), gp.maxPower)};
				}
			}
		};
		
		subCommands[5] = new FactionSubCommand(new String[] {"join"}, "Join a faction.", "[faction]") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[6] = new FactionSubCommand(new String[] {"create"}, "Create a faction.", "[name]") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				try {
					Utils.fManager.createFaction(((Player) caller).getName(), args[0]);
					return new String[] {String.format("%1$sFaction %2$s%3$s %1$screated.", Colors.Yellow, Colors.Gold, args[0])};
				} catch (ArrayIndexOutOfBoundsException e) {
					return new String[] {Utils.rose("Usage: /f create [name]")};
				} catch (ClassCastException e) {
					return new String[] {"Only in game players can create factions."};
				}
			}
		};
		
		subCommands[7] = new FactionSubCommand(new String[] {"leave"}, "Leave your current faction.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[8] = new FactionSubCommand(new String[] {"chat", "c"}, "Switch chat modes.", "(faction/f/ally/a/public/p)") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[9] = new FactionSubCommand(new String[] {"home"}, "Teleport to your faction's home.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[10] = new FactionSubCommand(new String[] {"ownerlist"}, "List the owners of a land plot.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[11] = new FactionSubCommand(new String[] {"money"}, "View commands related to faction banking.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[12] = new FactionSubCommand(new String[] {"desc"}, "Set your faction's description.", "[desc]") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[13] = new FactionSubCommand(new String[] {"tag"}, "Set your faction's tag (aka name).", "[tag]") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[14] = new FactionSubCommand(new String[] {"open"}, "Allow anyone to join your faction.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[15] = new FactionSubCommand(new String[] {"close"}, "Only allow those invited to join your faction.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[16] = new FactionSubCommand(new String[] {"invite", "inv"}, "Invite a player to your faction.", "[player]") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[17] = new FactionSubCommand(new String[] {"deinvite", "deinv"}, "Revoke a faction invitation.", "[player]") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[18] = new FactionSubCommand(new String[] {"sethome"}, "Set the faction's home.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[19] = new FactionSubCommand(new String[] {"claim"}, "Claim land for your faction.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[20] = new FactionSubCommand(new String[] {"autoclaim"}, "Toggle the automatic claiming of land for your faction.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[21] = new FactionSubCommand(new String[] {"unclaim"}, "Unclaim land for your faction.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[22] = new FactionSubCommand(new String[] {"unclaimall"}, "Unclaim all faction-owned land.", "") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[23] = new FactionSubCommand(new String[] {"owner"}, "Toggles build rights for players on land.", "(player)") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[24] = new FactionSubCommand(new String[] {"kick"}, "Kick a player from the faction.", "[player]") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
	}
	
	public FactionCommand() {
		super("- Base command for working with factions.", String.format("%s/f help %sfor a list of available commands.", Colors.Red, Colors.Rose), 2);
	}

	@Override
	protected void execute(MessageReceiver arg0, String[] args) {
		String[] msgs = null;
		for(FactionSubCommand cmd : subCommands) {
			if(cmd.isCalledBy(args[1])) {
				msgs = cmd.executeWrapper(arg0, Utils.trim(args, 2));
				break;
			}
		}
		if(msgs == null) {
			arg0.notify(String.format("%1$sInvalid command. %2$s/f help %1$sfor a list of available commands.", Colors.Rose, Colors.Red));
		} else {
			Utils.sendMsgs(arg0, msgs);
		}
	}
	
	/*private Object executeWrapper(MessageReceiver arg0, String[] args) {
		} else if(lArgs[1].equals("join")) {
			
		} else if(lArgs[1].equals("leave")) {
			
		} else if(lArgs[1].equals("chat") || lArgs[1].equals("c")) {
			
		} else if(lArgs[1].equals("home")) {
			
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
	}*/
	
	private static String[] lower(String[] arr) {
		String[] rt = new String[arr.length];
		for(int i=0; i<rt.length; i++) {
			rt[i] = arr[i].toLowerCase();
		}
		return rt;
	}
}
