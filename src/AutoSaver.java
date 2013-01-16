/**
 * Saves everything when it runs.
 * 
 * @author gregthegeek
 *
 */
public class AutoSaver extends CancellableRunnable {

	@Override
	public void execute() {
		Utils.saveAll();
		etc.getServer().addToServerQueue(new AutoSaver(), Utils.plugin.getConfig().getSaveInterval());
	}
}
