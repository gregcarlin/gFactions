package en.gregthegeek.gfactions;

import java.util.logging.Logger;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.plugin.Plugin;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

import en.gregthegeek.gfactions.db.Datasource;
import en.gregthegeek.gfactions.db.DatasourceException;
import en.gregthegeek.gfactions.db.NullSource;
import en.gregthegeek.gfactions.economy.Economy;
import en.gregthegeek.gfactions.faction.FactionManager;
import en.gregthegeek.gfactions.land.LandManager;
import en.gregthegeek.gfactions.player.gPlayerManager;
import en.gregthegeek.gfactions.relation.RelationManager;
import en.gregthegeek.util.AutoSaver;
import en.gregthegeek.util.ThreadManager;
import en.gregthegeek.util.Utils;

/**
 * Main gFactions class.
 * 
 * @author gregthegeek
 *
 */
public class gFactions extends Plugin implements TaskOwner {
    public static final String name = "gFactions";
    public static final String version = "3.0";
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
    public boolean enable() {
    	Utils.plugin = this;
    	config = new Config();
    	try {
			dataSource = config.getDataSource();
		} catch (DatasourceException e) {
			log.severe("Error retrieving initial data from datasource!");
			dataSource = new NullSource();
		}
    	economy = config.getEconomy();
    	fManager = new FactionManager();
    	rManager = new RelationManager();
    	pManager = new gPlayerManager();
    	lManager = new LandManager();
    	
    	try {
            Canary.commands().registerCommands(new FactionCommand(), this, false);
        } catch (CommandDependencyException e) {
            e.printStackTrace();
        }
    	Canary.hooks().registerListener(new gFactionsListener(), this);
    	
    	// in case plugin is enabled when players are already online
    	try {
        	for(Player p : Canary.getServer().getPlayerList()) {
        		listener.onLogin(p);
        	}
    	} catch (NullPointerException e) {
    	    // throws an NPE when loading on first server start
    	}
    	
    	int interval = config.getSaveInterval();
    	if(interval > 0) {
    		ServerTaskManager.addTask(new AutoSaver(this, config.getSaveInterval()));
    	}
    	
        log.info(name + " version " + version + " enabled.");
        
        return true;
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
}