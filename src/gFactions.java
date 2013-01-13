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
    private Config config;
    private Datasource dataSource;
    
    @Override
    public void disable() {
    	Utils.saveAll();
    	dataSource.close();
        log.info(name + " version " + version + " disabled.");
    }
    
    @Override
    public void enable() {
        log.info(name + " version " + version + " enabled.");
    }
    
    @Override
    public void initialize() {
    	config = new Config();
    	try {
			dataSource = config.getDataSource();
		} catch (DatasourceException e) {
			log.severe("Error retrieving initial data from datasource!");
		}
    	fManager = new FactionManager(this);
    	Utils.fManager = fManager;
    	config.fManager = fManager; //living life on the edge
    	rManager = new RelationManager();
    	pManager = new gPlayerManager(this);
    	PlayerCommands.getInstance().add("f", new FactionCommand());
    	
    	PluginLoader loader = etc.getLoader();
    	PluginLoader.Hook[] hooks = {PluginLoader.Hook.LOGIN};
    	for(PluginLoader.Hook h : hooks) {
    		loader.addListener(h, listener, this, PluginListener.Priority.MEDIUM);
    	}
    	
    	// in case plugin is enabled when players are already online
    	for(Player p : etc.getServer().getPlayerList()) {
    		listener.onLogin(p);
    	}
    	
        log.info(name + " version " + version + " initialized.");
    }
    
    public Datasource getDataSource() {
    	return dataSource;
    }
    
    public RelationManager getRelationManager() {
    	return rManager;
    }
    
    public gPlayerManager getPlayerManager() {
    	return pManager;
    }
    
    public Config getConfig() {
    	return config;
    }
    
    private class gFactionsListener extends PluginListener {
        @Override
        public void onLogin(Player player) {
        	pManager.initPlayer(player.getName());
        }
    }
}