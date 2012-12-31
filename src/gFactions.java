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
    	config.fManager = fManager; //living life on the edge
    	rManager = new RelationManager();
    	pManager = new gPlayerManager(this);
    	PlayerCommands.getInstance().add("f", new FactionCommand(fManager));
    	
    	PluginLoader loader = etc.getLoader();
    	PluginLoader.Hook[] hooks = {PluginLoader.Hook.COMMAND};
    	for(PluginLoader.Hook h : hooks) {
    		loader.addListener(h, listener, this, PluginListener.Priority.MEDIUM);
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
        
    }
}