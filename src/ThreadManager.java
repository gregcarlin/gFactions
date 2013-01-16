import java.util.ArrayList;

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
}
