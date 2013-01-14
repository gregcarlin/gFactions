import java.awt.Color;

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
					return Utils.plugin.getFactionManager().getList(page);
				} catch (NumberFormatException e) {
					return new String[] {Utils.rose("%s is not a number!", args[0])};
				}
			}
		};
		
		subCommands[2] = new FactionSubCommand(new String[] {"show", "who"}, "Gives information about a faction.", "(faction)") {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				if(args.length > 0) { // other faction specified
					Faction f = Utils.plugin.getFactionManager().getFactionByName(args[0]);
					if(f == null) {
						return new String[] {Utils.rose("Faction %s was not found.", args[0])};
					} else {
						return f.getWho(caller);
					}
				} else if(caller instanceof Player) {
					Faction f = Utils.plugin.getFactionManager().getFaction(((Player) caller).getName());
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
				String player = args.length > 0 ? args[0] : (caller instanceof Player ? ((Player) caller).getName() : null);
				gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(player);
				if(player == null) {
					return new String[] {Utils.rose("Usage: /f power [player]")};
				} else if(caller instanceof Player) {
					return new String[] {String.format("%s%s%s: %d/%d", Utils.plugin.getRelationManager().getRelation(((Player) caller).getName(), player).getColor(), gp.getFormattedName(), Colors.Yellow, gp.getPower(), gp.maxPower)};
				} else {
					return new String[] {String.format("%s: %d/%d", gp.getFormattedName(), gp.getPower(), gp.maxPower)};
				}
			}
		};
		
		subCommands[5] = new FactionSubCommand(new String[] {"join"}, "Join a faction.", "[faction]", 1) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[6] = new FactionSubCommand(new String[] {"create"}, "Create a faction.", "[name]", 1) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				try {
					Utils.plugin.getFactionManager().createFaction(((Player) caller).getName(), args[0]);
					return new String[] {String.format("%1$sFaction %2$s%3$s %1$screated.", Colors.Yellow, Colors.Gold, args[0])};
				} catch (ArrayIndexOutOfBoundsException e) {
					return new String[] {Utils.rose("Usage: /f create [name]")};
				} catch (ClassCastException e) {
					return new String[] {"Only in game players can create factions."};
				}
			}
		};
		
		subCommands[7] = new FactionSubCommand(new String[] {"leave"}, "Leave your current faction.", "", CommandUsageRank.FACTION_MEMBER) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[8] = new FactionSubCommand(new String[] {"chat", "c"}, "Switch chat modes.", "(faction/f/ally/a/public/p)", CommandUsageRank.FACTION_MEMBER) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[9] = new FactionSubCommand(new String[] {"home"}, "Teleport to your faction's home.", "", CommandUsageRank.FACTION_MEMBER) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				if(caller instanceof Player) {
					Player p = (Player) caller;
					Faction f = Utils.plugin.getFactionManager().getFaction(p.getName());
					assert f != null;
					Location home = f.getHome();
					if(home == null) {
						return new String[] {Utils.rose("Your faction does not have a home set.")};
					} else {
						p.teleportTo(home);
						return new String[] {String.format("%sTeleported.", Colors.Green)};
					}
				} else {
					return new String[] {"You cannot move."};
				}
			}
		};
		
		subCommands[10] = new FactionSubCommand(new String[] {"ownerlist"}, "List the owners of a land plot.", "", CommandUsageRank.FACTION_MEMBER) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[11] = new FactionSubCommand(new String[] {"money"}, "View commands related to faction banking.", "", CommandUsageRank.FACTION_MEMBER) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[12] = new FactionSubCommand(new String[] {"desc"}, "Set your faction's description.", "[desc]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[13] = new FactionSubCommand(new String[] {"tag", "name"}, "Set your faction's tag (aka name).", "[tag]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[14] = new FactionSubCommand(new String[] {"open"}, "Allow anyone to join your faction.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[15] = new FactionSubCommand(new String[] {"close"}, "Only allow those invited to join your faction.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[16] = new FactionSubCommand(new String[] {"invite", "inv"}, "Invite a player to your faction.", "[player]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[17] = new FactionSubCommand(new String[] {"deinvite", "deinv"}, "Revoke a faction invitation.", "[player]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[18] = new FactionSubCommand(new String[] {"sethome"}, "Set the faction's home.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[19] = new FactionSubCommand(new String[] {"claim"}, "Claim land for your faction.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[20] = new FactionSubCommand(new String[] {"autoclaim"}, "Toggle the automatic claiming of land for your faction.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[21] = new FactionSubCommand(new String[] {"unclaim", "declaim"}, "Unclaim land for your faction.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[22] = new FactionSubCommand(new String[] {"unclaimall", "declaimall"}, "Unclaim all faction-owned land.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[23] = new FactionSubCommand(new String[] {"owner"}, "Toggles build rights for players on land.", "(player)", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[24] = new FactionSubCommand(new String[] {"kick"}, "Kick a player from the faction.", "[player]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[25] = new FactionSubCommand(new String[] {"title"}, "Set a player's faction title.", "[player] [title]", 2, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[26] = new FactionSubCommand(new String[] {"ally"}, "Ally another faction.", "[faction]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[27] = new FactionSubCommand(new String[] {"neutral"}, "Dissolve relations with another faction.", "[faction]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[28] = new FactionSubCommand(new String[] {"enemy"}, "Enemy another faction.", "[faction]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[29] = new FactionSubCommand(new String[] {"mod"}, "Toggle whether or not another player is a faction mod.", "[player]", 1, CommandUsageRank.FACTION_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[30] = new FactionSubCommand(new String[] {"admin"}, "Transfer faction ownership to another player.", "[player]", 1, CommandUsageRank.FACTION_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[31] = new FactionSubCommand(new String[] {"noboom"}, "Toggle explosions in faction territory.", "", CommandUsageRank.FACTION_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[32] = new FactionSubCommand(new String[] {"disband"}, "Disband your faction.", "", CommandUsageRank.FACTION_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[33] = new FactionSubCommand(new String[] {"peaceful"}, "Toggle whether or not your faction is peaceful.", "", CommandUsageRank.FACTION_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[34] = new FactionSubCommand(new String[] {"bypass"}, "Toggle admin bypass mode.", "", CommandUsageRank.SERVER_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				try {
					gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(((Player) caller).getName());
					gp.adminBypass = !gp.adminBypass;
					return new String[] {String.format("%sBypass mode set to: %s", Colors.Yellow, Utils.readBoolS(gp.adminBypass))};
				} catch (ClassCastException e) {
					return new String[] {"You are not a real player."};
				}
			}
		};
		
		subCommands[35] = new FactionSubCommand(new String[] {"chatspy"}, "Toggle chatspy.", "", CommandUsageRank.SERVER_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[36] = new FactionSubCommand(new String[] {"permanentpower"}, "Freeze a faction's power.", "(faction)", CommandUsageRank.SERVER_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return null; // TODO
			}
		};
		
		subCommands[37] = new FactionSubCommand(new String[] {"save"}, "Save all faction and player data.", "", CommandUsageRank.SERVER_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				Utils.saveAll();
				return new String[] {String.format("%sAll data saved.", Color.GREEN)};
			}
		};
		
		subCommands[38] = new FactionSubCommand(new String[] {"version"}, "View the running version of gFactions.", "", CommandUsageRank.SERVER_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				return new String[] {String.format("%sYou are running gFactions version %s", Colors.Yellow, gFactions.version)};
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
	
	/*private static String[] lower(String[] arr) {
		String[] rt = new String[arr.length];
		for(int i=0; i<rt.length; i++) {
			rt[i] = arr[i].toLowerCase();
		}
		return rt;
	}*/
}
