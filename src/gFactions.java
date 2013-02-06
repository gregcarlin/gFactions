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
    public static final String version = "2.1";
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
    	economy = config.getEconomy();
    	fManager = new FactionManager();
    	rManager = new RelationManager();
    	pManager = new gPlayerManager();
    	lManager = new LandManager();
    	
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
    	PluginLoader.Hook[] hooks = {PluginLoader.Hook.LOGIN, PluginLoader.Hook.CHAT, PluginLoader.Hook.DEATH, PluginLoader.Hook.PLAYER_MOVE, PluginLoader.Hook.BLOCK_PLACE, PluginLoader.Hook.DAMAGE, PluginLoader.Hook.BLOCK_BROKEN, PluginLoader.Hook.PLAYER_RESPAWN, PluginLoader.Hook.BLOCK_RIGHTCLICKED};
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
        	String name = player.getName();
        	pManager.initPlayer(name);
        	economy.initPlayer(name);
        }
        
        @Override
        public HookParametersChat onChat(HookParametersChat hookParams) { // manages ally, faction, and public chat
        	Player player = hookParams.getPlayer();
        	String pName = player.getName();
        	ArrayList<Player> receivers = new ArrayList<Player>();
        	Faction f = Utils.plugin.getFactionManager().getFaction(pName);
        	if(f == null || f instanceof SpecialFaction) {
        		return hookParams;
        	}
        	gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(pName);
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
        		
        		hookParams.setPrefix(new StringBuilder(cc.getColor()).append(gp.getFormattedName()).append(Colors.White)); // title name
        		hookParams.setReceivers(receivers);
        		return hookParams;
        	default:
        		// public chat
        		if(f != null && !(f instanceof SpecialFaction)) {
        			hookParams.setCanceled(true);
        			Player[][] sorted = Utils.getOnlinePlayersSorted(f);
        			
        			String fName = f.getName();
        			String pNamef = player.getFullName();
        			String msg = hookParams.getMessage().toString();
        			String format = "[%s%s] <%s%s> %s";
        			
        			String neutral = String.format(format, Relation.Type.NEUTRAL.getColor(), fName, pNamef, Colors.White, msg);
        			for(Player p : sorted[0]) {
        				p.sendMessage(neutral);
        			}
        			
        			String ally = String.format(format, Relation.Type.ALLY.getColor(), fName, pNamef, Colors.White, msg);
        			for(Player p : sorted[1]) {
        				p.sendMessage(ally);
        			}
        			
        			String enemy = String.format(format, Relation.Type.ENEMY.getColor(), fName, pNamef, Colors.White, msg);
        			for(Player p : sorted[2]) {
        				p.sendMessage(enemy);
        			}
        		}
        		return hookParams;
        	}
        }
        
        @Override
        public void onDeath(LivingEntity entity) { // decreases players' power on death
        	if(entity.isPlayer()) {
        		Player p = entity.getPlayer();
        		gPlayer gp = Utils.plugin.getPlayerManager().getPlayer(p.getName());
        		gp.decreasePower(Utils.plugin.getLandManager().getLandAt(p.getLocation()).claimedBy() instanceof WarZone);
        		p.sendMessage(String.format("%sYour power is now %s%s", Colors.Yellow, Colors.White, gp.getPower()));
        	}
        }
        
        @Override
        public void onPlayerMove(Player player, Location from, Location to) { // will alert players when they move from one territory to another
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
        
        @Override
        public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) { // will restrict building to wilderness and owned land
        	return landEditHelper(player, blockPlaced);
        }
        
        @Override
        public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) { // prevents damage in spawn and among faction, reduces damage in owned territory
        	if(!defender.isPlayer()) { // defender is not player, we don't care about them
        		return false;
        	}
        	LandManager lm = Utils.plugin.getLandManager();
        	Faction owner = lm.getLandAt(defender.getLocation()).claimedBy();
        	if(owner instanceof SafeZone) { // defender is in safe zone
        		return true;
        	}
        	if(attacker == null || !attacker.isPlayer()) { // attacker is non-player
        		return false;
        	}
        	if(lm.getLandAt(attacker.getLocation()).claimedBy() instanceof SafeZone) { // attacker is in safe zone
        		attacker.getPlayer().sendMessage(String.format("%sYou cannot hurt someone while you are in a safe zone.", Colors.Yellow));
        		return true;
        	}
        	// we now know: attacker nor defender is not in safe zone, attacker and defender are both players
        	Player pDefend = defender.getPlayer();
        	Faction defense = Utils.plugin.getFactionManager().getFaction(pDefend.getName());
        	Player pAttack = attacker.getPlayer();
        	if(defense.has(pAttack.getName())) { // attacker and defender are in the same faction
        		pAttack.sendMessage(String.format("%sYou cannot hurt members of your own faction.", Colors.Yellow));
        		return true;
        	}
        	if(defense != null && !(defense instanceof SpecialFaction) && defense.equals(owner)) { // defender belongs to a faction and is in his own faction territory
        		double reduction = Utils.plugin.getConfig().getHomeLandDamageReduction();
        		if(reduction <= 0) {
        			return false;
        		}
        		pDefend.applyDamage(type, (int) (amount * reduction));
        		OPacket38EntityStatus pkt = new OPacket38EntityStatus(pDefend.getId(), (byte) 0x02);
				for(Player p : etc.getServer().getPlayerList()) {
					p.getUser().a.b(pkt);
				}
        		pDefend.sendMessage(String.format("%sDamage reduced by %d%%", Colors.Yellow, (int) (reduction * 100)));
        		return true;
        	}
        	return false;
        }
        
        @Override
        public boolean onBlockBreak(Player player, Block block) { // will restrict the breaking of blocks to wilderness and owned land
        	return landEditHelper(player, block);
        }
        
        private boolean landEditHelper(Player player, Block block) { // used by onBlockBreak, onBlockPlace, and onBlockRightClick
        	if(Utils.isBypass(player)) {
        		return false;
        	}
        	Faction f = Utils.plugin.getLandManager().getLandAt(block.getLocation()).claimedBy();
        	if(f instanceof ZoneFaction) {
        		player.sendMessage(String.format("%sYou cannot build in %s.", f.getColorRelative(null), f.getName()));
        		return true;
        	}
        	if(f == null || f instanceof Wilderness) {
        		return false;
        	}
        	Faction me = Utils.plugin.getFactionManager().getFaction(player.getName());
        	if(me == null || me instanceof Wilderness || !me.equals(f)) {
        		player.sendMessage(Utils.rose("You cannot build in the territory of %s.", f.getName()));
        		return true;
        	}
        	return false;
        }
        
        @Override
        public void onPlayerRespawn(Player player, Location loc) { // players respawn at their faction's homes
        	Faction f = Utils.plugin.getFactionManager().getFaction(player.getName());
        	if(f != null && !(f instanceof SpecialFaction) && Utils.plugin.getConfig().factionHomeOnDeath()) {
        		Location home = f.getHome();
        		if(home != null) {
        			loc = home;
        		}
        	}
        }
        
        @Override
        public boolean onBlockRightClick(Player player, Block blockClicked, Item itemInHand) { // restricts the interaction with blocks to wilderness and owned land
        	return landEditHelper(player, blockClicked);
        }
    }
}