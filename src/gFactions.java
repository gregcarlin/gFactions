import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Main gFactions class.
 * 
 * @author gregthegeek
 *
 */
public class gFactions extends Plugin {
    public static final String name = "gFactions";
    public static final String version = "2.0";
    private final Logger log = Logger.getLogger("Minecraft");
    private final gFactionsListener listener = new gFactionsListener();
    private FactionManager fManager;
    private RelationManager rManager;
    private gPlayerManager pManager;
    private LandManager lManager;
    private Config config;
    private Datasource dataSource;
    private Economy economy;
    
    @Override
    public void disable() {
    	ThreadManager.stopAll();
    	Utils.saveAll();
    	dataSource.close();
        log.info(name + " version " + version + " disabled.");
    }
    
    @Override
    public void enable() {
    	Utils.plugin = this;
    	config = new Config();
    	try {
			dataSource = config.getDataSource();
		} catch (DatasourceException e) {
			log.severe("Error retrieving initial data from datasource!");
		}
    	fManager = new FactionManager();
    	rManager = new RelationManager();
    	pManager = new gPlayerManager();
    	lManager = new LandManager();
    	economy = config.getEconomy();
    	
    	PlayerCommands.getInstance().add("f", new FactionCommand());
    	
    	// in case plugin is enabled when players are already online
    	for(Player p : etc.getServer().getPlayerList()) {
    		listener.onLogin(p);
    	}
    	
    	int interval = config.getSaveInterval();
    	if(interval > 0) {
    		etc.getServer().addToServerQueue(new AutoSaver(), config.getSaveInterval());
    	}
    	
        log.info(name + " version " + version + " enabled.");
    }
    
    @Override
    public void initialize() {
    	PluginLoader loader = etc.getLoader();
    	PluginLoader.Hook[] hooks = {PluginLoader.Hook.LOGIN, PluginLoader.Hook.CHAT, PluginLoader.Hook.DEATH, PluginLoader.Hook.PLAYER_MOVE};
    	for(PluginLoader.Hook h : hooks) {
    		loader.addListener(h, listener, this, PluginListener.Priority.MEDIUM);
    	}
    	
        log.info(name + " version " + version + " initialized.");
    }
    
    public Datasource getDataSource() {
    	return dataSource;
    }
    
    public FactionManager getFactionManager() {
    	return fManager;
    }
    
    public RelationManager getRelationManager() {
    	return rManager;
    }
    
    public gPlayerManager getPlayerManager() {
    	return pManager;
    }
    
    public LandManager getLandManager() {
    	return lManager;
    }
    
    public Config getConfig() {
    	return config;
    }
    
    public Economy getEconomy() {
    	return economy;
    }
    
    private class gFactionsListener extends PluginListener {
        @Override
        public void onLogin(Player player) {
        	pManager.initPlayer(player.getName());
        }
        
        @Override
        public HookParametersChat onChat(HookParametersChat hookParams) {
        	String player = hookParams.getPlayer().getName();
        	ArrayList<Player> receivers = new ArrayList<Player>();
        	Faction f = Utils.plugin.getFactionManager().getFaction(player);
        	gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(player);
        	gPlayer.ChatChannel cc = gp.getChatChannel();
        	switch(cc) {
        	case ALLY:
        		assert f != null && !(f instanceof SpecialFaction);
        		Faction[] allies = Utils.plugin.getRelationManager().getRelations(f, Relation.Type.ALLY);
        		for(Faction ally : allies) {
        			Utils.addItems(ally.getOnlineMembers(), receivers);
        		}
        	case FACTION:
        		assert f != null && !(f instanceof SpecialFaction);
        		Utils.addItems(f.getOnlineMembers(), receivers);
        		//etc.getServer().messageAll(String.format("online members are %s", java.util.Arrays.toString(f.getOnlineMembers())));
        		
        		gPlayer[] spies = Utils.plugin.getPlayerManager().spying();
        		for(gPlayer gP : spies) {
        			Player p = gP.toPlayer();
        			assert p != null;
        			if(!receivers.contains(p)) {
        				receivers.add(p);
        			}
        		}
        		
        		hookParams.setPrefix(new StringBuilder(cc.getColor()).append(gp.getFormattedName()).append(Colors.White));
        		hookParams.setReceivers(receivers);
        		//etc.getServer().messageAll(String.format("message receivers are %s", receivers.toString()));
        		return hookParams;
        	default:
        		return hookParams;
        	}
        }
        
        @Override
        public void onDeath(LivingEntity entity) {
        	if(entity instanceof Player) {
        		Player p = (Player) entity;
        		gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(p.getName());
        		gp.decreasePower();
        		p.sendMessage(String.format("%sYour power is now %s%s", Colors.Yellow, Colors.White, gp.getPower()));
        	}
        }
        
        @Override
        public void onPlayerMove(Player player, Location from, Location to) {
        	LandManager lm = Utils.plugin.getLandManager();
        	int start = lm.getLandAt(from).getClaimerId();
        	int finish = lm.getLandAt(to).getClaimerId();
        	if(start != finish) {
        		String pName = player.getName();
        		if(Utils.plugin.getPlayerManager().getPlayer(pName).autoClaim) {
        			String msg = FactionCommand.claimHelper(player);
        			if(msg != null) {
        				player.sendMessage(msg);
        			}
        		} else {
        			FactionManager fm = Utils.plugin.getFactionManager();
        			Faction landFac = fm.getFaction(finish);
        			player.sendMessage(String.format("%s~ %s - %s", Colors.Yellow, landFac.getNameRelative(fm.getFaction(pName)), landFac.getDescription()));
        		}
        	}
        }
    }
}