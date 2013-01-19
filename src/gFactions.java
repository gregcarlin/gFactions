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
    public static final String version = "1.0";
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
    	PluginLoader.Hook[] hooks = {PluginLoader.Hook.LOGIN, PluginLoader.Hook.CHAT};
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
        		
        		gPlayer[] spies = Utils.plugin.getPlayerManager().spying();
        		for(gPlayer gP : spies) {
        			Player p = gP.toPlayer();
        			assert p != null;
        			if(!receivers.contains(p)) {
        				receivers.add(p);
        			}
        		}
        		
        		hookParams.setPrefix(new StringBuilder(cc.getColor()).append(gp.getFormattedName()));
        		hookParams.setReceivers(receivers);
        		return hookParams;
        	default:
        		return hookParams;
        	}
        }
    }
}