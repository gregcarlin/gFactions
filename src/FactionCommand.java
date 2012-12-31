/**
 * Handles the execution of commands.
 * 
 * @author gregthegeek
 *
 */
public class FactionCommand extends BaseCommand {
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
			//TODO
		} else if(lArgs[1].equals("list") || lArgs[1].equals("ls")) {
			try {
				return fManager.getList(Integer.parseInt(lArgs[2]));
			} catch (NumberFormatException e) {
				return Utils.rose("%d is not a number!", lArgs[2]);
			} catch (ArrayIndexOutOfBoundsException e) {
				return Utils.rose("Usage: /f %s [page]", args[1]);
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
