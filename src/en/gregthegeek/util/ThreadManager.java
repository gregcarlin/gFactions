package en.gregthegeek.util;
import java.util.ArrayList;

import net.canarymod.api.entity.living.humanoid.Player;


/**
 * Keeps track of the plugin's active runnables. Works nicely with CancellableRunnable.
 * 
 * @author gregthegeek
 *
 */
public class ThreadManager {
	public static final ArrayList<CancellableRunnable> threads  = new ArrayList<CancellableRunnable>();
	
	public static void stopAll() {
		for(CancellableRunnable r : threads) {
			r.cancel();
		}
	}
	
	public static void disconnect(Player p) {
		int size = threads.size();
		String name = p.getName();
		for(int i=0; i<size; i++) {
			CancellableRunnable r = threads.get(i);
			if(r instanceof PowerAdder && ((PowerAdder) r).getPlayer().equals(name)) {
				r.cancel();
				break;
			}
		}
	}
}
