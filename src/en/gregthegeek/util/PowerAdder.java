package en.gregthegeek.util;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;
import en.gregthegeek.gfactions.player.gPlayer;

/**
 * Regenerates power regularly to a player.
 * 
 * @author gregthegeek
 *
 */
public class PowerAdder extends CancellableRunnable {
		private final gPlayer gp;
		
		public PowerAdder(gPlayer gp, TaskOwner owner, long delay) {
			super(owner, delay);
			this.gp = gp;
		}
		
		@Override
		public void execute() {
			if(gp.isOnline() && !gp.increasePower()) { // power won't increase unless player is online
				ServerTaskManager.addTask(new PowerAdder(gp, Utils.plugin, Utils.plugin.getConfig().getPowerRegenInterval()));
			}
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof PowerAdder) {
				return ((PowerAdder) obj).gp.equals(this.gp);
			}
			return false;
		}
		
		public String getPlayer() {
			return gp.getName();
		}
	}