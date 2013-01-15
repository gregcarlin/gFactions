import java.awt.Color;

/**
 * Handles the execution of commands.
 * 
 * @author gregthegeek
 *
 */
public class FactionCommand extends BaseCommand {
	public enum CommandUsageRank {
		NO_FACTION(6, "regular member"),
		FACTION_MEMBER(11, "faction member"),
		FACTION_MOD(28, "faction moderator"),
		FACTION_ADMIN(33, "faction admin"),
		SERVER_ADMIN(38, "server admin");
		
		private final int commandMax;
		private final String userReadable;
		
		private CommandUsageRank(int commandMax, String userReadable) {
			this.commandMax = commandMax;
			this.userReadable = userReadable;
		}
		
		public int getListMax() {
			return commandMax;
		}
		
		@Override
		public String toString() {
			return userReadable;
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
				if(caller instanceof Player) {
					String pName = ((Player) caller).getName();
					FactionManager fManager = Utils.plugin.getFactionManager();
					Faction old = fManager.getFaction(pName);
					if(old != null) {
						return new String[] {Utils.rose("You must leave your current faction first.")};
					}
					Faction nFac = fManager.getFactionByName(args[0]);
					if(nFac == null) {
						return new String[] {Utils.rose("Faction %s%s %snot found.", Colors.Red, args[0], Colors.Rose)};
					} else if(nFac.isOpen() || nFac.isInvited(pName) || Utils.plugin.getPlayerManager().getPlayer(pName).adminBypass) {
						nFac.add(pName);
						return new String[] {String.format("%1$sYou are now a member of %2$s%3$s %1$s.", Colors.Yellow, Colors.Green, nFac.getName())};
					} else {
						return new String[] {Utils.rose("You must be invited to join that faction.")};
					}
				} else {
					return new String[] {"You are not a player! How can you join a faction?"};
				}
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
				assert caller instanceof Player;
				String pName = ((Player) caller).getName();
				Faction f = Utils.plugin.getFactionManager().getFaction(pName);
				assert f != null && !(f instanceof SpecialFaction);
				f.remove(pName);
				return new String[] {String.format("%sYou are no longer in any faction.", Colors.Yellow)};
			}
		};
		
		subCommands[8] = new FactionSubCommand(new String[] {"chat", "c"}, "Switch chat modes.", "(faction/f/ally/a/public/p)", CommandUsageRank.FACTION_MEMBER) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(((Player) caller).getName());
				gPlayer.ChatChannel chatChannel = args.length > 0 ? gPlayer.ChatChannel.fromString(args[0]) : gp.chatChannel.increment();
				if(chatChannel == null) {
					assert args.length > 0;
					return new String[] {Utils.rose("Chat channel %s%s %snot found.", Colors.Red, args[0], Colors.Rose)};
				}
				gp.chatChannel = chatChannel;
				return new String[] {String.format("%1$sNow chatting in %2$s%3$s %1$smode.", Colors.Yellow, chatChannel.getColor(), chatChannel.toString())};
			}
		};
		
		subCommands[9] = new FactionSubCommand(new String[] {"home"}, "Teleport to your faction's home.", "", CommandUsageRank.FACTION_MEMBER) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				Player p = (Player) caller;
				Faction f = Utils.plugin.getFactionManager().getFaction(p.getName());
				assert f != null && !(f instanceof SpecialFaction);
				Location home = f.getHome();
				if(home == null) {
					return new String[] {Utils.rose("Your faction does not have a home set.")};
				} else {
					p.teleportTo(home);
					return new String[] {String.format("%sTeleported.", Colors.Green)};
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
				assert caller instanceof Player;
				Faction f = Utils.plugin.getFactionManager().getFaction(((Player) caller).getName());
				assert f != null && !(f instanceof SpecialFaction);
				f.setDescription(args[0]);
				return new String[] {String.format("%1$sDescription set to %2$s%3$s%1$s.", Colors.Yellow, Colors.Green, args[0])};
			}
		};
		
		subCommands[13] = new FactionSubCommand(new String[] {"tag", "name"}, "Set your faction's tag (aka name).", "[tag]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				FactionManager fManager = Utils.plugin.getFactionManager();
				Faction other = fManager.getFactionByName(args[0]);
				if(other == null) {
					Faction f = fManager.getFaction(((Player) caller).getName());
					assert f != null && !(f instanceof SpecialFaction);
					f.setName(args[0]);
					return new String[] {String.format("%1$sYour faction's name set to %2$s%3$s%1$s.", Colors.Yellow, Colors.Green, args[0])};
				} else {
					return new String[] {Utils.rose("A faction with the name %s%s %salready exists!", Colors.Red, args[0], Colors.Rose)};
				}
			}
		};
		
		subCommands[14] = new FactionSubCommand(new String[] {"open"}, "Allow anyone to join your faction.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				Faction f = Utils.plugin.getFactionManager().getFaction(((Player) caller).getName());
				assert f != null && !(f instanceof SpecialFaction);
				f.setOpen(true);
				return new String[] {String.format("%sYour faction is now %s%s", Colors.Yellow, Colors.Green, "Open")};
			}
		};
		
		subCommands[15] = new FactionSubCommand(new String[] {"close"}, "Only allow those invited to join your faction.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				Faction f = Utils.plugin.getFactionManager().getFaction(((Player) caller).getName());
				assert f != null && !(f instanceof SpecialFaction);
				f.setOpen(false);
				return new String[] {String.format("%sYour faction is now %s%s", Colors.Yellow, Colors.Red, "Closed")};
			}
		};
		
		subCommands[16] = new FactionSubCommand(new String[] {"invite", "inv"}, "Invite a player to your faction.", "[player]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				Faction f = Utils.plugin.getFactionManager().getFaction(((Player) caller).getName());
				assert f != null && !(f instanceof SpecialFaction);
				if(f.invite(args[0])) {
					return new String[] {Utils.rose("That player has already been invited to your faction.")};
				} else {
					Player p = etc.getServer().getPlayer(args[0]);
					if(p != null) {
						p.sendMessage(String.format("%sYou have been invited to %s%s", Colors.Yellow, Colors.Gray, f.getName()));
					}
					return new String[] {String.format("%s%s %shas been invited to your faction.", Colors.Gray, args[0], Colors.Yellow)};
				}
			}
		};
		
		subCommands[17] = new FactionSubCommand(new String[] {"deinvite", "deinv"}, "Revoke a faction invitation.", "[player]", 1, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				Faction f = Utils.plugin.getFactionManager().getFaction(((Player) caller).getName());
				assert f != null && !(f instanceof SpecialFaction);
				if(f.deinvite(args[0])) {
					Player p = etc.getServer().getPlayer(args[0]);
					if(p != null) {
						p.sendMessage(Utils.rose("You are no longer invited to %s%s", Colors.Red, f.getName()));
					}
					return new String[] {String.format("%s%s%s's faction invitation was revoked.", Colors.Gray, args[0], Colors.Yellow)};
				} else {
					return new String[] {String.format("%s%s %swas never invited to your faction.", Colors.Red, args[0], Colors.Rose)};
				}
			}
		};
		
		subCommands[18] = new FactionSubCommand(new String[] {"sethome"}, "Set the faction's home.", "", CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				Player p = (Player) caller;
				String pName = p.getName();
				Faction f = Utils.plugin.getFactionManager().getFaction(pName);
				assert f != null && !(f instanceof SpecialFaction);
				f.setHome(p.getLocation());
				f.sendToMembers(String.format("%s%s %sjust set the faction home.", Colors.LightGreen, pName, Colors.Yellow));
				return null;
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
				assert caller instanceof Player;
				String rt = powerOverHelper(((Player) caller).getName(), args[0], "You cannot kick that player from your faction.");
				if(rt != null) {
					return new String[] {rt};
				}
				Faction f = Utils.plugin.getFactionManager().getFaction(args[0]);
				f.deinvite(args[0]);
				f.remove(args[0]);
				f.sendToMembers(String.format("%s%s %swas kicked from the faction.", Colors.Gray, args[0], Colors.Yellow));
				Player p = etc.getServer().getPlayer(args[0]);
				if(p != null) {
					p.sendMessage(Utils.rose("You were kicked from %s%s", Colors.Red, f.getName()));
				}
				return null;
			}
		};
		
		subCommands[25] = new FactionSubCommand(new String[] {"title"}, "Set a player's faction title.", "[player] [title]", 2, CommandUsageRank.FACTION_MOD) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player;
				String rt = powerOverHelper(((Player) caller).getName(), args[0], "You cannot change that player's title.");
				if(rt != null) {
					return new String[] {rt};
				}
				Utils.plugin.getPlayerManager().getPlayer(args[0]).title = args[1];
				Player p = etc.getServer().getPlayer(args[0]);
				if(p != null) {
					p.sendMessage(String.format("%sYour title has been set to %s%s", Colors.Yellow, Colors.LightGreen, args[1]));
				}
				return new String[] {String.format("%1$s%2$s%3$s's title set to %1$s%4$s%3$s.", Colors.LightGreen, args[0], Colors.Yellow, args[1])};
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
				assert caller instanceof Player && args.length > 0;
				FactionManager fManager = Utils.plugin.getFactionManager();
				Faction f = fManager.getFaction(((Player) caller).getName());
				assert f != null && !(f instanceof SpecialFaction);
				Faction other = fManager.getFaction(args[0]);
				if(!f.equals(other)) {
					return new String[] {Utils.rose("That player is not a member of your faction.")};
				}
				boolean tMod = f.toggleMod(args[0]);
				String msg = tMod ? "now a faction moderator!" : "no longer a faction moderator.";
				Player p = etc.getServer().getPlayer(args[0]);
				if(p != null) {
					p.sendMessage(String.format("%s%s %s", tMod ? Colors.Green : Colors.Rose, "You are", msg));
				}
				return new String[] {String.format("%s%s %sis %s", Colors.LightGreen, args[0], Colors.Yellow, msg)};
			}
		};
		
		subCommands[30] = new FactionSubCommand(new String[] {"admin"}, "Transfer faction ownership to another player.", "[player]", 1, CommandUsageRank.FACTION_ADMIN) {
			@Override
			String[] execute(MessageReceiver caller, String[] args) {
				assert caller instanceof Player && args.length > 0;
				FactionManager fManager = Utils.plugin.getFactionManager();
				String pName = ((Player) caller).getName();
				Faction f = fManager.getFaction(pName);
				assert f != null && !(f instanceof SpecialFaction);
				Faction other = fManager.getFaction(args[0]);
				if(!f.equals(other)) {
					return new String[] {Utils.rose("That player is not a member of your faction.")};
				}
				f.setAdmin(args[0]);
				f.add(pName, Faction.PlayerRank.MODERATOR);
				f.sendToMembers(String.format("%1$s%2$s %3$stransferred faction ownership to %1$s%4s%3$s.", Colors.LightGreen, pName, Colors.Yellow, args[0]));
				return null;
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
				assert caller instanceof Player;
				String pName = ((Player) caller).getName();
				Faction f = Utils.plugin.getFactionManager().getFaction(pName);
				assert f != null && !(f instanceof SpecialFaction);
				String name = f.getName();
				return new String[] {String.format("%1$s%2$s %3$swas disbanded by %1$s%4$s%3$s.", Colors.Gray, name, Colors.Yellow, pName)};
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
				assert caller instanceof Player;
				gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(((Player) caller).getName());
				gp.adminBypass = !gp.adminBypass;
				return new String[] {String.format("%sBypass mode set to: %s", Colors.Yellow, Utils.readBool(gp.adminBypass, "ON", "OFF"))};
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
	
	/**
	 * Highly specialized internal use only.
	 * 
	 * @param one First player.
	 * @param two Second player.
	 * @param error
	 * @return String null if successful.
	 */
	private static String powerOverHelper(String one, String two, String error) {
		FactionManager fManager = Utils.plugin.getFactionManager();
		Faction factionOne = fManager.getFaction(one);
		if(factionOne == null || factionOne instanceof SpecialFaction) {
			return Utils.rose("That player is not in your faction.");
		}
		Faction factionTwo = fManager.getFaction(two);
		if(factionTwo == null || factionTwo instanceof SpecialFaction) {
			return Utils.rose("That player is not in your faction.");
		}
		if(!factionOne.equals(factionTwo)) {
			return Utils.rose("That player is not in your faction.");
		}
		Faction.PlayerRank rankOne = factionOne.getRank(one);
		Faction.PlayerRank rankTwo = factionTwo.getRank(two);
		if(rankOne.ordinal() >= rankTwo.ordinal()) {
			return Utils.rose(error);
		}
		return null;
	}
	
	public FactionCommand() {
		super("- Base command for working with factions.", String.format("%s/f help %sfor a list of available commands.", Colors.Red, Colors.Rose), 2);
	}

	@Override
	protected void execute(MessageReceiver arg0, String[] args) {
		String[] msgs = null;
		boolean found = false;
		for(FactionSubCommand cmd : subCommands) {
			if(cmd.isCalledBy(args[1])) {
				msgs = cmd.executeWrapper(arg0, Utils.trim(args, 2));
				found = true;
				break;
			}
		}
		if(!found) {
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
