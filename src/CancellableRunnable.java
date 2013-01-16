/**
 * Represents a runnable that can be cancelled. It will not realize it is cancelled until it is time for it to run, in which case it will simple not execute.
 * 
 * @author gregthegeek
 *
 */
public abstract class CancellableRunnable implements Runnable {
	private boolean isCancelled;
	
	public CancellableRunnable() {
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
