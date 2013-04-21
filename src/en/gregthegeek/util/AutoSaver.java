package en.gregthegeek.util;

import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

/**
 * Saves everything when it runs.
 * 
 * @author gregthegeek
 *
 */
public class AutoSaver extends CancellableRunnable {

	public AutoSaver(TaskOwner owner, long delay) {
        super(owner, delay);
    }

    @Override
	public void execute() {
		Utils.saveAll();
		ServerTaskManager.addTask(new AutoSaver(getOwner(), Utils.plugin.getConfig().getSaveInterval()));
	}
}
