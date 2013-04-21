package en.gregthegeek.util;

import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.TaskOwner;

/**
 * Represents a runnable that can be cancelled. It will not realize it is cancelled until it is time for it to run, in which case it will simply not execute.
 * 
 * @author gregthegeek
 *
 */
public abstract class CancellableRunnable extends ServerTask {
	private boolean isCancelled;
	
	public CancellableRunnable(TaskOwner owner, long delay) {
	    super(owner, delay, false);
		isCancelled = false;
		ThreadManager.threads.add(this);
	}
	
	public final void cancel() {
		isCancelled = true;
	}

	@Override
	public final void run() {
		if(!isCancelled) {
			ThreadManager.threads.remove(this);
			execute();
		}
	}
	
	public abstract void execute();
}
